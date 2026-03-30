package com.example.playcheck.Database;

import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;
import java.util.Map;

public class NotificationLinkToDatabase {

    private final DatabaseReference rootRef;

    public NotificationLinkToDatabase() {
        rootRef = FirebaseDatabase.getInstance().getReference();
    }

    public Task<Void> sendNotificationToUser(
            String role,
            String uid,
            String title,
            String message,
            String type,
            String relatedGameId
    ) {
        String notificationId = rootRef.child("users")
                .child(role)
                .child(uid)
                .child("notifications")
                .push()
                .getKey();

        Map<String, Object> notification = new HashMap<>();
        notification.put("title", title);
        notification.put("message", message);
        notification.put("type", type);
        notification.put("relatedGameId", relatedGameId);
        notification.put("read", false);
        notification.put("createdAt", System.currentTimeMillis());

        return rootRef.child("users")
                .child(role)
                .child(uid)
                .child("notifications")
                .child(notificationId)
                .setValue(notification);
    }

    public Task<Void> markAsRead(String role, String uid, String notificationId) {
        return rootRef.child("users")
                .child(role)
                .child(uid)
                .child("notifications")
                .child(notificationId)
                .child("read")
                .setValue(true);
    }
}