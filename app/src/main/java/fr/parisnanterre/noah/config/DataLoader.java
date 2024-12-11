package fr.parisnanterre.noah.config;


import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.jdbc.datasource.init.ScriptUtils;
import org.springframework.stereotype.Component;

import javax.sql.DataSource;
import java.sql.Connection;

@Component
public class DataLoader {
    private final DataSource dataSource;

    // Inject seed file paths
    @Value("classpath:seeds/annonce.sql")
    private Resource annonceSeedFile;

    @Value("classpath:seeds/demande.sql")
    private Resource demandeSeedFile;

    @Value("classpath:seeds/feedback.sql")
    private Resource feedbackSeedFile;

    @Value("classpath:seeds/information_colis.sql")
    private Resource informationColisSeedFile;

    @Value("classpath:seeds/livraison.sql")
    private Resource livraisonSeedFile;

    @Value("classpath:seeds/notation.sql")
    private Resource notationSeedFile;

    @Value("classpath:seeds/notification.sql")
    private Resource notificationSeedFile;

    @Value("classpath:seeds/pays.sql")
    private Resource paysSeedFile;

    @Value("classpath:seeds/role.sql")
    private Resource roleSeedFile;

    @Value("classpath:seeds/utilisateur.sql")
    private Resource utilisateurSeedFile;

    @Value("classpath:seeds/voyage.sql")
    private Resource voyageSeedFile;

    public DataLoader(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    @PostConstruct
    public void loadSeedData() {
        try (Connection connection = dataSource.getConnection()) {
            // Execute the seed script
            ScriptUtils.executeSqlScript(connection, annonceSeedFile);
            ScriptUtils.executeSqlScript(connection, demandeSeedFile);
            ScriptUtils.executeSqlScript(connection, feedbackSeedFile);
            ScriptUtils.executeSqlScript(connection, informationColisSeedFile);
            ScriptUtils.executeSqlScript(connection, livraisonSeedFile);
            ScriptUtils.executeSqlScript(connection, notationSeedFile);
            ScriptUtils.executeSqlScript(connection, notificationSeedFile);
            ScriptUtils.executeSqlScript(connection, paysSeedFile);
            ScriptUtils.executeSqlScript(connection, roleSeedFile);
            ScriptUtils.executeSqlScript(connection, utilisateurSeedFile);
            ScriptUtils.executeSqlScript(connection, voyageSeedFile);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
