package com.example.playcheck.database;

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

    public UserLinkToDatabase() {
        mAuth = FirebaseAuth.getInstance();
        databaseRef = FirebaseDatabase.getInstance().getReference();
    }


    FirebaseAuth uAuth;

    DatabaseReference userRef = FirebaseDatabase.getInstance().getReference();
    DatabaseReference rootRef = userRef.child("Referee");
    DatabaseReference rootPlayerRef = userRef.child("Player");

    DatabaseReference rootOrganizerRef = userRef.child("Organizer");
//-----------------------------------------------------------------------------------------------

    //The entity that updates/deletion are going to base on in the database
    User theUser;
    UserLinkToDatabase(User theUser){

        this.theUser = theUser;
        uAuth = FirebaseAuth.getInstance();
    }


    public static Task<String> getUserAccountType(FirebaseUser currentUser) {
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        String uid = currentUser.getUid();

        DatabaseReference playerRef = database.getReference("users")
                .child("Player")
                .child(uid)
                .child("profile")
                .child("accountType");

        DatabaseReference organizerRef = database.getReference("users")
                .child("Organizer")
                .child(uid)
                .child("profile")
                .child("accountType");

        DatabaseReference refereeRef = database.getReference("users")
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
    //-------------------------------------------------------------------------------------------
  /*  1. Core CRUD Operations
    These are the fundamental building blocks of any persistence class.

    create(entity) / save(entity) / insert(entity)

    Function: Persists a new record to the database.

    Returns: Often returns the saved entity with its generated ID.

    /**
     * Creates a new user with email and password.
     * Saves the user profile to the database after successful authentication.
     *
     * @param user     The user object (must contain email)
     * @param password The password for the new account
     * @return CompletableFuture with the new user's UID
     */
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

    /**
     * Logs in an existing user with email and password.
     * Fetches the user profile from the database after successful authentication.
     *
     * @param email    User's email
     * @param password User's password
     * @return CompletableFuture with the fully populated User object
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
     *
     * @return CompletableFuture with the current User object, or exceptionally if no user is logged in
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
            future.completeExceptionally(new Exception("No user logged in"));
        }

        return future;
    }

    /**
     * Deletes the currently authenticated user from Firebase Authentication (rollback helper).
     */
    private void deleteAuthUser() {
        FirebaseUser user = mAuth.getCurrentUser();
        if (user != null) {
            user.delete();
        }
    }

    //-------------------------------------------------------------------------------------------
    // Database Operations
    //-------------------------------------------------------------------------------------------

    /**
     * Saves a user object to the Realtime Database under "users/{uid}".
     */
    private Task<Void> saveUserToDatabase(User user) {
        return databaseRef.child("users").child(user.getUid()).setValue(user);
    }

    /**
     * Fetches a user by their UID from the "users" node.
     *
     * @param uid The user's unique identifier
     * @return CompletableFuture with the reconstructed User object (subclass instance)
     */
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

    /**
     * Updates an entire user object in the database.
     *
     * @param user The user object with updated fields
     * @return CompletableFuture indicating completion
     */
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

    /**
     * Updates only specific fields of a user in the database.
     * More efficient than updating the entire object.
     *
     * @param uid    The user's UID
     * @param fields A map of field names to new values
     * @return CompletableFuture indicating completion
     */
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

    /**
     * Deletes a user completely: removes from database and (if current user) from Authentication.
     *
     * @param uid The UID of the user to delete
     * @return CompletableFuture indicating completion
     */
    public CompletableFuture<Void> deleteUser(String uid) {
        CompletableFuture<Void> future = new CompletableFuture<>();

        // Delete from database first
        databaseRef.child("users").child(uid).removeValue()
                .addOnCompleteListener(dbTask -> {
                    if (dbTask.isSuccessful()) {
                        // If the deleted user is the currently authenticated one, also delete from Auth
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
                            // Either no current user or it's a different user – just complete
                            future.complete(null);
                        }
                    } else {
                        future.completeExceptionally(dbTask.getException());
                    }
                });

        return future;
    }

    /**
     * Retrieves all players from the database (users with classType = "Player").
     *
     * @return CompletableFuture with a list of Player objects
     */
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
                            // Use createUserFromSnapshot to properly set all fields including username
                            User user = createUserFromSnapshot(userSnapshot);
                            if (user instanceof Player) {
                                Player player = (Player) user;
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

    /**
     * Sends a verification email to update the user's email address in Firebase Authentication.
     * (The actual email change requires the user to click the verification link.)
     *
     * @param newEmail The new email address
     * @return CompletableFuture indicating that the verification email was sent
     */
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

    //-------------------------------------------------------------------------------------------
    // Helper Methods
    //-------------------------------------------------------------------------------------------

    /**
     * Determines the role string from a User object (used for database path, but not strictly needed
     * if we use a flat "users" node).
     */
    private String getRoleFromUser(User user) {
        if (user instanceof Referee) return "Referee";
        if (user instanceof Player) return "Player";
        if (user instanceof Organizer) return "Organizer";
        return "User";
    }

    /**
     * Reconstructs a User (or subclass) object from a DataSnapshot.
     * Now includes the username field.
     *
     * @param snapshot The DataSnapshot containing user data
     * @return A User object of the appropriate subclass, or null if parsing fails
     */
    private User createUserFromSnapshot(DataSnapshot snapshot) {
        String classType = snapshot.child("classType").getValue(String.class);
        String firstName = snapshot.child("firstName").getValue(String.class);
        String lastName = snapshot.child("lastName").getValue(String.class);
        String email = snapshot.child("email").getValue(String.class);
        String dob = snapshot.child("dateOfBirth").getValue(String.class);
        String gender = snapshot.child("gender").getValue(String.class);
        String username = snapshot.child("username").getValue(String.class);  // NEW: read username

        if (classType == null) return null;

        User user;
        switch (classType) {
            case "Referee":
                user = new Referee(firstName, lastName, email, dob, gender);
                break;
            case "Player":
                user = new Player(firstName, lastName, email, dob, gender);
                break;
            case "Organizer":
                user = new Organizer(firstName, lastName, email, dob, gender);
                break;
            default:
                return null;
        }

        // Set the username (and any other fields not in constructors)
        if (user != null) {
            user.setUsername(username);
        }
        return user;
    }


}