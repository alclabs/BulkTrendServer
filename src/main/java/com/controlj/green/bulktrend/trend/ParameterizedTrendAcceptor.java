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

    public ParameterizedTrendAcceptor names(String commaSepNames) {
        final String names[] = commaSepNames.split(",");
        for (int i=0; i<names.length; i++) {
            names[i] = names[i].trim();
        }
        checks.add(new AspectAcceptor<TrendSource>() {
            @Override
            public boolean accept(@NotNull TrendSource ts) {
                boolean result = false;

                String displayName = ts.getLocation().getDisplayName();
                for (String name : names) {
                    if (displayName.equalsIgnoreCase(name)) {
                        result = true;
                        break;
                    }
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
