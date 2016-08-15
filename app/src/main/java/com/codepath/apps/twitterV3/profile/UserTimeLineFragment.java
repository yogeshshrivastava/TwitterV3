package com.codepath.apps.twitterV3.profile;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.codepath.apps.twitterV3.app.TwitterV3Application;
import com.codepath.apps.twitterV3.models.Tweet;
import com.codepath.apps.twitterV3.rest.RestClient;
import com.codepath.apps.twitterV3.timeline.TimeLineFragment;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.loopj.android.http.JsonHttpResponseHandler;

import org.json.JSONArray;
import org.json.JSONObject;

import java.lang.reflect.Type;
import java.util.List;

import cz.msebera.android.httpclient.Header;

/**
 *
 * @author yvastavaus.
 */
public class UserTimeLineFragment extends TimeLineFragment {

    private static final String TAG = UserTimeLineFragment.class.getSimpleName();

    private static final int DEFAULT_SINCE = 1;
    private static final String EXTRA_SCREEN_NAME = TAG + ".EXTRA_SCREEN_NAME";
    private static final String EXTRA_IS_REFRESH = TAG + ".EXTRA_IS_REFRESH";

    private RestClient client;

    private boolean isRefreshEnabled;

    public static UserTimeLineFragment newInstance(String screenName, boolean isRefresh) {
        UserTimeLineFragment userTimeLineFragment = new UserTimeLineFragment();
        Bundle args = new Bundle();
        args.putString(EXTRA_SCREEN_NAME, screenName);
        args.putBoolean(EXTRA_IS_REFRESH, isRefresh);
        userTimeLineFragment.setArguments(args);
        return userTimeLineFragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        client = TwitterV3Application.getRestClient();

    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        isRefreshEnabled = getArguments().getBoolean(EXTRA_IS_REFRESH);
        return super.onCreateView(inflater, container, savedInstanceState);
    }


    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        if(!isRefreshEnabled) {
            swipeRefreshLayout.setEnabled(false);
        }
    }

    protected void populateTimeLine(long maxId) {
        String screenName = getArguments().getString(EXTRA_SCREEN_NAME);
        // This means this is the first request
        if(maxId == DEFAULT_SINCE) {
            client.getUserTimeLine(new JsonHttpResponseHandler() {
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
                }
            }, screenName, DEFAULT_SINCE);
        } else {
            // This is subsequent request
            client.getUserTimeLine(new JsonHttpResponseHandler() {
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
                }
            }, screenName, maxId);
        }
    }

    @Override
    protected boolean isRefreshEnabled() {
        return isRefreshEnabled;
    }
}
