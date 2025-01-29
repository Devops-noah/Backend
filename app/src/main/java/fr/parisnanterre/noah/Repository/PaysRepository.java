// Pays repository
package fr.parisnanterre.noah.Repository;

import fr.parisnanterre.noah.Entity.Pays;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface PaysRepository extends JpaRepository<Pays, Integer> {
    // Pays findByCodeISO(String codeISO); // Find country by ISO code
    Optional<Pays> findByNom(String nom);
}

