package com.example.android.secrecthitler;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.PersistableBundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * Created by staja on 3/28/2018.
 */

public class VotingActivity extends AppCompatActivity {

    private static final String TAG = VotingActivity.class.getName();
    Intent pickledRick;
    int playerID;

    TextView flavorText;
    LinearLayout buttonHolder;
    Button positiveVoteButton;
    Button negativeVoteButton;
    TextView deadNotification;
    ProgressBar progressBar;
    TextView waitingText;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_voting);
        pickledRick = getIntent();
        playerID = pickledRick.getIntExtra("id", 0);

        flavorText = findViewById(R.id.text_president_chancellor);
        buttonHolder = findViewById(R.id.ll_voting_buttons);
        positiveVoteButton = findViewById(R.id.button_voting_ja);
        negativeVoteButton = findViewById(R.id.button_voting_nien);
        deadNotification = findViewById(R.id.text_voting_dead_warning);
        waitingText = findViewById(R.id.text_voting_waiting);
        progressBar = findViewById(R.id.progbar_voting_wait);
        progressBar.setVisibility(View.GONE);

        flavorText.setText(pickledRick.getStringExtra("statusUpdate"));
        waitingText.setVisibility(View.INVISIBLE);

        if (pickledRick.getBooleanExtra("alive", true)) {
            deadNotification.setVisibility(View.INVISIBLE);
            positiveVoteButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    new submitVote().execute(true);
                }
            });
            negativeVoteButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    new submitVote().execute(false);
                }
            });
        } else {
            progressBar.setVisibility(View.VISIBLE);
            deadNotification.setVisibility(View.VISIBLE);
            waitingText.setVisibility(View.VISIBLE);
            new pollServer().execute();
        }
    }
    public void voteSumbitted() {
        progressBar.setVisibility(View.VISIBLE);
        waitingText.setVisibility(View.VISIBLE);
        positiveVoteButton.setOnClickListener(null);
        negativeVoteButton.setOnClickListener(null);
    }

    public class submitVote extends AsyncTask<Boolean, Void, Boolean> {
        @Override
        protected Boolean doInBackground(Boolean... booleans) {
            String response = null;
            if (booleans[0]) {
                try {
                    response = NetworkUtilities.getResponseFromHttpUrl(NetworkUtilities.buildUrl("client/submit_vote/" + String.valueOf(playerID) + "/1"));
                } catch (IOException e) {
                    e.printStackTrace();
                }

            } else {
                try {
                    response = NetworkUtilities.getResponseFromHttpUrl(NetworkUtilities.buildUrl("client/submit_vote/" + String.valueOf(playerID) + "/0"));
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            if (response.equals("confirm")) {
                return true;
            } else {
                return false;
            }
        }

        @Override
        protected void onPostExecute(Boolean aBoolean) {
            super.onPostExecute(aBoolean);
            if (!aBoolean) {
                Log.e(TAG, "onPostExecute: something wrong with submitVote()");
            }
            voteSumbitted();
            new pollServer().execute();
        }
    }

    public class pollServer extends AsyncTask<Void, Void, JSONObject> {


        @Override
        protected JSONObject doInBackground(Void... voids) {
            JSONObject received = null;
            while (true) {
                try {
                    received = NetworkUtilities.getJSONFromUrl(NetworkUtilities.buildUrl("client/client_status/" + Integer.toString(playerID)));
                } catch (IOException e) {
                    e.printStackTrace();
                }
                if (received != null) {
                    try {
                        if (received.getInt("statusID") != 6) { // requires additional checks for other presidential selection statuses.
                            return received;
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }
        }

        @Override
        protected void onPostExecute(JSONObject jsonObject) {
            super.onPostExecute(jsonObject);
            Intent intent = null;
            try {
                intent = statusActivityDispatcher.statusRouter(VotingActivity.this, jsonObject.getInt("statusID"));
            } catch (JSONException e) {
                e.printStackTrace();
            }
            Intent cookedIntent = ViewHandlingUtility.storeStatusInformation(intent, jsonObject);
            startActivity(cookedIntent);
        }
    }

}
