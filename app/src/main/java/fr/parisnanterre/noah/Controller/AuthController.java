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

import java.util.HashSet;
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
        // Check if the email already exists
        if (utilisateurRepository.findByEmail(utilisateur.getEmail()).isPresent()) {
            return ResponseEntity.badRequest().body(
                    Map.of("status", "error", "message", "Email already exists")
            );
        }

        // Determine the role (default to ROLE_USER if not specified)
        RoleType requestedRole = (utilisateur.getRole() != null) ? utilisateur.getRole().getName() : RoleType.ROLE_USER;
        Role role = roleRepository.findByName(requestedRole)
                .orElseThrow(() -> new RuntimeException("Role not found"));

        // Assign the role
        utilisateur.setRole(role);

        // Encode password before saving
        utilisateur.setMotDePasse(passwordEncoder.encode(utilisateur.getMotDePasse()));

        // Ensure that ROLE_USER starts with no type (Voyageur/Expediteur)
        if (role.getName() == RoleType.ROLE_USER) {
            utilisateur.setUserTypes(new HashSet<>()); // Empty set (no type at registration)
        }

        // ROLE_ADMIN is fixed and does not have Voyageur/Expediteur types
        utilisateurRepository.save(utilisateur);

        // Return success response
        return ResponseEntity.ok(
                Map.of("status", "success", "message", "User registered successfully")
        );
    }


    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody AuthenticationRequest request) {
        try {
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(request.getEmail(), request.getMotDePasse())
            );

            // Extract user details
            CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();
            Long userId = userDetails.getId();
            RoleType roleType = userDetails.getRole(); // ROLE_USER or ROLE_ADMIN
            String role = roleType != null ? roleType.name() : "ROLE_USER"; // Convert Enum to String (ROLE_USER / ROLE_ADMIN)

            // Instead of returning the actual userType, return null or "utilisateur"
            String userType = null; // Ensuring userType is NOT returned at login

            // Generate JWT token (passing userType as null)
            String jwt = jwtUtil.generateToken(userDetails.getUsername(), role, userType, userId);

            // Return success response
            return ResponseEntity.ok(new AuthenticationResponse(jwt, role, userType, userId));

        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(
                    Map.of("status", "error", "message", "Invalid email or password")
            );
        }
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
                "profileImage", utilisateur.getProfileImage() != null ? utilisateur.getProfileImage() : "N/A"
        );

        // Log the final user response
        System.out.println("User response: " + response);

        return ResponseEntity.ok(response);
    }

}
