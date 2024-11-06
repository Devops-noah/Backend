// Pays repository
package fr.parisnanterre.noah.Repository;

import fr.parisnanterre.noah.Entity.Pays;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface PaysRepository extends JpaRepository<Pays, Integer> {
    // Pays findByCodeISO(String codeISO); // Find country by ISO code
    Optional<Pays> findByNom(String nom);
}

