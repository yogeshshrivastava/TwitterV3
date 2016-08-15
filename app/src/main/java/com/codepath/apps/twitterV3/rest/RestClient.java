package com.codepath.apps.twitterV3.rest;

import android.content.Context;

import com.codepath.oauth.OAuthBaseClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import org.scribe.builder.api.Api;
import org.scribe.builder.api.TwitterApi;

/*
 * 
 * This is the object responsible for communicating with a REST API. 
 * Specify the constants below to change the API being communicated with.
 * See a full list of supported API classes: 
 *   https://github.com/fernandezpablo85/scribe-java/tree/master/src/main/java/org/scribe/builder/api
 * Key and Secret are provided by the developer site for the given API i.e dev.twitter.com
 * Add methods for each relevant endpoint in the API.
 * 
 * NOTE: You may want to rename this object based on the service i.e TwitterClient or FlickrClient
 * 
 */
public class RestClient extends OAuthBaseClient {
	public static final Class<? extends Api> REST_API_CLASS = TwitterApi.class; // Change this
	public static final String REST_URL = "https://api.twitter.com/1.1"; // Change this, base API URL
	public static final String REST_CONSUMER_KEY = "smqqNlT9CzpEl3I4zVgmVyw8a";       // Change this
	public static final String REST_CONSUMER_SECRET = "oTo0lTtMyKYqJfoIDSC3FRl7UVFKIQ0py3tx4n7rWrMiSISchc"; // Change this
	public static final String REST_CALLBACK_URL = "oauth://cptweetsv2"; // Change this (here and in manifest)

	public RestClient(Context context) {
		super(context, REST_API_CLASS, REST_URL, REST_CONSUMER_KEY, REST_CONSUMER_SECRET, REST_CALLBACK_URL);
	}

	/**
	 * Request the Home timeline for the user to get the list of tweets.
	 *
	 * @param handler
     */
	public void getHomeTimeLine(AsyncHttpResponseHandler handler, long since) {
		String apiUrl = getApiUrl("statuses/home_timeline.json");
		// Can specify query string params directly or through RequestParams.
		RequestParams params = new RequestParams();
		params.put("count", 25);
		params.put("since_id", since);
		getClient().get(apiUrl, params, handler);
	}

	/**
	 * Request the Home timeline for the user to get the list of tweets.
	 *
	 * @param handler
	 */
	public void getHomeTimeLineMaxId(AsyncHttpResponseHandler handler, long max) {
		String apiUrl = getApiUrl("statuses/home_timeline.json");
		// Can specify query string params directly or through RequestParams.
		RequestParams params = new RequestParams();
		params.put("count", 25);
		params.put("max_id", max);
		getClient().get(apiUrl, params, handler);
	}

	/**
	 * Request the Mentions for the intial user list.
	 *
	 * @param handler
	 */
	public void getMentions(AsyncHttpResponseHandler handler, long since) {
		String apiUrl = getApiUrl("statuses/mentions_timeline.json");
		// Can specify query string params directly or through RequestParams.
		RequestParams params = new RequestParams();
		params.put("count", 25);
		params.put("since_id", since);
		getClient().get(apiUrl, params, handler);
	}


	/**
	 * Request the mentions list since Max id
	 *
	 * @param handler
	 * @param max
     */
	public void getMentionsMaxId(AsyncHttpResponseHandler handler, long max) {
		String apiUrl = getApiUrl("statuses/mentions_timeline.json");
		// Can specify query string params directly or through RequestParams.
		RequestParams params = new RequestParams();
		params.put("count", 25);
		params.put("max_id", max);
		getClient().get(apiUrl, params, handler);
	}

	public void postComposedTweet(AsyncHttpResponseHandler handler, String tweet) {
		String apiUrl = getApiUrl("statuses/update.json");
		// Can specify query string params directly or through RequestParams.
		RequestParams params = new RequestParams();
		params.put("status", tweet);
		getClient().post(apiUrl, params, handler);
	}

	public void getProfileInformation(AsyncHttpResponseHandler handler) {
		long userId = 67548280;
		String screenName = "yogeshshri";
		String apiUrl = getApiUrl("users/show.json");
		// Can specify query string params directly or through RequestParams.
		RequestParams params = new RequestParams();
		params.put("user_id", userId);
		params.put("screen_name", screenName);
		getClient().get(apiUrl, params, handler);

	}


	public void getUserInfo(AsyncHttpResponseHandler handler, long userId, String screenName) {
		String apiUrl = getApiUrl("users/show.json");
		// Can specify query string params directly or through RequestParams.
		RequestParams params = new RequestParams();
		params.put("user_id", userId);
		params.put("screen_name", screenName);
		getClient().get(apiUrl, params, handler);
	}

	public void getUserInfo(AsyncHttpResponseHandler handler) {
		String apiUrl = getApiUrl("account/verify_credentials.json");
		getClient().get(apiUrl, null, handler);
	}

	public void getUserTimeLine(AsyncHttpResponseHandler handler, String screenName, long max) {
		String apiUrl = getApiUrl("statuses/user_timeline.json");
		// Can specify query string params directly or through RequestParams.
		RequestParams params = new RequestParams();
		params.put("count", 25);
		params.put("screen_name", screenName);
		// Only use max to get additional data.
		if(max != 1) {
			params.put("max_id", max);
		}
		getClient().get(apiUrl, params, handler);
	}



	/* 1. Define the endpoint URL with getApiUrl and pass a relative path to the endpoint
	 * 	  i.e getApiUrl("statuses/home_timeline.json");
	 * 2. Define the parameters to pass to the request (query or body)
	 *    i.e RequestParams params = new RequestParams("foo", "bar");
	 * 3. Define the request method and make a call to the client
	 *    i.e client.get(apiUrl, params, handler);
	 *    i.e client.post(apiUrl, params, handler);
	 */
}