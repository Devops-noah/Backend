package fr.parisnanterre.noah.config;

import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import com.google.firebase.database.FirebaseDatabase;
import com.google.auth.oauth2.GoogleCredentials;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.io.IOException;
import java.io.InputStream;

@Configuration
public class FirebaseConfig {



    @Bean
    public FirebaseDatabase firebaseDatabase() throws IOException {
        // Vérifier si FirebaseApp n'est pas déjà initialisé
        if (FirebaseApp.getApps().isEmpty()) {
            // Charger le fichier de configuration depuis le classpath
            InputStream serviceAccount = getClass().getClassLoader().getResourceAsStream("firebase-service-account.json");

            // Vérifier si le fichier a été trouvé
            if (serviceAccount == null) {
                throw new IOException("Fichier firebase-service-account.json introuvable dans resources/");
            }

            // Initialisation des options Firebase
            FirebaseOptions options = new FirebaseOptions.Builder()
                    .setCredentials(GoogleCredentials.fromStream(serviceAccount))
                    .setDatabaseUrl("https://travelc-messaging-default-rtdb.europe-west1.firebasedatabase.app")
                    .build();

            // Initialiser FirebaseApp
            FirebaseApp.initializeApp(options);
        }

        return FirebaseDatabase.getInstance(); // Retourner
    }
}

