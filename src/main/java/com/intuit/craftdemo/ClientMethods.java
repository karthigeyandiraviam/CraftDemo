package com.intuit.craftdemo;

import org.apache.http.Header;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpResponseException;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.nio.client.CloseableHttpAsyncClient;
import org.apache.http.util.EntityUtils;

import java.io.IOException;
import java.util.Map;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.logging.Level;
import java.util.logging.Logger;
/**
 * Created by karthi on 4/11/16.
 */
public class ClientMethods {
    public static void doGet(CloseableHttpClient httpClient, String url, Map<String, Object> response) throws IOException, InterruptedException, ExecutionException {
        LOGGER.log(Level.FINEST, "********************** GET DATA (HttpAsyncClient) **********************");
        LOGGER.log(Level.FINEST, "URL: " + url);
        HttpGet method = new HttpGet(url);
        long startTime = System.currentTimeMillis();
        CloseableHttpResponse httpResponse = httpClient.execute(method);
        long estimatedTime = System.currentTimeMillis() - startTime;
        LOGGER.log(Level.FINEST, "[PERF]: CloseableHttpClient :: executeMethod took " + estimatedTime + " ms");
        try {
            handleResponse(httpResponse, response);
        } finally {
            httpResponse.close();
        }
    }

    public static void doPost(CloseableHttpClient httpClient, String url, String postBody, Map<String, Object> response) throws IOException, InterruptedException, ExecutionException {
        LOGGER.log(Level.FINEST, "********************** POST DATA (HttpAsyncClient) **********************");
        LOGGER.log(Level.FINEST, "URL: " + url);
        HttpPost method = new HttpPost(url);
        method.setEntity(new StringEntity(postBody));
        long startTime = System.currentTimeMillis();
        CloseableHttpResponse httpResponse = httpClient.execute(method);
        long estimatedTime = System.currentTimeMillis() - startTime;
        LOGGER.log(Level.FINEST, "[PERF]: CloseableHttpClient :: executeMethod took " + estimatedTime + " ms");
        try {
            handleResponse(httpResponse, response);
        } finally {
            httpResponse.close();
        }
    }

    public static void doGet(CloseableHttpAsyncClient httpClient, String url, Map<String, Object> response) throws IOException, InterruptedException, ExecutionException {
        LOGGER.log(Level.FINEST, "********************** GET DATA (HttpAsyncClient) **********************");
        LOGGER.log(Level.FINEST, "URL: " + url);
        HttpGet method = new HttpGet(url);

        // Get Async call
        long startTime = System.currentTimeMillis();
        Future<HttpResponse> future = httpClient.execute(method, null);
        HttpResponse httpResponse = future.get();
        long estimatedTime = System.currentTimeMillis() - startTime;
        LOGGER.log(Level.FINEST, "[PERF]: CloseableHttpAsyncClient :: executeMethod took " + estimatedTime + " ms");

        handleResponse(httpResponse, response);
    }

    private static void handleResponse(CloseableHttpResponse httpResponse, Map<String, Object> response) throws IOException {
        response.put("responseCode", httpResponse.getStatusLine().getStatusCode());
        response.put("responseMessage", httpResponse.getStatusLine().getReasonPhrase());
        if (httpResponse.getEntity() != null)
            response.put("responseBody", EntityUtils.toString(httpResponse.getEntity()));
        response.put("responseHeaders", httpResponse.getAllHeaders());
        printResponse(response);
    }

    private static void handleResponse(HttpResponse httpResponse, Map<String, Object> response) throws IOException {
        response.put("responseCode", httpResponse.getStatusLine().getStatusCode());
        response.put("responseMessage", httpResponse.getStatusLine().getReasonPhrase());
        if (httpResponse.getEntity() != null)
            response.put("responseBody", EntityUtils.toString(httpResponse.getEntity()));
        response.put("responseHeaders", httpResponse.getAllHeaders());
        printResponse(response);
    }

    private static void printResponse(Map<String, Object> response) throws IOException {
        LOGGER.log(Level.FINEST, "HttpGet " + response.get("responseCode") + " / " + response.get("responseMessage"));
        if ( response.containsKey("responseBody") )
            LOGGER.log(Level.FINEST, "ResponseBody:: " + response.get("responseBody").toString());

        for (Header header : (Header[])response.get("responseHeaders") ) {
            LOGGER.log(Level.FINEST, "ResponseHeaders:: " + header.toString());
        }
        if(((Integer) response.get("responseCode")).intValue() != 200 & ((Integer) response.get("responseCode")).intValue() != 201 & ((Integer) response.get("responseCode")).intValue() != 202) {
            throw new HttpResponseException(((Integer) response.get("responseCode")).intValue(), "Response Message:: " + response.get("responseMessage"));
        }
    }

    static final Logger LOGGER = Logger.getLogger(Thread.currentThread().getStackTrace()[0].getClassName());
}
