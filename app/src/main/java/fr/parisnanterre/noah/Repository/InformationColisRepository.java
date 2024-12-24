package fr.parisnanterre.noah.Repository;

import fr.parisnanterre.noah.Entity.InformationColis;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface InformationColisRepository extends JpaRepository<InformationColis, Long> {
}
