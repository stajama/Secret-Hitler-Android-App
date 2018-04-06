package com.example.android.secrecthitler;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

/**
 * Created by staja on 3/28/2018.
 */

public class PolicySelection extends AppCompatActivity {

    private final static String TAG = PolicySelection.class.getName();

    Intent pickledRick;
    int playerID;
    int statusID;
    int selectedView;

    Toast mToast;
    LinearLayout presidentPolicyBox;
    LinearLayout chancellorPolicyBox;
    LinearLayout presidentVetoBox;
    Button pPolicy1;
    Button pPolicy2;
    Button pPolicy3;
    Button cPolicy1;
    Button cPolicy2;
    Button cVeto;
    Button pVetoPos;
    Button pVetoNeg;
    Button presidentConfirmation;
    Button chancellorConfirmation;
    TextView flavorText;
    TextView pFlavorText;
    TextView cFlavorText;
    TextView pVetoFlavorText;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_policy_selection);

        pickledRick = getIntent();
        playerID = pickledRick.getIntExtra("id", 0);
        statusID = pickledRick.getIntExtra("statusID", 0);

        presidentPolicyBox = findViewById(R.id.ll_policy_president_selection_boxes);
        chancellorPolicyBox = findViewById(R.id.ll_policy_chancellor_selection_grid);
        presidentVetoBox = findViewById(R.id.ll_policy_president_veto_grid);
        pPolicy1 = findViewById(R.id.button_policy_president_1);
        pPolicy2 = findViewById(R.id.button_policy_president_2);
        pPolicy3 = findViewById(R.id.button_policy_president_3);
        cPolicy1 = findViewById(R.id.button_policy_chancellor_1);
        cPolicy2 = findViewById(R.id.button_policy_chancellor_2);
        cVeto = findViewById(R.id.button_policy_chancellor_veto);
        pVetoPos = findViewById(R.id.button_policy_president_veto_yes);
        pVetoNeg = findViewById(R.id.button_policy_president_veto_no);
        presidentConfirmation = findViewById(R.id.button_policy_president_confirm);
        chancellorConfirmation = findViewById(R.id.button_policy_chancellor_confirm);
        flavorText = findViewById(R.id.text_policy_flavor_text);
        pFlavorText = findViewById(R.id.text_policy_president_flavor);
        cFlavorText = findViewById(R.id.text_policy_chancellor_flavor);
        pVetoFlavorText = findViewById(R.id.text_policy_veto_flavor_text);

        Log.i(TAG, "onCreate: Everything is assigned. Configuring activity basics...");
        presidentPolicyBox.setVisibility(View.GONE);
        presidentConfirmation.setVisibility(View.GONE);
        presidentVetoBox.setVisibility(View.GONE);
        pVetoFlavorText.setVisibility(View.GONE);
        pFlavorText.setVisibility(View.GONE);
        chancellorPolicyBox.setVisibility(View.GONE);
        chancellorConfirmation.setVisibility(View.GONE);
        cFlavorText.setVisibility(View.GONE);

        flavorText.setText(pickledRick.getStringExtra("statusUpdate"));

        Log.i(TAG, "onCreate: Basic setup complete");

        Log.i(TAG, "onCreate: starting initialServerCall()");
        new initialServerCall().execute();
    }

    public void showPolicySelectionPresident(JSONObject jsonObject) throws JSONException {
        presidentPolicyBox.setVisibility(View.VISIBLE);
        presidentConfirmation.setVisibility(View.VISIBLE);
        pFlavorText.setVisibility(View.VISIBLE);
        pFlavorText.setText(jsonObject.getString("information"));
        Log.i(TAG, "showPolicySelectionPresident: all presidential initial selection views should be visible, only card choice not yet populated.");
        pPolicy1.setText(jsonObject.getString("1"));
        pPolicy1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                pPolicy2.setClickable(true);
                pPolicy3.setClickable(true);
                pPolicy1.setClickable(false);
                selectedView = view.getId();
            }
        });
        pPolicy2.setText(jsonObject.getString("2"));
        pPolicy2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                pPolicy1.setClickable(true);
                pPolicy3.setClickable(true);
                pPolicy2.setClickable(false);
                selectedView = view.getId();
            }
        });
        pPolicy3.setText(jsonObject.getString("3"));
        pPolicy3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                pPolicy1.setClickable(true);
                pPolicy2.setClickable(true);
                pPolicy3.setClickable(false);
                selectedView = view.getId();
            }
        });
        presidentConfirmation.setVisibility(View.VISIBLE);
        presidentConfirmation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (selectedView != 0) {
                    new presidentPolicySend().execute();
                } else {
                    showErrorToast();
                }
            }
        });
    }

    public void showChancellorPolicySelection(JSONObject jsonObject) throws JSONException{
        cFlavorText.setText(jsonObject.getString("information") + " " + jsonObject.getString("vetoText"));
        cFlavorText.setVisibility(View.VISIBLE);
        chancellorPolicyBox.setVisibility(View.VISIBLE);
        cPolicy1.setText(jsonObject.getString("1"));
        cPolicy1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                selectedView = view.getId();
                cPolicy2.setClickable(true);
                cPolicy1.setClickable(false);
                cVeto.setClickable(true);
            }
        });
        cPolicy2.setText(jsonObject.getString("2"));
        cPolicy2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                selectedView = view.getId();
                cPolicy1.setClickable(true);
                cPolicy2.setClickable(false);
                cVeto.setClickable(true);
            }
        });
        if (jsonObject.getBoolean("veto")){
            cVeto.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    selectedView = view.getId();
                    cPolicy1.setClickable(true);
                    cPolicy2.setClickable(true);
                    cVeto.setClickable(false);
                }
            });
        }
        chancellorConfirmation.setVisibility(View.VISIBLE);
        chancellorConfirmation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (selectedView != 0) {
                    new chancellorPolicySend().execute();
                } else {
                    showErrorToast();
                }
            }
        });
    }

    public void showPresidentVetoChoice(JSONObject jsonObject) throws JSONException{
        pVetoFlavorText.setVisibility(View.VISIBLE);
        pVetoFlavorText.setText(jsonObject.getString("information"));
        presidentVetoBox.setVisibility(View.VISIBLE);
        pVetoPos.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                new presidentVetoSend().equals(true);
            }
        });
        pVetoPos.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                new presidentVetoSend().equals(false);
            }
        });
    }
    public class initialServerCall extends AsyncTask<Integer, Void, JSONObject> {
        @Override
        protected JSONObject doInBackground(Integer... integers) {
            JSONObject jsonObject = null;
            switch (statusID) {
                case 9:
                    try {
                        jsonObject = NetworkUtilities.getJSONFromUrl(NetworkUtilities.buildUrl("client/president_draw/" + String.valueOf(playerID)));
                    } catch (IOException e) {
                        Log.e(TAG, "doInBackground: check case 9, JSON error");
                    }
                    break;
                case 10:
                    try {
                        jsonObject = NetworkUtilities.getJSONFromUrl(NetworkUtilities.buildUrl("client/chancellor_draw/" + String.valueOf(playerID)));
                    } catch (IOException e) {
                        Log.e(TAG, "doInBackground: check case 10, JSON error");
                    }
                    break;
                case 11:
                    try {
                        jsonObject = NetworkUtilities.getJSONFromUrl(NetworkUtilities.buildUrl("client/president_veto/" + String.valueOf(playerID)));
                    } catch (IOException e) {
                        Log.e(TAG, "doInBackground: check case 11, JSON error");
                    }
                    break;
                case 12:
                    try {
                        jsonObject = NetworkUtilities.getJSONFromUrl(NetworkUtilities.buildUrl("client/chancellor_draw/" + String.valueOf(playerID)));
                    } catch (IOException e) {
                        Log.e(TAG, "doInBackground: check case 12, JSON error");
                    }
                    break;
            }
            return jsonObject;

        }

        @Override
        protected void onPostExecute(JSONObject jsonObject) {
            Log.i(TAG, "onPostExecute: start");
            super.onPostExecute(jsonObject);
            try {
                Log.i(TAG, "onPostExecute: what came backk from initialServerCall()['wait']: " + jsonObject.getString("wait"));
                if (!jsonObject.getString("wait").isEmpty()) {
                    Log.i(TAG, "onPostExecute: shows to be empty, treating player as non-president/non-chancellor");
                    pFlavorText.setText("wait");
                    pFlavorText.setVisibility(View.VISIBLE);
                    new pollServer().execute();
                } else {
                    Log.i(TAG, "onPostExecute: player being treated as something");
                    if (jsonObject.getString("information").contains("Select a policy card to discard.")) {
                        Log.i(TAG, "onPostExecute: player is president, run initial selection function");
                        showPolicySelectionPresident(jsonObject);
                    } else if (jsonObject.getString("information").contains("Select a policy card to enact.")) {
                        Log.i(TAG, "onPostExecute: player is chancellor, cards received from president (or Veto), run initial selection function");
                        showChancellorPolicySelection(jsonObject);
                    } else if (jsonObject.getString("information").contains("has requested to veto the current set of policies.")) {
                        Log.i(TAG, "onPostExecute: player is president, will they veto or not...");
                        showPresidentVetoChoice(jsonObject);
                    } else {
                        Log.e(TAG, "onPostExecute: something wrong in non empty wait/role router switch");
                    }
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }

    public class presidentPolicySend extends AsyncTask<Void, Void, Boolean> {
        @Override
        protected Boolean doInBackground(Void... voids) {
            String[] card = new String[3];
            if (selectedView == pPolicy1.getId()) {
                card[0] = pPolicy2.getText().toString();
                card[1] = pPolicy3.getText().toString();
                card[2] = pPolicy1.getText().toString();
            } else if (selectedView == pPolicy2.getId()){
                card[0] = pPolicy1.getText().toString();
                card[1] = pPolicy3.getText().toString();
                card[2] = pPolicy2.getText().toString();
            } else if (selectedView == pPolicy3.getId()) {
                card[0] = pPolicy2.getText().toString();
                card[1] = pPolicy1.getText().toString();
                card[2] = pPolicy3.getText().toString();
            }
            String response = null;
            try {
                response = NetworkUtilities.getResponseFromHttpUrl(NetworkUtilities.buildUrl("client/president_play/" + String.valueOf(playerID) + "/" + card[0] + "/" + card[1] + "/" + card[2]));
            } catch (IOException e) {
                e.printStackTrace();
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
            new pollServer().execute();
        }
    }

    public class chancellorPolicySend extends AsyncTask<Boolean, Void, Boolean> {
        @Override
        protected Boolean doInBackground(Boolean... booleans) {
            String response = null;
            if (booleans[0]) { //chancellor has selected Veto button
                try {
                    response = NetworkUtilities.getResponseFromHttpUrl(NetworkUtilities.buildUrl("client/chancellor_veto/" + String.valueOf(playerID)));
                } catch (IOException e) {
                    e.printStackTrace();
                }
            } else {
                if (selectedView == cPolicy1.getId()) {
                    try {
                        response = NetworkUtilities.getResponseFromHttpUrl(NetworkUtilities.buildUrl("client/chancellor_play/" + String.valueOf(playerID) + "/" + cPolicy1.getText().toString() + "/" + cPolicy2.getText().toString()));
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                } else {
                    try {
                        response = NetworkUtilities.getResponseFromHttpUrl(NetworkUtilities.buildUrl("client/chancellor_play/" + String.valueOf(playerID) + "/" + cPolicy2.getText().toString() + "/" + cPolicy1.getText().toString()));
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
            return !(response == null);
        }

        @Override
        protected void onPostExecute(Boolean aBoolean) {
            super.onPostExecute(aBoolean);
            new pollServer().execute();
        }
    }

    public class presidentVetoSend extends AsyncTask<Boolean, Void, Boolean> {
        @Override
        protected Boolean doInBackground(Boolean... booleans) {
            String response = null;
            if (booleans[0]) {
                try {
                    response = NetworkUtilities.getResponseFromHttpUrl(NetworkUtilities.buildUrl("client/president_veto_choice/" + String.valueOf(playerID) + "/1"));
                } catch (IOException e) {
                    e.printStackTrace();
                }
            } else {
                try {
                    response = NetworkUtilities.getResponseFromHttpUrl(NetworkUtilities.buildUrl("client/president_veto_choice/" + String.valueOf(playerID) + "/0"));
                } catch (IOException e) {
                    e.printStackTrace();
                }
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
                        if (received.getInt("statusID") != statusID) {
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
                intent = statusActivityDispatcher.statusRouter(PolicySelection.this, jsonObject.getInt("statusID"));
            } catch (JSONException e) {
                e.printStackTrace();
            }
            Intent cookedIntent = ViewHandlingUtility.storeStatusInformation(intent, jsonObject);
            startActivity(cookedIntent);
        }
    }

    public void showErrorToast() {
        if (mToast != null) {
            mToast = new Toast(PolicySelection.this);
            mToast.setText("Please select an option.");
            mToast.setDuration(Toast.LENGTH_LONG);
            mToast.show();
        }
    }
}

/* In hind sight, it was probably really stupid to try to house all of this functionality under a
single activity/class. Splitting it up in the future will definitely improve readability, and may
improve performance and maintenance in the long run.
 */

// TODO president views seem ok, start testing chancellor and other view solutions. (See debugTests3.py)