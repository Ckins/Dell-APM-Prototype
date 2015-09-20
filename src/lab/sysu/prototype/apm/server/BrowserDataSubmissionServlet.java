package lab.sysu.prototype.apm.server;

import java.io.IOException;
import java.util.Calendar;
import java.util.Date;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import lab.sysu.prototype.apm.model.APMPrototype;
import lab.sysu.prototype.apm.model.enduser.EndUserPerformance;
import lab.sysu.prototype.apm.util.MetricUtil;

import org.apache.commons.lang3.time.DateUtils;


import net.sf.json.JSONObject;

/**
 * A servelet provides data submission from browser instrumentation
 */
public class BrowserDataSubmissionServlet extends HttpServlet {

	private static final long serialVersionUID = 7832005991263177990L;
	private static final Logger logger = Logger.getLogger(BrowserDataSubmissionServlet.class.toString());

	@Override
	public void doGet(HttpServletRequest request, HttpServletResponse response) {
		String detail = request.getParameter("d");
		if (detail == null || detail.length() == 0) {
			try {
				response.sendError(403);
			} catch (IOException e) {
				logger.log(Level.WARNING, "cannot handle request", e);
			}
			return;
		}
		JSONObject obj = JSONObject.fromObject(detail);
		long networkTime = obj.getLong("networkTime");
		long responseTime = obj.getLong("responseTime");
		long domTime = obj.getLong("domTime");
		long enduserTime = obj.getLong("enduserTime");
		EndUserPerformance enduserModel = APMPrototype.getRoot().getEnduser();
		Date startTime = DateUtils.truncate(new Date(), Calendar.MINUTE);
		long period = 60 * 1000;
		/*
		 * set metric interval to 1 minute
		 */
		MetricUtil.retrieveOrCreateCoveredMetricValue(enduserModel.getNetworkTime(), startTime, period).append(
				new Double(networkTime));
		MetricUtil.retrieveOrCreateCoveredMetricValue(enduserModel.getResponseTime(), startTime, period).append(
				new Double(responseTime));
		MetricUtil.retrieveOrCreateCoveredMetricValue(enduserModel.getDomTime(), startTime, period).append(
				new Double(domTime));
		MetricUtil.retrieveOrCreateCoveredMetricValue(enduserModel.getEndUserTime(), startTime, period).append(
				new Double(enduserTime));
	}

	@Override
	public void doPost(HttpServletRequest request, HttpServletResponse response) {
		doGet(request, response);
	}
}
