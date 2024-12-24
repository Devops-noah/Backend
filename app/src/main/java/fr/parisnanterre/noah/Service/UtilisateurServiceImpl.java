package fr.parisnanterre.noah.Service;

import fr.parisnanterre.noah.DTO.AnnonceResponse;
import fr.parisnanterre.noah.DTO.UtilisateurProfileResponse;
import fr.parisnanterre.noah.Entity.Expediteur;
import fr.parisnanterre.noah.Entity.Utilisateur;
import fr.parisnanterre.noah.Entity.Voyage;
import fr.parisnanterre.noah.Entity.Voyageur;
import fr.parisnanterre.noah.Repository.UtilisateurRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class UtilisateurServiceImpl {

    @Autowired
    private UtilisateurRepository utilisateurRepository;

    public List<Utilisateur> getAllUtilisateurs() {
        return utilisateurRepository.findAll();
    }

    public UtilisateurProfileResponse getUserProfile(String email) {
        // Fetch the user by email
        Utilisateur utilisateur = utilisateurRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("Utilisateur not found"));

        // Create a response object
        UtilisateurProfileResponse profile = new UtilisateurProfileResponse();

        // Common fields for all users
        profile.setId(utilisateur.getId());
        profile.setNom(utilisateur.getNom());
        profile.setPrenom(utilisateur.getPrenom());
        profile.setEmail(utilisateur.getEmail());
        profile.setTelephone(utilisateur.getTelephone());
        profile.setAdresse(utilisateur.getAdresse());

        // Set the type based on the actual class of the user
        if (utilisateur instanceof Voyageur voyageur) {
            profile.setType("voyageur");  // Set type as "voyageur"

            // Map Voyageur-specific data
            List<AnnonceResponse> annonces = voyageur.getAnnonces().stream().map(annonce -> {
                AnnonceResponse annonceResponse = new AnnonceResponse();
                annonceResponse.setId(annonce.getId());
                annonceResponse.setDatePublication(annonce.getDatePublication());
                annonceResponse.setPoidsDisponible(annonce.getPoidsDisponible());

                if (annonce.getVoyage() != null) {
                    annonceResponse.setDateDepart(annonce.getVoyage().getDateDepart());
                    annonceResponse.setDateArrivee(annonce.getVoyage().getDateArrivee());
                    annonceResponse.setPaysDepart(annonce.getVoyage().getPaysDepart() != null ? annonce.getVoyage().getPaysDepart().getNom() : null);
                    annonceResponse.setPaysDestination(annonce.getVoyage().getPaysDestination() != null ? annonce.getVoyage().getPaysDestination().getNom() : null);
                    annonceResponse.setVoyageId(annonce.getVoyage().getId());
                }
                return annonceResponse;
            }).toList();
            profile.setAnnonces(annonces);

            List<Voyage> voyages = voyageur.getVoyages().stream().map(voyage -> {
                Voyage voyageResponse = new Voyage();
                voyageResponse.setId(voyage.getId());
                voyageResponse.setDateDepart(voyage.getDateDepart());
                voyageResponse.setDateArrivee(voyage.getDateArrivee());
                voyageResponse.setPaysDepart(voyage.getPaysDepart() != null ? voyage.getPaysDepart() : null);
                voyageResponse.setPaysDestination(voyage.getPaysDestination() != null ? voyage.getPaysDestination() : null);
                return voyageResponse;
            }).toList();
            profile.setVoyages(voyages);

        } else if (utilisateur instanceof Expediteur) {
            profile.setType("expediteur");  // Set type as "expediteur"

            // For Expediteur, display a message
            profile.setMessage("L'utilisateur est un expediteur. Les colis ne sont pas disponibles pour le moment.");
        }

        return profile;
    }


//    public Optional<Utilisateur> getUtilisateurById(Integer id) {
//        return utilisateurRepository.findById(id);
//    }

//    public Utilisateur createUtilisateur(Utilisateur utilisateur) {
//        return utilisateurRepository.save(utilisateur);
//    }
//
//    public Utilisateur updateUtilisateur(Integer id, Utilisateur utilisateurDetails) {
//        return utilisateurRepository.findById(id)
//                .map(utilisateur -> {
//                    utilisateur.setNom(utilisateurDetails.getNom());
//                    utilisateur.setPrenom(utilisateurDetails.getPrenom());
//                    utilisateur.setEmail(utilisateurDetails.getEmail());
//                    utilisateur.setMotDePasse(utilisateurDetails.getMotDePasse());
//                    utilisateur.setTelephone(utilisateurDetails.getTelephone());
//                    utilisateur.setAdresse(utilisateurDetails.getAdresse());
//                    //utilisateur.setRole(utilisateurDetails.getRole());
//                    return utilisateurRepository.save(utilisateur);
//
//                }).orElseThrow(() -> new RuntimeException("Utilisateur not found"));
//    }
//
//    public void deleteUtilisateur(Integer id) {
//        utilisateurRepository.deleteById(id);
//    }
//
//    public Optional<Utilisateur> getUtilisateurByEmail(String email) {
//        return utilisateurRepository.findByEmail(email);
//    }
}
