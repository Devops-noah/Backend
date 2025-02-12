package fr.parisnanterre.noah.Service;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import fr.parisnanterre.noah.DTO.DemandeResponse;
import fr.parisnanterre.noah.DTO.InformationColisResponse;
import fr.parisnanterre.noah.DTO.NotificationResponseDto;
import fr.parisnanterre.noah.Entity.Demande;
import fr.parisnanterre.noah.Entity.Notification;
import fr.parisnanterre.noah.Entity.Utilisateur;
import fr.parisnanterre.noah.Repository.DemandeRepository;
import fr.parisnanterre.noah.Repository.NotificationRepository;
import fr.parisnanterre.noah.Repository.UtilisateurRepository;
import fr.parisnanterre.noah.config.WebSocketSessionTracker;
import lombok.RequiredArgsConstructor;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;

import java.io.IOException;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class NotificationService {

    private final NotificationRepository notificationRepository;
    private final UtilisateurRepository utilisateurRepository;
    private final DemandeRepository demandeRepository;
    // Inject a WebSocket session tracker (you’ll need to create this)

    private final WebSocketSessionTracker sessionTracker;

    // Créer la notification après la création d'une demande
    public void createNotification(Long demandeId) {
        System.out.println("🚀 createNotification() called with demandeId: " + demandeId);
        Demande demande = demandeRepository.findById(demandeId)
                .orElseThrow(() -> new RuntimeException("Demande non trouvée"));

        Utilisateur expediteur = demande.getExpediteur();
        if (expediteur == null) {
            throw new RuntimeException("Expéditeur non trouvé pour la demande");
        }

        Utilisateur voyageur = demande.getInformationColis().getAnnonce().getVoyageur();
        if (voyageur == null) {
            throw new RuntimeException("Voyageur non trouvé pour l'annonce");
        }

        // ✅ Vérifier et attribuer dynamiquement le rôle EXPEDITEUR
        if (!expediteur.isExpediteur()) {
            expediteur.becomeExpediteur();  // Ajoute le type EXPEDITEUR
            utilisateurRepository.save(expediteur); // Sauvegarde le changement
        }

        // Create notification message
        String message = "Nouvelle demande reçue pour votre colis proposé : "
                + demande.getInformationColis().getDimensions();

        // Create and save notification
        Notification notification = new Notification();
        notification.setMessage(message);
        notification.setVoyageur(voyageur);
        notification.setExpediteur(expediteur);
        notification.setDemande(demande);
        notification.setCreatedAt(new Date());
        notification.setRead(false);
        notificationRepository.save(notification);

        // Update notification counter
        voyageur.setNotificationCount(voyageur.getNotificationCount() + 1);
        utilisateurRepository.save(voyageur);

        // 🚀 Send via raw WebSocket
        System.out.println("🔍 Looking for WebSocket session for voyageur ID: " + voyageur.getId());

// Temporary delay for testing (remove in production)
        try {
            Thread.sleep(2000); // Wait 2 seconds before checking session
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        // 🚀 Send via raw WebSocket
        WebSocketSession session = sessionTracker.getSession(voyageur.getId());

        if (session != null) {
            System.out.println("✅ WebSocket session found for user ID: " + voyageur.getId());

            if (session.isOpen()) {
                try {
                    // Convert notification to JSON
                    String jsonNotification = convertToJson(notification);
                    System.out.println("📩 Sending WebSocket notification: " + jsonNotification);

                    session.sendMessage(new TextMessage(jsonNotification));
                    System.out.println("✅ Notification sent successfully.");
                } catch (IOException e) {
                    System.err.println("❌ Failed to send WebSocket notification: " + e.getMessage());
                }
            } else {
                System.err.println("❌ WebSocket session for user ID " + voyageur.getId() + " is closed.");
            }
        } else {
            System.err.println("🚨 No WebSocket session found for user ID: " + voyageur.getId());
        }


    }

    // Helper method to convert notification to JSON
    private String convertToJson(Notification notification) {
        ObjectMapper mapper = new ObjectMapper();
        try {
            return mapper.writeValueAsString(notification);
        } catch (JsonProcessingException e) {
            throw new RuntimeException("Failed to serialize notification", e);
        }
    }

    public List<NotificationResponseDto> getUnreadNotifications() {
        try {
            // Get the authentication object
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

            // Extract CustomUserDetails from the authentication principal
            CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();
            String connectedUserEmail = userDetails.getUsername(); // Use getUsername() to get the email

            // Fetch the user entity by email
            Utilisateur connectedUser = utilisateurRepository.findByEmail(connectedUserEmail)
                    .orElseThrow(() -> new RuntimeException("Utilisateur non trouvé"));

            // Get unread notifications for the user
            List<Notification> notifications = notificationRepository.findByVoyageurIdAndIsReadFalse(connectedUser.getId());

            // Map notifications to response DTOs
            return notifications.stream()
                    .map(notification -> {
                        Demande demande = notification.getDemande();
                        DemandeResponse demandeResponse = new DemandeResponse();
                        demandeResponse.setId(demande.getId());
                        demandeResponse.setExpediteurId(demande.getExpediteur().getId());
                        demandeResponse.setExpediteurEmail(demande.getExpediteur().getEmail());
                        demandeResponse.setExpediteurNom(demande.getExpediteur().getNom());
                        demandeResponse.setStatus(demande.getStatus());
                        demandeResponse.setCreatedAt(demande.getCreatedAt());
                        demandeResponse.setVoyageurNom(demande.getVoyageur().getNom());

                        // Map InformationColis to InformationColisResponse
                        InformationColisResponse informationColisResponse = new InformationColisResponse();
                        informationColisResponse.setId(demande.getInformationColis().getId());
                        informationColisResponse.setNature(demande.getInformationColis().getNature());
                        informationColisResponse.setDimensions(demande.getInformationColis().getDimensions());
                        informationColisResponse.setPoids(demande.getInformationColis().getPoids());
                        informationColisResponse.setCategorie(demande.getInformationColis().getCategorie());
                        informationColisResponse.setDatePriseEnCharge(demande.getInformationColis().getDatePriseEnCharge());
                        informationColisResponse.setPlageHoraire(demande.getInformationColis().getPlageHoraire());

                        // Set InformationColisResponse in DemandeResponse
                        demandeResponse.setInformationColis(informationColisResponse);

                        // Create NotificationResponseDto
                        NotificationResponseDto responseDto = new NotificationResponseDto();
                        responseDto.setId(notification.getId());
                        responseDto.setMessage(notification.getMessage());
                        responseDto.setCreatedAt(notification.getCreatedAt());
                        responseDto.setRead(notification.isRead());
                        responseDto.setDemande(demandeResponse);

                        return responseDto;
                    })
                    .toList();
        } catch (Exception e) {
            throw new RuntimeException("Erreur lors de la récupération des notifications non lues", e);
        }
    }
    // Marquer une notification comme lue pour le voyageur connecté
    public void markAsRead(Long notificationId, Long voyageurId) {
        Notification notification = notificationRepository.findById(notificationId)
                .orElseThrow(() -> new RuntimeException("Notification non trouvée"));

        // Vérifier si la notification appartient au voyageur connecté
        if (!notification.getVoyageur().getId().equals(voyageurId)) {
            throw new RuntimeException("Accès refusé : Cette notification ne vous appartient pas.");
        }

        notification.setRead(true); // Marquer la notification comme lue
        notificationRepository.save(notification);
    }

}
