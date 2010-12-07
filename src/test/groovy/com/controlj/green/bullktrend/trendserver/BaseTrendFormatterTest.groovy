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