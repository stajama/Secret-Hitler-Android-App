package com.example.android.secrecthitler;

import android.content.Intent;
import android.nfc.Tag;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.PersistableBundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

/**
 * Created by staja on 3/28/2018.
 */

public class JoinGameActivity extends AppCompatActivity {

    final static String TAG = JoinGameActivity.class.getName();

    EditText nameBox;
    TextView errorText;
    Button joinGame;
    TextView waitingText;
    ProgressBar waitingBar;

    int playerID;
    static int currentStatus;
    JSONObject statusInfo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_joining);

        if (savedInstanceState != null) {
            playerID = savedInstanceState.getInt("statusID");
        }

        nameBox = findViewById(R.id.editbox_joining_name_input);
        errorText = findViewById(R.id.text_cant_join);
        joinGame = findViewById(R.id.button_joining_accept_name);
        waitingText = findViewById(R.id.text_waiting);
        waitingBar = findViewById(R.id.progbar_waiting);

        errorText.setVisibility(View.INVISIBLE);
        waitingText.setVisibility(View.INVISIBLE);
        waitingBar.setVisibility(View.INVISIBLE);

        joinGame.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String nameText;
                int idNumber;
                nameText = NetworkUtilities.checkForSpaces(nameBox.getText().toString());
                Log.i(TAG, "onClick: name going in URL :" + nameText);
                if (playerID == 0) {
                    new joinTheGame().execute(nameText);
                }
                Log.i(joinTheGame.class.getName(), "onClick: post joinTheGame()");
                errorText.setVisibility(View.GONE);
                waitingBar.setVisibility(View.VISIBLE);
                waitingText.setVisibility(View.VISIBLE);
                new pollServer().execute();
            }
        });
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
//        logAndAppend("onSaveInstanceState");
        outState.putInt("playerID", playerID);
    }

    public class joinTheGame extends AsyncTask<String, Void, Integer> {

        @Override
        protected Integer doInBackground(String... strings) {
            Integer idNumber = null;
            try {
                idNumber = Integer.parseInt(NetworkUtilities.getResponseFromHttpUrl(NetworkUtilities.buildUrl("client/join_game/" + strings[0])));
            } catch (IOException e) {
                Log.e("joinGame", "doInBackground: Something wrong with name.");
            } catch (NumberFormatException e) {
                Log.e("joinGame", "doInBackground: game full?");
            }
            if (idNumber != null) {
                return idNumber;
            } else {
                return 0;
            }
        }

        @Override
        protected void onPostExecute(Integer integer) {
            super.onPostExecute(integer);
            if (integer == 0) {
                errorText.setVisibility(View.VISIBLE);
            } else {
                playerID = integer;
            }
        }
    }

    public class pollServer extends AsyncTask<Integer, Boolean, Intent> {

        @Override
        protected void onProgressUpdate(Boolean... values) {
            if (!values[0]) {
                waitingBar.setVisibility(View.VISIBLE);
                waitingText.setVisibility(View.VISIBLE);
            } else {
                waitingBar.setVisibility(View.GONE);
                waitingText.setVisibility(View.INVISIBLE);
            }
        }


        @Override
        protected Intent doInBackground(Integer... integers) {
            Log.i(TAG, "doInBackground: pollServer() start");
            statusInfo = null;
            Intent intent;
            while (true) {
                Log.i(TAG, "doInBackground: pollServer() start of while loop.");
                try {
                    statusInfo = NetworkUtilities.getJSONFromUrl(NetworkUtilities.buildUrl("client/client_status/" + String.valueOf(playerID)));
                } catch (IOException e) {
                    e.printStackTrace();
                }
                if (statusInfo != null) {
                    try {
                        Log.i(TAG, "doInBackground: pollServer() in while loop, See statusID" + statusInfo.getString("statusID"));
                        if (statusInfo.getInt("statusID") != 2) {
                            Log.i(TAG, "doInBackground: whats coming back: " + statusInfo.toString());
                            intent = statusActivityDispatcher.statusRouter(JoinGameActivity.this, Integer.parseInt(statusInfo.getString("statusID")));
                            return intent;

                    } else {
                        publishProgress(false);
                    }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }
        }

        @Override
        protected void onPostExecute(Intent intent) {
            Log.i(JoinGameActivity.class.getName(), "onPostExecute: pollServer() complete, status should not be 2 -- " + intent.toString());
            Intent cookedIntent = ViewHandlingUtility.storeStatusInformation(intent, statusInfo);
            if (cookedIntent == null) {
                Log.e(TAG, "onPostExecute: cookintent is somehow null... Fuck I hate Java...");
            }
            startActivity(cookedIntent);
        }
    }

}