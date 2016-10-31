package com.shawn_duan.mytwitter.fragments;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Toast;

import com.loopj.android.http.JsonHttpResponseHandler;
import com.shawn_duan.mytwitter.MyTwitterApplication;
import com.shawn_duan.mytwitter.R;
import com.shawn_duan.mytwitter.adapters.TweetsArrayAdapter;
import com.shawn_duan.mytwitter.models.Tweet;
import com.shawn_duan.mytwitter.network.TwitterClient;
import com.shawn_duan.mytwitter.utils.DividerItemDecoration;
import com.shawn_duan.mytwitter.utils.EndlessRecyclerViewScrollListener;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;
import cz.msebera.android.httpclient.Header;

/**
 * Created by sduan on 10/29/16.
 */

public class TimeLineFragment extends Fragment {
    private final static String TAG = TimeLineFragment.class.getSimpleName();

    private TwitterClient mClient;

    @BindView(R.id.swipContainer)
    SwipeRefreshLayout mSwipeRefreshLayout;
    @BindView(R.id.rcTimeLine)
    RecyclerView mRecyclerView;
    private ArrayList<Tweet> mTweetList;
    private TweetsArrayAdapter mAdapter;
    private long mNewestId, mOldestId;
    private Unbinder unbinder;

    private final static int NORMAL_POPULATE_AMOUNT = 25;
    private final static long NOT_APPLICABLE = 0;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mClient = MyTwitterApplication.getRestClient();     // singleton client
        mTweetList = new ArrayList<>();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_timeline, container, false);
        unbinder = ButterKnife.bind(this, view);

        setupRecyclerView();

        populateTimeline(mNewestId, NOT_APPLICABLE, (int) NOT_APPLICABLE);

        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        Log.d(TAG, "onResume()");
    }

    @Override
    public void onPause() {
        super.onPause();
        Log.d(TAG, "onPause()");
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
    }

    // if count is -1 or 0, populate as much as possible in the range of sinceId to maxId.
    private void populateTimeline(long sinceId, final long maxId, int count) {

        Log.d(TAG, "populateTimeLine(), sinceId: " + sinceId + ", maxId: " + maxId + ", count: " + count);
        mClient.getHomeTimeLine(sinceId, maxId, count, new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONArray response) {
                // Deserialize Json
                // Create models
                // Load the model
                boolean addToBottom = (maxId != NOT_APPLICABLE);
                int newTweetCount = response.length();

                if (addToBottom) {
                    mTweetList.addAll(Tweet.fromJSONArray(response));
                } else {
                    mTweetList.addAll(0, Tweet.fromJSONArray(response));
                }

                // update max/since id based on the current TweetList
                mNewestId = mTweetList.get(0).getUid();
                mOldestId = mTweetList.get(mTweetList.size() - 1).getUid();

                mAdapter.notifyDataSetChanged();

                if (!addToBottom) {
                    mRecyclerView.smoothScrollToPosition(0);
                }

                Log.d(TAG, "Amount of new tweets added into timeline: " + newTweetCount);

                if (mSwipeRefreshLayout != null) {
                    mSwipeRefreshLayout.setRefreshing(false);
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                super.onFailure(statusCode, headers, throwable, errorResponse);
                if (statusCode == 429) {
                    Toast.makeText(getActivity(),
                            "Request number meets the limit, please wait for 15mins before retry.",
                            Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void populateTimeline() {
        populateTimeline(0, 0, 0);
    }

    private void setupRecyclerView() {
        mSwipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                populateTimeline(mNewestId, NOT_APPLICABLE, (int) NOT_APPLICABLE);
            }
        });
        mSwipeRefreshLayout.setColorSchemeResources(android.R.color.holo_blue_bright,
                android.R.color.holo_green_light,
                android.R.color.holo_orange_light,
                android.R.color.holo_red_light);

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getActivity());
        mRecyclerView.setLayoutManager(linearLayoutManager);
        mAdapter = new TweetsArrayAdapter(getActivity(), mTweetList);
        mRecyclerView.setAdapter(mAdapter);
        RecyclerView.ItemDecoration itemDecoration = new
                DividerItemDecoration(getActivity(), DividerItemDecoration.VERTICAL_LIST);
        mRecyclerView.addItemDecoration(itemDecoration);

        mRecyclerView.addOnScrollListener(new EndlessRecyclerViewScrollListener(linearLayoutManager) {
            @Override
            public void onLoadMore(int page, int totalItemsCount, RecyclerView view) {
                Log.d(TAG, "onLoadMore()");
                populateTimeline(NOT_APPLICABLE, mOldestId, NORMAL_POPULATE_AMOUNT);
            }
        });
    }
}
