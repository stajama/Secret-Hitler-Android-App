package com.example.android.secrecthitler;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
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

public class NominationActivity extends AppCompatActivity {

    static final String TAG = NominationActivity.class.getName();

    int playerID;
    String playerName;
    int statusID;
    Intent pickledRick;
    boolean isPresident;
    static JSONObject nominationList;

    TextView headline;
    TextView libCount;
    TextView fasCount;
    TextView eTrack;
    TextView headlinePresident;
    Spinner playerSelector;
    Button confirmationButton;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_base);

        pickledRick = getIntent();
        Log.w(TAG, "onCreate: start of onCreate, check contents of pickledRick:\n" + pickledRick.toString());
        playerID = pickledRick.getIntExtra("id", 0);
        statusID = pickledRick.getIntExtra("statusID", 0);
        Log.i(TAG, "onCreate: start, current playerID/statusID: " + String.valueOf(playerID) + "/" + String.valueOf(statusID));



        headline = (TextView) findViewById(R.id.text_base_headline);

        Log.i(TAG, "onCreate: statusUpdate from pickledRick pickled-intent-data = " + pickledRick.getStringExtra("statusUpdate"));
        Log.i(TAG, "onCreate: headline view id: " + String.valueOf(headline.getId()));
        headline.setText(pickledRick.getStringExtra("statusUpdate"));
        libCount = (TextView) findViewById(R.id.text_base_libCount);
        libCount.setText(libCount.getText() + String.valueOf(pickledRick.getIntExtra("libPolicies", 0)));
        fasCount = (TextView) findViewById(R.id.text_base_fasCount);
        fasCount.setText(fasCount.getText() + String.valueOf(pickledRick.getIntExtra("fasPolicies", 0)));
        eTrack = (TextView) findViewById(R.id.text_base_election_tracker);
        eTrack.setText(eTrack.getText() + String.valueOf(pickledRick.getIntExtra("electionTracker", 0)));
        headlinePresident = (TextView) findViewById(R.id.text_base_president_headline);
        playerSelector = (Spinner) findViewById(R.id.spinner_base_selection);
        confirmationButton = (Button) findViewById(R.id.button_base_confirmation);
        headlinePresident.setVisibility(View.GONE);
        playerSelector.setVisibility(View.GONE);
        confirmationButton.setVisibility(View.GONE);
        confirmationButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (playerSelector.getSelectedItem().toString() != null) {
                    new sendNomination().execute(playerSelector.getSelectedItem().toString());
                }
            }
        });


        switch (statusID) {
            case 3:
                Log.i(TAG, "onCreate: statusID 3, normal nomination screen");
                new nominateChancellorCall().execute();
                break;
            default:
                Log.e(TAG, "onCreate: Status does not match the desired activity");
        }

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

    public class nominateChancellorCall extends AsyncTask<Void, Void, JSONObject> {


        @Override
        protected JSONObject doInBackground(Void... voids) {
            JSONObject received = null;
            try {
                received = NetworkUtilities.getJSONFromUrl(NetworkUtilities.buildUrl("client/nominate_chancellor/" + Integer.toString(playerID)));
            } catch (IOException e) {
                e.printStackTrace();
            }
            if (received != null) {
                nominationList = received;
                return received;
            } else {
                Log.e(TAG, "doInBackground: did not receive JSON data.");
                return null;
            }
        }

        @Override
        protected void onPostExecute(JSONObject jsonObject) {
            super.onPostExecute(jsonObject);
            if (jsonObject != null) {
                try {
                    Log.i(TAG, "onPostExecute: nominateChancellorCall() start, what the JSON says: 'wait' = " + jsonObject.getString("wait"));
                    if (jsonObject.getString("wait").equals("")) {
                        headlinePresident.setVisibility(View.VISIBLE);
                        playerSelector.setVisibility(View.VISIBLE);
                        confirmationButton.setVisibility(View.VISIBLE);
                        Log.i(TAG, "onPostExecute: this player is president");
                        isPresident = true;
                        ArrayAdapter<String> playerList = new ArrayAdapter<String>(getBaseContext(), android.R.layout.simple_spinner_item, getPlayerNameArray(jsonObject));
                        playerList.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                        playerSelector.setAdapter(playerList);

                    } else {
                        Log.i(TAG, "onPostExecute: this player NOT is president");
                        isPresident = false;
                        new pollServer().execute();

                    }
                } catch (JSONException e) {
                    Log.i(TAG, "onPostExecute: nominateChancellorCall(), error parsing JSON data");
                    e.printStackTrace();
                }
            }
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
                        if (received.getInt("statusID") != 3) { // requires additional checks for other presidential selection statuses.
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
                intent = statusActivityDispatcher.statusRouter(NominationActivity.this, jsonObject.getInt("statusID"));
            } catch (JSONException e) {
                e.printStackTrace();
            }
            Intent cookedIntent = ViewHandlingUtility.storeStatusInformation(intent, jsonObject);
            startActivity(cookedIntent);
        }
    }

    List<String> getPlayerNameArray(JSONObject data) {
        Log.d(TAG, "getPlayerNameArray: check numbers on data size. eligiblePlayers/size of data/size of array = 5/" + String.valueOf(data.length()) + "/" + String.valueOf(data.length() - 2));
        List<String> returnList = new ArrayList<String>(); // data will only every be (all eligible players) + 'wait' + 'information' in length.
        if (data != null) {
            Iterator<String> keys = data.keys();
            String playerID;
            while (keys.hasNext()) {
                playerID = keys.next();
                Log.d(TAG, "getPlayerNameArray: !!!!!!!!" + playerID);
                if (!playerID.equals("wait") && !playerID.equals("information")){
                    try {
                        returnList.add(data.getString(playerID));
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }
        }
        return returnList;
    }

    static String setNominee(String selectedName) {
        Iterator<String> array = nominationList.keys();
        String check = null;
        String idNumber = null;
        while (array.hasNext()) {
            idNumber = array.next();
            try {
                Log.i(TAG, "setNominee: in loop, checking: " + idNumber + "-" + nominationList.getString(idNumber));
            } catch (JSONException e) {
                e.printStackTrace();
            }
            try {
                check = nominationList.getString(idNumber);
            } catch (JSONException e) {
                e.printStackTrace();
            }
            if (check.equals(selectedName)) {
                break;
            }
        }
        Log.e(TAG, "setNominee: Something wrong with JSON loop to pull playerID from original call.");
        return idNumber;
    }

    public class sendNomination extends AsyncTask<String, Void, String> {

        @Override
        protected String doInBackground(String... strings) {
            String response = null;
            String call = "client/nominate_chancellor_order/" + String.valueOf(playerID) + "/" + setNominee(strings[0]);
            Log.d(TAG, "doInBackground: calling to: " + call);
            try {
                response = NetworkUtilities.getResponseFromHttpUrl(NetworkUtilities.buildUrl(call));
            } catch (IOException e) {
                e.printStackTrace();
                Log.e(TAG, "doInBackground: HTTp request not sent correctly");
            }
            Log.i(TAG, "doInBackground: response:" + response);
            if (response.equals("confirm")) {
                System.out.println(response);
                Log.e(TAG, "doInBackground: Problem with sendNomination.doInBackground(). did not receive confirmation.");
            }
            return null;
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            new pollServer().execute();
        }
    }


}
