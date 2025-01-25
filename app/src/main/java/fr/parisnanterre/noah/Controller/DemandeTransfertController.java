package fr.parisnanterre.noah.Controller;

import fr.parisnanterre.noah.Entity.Segment;
import fr.parisnanterre.noah.Service.DemandeTransfertService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/demandeTransfert")
public class DemandeTransfertController {

    @Autowired
    private DemandeTransfertService demandeTransfertService;

    // Recherche des segments possibles en fonction des annonces
    @PostMapping("/recherche")
    public List<List<Segment>> rechercherSegments(@RequestParam String paysDepart, @RequestParam String paysArrivee) {
        return demandeTransfertService.rechercherSegments(paysDepart, paysArrivee);
    }

    // Enregistrer la chaîne choisie avec gestion des erreurs
    @PostMapping("/enregistrer")
    public ResponseEntity<String> enregistrerChaine(@RequestBody List<Segment> segmentsChoisis) {
        if (segmentsChoisis == null || segmentsChoisis.isEmpty()) {
            return ResponseEntity.badRequest().body("La liste des segments est manquante ou vide.");
        }

        try {
            // Appel du service pour enregistrer les segments
            demandeTransfertService.enregistrerChaine(segmentsChoisis);
            return ResponseEntity.status(HttpStatus.CREATED).body("Chaîne enregistrée avec succès.");
        } catch (Exception e) {
            // Log l'exception pour plus de détails
            System.err.println("Erreur lors de l'enregistrement de la chaîne: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Erreur lors de l'enregistrement de la chaîne: " + e.getMessage());
        }
    }
}
