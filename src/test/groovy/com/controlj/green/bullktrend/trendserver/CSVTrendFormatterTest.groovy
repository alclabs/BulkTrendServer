package com.controlj.green.bullktrend.trendserver;


import com.controlj.green.addonsupport.access.trend.TrendAnalogSample
import com.controlj.green.addonsupport.access.trend.TrendDigitalSample
import com.controlj.green.addonsupport.access.trend.TrendSample
import com.controlj.green.addonsupport.access.trend.TrendType
import com.controlj.green.bulktrend.trendserver.JSONTrendFormatter
import spock.lang.Specification
import com.controlj.green.bulktrend.trendserver.CSVTrendFormatter

public class CSVTrendFormatterTest extends Specification {

    CSVTrendFormatter formatter = new CSVTrendFormatter()
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
            out.toString() == 'test,1278261000000,42.01\n'

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
            out.toString() == 'test,1278261000000,true\n'

    }

    def "Escaped ID"() {
        setup:
            Date date = new Date(110,6,4,12,30,0);
            TrendAnalogSample sample = Mock()
            sample.doubleValue() >> 42.01
            sample.time >> date
            sample.type >> TrendType.DATA
            samples << sample

        when:
            formatter.setOutput(out)
            formatter.writeTrendData("don't,\"worry\"", samples)
            formatter.close()

        then:
            out.toString() == '"don\'t,\"\"worry\"\"",1278261000000,42.01\n'

    }

    def "Two Analog Samples"() {
        setup:
            TrendAnalogSample sample = Mock()
            sample.doubleValue() >>> [42.01, 3.14159]
            sample.time >>> [new Date(110,6,4,12,30,0), new Date(110,6,4,12,45,0)]
            sample.type >> TrendType.DATA
            samples.add sample
            samples.add sample

        when:
            formatter.setOutput(out)
            formatter.writeTrendData("test", samples)
            formatter.close()

        then:
            out.toString() == 'test,1278261000000,42.01,1278261900000,3.14\n'

    }

}