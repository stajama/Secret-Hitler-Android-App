package com.example.android.secrecthitler;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.Iterator;

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
    ProgressBar progressBar;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_voting_results);

        pickledRick = getIntent();
        playerID = pickledRick.getIntExtra("id", 0);

        showResult = findViewById(R.id.text_results_pass_or_fail);
        votingResultsList = findViewById(R.id.recycler_results_vote_list);
        electionTracker = findViewById(R.id.text_result_election_tracker);
        confirmViewButton = findViewById(R.id.button_result_move_on);
        progressBar = findViewById(R.id.progbar_result_wait);
        progressBar.setVisibility(View.INVISIBLE);

        confirmViewButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                new sendConfirmation().execute();
            }
        });

        new getVoteResults().execute();
    }

    public String[] gatherVotesToString(JSONObject data) throws JSONException {
        String[] outList = new String[data.getInt("number of votes")];
        int index = 0;
        Iterator<String> keyList = data.keys();
        String keyup = null;
        while (keyList.hasNext()) {
            keyup = keyList.next();
            if (keyup.equals("result") || keyup.equals("number of votes")) {
                continue;
            } else {
                if (data.getBoolean(keyup)){
                    outList[index++] = keyup + " --- Ja!";
                } else {
                    outList[index++] = keyup + " --- Nien!";
                }
            }
        }
        return outList;
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

        @Override
        protected void onPostExecute(Boolean aBoolean) {
            super.onPostExecute(aBoolean);
            progressBar.setVisibility(View.VISIBLE);
            new pollServer().execute();
        }
    }

    public class getVoteResults extends AsyncTask<Void, Void, JSONObject> {

        @Override
        protected JSONObject doInBackground(Void... voids) {
            JSONObject response = null;
            try {
                response = NetworkUtilities.getJSONFromUrl(NetworkUtilities.buildUrl("client/show_all_votes"));
            } catch (IOException e) {
                e.printStackTrace();
            }
            if (response == null) {
                Log.e(TAG, "doInBackground: JSON call did not return.");
            }
            return response;
        }

        @Override
        protected void onPostExecute(JSONObject jsonObject) {
            super.onPostExecute(jsonObject);
            LinearLayoutManager layoutManager = new LinearLayoutManager(VotingResultActivity.this);
            RCAdapterNoClick noClickAdapter = new RCAdapterNoClick(100);
            try {
                noClickAdapter.setPlayerNameList(gatherVotesToString(jsonObject));
            } catch (JSONException e) {
                e.printStackTrace();
            }
            votingResultsList.setAdapter(noClickAdapter);
            votingResultsList.setLayoutManager(layoutManager);
            try {
                showResult.setText(jsonObject.getString("result"));
            } catch (JSONException e) {
                e.printStackTrace();
            }
            electionTracker.setText("Current Election Tracker: " + pickledRick.getIntExtra("electionTracker", 0));
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
