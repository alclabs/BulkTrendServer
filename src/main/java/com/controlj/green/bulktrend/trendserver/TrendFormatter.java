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
import java.util.Collection;

public interface TrendFormatter {
    void setOutput(Writer out) throws IOException;
    void writeTrendData(String id, Collection<? extends TrendSample> samples) throws IOException;
    void nextSource() throws IOException;
    void close() throws IOException;
    void setDigitsPastDecimal(int digits);
}
