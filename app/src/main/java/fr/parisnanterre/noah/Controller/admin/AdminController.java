package fr.parisnanterre.noah.Controller.admin;

import fr.parisnanterre.noah.Entity.AdminType;
import fr.parisnanterre.noah.Entity.Utilisateur;
import fr.parisnanterre.noah.Service.admin.AdminServiceImpl;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/admin")
@RequiredArgsConstructor
public class AdminController {

    private final AdminServiceImpl adminServiceImpl;

    /**
     * Endpoint to create an Admin user.
     *
     * @param adminType AdminType object
     * @return Created AdminType entity
     */

    @PostMapping("/users/create-admin")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public ResponseEntity<AdminType> createAdminUser(@RequestBody AdminType adminType) {
        AdminType createdAdmin = adminServiceImpl.createAdminUser(adminType.getNom(), adminType.getPrenom(), adminType.getEmail(), adminType.getMotDePasse(), adminType.getTelephone(), adminType.getAdresse());
        return ResponseEntity.ok(createdAdmin);
    }

    /**
     * Endpoint to fetch all users.
     *
     * @return A list of all users
     */
    @GetMapping("/users")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public ResponseEntity<Page<Utilisateur>> getAllUsers(Pageable pageable) {
        Page<Utilisateur> users = adminServiceImpl.getAllUsers(pageable);
        return ResponseEntity.ok(users);
    }

    /**
     * Endpoint to suspend or reactivate a user account.
     *
     * @param userId  ID of the user to suspend or reactivate
     * @param suspend true to suspend, false to reactivate
     * @return The updated user
     */
    @PutMapping("/users/{userId}/suspend")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public ResponseEntity<Utilisateur> suspendOrReactivateUser(@PathVariable Long userId,
                                                               @RequestParam boolean suspend) {
        Utilisateur updatedUser = adminServiceImpl.suspendOrReactivateUser(userId, suspend);
        return ResponseEntity.ok(updatedUser);
    }

    /**
     * Activate a user account by ID.
     *
     * @param userId The ID of the user to activate.
     * @return The updated Utilisateur object.
     */
    @PutMapping("/users/{userId}/activate")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Utilisateur> activateUtilisateur(@PathVariable Long userId) {
        try {
            Utilisateur activatedUser = adminServiceImpl.activateUtilisateur(userId);
            return ResponseEntity.ok(activatedUser);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(null); // Handle invalid user ID
        }
    }


}
