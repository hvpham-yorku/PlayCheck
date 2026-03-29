package com.example.playcheck.database;

import com.example.playcheck.puremodel.Tournament;
import java.util.concurrent.CompletableFuture;

/**
 * Link to TournamentDatabase
 */
public class TournamentLinkToDatabase {
    
    private TournamentDatabase db;
    
    public TournamentLinkToDatabase() {
        db = new TournamentDatabase();
    }
    
    public CompletableFuture<String> saveTournament(Tournament tournament) {
        return db.createTournament(tournament);
    }
}
