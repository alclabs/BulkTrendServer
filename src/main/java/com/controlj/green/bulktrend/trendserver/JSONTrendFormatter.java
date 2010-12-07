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
