package fr.parisnanterre.noah.Controller;

import fr.parisnanterre.noah.DTO.UtilisateurProfileResponse;
import fr.parisnanterre.noah.DTO.UtilisateurRequest;
import fr.parisnanterre.noah.Entity.Utilisateur;
import fr.parisnanterre.noah.Repository.UtilisateurRepository;
import fr.parisnanterre.noah.Service.ImageServiceImpl;
import fr.parisnanterre.noah.Service.UtilisateurServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.FileSystemResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.nio.file.Paths;
import java.security.Principal;
import java.util.Arrays;
import java.util.List;

@RestController
@RequestMapping("/api/utilisateurs")
public class UtilisateurController {
    @Autowired
    private UtilisateurServiceImpl utilisateurService;

    @Autowired
    private ImageServiceImpl imageServiceImpl;

    @Autowired
    private UtilisateurRepository utilisateurRepository;

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

    @PostMapping("/profile/upload-image")
    public ResponseEntity<String> uploadProfileImage(
            Principal principal,
            @RequestParam("file") MultipartFile file
    ) {
        try {
            String email = principal.getName(); // Get the logged-in user's email
            Long userId = utilisateurService.getUserIdByEmail(email); // Retrieve user ID

            // Upload the image to Imgur and get the URL
            String imgurImageUrl = imageServiceImpl.uploadImageToImgur(file.getBytes());

            // Update the user's profile image URL in the database
            utilisateurService.updateUserProfileImage(userId, imgurImageUrl);

            return ResponseEntity.ok("Profile image uploaded successfully");
        } catch (IOException e) {
            return ResponseEntity.status(500).body("Failed to upload image to Imgur");
        }
    }

    //private static final String UPLOADS_DIR = "C:\\Users\\dell\\Desktop\\Nanterre-miage\\Master 1\\semestre 1\\Methodes-outils-developpement-logiciel\\Model-devops-Damien\\Devops-noah\\Backend\\app\\uploads\\";


    @GetMapping("/profiles/images/{userId}")
    public ResponseEntity<String> getProfileImage(@PathVariable Long userId) {
        try {
            String imageUrl = utilisateurService.getUserProfileImageUrl(userId);
            if (imageUrl == null || imageUrl.isEmpty()) {
                return ResponseEntity.notFound().build();
            }
            return ResponseEntity.ok(imageUrl);
        } catch (Exception e) {
            return ResponseEntity.status(500).build();
        }
    }


    @PutMapping("/profile/update-image")
    public ResponseEntity<String> updateProfileImage(
            Principal principal,
            @RequestParam("file") MultipartFile file
    ) {
        try {
            String email = principal.getName(); // Get the logged-in user's email
            Long userId = utilisateurService.getUserIdByEmail(email); // Retrieve user ID

            // Update the profile image as binary data
            String newImageData = Arrays.toString(imageServiceImpl.updateProfileImage(file));

            utilisateurService.updateUserProfileImage(userId, newImageData);

            return ResponseEntity.ok("Profile image updated successfully");
        } catch (IOException e) {
            return ResponseEntity.status(500).body("Failed to update image");
        }
    }

    @PutMapping("/profile/update/{userId}")
    public ResponseEntity<Utilisateur> updateUtilisateur(
            @PathVariable Long userId,
            @RequestBody UtilisateurRequest request
    ) {
        Utilisateur updatedUtilisateur = utilisateurService.updateUtilisateur(
                userId,
                request.getNom(),
                request.getPrenom(),
                request.getTelephone(),
                request.getAdresse()
        );
        return ResponseEntity.ok(updatedUtilisateur);
    }

    // Récupérer le nom de l'expéditeur par son ID
    @GetMapping("/expediteur/{expediteurId}")
    public String getExpediteurNom(@PathVariable Long expediteurId) {
        return utilisateurService.getExpediteurNom(expediteurId);
    }
}
