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

package com.controlj.green.bullktrend.trendserver;


import com.controlj.green.addonsupport.access.trend.TrendAnalogSample
import com.controlj.green.addonsupport.access.trend.TrendDigitalSample
import com.controlj.green.addonsupport.access.trend.TrendSample
import com.controlj.green.addonsupport.access.trend.TrendType
import com.controlj.green.bulktrend.trendserver.JSONTrendFormatter
import spock.lang.Specification
import com.controlj.green.bulktrend.trendserver.BaseTrendFormatter

public class BaseTrendFormatterTest extends Specification {

    BaseTrendFormatter formatter = new BaseTrendFormatter()

    def lastInterestingOffset() {
        expect:
            formatter.lastInterestingOffset(str) == off

        where:
            str         | off
            '42.010'    | 4
            '42.000'    | 1
            '0.000'     | 0
            '123.456'   | 6

    }

    def formatAnalog() {
        setup:
            formatter.setDigitsPastDecimal(3)

        expect:
            formatter.formatAnalog(val) == str

        where:
            val         | str
            30d         | "30"
            30.01d      | "30.01"
            123.45678d  | "123.457"
            0.03        | "0.03"
            0.0         | "0"
            12345678912345678d  | "12345678912345678"
    }

    def formatDate() {
        expect:
            formatter.formatDate(date) == str

        where:
            date                            | str
            new Date(110, 0, 20, 1, 2, 3)   | 1263967323000
            new Date(110,6,4,12,30,0)       | 1278261000000
            new Date(110,6,4,12,45,0)       | 1278261900000
    }
}