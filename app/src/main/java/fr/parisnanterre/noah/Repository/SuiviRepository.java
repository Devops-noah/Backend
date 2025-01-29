package fr.parisnanterre.noah.Repository;

import fr.parisnanterre.noah.Entity.Suivi;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface SuiviRepository extends JpaRepository<Suivi, Long> {

}
