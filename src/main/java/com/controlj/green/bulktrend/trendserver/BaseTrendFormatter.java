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

import com.controlj.green.addonsupport.access.trend.TrendSample;

import java.io.IOException;
import java.io.Writer;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Collection;
import java.util.Date;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class BaseTrendFormatter {
    protected Writer out;
    protected SimpleDateFormat dateFormatter = new SimpleDateFormat("yyyy-MM-dd-HH:mm:ss");
    private DecimalFormat floatFormat;
    private Pattern intPattern  = Pattern.compile("(.*\\..*[^0])0+\\z");

    public void setOutput(Writer out) throws IOException {
        this.out = out;
    }

    protected void confirmOutputSet() throws IllegalStateException {
        if (out == null) {
            throw new IllegalStateException("Output not set");
        }
    }

    public void setDigitsPastDecimal(int digits) {
        StringBuilder pattern = new StringBuilder("0.");
        for (int i=0; i<digits; i++) {
            pattern.append("0");
        }
        floatFormat = new DecimalFormat(pattern.toString());
    }

    protected long formatDate(Date date) {
        return date.getTime();
    }

    protected String formatAnalog(double value) {
        String result = floatFormat.format(value);
        int off = lastInterestingOffset(result);
        if (off < result.length()) {
            result = result.substring(0, off+1);
        }
        return result;
    }

    int lastInterestingOffset(String str) {
        for (int i=str.length()-1; i>=0; i--) {
            char next = str.charAt(i);
            if (next == '.') {
                return i-1;
            }else if (next != '0') {
                return i;
            }
        }
        return 0;
    }
}
