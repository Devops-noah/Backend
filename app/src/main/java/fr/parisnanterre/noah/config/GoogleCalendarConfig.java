package fr.parisnanterre.noah.config;

import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.jackson2.JacksonFactory;
import fr.parisnanterre.noah.GoogleApiProperties;
import com.google.api.services.calendar.Calendar;
import com.google.api.client.auth.oauth2.Credential;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import java.io.IOException;
import java.security.GeneralSecurityException;

@Configuration
public class GoogleCalendarConfig {

    private static final JsonFactory JSON_FACTORY = JacksonFactory.getDefaultInstance();

    private final GoogleApiProperties googleApiProperties;

    @Autowired
    public GoogleCalendarConfig(GoogleApiProperties googleApiProperties) {
        this.googleApiProperties = googleApiProperties;
    }

    @PostConstruct
    public void init() {
        System.out.println("🔍 Debugging Google API Properties:");
        System.out.println("Client ID: " + googleApiProperties.getClientId());
        System.out.println("Client Secret: " + googleApiProperties.getClientSecret());
        System.out.println("Application Name: " + googleApiProperties.getApplicationName());

        // ❌ Throw error if properties are missing
        if (googleApiProperties.getClientId() == null ||
                googleApiProperties.getClientSecret() == null ||
                googleApiProperties.getApplicationName() == null) {
            throw new IllegalArgumentException("❌ One or more Google API properties are missing");
        }
    }

    @Bean
    public Calendar getCalendarService() throws IOException, GeneralSecurityException {
        try {
            if (googleApiProperties.getClientId() == null || googleApiProperties.getClientSecret() == null ||
                    googleApiProperties.getApplicationName() == null) {
                throw new IllegalArgumentException("One or more Google API properties are missing");
            }

            // No static refresh token, as users will authenticate dynamically
            return new Calendar.Builder(
                    GoogleNetHttpTransport.newTrustedTransport(),
                    JSON_FACTORY,
                    null) // The user-specific credential will be added when making API requests
                    .setApplicationName(googleApiProperties.getApplicationName())
                    .build();
        } catch (IOException | GeneralSecurityException e) {
            System.out.println("Error creating Calendar service: " + e.getMessage());
            throw e; // Re-throw to preserve original exception flow
        }
    }

}



