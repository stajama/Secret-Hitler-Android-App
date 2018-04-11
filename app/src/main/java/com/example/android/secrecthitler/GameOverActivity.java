package com.example.android.secrecthitler;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.Iterator;

/**
 * Created by staja on 3/28/2018.
 */

public class GameOverActivity extends AppCompatActivity {

    JSONObject allinformation;

    TextView winners;
    TextView cause;
    RecyclerView allInfo;
    Button playAgainButton;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game_over);

        winners = findViewById(R.id.text_over_winner);
        cause = findViewById(R.id.text_over_win_clause);
        allInfo = findViewById(R.id.recycler_game_over);
        playAgainButton = findViewById(R.id.button_over_play_again);

        new getInfo().execute();

    }

    public class getInfo extends AsyncTask<Void, Void, Boolean> {

        @Override
        protected Boolean doInBackground(Void... voids) {
            allinformation = null;
            try {
                allinformation = NetworkUtilities.getJSONFromUrl(NetworkUtilities.buildUrl("client/game_over"));
            } catch (IOException e) {
                e.printStackTrace();
            }
            return !(allinformation == null);
        }

        @Override
        protected void onPostExecute(Boolean aBoolean) {
            super.onPostExecute(aBoolean);
            try {
                winners.setText(allinformation.getString("whatHappened"));
            } catch (JSONException e) {
                e.printStackTrace();
            }
            try {
                cause.setText("Liberal Policies: " + allinformation.getString("libPolicyCount") + " - Fascist Policies: " + allinformation.getString("fasPolicyCount"));
            } catch (JSONException e) {
                e.printStackTrace();
            }
            RCAdapterNoClick rcAdapter = new RCAdapterNoClick(100);
            try {
                rcAdapter.setPlayerNameList(getDataArray(allinformation));
            } catch (JSONException e) {
                e.printStackTrace();
            }
            playAgainButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    startActivity(new Intent(GameOverActivity.this, MainActivity.class));
                }
            });
        }
    }


    String[] getDataArray(JSONObject data) throws JSONException{
        String[] returnList = new String[data.length() - 3];
        int index = 0;
        Iterator<String> keys = data.keys();
        String check;
        while (keys.hasNext()) {
            check = keys.next();
            if (check.equals("fasPolicyCount") || check.equals("whatHappened") || check.equals("libPolicyCount")) {
                continue;
            } else {
                returnList[index++] = data.getString(check);
            }
        }
        return returnList;
    }
}

