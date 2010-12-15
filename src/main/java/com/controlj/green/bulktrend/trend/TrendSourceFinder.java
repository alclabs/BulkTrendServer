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

import com.controlj.green.addonsupport.access.*;
import com.controlj.green.addonsupport.access.aspect.TrendSource;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Collection;

public class TrendSourceFinder {
    SystemConnection connection;

    public TrendSourceFinder(SystemConnection connection) {
        this.connection = connection;
    }

    public Collection<TrendSourceInfo> findTrendsFromRoot(final AspectAcceptor<TrendSource> acceptor) throws SystemException, ActionExecutionException {
        return findTrends(null, acceptor);
    }

    public Collection<TrendSourceInfo> findTrends(final String startId, final AspectAcceptor<TrendSource> acceptor) throws SystemException, ActionExecutionException {
        final ArrayList<TrendSourceInfo> result = new ArrayList<TrendSourceInfo>();
        connection.runReadAction(new ReadAction() {
            public void execute(@NotNull SystemAccess access) throws Exception {
                Location start;
                if (startId == null) {
                    start = access.getGeoRoot();
                } else {
                    start = access.getTree(SystemTree.Geographic).resolve(startId);
                }

                Collection<TrendSource> sources = start.find(TrendSource.class, acceptor);
                for (TrendSource source : sources) {
                    result.add(new TrendSourceInfo(source));
                }
            }
        });

        return result;
    }

    public String getIdForPath(String path) throws SystemException, ActionExecutionException {
        final String searchString;
        if ((path == null) || (path.length() == 0)) {
            searchString = "";
        } else {
            searchString = path;
        }
        return connection.runReadAction(new ReadActionResult<String>() {
            public String execute(@NotNull SystemAccess access) throws Exception {
                Location loc = access.getGeoRoot();
                loc = loc.getDescendant(searchString);
                return loc.getTransientLookupString();
            }
        });

    }


}
