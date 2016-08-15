package com.codepath.apps.twitterV3.app;

import android.content.Context;

import com.codepath.apps.twitterV3.rest.RestClient;

/*
 * This is the Android application itself and is used to configure various settings
 * including the image cache in memory and on disk. This also adds a singleton
 * for accessing the relevant rest client.
 *
 *     RestClient client = RestApplication.getRestClient();
 *     // use client to send requests to API
 *
 */
public class TwitterV3Application extends com.activeandroid.app.Application {
	private static Context context;

	@Override
	public void onCreate() {
		super.onCreate();
		TwitterV3Application.context = this;
	}

	public static RestClient getRestClient() {
		return (RestClient) RestClient.getInstance(RestClient.class, TwitterV3Application.context);
	}
}