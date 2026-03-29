package com.example.playcheck.Database;

import com.example.playcheck.puremodel.Message;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;

import androidx.annotation.NonNull;

/**
 * Database helper for message board functionality
 */
public class MessageBoardDatabase {
    
    private DatabaseReference databaseRef;
    
    public MessageBoardDatabase() {
        databaseRef = FirebaseDatabase.getInstance().getReference();
    }
    
    /**
     * Send a message to the board
     * @param message The message object
     * @return CompletableFuture with the message ID
     */
    public CompletableFuture<String> sendMessage(Message message) {
        CompletableFuture<String> future = new CompletableFuture<>();
        
        DatabaseReference messagesRef = databaseRef.child("messages");
        String messageId = messagesRef.push().getKey();
        
        if (messageId == null) {
            future.completeExceptionally(new Exception("Failed to generate message ID"));
            return future;
        }
        
        message.setMessageId(messageId);
        message.setTimestamp(System.currentTimeMillis());
        
        messagesRef.child(messageId).setValue(message)
            .addOnSuccessListener(aVoid -> future.complete(messageId))
            .addOnFailureListener(future::completeExceptionally);
        
        return future;
    }
    
    /**
     * Get all messages (ordered by timestamp, newest first)
     * @param limit Maximum number of messages to retrieve
     * @return CompletableFuture with list of messages
     */
    public CompletableFuture<List<Message>> getAllMessages(int limit) {
        CompletableFuture<List<Message>> future = new CompletableFuture<>();
        List<Message> messages = new ArrayList<>();
        
        Query query = databaseRef.child("messages")
            .orderByChild("timestamp")
            .limitToLast(limit);
        
        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot msgSnapshot : snapshot.getChildren()) {
                    Message message = msgSnapshot.getValue(Message.class);
                    if (message != null) {
                        messages.add(0, message); // Add to beginning for reverse order
                    }
                }
                future.complete(messages);
            }
            
            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                future.completeExceptionally(error.toException());
            }
        });
        
        return future;
    }
    
    /**
     * Get messages about a specific game
     * @param gameId The game ID
     * @return CompletableFuture with list of messages
     */
    public CompletableFuture<List<Message>> getMessagesForGame(String gameId) {
        CompletableFuture<List<Message>> future = new CompletableFuture<>();
        List<Message> messages = new ArrayList<>();
        
        databaseRef.child("messages")
            .orderByChild("gameId")
            .equalTo(gameId)
            .addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    for (DataSnapshot msgSnapshot : snapshot.getChildren()) {
                        Message message = msgSnapshot.getValue(Message.class);
                        if (message != null) {
                            messages.add(message);
                        }
                    }
                    // Sort by timestamp
                    messages.sort((m1, m2) -> Long.compare(m2.getTimestamp(), m1.getTimestamp()));
                    future.complete(messages);
                }
                
                @Override
                public void onCancelled(@NonNull DatabaseError error) {
                    future.completeExceptionally(error.toException());
                }
            });
        
        return future;
    }
    
    /**
     * Listen for new messages in real-time
     * @param listener The ValueEventListener to receive updates
     */
    public void listenForMessages(ValueEventListener listener) {
        databaseRef.child("messages")
            .orderByChild("timestamp")
            .limitToLast(50)
            .addValueEventListener(listener);
    }
    
    /**
     * Mark a message as read
     * @param messageId The message ID
     * @return CompletableFuture indicating completion
     */
    public CompletableFuture<Void> markMessageAsRead(String messageId) {
        CompletableFuture<Void> future = new CompletableFuture<>();
        
        databaseRef.child("messages")
            .child(messageId)
            .child("read")
            .setValue(true)
            .addOnSuccessListener(aVoid -> future.complete(null))
            .addOnFailureListener(future::completeExceptionally);
        
        return future;
    }
    
    /**
     * Delete a message
     * @param messageId The message ID
     * @return CompletableFuture indicating completion
     */
    public CompletableFuture<Void> deleteMessage(String messageId) {
        CompletableFuture<Void> future = new CompletableFuture<>();
        
        databaseRef.child("messages")
            .child(messageId)
            .removeValue()
            .addOnSuccessListener(aVoid -> future.complete(null))
            .addOnFailureListener(future::completeExceptionally);
        
        return future;
    }
    
    /**
     * Get count of unread messages
     * @return CompletableFuture with count
     */
    public CompletableFuture<Integer> getUnreadCount() {
        CompletableFuture<Integer> future = new CompletableFuture<>();
        
        databaseRef.child("messages")
            .orderByChild("read")
            .equalTo(false)
            .addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    future.complete((int) snapshot.getChildrenCount());
                }
                
                @Override
                public void onCancelled(@NonNull DatabaseError error) {
                    future.completeExceptionally(error.toException());
                }
            });
        
        return future;
    }
}
