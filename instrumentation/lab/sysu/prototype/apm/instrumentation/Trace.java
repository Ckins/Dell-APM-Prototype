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

package lab.sysu.prototype.apm.instrumentation;

public class Trace {

    private final long tid;
    private final long timestamp;
    private final TraceMethodInfo traceMethodInfo;


    public long getTimestamp() {
        return timestamp;
    }

    public TraceMethodInfo getTraceMethodInfo() {
        return traceMethodInfo;
    }



    public boolean isJdbcTrace() {
        return traceMethodInfo instanceof JDBCTraceMethodInfo;
    }

    public static Trace create(long tid, TraceMethodInfo traceMethodInfo, long timestamp) {
        return new Trace(tid, traceMethodInfo, timestamp);
    }

    public String getSql() {
        if (this.isJdbcTrace()) {
            return ((JDBCTraceMethodInfo) traceMethodInfo).getSql();
        } else {
            return null;
        }
    }

    private Trace(long tid, TraceMethodInfo desc, long timestamp) {
        this.tid = tid;
        this.traceMethodInfo = desc;
        this.timestamp = timestamp;
    }


    @Override
    public String toString() {
        return new StringBuilder()
                .append(" ( ")
                .append(" tid: ").append(tid)
                .append(" time: ").append(timestamp)
                .append(" traceMethodInfo: ").append(traceMethodInfo.toString())
                .append(" ) ")
                .toString();
    }
}
