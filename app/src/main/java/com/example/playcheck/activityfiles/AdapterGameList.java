package com.example.playcheck.activityfiles;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.playcheck.activityfiles.GameDetailsActivity;
import com.example.playcheck.R;
import com.example.playcheck.puremodel.Game;


import java.util.ArrayList;


/*
The AdapterGameList class is used to bind game information called from the Games class ArrayList to single_game_view layout.
 */

public class AdapterGameList extends RecyclerView.Adapter<AdapterGameList.ViewHolder> {
    Context context;
    ArrayList<Game> list; //contains information about all the games from Firebase


    public AdapterGameList(Context context, ArrayList<Game> list) {
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
        Game game = list.get(position);
        holder.teamA.setText(game.getTeamA());
        holder.teamB.setText(game.getTeamB());
        holder.gameDate.setText(game.getGameDateLongtoString(game.getGameDate()));
        holder.gameVenue.setText(game.getGameVenue());
        holder.gameType.setText(game.getGameType());

        if (game==null) {
            return;
        }

        if (holder.gameName != null) {
            holder.gameName.setText(game.getTeamA() + " vs " + game.getTeamB());
        }
        if (holder.gameDate != null) {
            holder.gameDate.setText(game.getGameDateLongtoString(game.getGameDate()));
        }
        if (holder.gameVenue != null) {
            holder.gameVenue.setText(game.getGameVenue());
        }

        if (holder.card != null) {
            holder.card.setOnClickListener(v -> {

                Intent intent = new Intent(v.getContext(), GameDetailsActivity.class);

                intent.putExtra("teamA", game.getTeamA());
                intent.putExtra("teamB", game.getTeamB());
                intent.putExtra("date", game.getGameDateLongtoString(game.getGameDate()));
                intent.putExtra("location", game.getGameVenue());
                intent.putExtra("gameType", game.getGameType());
                intent.putExtra("gameId", game.getGameId()); // Fixed: passing gameId string instead of gameDate long

// temporary demo data
                intent.putExtra("score", "3 - 2");
                intent.putExtra("teamAPlayers", "Mike\nTom\nSam");
                intent.putExtra("teamBPlayers", "Alex\nJake\nChris");
                intent.putExtra("referee", "John Smith");
                intent.putExtra("sportsmanship", "Team A: 4.5 | Team B: 4.2");

                v.getContext().startActivity(intent);
            });
        }

    }

    @Override
    public int getItemCount() {
        return list.size();
    } //# of games in the ArrayList of Games

    public static class ViewHolder extends RecyclerView.ViewHolder{ // contains references to a ViewHolder
        TextView gameName, gameDate, gameVenue, gameType, teamA, teamB;
        View card;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            // Connect the Java variables to the XML IDs
            teamA = itemView.findViewById(R.id.teamA);
            teamB = itemView.findViewById(R.id.teamB);
            card = itemView.findViewById(R.id.singlegame);
            gameDate = itemView.findViewById(R.id.gameDate);
            gameVenue = itemView.findViewById(R.id.gameVenue);
            gameType = itemView.findViewById(R.id.gameType);

        }
    }
}
