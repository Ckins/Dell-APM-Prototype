package lab.sysu.prototype.apm.server;

import java.io.IOException;
import java.io.OutputStream;
import java.text.ParseException;
import java.util.Date;
import java.util.List;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import lab.sysu.prototype.apm.model.APMPrototype;
import lab.sysu.prototype.apm.model.MetricValue;
import lab.sysu.prototype.apm.model.Request;
import lab.sysu.prototype.apm.model.RequestTiming;
import lab.sysu.prototype.apm.model.enduser.EndUserPerformance;
import lab.sysu.prototype.apm.model.javaee.ApplicationServer;
import lab.sysu.prototype.apm.model.javaee.DatabaseTiming;
import lab.sysu.prototype.apm.model.javaee.JVM;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

import org.apache.commons.lang3.time.DateFormatUtils;
import org.apache.commons.lang3.time.FastDateFormat;


/**
 * A servelet provides data query from client
 */
public class PerformanceDataQueryServlet extends HttpServlet {

	private static final long serialVersionUID = -6177889195852377634L;

	@Override
	public void doGet(HttpServletRequest request, HttpServletResponse response) {
		Date startTime = parseDate(request.getParameter("startTime"));
		Date endTime = parseDate(request.getParameter("endTime"));
		long timeRangeDuration = endTime.getTime() - startTime.getTime();

		APMPrototype apmPrototype = APMPrototype.getRoot();
		EndUserPerformance enduserModel = apmPrototype.getEnduser();
		ApplicationServer appServer = apmPrototype.getAppServer();

		OutputStream os = null;

		try {
			os = response.getOutputStream();
			
			JSONObject data = new JSONObject();
			data.put("requestStat", responseToRequestStat(appServer));
			data.put(	"responseTime",
						responseToEnduserPerformance(	startTime, endTime, timeRangeDuration,
														enduserModel));
			data.put("transactions", responseToTransations(appServer));
			reponseToJVM(startTime, endTime, timeRangeDuration, appServer, data);
			
			os.write(data.toString().getBytes());
			os.flush();
		}
		catch (IOException e) {
			e.printStackTrace();
		}
		catch (Exception e) {
			e.printStackTrace();
		}

		finally {
			if (null != os) {
				try {
					os.close();
				}
				catch (IOException e) {
				}
			}
		}
	}

	private void reponseToJVM(Date startTime, Date endTime, long timeRangeDuration,
								ApplicationServer appServer, JSONObject data) {
		JVM jvm = appServer.getJvm();
		
		List<MetricValue> usedHeapValues = jvm.getUsedHeap().retrieve(startTime, endTime); 
		List<MetricValue> committedHeapValues = jvm.getCommittedHeap().retrieve(startTime, endTime);
		List<MetricValue> gcRateValues = jvm.getGcOverhead().retrieve(startTime, endTime);
		List<MetricValue> avgCPUTimeValues = jvm.getAvgThreadCpuTime().retrieve(startTime, endTime);
		List<MetricValue> threadCountVlaues = jvm.getThreadsCount().retrieve(startTime, endTime);
		
		JSONArray heapValueItems = new JSONArray();
		for(MetricValue usedHeapValue : usedHeapValues){
			JSONObject heapValueItem = new JSONObject();
			heapValueItem.put("label", formatDate(usedHeapValue.getStartTime(), timeRangeDuration));
			
			JSONArray heapValue = new JSONArray();
			heapValue.add(usedHeapValue.getAvg());
			heapValue.add(getSameTimeValue(committedHeapValues, usedHeapValue.getStartTime()));
			heapValueItem.put("value", heapValue);
			
			heapValueItems.add(heapValueItem);
		}
		data.put("heap", heapValueItems);
		
		JSONArray gcRateValueItems = new JSONArray();
		for(MetricValue gcRate : gcRateValues){
			JSONObject gcRateValueItem = new JSONObject();
			gcRateValueItem.put("label", formatDate(gcRate.getStartTime(), timeRangeDuration));
			gcRateValueItem.put("value", gcRate.getAvg());
			gcRateValueItems.add(gcRateValueItem);
		}
		data.put("gcRate", gcRateValueItems);
		

		JSONArray cpuTimeValueItems = new JSONArray();
		for(MetricValue cpuTime : avgCPUTimeValues){
			JSONObject cpuTimeValueItem = new JSONObject();
			cpuTimeValueItem.put("label", formatDate(cpuTime.getStartTime(), timeRangeDuration));
			cpuTimeValueItem.put("value", cpuTime.getAvg());
			cpuTimeValueItems.add(cpuTimeValueItem);
		}
		data.put("cpuTime", cpuTimeValueItems);
		
		JSONArray threadCountValueItems = new JSONArray();
		for(MetricValue threadCount : threadCountVlaues){
			JSONObject threadCountValueItem = new JSONObject();
			threadCountValueItem.put("label", formatDate(threadCount.getStartTime(), timeRangeDuration));
			threadCountValueItem.put("value", threadCount.getAvg());
			threadCountValueItems.add(threadCountValueItem);
		}
		data.put("threadCount", threadCountValueItems);
	}

	private JSONArray responseToTransations(ApplicationServer appServer){
		List<Request> requests = appServer.getRequests();
		JSONArray jArray = new JSONArray();

		for (Request _request : requests) {
			JSONObject item = new JSONObject();
			item.put("id", _request.getId());
			item.put("time",
						_request.getInitialTime() == null ? ""
										: DateFormatUtils.ISO_DATETIME_FORMAT.format(_request.getInitialTime()));
			item.put("name", _request.getUri());
			item.put("responseTime", _request.getE2ETime());
			item.put("dbTiming", retrieveDBTiming(_request));
			
			jArray.add(item);
		}
		
		
		return jArray;
	}

	private JSONArray retrieveDBTiming(Request _request) {
		JSONArray jArray = new JSONArray();
		for(RequestTiming requestTiming : _request.getTimings()){
			if(requestTiming instanceof DatabaseTiming){
				JSONObject timingItem = new JSONObject();
				DatabaseTiming dbTiming = (DatabaseTiming)requestTiming;
				timingItem.put("exeTime", dbTiming.getTime());
				timingItem.put("sql", dbTiming.getSql());
				
				JSONArray parasArray = null;
				List<String> parameters = dbTiming.getBindingParameters();
				if(null != parameters && parameters.size() > 0){
					parasArray = JSONArray.fromObject(parameters);
				} else {
					parasArray = new JSONArray();
				}
				
				timingItem.put("parameters", parasArray);
				jArray.add(timingItem);
			}
		}
		
		
		return jArray;
	}

	private JSONArray responseToEnduserPerformance(Date startTime, Date endTime, long timeRangeDuration,
												EndUserPerformance enduserModel) {
		List<MetricValue> enduserTimeValues = enduserModel.getEndUserTime().retrieve(startTime,
																						endTime);
		List<MetricValue> responseTimeValues = enduserModel.getResponseTime().retrieve(startTime,
																						endTime);
		List<MetricValue> networkTimeValues = enduserModel.getNetworkTime().retrieve(startTime,
																						endTime);
		List<MetricValue> domTimeValues = enduserModel.getEndUserTime()
														.retrieve(startTime, endTime);

		JSONArray jArray = new JSONArray();

		for (MetricValue value : enduserTimeValues) {
			JSONObject item = new JSONObject();
			Double[] sameTimeValues = new Double[] {
													value.getAvg(),
													getSameTimeValue(	responseTimeValues,
																		value.getStartTime()),
													getSameTimeValue(	networkTimeValues,
																		value.getStartTime()),
													getSameTimeValue(	domTimeValues,
																		value.getStartTime()) };
			item.put("label",formatDate(value.getStartTime(), timeRangeDuration));
			item.put("value", JSONArray.fromObject(sameTimeValues));
			jArray.add(item);
		}
		
		return jArray;
	}

	private JSONArray responseToRequestStat(ApplicationServer appServer){
		List<Request> requests = appServer.getRequests();
		int good = 0, fair = 0, poor = 0, error = 0;

		for (Request _request : requests) {

			if (_request.isHasError()) {
				error++;
				continue;
			}

			if (_request.getE2ETime() < 1 * 1000) {
				good++;
				continue;
			}

			if (_request.getE2ETime() < 5 * 1000) {
				fair++;
				continue;
			} else {
				poor++;
			}

		}

		JSONArray jArray = new JSONArray();
		jArray.add(good);
		jArray.add(fair);
		jArray.add(poor);
		jArray.add(error);

		return jArray;
	}

	private Double getSameTimeValue(List<MetricValue> values, Date startTime) {
		double sameTimeValue = 0d;
		if (null != values && values.size() != 0) {
			for (MetricValue value : values) {
				if (value.getStartTime().equals(startTime)) {
					sameTimeValue = value.getAvg();
				}
			}
		}

		return sameTimeValue;
	}

	private String formatDate(Date startTime, long timeRangeDuration) {
		long fourHours = 4 * 60 * 60 * 1000L;
		long sevenDays = 7 * 24 * 60 * 60 * 1000L;

		if (timeRangeDuration < fourHours) {
			return FastDateFormat.getInstance("HH:mm").format(startTime);
		} else if (timeRangeDuration > sevenDays) {
			return DateFormatUtils.ISO_DATE_FORMAT.format(startTime);
		} else {
			return DateFormatUtils.ISO_DATETIME_FORMAT.format(startTime);
		}
	}

	private Date parseDate(String parameter) {
		try {
			return FastDateFormat.getInstance("yyyy-MM-dd'T'HH:mm").parse(parameter);
		}
		catch (ParseException e) {
			e.printStackTrace();
			return null;
		}
	}

	@Override
	public void doPost(HttpServletRequest request, HttpServletResponse response) {
		this.doGet(request, response);
	}
}
