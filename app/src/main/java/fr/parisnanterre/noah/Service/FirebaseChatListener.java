package fr.parisnanterre.noah.Service;

import com.google.firebase.database.*;
import jakarta.annotation.PostConstruct;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class FirebaseChatListener {

    @Autowired
    private FirebaseDatabase firebaseDatabase;

    @Autowired
    private MessageService messageService;

    private static final Logger logger = LoggerFactory.getLogger(FirebaseChatListener.class);

    // Cache pour éviter le traitement en boucle
    private final Set<String> localCache = ConcurrentHashMap.newKeySet();

    @PostConstruct
    public void init() {
        DatabaseReference conversationsRef = firebaseDatabase.getReference(FirebasePaths.CONVERSATIONS);

        conversationsRef.addChildEventListener(new ChildEventListenerAdapter(
                snapshot -> listenForMessages(snapshot.getKey()),
                error -> logger.error("Erreur Firebase : {}", error.getMessage())
        ));
    }

    private void listenForMessages(String conversationId) {
        DatabaseReference messagesRef = firebaseDatabase.getReference(FirebasePaths.CONVERSATIONS)
                .child(conversationId)
                .child(FirebasePaths.CHAT);

        messagesRef.addChildEventListener(new ChildEventListenerAdapter(
                snapshot -> processMessage(conversationId, snapshot),
                snapshot -> processMessage(conversationId, snapshot),
                snapshot -> {
                    logger.info("Message supprimé : {}", snapshot.getKey());
                    messageService.deleteMessage(conversationId, snapshot.getKey());
                },
                error -> logger.error("Erreur Firebase : {}", error.getMessage())
        ));
    }

    private void processMessage(String conversationId, DataSnapshot snapshot) {
        Map<String, Object> messageData = (Map<String, Object>) snapshot.getValue();

        if (messageData == null) {
            logger.warn("Message invalide, ignoré.");
            return;
        }

        String senderId = (String) messageData.get("senderId");
        String content = (String) messageData.get(FirebasePaths.CONTENT);
        Long sentAt = (Long) messageData.get("sentAt");

        if (senderId == null || sentAt == null) {
            logger.warn("Message invalide, ignoré.");
            return;
        }

        // Générer un identifiant unique pour chaque message
        String messageId = UUID.randomUUID().toString();
        String messageKey = conversationId + "_" + senderId + "_" + messageId;

        if (!localCache.add(messageKey)) {
            logger.info("Message déjà traité, on ignore : {}", content);
            return;
        }



        messageService.findMessage(conversationId, senderId, messageId)
                .thenCompose(exists -> exists
                        ? CompletableFuture.completedFuture(null)
                        : messageService.saveMessage(conversationId, senderId, content))
                .thenRun(() -> logger.info("Message traité : {}", content))
                .exceptionally(ex -> {
                    logger.error("Erreur lors du traitement du message : {}", ex.getMessage());
                    return null;
                });
    }
}
