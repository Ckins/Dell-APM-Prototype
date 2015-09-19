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

package com.dell.prototype.apm.model.javaee;

import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import com.dell.prototype.apm.model.Metric;
import com.dell.prototype.apm.model.Request;
import com.dell.prototype.apm.model.base.MaxCapacityList;
import com.google.gson.GsonBuilder;
import com.google.gson.TypeAdapter;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;

/**
 * A object represents a application server
 */
public class ApplicationServer implements Serializable {

    private static final long serialVersionUID = -195747197602385354L;

    /**
     * app server name
     */
    private String name = "";
    /**
     * avg response time
     */
    private Metric<ApplicationServer> responseTime;
    /**
     * JVM performance object
     */
    private JVM jvm;
    /**
     * all the real time http requests, only keep first ten thousands elements
     */
    private List<Request> requests = new MaxCapacityList<Request>(10000);

    public ApplicationServer() {
        responseTime = new Metric<ApplicationServer>("responseTime", this);
    }

    public void creatJVM(String name, String version, String architecture) {
        if (jvm != null) {
            throw new RuntimeException("JVM already exists");
        }
        jvm = new JVM(name, version, architecture);
    }

    public String getName() {
        return name;
    }

    public Metric<ApplicationServer> getResponseTime() {
        return responseTime;
    }

    public JVM getJvm() {
        return jvm;
    }

    public List<Request> getRequests() {
        return requests;
    }

    public static long getSerialversionuid() {
        return serialVersionUID;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setResponseTime(Metric<ApplicationServer> responseTime) {
        this.responseTime = responseTime;
    }

    public void setRequests(List<Request> requests) {
        this.requests.addAll(requests);
    }

    @Override
    public String toString() {

        return new GsonBuilder().setPrettyPrinting().registerTypeAdapter(this.getClass(), new TypeAdapter<ApplicationServer>() {
            private String getFormattedRequests(List<Request> requests) {
                StringBuilder sb = new StringBuilder();
                sb.append("[");
                for (int i = 0; i < requests.size(); i++) {
                    sb.append("request sequence " + i + ": ");
                    sb.append(requests.get(i));
                }
                sb.append("]");
                return sb.toString();
            }


            @Override
            public void write(JsonWriter jsonWriter, ApplicationServer applicationServer) throws IOException {
                jsonWriter.beginObject();
                jsonWriter.name("name").value(applicationServer.getName());
                jsonWriter.name("jvm").value(applicationServer.getJvm() == null ? "" : applicationServer.getJvm().toString());
                jsonWriter.name("responseTime").value(applicationServer.getResponseTime().toString());
                jsonWriter.name("requests").value(getFormattedRequests(applicationServer.getRequests()));
                jsonWriter.endObject();
            }

            @Override
            public ApplicationServer read(JsonReader jsonReader) throws IOException {
                return null;
            }
        }).create().toJson(this);
    }
}
