/*
 * Copyright 2010 Automated Logic
 *
 *    Licensed under the Apache License, Version 2.0 (the "License");
 *    you may not use this file except in compliance with the License.
 *    You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 *    Unless required by applicable law or agreed to in writing, software
 *    distributed under the License is distributed on an "AS IS" BASIS,
 *    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *    See the License for the specific language governing permissions and
 *    limitations under the License.
 */

package com.controlj.green.bulktrend.trendserver;

import com.controlj.green.addonsupport.AddOnInfo;
import com.controlj.green.addonsupport.InvalidConnectionRequestException;
import com.controlj.green.addonsupport.access.ActionExecutionException;
import com.controlj.green.addonsupport.access.SystemConnection;
import com.controlj.green.addonsupport.access.SystemException;
import com.controlj.green.bulktrend.trend.ParameterizedTrendAcceptor;
import com.controlj.green.bulktrend.trend.TrendSourceFinder;
import com.controlj.green.bulktrend.trend.TrendSourceInfo;
import org.apache.commons.lang.StringEscapeUtils;

import javax.servlet.ServletException;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.text.DecimalFormat;
import java.util.Collection;

public class SearchServlet extends BaseHttpServlet  {
    // Filter options:
    public static final String PARAM_ENABLED = "enabled";
    public static final String PARAM_HISTORICAL = "historical";
    public static final String PARAM_FIXED_INTERVAL = "fixed_interval";
    public static final String PARAM_UPLOAD_GREATER = "upload_greater";
    public static final String PARAM_PATH = "path";

    private static final DecimalFormat twoDigitFormat = new DecimalFormat("00");


    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        doPost(req, resp);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        ParameterizedTrendAcceptor filter = getFilter(req);

        AddOnInfo ao = AddOnInfo.getAddOnInfo();
        SystemConnection connection = null;
        try {
            connection = ao.getUserSystemConnection(req);
        } catch (InvalidConnectionRequestException e) {
            throw new ServletException("Error getting Add-On connection", e);
        }

        TrendSourceFinder finder = new TrendSourceFinder(connection);

        Collection<TrendSourceInfo> trends;
        try {
            trends = finder.findTrends(finder.getIdForPath(req.getParameter(PARAM_PATH)), filter);
        } catch (SystemException e) {
            writeErrorInRow(resp, e.getMessage());
            throw new ServletException(e);
        } catch (ActionExecutionException e) {
            writeErrorInRow(resp, e.getMessage());
            throw new ServletException(e);
        }

        ServletOutputStream out = resp.getOutputStream();
        disableCache(resp);
        
        for (TrendSourceInfo trend : trends) {
            out.println("   <tr>");
            writeStringCell(out,    "displaypath", trend.getDisplayPath());
            writeStringCell(out,    "type", trend.getType().toString());
            writeBooleanCell(out,   "enabled", trend.isEnabled());
            writeRawCell(out,       "sampleinterval", trend.isCOV() ? "&nbsp;" : mSecToTime(trend.getSampleInterval()) );
            writeBooleanCell(out,   "cov", trend.isCOV());
            writeStringCell(out,    "buffersize", Integer.toString(trend.getBufferSize()) );
            writeBooleanCell(out,   "historian", trend.isHistorianEnabled());
            writeStringCell(out,    "trigger", Integer.toString(trend.getHistorianTrigger()) );
            writeRawCell(out,       "uploadtime", trend.isCOV() && trend.isEnabled() && trend.isHistorianEnabled() ?
                    "&nbsp;" : mSecToTime(trend.getHistorianUploadTime()) );
            writeRawCell(out,    "lookupstring", prepareLookupStringContent(trend.getLookupString()));
            out.println("   <tr>");
        }
    }

    private String prepareLookupStringContent(String lookupString)
    {
       try
       {
          String href = "query.html?lookupID="+ URLEncoder.encode(lookupString, "UTF-8");
          return "<a href=\"" + href + "\">"+StringEscapeUtils.escapeHtml(lookupString)+"</a>";
       }
       catch (UnsupportedEncodingException e)
       {
          throw new RuntimeException(e);
       }
    }

    private void writeErrorInRow(HttpServletResponse resp, String msg) throws IOException {
        ServletOutputStream out = resp.getOutputStream();
        out.println("<tr><td colspan=\"100\">Error: " + msg + "</td></tr>");
        out.flush();
    }

    private void writeStringCell(ServletOutputStream out, String cssClass, String content) throws IOException {
        writeRawCell(out, cssClass, StringEscapeUtils.escapeHtml(content));
    }

    private void writeRawCell(ServletOutputStream out, String cssClass, String content) throws IOException {
        out.print("      <td class='"+cssClass+"'>");
        out.print(content);
        out.println("      </td>");

    }

    private void writeBooleanCell(ServletOutputStream out, String cssClass, boolean content) throws IOException {
        writeRawCell(out, cssClass,  content ? " X " : "&nbsp;");
    }

    private String mSecToTime(long msec) {
        long secTotal = msec / 1000L;
        int hours = (int) (secTotal / (60 * 60));
        int sec = (int) (secTotal % 60);
        int min = (int)((secTotal / 60) % 60);

        StringBuilder result = new StringBuilder();
        if (hours > 0) {
            result.append(twoDigitFormat.format(hours));
            result.append(":");
        }

        result.append(twoDigitFormat.format(min));
        result.append(":");

        result.append(twoDigitFormat.format(sec));

        return result.toString();
    }


    private ParameterizedTrendAcceptor getFilter(HttpServletRequest req) {
        ParameterizedTrendAcceptor filter = new ParameterizedTrendAcceptor();

        if (hasBooleanOption(req, PARAM_ENABLED)) {
            filter.enabled();
        }

        if (hasBooleanOption(req, PARAM_HISTORICAL)) {
            filter.historical();
        }

        if (hasOption(req, PARAM_FIXED_INTERVAL)) {
            try {
                int param = getIntParameter(req, PARAM_FIXED_INTERVAL);
                filter.sampleIntervalEquals(param * 60 *  1000L);
            } catch (NumberFormatException e) { }  //Not a number, ignore
        }

        if (hasOption(req, PARAM_UPLOAD_GREATER)) {
            try {
                int param = getIntParameter(req, PARAM_UPLOAD_GREATER);
                filter.uploadIntervalGreater(param * 60 * 1000L);
            } catch (NumberFormatException e) { }  //Not a number, ignore
        }

        return filter;
    }


    private boolean hasBooleanOption(HttpServletRequest req, String paramName) {
        boolean result = false;
        String parValue = req.getParameter(paramName);
        if (parValue != null) {
            result =  (parValue.equalsIgnoreCase("true"));
        }
        return result;
    }


    private boolean hasOption(HttpServletRequest req, String paramName) {
        return (req.getParameter(paramName) != null);
    }

    private int getIntParameter(HttpServletRequest req, String paramName) throws NumberFormatException {
        String parValue = req.getParameter(paramName);
        if (parValue != null) {
            return Integer.parseInt(parValue);
        }
        throw new NumberFormatException("Number String is null");
    }
}
