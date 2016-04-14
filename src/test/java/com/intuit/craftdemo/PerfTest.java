package com.intuit.craftdemo;

import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.testng.Assert;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;
import java.util.concurrent.*;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Created by karthi on 4/13/16.
 */
public class PerfTest {
    /*
    ** Initialize HttpClient
    */
    @BeforeClass(alwaysRun = true)
    public void setup() {
        httpClient = HttpClients.createDefault();
    }

    /*
    ** Close HttpClient and HttpAsyncClient
    */
    @AfterClass(alwaysRun = true)
    public void tearDown() {
        try {
            httpClient.close();
        } catch (IOException e) {
            LOGGER.log(Level.SEVERE, e.getLocalizedMessage(), e);
            Assert.fail(e.getLocalizedMessage(), e);
        }
    }

    /*
    ** Performance Test Simulation
    ** Total of 100 requests with 5 requests per second
    ** 3 URLs, which returns different size of payload
    ** Headers are not included for this demo
    ** CloseableHttpClient is used - Blocking call
     */
    @Test( groups = "Perf" )
    public void perfTest() {
        int totalRequests = 100;
        List<String> urls = new ArrayList<>();
        urls.add("https://api.nasa.gov/planetary/sounds?q=appollo&limit=1&api_key=DEMO_KEY");
        urls.add("https://api.nasa.gov/planetary/sounds?q=appollo&limit=5&api_key=DEMO_KEY");
        urls.add("https://api.nasa.gov/planetary/sounds?q=appollo&api_key=DEMO_KEY");

        if ( urls.size() > 0 ) {
            ExecutorService es = Executors.newFixedThreadPool(5); // 5 concurrent threads
            List<Future<Counters>> futureList = new ArrayList<>();

            // Compute Iteration
            // Example:
            //      10 requests / 11 URLs: (0 Iteration) + 10 requests
            //      11 requests / 10 URLs: (1 Iteration * 10 URLs) + 10 requests
            //      10 requests / 10 URLs: (1 Iteration * 10 URLs)
            //      10 requests / 3 URLs: (3 Iteration * 3 URLs) + 1 request
            int numIteration = totalRequests / urls.size();
            int reminderRequest = totalRequests % urls.size();
            for ( int i = 0 ; i < numIteration ; i++ ) {
                futureList.add(es.submit(new PerfThread(urls, httpClient)));
            }
            if (reminderRequest > 0)
                futureList.add(es.submit(new PerfThread(urls.subList(0, reminderRequest), httpClient)));

            try {
                for (Future<Counters> future : futureList) {
                    Counters c = future.get();
                    LOGGER.log(Level.INFO, "Total Requests Completed: " + c.numRequests + ", NumErrors: " + c.error + ", AvgLatency: " + average(c.elapsedTime));
                }
            } catch (ExecutionException | InterruptedException e) {
                e.printStackTrace();
            }
            es.shutdown();
        }
    }

    private double average(List<Long> values) {
        if ( values == null || values.isEmpty() )
            return 0.0;
        long sum = 0;
        for ( Long v : values )
            sum += v.intValue();
        return (1.0 * sum) / values.size();
    }

    static final Logger LOGGER = Logger.getLogger(Thread.currentThread().getStackTrace()[0].getClassName());
    CloseableHttpClient httpClient;
}

// Counter class to keep track of number of requests, number of errors and latencies per thread
class Counters {
    public Counters() {
        numRequests = 0;
        elapsedTime = new ArrayList<>();
        error = 0;
        response = new Hashtable<String, Object>();
    }

    public int numRequests;
    public List<Long> elapsedTime;
    public int error;
    public Map<String, Object> response;
}

// Performance Worked implements Callable, which can return a Future Object <Counters>
class PerfThread implements Callable<Counters> {
    public PerfThread(List<String> urls, CloseableHttpClient httpClient) {
        this.httpClient = httpClient;
        this.urls = urls;
    }

    // Callable Thread
    @Override
    public Counters call() {
        Counters counters = new Counters();
        for ( String url : urls ) {
            doGet(url, counters);
        }
        return counters;
    }

    // Do Http Get
    private void doGet(String url, Counters counters) {
        HttpGet method = new HttpGet(url);
        long startTime = System.currentTimeMillis();
        try (CloseableHttpResponse httpResponse = httpClient.execute(method)) {
            handleResponse(httpResponse, counters);
        } catch ( IOException e) {
            e.printStackTrace();
        } finally {
            counters.elapsedTime.add(System.currentTimeMillis() - startTime);
        }
    }

    // Record response in Counters Object
    private static void handleResponse(CloseableHttpResponse httpResponse, Counters counters) throws IOException {
        counters.response.put("responseCode", httpResponse.getStatusLine().getStatusCode());
        counters.response.put("responseMessage", httpResponse.getStatusLine().getReasonPhrase());
        if (httpResponse.getEntity() != null)
            counters.response.put("responseBody", EntityUtils.toString(httpResponse.getEntity()));
        counters.response.put("responseHeaders", httpResponse.getAllHeaders());
        if (httpResponse.getStatusLine().getStatusCode() != 200)
            counters.error++;
        counters.numRequests++;
    }

    List<String> urls;
    CloseableHttpClient httpClient;
}