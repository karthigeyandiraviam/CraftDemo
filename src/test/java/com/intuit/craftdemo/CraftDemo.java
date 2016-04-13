package com.intuit.craftdemo;

import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.impl.nio.client.CloseableHttpAsyncClient;
import org.apache.http.impl.nio.client.HttpAsyncClients;
import org.testng.Assert;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import java.io.IOException;
import java.util.*;
import java.util.concurrent.ExecutionException;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Created by karthi on 4/11/16.
 */
public class CraftDemo {
    /*
    ** Initialize HttpClient and HttpAsyncClient
     */
    @BeforeClass(alwaysRun = true)
    public void setup() {
        httpClient = HttpClients.createDefault();
        httpAsyncClient = HttpAsyncClients.createDefault();
        httpAsyncClient.start();
    }

    /*
    ** Close HttpClient and HttpAsyncClient
     */
    @AfterClass(alwaysRun = true)
    public void tearDown() {
        try {
            httpClient.close();
            httpAsyncClient.close();
        } catch (IOException e) {
            LOGGER.log(Level.SEVERE, e.getLocalizedMessage(), e);
            Assert.fail(e.getLocalizedMessage(), e);
        }
    }

    /*
    ** Data Provider for all Positive Test Cases
     */
    @DataProvider (name = "queryParamPositive")
    public Iterator<Object[]> queryParamPositive() {
        List<Object[]> data = new ArrayList<Object[]>();
        data.add(new Object[]{"https://api.nasa.gov/planetary/sounds?q=appollo&limit=1&api_key=DEMO_KEY", 1});
        data.add(new Object[]{"https://api.nasa.gov/planetary/sounds?q=appollo&limit=5&api_key=DEMO_KEY", 5});
        data.add(new Object[]{"https://api.nasa.gov/planetary/sounds?q=appollo&api_key=DEMO_KEY", 10});
        data.add(new Object[]{"https://api.nasa.gov/planetary/sounds?q=appollo&limit=64&api_key=DEMO_KEY", 64});
        data.add(new Object[]{"https://api.nasa.gov/planetary/sounds?q=appollo&limit=100000&api_key=DEMO_KEY", 64});
        data.add(new Object[]{"https://api.nasa.gov/planetary/sounds?q=appollo&limit=0&api_key=DEMO_KEY", 0});
        data.add(new Object[]{"https://api.nasa.gov/planetary/sounds?limit=20&api_key=DEMO_KEY", 20});
        data.add(new Object[]{"https://api.nasa.gov/planetary/sounds?api_key=DEMO_KEY", 10});

        return data.iterator();
    }

    /*
    ** Test to do GET using HttpClient (Synchronous call)
     */
    @Test( groups = "smoke", testName = "GET API Call", dataProvider = "queryParamPositive" )
    public void testGet( String url, int expectedCount ) {
        try {
            Map<String, Object> response = new Hashtable<String, Object>();
            ClientMethods.doGet(httpClient, url, response);
            processResponse(response, expectedCount);
        } catch (IOException | InterruptedException | ExecutionException e ) {
            LOGGER.log(Level.SEVERE, e.getLocalizedMessage(), e);
            Assert.fail(e.getLocalizedMessage(), e);
        }
    }

    /*
    ** Test to do GET using HttpAsyncClient (Asynchronous call)
     */
    @Test( groups = "functional", testName = "GET API Call Asynchronously", dataProvider = "queryParamPositive" )
    public void testGetAsync( String url, int expectedCount ) {
        Map<String, Object> response = new Hashtable<String, Object>();
        try {
            ClientMethods.doGet(httpAsyncClient, url, response);
            processResponse(response, expectedCount);
        } catch (IOException | InterruptedException | ExecutionException e ) {
            LOGGER.log(Level.SEVERE, e.getLocalizedMessage(), e);
            Assert.fail(e.getLocalizedMessage(), e);
        }
    }

    /*
    ** Test to do POST using HttpClient (Synchronous call) - Make sure POST is not supported
    ** No need to iterate through different URLs
     */
    @Test( groups = "functional", testName = "POST API Call" )
    public void testPost( ) {
        Map<String, Object> getResponse = new Hashtable<String, Object>();
        Map<String, Object> postResponse = new Hashtable<String, Object>();
        try {
            ClientMethods.doGet(httpClient, "https://api.nasa.gov/planetary/sounds?q=appollo&limit=1&api_key=DEMO_KEY", getResponse);
            ClientMethods.doPost(httpClient, "https://api.nasa.gov/planetary/sounds?q=appollo&limit=1&api_key=DEMO_KEY", getResponse.get("responseBody").toString(), postResponse);
            LOGGER.log(Level.SEVERE, "Expecting An Exception as POST is not Supported Method, But Test Case Passed. Test is Failed.");
            Assert.fail("Expecting An Exception as POST is not Supported Method, But Test Case Passed. Test is Failed.");
        } catch (IOException | InterruptedException | ExecutionException e ) {
            int actualErrorCode = ((Integer) postResponse.get("responseCode")).intValue();
            if ( actualErrorCode == 405)
                LOGGER.log(Level.INFO, "Caught an Expected Exception. Test is Passed.");
            else {
                LOGGER.log(Level.SEVERE, "Test was expecting 405 but received " + actualErrorCode);
                Assert.fail("Test was expecting 405 but received " + actualErrorCode + ". Test is Failed.", e);
            }
        }
    }

    /*
    ** Data Provider for all Negavtive Test Cases
     */
    @DataProvider (name = "queryParamNegative")
    public Iterator<Object[]> queryParamNegative() {
        List<Object[]> data = new ArrayList<Object[]>();
        data.add(new Object[]{"https://api.nasa.gov/planetary/sounds", 403});
        data.add(new Object[]{"https://api.nasa.gov/planetary/sounds?q=appollo&limit=1", 403});
        data.add(new Object[]{"https://api.nasa.gov/planetary/sounds?q=appollo&limit=1&api_key=karthi", 403});
        data.add(new Object[]{"https://api.nasa.gov/planetary/sounds?q=appollo&limit=1&api_key=", 403});
        data.add(new Object[]{"https://api.nasa.gov/planetary/sounds?q=appollo&limit=karthi&api_key=DEMO_KEY", 500});
        // Negative value for limit is expected to fail but call is succeeding - This is a bug
        // data.add(new Object[]{"https://api.nasa.gov/planetary/sounds?q=appollo&limit=-1&api_key=DEMO_KEY", 500});

        // If Search query is not matching any results, API is returning all data - This is a bug
        // data.add(new Object[]{"https://api.nasa.gov/planetary/sounds?q=karthi&limit=100&api_key=DEMO_KEY", 500});
        data.add(new Object[]{"https://api.nasa.gov/planets/sounds?q=appollo&limit=1&api_key=DEMO_KEY", 404});
        data.add(new Object[]{"https://api.nasa.gov/planetary/lights?q=appollo&limit=1&api_key=DEMO_KEY", 404});
        return data.iterator();
    }

    /*
    ** Negative Test to do GET using HttpClient (Synchronous call)
    ** Validate with expected HTTP Return Code
     */
    @Test( groups = "functional", testName = "GET API Call", dataProvider = "queryParamNegative" )
    public void testGetNegative( String url, int expectedErrorCode ) {
        Map<String, Object> response = new Hashtable<String, Object>();
        try {
            ClientMethods.doGet(httpClient, url, response);
            LOGGER.log(Level.SEVERE, "Expecting An Exception, But Test Case Passed. Test is Failed.");
            Assert.fail("Expecting An Exception, But Test Case Passed. Test is Failed.");
        } catch (IOException | InterruptedException | ExecutionException e ) {
            int actualErrorCode = ((Integer) response.get("responseCode")).intValue();
            if ( actualErrorCode == expectedErrorCode)
                LOGGER.log(Level.INFO, "Caught an Expected Exception. Test is Passed.");
            else {
                LOGGER.log(Level.SEVERE, "Test was expecting " + expectedErrorCode + " but received " + actualErrorCode);
                Assert.fail("Test was expecting " + expectedErrorCode + " but received " + actualErrorCode + ". Test is Failed.", e);
            }
        }
    }

    /*
    ** Validate Count and Number of Result Objects
    ** Validate Values in Result Object
     */
    private void processResponse(Map<String, Object> response, int expectedCount) {
        ResponseHandler responseHandler = new ResponseHandler(response.get("responseBody").toString());
        responseHandler.printResult();
        responseHandler.validateResponse(expectedCount);
    }

    static final Logger LOGGER = Logger.getLogger(Thread.currentThread().getStackTrace()[0].getClassName());
    CloseableHttpClient httpClient;
    CloseableHttpAsyncClient httpAsyncClient;
}
