package com.shawn_duan.mytwitter;

import android.app.Application;
import android.content.Context;

import com.shawn_duan.mytwitter.network.TwitterClient;

/*
 * This is the Android application itself and is used to configure various settings
 * including the image cache in memory and on disk. This also adds a singleton
 * for accessing the relevant rest client.
 *
 *     TwitterClient client = MyTwitterApplication.getRestClient();
 *     // use client to send requests to API
 *
 */
public class MyTwitterApplication extends Application {
	private static Context context;

	@Override
	public void onCreate() {
		super.onCreate();
		MyTwitterApplication.context = this;
	}

	public static TwitterClient getRestClient() {
		return (TwitterClient) TwitterClient.getInstance(TwitterClient.class, MyTwitterApplication.context);
	}
}