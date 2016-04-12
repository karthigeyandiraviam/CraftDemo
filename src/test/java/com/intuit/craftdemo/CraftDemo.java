package com.intuit.craftdemo;

import com.google.common.base.Charsets;
import org.apache.commons.httpclient.HttpClient;
import org.testng.Assert;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Created by karthi on 4/11/16.
 */
public class CraftDemo {
    @BeforeClass(alwaysRun = true)
    public void setup() {
        httpClient = new HttpClient();
    }

    @AfterClass(alwaysRun = true)
    public void tearDown() {

    }

    @DataProvider (name = "queryParamPositive")
    public Iterator<Object[]> queryParamPositive() {
        List<Object[]> data = new ArrayList<Object[]>();
        data.add(new Object[]{"https://api.nasa.gov/planetary/sounds?q=appollo&api_key=DEMO_KEY"});
        return data.iterator();
    }

    @Test( groups = "smoke", testName = "GET API Call", dataProvider = "queryParamPositive" )
    public void testGet( String url ) {
        try {
            Map<String, Object> response = new Hashtable<String, Object>();
            ClientMethods.doGet(httpClient, url, response);
            JsonHandler jsonHandler = new JsonHandler(new String((byte[])(response.get("responseBody")), Charsets.UTF_8));
            jsonHandler.printResult();
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, e.getLocalizedMessage(), e);
            Assert.fail(e.getLocalizedMessage(), e);
        }
    }

    static final Logger LOGGER = Logger.getLogger(Thread.currentThread().getStackTrace()[0].getClassName());
    HttpClient httpClient;
}
