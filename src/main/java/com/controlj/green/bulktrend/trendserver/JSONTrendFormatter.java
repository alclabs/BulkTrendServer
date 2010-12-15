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

import com.controlj.green.addonsupport.access.trend.TrendAnalogSample;
import com.controlj.green.addonsupport.access.trend.TrendDigitalSample;
import com.controlj.green.addonsupport.access.trend.TrendSample;
import com.controlj.green.addonsupport.access.trend.TrendType;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.Writer;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Collection;
import java.util.Date;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class JSONTrendFormatter extends BaseTrendFormatter implements TrendFormatter {

    public JSONTrendFormatter() {
        setDigitsPastDecimal(2);
    }

    public void setOutput(Writer out) throws IOException {
        super.setOutput(out);
        out.write("[");
    }


    public void writeTrendData(String id, Collection<? extends TrendSample> samples) throws IOException {
        confirmOutputSet();

        JSONObject obj = new JSONObject();
        try {
            obj.put("id", id);
            JSONArray arrayData = new JSONArray();

            for (TrendSample sample : samples) {
                // Note that we currently just skip special (non-data) samples
                if (sample.getType() == TrendType.DATA) {
                    JSONObject json = new JSONObject();
                    json.put("t", formatDate(sample.getTime()));

                    //todo  - remove casting
                    // After expected 5.0 changes to the add-on api, we should be able to get
                    // a String value without this awkward casting

                    if (sample instanceof TrendAnalogSample) {
                        json.put("a", formatAnalog(((TrendAnalogSample)sample).doubleValue()) );
                    } else if (sample instanceof TrendDigitalSample) {
                        json.put("d", ((TrendDigitalSample)sample).getState() );
                    }
                    arrayData.put(json);
                }
            }
            obj.put("s", arrayData);

            out.write(obj.toString());

        } catch (JSONException e) {
            throw new IOException("Unexpected JSONException", e);
        }
    }

    public void nextSource() throws IOException {
        out.write(",");
    }

    public void close() throws IOException {
        out.write("]");
        out.close();
    }


}
