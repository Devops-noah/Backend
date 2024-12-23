// Annonce.controller
package fr.parisnanterre.noah.Controller;

import fr.parisnanterre.noah.DTO.AnnonceRequest;
import fr.parisnanterre.noah.DTO.AnnonceResponse;
import fr.parisnanterre.noah.DTO.Filtre;
import fr.parisnanterre.noah.Entity.Annonce;
import fr.parisnanterre.noah.Entity.Pays;
import fr.parisnanterre.noah.Entity.Voyageur;
import fr.parisnanterre.noah.Repository.AnnonceRepository;
import fr.parisnanterre.noah.Repository.UtilisateurRepository;
import fr.parisnanterre.noah.Service.AnnonceServiceImpl;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.List;

@RestController
@RequestMapping("/api/annonces")
//@CrossOrigin(origins = "http://localhost:4200") // Enable CORS for Angular frontend
public class AnnonceController {

    private final AnnonceServiceImpl annonceServiceImpl;
    private final UtilisateurRepository utilisateurRepository;
    private final AnnonceRepository annonceRepository;

    @Autowired
    public AnnonceController(AnnonceServiceImpl annonceServiceImpl, UtilisateurRepository utilisateurRepository,
                             AnnonceRepository annonceRepository) {
        this.annonceServiceImpl = annonceServiceImpl;
        this.utilisateurRepository = utilisateurRepository;
        System.out.println("utilisateur repository: " + this.utilisateurRepository);
        this.annonceRepository = annonceRepository;
    }

    @CrossOrigin(origins = "http://localhost:3000")
    @GetMapping
    public ResponseEntity<List<AnnonceResponse>> getAllAnnonces() {
        return ResponseEntity.ok(annonceServiceImpl.getAllAnnonces());
    }

    @GetMapping("/{id}")
    public ResponseEntity<AnnonceResponse> getAnnonceById(@PathVariable Integer id) {
        return annonceServiceImpl.getAnnonceById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @CrossOrigin(origins = "http://localhost:3000")
    @PostMapping("/filter")
    public List<Annonce> getFilteredAnnonces(@Valid @RequestBody Filtre filtre) {
        return annonceServiceImpl.getFilteredAnnonces(filtre);
    }

    @PostMapping("/{voyageId}")
    public ResponseEntity<?> createAnnonce(@PathVariable Integer voyageId, @Valid @RequestBody AnnonceRequest annonceRequest, Principal principal) {
        try {
            // Extract email of the logged-in user
            String email = principal.getName();

            // Create a new Annonce object (this could be passed from the frontend as well)
            Annonce annonce = new Annonce();

            // Call the service to create the Annonce
            AnnonceResponse response = annonceServiceImpl.createAnnonce(annonce, annonceRequest, email, voyageId);
            System.out.println("annonce response received: " + response);

            // Return the response with the created Annonce data
            return ResponseEntity.ok(response);

        } catch (Exception e) {
            // Handle error and return bad request response with the error message
            return ResponseEntity.badRequest().body("Error: " + e.getMessage());
        }
    }



//    @PutMapping("/{id}")
//    public ResponseEntity<?> updateAnnonce(@PathVariable Integer id, @RequestBody fr.parisnanterre.noah.DTO.AnnonceRequest annonceRequest) {
//        Annonce updateAnnonce = annonceServiceImpl.updateAnnonce(id, annonceRequest);
//        return ResponseEntity.ok("Annonce updated successfully");
//    }

    @DeleteMapping("/{id}")
    public void deleteAnnonce(@PathVariable Integer id) {
        annonceServiceImpl.deleteAnnonce(id);
    }

    public List<Annonce> getAnnoncesByPaysDepart(String paysDepartNom) {
        return annonceServiceImpl.getAnnoncesByPaysDepart(paysDepartNom);
    }

    @GetMapping("/destination")
    public List<Annonce> getAnnoncesByPaysDestination(String paysDestinationNom) {
        return annonceServiceImpl.getAnnoncesByPaysDestination(paysDestinationNom);
    }

//    @CrossOrigin(origins = "http://localhost:3000")
//    @PostMapping("/filter")
//    public List<Annonce> getFilteredAnnonces(@RequestBody Filtre filtre) {
//        return annonceServiceImpl.getFilteredAnnonces(filtre);
//    }


}

