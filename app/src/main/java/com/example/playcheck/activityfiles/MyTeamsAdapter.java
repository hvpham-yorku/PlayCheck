package com.example.playcheck.activityfiles;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.example.playcheck.R;
import com.example.playcheck.puremodel.Team;
import java.util.ArrayList;
import java.util.Map;

public class MyTeamsAdapter extends RecyclerView.Adapter<MyTeamsAdapter.ViewHolder> {

    private ArrayList<Team> teamList;
    public MyTeamsAdapter(ArrayList<Team> teamList) {
        this.teamList = teamList;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.single_my_teams, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Team team = teamList.get(position);

        //Info that is always visible
        holder.teamName.setText(team.getTeamName());
        int count = team.getPlayers().size();
        holder.numPlayers.setText("Players: "+ count);

        //info that is displayed when card is clicked
        holder.wins.setText("Wins: " + team.getTeamWins());
        holder.losses.setText("Losses: " + team.getTeamLosses());
        if (team.getCaptain() != null && !team.getCaptain().isEmpty()) {
            String captainName = new ArrayList<>(team.getCaptain().values()).get(0);
            holder.captain.setText("Captain: " + captainName);
        } else {
            holder.captain.setText("Captain: Not Assigned");
        }

        //Setup nested RecyclerView for Players in a team
        if (team.getPlayers() != null) {
            ArrayList<String> playerNames = new ArrayList<>(team.getPlayers().values());
            AddedPlayersAdapter playerAdapter = new AddedPlayersAdapter(playerNames, false);
            holder.innerRecyclerView.setLayoutManager(new LinearLayoutManager(holder.itemView.getContext()));
            holder.innerRecyclerView.setAdapter(playerAdapter);
        }

        //Expand/Collapse Logic
        holder.itemView.setOnClickListener(v -> {
            if (holder.extraDetails.getVisibility() == View.GONE) {
                holder.extraDetails.setVisibility(View.VISIBLE);
                holder.clickHint.setText("Click to Hide Team Details");
            } else {
                holder.extraDetails.setVisibility(View.GONE);
                holder.clickHint.setText("Click to Show Team Details");
            }
        });
    }

    @Override
    public int getItemCount() {
        return teamList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView teamName, numPlayers, clickHint, captain, wins, losses;
        LinearLayout extraDetails;
        RecyclerView innerRecyclerView;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            teamName = itemView.findViewById(R.id.myTeamName);
            numPlayers = itemView.findViewById(R.id.numberOfPlayers);
            clickHint = itemView.findViewById(R.id.clickShowDetails);
            captain = itemView.findViewById(R.id.captainText);
            wins = itemView.findViewById(R.id.winsText);
            losses = itemView.findViewById(R.id.lossesText);
            extraDetails = itemView.findViewById(R.id.extraDetailsLayout);
            innerRecyclerView = itemView.findViewById(R.id.innerRecyclerView);
        }
    }


}