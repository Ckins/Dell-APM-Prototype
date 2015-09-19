/*
* Copyright 2014 Dell Inc.
* ALL RIGHTS RESERVED.
*
* This software is the confidential and proprietary information of
* Dell Inc. ("Confidential Information").  You shall not
* disclose such Confidential Information and shall use it only in
* accordance with the terms of the license agreement you entered
* into with Dell Inc.
*
* DELL INC. MAKES NO REPRESENTATIONS OR WARRANTIES
* ABOUT THE SUITABILITY OF THE SOFTWARE, EITHER EXPRESS
* OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE IMPLIED
* WARRANTIES OF MERCHANTABILITY, FITNESS FOR A
* PARTICULAR PURPOSE, OR NON-INFRINGEMENT. DELL SHALL
* NOT BE LIABLE FOR ANY DAMAGES SUFFERED BY LICENSEE
* AS A RESULT OF USING, MODIFYING OR DISTRIBUTING
* THIS SOFTWARE OR ITS DERIVATIVES.
*/

package com.dell.prototype.apm.instrumentation;

import com.dell.prototype.apm.model.Request;
import com.dell.prototype.apm.model.RequestTiming;
import com.dell.prototype.apm.model.javaee.DatabaseTiming;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class TraceStorage {
    private static Map<Long, List<Trace>> store = new ConcurrentHashMap<Long, List<Trace>>();

    public synchronized static void clear(long tid) {
        store.remove(tid);
    }

    public synchronized static void setTrace(long tid, Trace trace) {
        try {
            if (!trace.isJdbcTrace()) {
                if (store.get(tid) == null) {
                    List<Trace> traces = new ArrayList<Trace>();
                    traces.add(trace);
                    store.put(tid, traces);
                } else {
                    store.get(tid).add(trace);
                }
                return;
            }

            if (trace.isJdbcTrace()) {
                if (store.get(tid) == null || store.get(tid).size() == 0) return;

                List<Trace> storedTraces = store.get(tid);
                Trace latestTrace = storedTraces.get(storedTraces.size() - 1);
                if (!latestTrace.isJdbcTrace()) {
                    storedTraces.add(trace);
                    return;
                }
                List<StackTraceElement> stackTraceElements = (trace.getTraceMethodInfo()).getStackTraceElements();
                List<StackTraceElement> latestStackTraceElements = (latestTrace.getTraceMethodInfo()).getStackTraceElements();
                if (stackTraceElements.size() == latestStackTraceElements.size() && stackTraceElements.subList(1, stackTraceElements.size()).equals(latestStackTraceElements.subList(1, latestStackTraceElements.size()))) {
                    storedTraces.add(trace);
                    return;
                }
                if (storedTraces.size() >= 3) {
                    Trace secondLatestTrace = (storedTraces.get(storedTraces.size() - 2));
                    List<StackTraceElement> secondLatestStackTraceElements = secondLatestTrace.getTraceMethodInfo().getStackTraceElements();
                    if (secondLatestStackTraceElements.subList(1, secondLatestStackTraceElements.size()).equals(latestStackTraceElements.subList(1, latestStackTraceElements.size()))) {
                        storedTraces.add(trace);
                        return;
                    }
                }
                return;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public synchronized static void deleteStaleTraces(long tid) {
        store.remove(tid);
    }

    public synchronized static String snapshot(long tid) {
        return store.get(tid).toString();
    }

    public synchronized static void submit(long tid, String uri, String queryString, boolean hasError) {
        Request request = new Request(uri, queryString, hasError);
        List<Trace> traces = store.get(tid);
        int size = traces.size();
        if (size < 2) return;
        Trace beforeDoService = traces.get(0);
        Trace afterDoService = traces.get(size - 1);
        request.setInitialTime(new Date(beforeDoService.getTimestamp()));
        request.setEndTime(new Date(afterDoService.getTimestamp()));
        if (!beforeDoService.getTraceMethodInfo().getClazz().equals(afterDoService.getTraceMethodInfo().getClazz()) ||
                !beforeDoService.getTraceMethodInfo().getMethod().equals(afterDoService.getTraceMethodInfo().getMethod())) return;
        List<DatabaseTiming> databaseTimings = new ArrayList<DatabaseTiming>();
        long totalDatabase = 0l;
        if (size >= 4) {
            List<Trace> jdbcTraces = traces.subList(1, size - 1);
            long stepStartTime = jdbcTraces.get(0).getTimestamp();
            for (int i = 1; i < jdbcTraces.size(); i = i + 2) {
                DatabaseTiming step = new DatabaseTiming();
                Trace databaseTrace = jdbcTraces.get(i);
                step.setSql(databaseTrace.getSql());
                long stepDaltTime = databaseTrace.getTimestamp() - stepStartTime;
                totalDatabase += stepDaltTime;
                step.setTime(stepDaltTime);
                databaseTimings.add(step);
                stepStartTime = databaseTrace.getTimestamp();
            }
        }

        long duringServiceTime = afterDoService.getTimestamp() - beforeDoService.getTimestamp();
        RequestTiming duringService = new RequestTiming();
        if (totalDatabase != 0) {
            long duringPureService = duringServiceTime - totalDatabase;
            duringService.setTime(duringPureService);
        } else {
            duringService.setTime(duringServiceTime);
        }


        List<RequestTiming> requestTimings = new ArrayList<RequestTiming>();
        requestTimings.add(duringService);
        if (totalDatabase != 0) requestTimings.addAll(databaseTimings);
        request.setTimings(requestTimings);
        DataSubmitter.submit(request);

    }

}
