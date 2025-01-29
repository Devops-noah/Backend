package fr.parisnanterre.noah.Controller;

import fr.parisnanterre.noah.DTO.NotificationResponseDto;
import fr.parisnanterre.noah.Entity.Notification;
import fr.parisnanterre.noah.Service.NotificationService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/notifications")
@RequiredArgsConstructor
public class NotificationController {

    private final NotificationService notificationService;

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
