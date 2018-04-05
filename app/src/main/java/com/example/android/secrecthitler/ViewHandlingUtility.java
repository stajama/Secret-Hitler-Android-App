package com.example.android.secrecthitler;

import android.content.Context;
import android.util.Log;
import android.widget.ScrollView;
import android.widget.TextView;

import org.json.JSONException;
import org.json.JSONObject;

import android.support.v7.app.AppCompatActivity;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.PersistableBundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by staja on 3/29/2018.
 */

public class ViewHandlingUtility extends AppCompatActivity{

    private static final String TAG = ViewHandlingUtility.class.getName();



    public static Intent storeStatusInformation(Intent intent, JSONObject data) {
        Log.w(TAG, "storeStatusInformation: check json data received. " + data.toString());
        try {
            intent.putExtra("livingPlayerCount", data.getInt("livingPlayerCount"));
            Log.i(TAG, "storeStatusInformation: livingPlayerCount set.");
        } catch (JSONException e) {
            Log.e(TAG, "storeStatusInformation: JSON to Intent data transferred failed");
        }
        try {
            intent.putExtra("id", data.getInt("id"));
            Log.i(TAG, "storeStatusInformation: id set.");
        } catch (JSONException e) {
            Log.e(TAG, "storeStatusInformation: JSON to Intent data transferred failed");
        }
        try {
            intent.putExtra("name", data.getString("name"));
            Log.i(TAG, "storeStatusInformation: name set.");
        } catch (JSONException e) {
            Log.e(TAG, "storeStatusInformation: JSON to Intent data transferred failed");
            }
        try {
            intent.putExtra("party", data.getString("party"));
            Log.i(TAG, "storeStatusInformation: party set.");
        } catch (JSONException e) {
            Log.e(TAG, "storeStatusInformation: JSON to Intent data transferred failed");
        }
        try {
            intent.putExtra("role", data.getString("role"));
            Log.i(TAG, "storeStatusInformation: role set.");
        } catch (JSONException e) {
            Log.e(TAG, "storeStatusInformation: JSON to Intent data transferred failed");
        }
        try {
            intent.putExtra("alive", data.getBoolean("alive"));
            Log.i(TAG, "storeStatusInformation: alive set.");
        } catch (JSONException e) {
            Log.e(TAG, "storeStatusInformation: JSON to Intent data transferred failed");
        }
        try {
            intent.putExtra("statusID", data.getInt("statusID"));
            Log.i(TAG, "storeStatusInformation: statusID set.");
        } catch (JSONException e) {
            Log.e(TAG, "storeStatusInformation: JSON to Intent data transferred failed");
        }
        try {
            intent.putExtra("statusUpdate", data.getString("statusUpdate"));
            Log.i(TAG, "storeStatusInformation: statusUpdate set. = " + data.getString("statusUpdate"));
        } catch (JSONException e) {
            Log.e(TAG, "storeStatusInformation: JSON to Intent data transferred failed");
        }
        try {
            intent.putExtra("libPolicies", data.getInt("libPolicies"));
            Log.i(TAG, "storeStatusInformation: lib set.");
        } catch (JSONException e) {
            Log.e(TAG, "storeStatusInformation: JSON to Intent data transferred failed");
        }
        try {
            intent.putExtra("fasPolicies", data.getInt("fasPolicies"));
            Log.i(TAG, "storeStatusInformation: fas set.");
        } catch (JSONException e) {
            Log.e(TAG, "storeStatusInformation: JSON to Intent data transferred failed");
        }
        try {
            intent.putExtra("electionTracker", data.getInt("electionTracker"));
            Log.i(TAG, "storeStatusInformation: et set.");

        } catch (JSONException e) {
            Log.e(TAG, "storeStatusInformation: JSON to Intent data transferred failed");
        }
        return intent;
    }


    public static ArrayList<String> arrayOfStrings(JSONObject data, int listSize) {
        ArrayList<String> returnList = new ArrayList<String>(listSize);
        int index = 0;
        while (data.keys().hasNext()) {
            String nextItem = data.keys().next();
            if (!nextItem.equals("information") && !nextItem.equals("wait")) {
                try {
                    returnList.set(index++, data.getString(nextItem));
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }
        return returnList;

    }
}
