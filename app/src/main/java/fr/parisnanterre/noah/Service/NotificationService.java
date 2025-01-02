package fr.parisnanterre.noah.Service;

import fr.parisnanterre.noah.Entity.InformationColis;
import fr.parisnanterre.noah.Entity.Notification;
import fr.parisnanterre.noah.Entity.Utilisateur;
import fr.parisnanterre.noah.Repository.InformationColisRepository;
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
    private final InformationColisRepository informationColisRepository;

    public void createNotification(Utilisateur voyageur, String message, Long informationColisId) {
        // Charger l'objet InformationColis depuis la base de données
        InformationColis informationColis = informationColisRepository.findById(informationColisId)
                .orElseThrow(() -> new RuntimeException("InformationColis non trouvé avec l'ID : " + informationColisId));

        // Créer la notification
        Notification notification = new Notification();
        notification.setMessage(message);
        notification.setRead(false);
        notification.setCreatedAt(new Date());
        notification.setVoyageur(voyageur);
        notification.setInformationColis(informationColis); // Associer l'objet InformationColis

        // Sauvegarder la notification
        notificationRepository.save(notification);
    }


    public List<Notification> getUnreadNotifications(Long voyageurId) {
        return notificationRepository.findByVoyageurIdAndIsReadFalse(voyageurId);
    }

    public void markAsRead(Long notificationId) {
        Notification notification = notificationRepository.findById(notificationId)
                .orElseThrow(() -> new RuntimeException("Notification non trouvée"));
        notification.setRead(true);
        notificationRepository.save(notification);
    }
}
