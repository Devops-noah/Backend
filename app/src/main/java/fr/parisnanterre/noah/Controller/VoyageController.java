package fr.parisnanterre.noah.Controller;

import fr.parisnanterre.noah.DTO.VoyageRequest;
import fr.parisnanterre.noah.DTO.VoyageResponse;
import fr.parisnanterre.noah.Entity.Pays;
import fr.parisnanterre.noah.Entity.Utilisateur;
import fr.parisnanterre.noah.Entity.Voyage;
import fr.parisnanterre.noah.Service.VoyageServiceImpl;
import org.springframework.http.HttpStatus;
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

    // GET endpoint to fetch all voyages
    @GetMapping
    public List<VoyageResponse> getAllVoyages() {
        return voyageServiceImpl.getAllVoyages();
    }

    @GetMapping("/{id}")
    public ResponseEntity<Voyage> getVoyageById(@PathVariable Integer id) {
        return voyageServiceImpl.getVoyageById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    public ResponseEntity<?> createVoyage(
            @RequestBody VoyageRequest voyageRequest,
            Principal principal) {
        try {
            // Get the email of the logged-in user
            String email = principal.getName();

            // Call the service method to create the Voyage
            VoyageResponse createdVoyage = voyageServiceImpl.createVoyage(
                    voyageRequest.getVoyage(),
                    voyageRequest.getPaysDepart(),
                    voyageRequest.getPaysDestination(),
                    email
            );
            System.out.println("created voyage: " + createdVoyage);

            // Return the created Voyage with status 201 (Created)
            return ResponseEntity.status(HttpStatus.CREATED).body(createdVoyage);
        } catch (RuntimeException e) {
            // Return a 400 (Bad Request) with the exception message
            return ResponseEntity.badRequest().body(e.getMessage());
        } catch (Exception e) {
            // Catch-all for unexpected errors, returning a 500 (Internal Server Error)
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("An unexpected error occurred.");
        }
    }


    @PutMapping("/update/{voyageId}")
    public ResponseEntity<?> updateVoyage(
            @PathVariable Integer voyageId,
            @RequestBody VoyageRequest voyageRequest,
            Principal principal) {
        try {
            // Get the email of the logged-in user
            String email = principal.getName();

            // Call the service to update the voyage
            Voyage updatedVoyage = voyageServiceImpl.updateVoyage(voyageId, voyageRequest, email);

            // Return the updated voyage
            return ResponseEntity.ok(updatedVoyage);
        } catch (RuntimeException e) {
            // Return a 400 (Bad Request) with the exception message
            return ResponseEntity.badRequest().body(e.getMessage());
        } catch (Exception e) {
            // Catch-all for unexpected errors
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("An unexpected error occurred.");
        }
    }

    // Delete voyage
    @DeleteMapping("/delete/{voyageId}")
    public ResponseEntity<?> deleteVoyage(
            @PathVariable Integer voyageId,
            Principal principal) {
        try {
            // Get the email of the logged-in user
            String email = principal.getName();

            // Call the service to delete the voyage
            voyageServiceImpl.deleteVoyage(voyageId, email);

            // Return a success response
            return ResponseEntity.ok("Voyage deleted successfully");
        } catch (RuntimeException e) {
            // Return a 400 (Bad Request) with the exception message
            return ResponseEntity.badRequest().body(e.getMessage());
        } catch (Exception e) {
            // Catch-all for unexpected errors
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("An unexpected error occurred.");
        }
    }



}
