package com.example.playcheck;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class AdapterGameListPlayer extends RecyclerView.Adapter<AdapterGameListPlayer.ViewHolder> {
    Context context;
    ArrayList<GameListPlayerInfo> list;

    public AdapterGameListPlayer(Context context, ArrayList<GameListPlayerInfo> list) {
        this.context = context;
        this.list = list;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.single_game_view,parent,false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) { //associates a ViewHolder with data
        GameListPlayerInfo game = list.get(position);
        holder.gameName.setText(game.getGameName());
        holder.gameDate.setText(game.getGameDate());
        holder.gameVenue.setText(game.getGameVenue());

    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder{
        TextView gameName, gameDate, gameVenue;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            // Connect the Java variables to the XML IDs
            gameName = itemView.findViewById(R.id.gameName);
            gameDate = itemView.findViewById(R.id.gameDate);
            gameVenue = itemView.findViewById(R.id.gameVenue);

        }
    }
}
