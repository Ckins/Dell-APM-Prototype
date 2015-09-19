package com.dell.prototype.apm.util;

import java.util.Date;
import java.util.List;

import com.dell.prototype.apm.model.Metric;
import com.dell.prototype.apm.model.MetricValue;

/**
 * 
 * A utility helps to process Metric object
 */
public class MetricUtil {

	/**
	 * try to take a look whether last metric value period covers current time,
	 * if yes, return it, otherwise, create a new one that cover it
	 * 
	 * @param target
	 *            metric
	 * @return metric value covered current time
	 */
	public static MetricValue retrieveOrCreateCoveredMetricValue(Metric<?> metric, Date startTime, long period) {
		Date now = new Date();
		MetricValue val = null;
		if (metric.getValues().size() == 0) {
			val = metric.createMetricValue();
		} else {
			MetricValue lastVal = metric.getValues().get(metric.getValues().size() - 1);
			if (now.getTime() >= lastVal.getStartTime().getTime() && now.getTime() <= lastVal.getEndTime().getTime()) {
				return lastVal;
			} else {
				val = metric.createMetricValue();
			}
		}
		val.setStartTime(startTime);
		val.setEndTime(new Date(val.getStartTime().getTime() + period));
		val.setSamplePeriod(60 * 1000);
		return val;
	}

	/**
	 * roll up the raw metric to a new proper one
	 * 
	 * @param metric
	 *            the raw metric
	 * @param startTime
	 *            roll up to start time
	 * @param endTime
	 *            roll up to end time
	 * @param dataPointCount
	 *            expected the number of data point
	 * @return a properly rolled up metric
	 */
	public static <T> Metric<T> rollup(Metric<T> metric, Date startTime, Date endTime, int dataPointCount) {
		Metric<T> rollupMetric = new Metric<T>(metric.getName(), metric.getParent());
		if (metric.getValues().size() == 0) {
			return metric;
		}
		long samplePeriod = metric.getValues().get(0).getSamplePeriod();
		int granularity = (int) Math.ceil((endTime.getTime() - startTime.getTime()) * 1.0 / samplePeriod
				/ dataPointCount);
		Date endTimeCursor = endTime;
		for (int i = 0; i < dataPointCount; i++) {
			Date rollupMetricValStartTime = new Date(endTimeCursor.getTime() - granularity * samplePeriod);
			List<MetricValue> values = metric.retrieve(rollupMetricValStartTime, endTimeCursor);
			MetricValue rollupMetricVal = rollupMetric.createPreviousMetricValue(rollupMetricValStartTime,
					endTimeCursor);
			rollupMetricVal.merge(values);
			endTimeCursor = rollupMetricValStartTime;
		}
		return rollupMetric;
	}
}
