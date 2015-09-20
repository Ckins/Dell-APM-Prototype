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

package lab.sysu.prototype.apm.model;

import java.io.IOException;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import lab.sysu.prototype.apm.model.base.MaxCapacityList;
import lab.sysu.prototype.apm.model.javaee.JVM;

import com.google.gson.*;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;

/**
 * A object represents a serial data over timeline
 *
 * @param <T> parent who owns this type
 */
public class Metric<T> {

    /**
     * metric name
     */
    private String name;
    /**
     * the metric points, to reduce the risk of heap size too large, only keep
     * first ten thousands elements
     */
    private List<MetricValue> values = new MaxCapacityList<MetricValue>(10000);
    /**
     * owner of the metric
     */
    private transient T parent;

    public Metric(String name, T parent) {
        this.name = name;
        this.parent = parent;
    }

    /**
     * retrieve metric values by a time range
     *
     * @param startTime start time of the time range
     * @param endTime   end time of the time range
     * @return a list of metric values in the time range
     */
    public List<MetricValue> retrieve(Date startTime, Date endTime) {
        List<MetricValue> newValues = new ArrayList<MetricValue>();
        for (MetricValue value : values) {
            if (endTime.getTime() > value.getStartTime().getTime()
                    && startTime.getTime() < value.getEndTime().getTime()) {
                newValues.add(value);
            }
        }
        return newValues;
    }

    public String getName() {
        return name;
    }

    public List<MetricValue> getValues() {
        return values;
    }

    public T getParent() {
        return parent;
    }

    public MetricValue createMetricValue() {
        MetricValue val = new MetricValue();
        values.add(val);
        return val;
    }

    public MetricValue createMetricValue(Date startTime, Date endTime) {
        MetricValue val = createMetricValue();
        val.setStartTime(startTime);
        val.setEndTime(endTime);
        return val;
    }

    public MetricValue createPreviousMetricValue(Date startTime, Date endTime) {
        MetricValue val = new MetricValue();
        values.add(0, val);
        val.setStartTime(startTime);
        val.setEndTime(endTime);
        return val;
    }

    public void addMetricValue(MetricValue value) {
        this.values.add(value);
    }

    public void setParent(T jvm) {
        this.parent = jvm;
    }

    @Override
    public String toString() {
        return new GsonBuilder().registerTypeAdapter(this.getClass(), new TypeAdapter<Metric>() {
            @Override
            public void write(JsonWriter jsonWriter, Metric metric) throws IOException {
                jsonWriter.beginObject();
                jsonWriter.name("name").value(metric.getName());
                List<MetricValue> values = metric.getValues();
                if (values.size() > 0) {
                    jsonWriter.name("latestValue").value(values.get(values.size() - 1).toString());
                } else {
                    jsonWriter.name("latestValue").value("");
                }
                jsonWriter.endObject();
            }

            @Override
            public Metric read(JsonReader jsonReader) throws IOException {
                return null;
            }
        }).setPrettyPrinting().create().toJson(this);
    }
}