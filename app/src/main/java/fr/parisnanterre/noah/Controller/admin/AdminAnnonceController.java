package fr.parisnanterre.noah.Controller.admin;

import fr.parisnanterre.noah.Entity.Annonce;
import fr.parisnanterre.noah.Service.admin.AdminServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/admin/annonces")
public class AdminAnnonceController {
    private final AdminServiceImpl adminServiceImpl;

    @Autowired
    public AdminAnnonceController(AdminServiceImpl adminServiceImpl) {
        this.adminServiceImpl = adminServiceImpl;
    }

    // Get all annonces
    @GetMapping("/all")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<Annonce>> getAllAnnonces() {
        List<Annonce> annonces = adminServiceImpl.getAllAnnonces();
        return ResponseEntity.ok(annonces);
    }

    // Approve an annonce
    @PutMapping("/approve/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> approveAnnonce(@PathVariable("id") Integer annonceId) {
        try {
            Annonce approvedAnnonce = adminServiceImpl.approveAnnonce(annonceId);
            return ResponseEntity.ok(approvedAnnonce);
        } catch (IllegalArgumentException e) {
            // Return 404 Not Found with a custom error message
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("{\"error\": \"" + e.getMessage() + "\"}");
        }
    }

    // Delete an annonce
    @DeleteMapping("/delete/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<String> deleteAnnonce(@PathVariable("id") Integer annonceId) {
        try {
            adminServiceImpl.deleteAnnonce(annonceId);
            return ResponseEntity.noContent().build();  // No content means success with no response body
        } catch (IllegalArgumentException e) {
            // Return 404 Not Found with a custom error message
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("{\"error\": \"" + e.getMessage() + "\"}");
        }


    }

    // Suspend an annonce
    @PutMapping("/suspend/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> suspendAnnonce(@PathVariable("id") Integer annonceId) {
        try {
            Annonce suspendedAnnonce = adminServiceImpl.suspendAnnonce(annonceId);
            return ResponseEntity.ok(suspendedAnnonce);
        } catch (IllegalArgumentException e) {
            // Return 404 Not Found with a custom error message
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("{\"error\": \"" + e.getMessage() + "\"}");
        }
    }
}
