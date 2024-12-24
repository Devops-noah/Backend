package fr.parisnanterre.noah.Controller;

import fr.parisnanterre.noah.DTO.UtilisateurProfileResponse;
import fr.parisnanterre.noah.Entity.Utilisateur;
import fr.parisnanterre.noah.Service.UtilisateurServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.List;

@RestController
@RequestMapping("/api/utilisateurs")
public class UtilisateurController {
    @Autowired
    private UtilisateurServiceImpl utilisateurService;

    @GetMapping
    public List<Utilisateur> getAllUtilisateurs() {
        return utilisateurService.getAllUtilisateurs();
    }

    @GetMapping("/profile")
    public ResponseEntity<UtilisateurProfileResponse> getLoggedInUserProfile(Principal principal) {
        // Get the email of the currently authenticated user
        String email = principal.getName(); // This comes from Spring Security
        UtilisateurProfileResponse profile = utilisateurService.getUserProfile(email);
        return ResponseEntity.ok(profile);
    }

//    @GetMapping("/{id}")
//    public ResponseEntity<Utilisateur> getUtilisateurById(@PathVariable Long id) {
//        return utilisateurService.getUtilisateurById(id)
//                .map(ResponseEntity::ok)
//                .orElse(ResponseEntity.notFound().build());
//    }

//    @PostMapping
//    public Utilisateur createUtilisateur(@RequestBody Utilisateur utilisateur) {
//        return utilisateurService.createUtilisateur(utilisateur);
//    }
//
//    @PutMapping("/{id}")
//    public ResponseEntity<Utilisateur> updateUtilisateur(@PathVariable Integer id, @RequestBody Utilisateur utilisateur) {
//        return ResponseEntity.ok(utilisateurService.updateUtilisateur(id, utilisateur));
//    }
//
//    @DeleteMapping("/{id}")
//    public ResponseEntity<Void> deleteUtilisateur(@PathVariable Integer id) {
//        utilisateurService.deleteUtilisateur(id);
//        return ResponseEntity.noContent().build();
//    }
//
//    @GetMapping("/email")
//    public ResponseEntity<Utilisateur> getUtilisateurByEmail(@RequestParam String email) {
//        return utilisateurService.getUtilisateurByEmail(email)
//                .map(ResponseEntity::ok)
//                .orElse(ResponseEntity.notFound().build());
//    }
}
