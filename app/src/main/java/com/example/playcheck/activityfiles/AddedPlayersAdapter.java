package com.example.playcheck.activityfiles;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.playcheck.R;

import java.util.ArrayList;

public class AddedPlayersAdapter extends RecyclerView.Adapter<AddedPlayersAdapter.ViewHolder> {

    private ArrayList<String> currentPlayers;
    private boolean isDeletable; //this will be used later when adding the 'remove player from team' feature

    public AddedPlayersAdapter(ArrayList<String> players, boolean isDeletable) {
        this.currentPlayers = players;
        this.isDeletable = isDeletable;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.activity_item_added_player, parent, false);

        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        holder.playerName.setText(currentPlayers.get(position));
    }

    @Override
    public int getItemCount() {
        return currentPlayers.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView playerName;

        public ViewHolder(View view) {
            super(view);
            playerName = view.findViewById(R.id.playerName);
        }
    }
}
