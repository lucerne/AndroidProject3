package com.codepath.apps.LuluTweet;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.EditText;

import com.codepath.apps.LuluTweet.models.Tweet;

import org.parceler.Parcels;

public class ComposeActivity extends AppCompatActivity {

    private TweetClient client;
    int textLength = 0;
    int reply;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_compose);

        Tweet tweet = (Tweet) Parcels.unwrap(getIntent().getParcelableExtra("tweet"));

        reply = (int) Parcels.unwrap(getIntent().getParcelableExtra("reply"));
        final EditText etComposeBody = (EditText) findViewById(R.id.etComposeBody);

        String screenname = tweet.getIn_reply_to_username();
        // reply to tweet, reply body begins with reply screen name
        if (reply == 1){
            etComposeBody.setText(tweet.getIn_reply_to_username());
            etComposeBody.setSelection(etComposeBody.getText().length());
        }

        final EditText etRemainChar = (EditText) findViewById(R.id.etRemainChar);
        etComposeBody.addTextChangedListener(new TextWatcher() {
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                // Fires right as the text is being changed (even supplies the range of text)
                etRemainChar.setText(Integer.toString(140-s.length()));
                if (s.length() > 140){
                    etRemainChar.setTextColor(Color.parseColor("#ff00aaff"));
                }
                else {
                    etRemainChar.setTextColor(Color.parseColor("#ff000000"));
                }
            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count,
                                          int after) {
                // Fires right before text is changing
            }

            @Override
            public void afterTextChanged(Editable s) {
                // Fires right after the text has changed
//                etComposeBody.setText(s.toString());
            }
        });
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_compose, menu);
        return true;
    }

    public void onSave(MenuItem mi){
        EditText tvComposeBody = (EditText) findViewById(R.id.etComposeBody);

        // inject tweet into adapter
        Tweet tweet = new Tweet();
        tweet.setBody(tvComposeBody.getText().toString());

        Intent intent = new Intent();
        intent.putExtra("tweet", Parcels.wrap(tweet));
        intent.putExtra("reply", Parcels.wrap(reply));
        setResult(RESULT_OK, intent);
        this.finish();
    }
}
