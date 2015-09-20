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

package lab.sysu.prototype.apm.instrumentation;

import java.lang.instrument.ClassFileTransformer;
import java.lang.instrument.Instrumentation;
import java.util.logging.Logger;

public class Premain {

    public static void premain(String agentArgs, Instrumentation inst) {
        Logger.getLogger("lab.sysu.prototype.apm.instrumentation.Premain").info("Premain.premain() was called.");
        ClassFileTransformer trans = new TransformerAgent();
        inst.addTransformer(trans);
    }

}
