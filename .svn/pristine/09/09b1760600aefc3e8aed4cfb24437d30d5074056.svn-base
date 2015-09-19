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

import com.dell.prototype.apm.model.enduser.EndUserPerformance;
import com.dell.prototype.apm.model.javaee.ApplicationServer;

/**
 * root node of APM prototype project data model
 * 
 */
public class APMPrototype {

	/**
	 * end user performance data
	 */
	private EndUserPerformance enduser = new EndUserPerformance();

	/**
	 * application server performance data
	 */
	private ApplicationServer appServer = new ApplicationServer();

	private static APMPrototype root = new APMPrototype();

	private APMPrototype() {
	}

	public EndUserPerformance getEnduser() {
		return enduser;
	}

	public void setEnduser(EndUserPerformance enduser) {
		this.enduser = enduser;
	}

	public ApplicationServer getAppServer() {
		return appServer;
	}

	public void setAppServer(ApplicationServer appServer) {
		this.appServer = appServer;
	}

	public static APMPrototype getRoot() {
		return root;
	}
}
