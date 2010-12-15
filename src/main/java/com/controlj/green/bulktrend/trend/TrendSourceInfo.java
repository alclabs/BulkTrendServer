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

package com.controlj.green.bulktrend.trend;

import com.controlj.green.addonsupport.AddOnInfo;
import com.controlj.green.addonsupport.access.*;
import com.controlj.green.addonsupport.access.aspect.AnalogTrendSource;
import com.controlj.green.addonsupport.access.aspect.TrendSource;
import com.controlj.green.addonsupport.access.trend.*;
import org.jetbrains.annotations.NotNull;

import java.io.BufferedReader;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.Iterator;

public class TrendSourceInfo
{
    private String lookup;
    private String displayPath;
    private boolean enabled;

    private TrendSource.Type type;
    private long sampleInterval;
    private boolean isCOV;
    private float covIncrement;
    private int bufferSize;
    private boolean stopWhenFull;
    private boolean historianEnabled;
    private int historianTrigger;



    public TrendSourceInfo(TrendSource ts)
    {
        Location loc = ts.getLocation();
        lookup = loc.getPersistentLookupString(false);
        displayPath = loc.getDisplayPath();

        enabled = ts.isEnabled();
        type = ts.getType();
        isCOV = ts.isCOV();
        if (isCOV) {
            try {
                covIncrement = ts.getCOVIncrement();
            } catch (TrendException e) { } // previous test should handle this case
        } else {
            try {
                sampleInterval = ts.getSampleInterval();
            } catch (TrendException e) { } // previous test should handle this case
        }
        bufferSize = ts.getBufferSize();
        stopWhenFull = ts.isStopWhenFull();
        historianEnabled = ts.isHistorianEnabled();
        historianTrigger = ts.getHistorianTrigger();
    }


    public String getLookupString()
    {
        return lookup;
    }

    public String getDisplayPath()
    {
        return displayPath;
    }

    public boolean isEnabled() {
        return enabled;
    }

    public TrendSource.Type getType() {
        return type;
    }

    public long getSampleInterval() {
        return sampleInterval;
    }

    public boolean isCOV() {
        return isCOV;
    }

    public float getCovIncrement() {
        return covIncrement;
    }

    public int getBufferSize() {
        return bufferSize;
    }

    public boolean isStopWhenFull() {
        return stopWhenFull;
    }

    public boolean isHistorianEnabled() {
        return historianEnabled;
    }

    public int getHistorianTrigger() {
        return historianTrigger;
    }

    public long getHistorianUploadTime() {
        return (historianTrigger * sampleInterval);
    }
}