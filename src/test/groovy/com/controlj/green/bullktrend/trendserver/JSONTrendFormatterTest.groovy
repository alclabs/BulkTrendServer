package com.controlj.green.bullktrend.trendserver;

import spock.lang.*
import com.controlj.green.bulktrend.trendserver.JSONTrendFormatter
import com.controlj.green.addonsupport.access.trend.TrendSample
import com.controlj.green.addonsupport.access.trend.TrendAnalogSample
import com.controlj.green.addonsupport.access.trend.TrendDigitalSample
import com.controlj.green.addonsupport.access.trend.TrendType

public class JSONTrendFormatterTest extends Specification {

    JSONTrendFormatter formatter = new JSONTrendFormatter()
    StringWriter out = new StringWriter()
    Collection<TrendSample> samples = new ArrayList<TrendSample>()

    def "Exception thrown if output not set"() {
        when:
            formatter.writeTrendData("test", samples)

        then:
            thrown(IllegalStateException)
    }


    def "Exception not thrown if output set"() {
        setup:

        when:
            formatter.setOutput(out)
            formatter.writeTrendData("test", samples)

        then:
            notThrown(IllegalStateException)
    }

    //Todo - no samples

    def "Single Analog Sample"() {
        setup:
            Date date = new Date(110,6,4,12,30,0);
            TrendAnalogSample sample = Mock()
            sample.doubleValue() >> 42.01
            sample.time >> date
            sample.type >> TrendType.DATA
            samples << sample

        when:
            formatter.setOutput(out)
            formatter.writeTrendData("test", samples)
            formatter.close()

        then:
            out.toString() == '[{"id":"test","s":[{"t":1278261000000,"a":"42.01"}]}]'

    }


    def "Single Binary Sample"() {
        setup:
            TrendDigitalSample sample = Mock()
            sample.state >> true
            sample.time >> new Date(110,6,4,12,30,0)
            sample.type >> TrendType.DATA
            samples << sample

        when:
            formatter.setOutput(out)
            formatter.writeTrendData("test", samples)
            formatter.close()

        then:
            out.toString() == '[{"id":"test","s":[{"d":true,"t":1278261000000}]}]'

    }
        

    def "Two Analog Samples"() {
        setup:
            TrendAnalogSample sample = Mock()
            sample.doubleValue() >>> [42.01, 3.14]
            sample.time >>> [new Date(110,6,4,12,30,0), new Date(110,6,4,12,45,0)]
            sample.type >> TrendType.DATA
            samples.add sample
            samples.add sample

        when:
            formatter.setOutput(out)
            formatter.writeTrendData("test", samples)
            formatter.close()

        then:
            out.toString() == '[{"id":"test","s":['+
                    '{"t":1278261000000,"a":"42.01"},'+
                    '{"t":1278261900000,"a":"3.14"}'+
                    ']}]'

    }

    def "Skip NonData"() {
        setup:
            TrendAnalogSample sample1 = Mock()
            sample1.doubleValue() >>> 42.01
            sample1.time >> new Date(110,6,4,12,30,0)
            sample1.type >> TrendType.TIMESYNC

            TrendAnalogSample sample2 = Mock()
            sample2.doubleValue() >> 3.14
            sample2.time >> new Date(110,6,4,12,45,0)
            sample2.type >> TrendType.DATA
            samples.add sample1
            samples.add sample2

        when:
            formatter.setOutput(out)
            formatter.writeTrendData("test", samples)
            formatter.close()

        then:
            out.toString() == '[{"id":"test","s":['+
                    '{"t":1278261900000,"a":"3.14"}'+
                    ']}]'

    }

}
