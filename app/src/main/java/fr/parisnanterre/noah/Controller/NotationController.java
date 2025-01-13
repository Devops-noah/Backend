package fr.parisnanterre.noah.Controller;

import fr.parisnanterre.noah.Entity.Notation;
import fr.parisnanterre.noah.DTO.NotationRequest;
import fr.parisnanterre.noah.Service.NotationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/notations")
public class NotationController {

    private final NotationService notationService;

    @Autowired
    public NotationController(NotationService notationService) {
        this.notationService = notationService;
    }

    // API pour créer une nouvelle notation
    @PostMapping("/create")
    public ResponseEntity<Notation> createNotation(@RequestBody NotationRequest notationRequest) {
        try {
            Notation notation = notationService.createNotation(notationRequest);
            return ResponseEntity.ok(notation);
        } catch (Exception e) {
            return ResponseEntity.status(400).body(null);
        }
    }

    // API pour récupérer toutes les notations
    @GetMapping("/get-notations")
    public ResponseEntity<List<Notation>> getAllNotations() {
        try {
            List<Notation> notations = notationService.getAllNotations();
            System.out.println("notations controller: " + notations.get(0).getUtilisateur().getNom());
            return ResponseEntity.ok(notations);
        } catch (AccessDeniedException e) {
            System.err.println("Access Denied: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }
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
