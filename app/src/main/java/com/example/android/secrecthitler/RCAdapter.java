package com.example.android.secrecthitler;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.List;

/**
 * Created by staja on 4/3/2018.
 */

public class RCAdapter extends RecyclerView.Adapter<RCAdapter.PlayerListHolder> {

    private final static String TAG = RCAdapter.class.getName();

    private int displayCount;
    private static String[] playerNameList;

    public RCAdapter(int numberOfItems) {
        displayCount = playerNameList.length;
    }
    @NonNull
    @Override
    public PlayerListHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        int listLayoutId = R.layout.component_player_selection;
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View view = inflater.inflate(listLayoutId, parent, false);
        PlayerListHolder listItem = new PlayerListHolder(view);
        return listItem;
    }

    @Override
    public void onBindViewHolder(@NonNull PlayerListHolder holder, int position) {
        holder.bind(playerNameList[position]);
    }


    @Override
    public int getItemCount() {
        return displayCount;
    }

    class PlayerListHolder extends RecyclerView.ViewHolder implements View.OnClickListener{

        TextView playerNameView;

        public PlayerListHolder(View itemView) {
            super(itemView);

            playerNameView = (TextView) itemView.findViewById(R.id.text_player_name_list);
        }

        void bind(String playerName) {
            playerNameView.setText(playerName);
        }

        @Override
        public void onClick(View view) {
            TextView casted = (TextView) view;
            Log.d(TAG, "onClick: scrollview item clicked, name - " + casted.getText().toString());
            NominationActivity.setNominee((String) casted.getText());
        }
    }

    public static void setPlayerNameList(String[] names) {
        playerNameList = names;
    }
}
