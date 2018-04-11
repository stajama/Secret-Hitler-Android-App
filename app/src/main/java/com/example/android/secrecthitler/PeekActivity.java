package com.example.android.secrecthitler;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import org.json.JSONException;
import org.json.JSONObject;
import org.w3c.dom.Text;

import java.io.IOException;

/**
 * Created by staja on 3/28/2018.
 */

public class PeekActivity extends AppCompatActivity {

    private final static String TAG = PeekActivity.class.getName();

    Intent pickledRick;
    int playerID;
    int statusID;

    TextView announcementPanel;
    TextView presidentText;
    LinearLayout presidentViewBox;
    Button policy1;
    Button policy2;
    Button policy3;
    Button confirmationButton;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_president_policy_peek);

        pickledRick = getIntent();
        playerID = pickledRick.getIntExtra("id", 0);
        statusID = pickledRick.getIntExtra("statusID", 0);

        announcementPanel = findViewById(R.id.text_peek_announcement);
        announcementPanel.setText(pickledRick.getStringExtra("statusUpdate"));
        presidentText = findViewById(R.id.text_peek_president_flavor);
        presidentViewBox = findViewById(R.id.ll_peek_policy_box);
        policy1 = findViewById(R.id.button_peek_policy1);
        policy2 = findViewById(R.id.button_peek_policy2);
        policy3 = findViewById(R.id.button_peek_policy3);
        confirmationButton = findViewById(R.id.button_peek_president_confirmation);

        presidentText.setVisibility(View.GONE);
        presidentViewBox.setVisibility(View.GONE);
        confirmationButton.setVisibility(View.GONE);

        // call to server, if president call showPresidentInfo()
    }

    public void showPresidentInfo(JSONObject data) throws JSONException {
        presidentText.setVisibility(View.VISIBLE);
        presidentText.setText(data.getString("information"));
        presidentViewBox.setVisibility(View.VISIBLE);
        policy1.setText(data.getString("1"));
        policy2.setText(data.getString("2"));
        policy3.setText(data.getString("3"));
        confirmationButton.setVisibility(View.VISIBLE);
        confirmationButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // sendPresidentConfirmation
                // onPostExecute will start pollServer()
            }
        });
    }

    public class initialServerCall extends AsyncTask<Void, Void, JSONObject> {
        @Override
        protected JSONObject doInBackground(Void... voids) {
            JSONObject response = null;
            try {
                response = NetworkUtilities.getJSONFromUrl(NetworkUtilities.buildUrl("client/president_policy_peek/" + String.valueOf(playerID)));
            } catch (IOException e) {
                e.printStackTrace();
            }
            return response;
        }

        @Override
        protected void onPostExecute(JSONObject jsonObject) {
            super.onPostExecute(jsonObject);
            try {
                if (jsonObject.getString("wait").isEmpty()) {
                    showPresidentInfo(jsonObject);
                } else {
                    new pollServer().execute();
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }

    public class sendPresidentConfirmation extends AsyncTask<Void, Void, Boolean> {

        @Override
        protected Boolean doInBackground(Void... voids) {
            String response = null;
            try {
                response = NetworkUtilities.getResponseFromHttpUrl(NetworkUtilities.buildUrl("client/president_policy_peek_confirm/" + String.valueOf(playerID)));
            } catch (IOException e) {
                e.printStackTrace();
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
                        if (received.getInt("statusID") != 16) {
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
                intent = statusActivityDispatcher.statusRouter(PeekActivity.this, jsonObject.getInt("statusID"));
            } catch (JSONException e) {
                e.printStackTrace();
            }
            Intent cookedIntent = ViewHandlingUtility.storeStatusInformation(intent, jsonObject);
            startActivity(cookedIntent);
        }
    }
}
