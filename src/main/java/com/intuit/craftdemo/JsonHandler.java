package com.intuit.craftdemo;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Created by karthi on 4/12/16.
 */
public class JsonHandler {

    public JsonHandler() {
        this.jsonString = null;
        jsonObject = null;
        resultList = null;
    }

    public JsonHandler(String jsonString) {
        initialize(jsonString);
    }

    public void initialize(String jsonString) {
        this.jsonString = jsonString;
        jsonObject = new JSONObject(this.jsonString);
    }

    public int getCount() {
        return jsonObject.getInt("count");
    }

    public JSONArray getResult() {
        return jsonObject.getJSONArray("results");
    }

    public void printResult() {
        getResultList();
        for ( Result result : resultList ) {
            LOGGER.log(Level.INFO, result.toString());
        }
    }

    public List<Result> getResultList() {
        if ( resultList == null ) {
            resultList = new ArrayList<Result>();
            prepareResultList();
        }
        return resultList;
    }

    private void prepareResultList() {
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

    List<Result> resultList;
    String jsonString;
    JSONObject jsonObject;
    static final Logger LOGGER = Logger.getLogger(Thread.currentThread().getStackTrace()[0].getClassName());

}
