package com.example.playcheck.Database;

import android.util.Log;

import androidx.annotation.NonNull;

import com.example.playcheck.puremodel.Organizer;
import com.example.playcheck.puremodel.Player;
import com.example.playcheck.puremodel.Referee;
import com.example.playcheck.puremodel.User;
import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.TaskCompletionSource;
import com.google.android.gms.tasks.Tasks;
import com.google.firebase.auth.AuthResult;
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

    public UserLinkToDatabase() {
        mAuth = FirebaseAuth.getInstance();
        databaseRef = FirebaseDatabase.getInstance().getReference();
    }


    /**
     * Creates a new user with email and password.
     * Saves the user profile to the database after successful authentication.
     */
    public CompletableFuture<String> createUser(User user, String password) {
        CompletableFuture<String> future = new CompletableFuture<>();

        mAuth.createUserWithEmailAndPassword(user.getEmail(), password)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        String uid = mAuth.getCurrentUser().getUid();
                        user.setUid(uid);

                        saveUserToDatabase(user)
                                .addOnCompleteListener(dbTask -> {
                                    if (dbTask.isSuccessful()) {
                                        future.complete(uid);
                                    } else {
                                        FirebaseUser currentUser = mAuth.getCurrentUser();
                                        if (currentUser != null) currentUser.delete();
                                        future.completeExceptionally(dbTask.getException());
                                    }
                                });
                    } else {
                        future.completeExceptionally(task.getException());
                    }
                });

        return future;
    }

    /**
     * Logs in an existing user with email and password.
     */
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

    /**
     * Logs out the current user.
     */
    public void logout() {
        mAuth.signOut();
    }

    /**
     * Retrieves the currently authenticated user's profile.
     */
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
            future.complete(null);
        }

        return future;
    }

    private Task<Void> saveUserToDatabase(User user) {
        String role = getRoleFromUser(user);
        return databaseRef.child("users").child(role).child(user.getUid()).setValue(user);
    }

    public CompletableFuture<User> fetchUserByUid(String uid) {
        CompletableFuture<User> future = new CompletableFuture<>();

        databaseRef.child("users").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                User user = null;
                if (snapshot.child("Organizer").hasChild(uid)) {
                    user = snapshot.child("Organizer").child(uid).getValue(Organizer.class);
                    if (user == null) user = snapshot.child("Organizer").child(uid).child("profile").getValue(Organizer.class);
                } else if (snapshot.child("Player").hasChild(uid)) {
                    user = snapshot.child("Player").child(uid).getValue(Player.class);
                    if (user == null) user = snapshot.child("Player").child(uid).child("profile").getValue(Player.class);
                } else if (snapshot.child("Referee").hasChild(uid)) {
                    user = snapshot.child("Referee").child(uid).getValue(Referee.class);
                    if (user == null) user = snapshot.child("Referee").child(uid).child("profile").getValue(Referee.class);
                }

                if (user != null) {
                    user.setUid(uid);
                    future.complete(user);
                } else {
                    future.completeExceptionally(new Exception("User not found"));
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
        String role = getRoleFromUser(user);
        databaseRef.child("users").child(role).child(user.getUid())
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

    public CompletableFuture<Void> updateUserFields(String uid, String role, Map<String, Object> fields) {
        CompletableFuture<Void> future = new CompletableFuture<>();
        databaseRef.child("users").child(role).child(uid)
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

    public CompletableFuture<List<User>> getAllUsers() {
        CompletableFuture<List<User>> future = new CompletableFuture<>();
        List<User> users = new ArrayList<>();

        databaseRef.child("users").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot roleSnap : snapshot.getChildren()) {
                    String role = roleSnap.getKey();
                    for (DataSnapshot userSnap : roleSnap.getChildren()) {
                        User user = null;
                        if (userSnap.hasChild("profile")) {
                            DataSnapshot profileSnap = userSnap.child("profile");
                            if ("Organizer".equals(role)) user = profileSnap.getValue(Organizer.class);
                            else if ("Player".equals(role)) user = profileSnap.getValue(Player.class);
                            else if ("Referee".equals(role)) user = profileSnap.getValue(Referee.class);
                        } else {
                            if ("Organizer".equals(role)) user = userSnap.getValue(Organizer.class);
                            else if ("Player".equals(role)) user = userSnap.getValue(Player.class);
                            else if ("Referee".equals(role)) user = userSnap.getValue(Referee.class);
                        }
                        
                        if (user != null) {
                            user.setUid(userSnap.getKey());
                            users.add(user);
                        }
                    }
                }
                future.complete(users);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                future.completeExceptionally(error.toException());
            }
        });

        return future;
    }

    private String getRoleFromUser(User user) {
        if (user instanceof Referee) return "Referee";
        if (user instanceof Player) return "Player";
        if (user instanceof Organizer) return "Organizer";
        return "User";
    }

    public interface PlayerIdCallback {
        void onCallback(ArrayList<String> ids);
    }

    public interface PlayerNameCallback {
        void onCallback(ArrayList<String> names);
    }

    public static void getPlayerIDs(PlayerIdCallback callback) {
        FirebaseDatabase.getInstance().getReference("users").child("Player").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                ArrayList<String> ids = new ArrayList<>();
                for (DataSnapshot child : snapshot.getChildren()) {
                    ids.add(child.getKey());
                }
                callback.onCallback(ids);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }
        });
    }

    public static void getPlayerNames(PlayerNameCallback callback) {
        FirebaseDatabase.getInstance().getReference("users").child("Player").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                ArrayList<String> names = new ArrayList<>();
                for (DataSnapshot child : snapshot.getChildren()) {
                    String firstName = child.child("firstName").getValue(String.class);
                    String lastName = child.child("lastName").getValue(String.class);
                    if (firstName == null && child.hasChild("profile")) {
                        firstName = child.child("profile").child("firstName").getValue(String.class);
                        lastName = child.child("profile").child("lastName").getValue(String.class);
                    }
                    names.add(firstName + " " + lastName);
                }
                callback.onCallback(names);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }
        });
    }

    public static Task<String> getUserAccountType(FirebaseUser user) {
        String uid = user.getUid();
        TaskCompletionSource<String> tcs = new TaskCompletionSource<>();
        FirebaseDatabase.getInstance().getReference("users").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.child("Organizer").hasChild(uid)) {
                    tcs.setResult("Organizer");
                } else if (snapshot.child("Player").hasChild(uid)) {
                    tcs.setResult("Player");
                } else if (snapshot.child("Referee").hasChild(uid)) {
                    tcs.setResult("Referee");
                } else {
                    tcs.setResult(null);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                tcs.setException(error.toException());
            }
        });
        return tcs.getTask();
    }
}
