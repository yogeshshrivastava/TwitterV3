package com.codepath.apps.twitterV3.profile;

import android.os.Bundle;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.widget.ImageView;
import android.widget.TextView;

import com.codepath.apps.twitterV3.R;
import com.codepath.apps.twitterV3.app.TwitterV3Application;
import com.codepath.apps.twitterV3.models.User;
import com.codepath.apps.twitterV3.rest.RestClient;
import com.google.gson.Gson;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.squareup.picasso.Picasso;

import org.json.JSONObject;

import butterknife.BindView;
import butterknife.ButterKnife;
import cz.msebera.android.httpclient.Header;
import jp.wasabeef.picasso.transformations.RoundedCornersTransformation;

public class ProfileActivity extends AppCompatActivity {

    private static final String TAG = ProfileActivity.class.getSimpleName();

    public static final String EXTRA_SCREEN_NAME = TAG + ".EXTRA_SCREEN_NAME";
    public static final String EXTRA_USER_ID = TAG + ".EXTRA_USER_ID";

    @BindView(R.id.toolbar)
    Toolbar toolbar;

    @BindView(R.id.title)
    TextView title;

    @BindView(R.id.ivProfile)
    ImageView ivProfile;
    @BindView(R.id.tvName)
    TextView tvName;
    @BindView(R.id.tvTagLine)
    TextView tvTagLine;
    @BindView(R.id.tvFollowers)
    TextView tvFollowers;
    @BindView(R.id.tvFollowing)
    TextView tvFollowing;

    private RestClient client;
    private User user;

    private static final int MY_PROFILE = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);
        ButterKnife.bind(this);
        initToobar();
        client = TwitterV3Application.getRestClient();

        long userId = getIntent().getLongExtra(EXTRA_USER_ID, MY_PROFILE);
        String screenName = getIntent().getStringExtra(EXTRA_SCREEN_NAME);

        if(userId == MY_PROFILE) {
            client.getUserInfo(new JsonHttpResponseHandler() {
                @Override
                public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                    Gson gson = new Gson();
                    user = gson.fromJson(response.toString(), User.class);
                    populateUserHeader(user);
                }

                @Override
                public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                    Log.e(TAG, "onFailure: error while getting the user info.", throwable);
                }
            });
        } else {
            client.getUserInfo(new JsonHttpResponseHandler() {
                @Override
                public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                    Gson gson = new Gson();
                    user = gson.fromJson(response.toString(), User.class);
                    populateUserHeader(user);
                }

                @Override
                public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                    Log.e(TAG, "onFailure: error while getting the user info.", throwable);
                }
            }, userId, screenName);
        }


        if (savedInstanceState == null) {
            UserTimeLineFragment fragmentUserTimeLine = UserTimeLineFragment.newInstance(screenName, false);
            FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
            ft.replace(R.id.flContainer, fragmentUserTimeLine);
            ft.commit();
        }
    }

    private void initToobar() {
        setSupportActionBar(toolbar);
    }

    private void populateUserHeader(User user) {
        toolbar.setTitle(null);
        title.setText("@" + user.getScreenName());
        tvName.setText(user.getName());
        tvTagLine.setText(user.getTagLine());
        tvFollowers.setText(user.getFollowersCount() + getString(R.string.followers));
        tvFollowing.setText(user.getFollowingCount() + getString(R.string.following));
        Picasso.with(this).load(user.getProfileImageUrl()).transform(new RoundedCornersTransformation(5, 0)).into(ivProfile);
    }
}
