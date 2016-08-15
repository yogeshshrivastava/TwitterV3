package com.codepath.apps.twitterV3.timeline;

import android.os.Bundle;
import android.support.annotation.Nullable;

import com.codepath.apps.twitterV3.app.TwitterV3Application;
import com.codepath.apps.twitterV3.models.Tweet;
import com.codepath.apps.twitterV3.rest.RestClient;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.loopj.android.http.JsonHttpResponseHandler;

import org.json.JSONArray;
import org.json.JSONObject;

import java.lang.reflect.Type;
import java.util.List;

import cz.msebera.android.httpclient.Header;

/**
 * author yvastavaus.
 */
public class HomeTimeLineFragment extends TimeLineFragment {
    private static final String TAG = HomeTimeLineFragment.class.getSimpleName();

    private static final int DEFAULT_SINCE = 1;

    private RestClient client;

    public static HomeTimeLineFragment newInstance() {
        HomeTimeLineFragment homeTimeLineFragment = new HomeTimeLineFragment();
        return homeTimeLineFragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        client = TwitterV3Application.getRestClient();

    }

    protected void populateTimeLine(long maxId) {
        // This means this is the first request
        if(maxId == DEFAULT_SINCE) {
            client.getHomeTimeLine(new JsonHttpResponseHandler() {
                @Override
                public void onSuccess(int statusCode, Header[] headers, JSONArray response) {
                    Gson gson = new Gson();
                    Type listType = new TypeToken<List<Tweet>>(){}.getType();
                    // In this test code i just shove the JSON here as string.
                    List<Tweet> list = gson.fromJson(response.toString(), listType);
                    // Remove older tweets and ask for fresh list.
                    clearTweets();
                    addListToTimeLine(list);
                }

                @Override
                public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject jsonObject) {
                    resetPageValueToPrevious();
                    swipeRefreshLayout.setRefreshing(false);
                }
            }, DEFAULT_SINCE);
        } else {
            // This is subsequent request
            client.getHomeTimeLineMaxId(new JsonHttpResponseHandler() {
                @Override
                public void onSuccess(int statusCode, Header[] headers, JSONArray response) {
                    Gson gson = new Gson();
                    Type listType = new TypeToken<List<Tweet>>(){}.getType();
                    // In this test code i just shove the JSON here as string.
                    List<Tweet> list = gson.fromJson(response.toString(), listType);
                    addListToTimeLine(list);
                }

                @Override
                public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject jsonObject) {
                    resetPageValueToPrevious();
                    swipeRefreshLayout.setRefreshing(false);
                }
            }, maxId);
        }
    }

    @Override
    protected boolean isRefreshEnabled() {
        return true;
    }


}
