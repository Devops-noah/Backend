// Annonce.controller
package fr.parisnanterre.noah.Controller;

import fr.parisnanterre.noah.DTO.Filtre;
import fr.parisnanterre.noah.Entity.Annonce;
import fr.parisnanterre.noah.Entity.Pays;
import fr.parisnanterre.noah.Service.AnnonceServiceImpl;
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
    @Autowired
    private AnnonceServiceImpl annonceServiceImpl;

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
    public void createAnnonce(@RequestBody Annonce annonce) {
        annonceServiceImpl.createAnnonce(annonce);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Annonce> updateAnnonce(@PathVariable Integer id, @RequestBody Annonce annonce) {
        return ResponseEntity.ok(annonceServiceImpl.updateAnnonce(id, annonce));
    }


}

