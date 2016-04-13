package com.intuit.craftdemo;

import org.json.JSONArray;
import org.json.JSONObject;
import org.testng.Assert;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Created by karthi on 4/12/16.
 ** ResponseHandler will parse JSON String
 ** Gets Count variable from Json
 ** Gets Result Array from Json and Build array of Result Object
 */
public class ResponseHandler {

    public ResponseHandler(String jsonString) {
        initialize(jsonString);
    }

    public void initialize(String jsonString) {
        this.jsonString = jsonString;
        jsonObject = new JSONObject(this.jsonString);
    }

    // Get count from Json
    public int getCount() {
        return jsonObject.getInt("count");
    }

    // Get Results List from Json
    public JSONArray getResult() {
        return jsonObject.getJSONArray("results");
    }

    // Print Count and Elements in Results List
    public void printResult() {
        StringBuilder count = new StringBuilder("Count: ").append(getCount());
        LOGGER.log(Level.INFO, count.toString());
        getResultList();
        for ( Result result : resultList ) {
            LOGGER.log(Level.INFO, result.toString());
        }
    }

    // Get Results List
    public List<Result> getResultList() {
        if ( resultList == null ) {
            resultList = new ArrayList<Result>();
            prepareResultList();
        }
        return resultList;
    }

    // Build Result Object and Prepare Results List
    public void prepareResultList() {
        resultList.clear();
        for ( Object j : getResult() ) {
            resultList.add(
                    new Result.Builder(((JSONObject) j).getString("title"))
                            .description(((JSONObject) j).get("description"))
                            .license(((JSONObject) j).getString("license"))
                            .tag_list(((JSONObject) j).getString("tag_list"))
                            .download_url(((JSONObject) j).getString("download_url"))
                            .stream_url(((JSONObject) j).getString("stream_url"))
                            .last_modified(((JSONObject) j).getString("last_modified"))
                            .duration(((JSONObject) j).getInt("duration"))
                            .id(((JSONObject) j).getInt("id"))
                            .build()
            );
        }
    }

    /*
    **  Validate Content
    **  Json should have count and array of result - number of Json Elements = 2
    **  If Json has any other value other than count or results, test will fail
    **  If Count and Size of Result Array should be equal to Expected number of response
    **  Note:
    **    If Limit is not specified, then count and size of results array will be 10 (equals to default limit 10 - expected number is 10)
    **    If Limit is < 0, then count and size of results array will be maximum number of data (in this case expected number is 64)
    **    If Limit is > maximum number of data (64), then count and size of results array will be maximum number of data (in this case expected number is 64)
    **    If Limit is <= maximum number of data (64), then count and size of results array will be equal to limit (expected number = limit specified in URL)
     */
    public void validateResponse(int expectedCount) {
        if ( jsonObject.length() != 2 ) {
            LOGGER.log(Level.SEVERE, "JSON Object is expected to have 2 elements, but it's not equal to 2");
            Assert.fail("JSON Object is expected to have 2 elements, but it's not equal to 2");
        }
        for ( String key : jsonObject.keySet() ) {
            if ( ! ("count".contentEquals(key) || "results".contentEquals(key)) ) {
                LOGGER.log(Level.SEVERE, "JSON Object contains unexpected key " + key);
                Assert.fail("JSON Object contains unexpected key " + key);
            }
        }
        if (getCount() != expectedCount) {
            LOGGER.log(Level.SEVERE, "Count in response doesn't match with expected count");
            LOGGER.log(Level.SEVERE, "Actual Count: " + getCount() + ", Expected Count: " + expectedCount);
            Assert.fail("Count in response doesn't match with expected count");
        }
        if (getResultList().size() != expectedCount) {
            LOGGER.log(Level.SEVERE, "Number of response doesn't match with expected count");
            LOGGER.log(Level.SEVERE, "Actual Count: " + getResultList().size() + ", Expected Count: " + expectedCount);
            Assert.fail("Count in response doesn't match with expected count");
        } else {
            for ( Result result : getResultList() ) {
                if ( result.description == null )
                    LOGGER.log(Level.WARNING, "Description has null value");
                if ( result.id < 0 ) {
                    LOGGER.log(Level.SEVERE, "ID is a negative value");
                    org.testng.Assert.fail("ID is a negative value");
                }
                if ( result.duration < 0 ) {
                    LOGGER.log(Level.SEVERE, "Duration is a negative value");
                    Assert.fail("Duration is a negative value");
                }
            }
        }
    }

    List<Result> resultList;
    String jsonString;
    JSONObject jsonObject;
    static final Logger LOGGER = Logger.getLogger(Thread.currentThread().getStackTrace()[0].getClassName());

}
