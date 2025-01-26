package fr.parisnanterre.noah.Controller;

import fr.parisnanterre.noah.DTO.AuthenticationRequest;
import fr.parisnanterre.noah.DTO.AuthenticationResponse;
import fr.parisnanterre.noah.Entity.Role;
import fr.parisnanterre.noah.Entity.RoleType;
import fr.parisnanterre.noah.Entity.Utilisateur;
import fr.parisnanterre.noah.Repository.RoleRepository;
import fr.parisnanterre.noah.Repository.UtilisateurRepository;
import fr.parisnanterre.noah.Service.CustomUserDetails;
import fr.parisnanterre.noah.Service.CustomUserDetailsService;
import fr.parisnanterre.noah.util.JwtUtil;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.Map;
import java.util.Optional;
import java.util.Set;


@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {
    private final AuthenticationManager authenticationManager;
    private final JwtUtil jwtUtil;
    private final PasswordEncoder passwordEncoder;
    private final UtilisateurRepository utilisateurRepository;
    private final RoleRepository roleRepository;

    @PostMapping("/register")
    public ResponseEntity<?> register(@Valid @RequestBody Utilisateur utilisateur) {
        // Check if the email is already in use
        if (utilisateurRepository.findByEmail(utilisateur.getEmail()).isPresent()) {
            return ResponseEntity.badRequest().body(
                    Map.of("status", "error", "message", "Email already exists")
            );
        }

        // Assign the default "ROLE_USER" role if no role is provided
        Role role = roleRepository.findByName(utilisateur.getRole() != null ?
                        utilisateur.getRole().getName() : RoleType.ROLE_USER)
                .orElseThrow(() -> new RuntimeException("Role not found"));

        // Assign the role and encode the password
        utilisateur.setRole(role);
        utilisateur.setMotDePasse(passwordEncoder.encode(utilisateur.getMotDePasse()));

        // Save the user
        utilisateurRepository.save(utilisateur);

        // Return success response
        return ResponseEntity.ok(
                Map.of("status", "success", "message", "User registered successfully")
        );
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody AuthenticationRequest request) {
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.getEmail(), request.getMotDePasse())
        );

        // Extract user details
        CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();
        System.out.println("user details: " + userDetails);
        String userType = userDetails.getUserType();  // Get the user type (expediteur/voyageur)
        Long userId = userDetails.getId(); // Ajoutez ceci si vous avez un getter pour l'ID utilisateur
        String profileImageUrl = userDetails.getProfileImageUrl();
        String jwt = jwtUtil.generateToken(userDetails.getUsername(), userType, userId, profileImageUrl);
        System.out.println("jwt valeur: " + jwt);
        return ResponseEntity.ok(new AuthenticationResponse(jwt, userType, userId));
    }


    @GetMapping("/me")
    public ResponseEntity<?> getUserDetails(Authentication authentication) {
        // Check if the authentication object is null or not authenticated
        if (authentication == null || !authentication.isAuthenticated()) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(Map.of("status", "error", "message", "Unauthorized access"));
        }

        // Log basic authentication details
        String email = authentication.getName();
        System.out.println("Authenticated user: " + email);
        System.out.println("Authorities: " + authentication.getAuthorities());

        // Fetch the user from the database
        Optional<Utilisateur> utilisateurOpt = utilisateurRepository.findByEmail(email);

        if (utilisateurOpt.isEmpty()) {
            System.out.println("User not found for email: " + email);
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(Map.of("status", "error", "message", "User not found"));
        }

        Utilisateur utilisateur = utilisateurOpt.get();

        // Log the user details
        System.out.println("Retrieved user: " + utilisateur);

        // Construct a simplified user response with null checks
        Map<String, Object> response = Map.of(
                "id", utilisateur.getId(),
                "nom", utilisateur.getNom() != null ? utilisateur.getNom() : "N/A",
                "prenom", utilisateur.getPrenom() != null ? utilisateur.getPrenom() : "N/A",
                "email", utilisateur.getEmail() != null ? utilisateur.getEmail() : "N/A",
                "role", utilisateur.getRole() != null ? utilisateur.getRole().getName() : "N/A",
                "type", utilisateur.getClass().getSimpleName().toLowerCase(),
                "profileImageUrl", utilisateur.getProfileImageUrl() != null ? utilisateur.getProfileImageUrl() : "N/A"
        );

        // Log the final user response
        System.out.println("User response: " + response);

        return ResponseEntity.ok(response);
    }

}
