package com.codepath.apps.LuluTweet;

import android.content.Context;
import android.widget.AbsListView;

/*
 * This is the Android application itself and is used to configure various settings
 * including the image cache in memory and on disk. This also adds a singleton
 * for accessing the relevant rest client.
 *
 *     TweetClient client = TweetApplication.getRestClient();
 *     // use client to send requests to API
 *
 */
public class TweetApplication extends com.activeandroid.app.Application {
	private static Context context;

	@Override
	public void onCreate() {
		super.onCreate();
		TweetApplication.context = this;
	}

	public static TweetClient getRestClient() {
		return (TweetClient) TweetClient.getInstance(TweetClient.class, TweetApplication.context);
	}
}

