package fr.parisnanterre.noah.Service;

import fr.parisnanterre.noah.Entity.Segment;
import fr.parisnanterre.noah.Entity.Voyage;
import fr.parisnanterre.noah.Repository.SegmentRepository;
import fr.parisnanterre.noah.Repository.VoyageRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class DemandeTransfertService {

    private final VoyageRepository voyageRepository;
    private final SegmentRepository segmentRepository;

    private static final Logger log = LoggerFactory.getLogger(DemandeTransfertService.class);

    @Autowired
    public DemandeTransfertService(VoyageRepository voyageRepository, SegmentRepository segmentRepository) {
        this.voyageRepository = voyageRepository;
        this.segmentRepository = segmentRepository;
    }

    // Méthode pour rechercher les segments possibles entre deux pays
    public List<List<Segment>> rechercherSegments(String paysDepart, String paysArrivee) {
        log.info("Démarrage de la recherche des segments entre {} et {}", paysDepart, paysArrivee);

        // Récupérer tous les voyages existants
        List<Voyage> voyages = voyageRepository.findAll();
        log.info("Nombre de voyages trouvés dans la base de données : {}", voyages.size());
        // Ajout du log pour afficher tous les voyages récupérés
        log.info("Liste des voyages récupérés : {}", voyages);

        // Convertir les voyages en segments
        List<Segment> segments = convertirVoyagesEnSegments(voyages);

        // Construire le graphe des segments
        Map<String, List<Segment>> graph = new HashMap<>();
        for (Segment segment : segments) {
            String departNormalise = segment.getPointDepart().toLowerCase();
            graph.computeIfAbsent(departNormalise, k -> new ArrayList<>()).add(segment);
        }

        log.info("Graphe des segments construit avec succès");

        // Trouver tous les chemins possibles entre départ et arrivée
        List<List<Segment>> allPaths = findAllPaths(graph, paysDepart.toLowerCase(), paysArrivee.toLowerCase());
        log.info("Nombre de chemins trouvés : {}", allPaths.size());

        return allPaths;
    }

    // Conversion des voyages en segments
    // Conversion des voyages en segments
    private List<Segment> convertirVoyagesEnSegments(List<Voyage> voyages) {
        List<Segment> segments = new ArrayList<>();

        for (Voyage voyage : voyages) {
            // Assurez-vous que le voyageur n'est pas null
            if (voyage.getVoyageur() == null) {
                log.warn("Voyageur est null pour le voyage id: {}", voyage.getId());
                continue; // Ignore ce voyage si le voyageur est null
            }

            Segment segment = new Segment();
            segment.setPointDepart(voyage.getPaysDepart().getNom());
            segment.setPointArrivee(voyage.getPaysDestination().getNom());

            // Vérifier que le voyageur existe et affecter son ID au segment
            if (voyage.getVoyageur().getId() != null) {
                segment.setVoyageur(voyage.getVoyageur());  // Affecter l'objet Voyageur
            } else {
                log.warn("Le voyageur du voyage {} n'a pas d'ID valide", voyage.getId());
                continue; // Ignorer ce voyage si l'ID du voyageur est invalide
            }
            // Ajouter une relation explicite entre le voyage et le segment
            segment.setVoyage(voyage);  // Associer le voyage au segment

            // Afficher les informations du segment avant de l'enregistrer
            log.info("Segment à enregistrer : {}", segment);

            // Sauvegarder le segment dans la base de données
            segmentRepository.save(segment);
            segments.add(segment);
            // Récupérer et afficher le segment après la sauvegarde
            Segment savedSegment = segmentRepository.findById(segment.getId()).orElse(null);
            log.info("Segment après sauvegarde : {}", savedSegment);
        }

        log.info("Conversion de {} voyages en segments", voyages.size());
        return segments;
    }




    // Algorithme DFS pour trouver tous les chemins possibles
    private List<List<Segment>> findAllPaths(Map<String, List<Segment>> graph, String start, String end) {
        List<List<Segment>> allPaths = new ArrayList<>();
        findPathsDFS(graph, start, end, new ArrayList<>(), allPaths, new HashSet<>());
        return allPaths;
    }

    // Fonction récursive DFS pour explorer tous les chemins
    private void findPathsDFS(Map<String, List<Segment>> graph, String current, String end, List<Segment> currentPath, List<List<Segment>> allPaths, Set<String> visited) {
        if (current.equals(end)) {
            allPaths.add(new ArrayList<>(currentPath));  // Ajouter le chemin trouvé
            log.debug("Chemin trouvé : {}", currentPath);
            return;
        }

        if (!graph.containsKey(current)) {
            return;
        }

        visited.add(current);

        for (Segment segment : graph.get(current)) {
            if (visited.contains(segment.getPointArrivee().toLowerCase())) {
                continue;
            }

            currentPath.add(segment);
            findPathsDFS(graph, segment.getPointArrivee().toLowerCase(), end, currentPath, allPaths, visited);
            currentPath.remove(currentPath.size() - 1);
        }

        visited.remove(current);
    }

    // Méthode pour enregistrer la chaîne choisie
    public void enregistrerChaine(List<Segment> segmentsChoisis) {
        log.info("Enregistrement de la chaîne choisie contenant {} segments", segmentsChoisis.size());
        segmentsChoisis.forEach(segment -> {
            // Sauvegarder chaque segment dans la base de données
            segmentRepository.save(segment);
        });
        log.info("Chaîne enregistrée avec succès");
    }
}
