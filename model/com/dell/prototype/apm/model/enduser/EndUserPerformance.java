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

package com.dell.prototype.apm.model.enduser;

import com.dell.prototype.apm.model.Metric;

/**
 * End user performance metric
 */
public class EndUserPerformance {

	/**
	 * network time by time serial
	 */
	private Metric<EndUserPerformance> networkTime;

	/**
	 * response time evaluated in browser by time serial
	 */
	private Metric<EndUserPerformance> responseTime;

	/**
	 * dom render time by time serial
	 */
	private Metric<EndUserPerformance> domTime;

	/**
	 * total end user time by time serial, it is the real end user waiting time
	 */
	private Metric<EndUserPerformance> endUserTime;

	public EndUserPerformance() {
		networkTime = new Metric<EndUserPerformance>("networkTime", this);
		responseTime = new Metric<EndUserPerformance>("responseTime", this);
		domTime = new Metric<EndUserPerformance>("domTime", this);
		endUserTime = new Metric<EndUserPerformance>("endUserTime", this);
	}

	public Metric<EndUserPerformance> getNetworkTime() {
		return networkTime;
	}

	public void setNetworkTime(Metric<EndUserPerformance> networkTime) {
		this.networkTime = networkTime;
	}

	public Metric<EndUserPerformance> getResponseTime() {
		return responseTime;
	}

	public void setResponseTime(Metric<EndUserPerformance> responseTime) {
		this.responseTime = responseTime;
	}

	public Metric<EndUserPerformance> getDomTime() {
		return domTime;
	}

	public void setDomTime(Metric<EndUserPerformance> domTime) {
		this.domTime = domTime;
	}

	public Metric<EndUserPerformance> getEndUserTime() {
		return endUserTime;
	}

	public void setEndUserTime(Metric<EndUserPerformance> endUserTime) {
		this.endUserTime = endUserTime;
	}

}
