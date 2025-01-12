package fr.parisnanterre.noah.Controller;

import fr.parisnanterre.noah.Entity.Notification;
import fr.parisnanterre.noah.Service.NotificationService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/notifications")
@RequiredArgsConstructor
public class NotificationController {

    private final NotificationService notificationService;

    // Récupérer les notifications non lues pour un voyageur
    @GetMapping("/unread/{voyageurId}")
    public ResponseEntity<List<Notification>> getUnreadNotifications(@PathVariable Long voyageurId) {
        try {
            // Récupérer toutes les notifications non lues pour le voyageur avec l'ID spécifié
            List<Notification> notifications = notificationService.getUnreadNotifications(voyageurId);
            if (notifications.isEmpty()) {
                return ResponseEntity.noContent().build();  // Si aucune notification, retourne 204 No Content
            }
            return ResponseEntity.ok(notifications);  // Retourner les notifications non lues
        } catch (Exception e) {
            // Retourner une erreur HTTP 400 en cas d'échec
            return ResponseEntity.badRequest().body(null);
        }
    }

    // Marquer une notification comme lue
    @PutMapping("/read/{notificationId}")
    public ResponseEntity<String> markAsRead(@PathVariable Long notificationId) {
        try {
            // Marquer la notification comme lue
            notificationService.markAsRead(notificationId);
            return ResponseEntity.ok("Notification marquée comme lue");
        } catch (Exception e) {
            // Retourner une erreur HTTP 400 si l'opération échoue
            return ResponseEntity.badRequest().body("Erreur lors de la mise à jour du statut de notification");
        }
    }
}
