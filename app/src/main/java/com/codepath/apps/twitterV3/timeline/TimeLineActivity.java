package com.codepath.apps.twitterV3.timeline;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.TabLayout;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.codepath.apps.twitterV3.R;
import com.codepath.apps.twitterV3.create.CreateTweetActivity;
import com.codepath.apps.twitterV3.mentions.MentionsTimeLineFragment;
import com.codepath.apps.twitterV3.profile.ProfileActivity;
import com.codepath.apps.twitterV3.views.SmartFragmentStatePagerAdapter;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class TimeLineActivity extends AppCompatActivity {

    private static final String TAG = TimeLineActivity.class.getSimpleName();

    private static final int REQUEST_CODE_CREATE_TWEET = 1001;

    @BindView(R.id.toolbar)
    Toolbar toolbar;

    @BindView(R.id.tabs)
    TabLayout slidingTabLayout;

    @BindView(R.id.vpPager)
    ViewPager vpPager;

    @BindView(R.id.fab)
    FloatingActionButton fab;

    private SmartFragmentStatePagerAdapter adapterViewPager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_time_line);
        ButterKnife.bind(this);
        init();
    }

    private void init() {
        setSupportActionBar(toolbar);
        adapterViewPager = new MyPagerAdapter(getSupportFragmentManager());
        vpPager.setAdapter(adapterViewPager);

        // Setting the ViewPager For the SlidingTabsLayout
        slidingTabLayout.setupWithViewPager(vpPager);

        vpPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                if(position == 0) {
                    fab.show();
                } else {
                    fab.hide();
                }
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
    }

    @OnClick(R.id.fab)
    public void onFabClicked(View view) {
        Intent intent = new Intent(this, CreateTweetActivity.class);
        ActivityCompat.startActivityForResult(this, intent, REQUEST_CODE_CREATE_TWEET, null);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if(resultCode == RESULT_OK && requestCode == REQUEST_CODE_CREATE_TWEET) {
            // Refesh the latest tweet from the server.
            Fragment fragment = adapterViewPager.getItem(0);
            if(fragment != null) {
                ((TimeLineFragment) fragment).refreshTimeLine();
            }
        }
    }

    public static class MyPagerAdapter extends SmartFragmentStatePagerAdapter {

        private static int NUM_ITEMS = 2;

        public MyPagerAdapter(FragmentManager fragmentManager) {
            super(fragmentManager);
        }

        // Returns total number of pages
        @Override
        public int getCount() {
            return NUM_ITEMS;
        }

        @Override
        public Fragment getItem(int position) {
            switch (position) {
                case 0:
                    return HomeTimeLineFragment.newInstance();
                case 1:
                    return MentionsTimeLineFragment.newInstance();
                default:
                    return null;
            }
        }

        // Returns the page title for the top indicator
        @Override
        public CharSequence getPageTitle(int position) {
            if(position == 0) {
                return "HOME";
            } else {
                return "MENTIONS";
            }
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        return super.onOptionsItemSelected(item);
    }

    public void onProfileViewClicked(MenuItem item) {
        Toast.makeText(this, "Profile clicked", Toast.LENGTH_LONG).show();
        Intent intent = new Intent(this, ProfileActivity.class);
        ActivityCompat.startActivity(this, intent, null);
    }
}
