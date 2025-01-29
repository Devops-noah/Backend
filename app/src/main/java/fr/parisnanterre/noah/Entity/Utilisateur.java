package fr.parisnanterre.noah.Entity;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.*;

import java.util.List;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
@DiscriminatorColumn(name = "type", discriminatorType = DiscriminatorType.STRING)

@JsonTypeInfo(
        use = JsonTypeInfo.Id.NAME,
        include = JsonTypeInfo.As.PROPERTY,
        property = "type"
)
@JsonSubTypes({
        @JsonSubTypes.Type(value = Voyageur.class, name = "voyageur"),
        @JsonSubTypes.Type(value = Expediteur.class, name = "expediteur"),
        @JsonSubTypes.Type(value = AdminType.class, name = "admin")
})
public abstract class Utilisateur {
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

    // Ajout du champ notificationCount
    //@JoinColumn(name = "notification_count", nullable = true)
    private int notificationCount = 0;  // Compteur des notifications non lues

    @ManyToOne
    @JoinColumn(name = "role_id", nullable = false) // Foreign key for Role
    private Role role;

    @OneToMany(mappedBy = "expediteur", cascade = CascadeType.ALL)
    private List<Livraison> livraisonsExpediteur;

    @OneToMany(mappedBy = "voyageur", cascade = CascadeType.ALL)
    private List<Livraison> livraisonsVoyageur;

    @OneToMany(mappedBy = "utilisateur", cascade = CascadeType.ALL)
    private List<Notation> notations;

    @OneToMany(mappedBy = "expediteur", cascade = CascadeType.ALL)
    private List<Demande> demandes;

    @OneToMany(mappedBy = "voyageur", cascade = CascadeType.ALL)
    private List<Annonce> annonces;

    @OneToMany(mappedBy = "voyageur", cascade = CascadeType.ALL)
    private List<Voyage> voyages;



}
