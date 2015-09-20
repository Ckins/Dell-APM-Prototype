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

package lab.sysu.prototype.apm.model.javaee;

import com.google.gson.GsonBuilder;
import com.google.gson.TypeAdapter;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;

import java.io.IOException;

import lab.sysu.prototype.apm.model.Metric;

/**
 * a object represents a JVM instance, including the base information and
 * runtime performance metrics
 * 
 */
public class JVM {

	/**
	 * JVM name
	 */
	private String name;
	/**
	 * JVM version
	 */
	private String version;
	/**
	 * JVM architecture
	 */
	private String architecture;
	/**
	 * JVM used heap by time serial
	 */
	private Metric<JVM> usedHeap;
	/**
	 * JVM committed heap by time serial
	 */
	private Metric<JVM> committedHeap;
	/**
	 * JVM GC overheads by time serial
	 */
	private Metric<JVM> gcOverhead;
	/**
	 * threads count in JVM by time serial
	 */
	private Metric<JVM> threadsCount;
	/**
	 * average thread cpu time by time serial
	 */
	private Metric<JVM> avgThreadCpuTime;

	public void init() {
		usedHeap = new Metric<JVM>("usedHeap", this);
		committedHeap = new Metric<JVM>("committedHeap", this);
		gcOverhead = new Metric<JVM>("gcOverhead", this);
		threadsCount = new Metric<JVM>("threadsCount", this);
		avgThreadCpuTime = new Metric<JVM>("avgThreadCpuTime", this);
	}

	public JVM(String name, String version, String architecture) {
		this.name = name;
		this.version = version;
		this.architecture = architecture;
        init();
	}

	public String getName() {
		return name;
	}

	public String getVersion() {
		return version;
	}

	public String getArchitecture() {
		return architecture;
	}

	public Metric<JVM> getUsedHeap() {
		return usedHeap;
	}

	public Metric<JVM> getCommittedHeap() {
		return committedHeap;
	}

	public Metric<JVM> getGcOverhead() {
		return gcOverhead;
	}

	public Metric<JVM> getThreadsCount() {
		return threadsCount;
	}

	public Metric<JVM> getAvgThreadCpuTime() {
		return avgThreadCpuTime;
	}

	public void setName(String name) {
		this.name = name;
	}

	public void setVersion(String version) {
		this.version = version;
	}

	public void setArchitecture(String architecture) {
		this.architecture = architecture;
	}

	public void setUsedHeap(Metric<JVM> usedHeap) {
		this.usedHeap = usedHeap;
	}

	public void setCommittedHeap(Metric<JVM> committedHeap) {
		this.committedHeap = committedHeap;
	}

	public void setGcOverhead(Metric<JVM> gcOverhead) {
		this.gcOverhead = gcOverhead;
	}

	public void setThreadsCount(Metric<JVM> threadsCount) {
		this.threadsCount = threadsCount;
	}

	public void setAvgThreadCpuTime(Metric<JVM> avgThreadCpuTime) {
		this.avgThreadCpuTime = avgThreadCpuTime;
	}

    @Override
    public String toString() {
        return new GsonBuilder().setPrettyPrinting().registerTypeAdapter(this.getClass(), new TypeAdapter<JVM>() {
            @Override
            public void write(JsonWriter jsonWriter, JVM jvm) throws IOException {
                jsonWriter.beginObject();
                jsonWriter.name("name").value(jvm.getName());
                jsonWriter.name("version").value(jvm.getVersion());
                jsonWriter.name("architecture").value(jvm.getArchitecture());
                jsonWriter.name("usedHeap").value(jvm.getUsedHeap().toString());
                jsonWriter.name("committedHeap").value(jvm.getCommittedHeap().toString());
                jsonWriter.name("gcOverhead").value(jvm.getGcOverhead().toString());
                jsonWriter.name("threadsCount").value(jvm.getThreadsCount().toString());
                jsonWriter.name("avgThreadCpuTime").value(jvm.getAvgThreadCpuTime().toString());
                jsonWriter.endObject();
            }

            @Override
            public JVM read(JsonReader jsonReader) throws IOException {
                return null;
            }
        }).create().toJson(this);
    }
}

