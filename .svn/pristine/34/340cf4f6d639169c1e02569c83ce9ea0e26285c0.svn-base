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

package com.dell.prototype.apm.model;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

/**
 * It presents a data point of sample
 */
public class MetricValue implements Serializable {

    private static final long serialVersionUID = -6529910121777530621L;
    private Date startTime;
    private Date endTime;
    private long samplePeriod;
    private int count;
    private Double min;
    private Double max;
    private Double avg;

    public Date getStartTime() {
        return startTime;
    }

    public void setStartTime(Date startTime) {
        this.startTime = startTime;
    }

    public Date getEndTime() {
        return endTime;
    }

    public void setEndTime(Date endTime) {
        this.endTime = endTime;
    }

    public long getSamplePeriod() {
        return samplePeriod;
    }

    public void setSamplePeriod(long samplePeriod) {
        this.samplePeriod = samplePeriod;
    }

    public int getCount() {
        return count;
    }

    public void setCount(int count) {
        this.count = count;
    }

    public Double getMin() {
        return min;
    }

    public void setMin(Double min) {
        this.min = min;
    }

    public Double getMax() {
        return max;
    }

    public void setMax(Double max) {
        this.max = max;
    }

    public Double getAvg() {
        return avg;
    }

    public void setAvg(Double avg) {
        this.avg = avg;
    }

    public MetricValue() {
        Date now = new Date();
        this.startTime = now;
        this.endTime = now;
        this.samplePeriod = 0l;
        this.count = 1;
    }

    /*
     * not handle null case
     */
    public void append(Double val) {
        count++;
        if (min == null || min > val) {
            min = val;
        }
        if (max == null || max < val) {
            max = val;
        }
        if (avg == null) {
            avg = val;
        } else {
            avg = (avg * (count - 1) + val) / count;
        }
    }

    public void merge(MetricValue value) {
        count += value.getCount();
        if (min == null || min > value.getMin()) {
            min = value.getMin();
        }
        if (max == null || max < value.getMax()) {
            max = value.getMax();
        }
        if (avg == null) {
            avg = value.getAvg();
        } else {
            avg = (avg * (count - value.getCount()) + value.getAvg() * value.getCount()) / count;
        }
    }

    public void merge(List<MetricValue> values) {
        for (MetricValue value : values) {
            merge(value);
        }
    }

    public void setValue(double value) {
        this.max = value;
        this.min = value;
        this.avg = value;
    }

    @Override
    public String toString() {
        return getStartTime() + " ~ " + getEndTime() + " avg:" + getAvg() + " count:" + getCount();
    }
}
