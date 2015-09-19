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

package com.dell.prototype.apm.instrumentation;

import javassist.*;

import java.io.IOException;
import java.lang.instrument.ClassFileTransformer;
import java.lang.instrument.IllegalClassFormatException;
import java.security.ProtectionDomain;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

public class TransformerAgent implements ClassFileTransformer {
    public static String POINTCUT_CLASS = "javax.servlet.http.HttpServlet";
    public static String POINTCUT_METHOD_SIGNATURE = "(Ljavax/servlet/http/HttpServletRequest;Ljavax/servlet/http/HttpServletResponse;)V";
    public static String POINTCUT_METHOD_NAME = "service";
    public static ClassPool pool = ClassPool.getDefault();
    private Logger logger = Logger.getLogger("com.dell.prototype.apm.instrumentation.TransformerAgent");

    private static String RECORD = new StringBuilder()
            .append("\n{")
            .append("com.dell.prototype.apm.instrumentation.TraceMethodInfo cursor = new com.dell.prototype.apm.instrumentation.TraceMethodInfo(Thread.currentThread().getStackTrace());" +
                    "com.dell.prototype.apm.instrumentation.Trace trace = com.dell.prototype.apm.instrumentation.Trace.create(Thread.currentThread().getId(), cursor, System.currentTimeMillis());\n" +
                    "com.dell.prototype.apm.instrumentation.TraceStorage.setTrace(Thread.currentThread().getId(), trace);\n")
            .append("}\n").toString();

    private static String JDBC_RECORD = new StringBuilder()
            .append("\n{")
            .append("com.dell.prototype.apm.instrumentation.TraceMethodInfo cursor = new com.dell.prototype.apm.instrumentation.JDBCTraceMethodInfo(Thread.currentThread().getStackTrace(), $1);" +
                    "com.dell.prototype.apm.instrumentation.Trace trace = com.dell.prototype.apm.instrumentation.Trace.create(Thread.currentThread().getId(), cursor, System.currentTimeMillis());\n" +
                    "com.dell.prototype.apm.instrumentation.TraceStorage.setTrace(Thread.currentThread().getId(), trace);\n")
            .append("}\n").toString();

    private static String CLEAR = new StringBuilder()
            .append("{")
            .append("com.dell.prototype.apm.instrumentation.TraceStorage.clear(Thread.currentThread().getId());\n")
            .append("}")
            .toString();


    private static String AFTER_SERVICE = new StringBuilder()
            .append("\n{")
            .append("com.dell.prototype.apm.instrumentation.TraceStorage.submit(Thread.currentThread().getId(), ((javax.servlet.http.HttpServletRequest)$1).getRequestURI(), ((javax.servlet.http.HttpServletRequest)$1).getQueryString(), $2.getStatus() > 400);\n")
            .append("System.out.println(\"response code:\" + $2.getStatus());")
            .append("\n}\n")
            .toString();

    private static String LOAD_SQLITE_WITH_SEQUENCE = new StringBuilder()
            .append("{")
            .append("Class.forName(\"org.sqlite.jdbc3.JDBC3Statement\");")
            .append("}").toString();


    private static String AFTER_FILTER = new StringBuilder()
            .append("{")
            .append("String replacement = \"<body><script type=text/javascript src=/apm/APMPrototypeAgent.js></script>\";")
            .append("java.io.PrintWriter cachedWriter = null;")
            .append("com.dell.prototype.apm.instrumentation.ChrResponseWrapper responseWrapper = (com.dell.prototype.apm.instrumentation.ChrResponseWrapper)$2;")
            .append("try {\n" +
                    "                String content = responseWrapper.toString();\n" +
                    "				 java.io.PrintWriter writer = responseWrapper.response.getWriter();" +
                    "                String contentModified = content.replaceFirst(\"<body>\", replacement);\n" +
                    "                writer.write(contentModified);\n" +
                    "                writer.flush();" +
                    "            } catch (Exception e1) {\n" +
                    "                e1.printStackTrace();\n" +
                    "            } finally {" +
                    "                if(cachedWriter != null) cachedWriter.close();" +
                    "            }")
            .append("}")
            .toString();

    private String BEFORE_FILTER = new StringBuilder()
            .append("{")
            .append("com.dell.prototype.apm.instrumentation.ChrResponseWrapper responseWrapper = new com.dell.prototype.apm.instrumentation.ChrResponseWrapper((javax.servlet.http.HttpServletResponse)$2);")
            .append("$2 = responseWrapper;")
            .append("}")
            .toString();


    @Override
    public byte[] transform(ClassLoader loader, String className, Class<?> classBeingRedefined, ProtectionDomain protectionDomain, byte[] classfileBuffer) throws IllegalClassFormatException {
        CtClass cl = null;
        try {
            cl = pool.makeClass(new java.io.ByteArrayInputStream(classfileBuffer));
        } catch (IOException e) {
            logger.log(Level.WARNING, "Could not instrument class:" + className, e);
        }

        CtClass pointcutClass = null;
        if ("org.apache.catalina.core.ApplicationFilterChain".equals(cl.getName())) {
            try {
                CtMethod doFilterMethod = cl.getDeclaredMethod("doFilter");
                doFilterMethod.insertBefore(BEFORE_FILTER);
                doFilterMethod.insertAfter(AFTER_FILTER);
                pointcutClass = cl;
            } catch (Exception e) {
                logger.log(Level.WARNING, "Instrument tomcat ApplicationFilterChain fail.", e);
            }
        }
        if ("org.sqlite.JDBC".equals(cl.getName())) {
            try {
                CtMethod method = cl.getMethod("connect", "(Ljava/lang/String;Ljava/util/Properties;)Ljava/sql/Connection;");
                method.insertBefore(LOAD_SQLITE_WITH_SEQUENCE);
                pointcutClass = cl;
            } catch (Exception e) {
                logger.log(Level.WARNING, "Instrument tomcat JDBC driver fail.", e);
            }
        }
        CtMethod pointcutMethod = null;
        if ("org.apache.jasper.servlet.JspServlet".equals(cl.getName()) && isExtendedByHttpServlet(cl)) {
            pointcutMethod = findPointcut(cl);
        }

        if (pointcutMethod != null) instrumentSetvlet(pointcutMethod);

        List<CtMethod> jdbcPointcuts = new ArrayList<CtMethod>(9);

        if (cl.getName().equals("org.sqlite.jdbc3.JDBC3Statement")) {
            for (CtMethod method : cl.getMethods()) {
                MethodId methodId = null;
                if ((methodId = MethodId.findMethodId(method.getName(), method.getSignature())) != null) {
                    String classNameParsedFromMethod = method.getLongName().replaceFirst("\\.\\b" + method.getName() + "\\b\\(.*\\)", "");
                    if (!cl.getName().equals(classNameParsedFromMethod)) {
                        try {
                            CtMethod jdbcMethod = CtNewMethod.make(methodId.getShell(), cl);
                            cl.addMethod(jdbcMethod);
                        } catch (CannotCompileException e) {
                            logger.log(Level.WARNING, "Instrument JDBC interface failed.", e);
                        }
                    }
                    jdbcPointcuts.add(method);
                }
            }
        }


        for (CtMethod jdbcMethod : jdbcPointcuts) {
            instrumentJDBCPointcut(jdbcMethod);
        }

        byte[] transformed = null;
        try {
            transformed = cl.toBytecode();
        } catch (Exception e) {
            logger.log(Level.WARNING, "javassist class toBytecode failed.", e);
        }
        if(pointcutClass != null) {
            cl.debugWriteFile("javassist_tmp");
        }
        return transformed == null ? classfileBuffer : transformed;
    }

    private void instrumentJDBCPointcut(CtMethod jdbcMethod) {
        try {
            jdbcMethod.insertBefore(JDBC_RECORD);
            jdbcMethod.insertAfter(JDBC_RECORD);
        } catch (CannotCompileException e) {
            logger.log(Level.WARNING, "Modify JDBC interface method:" + jdbcMethod + "failed.", e);
        }
    }

    private void instrumentSetvlet(CtMethod pointcut) {
        try {
            pointcut.insertBefore(RECORD);
            pointcut.insertBefore(CLEAR);
            pointcut.insertAfter(RECORD);
            pointcut.insertAfter(AFTER_SERVICE);
        } catch (CannotCompileException e) {
            logger.log(Level.WARNING, "Modify Servlet servic method:" + pointcut + "failed.", e);
        }
    }

    private CtMethod findPointcut(CtClass cl) {
        CtMethod pointcut = null;
        CtMethod[] behaviors = cl.getDeclaredMethods();
        for (CtMethod behavior : behaviors) {
            if (POINTCUT_METHOD_NAME.equals(behavior.getName()) && POINTCUT_METHOD_SIGNATURE.equals(behavior.getSignature())) {
                pointcut = behavior;
            }
        }
        return pointcut;
    }

    private boolean isExtendedByHttpServlet(CtClass ctClass) {
        if (quickBreak(ctClass)) return false;
        if (POINTCUT_CLASS.equals(ctClass.getName())) return true;

        CtClass superclass = null;
        try {
            superclass = ctClass.getSuperclass();
        } catch (Exception e) {
            return false;
        }
        if (superclass == null) return false;
        return isExtendedByHttpServlet(superclass);
    }

    private boolean quickBreak(CtClass ctClass) {
        if (ctClass.isEnum()) return true;
        if (ctClass.isArray()) return true;
        if ("java.lang.Object".equals(ctClass.getName())) return true;
        return false;
    }
}
