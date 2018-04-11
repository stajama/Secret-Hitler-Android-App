package com.example.android.secrecthitler;

import android.content.Context;
import android.content.Intent;

/**
 * Created by staja on 3/28/2018.
 */

public class statusActivityDispatcher {

    public static Intent statusRouter(Context context, int statusID) {
        switch (statusID) {
            case 2:
                return new Intent(context, JoinGameActivity.class);

            case 3:
                return new Intent(context, NominationActivity.class);

            case 6:
                return new Intent(context, VotingActivity.class);

            case 98:
                return new Intent(context, VotingResultActivity.class);

            case 99:
                return new Intent(context, VotingResultActivity.class);

            case 9:
                return new Intent(context, PolicySelection.class);

            case 10:
                return new Intent(context, PolicySelection.class);

            case 11:
                return new Intent(context, PolicySelection.class);

            case 12:
                return new Intent(context, PolicySelection.class);

            case 97:
                return new Intent(context, PolicyResultActivity.class);

            case 14:
                return new Intent(context, NominationActivity.class);

            case 15:
                return new Intent(context, NominationActivity.class);
            case 17:
                return new Intent(context, NominationActivity.class);
            case 16:
                return new Intent(context, PeekActivity.class);
            case 100:
                return new Intent(context, GameOverActivity.class);
            case 13:
                return new Intent(context, PolicyResultActivity.class);
            default:
                return new Intent();
        }
    }
}
