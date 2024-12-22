// Annonce.controller
package fr.parisnanterre.noah.Controller;

import fr.parisnanterre.noah.DTO.AnnonceRequest;
import fr.parisnanterre.noah.DTO.AnnonceResponseDto;
import fr.parisnanterre.noah.DTO.Filtre;
import fr.parisnanterre.noah.Entity.Annonce;
import fr.parisnanterre.noah.Entity.Pays;
import fr.parisnanterre.noah.Entity.Voyageur;
import fr.parisnanterre.noah.Repository.AnnonceRepository;
import fr.parisnanterre.noah.Repository.UtilisateurRepository;
import fr.parisnanterre.noah.Service.AnnonceServiceImpl;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.*;

import java.sql.Date;
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
    public ResponseEntity<List<AnnonceResponseDto>> getAllAnnonces() {
        return ResponseEntity.ok(annonceServiceImpl.getAllAnnonces());
    }

    @GetMapping("/{id}")
    public ResponseEntity<Annonce> getAnnonceById(@PathVariable Integer id) {
        return annonceServiceImpl.getAnnonceById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @CrossOrigin(origins = "http://localhost:3000")
    @PostMapping("/filter")
    public List<Annonce> getFilteredAnnonces(@Valid @RequestBody Filtre filtre) {
        return annonceServiceImpl.getFilteredAnnonces(filtre);
    }

    @PostMapping
    public ResponseEntity<?> createAnnonce(@Valid @RequestBody AnnonceRequest annonceRequest) {
        try {
            // Log the incoming request
            System.out.println("Received request: " + annonceRequest);

            // Create a new Annonce object from the request data
            Annonce annonce = new Annonce();
            annonce.setDateDepart(annonceRequest.getDateDepart());  // Convert string to Date
            annonce.setDateArrivee(annonceRequest.getDateArrivee()); // Convert string to Date
            annonce.setDatePublication(annonceRequest.getDatePublication()); // Convert string to Date
            annonce.setPoidsDisponible(annonceRequest.getPoidsDisponible());

            // Fetch the Voyageur (replace 1L with dynamic ID if available)
            Voyageur voyageur = (Voyageur) utilisateurRepository.findById(1L)
                    .orElseThrow(() -> new RuntimeException("Voyageur not found"));
            annonce.setVoyageur(voyageur);

            // Ensure the fields are set properly
            System.out.println("Annonce details: " + annonce);

            // Call service to create annonce
            annonceServiceImpl.createAnnonce(
                    annonce,
                    annonceRequest.getVoyageId(),
                    annonceRequest.getPaysDepart(),
                    annonceRequest.getPaysDestination()
            );

            return ResponseEntity.ok("Annonce created successfully");
        } catch (Exception e) {
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

    public List<Annonce> getAnnoncesByPaysDepart(Pays paysDepart) {
        return annonceServiceImpl.getAnnoncesByPaysDepart(paysDepart);
    }

    @GetMapping("/destination")
    public List<Annonce> getAnnoncesByPaysDestination(@RequestParam Pays paysDest) {
        return annonceServiceImpl.getAnnoncesByPaysDestination(paysDest);
    }

//    @CrossOrigin(origins = "http://localhost:3000")
//    @PostMapping("/filter")
//    public List<Annonce> getFilteredAnnonces(@RequestBody Filtre filtre) {
//        return annonceServiceImpl.getFilteredAnnonces(filtre);
//    }


}

