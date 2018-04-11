package com.example.android.secrecthitler;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

/**
 * Created by staja on 4/4/2018.
 */

public class PolicyResultActivity extends AppCompatActivity {

    private static final String TAG = PolicyResultActivity.class.getName();

    Intent pickledRick;
    int playerID;
    int statusID;
    String statusUpdate;
    int libCount;
    int fasCount;
    int eTrack;

    TextView resultAnnouncement;
    TextView liberalPolicyCounter;
    TextView fascistPolicyCounter;
    TextView electionTracker;
    Button confirmationButton;
    TextView waitingText;
    ProgressBar progressBar;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_policy_results);

        pickledRick = getIntent();
        playerID = pickledRick.getIntExtra("id", 0);
        statusID = pickledRick.getIntExtra("statusID", 0);
        libCount = pickledRick.getIntExtra("libCount", 0);
        fasCount = pickledRick.getIntExtra("fasCount", 0);
        eTrack = pickledRick.getIntExtra("electionTracker", 0);
        statusUpdate = pickledRick.getStringExtra("statusUpdate");

        resultAnnouncement = findViewById(R.id.text_policy_announcement);
        resultAnnouncement.setText(statusUpdate);
        liberalPolicyCounter = findViewById(R.id.text_results_libCount);
        liberalPolicyCounter.setText(liberalPolicyCounter.getText().toString() + String.valueOf(libCount));
        fascistPolicyCounter = findViewById(R.id.text_results_fasCount);
        fascistPolicyCounter.setText(fascistPolicyCounter.getText().toString() + String.valueOf(fasCount));
        electionTracker = findViewById(R.id.text_results_election_tracker);
        electionTracker.setText(electionTracker.getText().toString() + String.valueOf(eTrack));
        progressBar = findViewById(R.id.progbar_policy_result_wait);
        progressBar.setVisibility(View.GONE);
        waitingText = findViewById(R.id.text_policy_conf_waiting);
        waitingText.setVisibility(View.GONE);
        confirmationButton = findViewById(R.id.button_policy_result_confirm);
        confirmationButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                new sendConfirmation().execute();
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.information, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int clickedItem = item.getItemId();
        if (clickedItem == R.id.info_menu_button) {
            Intent goTo = new Intent(this, RoleActivity.class);
            goTo.putExtra("id", pickledRick.getIntExtra("id", 0));
            startActivity(goTo);
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
//        logAndAppend("onSaveInstanceState");
        outState.putInt("playerID", playerID);
    }

    public void showWaiting() {
        confirmationButton.setClickable(false);
        waitingText.setVisibility(View.VISIBLE);
        progressBar.setVisibility(View.VISIBLE);
    }

    public class sendConfirmation extends AsyncTask<Void, Void, Boolean> {

        @Override
        protected Boolean doInBackground(Void... voids) {
            String response = null;
            try {
                response = NetworkUtilities.getResponseFromHttpUrl(NetworkUtilities.buildUrl("client/policy_result_confirm/" + String.valueOf(playerID)));
            } catch (IOException e) {
                e.printStackTrace();
            }
            return !(response == null);
        }

        @Override
        protected void onPostExecute(Boolean aBoolean) {
            super.onPostExecute(aBoolean);
            showWaiting();
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
                        if (received.getInt("statusID") != 97 && received.getInt("statusID") != 13) {
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
                intent = statusActivityDispatcher.statusRouter(PolicyResultActivity.this, jsonObject.getInt("statusID"));
            } catch (JSONException e) {
                e.printStackTrace();
            }
            Intent cookedIntent = ViewHandlingUtility.storeStatusInformation(intent, jsonObject);
            startActivity(cookedIntent);
        }
    }
}
