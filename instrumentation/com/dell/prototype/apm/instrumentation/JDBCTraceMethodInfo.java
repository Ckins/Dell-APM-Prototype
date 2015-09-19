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

public class JDBCTraceMethodInfo extends TraceMethodInfo {

    public String getSql() {
        return sql;
    }

    private final String sql;

    public JDBCTraceMethodInfo(StackTraceElement[] stackTraceElements, String sql) {
        super(stackTraceElements);
        this.sql = sql;
    }

    @Override
    public String toString() {
        return " ( " + super.toString() + " sql: " + sql + " ) ";
    }
}
