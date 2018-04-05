package com.example.android.secrecthitler;

import android.net.Uri;
import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Scanner;

/**
 * Created by staja on 3/28/2018.
 */

public class NetworkUtilities {

    private static final String TAG = NetworkUtilities.class.getName();

    public final static String SERVER_STATIC_URL = "https://secret-ravine-44641.herokuapp.com/sh/";

    public static URL buildUrl(String additional) {
        URL url = null;
        try {
            url = new URL(SERVER_STATIC_URL + additional);
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
        Log.i(TAG, "buildUrl: ");
        return url;
    }

    /**
     * This method returns the entire result from the HTTP response.
     *
     * @param url The URL to fetch the HTTP response from.
     * @return The contents of the HTTP response.
     * @throws IOException Related to network and stream reading
     */
    public static String getResponseFromHttpUrl(URL url) throws IOException {
        Log.i(TAG, "getResponseFromHttpUrl: start, url = " + url.toString());
        HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
        Log.i(TAG, "getResponseFromHttpUrl: start2, HttpURLConnection worked");
        try {
            InputStream in = urlConnection.getInputStream();
            Log.i(TAG, "getResponseFromHttpUrl: InputStream established successfully.");
            Scanner scanner = new Scanner(in);
            scanner.useDelimiter("\\A");
            Log.i(TAG, "getResponseFromHttpUrl: Scanner worked");
            boolean hasInput = scanner.hasNext();
            if (hasInput) {
                Log.i(TAG, "getResponseFromHttpUrl: successful connection complete.");
                return scanner.next();
            } else {
                return null;
            }
        } finally {
            urlConnection.disconnect();
        }
    }

    public static JSONObject getJSONFromUrl(URL url) throws IOException {
        HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
        String holdingPlace;
        try {
            InputStream in = urlConnection.getInputStream();

            Scanner scanner = new Scanner(in);
            scanner.useDelimiter("\\A");

            boolean hasInput = scanner.hasNext();
            if (hasInput) {
                holdingPlace = scanner.next();
            } else {
                holdingPlace = null;
                return new JSONObject();
            }
        } finally {
            urlConnection.disconnect();
        }

        try {
            JSONObject output = new JSONObject(holdingPlace);
            return output;
        } catch (JSONException e) {
            e.printStackTrace();
            return new JSONObject();
        }
    }

    public static String checkForSpaces(String input) {
        String output = "";
        for (int i = 0; i < input.length(); i++) {
            if (input.charAt(i) == ' ') {
                output = output + "_ ";
            } else {
                output = output + Character.toString(input.charAt(i));
            }
        }
        return output;
    }
}
