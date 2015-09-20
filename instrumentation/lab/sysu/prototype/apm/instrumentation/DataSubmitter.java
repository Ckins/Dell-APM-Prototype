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

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import org.apache.http.ConnectionReuseStrategy;
import org.apache.http.HttpHost;
import org.apache.http.HttpResponse;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.DefaultBHttpClientConnection;
import org.apache.http.impl.DefaultConnectionReuseStrategy;
import org.apache.http.message.BasicHttpEntityEnclosingRequest;
import org.apache.http.protocol.*;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.lang.reflect.Modifier;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;
import java.util.logging.Level;
import java.util.logging.Logger;

import lab.sysu.prototype.apm.model.Request;
import lab.sysu.prototype.apm.model.javaee.JVM;

public class DataSubmitter {

    private static ScheduledExecutorService scheduledExecutorService = Executors.newSingleThreadScheduledExecutor(Executors.defaultThreadFactory());
    private static List<Request> requests = new ArrayList<Request>();
    private static Lock mutex = new ReentrantLock();
    private static String serverAddr = "/apm/appData";
    private static HttpRequestExecutor httpExecutor = new HttpRequestExecutor();
    private static HttpProcessor httpProc = HttpProcessorBuilder.create()
            .add(new RequestContent())
            .add(new RequestTargetHost())
            .add(new RequestConnControl())
            .add(new RequestUserAgent("Test/1.1"))
            .add(new RequestExpectContinue(true)).build();
    private static HttpCoreContext coreContext = HttpCoreContext.create();
    private static DefaultBHttpClientConnection conn = new DefaultBHttpClientConnection(8 * 1024);
    private static ConnectionReuseStrategy connStrategy = DefaultConnectionReuseStrategy.INSTANCE;
    private static HttpHost host = new HttpHost("localhost", 8080);
    private static Logger logger = Logger.getLogger("lab.sysu.prototype.apm.instrumentation.DataSubmitter");

    private static Runnable conveyor = new Runnable() {
        @Override
        public void run() {
            try {
                JVMCollector.collect();
                mutex.lock();
                List<Request> requestsTmp = null;
                try {
                    requestsTmp = requests;
                    requests = new ArrayList<Request>();
                } finally {
                    mutex.unlock();
                }

                if (requestsTmp.size() == 0) return;
                Gson gson = new GsonBuilder()
                        .excludeFieldsWithModifiers(Modifier.TRANSIENT)
                        .create();
                String collectionResult = "";

                JVM jvm = null;
                for (Request request : requestsTmp) {
                    String requestJson = gson.toJson(request);
                    if (requestJson == null || "".equals(requestJson)) continue;
                    collectionResult = collectionResult + ";;" + requestJson;
                }
                jvm = JVMCollector.collect();

                String jvmJson = gson.toJson(jvm);
                collectionResult = collectionResult + ";;" + jvmJson;
                logger.log(Level.INFO, "Inspect submitted data: " + collectionResult);

                StringEntity resultEntity = null;
                try {
                    resultEntity = new StringEntity(collectionResult);
                } catch (UnsupportedEncodingException e) {
                    logger.log(Level.WARNING, "Build HttpEntity failed.", e);
                }
                BasicHttpEntityEnclosingRequest request = new BasicHttpEntityEnclosingRequest("POST", serverAddr);
                try {
                    if (!conn.isOpen()) {
                        Socket socket = null;
                        socket = new Socket(host.getHostName(), host.getPort());
                        conn.bind(socket);
                    }
                    request.setEntity(resultEntity);
                    HttpResponse response = null;
                    httpExecutor.preProcess(request, httpProc, coreContext);
                    response = httpExecutor.execute(request, conn, coreContext);
                    httpExecutor.postProcess(response, httpProc, coreContext);
                    if (!connStrategy.keepAlive(response, coreContext)) {
                        conn.close();
                    } else {
                        logger.log(Level.INFO, "Connection kept alive...");
                    }
                } finally {
                    try {
                        conn.close();
                    } catch (IOException e) {
                    }
                }
            } catch (Exception e) {
                logger.log(Level.WARNING, "Submit data occurred exception.", e);
            }
        }
    };

    static {
        coreContext.setTargetHost(host);
        scheduledExecutorService.scheduleWithFixedDelay(conveyor, 1000, 30000, TimeUnit.MILLISECONDS);
    }

    public static void submit(Request request) {
        mutex.lock();
        try {
            requests.add(request);
        } finally {
            mutex.unlock();
        }
    }


}
