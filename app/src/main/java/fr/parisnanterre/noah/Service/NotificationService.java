package fr.parisnanterre.noah.Service;
import fr.parisnanterre.noah.DTO.DemandeResponse;
import fr.parisnanterre.noah.DTO.InformationColisResponse;
import fr.parisnanterre.noah.DTO.NotificationResponseDto;
import fr.parisnanterre.noah.Entity.Demande;
import fr.parisnanterre.noah.Entity.Notification;
import fr.parisnanterre.noah.Entity.Utilisateur;
import fr.parisnanterre.noah.Repository.DemandeRepository;
import fr.parisnanterre.noah.Repository.NotificationRepository;
import fr.parisnanterre.noah.Repository.UtilisateurRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class NotificationService {

    private final NotificationRepository notificationRepository;
    private final UtilisateurRepository utilisateurRepository;
    private final DemandeRepository demandeRepository;

    // Créer la notification après la création d'une demande
    public void createNotification(Long demandeId) {
        // Récupérer la demande
        Demande demande = demandeRepository.findById(demandeId)
                .orElseThrow(() -> new RuntimeException("Demande non trouvée"));

        // Récupérer le voyageur à qui la notification appartient
        Utilisateur voyageur = demande.getInformationColis().getAnnonce().getVoyageur(); // C'est le voyageur qui reçoit la notification

        // Créer le message pour la notification
        String message = "Nouvelle demande reçue pour votre colis proposé : " + demande.getInformationColis().getDimensions();

        // Créer la notification
        Notification notification = new Notification();
        notification.setMessage(message);
        notification.setVoyageur(voyageur);  // Le voyageur qui reçoit la notification
        notification.setDemande(demande);  // Lier la notification à la demande
        notification.setCreatedAt(new Date());
        notification.setRead(false);

        // Sauvegarder la notification dans la base de données
        notificationRepository.save(notification);

        // Mettre à jour le compteur de notifications non lues pour le voyageur
        voyageur.setNotificationCount(voyageur.getNotificationCount() + 1);
        utilisateurRepository.save(voyageur);  // Sauvegarder l'utilisateur avec le nouveau compteur
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
                        demandeResponse.setExpediteurEmail(demande.getExpediteur().getEmail());
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
    // Marquer une notification comme lue
    public void markAsRead(Long notificationId) {
        Notification notification = notificationRepository.findById(notificationId)
                .orElseThrow(() -> new RuntimeException("Notification non trouvée"));
        notification.setRead(true); // Marquer la notification comme lue
        notificationRepository.save(notification);
    }
}
