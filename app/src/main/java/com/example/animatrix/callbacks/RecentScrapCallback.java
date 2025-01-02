package com.example.animatrix.callbacks;

import org.json.JSONArray;

public interface RecentScrapCallback {
    void onScrapeComplete(JSONArray results);
    void onScrapeFailed(String error);
}
