// Annonce service
package fr.parisnanterre.noah.Service;

import fr.parisnanterre.noah.DTO.Filtre;
import fr.parisnanterre.noah.Entity.Annonce;
import fr.parisnanterre.noah.Entity.Pays;
import fr.parisnanterre.noah.Repository.AnnonceRepository;
import fr.parisnanterre.noah.Repository.PaysRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@ComponentScan
public class AnnonceServiceImpl {


    private AnnonceRepository annonceRepository;
    private PaysRepository paysRepository;

    @Autowired
    public AnnonceServiceImpl(AnnonceRepository annonceRepository, PaysRepository paysRepository) {
        this.annonceRepository = annonceRepository;
        this.paysRepository = paysRepository;
    }

    public List<Annonce> getAllAnnonces() {
        return annonceRepository.findAll();
    }

    public Optional<Annonce> getAnnonceById(Integer id) {
        return annonceRepository.findById(id);
    }


}

