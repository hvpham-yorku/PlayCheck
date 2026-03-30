package com.example.playcheck.database;

import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import java.util.HashMap;
import java.util.Map;

public class NotificationLinkToDatabase {
    private DatabaseReference mDatabase;

    public NotificationLinkToDatabase() {
        mDatabase = FirebaseDatabase.getInstance().getReference();
    }

    public void sendNotificationToUser(String role, String userId, String title, String message, String type, String gameId) {
        String notificationId = mDatabase.child("notifications").child(userId).push().getKey();
        if (notificationId != null) {
            Map<String, Object> notification = new HashMap<>();
            notification.put("title", title);
            notification.put("message", message);
            notification.put("type", type);
            notification.put("gameId", gameId);
            notification.put("timestamp", System.currentTimeMillis());
            notification.put("read", false);

            mDatabase.child("notifications").child(userId).child(notificationId).setValue(notification);
        }
    }
}
