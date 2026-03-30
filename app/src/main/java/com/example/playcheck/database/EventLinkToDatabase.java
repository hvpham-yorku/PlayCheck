package com.example.playcheck.database;

import androidx.annotation.NonNull;

import com.example.playcheck.puremodel.Event;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;

public class EventLinkToDatabase {

    private DatabaseReference eventsRef;

    public EventLinkToDatabase() {
        this.eventsRef = FirebaseDatabase.getInstance().getReference("events");
    }

    public CompletableFuture<String> saveEvent(Event event) {
        CompletableFuture<String> future = new CompletableFuture<>();
        String eventId = event.getEventId();
        if (eventId == null || eventId.isEmpty()) {
            eventId = eventsRef.push().getKey();
            event.setEventId(eventId);
        }

        eventsRef.child(eventId).setValue(event).addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                future.complete(event.getEventId());
            } else {
                future.completeExceptionally(task.getException());
            }
        });

        return future;
    }

    public CompletableFuture<List<Event>> getAllEvents() {
        CompletableFuture<List<Event>> future = new CompletableFuture<>();
        eventsRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                List<Event> events = new ArrayList<>();
                for (DataSnapshot eventSnapshot : snapshot.getChildren()) {
                    Event event = eventSnapshot.getValue(Event.class);
                    if (event != null) {
                        event.setEventId(eventSnapshot.getKey());
                        events.add(event);
                    }
                }
                future.complete(events);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                future.completeExceptionally(error.toException());
            }
        });
        return future;
    }

    public CompletableFuture<Event> getEventById(String eventId) {
        CompletableFuture<Event> future = new CompletableFuture<>();
        eventsRef.child(eventId).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                Event event = snapshot.getValue(Event.class);
                if (event != null) {
                    event.setEventId(snapshot.getKey());
                    future.complete(event);
                } else {
                    future.completeExceptionally(new Exception("Event not found"));
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                future.completeExceptionally(error.toException());
            }
        });
        return future;
    }
}
