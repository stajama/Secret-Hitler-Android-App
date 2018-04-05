package com.example.android.secrecthitler;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

/**
 * Created by staja on 3/28/2018.
 */

public class VotingResultActivity extends AppCompatActivity {

    private static final String TAG = VotingResultActivity.class.getName();
    Intent pickledRick;
    int playerID;

    TextView showResult;
    RecyclerView votingResultsList;
    TextView electionTracker;
    Button confirmViewButton;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_voting_results);

        showResult = findViewById(R.id.text_results_pass_or_fail);
        votingResultsList = findViewById(R.id.recycler_results_vote_list);
        electionTracker = findViewById(R.id.text_result_election_tracker);
        confirmViewButton = findViewById(R.id.button_result_move_on);

        confirmViewButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // send confirmation via client/vote_show_confirmation/<int:id>
                // start pollserver() in onPostExecute()
            }
        });
        // call Async task to get all vote data
        // onPostExecute will set up Adapter and fill recycler
    }


    public class sendConfirmation extends AsyncTask<Void, Void, Boolean> {
        @Override
        protected Boolean doInBackground(Void... voids) {
            try {
                NetworkUtilities.getResponseFromHttpUrl(NetworkUtilities.buildUrl("client/vote_show_confirmation/" + String.valueOf(playerID)));
            } catch (IOException e) {
                e.printStackTrace();
                Log.e(TAG, "doInBackground: Something wrong with sendConfirmation");
            }
            return true;
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
                        if (received.getInt("statusID") != 99 && received.getInt("statusID") != 98) {
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
                intent = statusActivityDispatcher.statusRouter(VotingResultActivity.this, jsonObject.getInt("statusID"));
            } catch (JSONException e) {
                e.printStackTrace();
            }
            Intent cookedIntent = ViewHandlingUtility.storeStatusInformation(intent, jsonObject);
            startActivity(cookedIntent);
        }
    }
}
