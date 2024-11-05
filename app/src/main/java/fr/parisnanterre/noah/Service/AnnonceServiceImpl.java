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

    public void deleteAnnonce(Integer id) {
        annonceRepository.deleteById(id);
    }

    public List<Annonce> getAnnoncesByPaysDepart(Pays paysDepart) {
        return annonceRepository.findByPaysDepart(paysDepart);
    }

    public List<Annonce> getAnnoncesByPaysDestination(Pays paysDest) {
        return annonceRepository.findByPaysDestination(paysDest);
    }


    public List<Annonce> getFilteredAnnonces(Filtre filtre) {
        return annonceRepository.findAll()
                .stream()
                .filter(annonce ->
                        Optional.ofNullable(filtre.getDateDepart())
                                .map(dateDepart -> Optional.ofNullable(annonce.getDateCreation())
                                        .map(dateCreation -> !dateCreation.before(dateDepart)) // Checks if dateCreation is on or after dateDepart
                                        .orElse(false))
                                .orElse(true))
                .filter(annonce ->
                        Optional.ofNullable(filtre.getPrixMax())
                                .map(prixMax -> annonce.getPrix() <= prixMax)
                                .orElse(true))
                .filter(annonce ->
                        Optional.ofNullable(filtre.getPoidsMin())
                                .map(poidsMin -> annonce.getPoids() >= poidsMin)
                                .orElse(true))
                .filter(annonce ->
                        Optional.ofNullable(filtre.getDestinationNom())
                                .map(destinationNom -> Optional.ofNullable(annonce.getPaysDestination())
                                        .map(paysDestination -> paysDestination.getNom().equalsIgnoreCase(destinationNom)) // Case-insensitive comparison with 'nom'
                                        .orElse(false))
                                .orElse(true))
                .collect(Collectors.toList());
    }

}

