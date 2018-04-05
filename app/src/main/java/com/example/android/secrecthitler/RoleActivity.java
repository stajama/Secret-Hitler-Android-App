package com.example.android.secrecthitler;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.PersistableBundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.ScrollView;
import android.widget.TextView;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.Iterator;

/**
 * Created by staja on 3/29/2018.
 */

public class RoleActivity extends AppCompatActivity {

    final static String TAG = RoleActivity.class.getName();
    RecyclerView mainScroll;
    int playerID;
    JSONObject roleInformation;
    String[] roleInformationArray;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        playerID = getIntent().getIntExtra("id", 0);
        Log.i(TAG, "onCreate: start");
        setContentView(R.layout.activity_role_information);
        Intent persistentInfo = getIntent();
        int idNumber = persistentInfo.getIntExtra("id", 0);
        mainScroll = (RecyclerView) findViewById(R.id.recycler_role_info);
        Log.i(TAG, "onCreate: everything set, starting asynctask");
        new getRoleInfo().execute(idNumber);

        // get data Async task
        // call populateScrollView with data to fill scrollview
    }
    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
//        logAndAppend("onSaveInstanceState");
        outState.putInt("playerID", playerID);
    }
    public class getRoleInfo extends AsyncTask<Integer, Void, JSONObject> {

        @Override
        protected JSONObject doInBackground(Integer... integers) {
            Log.i(TAG, "doInBackground: start of aSync");
            JSONObject dataComingIn = null;
            try {
                dataComingIn = NetworkUtilities.getJSONFromUrl(NetworkUtilities.buildUrl("client/client_status/" + String.valueOf(integers[0])));
            } catch (IOException e) {
                e.printStackTrace();
            }
            Log.i(TAG, "doInBackground: call successful, going to postExecute");
            return dataComingIn;
        }

        @Override
        protected void onPostExecute(JSONObject jsonObject) {
            Log.i(TAG, "onPostExecute: start of onPostExecute()");
            roleInformationArray = getDataArray(jsonObject);
            LinearLayoutManager layoutManager = new LinearLayoutManager(RoleActivity.this);
            mainScroll.setLayoutManager(layoutManager);
            mainScroll.setHasFixedSize(true);
            RCAdapterNoClick.setPlayerNameList(roleInformationArray);
            RCAdapterNoClick rcAdapter = new RCAdapterNoClick(100);
            mainScroll.setAdapter(rcAdapter);
            Log.d(TAG, "onPostExecute: finished executing.");
        }
    }

    String[] getDataArray(JSONObject data) {
        String[] returnList = new String[7];
        if (data != null) {
            try {
                returnList[0] = "Current Phase: " + data.getString("statusUpdate");
            } catch (JSONException e) {
                e.printStackTrace();
            }
            try {
                returnList[1] = "Name: " + data.getString("name");
            } catch (JSONException e) {
                e.printStackTrace();
            }
            try {
                if (data.getBoolean("alive")) {
                    returnList[2] = "You are still alive.";
                } else {
                    returnList[2] = "You have been executed.";

                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
            try {
                returnList[3] = "Your party: " + data.getString("party");
            } catch (JSONException e) {
                e.printStackTrace();
            }
            try {
                returnList[4] = "Your role: " + data.getString("role");
            } catch (JSONException e) {
                e.printStackTrace();
            }
            if (data.has("Other Fascists")) {
                try {
                    returnList[5] = "Your Fellow Fascists: " + data.getString("Other Fascists");
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
            if (data.has("Hitler")) {
                try {
                    returnList[6] = "Hitler is - " + data.getString("Hitler");
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }
        return returnList;
    }
}
