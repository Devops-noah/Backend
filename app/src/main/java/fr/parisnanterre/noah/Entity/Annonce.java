package fr.parisnanterre.noah.Entity;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import jakarta.persistence.Id;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.time.LocalDate;
import java.util.Date;
import java.util.List;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Annonce {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull
    private Date dateDepart;
    @NotNull
    private Date dateArrivee;

    @NotNull
    private LocalDate datePublication;

    @NotNull
    private Double poidsDisponible;

    @NotNull
    @ManyToOne
    @JsonIgnore // Prevent serialization of voyageur to avoid recursion
    @ToString.Exclude // Avoid recursion in toString()
    private Voyageur voyageur;

    //@NotNull
    @OneToMany(mappedBy = "annonce", cascade = CascadeType.ALL)
    @JsonManagedReference // Forward serialization for Demande -> Annonce
    private List<Demande> demandes;

    @NotNull
    @ManyToOne(fetch = FetchType.EAGER) // or FetchType.LAZY if you handle the fetch explicitly in the query
    @JoinColumn(name = "voyage_id", referencedColumnName = "id") // Ensure this matches your actual column name
    @JsonBackReference // Prevent recursion for Voyage -> Annonce
    @ToString.Exclude // Avoid recursion in toString()
    private Voyage voyage;

    @NotNull
    @ManyToOne
    @JoinColumn(name = "pays_destination_id")
    private Pays paysDestination;

    @NotNull
    @ManyToOne
    @JoinColumn(name = "pays_depart_id", nullable = false)
    private Pays paysDepart;

}
