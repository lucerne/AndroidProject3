package com.codepath.apps.LuluTweet;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import com.codepath.apps.LuluTweet.models.Tweet;
import com.codepath.apps.LuluTweet.models.TweetArrayAdapter;
import com.loopj.android.http.JsonHttpResponseHandler;

import org.json.JSONArray;
import org.json.JSONObject;
import org.parceler.Parcels;

import java.util.ArrayList;

import cz.msebera.android.httpclient.Header;

public class TimeLineActivity extends AppCompatActivity {

    private TweetClient client;
    private ArrayList<Tweet> tweets;
    private TweetArrayAdapter aTweets;
    private ListView lvTweets;
    private long lastMaxId;
    private final int COMPOSE_REQUEST_CODE = 1;
    private final int VIEW_REQUEST_CODE = 2;
    private SwipeRefreshLayout swipeContainer;
    private long replyTweetId;
    private String replyScreenname;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_time_line);

        // Set view, data, and adapter
        lvTweets= (ListView) findViewById(R.id.lvTweet);
        tweets = new ArrayList<>();
        aTweets = new TweetArrayAdapter(this, tweets);
        lvTweets.setAdapter(aTweets);

        client = TweetApplication.getRestClient();
        populateTimeline(0);

        // reply to earlier tweet id
        replyTweetId = 0;

        // Attach the listener to the AdapterView onCreate
        lvTweets.setOnScrollListener(new EndlessScrollListener() {
            @Override
            public boolean onLoadMore(int page, int totalItemsCount) {
                // Triggered only when new data needs to be appended to the list
                // Add whatever code is needed to append new items to your AdapterView
                if (tweets.size() > 0) {
                    lastMaxId = tweets.get(tweets.size()-1).getUid();
                    populateTimeline(lastMaxId);
                }
                else {
                    populateTimeline(0);
                }
                // or customLoadMoreDataFromApi(totalItemsCount);
                return true; // ONLY if more data is actually being loaded; false otherwise.
            }
        });


        // Lookup the swipe container view
        swipeContainer = (SwipeRefreshLayout) findViewById(R.id.swipeContainer);
        // Setup refresh listener which triggers new data loading
        swipeContainer.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                // Your code to refresh the list here.
                // Make sure you call swipeContainer.setRefreshing(false)
                // once the network request has completed successfully.
                aTweets.clear();
                tweets.clear();
                populateTimeline(0);
            }
        });
        // Configure the refreshing colors
        swipeContainer.setColorSchemeResources(android.R.color.holo_blue_bright,
                android.R.color.holo_green_light,
                android.R.color.holo_orange_light,
                android.R.color.holo_red_light);


        // Launch tweet view
        lvTweets.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                launchTweetViewActivity(position);
                replyTweetId = tweets.get(position).getUid();
                replyScreenname = tweets.get(position).getUser().getScreenName();

            }
        });

    }

    //
    public void populateTimeline(long offset){
        client.getTimeline( new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONArray response) {
                response.toString();
                tweets = Tweet.fromJSONArray(response);
                aTweets.addAll(tweets);

                // Now we call setRefreshing(false) to signal refresh has finished
                swipeContainer.setRefreshing(false);
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable,
                                  JSONObject errorResponse) {
            }
        }, offset);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_timeline, menu);
        return true;
    }

    public void onCompose(MenuItem mi){
        launchComposeActivity(0);
    }

    public void launchComposeActivity(int reply){
        Tweet tweet = new Tweet();
        Intent intent = new Intent(this, ComposeActivity.class);

        // reply screen name
        if (reply == 1 && replyScreenname != null) {
            tweet.setIn_reply_to_username("@" + replyScreenname + " ");
        }
        intent.putExtra("tweet", Parcels.wrap(tweet));
        intent.putExtra("reply", Parcels.wrap(reply));
        startActivityForResult(intent, COMPOSE_REQUEST_CODE);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == RESULT_OK) {
            if (requestCode == COMPOSE_REQUEST_CODE) {
                final Tweet tweet = (Tweet) Parcels.unwrap(data.getParcelableExtra("tweet"));
                Log.d("DEBUG", tweet.toString());

                final int reply = (int) Parcels.unwrap(data.getParcelableExtra("reply"));
                if (reply == 1){
                    tweet.setIn_reply_to_status_id(replyTweetId);
                }

                // post to twitter
                client.postTimeline(new JsonHttpResponseHandler() {
                    @Override
                    public void onSuccess(int statusCode, Header[] headers, JSONObject jsonObj) {
                        jsonObj.toString();

                        // inject tweet into adapter
                        Tweet tweet = Tweet.fromJSON(jsonObj);

                        // show the latest tweet at the top
                        aTweets.insert(tweet, 0);
                    }

                    @Override
                    public void onFailure(int statusCode, Header[] headers, Throwable throwable,
                                          JSONObject errorResponse) {
                        errorResponse.toString();
                    }
                }, tweet);

            }

            // reply to earlier tweet
            if (requestCode == VIEW_REQUEST_CODE) {
                launchComposeActivity(1);
            }
        }
    }

    public void launchTweetViewActivity(int position){

        // view detailed tweet
        Intent intent = new Intent(this, TweetViewActivity.class);
        Tweet tweet = tweets.get(position);
        intent.putExtra("tweet", Parcels.wrap(tweet));
        startActivityForResult(intent, VIEW_REQUEST_CODE);
    }
}
