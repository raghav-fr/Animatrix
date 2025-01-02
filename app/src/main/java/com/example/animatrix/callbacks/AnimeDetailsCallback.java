package com.example.animatrix.callbacks;

import org.json.JSONArray;
import org.json.JSONObject;

public interface AnimeDetailsCallback {
    void onScrapeingComplete(JSONObject results);
    void onScrapeingFailed(String error);
}
