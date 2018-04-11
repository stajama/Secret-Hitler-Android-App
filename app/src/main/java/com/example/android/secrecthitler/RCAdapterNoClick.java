package com.example.android.secrecthitler;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

/**
 * Created by staja on 4/3/2018.
 */

public class RCAdapterNoClick extends RecyclerView.Adapter<RCAdapterNoClick.PlayerListHolder> {

    private final static String TAG = RCAdapterNoClick.class.getName();

    private int displayCount;
    private String[] informationList;

    public RCAdapterNoClick(int numberOfItems) {
        displayCount = numberOfItems;
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
        holder.bind(informationList[position]);
    }


    @Override
    public int getItemCount() {
        return displayCount;
    }

    class PlayerListHolder extends RecyclerView.ViewHolder{

        TextView dataPanel;

        public PlayerListHolder(View itemView) {
            super(itemView);

            dataPanel = (TextView) itemView.findViewById(R.id.text_player_name_list);
        }
        void bind(String playerName) {
            dataPanel.setText(playerName);
        }



    }

    public  void setPlayerNameList(String[] data) {
        informationList = data;
        displayCount = informationList.length;
    }

}
