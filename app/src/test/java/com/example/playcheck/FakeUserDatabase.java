package com.example.playcheck;

import com.example.playcheck.database.UserLinkToDatabase;
import com.example.playcheck.puremodel.User;

import java.util.Map;
import java.util.concurrent.CompletableFuture;

class FakeUserDatabase extends UserLinkToDatabase {

    User storedUser;

    @Override
    public CompletableFuture<String> createUser(User user, String password) {
        storedUser = user;
        user.setUid("TEST_UID");
        return CompletableFuture.completedFuture("TEST_UID");
    }

    @Override
    public CompletableFuture<User> loginUser(String email, String password) {
        return CompletableFuture.completedFuture(storedUser);
    }

    @Override
    public CompletableFuture<Void> updateUser(User user) {
        storedUser = user;
        return CompletableFuture.completedFuture(null);
    }

    @Override
    public CompletableFuture<Void> updateUserFields(String uid, Map<String,Object> fields) {

        if(fields.containsKey("firstName"))
            storedUser.setFirstName((String) fields.get("firstName"));

        if(fields.containsKey("email"))
            storedUser.setEmail((String) fields.get("email"));

        return CompletableFuture.completedFuture(null);
    }

    @Override
    public CompletableFuture<Void> updateEmail(String email) {
        storedUser.setEmail(email);
        return CompletableFuture.completedFuture(null);
    }

    @Override
    public CompletableFuture<Void> deleteUser(String uid) {
        storedUser = null;
        return CompletableFuture.completedFuture(null);
    }

    @Override
    public CompletableFuture<User> fetchUserByUid(String uid) {
        return CompletableFuture.completedFuture(storedUser);
    }

    @Override
    public void logout() {
        storedUser = null;
    }
}
