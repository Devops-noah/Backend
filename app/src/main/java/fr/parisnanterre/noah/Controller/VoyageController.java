package fr.parisnanterre.noah.Controller;

import fr.parisnanterre.noah.DTO.GoogleCalendarEventDto;
import fr.parisnanterre.noah.DTO.VoyageRequest;
import fr.parisnanterre.noah.DTO.VoyageResponse;
import fr.parisnanterre.noah.Entity.Pays;
import fr.parisnanterre.noah.Entity.Utilisateur;
import fr.parisnanterre.noah.Entity.Voyage;
import fr.parisnanterre.noah.Repository.UtilisateurRepository;
import fr.parisnanterre.noah.Repository.VoyageRepository;
import fr.parisnanterre.noah.Service.GoogleCalendarService;
import fr.parisnanterre.noah.Service.UtilisateurServiceImpl;
import fr.parisnanterre.noah.Service.VoyageServiceImpl;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.security.Principal;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/voyages")
public class VoyageController {
    private final VoyageServiceImpl voyageServiceImpl;
    private final UtilisateurServiceImpl utilisateurServiceImpl;

    private final GoogleCalendarService googleCalendarService;
    private final UtilisateurRepository utilisateurRepository;
    private final VoyageRepository voyageRepository;

    public VoyageController(VoyageServiceImpl voyageServiceImpl,
                            UtilisateurServiceImpl utilisateurServiceImpl,
                            GoogleCalendarService googleCalendarService,
                            UtilisateurRepository utilisateurRepository,
                            VoyageRepository voyageRepository) {
        this.voyageServiceImpl = voyageServiceImpl;
        this.utilisateurServiceImpl = utilisateurServiceImpl;
        this.googleCalendarService = googleCalendarService;
        this.utilisateurRepository = utilisateurRepository;
        this.voyageRepository = voyageRepository;
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
    public ResponseEntity<?> createVoyage(@RequestBody VoyageRequest voyageRequest, Principal principal) {
        try {
            String email = principal.getName();
            Optional<Utilisateur> utilisateurOpt = utilisateurRepository.findByEmail(email);

            if (utilisateurOpt.isEmpty()) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body("User not found.");
            }

            Utilisateur utilisateur = utilisateurOpt.get();

            if (utilisateur.getGoogleRefreshToken() == null) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN)
                        .body("User has not authorized Google Calendar. Please log in and authorize.");
            }

            VoyageResponse createdVoyage = voyageServiceImpl.createVoyage(
                    voyageRequest.getVoyage(),
                    voyageRequest.getPaysDepart(),
                    voyageRequest.getPaysDestination(),

                    email
            );
            System.out.println("value of createdVoyage: " + createdVoyage);

            System.out.println("🟢 Voyage created successfully. Proceeding to create calendar event...");
            //googleCalendarService.createEventInGoogleCalendar(email, createdVoyage.getVoyageurNom() + " Voyage", createdVoyage.getDateDepart());
            googleCalendarService.createEventInGoogleCalendar(
                    email,
                    "[TravelCarry] " + createdVoyage.getVoyageurNom() + " Voyage de " +
                            createdVoyage.getPaysDepartNom() + " à " +
                            createdVoyage.getPaysDestinationNom(),
                    createdVoyage.getDateDepart()
            );


            System.out.println("🟢 Calendar event creation attempted.");
            return ResponseEntity.status(HttpStatus.CREATED).body(createdVoyage);
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("An error occurred.");
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
    public ResponseEntity<?> deleteVoyage(@PathVariable Integer voyageId, Principal principal) {
        try {
            String email = principal.getName();

            // Step 1: Get the voyage to retrieve Google Event ID
            Voyage voyage = voyageRepository.findById(voyageId)
                    .orElseThrow(() -> new RuntimeException("Voyage not found"));

            // Step 2: Delete the associated Google Calendar event
            if (voyage.getGoogleEventId() != null) {
                googleCalendarService.deleteEventById(email, voyage.getGoogleEventId());
                System.out.println("🗑️ Deleted Google Calendar event");
            }

            // Step 3: Delete the voyage from the database
            voyageServiceImpl.deleteVoyage(voyageId, email);
            return ResponseEntity.ok("Voyage and calendar event deleted successfully.");
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Unexpected error.");
        }
    }


    @GetMapping("/calendar/events")
    public ResponseEntity<?> getCalendarEvents(Authentication authentication) {
        String email = authentication.getName(); // Retrieved from JWT
        try {
            List<GoogleCalendarEventDto> events = googleCalendarService.getEvents(email);
            return ResponseEntity.ok(events);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
        }
    }





}
