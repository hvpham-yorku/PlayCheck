package com.example.playcheck.dataBaseLinkFiles;

import com.example.playcheck.puremodel.Player;
import com.example.playcheck.puremodel.User;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class PlayerLinkToDatabase extends UserLinkToDatabase{
    PlayerLinkToDatabase(Player thePlayer) {
        super(thePlayer);
    }

    public static String getCurrentUserId(FirebaseAuth mAuth) {
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if (currentUser != null) {
            return currentUser.getUid();
        } else {
            return null;
        }
    }
}
