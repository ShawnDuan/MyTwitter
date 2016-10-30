package com.shawn_duan.mytwitter.fragments;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.loopj.android.http.JsonHttpResponseHandler;
import com.shawn_duan.mytwitter.utils.DividerItemDecoration;
import com.shawn_duan.mytwitter.MyTwitterApplication;
import com.shawn_duan.mytwitter.R;
import com.shawn_duan.mytwitter.network.TwitterClient;
import com.shawn_duan.mytwitter.adapters.TweetsArrayAdapter;
import com.shawn_duan.mytwitter.models.Tweet;
import com.shawn_duan.mytwitter.utils.EndlessRecyclerViewScrollListener;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;

import cz.msebera.android.httpclient.Header;

/**
 * Created by sduan on 10/29/16.
 */

public class TimeLineFragment extends Fragment {
    private final static String TAG = TimeLineFragment.class.getSimpleName();

    private TwitterClient mClient;

    private RecyclerView mTimelineList;
    private ArrayList<Tweet> mTweetList;
    private TweetsArrayAdapter mAdapter;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mClient = MyTwitterApplication.getRestClient();     // singleton client
        mTweetList = new ArrayList<>();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_main, container, false);
        mTimelineList = (RecyclerView) view.findViewById(R.id.rcTimeLine);

        setupRecyclerView();

        populateTimeline();

        return view;
    }

    // Send an API request to get the timeline json
    // Fill the recyclerview by creating the tweet object from the json
    private void populateTimeline() {
        long sinceId = -1;
        if (mTweetList.size() > 0) {
            sinceId = mTweetList.get(mTweetList.size() - 1).getUid();
        }
        mClient.getHomeTimeLine(sinceId, new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONArray response) {
                // Deserialize Json
                // Create models
                // Load the model

                mTweetList.addAll(Tweet.fromJSONArray(response));
                mAdapter.notifyDataSetChanged();
                Log.d(TAG, response.toString());
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                super.onFailure(statusCode, headers, throwable, errorResponse);
            }
        });
    }

    private void setupRecyclerView() {
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getActivity());
        mTimelineList.setLayoutManager(linearLayoutManager);
        mAdapter = new TweetsArrayAdapter(getActivity(), mTweetList);
        mTimelineList.setAdapter(mAdapter);
        RecyclerView.ItemDecoration itemDecoration = new
                DividerItemDecoration(getActivity(), DividerItemDecoration.VERTICAL_LIST);
        mTimelineList.addItemDecoration(itemDecoration);

        mTimelineList.addOnScrollListener(new EndlessRecyclerViewScrollListener(linearLayoutManager) {
            @Override
            public void onLoadMore(int page, int totalItemsCount, RecyclerView view) {
                populateTimeline();
            }
        });
    }
}
