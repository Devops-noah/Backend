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
        String userType = userDetails.getUserType();  // Get the user type (expediteur/voyageur)

        String jwt = jwtUtil.generateToken(authentication.getName(), userType);  // Pass userType to token generation
        return ResponseEntity.ok(new AuthenticationResponse(jwt, userType));  // Include userType in the response
    }


    @GetMapping("/me")
    public ResponseEntity<?> getUserDetails(Authentication authentication) {
        // Obtenir l'utilisateur connecté à partir du contexte d'authentification
        String email = authentication.getName();

        // Rechercher l'utilisateur dans la base de données
        Optional<Utilisateur> utilisateurOpt = utilisateurRepository.findByEmail(email);
        if (utilisateurOpt.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(
                    Map.of("status", "error", "message", "User not found")
            );
        }

        Utilisateur utilisateur = utilisateurOpt.get();
        System.out.println("utilisateur list: " + utilisateur);
        // Créer une réponse utilisateur simplifiée
        Map<String, Object> response = Map.of(
                "id", utilisateur.getId(),
                "nom", utilisateur.getNom(),
                "prenom", utilisateur.getPrenom(),
                "email", utilisateur.getEmail(),
                //"voyageur", utilisateur.getVoyageur,
                "role", utilisateur.getRole().getName(),
                "type", utilisateur.getClass().getSimpleName().toLowerCase()
        );

        return ResponseEntity.ok(response);
    }



}
