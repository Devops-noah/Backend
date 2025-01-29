package fr.parisnanterre.noah.Controller.admin;

import fr.parisnanterre.noah.DTO.NotationResponse;
import fr.parisnanterre.noah.Entity.Notation;
import fr.parisnanterre.noah.Service.admin.AdminServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/admin/notations")
public class AdminNotationCommentsController {

    private final AdminServiceImpl adminServiceImpl;

    @Autowired
    public AdminNotationCommentsController(AdminServiceImpl adminServiceImpl) {
        this.adminServiceImpl = adminServiceImpl;
    }
    // API pour récupérer toutes les notations
    @GetMapping("/get-notations")
    public ResponseEntity<List<NotationResponse>> getAllNotations() {
        try {
            // Fetch notations using the service
            List<NotationResponse> notations = adminServiceImpl.getAllNotations();

            // Log the first user's name (optional, for debugging)
            if (!notations.isEmpty()) {
                System.out.println("First user's name: " + notations.get(0).getUserName());
            }

            // Return the list of notations
            return ResponseEntity.ok(notations);
        } catch (AccessDeniedException e) {
            System.err.println("Access Denied: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        } catch (Exception e) {
            System.err.println("Error: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    // Approve a comment
    @PutMapping("/approve/{id}")
    @PreAuthorize("hasAnyRole('ADMIN')")
    public ResponseEntity<NotationResponse> approveNotation(@PathVariable Long id) {
        System.out.println("Approving ID: " + id); // Debugging the received ID
        try {
            // Call the service method to approve the notation
            NotationResponse notationResponse = adminServiceImpl.approveNotation(id);
            System.out.println("Notation approved: " + notationResponse);

            // Return the DTO as the response
            return ResponseEntity.ok(notationResponse);
        } catch (Exception e) {
            System.err.println("Error approving notation: " + e.getMessage());
            return ResponseEntity.status(404).body(null);
        }
    }

    @PutMapping("/suspend/{id}")
    @PreAuthorize("hasAnyRole('ADMIN')")
    public ResponseEntity<NotationResponse> suspendNotation(@PathVariable Long id) {
        System.out.println("Approving ID: " + id); // Debugging the received ID
        try {
            // Call the service method to approve the notation
            NotationResponse notationResponse = adminServiceImpl.suspendNotation(id);
            System.out.println("Notation approved: " + notationResponse);

            // Return the DTO as the response
            return ResponseEntity.ok(notationResponse);
        } catch (Exception e) {
            System.err.println("Error approving notation: " + e.getMessage());
            return ResponseEntity.status(404).body(null);
        }
    }

    // Fetch pending comments for admin
    @GetMapping("/pending")
    public ResponseEntity<List<Notation>> getPendingNotations() {
        List<Notation> notations = adminServiceImpl.getPendingNotations();
        return ResponseEntity.ok(notations);
    }
}
