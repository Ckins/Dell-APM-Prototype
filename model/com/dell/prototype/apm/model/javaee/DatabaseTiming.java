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

import java.util.ArrayList;
import java.util.List;

import com.dell.prototype.apm.model.RequestTiming;


/**
 * database time detail
 * 
 */
public class DatabaseTiming extends RequestTiming {

	/**
	 * the sql statement
	 */
	private String sql;
	/**
	 * the sql binding parameters
	 */
	private List<String> bindingParameters = new ArrayList<String>();

	public DatabaseTiming() {
        this.name = "database";
	}

	public DatabaseTiming(String sql) {
        this.name = "database";
        this.sql = sql;
	}

	public String getSql() {
		return sql;
	}

	public void setSql(String sql) {
		this.sql = sql;
	}

	public List<String> getBindingParameters() {
		return bindingParameters;
	}

	public void setBindingParameters(List<String> bindingParameters) {
		this.bindingParameters = bindingParameters;
	}

}
