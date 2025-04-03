package fr.parisnanterre.noah.Controller;

import fr.parisnanterre.noah.DTO.NotificationResponseDto;
import fr.parisnanterre.noah.Entity.Notification;
import fr.parisnanterre.noah.Entity.Utilisateur;
import fr.parisnanterre.noah.Entity.Voyageur;
import fr.parisnanterre.noah.Repository.UtilisateurRepository;
import fr.parisnanterre.noah.Service.NotificationService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.List;

@RestController
@RequestMapping("/api/notifications")
@RequiredArgsConstructor
public class NotificationController {

    private final NotificationService notificationService;
    private final UtilisateurRepository utilisateurRepository;

    @GetMapping("/unread")
    public ResponseEntity<List<NotificationResponseDto>> getUnreadNotifications() {
        try {
            // Retrieve unread notifications from the service
            List<NotificationResponseDto> unreadNotifications = notificationService.getUnreadNotifications();

            // Return 204 No Content if no notifications are found
            if (unreadNotifications.isEmpty()) {
                return ResponseEntity.noContent().build();
            }

            // Return the list of notifications
            return ResponseEntity.ok(unreadNotifications);
        } catch (Exception e) {
            // Log the exception if necessary
            e.printStackTrace();
            // Return 400 Bad Request in case of any errors
            return ResponseEntity.badRequest().build();
        }
    }


    @PutMapping("/read/{notificationId}")
    public ResponseEntity<String> markAsRead(@PathVariable Long notificationId, Principal principal) {
        try {
            // Récupérer l'ID du voyageur connecté depuis l'authentification
            Utilisateur voyageur = utilisateurRepository.findByEmail(principal.getName())
                    .orElseThrow(() -> new RuntimeException("Voyageur non trouvé"));

            // Marquer la notification comme lue seulement si elle appartient au voyageur connecté
            notificationService.markAsRead(notificationId, voyageur.getId());

            return ResponseEntity.ok("Notification marquée comme lue");
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Erreur : " + e.getMessage());
        }
    }

}
