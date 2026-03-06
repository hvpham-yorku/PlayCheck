package com.example.playcheck.puremodel;

import android.os.Build;

import com.example.playcheck.dataBaseLinkFiles.UserLinkToDatabase;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

public abstract class User {

    private String firstName;
    private String lastName;
    private String gender;
    private LocalDate dateOfBirth;
    private String email;
    private String password;
    private String username;          // NEW: username field
    private String uid;
    private String classType; // Stores the concrete class name for deserialization

    // Static database service shared by all User instances
    protected static UserLinkToDatabase databaseService = new UserLinkToDatabase();

    // Firebase instances (can be removed if not needed elsewhere)
    private FirebaseUser uAuth;
    private FirebaseAuth mAuth;

    // Constructors
    public User() {
        this.classType = this.getClass().getSimpleName();
    }

    public User(String email, String password) {
        this();
        this.email = email;
        this.password = password;
    }

    public User(String firstName, String lastName, String email, String dob, String gender) {
        this();
        this.firstName = firstName;
        this.lastName = lastName;
        this.email = email;
        setDateOfBirth(LocalDate.parse(dob));
        this.gender = gender;
    }

    // -------------------------------------------------------------------------------------------
    // Core Business Logic Methods
    // -------------------------------------------------------------------------------------------

    /**
     * Registers a new user with email/password and saves profile to database.
     * @return CompletableFuture with the new user's UID
     */
    public CompletableFuture<String> register() {
        return databaseService.createUser(this, this.password);
    }

    /**
     * Logs in an existing user with email and password.
     * @return CompletableFuture with the logged-in User object (subclass instance)
     */
    public static CompletableFuture<User> login(String email, String password) {
        return databaseService.loginUser(email, password);
    }

    /**
     * Logs out the current user.
     */
    public static void logout() {
        databaseService.logout();
    }

    /**
     * Retrieves the currently authenticated user's profile.
     * @return CompletableFuture with the current User object, or exceptionally if no user is logged in
     */
    public static CompletableFuture<User> getCurrentUser() {
        return databaseService.getCurrentUser();
    }

    /**
     * Saves the entire user profile to the database.
     * @return CompletableFuture indicating completion
     */
    public CompletableFuture<Void> saveProfile() {
        if (this.uid == null || this.uid.isEmpty()) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                return CompletableFuture.failedFuture(
                        new Exception("Cannot save profile: User UID is null. Register first.")
                );
            }
        }
        return databaseService.updateUser(this);
    }

    /**
     * Saves only the specified fields of the user profile (more efficient).
     * @param fields Map of field names to new values
     * @return CompletableFuture indicating completion
     */
    public CompletableFuture<Void> saveProfileFields(Map<String, Object> fields) {
        if (this.uid == null || this.uid.isEmpty()) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                return CompletableFuture.failedFuture(
                        new Exception("Cannot save profile fields: User UID is null.")
                );
            }
        }
        return databaseService.updateUserFields(this.uid, fields)
                .thenAccept(v -> updateLocalFields(fields));
    }

    /**
     * Updates the local object with values from a fields map after a successful database update.
     * Subclasses should override this to handle their own fields.
     */
    protected void updateLocalFields(Map<String, Object> fields) {
        for (Map.Entry<String, Object> entry : fields.entrySet()) {
            switch (entry.getKey()) {
                case "firstName":
                    this.firstName = (String) entry.getValue();
                    break;
                case "lastName":
                    this.lastName = (String) entry.getValue();
                    break;
                case "email":
                    this.email = (String) entry.getValue();
                    break;
                case "gender":
                    this.gender = (String) entry.getValue();
                    break;
                case "username":                     // NEW: handle username update
                    this.username = (String) entry.getValue();
                    break;
                case "dateOfBirth":
                    if (entry.getValue() instanceof String) {
                        this.dateOfBirth = LocalDate.parse((String) entry.getValue());
                    }
                    break;
                // Subclasses will handle additional fields
            }
        }
    }

    /**
     * Updates the user's email address (both Firebase Auth and database).
     * @param newEmail New email address
     * @return CompletableFuture indicating completion
     */
    public CompletableFuture<Void> updateEmail(String newEmail) {
        if (this.uid == null || this.uid.isEmpty()) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                return CompletableFuture.failedFuture(new Exception("No user UID"));
            }
        }
        return databaseService.updateEmail(newEmail)
                .thenCompose(v -> {
                    Map<String, Object> emailUpdate = new HashMap<>();
                    emailUpdate.put("email", newEmail);
                    return saveProfileFields(emailUpdate);
                });
    }

    /**
     * Permanently deletes the user account from both Authentication and Realtime Database.
     * @return CompletableFuture indicating completion
     */
    public CompletableFuture<Void> deleteAccount() {
        if (this.uid == null || this.uid.isEmpty()) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                return CompletableFuture.failedFuture(new Exception("No user UID"));
            }
        }
        return databaseService.deleteUser(this.uid);
    }

    /**
     * Reloads the user profile from the database and updates this object.
     * @return CompletableFuture with the refreshed User object
     */
    public CompletableFuture<User> refreshProfile() {
        if (this.uid == null || this.uid.isEmpty()) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                return CompletableFuture.failedFuture(new Exception("No user UID"));
            }
        }
        return databaseService.fetchUserByUid(this.uid)
                .thenApply(refreshedUser -> {
                    // Copy fields from refreshed user to this instance
                    this.firstName = refreshedUser.firstName;
                    this.lastName = refreshedUser.lastName;
                    this.email = refreshedUser.email;
                    this.gender = refreshedUser.gender;
                    this.username = refreshedUser.username;   // NEW: copy username
                    this.dateOfBirth = refreshedUser.dateOfBirth;
                    this.classType = refreshedUser.classType;
                    // Subclasses should override if they have additional fields
                    return this;
                });
    }

    // -------------------------------------------------------------------------------------------
    // Getters and Setters
    // -------------------------------------------------------------------------------------------

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getName() {
        return firstName + " " + lastName;
    }

    public void setName(String firstName, String lastName) {
        this.firstName = firstName;
        this.lastName = lastName;
    }

    public LocalDate getDateOfBirth() {
        return dateOfBirth;
    }

    public String getDOBasString() {
        return dateOfBirth != null ? dateOfBirth.toString() : null;
    }

    public void setDateOfBirth(LocalDate dateOfBirth) {
        this.dateOfBirth = dateOfBirth;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getClassType() {
        return classType;
    }

    // NEW: username getter and setter
    public void setUsername(String username) {
        this.username = username;
    }

    public String getUsername() {
        return username;
    }
}