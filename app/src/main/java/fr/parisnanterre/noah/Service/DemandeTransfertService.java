package fr.parisnanterre.noah.Service;

import fr.parisnanterre.noah.Entity.Segment;
import fr.parisnanterre.noah.Entity.Voyage;
import fr.parisnanterre.noah.Entity.Annonce;
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
    private List<Segment> convertirVoyagesEnSegments(List<Voyage> voyages) {
        List<Segment> segments = new ArrayList<>();

        for (Voyage voyage : voyages) {
            Segment segment = new Segment();
            segment.setPointDepart(voyage.getPaysDepart().getNom());
            segment.setPointArrivee(voyage.getPaysDestination().getNom());
            segment.setVoyageur(voyage.getVoyageur());

            // Associer uniquement l'ID du voyage (convertir en Long)
            segment.setVoyageId((long) voyage.getId()); // Conversion explicite de int à Long

            // Associer uniquement l'ID de l'annonce
            if (voyage.getAnnonces() != null && !voyage.getAnnonces().isEmpty()) {
                Annonce annonce = voyage.getAnnonces().get(0); // Récupérer la première annonce
                segment.setAnnonceId(annonce.getId()); // Associer l'ID de l'annonce
            }

            log.info("Segment à enregistrer : {}", segment);

            // Sauvegarder le segment dans la base de données
            Segment savedSegment = segmentRepository.save(segment);
            log.info("Segment sauvegardé : {}", savedSegment);

            segments.add(savedSegment); // Ajouter le segment sauvegardé à la liste
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
            allPaths.add(new ArrayList<>(currentPath));
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
            segmentRepository.save(segment);
        });
        log.info("Chaîne enregistrée avec succès");
    }
}
