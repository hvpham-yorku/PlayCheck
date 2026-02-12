package com.example.playcheck;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

/*
The AdapterGameListPlayer class is used to bind game information called from the Games class ArrayList to single_game_view layout.
 */

public class AdapterGameListPlayer extends RecyclerView.Adapter<AdapterGameListPlayer.ViewHolder> {
    Context context;
    ArrayList<Games> list; //contains information about all the games from Firebase

    public AdapterGameListPlayer(Context context, ArrayList<Games> list) {
        this.context = context;
        this.list = list;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) { //called when a new game needs to be added to screen (RecycleView needs a new ViewHolder)
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.single_game_view,parent,false); //creates a new view and defines it's layout
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) { //Replaces the contents of ViewHolder's views with data
        Games game = list.get(position);
        holder.gameName.setText(game.getGameName());
        holder.gameDate.setText(game.getGameDate());
        holder.gameVenue.setText(game.getGameVenue());
        holder.gameType.setText(game.getGameType());

    }

    @Override
    public int getItemCount() {
        return list.size();
    } //# of games in the ArrayList of Games

    public static class ViewHolder extends RecyclerView.ViewHolder{ // contains references to a ViewHolder
        TextView gameName, gameDate, gameVenue, gameType;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            // Connect the Java variables to the XML IDs
            gameName = itemView.findViewById(R.id.gameName);
            gameDate = itemView.findViewById(R.id.gameDate);
            gameVenue = itemView.findViewById(R.id.gameVenue);
            gameType = itemView.findViewById(R.id.gameType);

        }
    }
}
