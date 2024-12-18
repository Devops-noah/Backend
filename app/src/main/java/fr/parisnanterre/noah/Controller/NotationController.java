package fr.parisnanterre.noah.Controller;

import fr.parisnanterre.noah.DTO.NotationDto;
import fr.parisnanterre.noah.Entity.Livraison;
import fr.parisnanterre.noah.Entity.Notation;
import fr.parisnanterre.noah.Service.LivraisonService;
import fr.parisnanterre.noah.Service.NotationService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/notations")
@RequiredArgsConstructor
public class NotationController {

    private final NotationService notationService;
    private final LivraisonService livraisonService;

    // Créer une notation à partir d'un NotationDto
    @PostMapping
    public ResponseEntity<Notation> createNotation(@RequestBody NotationDto notationDto) {
        Notation createdNotation = notationService.createNotationFromDto(notationDto);
        return ResponseEntity.ok(createdNotation);
    }

    // Récupérer les notations par livraison
    @GetMapping("/livraison/{livraisonId}")
    public ResponseEntity<List<Notation>> getNotationsByLivraison(@PathVariable int livraisonId) {
        Livraison livraison = livraisonService.getLivraisonById(livraisonId);
        List<Notation> notations = notationService.getNotationsByLivraison(livraison);
        return ResponseEntity.ok(notations);
    }

    // Calculer la note globale pour une livraison
    @GetMapping("/livraison/{livraisonId}/global-note")
    public ResponseEntity<Double> getGlobalNoteByLivraison(@PathVariable int livraisonId) {
        Livraison livraison = livraisonService.getLivraisonById(livraisonId);
        Double globalNote = notationService.calculateGlobalNote(livraison);
        return ResponseEntity.ok(globalNote);
    }
}
