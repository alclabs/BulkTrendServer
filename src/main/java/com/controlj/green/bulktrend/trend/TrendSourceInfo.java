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

package com.controlj.green.bulktrend.trend;

import com.controlj.green.addonsupport.access.Location;
import com.controlj.green.addonsupport.access.TrendException;
import com.controlj.green.addonsupport.access.aspect.TrendSource;

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