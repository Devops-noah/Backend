package fr.parisnanterre.noah.Service;

import com.google.firebase.database.*;

import java.util.function.Consumer;

public class ChildEventListenerAdapter implements ChildEventListener {

    private final Consumer<DataSnapshot> onChildAdded;
    private final Consumer<DataSnapshot> onChildChanged;
    private final Consumer<DataSnapshot> onChildRemoved;
    private final Consumer<DatabaseError> onCancelled;

    public ChildEventListenerAdapter(
            Consumer<DataSnapshot> onChildAdded,
            Consumer<DataSnapshot> onChildChanged,
            Consumer<DataSnapshot> onChildRemoved,
            Consumer<DatabaseError> onCancelled) {
        this.onChildAdded = onChildAdded;
        this.onChildChanged = onChildChanged;
        this.onChildRemoved = onChildRemoved;
        this.onCancelled = onCancelled;
    }

    public ChildEventListenerAdapter(Consumer<DataSnapshot> onChildAdded, Consumer<DatabaseError> onCancelled) {
        this(onChildAdded, ds -> {}, ds -> {}, onCancelled);
    }

    @Override
    public void onChildAdded(DataSnapshot snapshot, String previousChildName) {
        onChildAdded.accept(snapshot);
    }

    @Override
    public void onChildChanged(DataSnapshot snapshot, String previousChildName) {
        onChildChanged.accept(snapshot);
    }

    @Override
    public void onChildRemoved(DataSnapshot snapshot) {
        onChildRemoved.accept(snapshot);
    }

    @Override
    public void onChildMoved(DataSnapshot snapshot, String previousChildName) {}

    @Override
    public void onCancelled(DatabaseError error) {
        onCancelled.accept(error);
    }
}
