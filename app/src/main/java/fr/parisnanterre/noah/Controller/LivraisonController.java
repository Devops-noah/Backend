package fr.parisnanterre.noah.Controller;

import fr.parisnanterre.noah.Entity.Livraison;
import fr.parisnanterre.noah.Service.LivraisonService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/livraisons")
@RequiredArgsConstructor
public class LivraisonController {

    private final LivraisonService livraisonService;

    @GetMapping
    public ResponseEntity<List<Livraison>> getAllLivraisons() {
        return ResponseEntity.ok(livraisonService.getAllLivraisons());
    }

    @GetMapping("/{id}")
    public ResponseEntity<Livraison> getLivraisonById(@PathVariable int id) {
        return ResponseEntity.ok(livraisonService.getLivraisonById(id));
    }
}
