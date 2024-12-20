// Utilisateur repository

package fr.parisnanterre.noah.Repository;

import fr.parisnanterre.noah.Entity.Utilisateur;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UtilisateurRepository extends JpaRepository<Utilisateur, Long> {
    Optional<Utilisateur> findByEmail(String email);

    Boolean existsByEmail(String email);
}


