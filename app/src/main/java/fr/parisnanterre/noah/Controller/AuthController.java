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
import fr.parisnanterre.noah.Service.GoogleCalendarService;
import fr.parisnanterre.noah.util.JwtUtil;
import jakarta.servlet.http.HttpServletResponse;
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

import java.io.IOException;
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
    private final GoogleCalendarService googleCalendarService;

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

            CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();
            String email = userDetails.getUsername();
            Long userId = userDetails.getId();
            RoleType roleType = userDetails.getRole();
            String role = roleType != null ? roleType.name() : "ROLE_USER";

            // Check if the user has already authorized Google Calendar
            Optional<Utilisateur> utilisateurOpt = utilisateurRepository.findByEmail(email);

            boolean isGmailUser = email.endsWith("@gmail.com");  // ✅ Check if user has a Gmail account
            boolean needsGoogleAuth = utilisateurOpt.map(user -> user.getGoogleRefreshToken() == null).orElse(true);

            // 🔴 Gmail users MUST authorize Google Calendar before logging in
            if (isGmailUser && needsGoogleAuth) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN).body(Map.of(
                        "status", "error",
                        "message", "Vous devez autoriser Google Calendar pour continuer.",
                        "needsGoogleAuth", true
                ));
            }

            // Generate JWT
            String jwt = jwtUtil.generateToken(email, role, null, userId);

            // Return response with Google Auth status
            return ResponseEntity.ok(Map.of(
                    "token", jwt,
                    "role", role,
                    "userId", userId
            ));

        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(Map.of(
                    "status", "error",
                    "message", "Invalid email or password"
            ));
        }
    }

    @GetMapping("/callback")
    public void handleOAuthCallback(@RequestParam("code") String code,
                                    @RequestParam(value = "state", required = false) String email,
                                    HttpServletResponse response) throws IOException {
        try {
            System.out.println("🔵 OAuth Callback Triggered");
            System.out.println("🔑 Received code: " + code);
            if (email != null) {
                System.out.println("📧 Email received: " + email);
            }

            // ✅ Exchange code and store refresh token
            googleCalendarService.exchangeCodeAndStoreTokens(email, code);

            // ✅ Fetch user details
            Optional<Utilisateur> utilisateurOpt = utilisateurRepository.findByEmail(email);
            if (utilisateurOpt.isEmpty()) {
                response.sendRedirect("http://localhost:3000/login?error=user_not_found");
                return;
            }

            Utilisateur utilisateur = utilisateurOpt.get();
            String role = utilisateur.getRole().getName().name();
            Long userId = utilisateur.getId();

            // ✅ Generate a new JWT token
            String jwt = jwtUtil.generateToken(email, role, null, userId);

            // ✅ Redirect to frontend with JWT token in URL
            String redirectUrl = "http://localhost:3000/login?token=" + jwt + "&userId=" + userId + "&role=" + role;
            response.sendRedirect(redirectUrl);

        } catch (IOException e) {
            System.out.println("❌ OAuth Token Exchange Failed: " + e.getMessage());
            response.sendRedirect("http://localhost:3000/login?error=token_exchange_failed");
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
