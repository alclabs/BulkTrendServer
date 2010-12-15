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
