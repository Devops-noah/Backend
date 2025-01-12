package fr.parisnanterre.noah.Service;

import fr.parisnanterre.noah.Entity.Demande;
import fr.parisnanterre.noah.Entity.Notification;
import fr.parisnanterre.noah.Entity.Utilisateur;
import fr.parisnanterre.noah.Repository.DemandeRepository;
import fr.parisnanterre.noah.Repository.NotificationRepository;
import fr.parisnanterre.noah.Repository.UtilisateurRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;

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

    // Récupérer les notifications non lues pour un voyageur
    public List<Notification> getUnreadNotifications(Long voyageurId) {
        return notificationRepository.findByVoyageurIdAndIsReadFalse(voyageurId);
    }


    // Marquer une notification comme lue
    public void markAsRead(Long notificationId) {
        Notification notification = notificationRepository.findById(notificationId)
                .orElseThrow(() -> new RuntimeException("Notification non trouvée"));
        notification.setRead(true); // Marquer la notification comme lue
        notificationRepository.save(notification);
    }
}
