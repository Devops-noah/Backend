package fr.parisnanterre.noah.Entity;

import com.fasterxml.jackson.annotation.*;
import jakarta.persistence.*;
import lombok.Data;
import lombok.ToString;

import java.util.Date;
import java.util.List;

@Entity
@Data
public class Voyage {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    private Date dateDepart;
    private Date dateArrivee;

    @OneToMany(mappedBy = "voyage", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @JsonIgnore // Forward serialization for Voyage -> Annonce
    @JsonInclude(JsonInclude.Include.NON_NULL) // Include only if not null
    @ToString.Exclude // Avoid recursion in toString()
    private List<Annonce> annonces;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "pays_depart_id", nullable = false)
    @JsonProperty("paysDepart") // Ensure serialization
    private Pays paysDepart;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "pays_destination_id", nullable = false)
    @JsonProperty("paysDestination") // Ensure serialization
    private Pays paysDestination;

    @ManyToOne
    @JoinColumn(name = "voyageur_id")
    @JsonIgnore
    @ToString.Exclude
    private Utilisateur voyageur;
}