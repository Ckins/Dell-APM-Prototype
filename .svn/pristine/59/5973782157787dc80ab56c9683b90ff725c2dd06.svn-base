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

import java.lang.management.GarbageCollectorMXBean;
import java.lang.management.ManagementFactory;
import java.lang.management.MemoryMXBean;
import java.lang.management.RuntimeMXBean;
import java.lang.management.ThreadMXBean;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.dell.prototype.apm.model.Metric;
import com.dell.prototype.apm.model.MetricValue;
import com.dell.prototype.apm.model.javaee.JVM;
import com.sun.management.OperatingSystemMXBean;

public class JVMCollector {

    private static MemoryMXBean memoryMXBean = ManagementFactory.getMemoryMXBean();
    private static RuntimeMXBean runtime = ManagementFactory.getRuntimeMXBean();
    private static OperatingSystemMXBean operatingSystemMXBean = (OperatingSystemMXBean) ManagementFactory.getOperatingSystemMXBean();
    private static ThreadMXBean threadMXBean = ManagementFactory.getThreadMXBean();
    private static List<GarbageCollectorMXBean> gcMbeans = ManagementFactory.getGarbageCollectorMXBeans();
    private static Map<String, Double> oldValues = new HashMap<String, Double>();
    private static Date lastScheduleDateTime = null;

    public static JVM collect() {
    	long interval = lastScheduleDateTime != null ? new Date().getTime() - lastScheduleDateTime.getTime() : Long.MAX_VALUE;
        JVM jvm = new JVM(runtime.getName(), runtime.getVmVersion(), operatingSystemMXBean.getArch());

        Metric<JVM> usedHeap = new Metric<JVM>("usedHeap", jvm);
        MetricValue usedHeapValue = new MetricValue();
        //in MB
        usedHeapValue.setValue(memoryMXBean.getHeapMemoryUsage().getUsed() / (1024 * 1024));
        usedHeap.addMetricValue(usedHeapValue);
        jvm.setUsedHeap(usedHeap);

        Metric<JVM> committedHeap = new Metric<JVM>("committedHeap", jvm);
        MetricValue committedHeapValue = new MetricValue();
        committedHeapValue.setValue(memoryMXBean.getHeapMemoryUsage().getCommitted()  / (1024 * 1024));
        committedHeap.addMetricValue(committedHeapValue);
        jvm.setCommittedHeap(committedHeap);

        Metric<JVM> threadsCount = new Metric<JVM>("threadsCount", jvm);
        MetricValue threadsCountValue = new MetricValue();
        threadsCountValue.setValue(threadMXBean.getThreadCount());
        threadsCount.addMetricValue(threadsCountValue);
        jvm.setThreadsCount(threadsCount);

        Metric<JVM> avgThreadCpuTime = new Metric<JVM>("avgThreadCpuTime", jvm);
        MetricValue avgThreadCpuTimeValue = new MetricValue();
        Double oldProcessCpuTime = oldValues.get("oldProcessCpuTime");
        long processCpuTime = operatingSystemMXBean.getProcessCpuTime();
        Double processCpuTimeDelta = 0.0d;
        if(oldProcessCpuTime != null){
        	processCpuTimeDelta = processCpuTime - oldProcessCpuTime;
        }
        avgThreadCpuTimeValue.setValue(processCpuTimeDelta /(1000 * 1000));
        avgThreadCpuTime.addMetricValue(avgThreadCpuTimeValue);
        jvm.setAvgThreadCpuTime(avgThreadCpuTime);
        oldValues.put("oldProcessCpuTime", new Double(processCpuTime));


        double totalCollectionCount = 0d;
        for (GarbageCollectorMXBean gcMbean : gcMbeans) {
            totalCollectionCount += gcMbean.getCollectionCount();
        }
        Double oldTotalCollectionCount = oldValues.get("oldTotalCollectionCount");
        double collectionCountDelta = 0d;
        if(oldTotalCollectionCount != null){
        	collectionCountDelta = totalCollectionCount - oldTotalCollectionCount;
        }
        Metric<JVM> gcOverhead = new Metric<JVM>("gcOverhead", jvm);
        MetricValue gcOverheadValue = new MetricValue();
        //The frequency in minute
        gcOverheadValue.setValue(collectionCountDelta / (interval / 1000 / 60));
        gcOverhead.addMetricValue(gcOverheadValue);
        jvm.setGcOverhead(gcOverhead);
        oldValues.put("oldTotalCollectionCount", totalCollectionCount);
        return jvm;
    }
}

