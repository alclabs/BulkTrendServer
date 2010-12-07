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
