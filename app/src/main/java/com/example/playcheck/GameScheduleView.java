package com.example.playcheck;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

/**
 * Adapter for displaying games in schedule format
 * Shows date, time, teams, and location
 */
public class GameScheduleView extends RecyclerView.Adapter<GameScheduleView.ViewHolder> {

    private Context context;
    private ArrayList<GameListPlayerInfo> gamesList;

    public GameScheduleView(Context context, ArrayList<GameListPlayerInfo> gamesList) {
        this.context = context;
        this.gamesList = gamesList;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_game_schedule, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        GameListPlayerInfo game = gamesList.get(position);

        // TODO: Update these based on actual fields in GameListPlayerInfo
        // holder.txtDate.setText(formatDate(game.getGameDate()));
        // holder.txtTime.setText(formatTime(game.getGameTime()));
        // holder.txtTeams.setText(game.getHomeTeam() + " vs " + game.getAwayTeam());
        // holder.txtLocation.setText(game.getLocation());
        // holder.txtStatus.setText(game.getStatus());

        // Placeholder - replace with actual game data
        holder.txtDate.setText("March 15, 2024");
        holder.txtTime.setText("7:00 PM");
        holder.txtTeams.setText("Team A vs Team B");
        holder.txtLocation.setText("Gym 1");
        holder.txtStatus.setText("Upcoming");
    }

    @Override
    public int getItemCount() {
        return gamesList.size();
    }

    /**
     * Format timestamp to readable date
     */
    private String formatDate(long timestamp) {
        SimpleDateFormat sdf = new SimpleDateFormat("EEEE, MMMM dd, yyyy", Locale.getDefault());
        return sdf.format(new Date(timestamp));
    }

    /**
     * Format timestamp to readable time
     */
    private String formatTime(long timestamp) {
        SimpleDateFormat sdf = new SimpleDateFormat("h:mm a", Locale.getDefault());
        return sdf.format(new Date(timestamp));
    }

    /**
     * ViewHolder for game schedule items
     */
    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView txtDate;
        TextView txtTime;
        TextView txtTeams;
        TextView txtLocation;
        TextView txtStatus;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            txtDate = itemView.findViewById(R.id.txtGameDate);
            txtTime = itemView.findViewById(R.id.txtGameTime);
            txtTeams = itemView.findViewById(R.id.txtGameTeams);
            txtLocation = itemView.findViewById(R.id.txtGameLocation);
            txtStatus = itemView.findViewById(R.id.txtGameStatus);
        }
    }
}
