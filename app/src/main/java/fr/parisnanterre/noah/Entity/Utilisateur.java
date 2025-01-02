// Utilisateur entity
package fr.parisnanterre.noah.Entity;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import jakarta.persistence.*;
import jakarta.persistence.Id;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.*;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
@DiscriminatorColumn(name = "type", discriminatorType = DiscriminatorType.STRING)

@JsonTypeInfo(
        use = JsonTypeInfo.Id.NAME,    // Use a property to indicate the type
        include = JsonTypeInfo.As.PROPERTY,
        property = "type"             // This field will determine the subtype
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
    //@Size(min = 6, max = 100, message = "Mot de passe must be between 6 and 100 characters")
    private String motDePasse;

    private String telephone;
    private String adresse;

  
    private boolean enabled = true; // Default to true (enabled)

    @ManyToOne
    @JoinColumn(name = "role_id", nullable = false) // Foreign key for Role
    private Role role; // Single Role for the user

    @OneToMany(mappedBy = "expediteur", cascade = CascadeType.ALL)
    private List<Livraison> livraisonsExpediteur;

    @OneToMany(mappedBy = "voyageur", cascade = CascadeType.ALL)
    private List<Livraison> livraisonsVoyageur;

//    @OneToMany(mappedBy = "receveur", cascade = CascadeType.ALL)
//    private List<Livraison> livraisonsReceveur;

    @OneToMany(mappedBy = "utilisateur", cascade = CascadeType.ALL)
    private List<Notation> notations;

    @OneToMany(mappedBy = "expediteur", cascade = CascadeType.ALL)
    private List<Demande> demandes;


    @OneToMany(mappedBy = "voyageur", cascade = CascadeType.ALL)
    private List<Annonce> annonces;

    @OneToMany(mappedBy = "voyageur", cascade = CascadeType.ALL)
    private List<Voyage> voyages;


}

