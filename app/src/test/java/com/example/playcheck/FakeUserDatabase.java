package com.example.playcheck;

import com.example.playcheck.Database.UserLinkToDatabase;
import com.example.playcheck.puremodel.User;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

class FakeUserDatabase extends UserLinkToDatabase {

    User storedUser;

    private Map<String, User> users = new HashMap<>();
    private static final String TEST_UID = "TEST_UID";

    public FakeUserDatabase() {
        super(null, (DatabaseReference) null);
    }

    @Override
    public CompletableFuture<String> createUser(User user, String password) {
        user.setUid(TEST_UID);
        users.put(TEST_UID, user);
        storedUser = user;              // keep reference for easy access
        return CompletableFuture.completedFuture(TEST_UID);
    }

    @Override
    public CompletableFuture<User> loginUser(String email, String password) {
        // For testing, assume the only user is the stored one
        return CompletableFuture.completedFuture(storedUser);
    }

    @Override
    public CompletableFuture<Void> updateUser(User user) {
        String uid = user.getUid();
        users.put(uid, user);
        if (TEST_UID.equals(uid)) {
            storedUser = user;           // keep in sync
        }
        return CompletableFuture.completedFuture(null);
    }


    @Override
    public CompletableFuture<Void> updateUserFields(String uid, String role, Map<String, Object> fields) {
        User target = users.get(uid);
        if (target != null) {
            if (fields.containsKey("firstName")) {
                target.setFirstName((String) fields.get("firstName"));
            }
            if (fields.containsKey("email")) {
                target.setEmail((String) fields.get("email"));
            }
            // add other fields as needed
        }
        if (TEST_UID.equals(uid)) {
            storedUser = target;          // sync reference
        }
        return CompletableFuture.completedFuture(null);
    }

    public CompletableFuture<Void> updateEmail(String email) {
        if (storedUser != null) {
            storedUser.setEmail(email);
            users.put(TEST_UID, storedUser);   // update map as well
        }
        return CompletableFuture.completedFuture(null);
    }

    public CompletableFuture<Void> deleteUser(String uid) {
        users.remove(uid);
        if (TEST_UID.equals(uid)) {
            storedUser = null;
        }
        return CompletableFuture.completedFuture(null);
    }

    @Override
    public CompletableFuture<User> fetchUserByUid(String uid) {
        CompletableFuture<User> future = new CompletableFuture<>();
        if (users.containsKey(uid)) {
            future.complete(users.get(uid));
        } else {
            future.completeExceptionally(new Exception("User not found"));
        }
        return future;
    }

    @Override
    public void logout() {
        storedUser = null;
        // optionally clear map if desired
    }
}
