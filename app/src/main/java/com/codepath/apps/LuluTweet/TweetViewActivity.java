package com.codepath.apps.LuluTweet;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.codepath.apps.LuluTweet.models.Tweet;
import com.squareup.picasso.Picasso;

import org.parceler.Parcels;

public class TweetViewActivity extends AppCompatActivity {

    Tweet tweet;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tweet_view);

        tweet = (Tweet) Parcels.unwrap(getIntent().getParcelableExtra("tweet"));

        // display detailed view of tweet
        ImageView ivAvatar = (ImageView) findViewById(R.id.ivAvatar);
        TextView tvUserName = (TextView) findViewById(R.id.tvUserName);
        TextView tvBody = (TextView) findViewById(R.id.tvBody);
        TextView tvDate = (TextView) findViewById(R.id.tvDate);

        tvUserName.setText(tweet.getUser().getName());
        tvBody.setText(tweet.getBody());
        tvDate.setText(Tweet.getRelativeTimeAgo(tweet.getCreatedAt()));

        // set picasso image
        ivAvatar.setImageResource(android.R.color.transparent);
        Picasso.with(this).load(tweet.getUser().getProfileImageUrl()).into(ivAvatar);
    }

    // display detailed view of tweet


    // reply to respond from tweet
    public void onReply(View view){

        // signal button click to timeline
        Intent intent = new Intent();
        intent.putExtra("reply", Parcels.wrap(1));
        setResult(RESULT_OK, intent);
        this.finish();
    }

    // back press
//    @Override
//    public void onBackPressed() {
//        this.finish();
//    }
}
