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

    public void createAnnonce(Annonce annonce) {
        // Handle paysDepart by checking if it already exists in the database
        if (annonce.getPaysDepart() != null) {
            Optional<Pays> existingPaysDepart = paysRepository.findByNom(annonce.getPaysDepart().getNom());
            if (existingPaysDepart.isPresent()) {
                annonce.setPaysDepart(existingPaysDepart.get()); // Set existing paysDepart
            } else {
                paysRepository.save(annonce.getPaysDepart()); // Save new paysDepart
            }
        }

        // Handle paysDestination by checking if it already exists in the database
        if (annonce.getPaysDestination() != null) {
            Optional<Pays> existingPaysDestination = paysRepository.findByNom(annonce.getPaysDestination().getNom());
            if (existingPaysDestination.isPresent()) {
                annonce.setPaysDestination(existingPaysDestination.get()); // Set existing paysDestination
            } else {
                paysRepository.save(annonce.getPaysDestination()); // Save new paysDestination
            }
        }

        // Save the Annonce with updated Pays references
        annonceRepository.save(annonce);
    }


    public Annonce updateAnnonce(Integer id, Annonce annonceDetails) {
        return annonceRepository.findById(id)
                .map(annonce -> {
                    annonce.setPoids(annonceDetails.getPoids());
                    annonce.setPrix(annonceDetails.getPrix());
                    annonce.setDateCreation(annonceDetails.getDateCreation());
                    annonce.setExpediteur(annonceDetails.getExpediteur());
                    annonce.setVoyageur(annonceDetails.getVoyageur());
                    annonce.setPaysDepart(annonceDetails.getPaysDepart());
                    annonce.setPaysDestination(annonceDetails.getPaysDestination());
                    return annonceRepository.save(annonce);
                })
                .orElseThrow(() -> new RuntimeException("Annonce not found"));
    }

}

