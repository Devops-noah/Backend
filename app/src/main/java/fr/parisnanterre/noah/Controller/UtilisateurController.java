package fr.parisnanterre.noah.Controller;

import fr.parisnanterre.noah.DTO.UtilisateurProfileResponse;
import fr.parisnanterre.noah.DTO.UtilisateurRequest;
import fr.parisnanterre.noah.Entity.Utilisateur;
import fr.parisnanterre.noah.Service.ImageServiceImpl;
import fr.parisnanterre.noah.Service.UtilisateurServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.FileSystemResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.nio.file.Paths;
import java.security.Principal;
import java.util.List;

@RestController
@RequestMapping("/api/utilisateurs")
public class UtilisateurController {
    @Autowired
    private UtilisateurServiceImpl utilisateurService;

    @Autowired
    private ImageServiceImpl imageServiceImpl;

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

            // Save the image and update the user's profile image URL
            String imageUrl = imageServiceImpl.saveProfileImage(file, userId);
            System.out.println("image url: " + imageUrl);
            utilisateurService.updateUserProfileImage(userId, imageUrl);

            return ResponseEntity.ok(imageUrl);
        } catch (IOException e) {
            return ResponseEntity.status(500).body("Failed to upload image");
        }
    }

    private static final String UPLOADS_DIR = "C:\\Users\\dell\\Desktop\\Nanterre-miage\\Master 1\\semestre 1\\Methodes-outils-developpement-logiciel\\Model-devops-Damien\\Devops-noah\\Backend\\app\\uploads\\";


    // Serve profile image by filename
    @GetMapping("/profiles/images/{imageName}")
    public ResponseEntity<FileSystemResource> getProfileImage(@PathVariable String imageName) {
        System.out.println("uuuuuuuuuuuuuuuuuuuuuuuuuuuuuuuuuuuu");
        System.out.println("uploads dir: " + UPLOADS_DIR);
        try {
            // Construct the path to the image file
            File imageFile = Paths.get(UPLOADS_DIR, imageName).toFile();
            System.out.println("image file: " + imageFile);

            // Check if file exists
            if (!imageFile.exists()) {
                return ResponseEntity.notFound().build();
            }

            // Return the image as a response
            FileSystemResource resource = new FileSystemResource(imageFile);

            // Add content headers (for example, content type could be 'image/png')
            HttpHeaders headers = new HttpHeaders();
            headers.add(HttpHeaders.CONTENT_DISPOSITION, "inline; filename=\"" + imageName + "\"");

            return ResponseEntity.ok()
                    .headers(headers)
                    .body(resource);

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

            // Get the current profile image URL
            String existingFileName = utilisateurService.getProfileImageByUserId(userId);

            // Update the profile image and return the new image URL
            String newImageUrl = imageServiceImpl.updateProfileImage(file, userId, existingFileName);
            utilisateurService.updateUserProfileImage(userId, newImageUrl);

            return ResponseEntity.ok(newImageUrl);
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
}
