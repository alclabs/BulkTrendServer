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
import org.apache.commons.lang.StringEscapeUtils;

import java.io.IOException;
import java.io.Writer;
import java.util.Collection;
import java.util.Iterator;

public class CSVTrendFormatter extends BaseTrendFormatter implements TrendFormatter {
    public CSVTrendFormatter() {
        setDigitsPastDecimal(2);        
    }

    public void writeTrendData(String id, Collection<? extends TrendSample> samples) throws IOException {
        confirmOutputSet();
        
        out.write(escapeString(id));
        Iterator<? extends TrendSample> it = samples.iterator();
        while (it.hasNext()) {
            TrendSample sample = it.next();
            if (sample.getType() == TrendType.DATA) {
                out.write(',');
                out.write(Long.toString(formatDate(sample.getTime())));
                out.write(',');

                if (sample instanceof TrendAnalogSample) {
                    out.write(formatAnalog(((TrendAnalogSample)sample).doubleValue()) );
                } else if (sample instanceof TrendDigitalSample) {
                    out.write(Boolean.toString(((TrendDigitalSample)sample).getState()) );
                }
            }
        }
        out.write('\n');
    }

    public void nextSource() throws IOException {
    }

    public void close() throws IOException {
        out.close();
    }

    private String escapeString(String str) {
        return StringEscapeUtils.escapeCsv(str);
    }
}
