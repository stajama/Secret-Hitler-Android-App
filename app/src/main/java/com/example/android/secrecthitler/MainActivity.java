package com.example.android.secrecthitler;

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

import java.io.IOException;

public class MainActivity extends AppCompatActivity {

    EditText numOfPlayers;
    Button startGame;
    Button joinGame;
    TextView errorText;
    Button confirmationButton;
    TextView canJoin;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        numOfPlayers = (EditText) findViewById(R.id.editbox_number_of_players);
        startGame = (Button) findViewById(R.id.button_new_game_start);
        joinGame = (Button) findViewById(R.id.button_new_game_join);
        errorText = (TextView) findViewById(R.id.text_main_error);
        confirmationButton = (Button) findViewById(R.id.button_main_confirmation);
        canJoin = (TextView) findViewById(R.id.text_join_good);

        errorText.setVisibility(View.INVISIBLE);
        canJoin.setVisibility(View.INVISIBLE);
        startGame.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int check;
                try {
                    check = Integer.parseInt(String.valueOf(numOfPlayers.getText()));
                } catch (NumberFormatException e) {
                    errorText.setText(R.string.initialize_count_error);
                    errorOccurred();
                    return;
                }
                if (check >= 5 && check <= 10) {
                    errorOff();
                    new spinUpGame().execute("setup/0/" + String.valueOf(numOfPlayers.getText()));
                } else {
                    errorText.setText(R.string.initialize_count_error);
                    errorOccurred();
                }
            }
        });
        joinGame.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Class goingTo = JoinGameActivity.class;
                Intent intent = new Intent(MainActivity.this, goingTo);
                startActivity(intent);
            }
        });
        confirmationButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int check;
                try {
                    check = Integer.parseInt(String.valueOf(numOfPlayers.getText()));
                } catch (NumberFormatException e) {
                    errorText.setText(R.string.initialize_count_error);
                    errorOccurred();
                    return;
                }
                if (check >= 5 && check <= 10) {
                    errorOff();
                    new spinUpGame().execute("setup/1/" + String.valueOf(numOfPlayers.getText()));
                } else {
                    errorText.setText(R.string.initialize_count_error);
                    errorOccurred();
                }
            }
        });

    }

    public void errorOccurred() {
        errorText.setVisibility(View.VISIBLE);
        confirmationButton.setVisibility(View.INVISIBLE);
    }

    public void errorOff() {
        errorText.setVisibility(View.INVISIBLE);
        confirmationButton.setVisibility(View.INVISIBLE);
    }

    public void gameInProgress() {
        errorText.setText(R.string.game_in_progress);
        errorOccurred();
        confirmationButton.setVisibility(View.VISIBLE);
    }

    public class spinUpGame extends AsyncTask<String, Void, String> {

        @Override
        protected String doInBackground(String... strings) {
            String serverAnswer = null;
            Log.i("mainJava", "doInBackground: start");
            try {
                serverAnswer = NetworkUtilities.getResponseFromHttpUrl(NetworkUtilities.buildUrl(strings[0]));
            } catch (IOException e) {
                Log.i("mainJava", "doInBackground: error in server contact");
                e.printStackTrace();
            }
            if (serverAnswer != null) {
                Log.i("mainJava", "doInBackground: received - " + serverAnswer);
                return serverAnswer;
            } else {
                Log.i("mainJava", "doInBackground: end - no response received.");
                return "error";
            }
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            Log.i("mainJava", "onPostExecute: start - s = " + s);
            if (s.equals("error")) {
                Log.i("mainJava", "onPostExecute: following error protocol");
                errorText.setText("An error has occurred, please try again.");
                errorText.setVisibility(View.VISIBLE);
            } else {
                if (s.equals("confirm")) {
                    Log.i("mainJava", "onPostExecute: received the go ahead");
                    canJoin.setVisibility(View.VISIBLE);
                } else {
                    Log.i("mainJava", "onPostExecute: received a game in progress warning.");
                    gameInProgress();
                }
            }
        }
    }
}
