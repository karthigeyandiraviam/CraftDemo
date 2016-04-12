package com.intuit.craftdemo;

import com.google.common.base.Charsets;
import org.apache.commons.httpclient.Header;
import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpMethod;
import org.apache.commons.httpclient.methods.GetMethod;

import java.io.IOException;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
/**
 * Created by karthi on 4/11/16.
 */
public class ClientMethods {
    public static void doGet(HttpClient httpClient, String url, Map<String, Object> response) throws Exception {
        LOGGER.log(Level.FINEST, "********************** GET USER DATA **********************");
        LOGGER.log(Level.FINEST, "URL: " + url);
        HttpMethod method = new GetMethod(url);
        long startTime = System.currentTimeMillis();
        httpClient.executeMethod(method);
        long estimatedTime = System.currentTimeMillis() - startTime;
        LOGGER.log(Level.FINEST, "[PERF]: HttpClient :: executeMethod took " + estimatedTime + " ms");
        LOGGER.log(Level.FINEST, "HttpGet " + method.getStatusCode() + " / " + method.getStatusText());
        if ( method.getResponseBody() != null )
            LOGGER.log(Level.FINEST, "ResponseBody:: " + new String((byte[])(method.getResponseBody()), Charsets.UTF_8));

        for ( Header header : method.getResponseHeaders() ) {
            LOGGER.log(Level.FINEST, "ResponseHeaders:: " + header.toString());
        }

        response.put("responseCode", method.getStatusCode());
        response.put("responseMessage", method.getStatusText());
        if ( method.getResponseBody() != null )
            response.put("responseBody", method.getResponseBody());
        response.put("responseHeaders", method.getResponseHeaders());
        method.releaseConnection();


        if(((Integer) response.get("responseCode")).intValue() != 200 & ((Integer) response.get("responseCode")).intValue() != 201 & ((Integer) response.get("responseCode")).intValue() != 202) {
            throw new Exception("Response Code:: " + response.get("responseCode") +
                    ", Response Message:: " + response.get("responseMessage"));
        }
    }

    static final Logger LOGGER = Logger.getLogger(Thread.currentThread().getStackTrace()[0].getClassName());
}
