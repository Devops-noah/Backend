// Annonce.controller
package fr.parisnanterre.noah.Controller;

//import fr.parisnanterre.noah.DTO.AnnonceRequest;
import fr.parisnanterre.noah.DTO.Filtre;
import fr.parisnanterre.noah.Entity.Annonce;
import fr.parisnanterre.noah.Entity.Pays;
import fr.parisnanterre.noah.Service.AnnonceServiceImpl;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/annonces")
//@CrossOrigin(origins = "http://localhost:4200") // Enable CORS for Angular frontend
public class AnnonceController {

    private AnnonceServiceImpl annonceServiceImpl;

    @Autowired
    public AnnonceController(AnnonceServiceImpl annonceServiceImpl) {
        this.annonceServiceImpl = annonceServiceImpl;
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

    @CrossOrigin(origins = "http://localhost:3000")
    @PostMapping("/filter")
    public List<Annonce> getFilteredAnnonces(@Valid @RequestBody Filtre filtre) {
        return annonceServiceImpl.getFilteredAnnonces(filtre);
    }

    @PostMapping
    public ResponseEntity<?> createAnnonce(
            @Valid @RequestBody Annonce annonce,
            @RequestParam Long voyageId,
            @RequestParam String paysDepartNom,
            @RequestParam String paysDestinationNom
    ) {
        try {
            // Create the Annonce
            annonceServiceImpl.createAnnonce(annonce, voyageId, paysDepartNom, paysDestinationNom);
            return ResponseEntity.ok("Annonce created successfully");
        } catch (RuntimeException e) {
            // Handle invalid requests (e.g., non-Voyageurs trying to create an annonce)
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

