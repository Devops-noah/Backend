package fr.parisnanterre.noah.Controller;

import fr.parisnanterre.noah.Entity.Pays;
import fr.parisnanterre.noah.Entity.Utilisateur;
import fr.parisnanterre.noah.Entity.Voyage;
import fr.parisnanterre.noah.Service.VoyageServiceImpl;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/voyages")
public class VoyageController {
    private final VoyageServiceImpl voyageServiceImpl;

    public VoyageController(VoyageServiceImpl voyageServiceImpl) {
        this.voyageServiceImpl = voyageServiceImpl;
    }

    @GetMapping
    public List<Voyage> getAllVoyages() {
        return voyageServiceImpl.getAllVoyages();
    }

    @GetMapping("/{id}")
    public ResponseEntity<Voyage> getVoyageById(@PathVariable Integer id) {
        return voyageServiceImpl.getVoyageById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping("/{destinationId}")
    public ResponseEntity<Voyage> createVoyage(@RequestBody Voyage voyage, @PathVariable Integer destinationId) {
        System.out.println("Received request to create a voyage");
        try {
            // Call the createVoyage method in the service
            Voyage createdVoyage = voyageServiceImpl.createVoyage(voyage, destinationId);
            System.out.println("Voyage created successfully");
            return ResponseEntity.ok(createdVoyage); // Return the created Voyage with status 200 OK
        } catch (RuntimeException e) {
            System.out.println("Error: " + e.getMessage());
            // Handle any exception, such as "Pays not found"
            return ResponseEntity.status(404).body(null); // Return a 404 error if destination is not found
        }
    }

}
