/*
 * Copyright (c) 2010 Automated Logic Corporation
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */

package com.controlj.green.bulktrend.trendserver;

import com.controlj.green.addonsupport.AddOnInfo;
import com.controlj.green.addonsupport.InvalidConnectionRequestException;
import com.controlj.green.addonsupport.access.*;
import com.controlj.green.addonsupport.access.aspect.AnalogTrendSource;
import com.controlj.green.addonsupport.access.aspect.DigitalTrendSource;
import com.controlj.green.addonsupport.access.aspect.TrendSource;
import com.controlj.green.addonsupport.access.trend.*;
//import org.apache.log4j.Logger;
//import org.apache.log4j.PropertyConfigurator;
import org.jetbrains.annotations.NotNull;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintWriter;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

public class TrendServlet extends BaseHttpServlet {
    private static final int BAD_RESPONSE = 400;
    private SimpleDateFormat dateFormatter = new SimpleDateFormat("yyyy-MM-dd");

    public void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException {
        doPost(request, response);
    }

    public void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException {
       try
       {
          doWork(request, response);
       }
       catch (ServletException e)
       {
          response.sendError(BAD_RESPONSE, e.getMessage());
       }
    }

    public void doWork(HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException {
        // WebCTRL only has the default mime types set to be compressable: text/html, text/xml, text/plain
        // for JSON, should probably be application/json, and csv should be text/csv
        response.setContentType("text/plain");
        disableCache(response);

        String[] ids = request.getParameterValues("id");

        // Get format
        String formatString = request.getParameter("format");
        if (formatString == null) {
            formatString = "json";
        }
        TrendFormatter formatter;
        if (formatString.trim().equalsIgnoreCase("json")) {
            formatter = new JSONTrendFormatter();
        } else if (formatString.trim().equalsIgnoreCase("csv")) {
            formatter = new CSVTrendFormatter();
        } else {
            throw new ServletException("Formatter parameter has illegal value of '"+formatString+"'.  "+
                    "Should be 'json' or 'csv'.");
        }

        String startString = request.getParameter("start");
        String endString = request.getParameter("end");
        
        if (startString == null) {
            throw new ServletException("missing start parameter");
        }
        if (endString == null) {
            throw new ServletException("missing end parameter");
        }

        // Need to query database for midnight of the night of the end,
        // which will actually be 0:00 the next day.
        Date start;
        Date end;
        try {
            start = parseDate(startString.trim());
            end   = parseEnd(endString.trim());
        } catch (ParseException e) {
            throw new ServletException("Invalid argument for start or end date. Start:"+startString+", End:"+endString, e);
        }

        String digitsString = request.getParameter("defaultdigits");
        if (digitsString != null) {
            try {
                formatter.setDigitsPastDecimal(Integer.parseInt(digitsString));
            } catch (NumberFormatException e) {
                throw new ServletException("defaultdigits parameter is not a number: "+digitsString);
            }
        }


        AddOnInfo ao = AddOnInfo.getAddOnInfo();
        SystemConnection connection;
        try {
            connection = ao.getUserSystemConnection(request);
        } catch (InvalidConnectionRequestException e) {
            throw new ServletException("Error getting a SystemConnection", e);
        }


        formatter.setOutput(response.getWriter());

        try {
            handleSamples(formatter, connection, start, end, ids);
        } catch (SystemException e) {
           throw new ServletException(new Date()+" - SystemException gathering bulk trends", e);
        } catch (ActionExecutionException e) {
           Throwable cause = e.getCause();
           if (cause instanceof ServletException)
             throw (ServletException)cause;
           else
             throw new ServletException(new Date()+" - ActionExecutionException gathering bulk trends", e);
        }

        formatter.close();
    }

    private void handleSamples(final TrendFormatter formatter,
                               final SystemConnection connection,
                               final Date start,
                               final Date end,
                               final String[] ids ) throws SystemException, ActionExecutionException {
        connection.runReadAction(new ReadAction() {
            public void execute(@NotNull SystemAccess access) throws Exception {
                Tree geo = access.getTree(SystemTree.Geographic);
                TrendRange range = TrendRangeFactory.byDateRange(start, end);

                for (int i=0; i<ids.length; i++) {
                    String id = ids[i].trim();
                    TrendData<? extends TrendSample> data;
                    try {
                        Location loc = geo.resolve(id);
                        TrendSource ts = loc.getAspect(TrendSource.class);
                        TrendSource.Type type = ts.getType();

                        data = null;
                        if (type == TrendSource.Type.Analog) {
                            data = ((AnalogTrendSource) ts).getTrendData(range);
                        } else if (type == TrendSource.Type.Digital) {
                            data = ((DigitalTrendSource) ts).getTrendData(range);
                        } else {
                            // intentionally ignore special types for now
                        }

                        formatter.writeTrendData(id, filterDataToExclusiveEnd(end, data));
                        if (i+1 < ids.length) {
                            formatter.nextSource();
                        }
                    } catch (UnresolvableException e) {
                       throw new ServletException("Error resolving location: '"+ id+ '\'', e);
                    } catch (NoSuchAspectException e) {
                       throw new ServletException("Error getting trend source from location:'"+ id+ '\'', e);
                    }
                }
                formatter.close();
            }

            private Collection<TrendSample> filterDataToExclusiveEnd(Date end, TrendData<? extends TrendSample> data) throws TrendException {
                List<TrendSample> result = new ArrayList<TrendSample>(300);
                Iterator<? extends TrendSample> it = data.getSamples();
                while (it.hasNext()) {
                    TrendSample next = it.next();
                    if (next.getTimeInMillis() < end.getTime()) {
                        result.add(next);
                    }
                }
                return result;
            }
        });
    }

    private Date parseDate(String startString) throws ParseException {
        return dateFormatter.parse(startString);
    }

    private Date parseEnd(String endString) throws ParseException {
        Date result = parseDate(endString);
        result.setHours(24);
        return result;
    }

    /*
    ONLY needed for hack to allow BASIC authentication to work on a 4.1 system.  Not needed for 5.0
     */
    @Override
    public void init(ServletConfig servletConfig) {
        File testFile = new File("webserver/conf/Standalone/localhost/bulktrendserver.xml");
        if (testFile.exists()) {
            try {
                PrintWriter writer = new PrintWriter(testFile);
                writer.println("<Context path=\"/bulktrendserver\">");
                writer.println("</Context>");
                writer.close();
            } catch (FileNotFoundException e) {
                System.err.println("Error rewriting bulktrendserver context file at :"+testFile+".  "+e.getMessage());
            }
        }
    }
}
