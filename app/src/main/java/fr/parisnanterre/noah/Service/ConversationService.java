package fr.parisnanterre.noah.Service;

import com.google.firebase.database.*;
import fr.parisnanterre.noah.Entity.Utilisateur;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.concurrent.CompletableFuture;

@Service
@RequiredArgsConstructor
public class ConversationService {

    private final MessageService messageService;
    private static final Logger logger = LoggerFactory.getLogger(ConversationService.class);
    private final FirebaseDatabase firebaseDatabase;

    public CompletableFuture<Void> createConversationIfNeeded(Utilisateur utilisateurA, Utilisateur utilisateurB) {
        DatabaseReference conversationsRef = firebaseDatabase.getReference("conversations");
        String conversationId = generateConversationId(utilisateurA.getId(), utilisateurB.getId());
        DatabaseReference conversationRef = conversationsRef.child(conversationId);

        return checkIfConversationExists(conversationRef)
                .thenCompose(exists -> exists ? CompletableFuture.completedFuture(null) : createNewConversation(conversationRef, utilisateurA, utilisateurB))
                .thenRun(() -> logger.info("Opération terminée pour la conversation {}", conversationId))
                .exceptionally(ex -> {
                    logger.error("Erreur lors de la création de la conversation : {}", ex.getMessage());
                    return null;
                });
    }

    private CompletableFuture<Boolean> checkIfConversationExists(DatabaseReference conversationRef) {
        CompletableFuture<Boolean> future = new CompletableFuture<>();
        conversationRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                future.complete(snapshot.exists());
            }

            @Override
            public void onCancelled(DatabaseError error) {
                logger.error("Erreur lors de la vérification de la conversation : {}", error.getMessage());
                future.completeExceptionally(error.toException());
            }
        });
        return future;
    }

    private CompletableFuture<Void> createNewConversation(DatabaseReference conversationRef, Utilisateur utilisateurA, Utilisateur utilisateurB) {
        Map<String, Object> conversationMap = Map.of(
                "utilisateurA", utilisateurA.getEmail(),
                "utilisateurB", utilisateurB.getEmail(),
                "createdAt", System.currentTimeMillis()
        );

        CompletableFuture<Void> future = new CompletableFuture<>();
        conversationRef.setValue(conversationMap, (error, ref) -> {
            if (error != null) {
                logger.error("Erreur lors de l'enregistrement de la conversation : {}", error.getMessage());
                future.completeExceptionally(error.toException());
            } else {
                logger.info("Conversation créée avec succès.");
                future.complete(null);
            }
        });
        return future;
    }

    public CompletableFuture<Void> deleteConversation(String conversationId) {
        DatabaseReference conversationRef = firebaseDatabase.getReference("conversations").child(conversationId);

        return CompletableFuture.runAsync(() ->
                conversationRef.removeValue((error, ref) -> {
                    if (error != null) {
                        logger.error("Erreur suppression conversation : {}", error.getMessage());
                    } else {
                        logger.info("Conversation {} supprimée avec succès.", conversationId);
                    }
                })
        );
    }

    public CompletableFuture<Void> saveMessage(String conversationId, String senderId, String content) {
        String recipientId = getRecipientId(conversationId, senderId);
        return messageService.saveMessage(conversationId, senderId, content)
                .thenRun(() -> logger.info("Message envoyé à recipientId: {}", recipientId))
                .exceptionally(ex -> {
                    logger.error("Erreur lors de l'envoi du message : {}", ex.getMessage());
                    return null;
                });
    }

    private String getRecipientId(String conversationId, String senderId) {
        String[] ids = conversationId.split("_");
        return ids[0].equals(senderId) ? ids[1] : ids[0];
    }

    private String generateConversationId(Long idA, Long idB) {
        return (idA < idB) ? idA + "" + idB : idB + "" + idA;
    }

    public CompletableFuture<Void> deleteAllMessagesInConversation(String conversationId) {
        return messageService.deleteAllMessagesInConversation(conversationId)
                .thenRun(() -> logger.info("Tous les messages de la conversation {} supprimés.", conversationId))
                .exceptionally(ex -> {
                    logger.error("Erreur lors de la suppression des messages : {}", ex.getMessage());
                    return null;
                });
    }
}