package fr.parisnanterre.noah.Controller;

import fr.parisnanterre.noah.Entity.Notation;
import fr.parisnanterre.noah.DTO.NotationRequest;
import fr.parisnanterre.noah.Service.NotationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/notations")
public class NotationController {

    @Autowired
    private NotationService notationService;

    // API pour ajouter ou mettre à jour une notation
    @PostMapping("/update-or-create")
    public ResponseEntity<Notation> updateOrCreateNotation(@RequestBody NotationRequest notationRequest) {
        try {
            Notation notation = notationService.updateOrCreateNotation(notationRequest);
            return ResponseEntity.ok(notation);
        } catch (Exception e) {
            return ResponseEntity.status(400).body(null);
        }
    }

    // API pour récupérer toutes les notations
    @GetMapping("/")
    public ResponseEntity<List<Notation>> getAllNotations() {
        List<Notation> notations = notationService.getAllNotations();
        return ResponseEntity.ok(notations);
    }

    // API pour récupérer les notations par utilisateur
    @GetMapping("/user/{utilisateurId}")
    public ResponseEntity<List<Notation>> getNotationsByUtilisateurId(@PathVariable Long utilisateurId) {
        List<Notation> notations = notationService.getNotationsByUtilisateurId(utilisateurId);
        return ResponseEntity.ok(notations);
    }

    // API pour récupérer les 3 dernières notations
    @GetMapping("/last-three")
    public ResponseEntity<List<Notation>> getLastThreeNotations() {
        List<Notation> notations = notationService.getLastThreeNotations();
        return ResponseEntity.ok(notations);
    }
}
