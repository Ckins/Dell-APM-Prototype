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

package com.dell.prototype.apm.server;

import com.dell.prototype.apm.model.APMPrototype;
import com.dell.prototype.apm.model.Metric;
import com.dell.prototype.apm.model.Request;
import com.dell.prototype.apm.model.RequestTiming;
import com.dell.prototype.apm.model.javaee.ApplicationServer;
import com.dell.prototype.apm.model.javaee.DatabaseTiming;
import com.dell.prototype.apm.model.javaee.JVM;
import com.google.gson.*;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * A servelet provides data submission from java instrumentation
 */
public class AppServerMonitorDataSubmissionServlet extends HttpServlet {

    private static final long serialVersionUID = 719478316100613642L;

    @Override
    public void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
        ApplicationServer server = APMPrototype.getRoot().getAppServer();
        response.getWriter().append(server.toString());
    }

    @Override
    public void doPost(HttpServletRequest request, HttpServletResponse response) {
        String line = "";
        try {
            line = request.getReader().readLine();
        } catch (IOException e) {
            e.printStackTrace();
        }
        String[] msgs = line.split(";;");
        List<Request> requests = new ArrayList<Request>();

        Gson gson = new GsonBuilder()
                .registerTypeAdapter(RequestTiming.class, new JsonDeserializer<RequestTiming>() {
                    @Override
                    public RequestTiming deserialize(JsonElement jsonElement, Type type, JsonDeserializationContext jsonDeserializationContext) throws JsonParseException {
                        if (!jsonElement.isJsonObject()) return new RequestTiming();

                        String name = ((JsonObject) jsonElement).getAsJsonPrimitive("name").getAsString();
                        if ("database".equals(name)) {
                            DatabaseTiming databaseTiming = new DatabaseTiming(name);
                            databaseTiming.setTime(((JsonObject) jsonElement).getAsJsonPrimitive("time").getAsLong());
                            String sql = ((JsonObject) jsonElement).getAsJsonPrimitive("sql").getAsString();
                            databaseTiming.setSql(sql);
                            List<String> bindingParameters = new ArrayList<String>();
                            for (JsonElement bindParameter : ((JsonObject) jsonElement).get("bindingParameters").getAsJsonArray()) {
                                bindingParameters.add(bindParameter.getAsString());
                            }
                            databaseTiming.setBindingParameters(bindingParameters);
                            return databaseTiming;
                        }
                        RequestTiming requestTiming = new RequestTiming();
                        requestTiming.setName("server");
                        requestTiming.setTime(((JsonObject) jsonElement).getAsJsonPrimitive("time").getAsLong());
                        return requestTiming;
                    }
                })
                .create();
        for (int i = 0; i < msgs.length - 1; i++) {
            if (null != msgs[i] && !"".equals(msgs[i])) {
                requests.add(gson.fromJson(msgs[i], Request.class));
            }
        }
        String jvmMsg = msgs[msgs.length - 1];
        JVM jvm = gson.fromJson(jvmMsg, JVM.class);
        Metric<JVM> avgThreadCpuTime = jvm.getAvgThreadCpuTime();
        setParent(jvm, avgThreadCpuTime);

        Metric<JVM> committedHeap = jvm.getCommittedHeap();
        setParent(jvm, committedHeap);

        Metric<JVM> usedHeap = jvm.getUsedHeap();
        setParent(jvm, usedHeap);

        Metric<JVM> gcOverhead = jvm.getGcOverhead();
        setParent(jvm, gcOverhead);

        Metric<JVM> threadsCount = jvm.getThreadsCount();
        setParent(jvm, threadsCount);

        ApplicationServer server = APMPrototype.getRoot().getAppServer();
        server.setRequests(requests);
        if (server.getJvm() == null) server.creatJVM(jvm.getName(), jvm.getVersion(), jvm.getArchitecture());

        JVM storedJvm = server.getJvm();
        storedJvm.setName(jvm.getName());
        storedJvm.setArchitecture(jvm.getArchitecture());
        storedJvm.setVersion(jvm.getVersion());

        mergeJVM(jvm, storedJvm);
    }

    private void mergeJVM(JVM jvm, JVM storedJvm) {
        Metric avgThreadCpuTimeInData = null;
        if ((avgThreadCpuTimeInData = jvm.getAvgThreadCpuTime()) != null) {
            storedJvm.getAvgThreadCpuTime().getValues().addAll(avgThreadCpuTimeInData.getValues());
        }
        Metric committedHeap = null;
        if ((committedHeap = jvm.getCommittedHeap()) != null) {
            storedJvm.getCommittedHeap().getValues().addAll(committedHeap.getValues());
        }
        Metric gcOverhead = null;
        if ((gcOverhead = jvm.getGcOverhead()) != null) {
            storedJvm.getGcOverhead().getValues().addAll(gcOverhead.getValues());
        }
        Metric threadsCount = null;
        if ((threadsCount = jvm.getThreadsCount()) != null) {
            storedJvm.getThreadsCount().getValues().addAll(threadsCount.getValues());
        }
        Metric usedHeap = null;
        if ((usedHeap = jvm.getUsedHeap()) != null) {
            storedJvm.getUsedHeap().getValues().addAll(usedHeap.getValues());
        }
    }

    private void setParent(JVM jvm, Metric<JVM> metric) {
        if (metric != null) {
            metric.setParent(jvm);
        }
    }
}
