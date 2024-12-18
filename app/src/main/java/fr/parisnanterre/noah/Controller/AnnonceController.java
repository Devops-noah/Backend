// Annonce.controller
package fr.parisnanterre.noah.Controller;

//import fr.parisnanterre.noah.DTO.AnnonceRequest;
import fr.parisnanterre.noah.DTO.AnnonceDTO;
import fr.parisnanterre.noah.DTO.Filtre;
import fr.parisnanterre.noah.Entity.Annonce;
import fr.parisnanterre.noah.Entity.Pays;
import fr.parisnanterre.noah.Entity.Utilisateur;
import fr.parisnanterre.noah.Entity.Voyageur;
import fr.parisnanterre.noah.Repository.UtilisateurRepository;
import fr.parisnanterre.noah.Service.AnnonceServiceImpl;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/annonces")
//@CrossOrigin(origins = "http://localhost:4200") // Enable CORS for Angular frontend
public class AnnonceController {

    private final AnnonceServiceImpl annonceServiceImpl;
    private final UtilisateurRepository utilisateurRepository;

    @Autowired
    public AnnonceController(AnnonceServiceImpl annonceServiceImpl, UtilisateurRepository utilisateurRepository) {
        this.annonceServiceImpl = annonceServiceImpl;
        this.utilisateurRepository = utilisateurRepository;
        System.out.println("AnnonceController: UtilisateurRepository bean injected successfully.");
    }

    @CrossOrigin(origins = "http://localhost:3000")
    @GetMapping
    public List<Annonce> getAllAnnonces() {
        return annonceServiceImpl.getAllAnnonces();
    }

    @GetMapping("/{id}")
    public ResponseEntity<Annonce> getAnnonceById(@PathVariable Integer id) {
        return annonceServiceImpl.getAnnonceById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    public ResponseEntity<?> createAnnonce(@RequestBody AnnonceDTO annonceDTO) {
        try {
            // Get authenticated user (voyageur) from the security context
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            String email = authentication.getName(); // Assuming the email is unique
            System.out.println("email value: " + email);

            Utilisateur utilisateur = utilisateurRepository.findByEmail(email)
                    .orElseThrow(() -> new RuntimeException("User not found"));

            System.out.println("utilisateur value: " + utilisateur);
            System.out.println("AnnonceServiceImpl: utilisateurRepository is " + (utilisateurRepository == null ? "null" : "not null"));

            // Check if the user is a Voyageur
            if (!(utilisateur instanceof Voyageur)) {
                throw new RuntimeException("Only Voyageurs can create annonces");
            }
            Voyageur voyageur = (Voyageur) utilisateur;

            // Map DTO to Annonce entity
            Annonce annonce = new Annonce();
            annonce.setDateDepart(annonceDTO.getDateDepart());
            annonce.setDateArrivee(annonceDTO.getDateArrivee());
            annonce.setDatePublication(annonceDTO.getDatePublication());
            annonce.setPoidsDisponible(annonceDTO.getPoidsDisponible());
            annonce.setVoyageur(voyageur); // Set the authenticated Voyageur

            // Call the service to save the annonce
            annonceServiceImpl.createAnnonce(
                    annonce,
                    annonceDTO.getVoyageId() != null ? annonceDTO.getVoyageId().longValue() : null,
                    annonceDTO.getPaysDepartNom(),
                    annonceDTO.getPaysDestinationNom()
            );

            return ResponseEntity.ok("Annonce created successfully");
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }



//    @PutMapping("/{id}")
//    public ResponseEntity<?> updateAnnonce(@PathVariable Integer id, @RequestBody AnnonceRequest annonceRequest) {
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

