package com.example.playcheck.activityfiles;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.example.playcheck.database.GameLinkToDatabase;
import com.example.playcheck.database.TeamLinkToDatabase;
import com.example.playcheck.activityfiles.GameDetailsActivity;
import com.example.playcheck.R;
import com.example.playcheck.puremodel.Team;


import java.util.ArrayList;

/*This class is an adapter for the Team Standings */

public class AdapterTeamStandings extends RecyclerView.Adapter<AdapterTeamStandings.ViewHolder> {
    private ArrayList<Team> teamList;

    public AdapterTeamStandings(ArrayList<Team> teamList) {
        this.teamList = teamList;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.standings_list, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Team team = teamList.get(position);

        int rank = position + 1;
        holder.rankNumber.setText(String.valueOf(rank));
        holder.teamName.setText(team.getTeamName());
        holder.wins.setText(team.getTeamWins() + "W");
        holder.losses.setText(team.getTeamLosses() + "L");

        // Displays Team ranked #1 prominently
        if (rank == 1) {
            holder.rankBadge.setCardBackgroundColor(android.graphics.Color.parseColor("#F4D47A"));
            holder.rankNumber.setTextColor(android.graphics.Color.parseColor("#2C3E50"));
        } else {
            holder.rankBadge.setCardBackgroundColor(android.graphics.Color.parseColor("#E8E8E8"));
            holder.rankNumber.setTextColor(android.graphics.Color.parseColor("#7F8C8D"));
        }
    }

    @Override
    public int getItemCount() {
        return teamList.size();
    }
    
    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView rankNumber, teamName, wins, losses;
        CardView rankBadge;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            rankNumber = itemView.findViewById(R.id.rankNumber);
            teamName = itemView.findViewById(R.id.teamNameText);
            wins = itemView.findViewById(R.id.winsText);
            losses = itemView.findViewById(R.id.lossesText);
            rankBadge = itemView.findViewById(R.id.rankBadge);
        }
    }
}
