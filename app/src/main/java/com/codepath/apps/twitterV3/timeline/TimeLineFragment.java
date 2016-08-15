package com.codepath.apps.twitterV3.timeline;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.codepath.apps.twitterV3.R;
import com.codepath.apps.twitterV3.models.Tweet;
import com.codepath.apps.twitterV3.models.User;
import com.codepath.apps.twitterV3.profile.ProfileActivity;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * author yvastavaus.
 */
abstract public class TimeLineFragment extends Fragment implements TweetAdapter.OnItemClickListener, TweetAdapter.OnProfileImageClickListener {

    private static final String TAG = TimeLineFragment.class.getSimpleName();
    private static final String EXTRA_TWEET_LIST = TAG + ".EXTRA_TWEET_LIST";

    private static final int DEFAULT_SINCE = 1;

    @BindView(R.id.rvTimeline)
    public RecyclerView rvTimeLine;

    @BindView(R.id.swipeRefreshLayout)
    public SwipeRefreshLayout swipeRefreshLayout;

    private TweetAdapter mTweetAdapter;

    private EndlessRecyclerViewScrollListener scrollListener;

    private List<Tweet> mTweetList;

    private int currentPage;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_time_line, container, false);
        ButterKnife.bind(this, view);
        if(savedInstanceState != null) {
            mTweetList = savedInstanceState.getParcelableArrayList(EXTRA_TWEET_LIST);
        }
        init();
        if(mTweetList == null || mTweetList.size() == 0) {
            populateTimeLine(DEFAULT_SINCE);
        }
        return view;
    }

    private void init() {
        if(mTweetList == null) {
            mTweetList = new ArrayList<>();
        }
        mTweetAdapter = new TweetAdapter(getActivity(), mTweetList);
        mTweetAdapter.setOnItemClickListener(this);
        mTweetAdapter.setOnProfileImageClickListener(this);
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
        if(isRefreshEnabled()) {
            swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
                @Override
                public void onRefresh() {
                    populateTimeLine(DEFAULT_SINCE);
                }
            });
        }
    }

    private void loadMoreDataFromApi() {
        if(mTweetList != null && mTweetList.size() > 1) {
            populateTimeLine(mTweetList.get(mTweetList.size() - 1).getUuid());
        }
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putParcelableArrayList(EXTRA_TWEET_LIST, (ArrayList<Tweet>)mTweetList);
    }

    protected void clearTweets() {
        mTweetList.clear();
    }

    protected void addListToTimeLine(List<Tweet> list) {
        mTweetList.addAll(list);
        mTweetAdapter.notifyDataSetChanged();
        if(isRefreshEnabled()) {
            swipeRefreshLayout.setRefreshing(false);
        }
    }

    /**
     * If request fails then function to revert to the previous page.
     */
    protected void resetPageValueToPrevious() {
        if(currentPage > 0) {
            currentPage = currentPage - 1;
            scrollListener.setCurrentPage(currentPage);
        }
    }

    @Override
    public void onItemClicked(int pos) {

    }

    @Override
    public void onProfileImageItemClicked(int pos) {
        Tweet tweet = mTweetList.get(pos);
        launchProfile(tweet.getUser());
    }

    public void launchProfile(User user) {
        Intent intent = new Intent(getActivity(), ProfileActivity.class);
        intent.putExtra(ProfileActivity.EXTRA_USER_ID, user.getId());
        intent.putExtra(ProfileActivity.EXTRA_SCREEN_NAME, user.getScreenName());
        ActivityCompat.startActivity(getActivity(), intent, null);
    }

    public void refreshTimeLine() {
        populateTimeLine(DEFAULT_SINCE);
    }

    abstract protected void populateTimeLine(long maxId);

    abstract protected boolean isRefreshEnabled();
}
