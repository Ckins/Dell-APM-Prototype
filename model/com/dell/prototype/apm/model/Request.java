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

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.UUID;

/**
 * a object represents one http request
 * 
 */
public class Request {

	/**
	 * auto generated unique id
	 */
	private String id = UUID.randomUUID().toString();
	/**
	 * the request initial time in browser
	 */
	private Date initialTime;
	/**
	 * the request end time after browser finished receiving html
	 */
	private Date endTime;
	/**
	 * whether the request has error
	 */
	private boolean hasError;
	/**
	 * request uri
	 */
	private String uri;
	/**
	 * request query string
	 */
	private String queryString;
	/**
	 * request time break down
	 */
	private List<RequestTiming> timings = new ArrayList<RequestTiming>();

    public Request(String uri, String queryString, boolean hasError) {
        this.uri = uri;
        this.queryString = queryString;
        this.hasError = hasError;
    }

    public long getE2ETime() {
		return endTime.getTime() - initialTime.getTime();
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public Date getInitialTime() {
		return initialTime;
	}

	public void setInitialTime(Date initialTime) {
		this.initialTime = initialTime;
	}

	public Date getEndTime() {
		return endTime;
	}

	public void setEndTime(Date endTime) {
		this.endTime = endTime;
	}

	public boolean isHasError() {
		return hasError;
	}

	public void setHasError(boolean hasError) {
		this.hasError = hasError;
	}

	public String getUri() {
		return uri;
	}

	public void setUri(String uri) {
		this.uri = uri;
	}

	public String getQueryString() {
		return queryString;
	}

	public void setQueryString(String queryString) {
		this.queryString = queryString;
	}

	public List<RequestTiming> getTimings() {
		return timings;
	}

	public void setTimings(List<RequestTiming> timings) {
		this.timings = timings;
	}

    @Override
    public String toString() {
        return new GsonBuilder().setPrettyPrinting().create().toJson(this).toString();
    }
}
