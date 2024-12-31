package fr.parisnanterre.noah.Service;

import fr.parisnanterre.noah.DTO.InformationColisRequest;
import fr.parisnanterre.noah.DTO.InformationColisResponse;
import fr.parisnanterre.noah.Entity.Annonce;
import fr.parisnanterre.noah.Entity.InformationColis;
import fr.parisnanterre.noah.Entity.Utilisateur;
import fr.parisnanterre.noah.Repository.AnnonceRepository;
import fr.parisnanterre.noah.Repository.InformationColisRepository;
import fr.parisnanterre.noah.Repository.UtilisateurRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class InformationColisService {

    @Autowired
    private InformationColisRepository informationColisRepository;

    @Autowired
    private AnnonceRepository annonceRepository;

    @Autowired
    private UtilisateurRepository utilisateurRepository;

    public InformationColisResponse proposerColis(InformationColisRequest colisRequest) throws Exception {
        // Vérifiez l'existence de l'annonce
        Optional<Annonce> annonceOpt = annonceRepository.findById(Math.toIntExact(colisRequest.getAnnonceId()));
        if (annonceOpt.isEmpty()) {
            throw new Exception("Annonce non trouvée");
        }

        // Vérifiez l'existence de l'expéditeur
        Optional<Utilisateur> expediteurOpt = utilisateurRepository.findById(colisRequest.getExpediteurId());
        if (expediteurOpt.isEmpty()) {
            throw new Exception("Expéditeur non trouvé");
        }

        // Préparez l'entité InformationColis
        InformationColis informationColis = new InformationColis();
        informationColis.setPoids(colisRequest.getPoids());
        informationColis.setDimensions(
                colisRequest.getLongueur() + "x" + colisRequest.getLargeur() + "x" + colisRequest.getHauteur()
        ); // Concaténer les dimensions
        informationColis.setNature(colisRequest.getNature());
        informationColis.setCategorie(colisRequest.getCategorie());
        informationColis.setDatePriseEnCharge(colisRequest.getDatePriseEnCharge());
        informationColis.setPlageHoraire(colisRequest.getPlageHoraire());
        informationColis.setAnnonce(annonceOpt.get());
        informationColis.setExpediteur(expediteurOpt.get());

        // Enregistrez dans la base de données
        InformationColis savedColis = informationColisRepository.save(informationColis);

        // Convertissez l'entité enregistrée en DTO de réponse
        InformationColisResponse response = new InformationColisResponse();
        response.setId(savedColis.getId());
        response.setPoids(savedColis.getPoids());
        response.setDimensions(savedColis.getDimensions());
        response.setNature(savedColis.getNature());
        response.setCategorie(savedColis.getCategorie());
        response.setDatePriseEnCharge(savedColis.getDatePriseEnCharge());
        response.setPlageHoraire(savedColis.getPlageHoraire());
        response.setMessage("Colis proposé avec succès !");
        return response;
    }
}
