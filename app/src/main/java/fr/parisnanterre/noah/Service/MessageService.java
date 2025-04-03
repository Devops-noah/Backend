package fr.parisnanterre.noah.Service;


import com.google.firebase.database.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.function.Consumer;

@Service
public class MessageService {

    @Autowired
    private FirebaseDatabase firebaseDatabase;

    private static final Logger logger = LoggerFactory.getLogger(MessageService.class);

    public CompletableFuture<Void> saveMessage(String conversationId, String senderId, String content) {
        DatabaseReference messagesRef = firebaseDatabase.getReference(FirebasePaths.CONVERSATIONS)
                .child(conversationId)
                .child(FirebasePaths.CHAT);

        String messageId = messagesRef.push().getKey();
        if (messageId == null) {
            logger.error("Impossible de générer un ID pour le message.");
            return CompletableFuture.failedFuture(new Exception("Impossible de générer un ID pour le message."));
        }

        Map<String, Object> messageData = Map.of(
                "senderId", senderId,
                FirebasePaths.CONTENT, content,
                "sentAt", System.currentTimeMillis(),
                "status", "sent"
        );

        CompletableFuture<Void> future = new CompletableFuture<>();
        messagesRef.child(messageId).setValue(messageData, (databaseError, databaseReference) -> {
            if (databaseError != null) {
                logger.error("Erreur enregistrement message : {}", databaseError.getMessage());
                future.completeExceptionally(databaseError.toException());
            } else {
                logger.info("Message enregistré dans Firebase avec ID : {}", messageId);
                future.complete(null);
            }
        });
        return future;
    }

    // Méthode pour rechercher un message de manière fonctionnelle
    public CompletableFuture<Boolean> findMessage(String conversationId, String senderId, String messageId) {
        CompletableFuture<Boolean> future = new CompletableFuture<>();
        DatabaseReference messagesRef = firebaseDatabase.getReference(FirebasePaths.CONVERSATIONS)
                .child(conversationId)
                .child(FirebasePaths.CHAT);

        messagesRef.child(messageId).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                future.complete(snapshot.exists());
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                handleDatabaseError(databaseError, future);
            }
        });

        return future;
    }

    // Méthode pour mettre à jour un message de manière fonctionnelle
    public void updateMessage(String conversationId, String messageId, String senderId, String content) {
        DatabaseReference messageRef = firebaseDatabase.getReference(FirebasePaths.CONVERSATIONS)
                .child(conversationId)
                .child(FirebasePaths.CHAT)
                .child(messageId);

        Map<String, Object> updatedMessageData = Map.of(
                "senderId", senderId,
                FirebasePaths.CONTENT, content,
                "sentAt", System.currentTimeMillis(),
                "status", "updated"
        );

        messageRef.updateChildren(updatedMessageData, (databaseError, databaseReference) -> {
            if (databaseError != null) {
                logger.error("Erreur mise à jour du message : {}", databaseError.getMessage());
            } else {
                logger.info("Message avec ID {} mis à jour avec succès.", messageId);
            }
        });
    }

    // Méthode pour supprimer un message de manière fonctionnelle
    public void deleteMessage(String conversationId, String messageId) {
        DatabaseReference messageRef = firebaseDatabase.getReference(FirebasePaths.CONVERSATIONS)
                .child(conversationId)
                .child(FirebasePaths.CHAT)
                .child(messageId);

        messageRef.removeValue((databaseError, databaseReference) -> {
            if (databaseError != null) {
                logger.error("Erreur suppression du message : {}", databaseError.getMessage());
            } else {
                logger.info("Message avec ID {} supprimé avec succès.", messageId);
            }
        });
    }

    // Méthode pour supprimer tous les messages d'une conversation
    public CompletableFuture<Void> deleteAllMessagesInConversation(String conversationId) {
        DatabaseReference conversationRef = firebaseDatabase.getReference(FirebasePaths.CONVERSATIONS)
                .child(conversationId)
                .child(FirebasePaths.CHAT);

        CompletableFuture<Void> future = new CompletableFuture<>();

        conversationRef.removeValue(new DatabaseReference.CompletionListener() {
            @Override
            public void onComplete(DatabaseError error, DatabaseReference ref) {
                if (error != null) {
                    logger.error("Erreur lors de la suppression des messages : {}", error.getMessage());
                    future.completeExceptionally(error.toException());
                } else {
                    logger.info("Tous les messages ont été supprimés pour la conversation : {}", conversationId);
                    future.complete(null);
                }
            }
        });

        return future;
    }



    // Gestion centralisée des erreurs Firebase
    private void handleDatabaseError(DatabaseError error, CompletableFuture<Boolean> future) {
        logger.error("Erreur lors de l'opération sur Firebase : {}", error.getMessage());
        future.complete(false);
    }
}