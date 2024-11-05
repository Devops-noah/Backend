// Utilisateur entity
package fr.parisnanterre.noah.Entity;

import jakarta.persistence.*;
import jakarta.persistence.Id;
import lombok.*;

import java.util.List;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Utilisateur {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String nom;
    private String prenom;
    private String email;
    private String motDePasse;
    private String telephone;
    private String adresse;

    @Enumerated(EnumType.STRING)
    //private Role role;

    // Relationships
    @OneToMany(mappedBy = "expediteur")
    private List<Annonce> annoncesExpediteur;

    @OneToMany(mappedBy = "voyageur")
    private List<Annonce> annoncesVoyageur;

    @OneToMany(mappedBy = "voyageur")
    private List<Voyage> voyages;

    // Business logic
    public boolean authentifier(String password) {
        return motDePasse.equals(password);
    }

    public void updateProfile(String nom, String prenom, String telephone, String adresse) {
        this.nom = nom;
        this.prenom = prenom;
        this.telephone = telephone;
        this.adresse = adresse;
    }
}

""