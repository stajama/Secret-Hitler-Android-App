package com.example.android.secrecthitler;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
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
 * Created by staja on 4/6/2018.
 */

public class ExecutiveOrderActivity extends AppCompatActivity {

    private final static String TAG = ExecutiveOrderActivity.class.getName();

    Intent pickledRick;
    int playerID;
    int statusID;
    JSONObject selectionList;

    TextView generalAnnouncement;
    TextView presidentAnnouncement;
    Spinner playerSelectionList;
    Button confirmationButton;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_executive_action);

        generalAnnouncement = findViewById(R.id.text_exec_act_announcement);
        presidentAnnouncement = findViewById(R.id.text_exec_president_headline);
        playerSelectionList = findViewById(R.id.spinner_exec_selection);
        confirmationButton = findViewById(R.id.button_exec_confirmation);

        presidentAnnouncement.setVisibility(View.GONE);
        playerSelectionList.setVisibility(View.GONE);
        confirmationButton. setVisibility(View.GONE);

        // call to server, if wait, call pollServer()
        // else, assign flavor text, show all, wait for input, then pollServer()
    }

    public void showPresidentSelectionItems(JSONObject data) {
        presidentAnnouncement.setVisibility(View.VISIBLE);
        playerSelectionList.setVisibility(View.VISIBLE);
        confirmationButton. setVisibility(View.VISIBLE);
        ArrayAdapter<String> playerList = new ArrayAdapter<>(getBaseContext(), android.R.layout.simple_spinner_item, getPlayerNameArray(data));
        playerList.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        playerSelectionList.setAdapter(playerList);
        confirmationButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // sendExecConfirmation()
                new sendExecutiveOrder().execute(playerSelectionList.getSelectedItem().toString());
            }
        });
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
    String setNominee(String selectedName) {
        Iterator<String> array = selectionList.keys();
        String check = null;
        String idNumber = null;
        while (array.hasNext()) {
            idNumber = array.next();
            try {
                Log.i(TAG, "setNominee: in loop, checking: " + idNumber + "-" + selectionList.getString(idNumber));
            } catch (JSONException e) {
                e.printStackTrace();
            }
            try {
                check = selectionList.getString(idNumber);
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

    public class initialServerCall extends AsyncTask<Void, Void, JSONObject> {

        @Override
        protected JSONObject doInBackground(Void... voids) {
            JSONObject response = null;
            switch (statusID) {
                case 17: // Summary Execution...
                    try {
                        response = NetworkUtilities.getJSONFromUrl(NetworkUtilities.buildUrl("client/president_execute/" + String.valueOf(playerID)));
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    break;
                case 15: // Special Election...
                    try {
                        response = NetworkUtilities.getJSONFromUrl(NetworkUtilities.buildUrl("client/president_special_election/" + String.valueOf(playerID)));
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    break;
                case 14: // Affiliation Investigation...
                    try {
                        response = NetworkUtilities.getJSONFromUrl(NetworkUtilities.buildUrl("client/president_party_peek/" + String.valueOf(playerID)));
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    break;
                default:
                    Log.e(TAG, "doInBackground: this shouldn't be happening, switch in initialServerCall()");
            }
            if (response == null) {
                Log.e(TAG, "doInBackground: JSON data from server null");
            }
            selectionList = response;
            return response;
        }

        @Override
        protected void onPostExecute(JSONObject jsonObject) {
            super.onPostExecute(jsonObject);
            try {
                if (jsonObject.getString("wait").isEmpty()) {
                    showPresidentSelectionItems(jsonObject);
                } else {
                    try {
                        generalAnnouncement.setText(jsonObject.getString("information"));
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    new pollServer().execute();
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }

    public class sendExecutiveOrder extends AsyncTask<String, Void, Boolean> {

        @Override
        protected Boolean doInBackground(String... strings) {
            String response = null;
            switch (statusID) {
                case 17: // Summary Execution...
                    try {
                        response = NetworkUtilities.getResponseFromHttpUrl(NetworkUtilities.buildUrl("client/president_execute_order/" + String.valueOf(playerID) + "/" + setNominee(strings[0])));
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    break;
                case 15: // Special Election...
                    try {
                        response = NetworkUtilities.getResponseFromHttpUrl(NetworkUtilities.buildUrl("client/president_special_election_order/" + String.valueOf(playerID) + "/" + setNominee(strings[0])));
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    break;
                case 14: // Affiliation Investigation...
                    try {
                        response = NetworkUtilities.getResponseFromHttpUrl(NetworkUtilities.buildUrl("client/president_party_peek_order/" + String.valueOf(playerID) + "/" + setNominee(strings[0])));
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    break;
                default:
                    Log.e(TAG, "doInBackground: this shouldn't be happening, switch in sendExecutiveOrder()");
            }
            return !(response == null);
        }

        @Override
        protected void onPostExecute(Boolean aBoolean) {
            super.onPostExecute(aBoolean);
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
                        if (received.getInt("statusID") != 17 && received.getInt("statusID") != 15 && received.getInt("statusID") != 14) {
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
                intent = statusActivityDispatcher.statusRouter(ExecutiveOrderActivity.this, jsonObject.getInt("statusID"));
            } catch (JSONException e) {
                e.printStackTrace();
            }
            Intent cookedIntent = ViewHandlingUtility.storeStatusInformation(intent, jsonObject);
            startActivity(cookedIntent);
        }
    }
}
