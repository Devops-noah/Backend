package fr.parisnanterre.noah.Entity;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.*;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Entity
@Data
@ToString(exclude = {"notations", "livraisonsExpediteur", "livraisonsVoyageur", "demandes", "annonces", "voyages"})  // ✅ Exclude the list to prevent recursion
@NoArgsConstructor
@AllArgsConstructor
public class Utilisateur {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "Nom cannot be blank")
    @Size(max = 50, message = "Nom cannot exceed 50 characters")
    private String nom;

    @NotBlank(message = "Prenom cannot be blank")
    @Size(max = 50, message = "Prenom cannot exceed 50 characters")
    private String prenom;

    @NotBlank(message = "Email cannot be blank")
    @Email(message = "Email should be valid")
    @Column(unique = true, nullable = false)
    private String email;

    @NotBlank(message = "Mot de passe cannot be blank")
    private String motDePasse;

    private String telephone;
    private String adresse;
    private String profileImage;
    private boolean enabled = true; // Default to true (enabled)

    private int notificationCount = 0; // Default to 0

    @ManyToOne
    @JoinColumn(name = "role_id", nullable = false) // Foreign key for Role
    private Role role;

    // Instead of using a String set, use an Enum for better type safety

    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(name = "user_types", joinColumns = @JoinColumn(name = "user_id"))
    @Enumerated(EnumType.STRING) // Store as String instead of Integer
    @Column(name = "dtype")
    private Set<UserType> userTypes = new HashSet<>();


    @OneToMany(mappedBy = "expediteur", cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    private List<Livraison> livraisonsExpediteur;

    @OneToMany(mappedBy = "voyageur", cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    private List<Livraison> livraisonsVoyageur;

    @OneToMany(mappedBy = "utilisateur", cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    private List<Notation> notations;

    @OneToMany(mappedBy = "expediteur", cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    private List<Demande> demandes;

    @OneToMany(mappedBy = "voyageur", cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    private List<Annonce> annonces;

    @OneToMany(mappedBy = "voyageur", cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    private List<Voyage> voyages;

    // === Methods to manage user type dynamically ===

    public void becomeVoyageur() {
        this.userTypes.add(UserType.VOYAGEUR);
    }

    public void becomeExpediteur() {
        this.userTypes.add(UserType.EXPEDITEUR);
    }

    public boolean isVoyageur() {
        return userTypes.contains(UserType.VOYAGEUR);
    }

    public boolean isExpediteur() {
        return userTypes.contains(UserType.EXPEDITEUR);
    }

    public boolean isAdmin() {
        return this.role.getName() == RoleType.ROLE_ADMIN;
    }

}
