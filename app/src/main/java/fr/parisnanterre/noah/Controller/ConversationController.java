package fr.parisnanterre.noah.Controller;

import fr.parisnanterre.noah.Service.ConversationService;
import fr.parisnanterre.noah.Service.DemandeService;
import fr.parisnanterre.noah.Service.UtilisateurServiceImpl;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;



@RestController
@RequestMapping("/api/conversations")
@RequiredArgsConstructor
public class ConversationController {
    private final UtilisateurServiceImpl utilisateurService;
    // Ajoute cette dépendance dans la classe
    private final DemandeService demandeService;
    // import static fr.parisnanterre.noah.Service.ConversationService.logger;


    private final ConversationService conversationService;
    private static final Logger logger = LoggerFactory.getLogger(ConversationController.class);

    @PostMapping("/process-accepted-requests")
    public ResponseEntity<String> processAcceptedRequests() {
        try {
            // Appel du service DemandeService pour traiter les demandes acceptées
            demandeService.processExistingAcceptedRequests();
            return ResponseEntity.ok("Traitement des demandes acceptées terminé. Conversations créées si nécessaire.");
        } catch (Exception e) {
            // En cas d'erreur, on renvoie un code d'erreur 500 avec un message
            return ResponseEntity.status(500).body("Erreur lors du traitement des demandes acceptées.");
        }
    }

    @DeleteMapping("/{conversationId}/messages")
    public ResponseEntity<String> deleteAllMessages(@PathVariable String conversationId) {
        try {
            conversationService.deleteAllMessagesInConversation(conversationId);
            return ResponseEntity.ok("Tous les messages de la conversation ont été supprimés.");
        } catch (RuntimeException e) {
            // Capture des erreurs et retour d'un message d'erreur détaillé
            logger.error("Erreur lors de la suppression des messages : {}", e.getMessage());
            return ResponseEntity.status(500).body("Erreur lors de la suppression des messages : " + e.getMessage());
        }
    }

    @DeleteMapping("/{conversationId}")
    public ResponseEntity<String> deleteConversation(@PathVariable String conversationId) {
        try {
            // Appel du service ConversationService pour supprimer la conversation
            conversationService.deleteConversation(conversationId);
            return ResponseEntity.ok("La conversation avec ID " + conversationId + " a été supprimée.");
        } catch (RuntimeException e) {
            // Capture des erreurs et retour d'un message d'erreur détaillé
            logger.error("Erreur lors de la suppression de la conversation : {}", e.getMessage());
            return ResponseEntity.status(500).body("Erreur lors de la suppression de la conversation : " + e.getMessage());
        }
    }


}

