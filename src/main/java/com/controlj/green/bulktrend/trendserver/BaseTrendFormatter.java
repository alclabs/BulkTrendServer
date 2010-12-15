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
