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

import com.controlj.green.addonsupport.access.AspectAcceptor;
import com.controlj.green.addonsupport.access.TrendException;
import com.controlj.green.addonsupport.access.aspect.TrendSource;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;

public class ParameterizedTrendAcceptor implements AspectAcceptor<TrendSource> {
    private ArrayList<AspectAcceptor<TrendSource>> checks = new ArrayList<AspectAcceptor<TrendSource>>();

    public boolean accept(@NotNull TrendSource trendSource) {
        for (AspectAcceptor<TrendSource> check : checks) {
            if (!check.accept(trendSource)) {
                return false;
            }
        }
        return true;
    }


    public ParameterizedTrendAcceptor enabled() {
        checks.add(new AspectAcceptor<TrendSource>() {
            public boolean accept(@NotNull TrendSource ts) {
                return ts.isEnabled();
            }
        });
        return this;
    }

    public ParameterizedTrendAcceptor historical() {
        checks.add(new AspectAcceptor<TrendSource>() {
            public boolean accept(@NotNull TrendSource ts) {
                return ts.isHistorianEnabled();
            }
        });
        return this;
    }

    public ParameterizedTrendAcceptor sampleIntervalEquals(final long interval) {
        checks.add(new AspectAcceptor<TrendSource>() {
            public boolean accept(@NotNull TrendSource ts) {
                boolean result = false;
                if (ts.isInterval()) {
                    try {
                        result = (ts.getSampleInterval() == interval);
                    } catch (TrendException e) { }  // intentionally ignore, COV check should prevent
                }
                return result;
            }
        });
        return this;
    }

    public ParameterizedTrendAcceptor uploadIntervalGreater(final long interval) {
        checks.add(new AspectAcceptor<TrendSource>() {
            public boolean accept(@NotNull TrendSource ts) {
                boolean result = false;
                if (ts.isInterval() && ts.isHistorianEnabled() && ts.isEnabled()) {
                    try {
                        result = ((ts.getSampleInterval() * ts.getHistorianTrigger()) > interval );
                    } catch (TrendException e) { }  // intentionally ignore, COV check should prevent
                }
                return result;
            }
        });
        return this;
    }


/* Template
    public ParameterizedTrendAcceptor historical() {
        checks.add(new AspectAcceptor<TrendSource>() {
            public boolean accept(@NotNull TrendSource ts) {
                return ts.;
            }
        });
        return this;
    }

 */
}
