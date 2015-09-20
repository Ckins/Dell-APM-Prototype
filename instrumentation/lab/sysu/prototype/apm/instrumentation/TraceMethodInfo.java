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

import java.util.Arrays;
import java.util.List;

public class TraceMethodInfo {
    private final String clazz;
    private final String method;
    private final List<StackTraceElement> stackTraceElements;

    public String getClazz() {
        return clazz;
    }

    public String getMethod() {
        return method;
    }

    public TraceMethodInfo(StackTraceElement[] stackTraceElements) {
        this.clazz = stackTraceElements[1].getClassName();
        this.method = stackTraceElements[1].getMethodName();
        this.stackTraceElements = Arrays.asList(stackTraceElements).subList(1, stackTraceElements.length);
    }

    public List<StackTraceElement> getStackTraceElements() {
        return stackTraceElements;
    }

    @Override
    public String toString() {
        return new StringBuilder()
                .append(" ( ")
                .append(" class: ").append(clazz)
                .append(" method: ").append(method)
                .append(" ) ")
                .toString();
    }
}
