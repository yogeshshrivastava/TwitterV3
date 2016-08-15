package com.codepath.apps.twitterV3.mentions;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.codepath.apps.twitterV3.R;
import com.codepath.apps.twitterV3.app.TwitterV3Application;
import com.codepath.apps.twitterV3.models.Tweet;
import com.codepath.apps.twitterV3.rest.RestClient;
import com.codepath.apps.twitterV3.timeline.EndlessRecyclerViewScrollListener;
import com.codepath.apps.twitterV3.timeline.TweetAdapter;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.loopj.android.http.JsonHttpResponseHandler;

import org.json.JSONArray;
import org.json.JSONObject;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import cz.msebera.android.httpclient.Header;

/**
 * author yvastavaus.
 */
public class MentionsFragment extends Fragment {
    private static final String  TAG = MentionsFragment.class.getSimpleName();

    private static final String EXTRA_TWEET_LIST = TAG + ".EXTRA_TWEET_LIST";

    private static final int DEFAULT_SINCE = 1;

    @BindView(R.id.rvMentions)
    RecyclerView rvTimeLine;

    @BindView(R.id.swipeRefreshLayout)
    SwipeRefreshLayout swipeRefreshLayout;

    private RestClient client;

    private TweetAdapter mTweetAdapter;

    private EndlessRecyclerViewScrollListener scrollListener;

    private List<Tweet> mTweetList;

    private int currentPage;

    public static MentionsFragment newInstance() {
        MentionsFragment mentionsFragment = new MentionsFragment();
        return mentionsFragment;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_mentions, container, false);
        ButterKnife.bind(this, view);
        if(savedInstanceState != null) {
            mTweetList = savedInstanceState.getParcelableArrayList(EXTRA_TWEET_LIST);
        }
        init();
        if(mTweetList == null || mTweetList.size() == 0) {
            populateHomeTimeLine(DEFAULT_SINCE);
        }
        return view;
    }

    private void init() {
        client = TwitterV3Application.getRestClient();
        if(mTweetList == null) {
            mTweetList = new ArrayList<>();
        }
        mTweetAdapter = new TweetAdapter(getActivity(), mTweetList);
        rvTimeLine.setAdapter(mTweetAdapter);
        LinearLayoutManager layoutManager = new LinearLayoutManager(getContext());
        rvTimeLine.setLayoutManager(layoutManager);
        scrollListener = new EndlessRecyclerViewScrollListener(layoutManager) {
            @Override
            public void onLoadMore(int page, int totalItemsCount) {
                // Set the current page.
                currentPage = page;
                loadMoreDataFromApi();
            }
        };
        rvTimeLine.addOnScrollListener(scrollListener);
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                populateHomeTimeLine(DEFAULT_SINCE);
            }
        });
    }

    private void loadMoreDataFromApi() {
        if(mTweetList != null && mTweetList.size() > 1) {
            populateHomeTimeLine(mTweetList.get(mTweetList.size() - 1).getUuid());
        }
    }

    private void populateHomeTimeLine(long maxId) {
        // This means this is the first request
        if(maxId == DEFAULT_SINCE) {
            client.getMentions(new JsonHttpResponseHandler() {
                @Override
                public void onSuccess(int statusCode, Header[] headers, JSONArray response) {
                    Gson gson = new Gson();
                    Type listType = new TypeToken<List<Tweet>>(){}.getType();
                    // In this test code i just shove the JSON here as string.
                    List<Tweet> list = gson.fromJson(response.toString(), listType);
                    // Remove older tweets and ask for fresh list.
                    clearTweets();
                    populateTimeLine(list);
                }

                @Override
                public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject jsonObject) {
                    resetPageValueToPrevious();
                    swipeRefreshLayout.setRefreshing(false);
                }
            }, DEFAULT_SINCE);
        } else {
            // This is subsequent request
            client.getMentionsMaxId(new JsonHttpResponseHandler() {
                @Override
                public void onSuccess(int statusCode, Header[] headers, JSONArray response) {
                    Gson gson = new Gson();
                    Type listType = new TypeToken<List<Tweet>>(){}.getType();
                    // In this test code i just shove the JSON here as string.
                    List<Tweet> list = gson.fromJson(response.toString(), listType);
                    populateTimeLine(list);
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
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putParcelableArrayList(EXTRA_TWEET_LIST, (ArrayList<Tweet>)mTweetList);
    }

    private void clearTweets() {
        mTweetList.clear();
    }

    private void populateTimeLine(List<Tweet> list) {
        mTweetList.addAll(list);
        mTweetAdapter.notifyDataSetChanged();
        swipeRefreshLayout.setRefreshing(false);
    }

    /**
     * If request fails then function to revert to the previous page.
     */
    private void resetPageValueToPrevious() {
        if(currentPage > 0) {
            currentPage = currentPage - 1;
            scrollListener.setCurrentPage(currentPage);
        }
    }
}
