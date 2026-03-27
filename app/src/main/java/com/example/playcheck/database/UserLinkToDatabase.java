package com.example.playcheck.Database;

import androidx.annotation.NonNull;

import com.example.playcheck.puremodel.Organizer;
import com.example.playcheck.puremodel.Player;
import com.example.playcheck.puremodel.Referee;
import com.example.playcheck.puremodel.User;
import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.Tasks;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

public class UserLinkToDatabase {

    protected FirebaseAuth mAuth;
    protected DatabaseReference databaseRef;
    protected User theUser;

    public UserLinkToDatabase() {
        mAuth = FirebaseAuth.getInstance();
        databaseRef = FirebaseDatabase.getInstance().getReference();
    }

    public UserLinkToDatabase(User theUser){
        this();
        this.theUser = theUser;
    }

    public static Task<String> getUserAccountType(FirebaseUser currentUser) {
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        String uid = currentUser.getUid();

        DatabaseReference userNodeRef = database.getReference("users");

        DatabaseReference playerRef = userNodeRef
                .child("Player")
                .child(uid)
                .child("profile")
                .child("accountType");

        DatabaseReference organizerRef = userNodeRef
                .child("Organizer")
                .child(uid)
                .child("profile")
                .child("accountType");

        DatabaseReference refereeRef = userNodeRef
                .child("Referee")
                .child(uid)
                .child("profile")
                .child("accountType");

        return playerRef.get().continueWithTask(task -> {
            if (task.isSuccessful() && task.getResult().exists()) {
                return Tasks.forResult(task.getResult().getValue(String.class));
            }

            return organizerRef.get().continueWithTask(task2 -> {
                if (task2.isSuccessful() && task2.getResult().exists()) {
                    return Tasks.forResult(task2.getResult().getValue(String.class));
                }

                return refereeRef.get().continueWith(task3 -> {
                    if (task3.isSuccessful() && task3.getResult().exists()) {
                        return task3.getResult().getValue(String.class);
                    }
                    return null;
                });
            });
        });
    }

    public CompletableFuture<String> createUser(User user, String password) {
        CompletableFuture<String> future = new CompletableFuture<>();

        mAuth.createUserWithEmailAndPassword(user.getEmail(), password)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        String uid = mAuth.getCurrentUser().getUid();
                        user.setUid(uid);

                        // Save user data to Realtime Database
                        saveUserToDatabase(user)
                                .addOnCompleteListener(dbTask -> {
                                    if (dbTask.isSuccessful()) {
                                        future.complete(uid);
                                    } else {
                                        // If database save fails, rollback authentication
                                        deleteAuthUser();
                                        future.completeExceptionally(dbTask.getException());
                                    }
                                });
                    } else {
                        future.completeExceptionally(task.getException());
                    }
                });

        return future;
    }

    public CompletableFuture<User> loginUser(String email, String password) {
        CompletableFuture<User> future = new CompletableFuture<>();

        mAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        String uid = mAuth.getCurrentUser().getUid();
                        fetchUserByUid(uid).thenAccept(future::complete)
                                .exceptionally(throwable -> {
                                    future.completeExceptionally(throwable);
                                    return null;
                                });
                    } else {
                        future.completeExceptionally(task.getException());
                    }
                });

        return future;
    }

    public void logout() {
        mAuth.signOut();
    }

    public CompletableFuture<User> getCurrentUser() {
        CompletableFuture<User> future = new CompletableFuture<>();

        FirebaseUser currentUser = mAuth.getCurrentUser();
        if (currentUser != null) {
            fetchUserByUid(currentUser.getUid()).thenAccept(future::complete)
                    .exceptionally(throwable -> {
                        future.completeExceptionally(throwable);
                        return null;
                    });
        } else {
            future.completeExceptionally(new Exception("No user logged in"));
        }

        return future;
    }

    private Task<Void> saveUserToDatabase(User user) {
        return databaseRef.child("users").child(user.getUid()).setValue(user);
    }

    public CompletableFuture<User> fetchUserByUid(String uid) {
        CompletableFuture<User> future = new CompletableFuture<>();

        databaseRef.child("users").child(uid).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    User user = createUserFromSnapshot(snapshot);
                    if (user != null) {
                        user.setUid(uid);
                        future.complete(user);
                    } else {
                        future.completeExceptionally(new Exception("Failed to parse user data"));
                    }
                } else {
                    future.completeExceptionally(new Exception("User not found with UID: " + uid));
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                future.completeExceptionally(error.toException());
            }
        });

        return future;
    }

    public CompletableFuture<Void> updateUser(User user) {
        CompletableFuture<Void> future = new CompletableFuture<>();

        databaseRef.child("users").child(user.getUid())
                .setValue(user)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        future.complete(null);
                    } else {
                        future.completeExceptionally(task.getException());
                    }
                });

        return future;
    }

    public CompletableFuture<Void> updateUserFields(String uid, Map<String, Object> fields) {
        CompletableFuture<Void> future = new CompletableFuture<>();

        databaseRef.child("users").child(uid)
                .updateChildren(fields)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        future.complete(null);
                    } else {
                        future.completeExceptionally(task.getException());
                    }
                });

        return future;
    }

    public CompletableFuture<Void> deleteUser(String uid) {
        CompletableFuture<Void> future = new CompletableFuture<>();

        databaseRef.child("users").child(uid).removeValue()
                .addOnCompleteListener(dbTask -> {
                    if (dbTask.isSuccessful()) {
                        FirebaseUser currentUser = mAuth.getCurrentUser();
                        if (currentUser != null && currentUser.getUid().equals(uid)) {
                            currentUser.delete()
                                    .addOnCompleteListener(authTask -> {
                                        if (authTask.isSuccessful()) {
                                            future.complete(null);
                                        } else {
                                            future.completeExceptionally(authTask.getException());
                                        }
                                    });
                        } else {
                            future.complete(null);
                        }
                    } else {
                        future.completeExceptionally(dbTask.getException());
                    }
                });

        return future;
    }

    public CompletableFuture<List<Player>> getAllPlayers() {
        CompletableFuture<List<Player>> future = new CompletableFuture<>();
        List<Player> players = new ArrayList<>();

        databaseRef.child("users")
                .orderByChild("classType")
                .equalTo("Player")
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        for (DataSnapshot userSnapshot : snapshot.getChildren()) {
                            Player player = userSnapshot.getValue(Player.class);
                            if (player != null) {
                                player.setUid(userSnapshot.getKey());
                                players.add(player);
                            }
                        }
                        future.complete(players);
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        future.completeExceptionally(error.toException());
                    }
                });

        return future;
    }

    public CompletableFuture<Void> updateEmail(String newEmail) {
        CompletableFuture<Void> future = new CompletableFuture<>();

        FirebaseUser user = mAuth.getCurrentUser();
        if (user != null) {
            user.verifyBeforeUpdateEmail(newEmail)
                    .addOnCompleteListener(task -> {
                        if (task.isSuccessful()) {
                            future.complete(null);
                        } else {
                            future.completeExceptionally(task.getException());
                        }
                    });
        } else {
            future.completeExceptionally(new Exception("No user logged in"));
        }

        return future;
    }

    private String getRoleFromUser(User user) {
        if (user instanceof Referee) return "Referee";
        if (user instanceof Player) return "Player";
        if (user instanceof Organizer) return "Organizer";
        return "User";
    }

    private User createUserFromSnapshot(DataSnapshot snapshot) {
        String classType = snapshot.child("classType").getValue(String.class);
        String firstName = snapshot.child("firstName").getValue(String.class);
        String lastName = snapshot.child("lastName").getValue(String.class);
        String email = snapshot.child("email").getValue(String.class);
        String dob = snapshot.child("dateOfBirth").getValue(String.class);
        String gender = snapshot.child("gender").getValue(String.class);

        if (classType == null) return null;

        switch (classType) {
            case "Referee":
                return new Referee(firstName, lastName, email, dob, gender);
            case "Player":
                return new Player(firstName, lastName, email, dob, gender);
            case "Organizer":
                return new Organizer(firstName, lastName, email, dob, gender);
            default:
                return null;
        }
    }

    private void deleteAuthUser() {
        FirebaseUser user = mAuth.getCurrentUser();
        if (user != null) {
            user.delete();
        }
    }
}
