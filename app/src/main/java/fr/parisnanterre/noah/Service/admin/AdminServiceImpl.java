package fr.parisnanterre.noah.Service.admin;

import fr.parisnanterre.noah.DTO.AnnonceResponse;
import fr.parisnanterre.noah.Entity.*;
import fr.parisnanterre.noah.Repository.AnnonceRepository;
import fr.parisnanterre.noah.Repository.RoleRepository;
import fr.parisnanterre.noah.Repository.UtilisateurRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class AdminServiceImpl {

    private final UtilisateurRepository utilisateurRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;
    private final AnnonceRepository annonceRepository;

    @Autowired
    public AdminServiceImpl(UtilisateurRepository utilisateurRepository,
                            RoleRepository roleRepository,
                            PasswordEncoder passwordEncoder,
                            AnnonceRepository annonceRepository) {
        this.utilisateurRepository = utilisateurRepository;
        this.roleRepository = roleRepository;
        this.passwordEncoder = passwordEncoder;
        this.annonceRepository = annonceRepository;
    }

    // Method to create an admin user
    /**
     * Creates a new Admin user from individual fields.
     *
     * @param nom       The nom of the admin
     * @param prenom    The prenom of the admin
     * @param email     The email of the admin
     * @param motDePasse The password of the admin
     * @param telephone The telephone of the admin
     * @param adresse   The adresse of the admin
     * @return The created Admin user
     */

    public AdminType createAdminUser(String nom, String prenom, String email, String motDePasse, String telephone, String adresse) {
        // Create new Admin instance
        AdminType adminType = new AdminType();
        adminType.setNom(nom);
        adminType.setPrenom(prenom);
        adminType.setEmail(email);
        adminType.setMotDePasse(passwordEncoder.encode(motDePasse));
        adminType.setTelephone(telephone);
        adminType.setAdresse(adresse);

        // Retrieve the ROLE_ADMIN role from the Role repository
        Role adminRole = roleRepository.findByName(RoleType.ROLE_ADMIN)
                .orElseThrow(() -> new IllegalArgumentException("Role ADMIN not found"));

        System.out.println("admin role: " + adminRole);
        // Set the role to Admin
        adminType.setRole(adminRole);

        // Save the Admin user
        return utilisateurRepository.save(adminType);
    }

    /**
     * Get all users in the system.
     *
     * @return A list of all users
     */
    public Page<Utilisateur> getAllUsers(Pageable pageable) {
        return utilisateurRepository.findAll(pageable);
    }

    /**
     * Suspend or reactivate a user account.
     *
     * @param userId  ID of the user to suspend or reactivate
     * @param suspend true to suspend, false to reactivate
     * @return The updated Utilisateur entity
     * @throws IllegalArgumentException if the user is not found
     */
    public Utilisateur suspendOrReactivateUser(Long userId, boolean suspend) {
        // Retrieve the user by ID
        Optional<Utilisateur> optionalUser = utilisateurRepository.findById(userId);
        if (optionalUser.isEmpty()) {
            throw new IllegalArgumentException("User not found with ID: " + userId);
        }

        // Update the user's enabled status
        Utilisateur user = optionalUser.get();
        user.setEnabled(!suspend); // If suspend is true, disable the account; otherwise, enable it

        // Save the updated user
        return utilisateurRepository.save(user);
    }

    // Activate a user account
    public Utilisateur activateUtilisateur(Long userId) {
        Utilisateur utilisateur = utilisateurRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("User not found with ID: " + userId));
        utilisateur.setEnabled(true); // Set user as enabled
        return utilisateurRepository.save(utilisateur);
    }

    // --- Annonce Management ---

    // Get all annonces
    public List<AnnonceResponse> getAllAnnonces() {
        return annonceRepository.findAll().stream()
                .map(this::mapToAnnonceResponse)
                .collect(Collectors.toList());
    }

    private AnnonceResponse mapToAnnonceResponse(Annonce annonce) {
        AnnonceResponse response = new AnnonceResponse();
        response.setId(annonce.getId());
        response.setDatePublication(annonce.getDatePublication());
        response.setPoidsDisponible(annonce.getPoidsDisponible());
        response.setApproved(annonce.isApproved());
        response.setSuspended(annonce.isSuspended());

        // Retrieve Voyage information
        if (annonce.getVoyage() != null) {
            Voyage voyage = annonce.getVoyage();

            // Use dateDepart and dateArrivee from the Voyage
            response.setDateDepart(voyage.getDateDepart());
            response.setDateArrivee(voyage.getDateArrivee());

            // Retrieve paysDepart and paysDestination from the Voyage
            response.setPaysDepart(voyage.getPaysDepart() != null ? voyage.getPaysDepart().getNom() : null);
            response.setPaysDestination(voyage.getPaysDestination() != null ? voyage.getPaysDestination().getNom() : null);

            // Set Voyage ID
            response.setVoyageId(voyage.getId());
        } else {
            // Handle case where Voyage is null
            response.setDateDepart(null);
            response.setDateArrivee(null);
            response.setPaysDepart(null);
            response.setPaysDestination(null);
            response.setVoyageId(null);
        }
        // Retrieve Voyageur information using UtilisateurRepository
        if (annonce.getVoyageur() != null) {
            // Assuming the Voyageur entity has getId() and getName() methods
            response.setVoyageurId(Math.toIntExact(annonce.getVoyageur().getId()));
            response.setVoyageurNom(annonce.getVoyageur().getNom());
            response.setVoyageurEmail(annonce.getVoyageur().getEmail());
        }
        return response;
    }

    // Approve an annonce
    public Annonce approveAnnonce(Integer annonceId) {
        Annonce annonce = annonceRepository.findById(annonceId)
                .orElseThrow(() -> new IllegalArgumentException("Annonce not found with ID: " + annonceId));
        annonce.setApproved(!annonce.isApproved());
        return annonceRepository.save(annonce);
    }

    // Delete an annonce
    public void deleteAnnonce(Integer annonceId) {
        if (!annonceRepository.existsById(annonceId)) {
            throw new IllegalArgumentException("Annonce not found with ID: " + annonceId);
        }
        annonceRepository.deleteById(annonceId);
    }

    // Suspend or resume an annonce (toggle suspended state based on 'suspend' parameter)
    public Annonce suspendAnnonce(Integer annonceId) {
        Annonce annonce = annonceRepository.findById(annonceId)
                .orElseThrow(() -> new IllegalArgumentException("Annonce not found with ID: " + annonceId));
        annonce.setSuspended(!annonce.isSuspended()); // Set suspended state to true or false based on the parameter
        return annonceRepository.save(annonce);
    }



}
