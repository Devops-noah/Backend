package fr.parisnanterre.noah.Controller;

import fr.parisnanterre.noah.Entity.Pays;
import fr.parisnanterre.noah.Entity.Utilisateur;
import fr.parisnanterre.noah.Entity.Voyage;
import fr.parisnanterre.noah.Service.VoyageServiceImpl;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
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
    public ResponseEntity<Voyage> getVoyageById(@PathVariable Long id) {
        return voyageServiceImpl.getVoyageById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    public ResponseEntity<?> createVoyage(
            @RequestBody Voyage voyage,
            @RequestParam Integer destinationId,
            Principal principal) {
        try {
            // Get the email of the logged-in user
            String email = principal.getName();

            // Call the service method to create the Voyage
            Voyage createdVoyage = voyageServiceImpl.createVoyage(voyage, destinationId, email);

            // Return the created Voyage
            return ResponseEntity.ok(createdVoyage);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

//    @PutMapping("/{id}")
//    public ResponseEntity<Voyage> updateVoyage(@PathVariable Long id, @RequestBody Voyage voyage) {
//        return ResponseEntity.ok(voyageServiceImpl.updateVoyage(id, voyage));
//    }

//    @DeleteMapping("/{id}")
//    public ResponseEntity<Void> deleteVoyage(@PathVariable Long id) {
//        voyageServiceImpl.deleteVoyage(id);
//        return ResponseEntity.noContent().build();
//    }
//
//    @GetMapping("/voyageur")
//    public List<Voyage> getVoyagesByVoyageur(@RequestParam Utilisateur voyageur) {
//        return voyageServiceImpl.getVoyagesByVoyageur(voyageur);
//    }
//
//    @GetMapping("/destination")
//    public List<Voyage> getVoyagesByDestination(@RequestParam Pays destination) {
//        return voyageServiceImpl.getVoyagesByDestination(destination);
//    }

}
