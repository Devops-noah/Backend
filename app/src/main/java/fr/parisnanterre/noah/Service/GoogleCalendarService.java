package fr.parisnanterre.noah.Service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport;
import com.google.api.client.http.HttpRequestInitializer;
import com.google.api.client.util.DateTime;
import com.google.api.services.calendar.Calendar;
import com.google.api.services.calendar.model.Event;
import com.google.api.services.calendar.model.EventDateTime;
import fr.parisnanterre.noah.Entity.Utilisateur;
import fr.parisnanterre.noah.Repository.UtilisateurRepository;
import fr.parisnanterre.noah.GoogleApiProperties;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;
        import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import java.io.IOException;
import java.security.GeneralSecurityException;
import java.util.Optional;
import java.util.TimeZone;

@Service
public class GoogleCalendarService {

    @Autowired
    private UtilisateurRepository utilisateurRepository;

    @Autowired
    private GoogleApiProperties googleApiProperties;

    private final RestTemplate restTemplate = new RestTemplate();

    // ✅ 1️⃣ Get Access Token from Refresh Token
    public String getAccessToken(String refreshToken) throws IOException {
        String url = "https://oauth2.googleapis.com/token";
        String requestBody = "client_id=" + googleApiProperties.getClientId() +
                "&client_secret=" + googleApiProperties.getClientSecret() +
                "&refresh_token=" + refreshToken +
                "&grant_type=refresh_token";

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
        HttpEntity<String> requestEntity = new HttpEntity<>(requestBody, headers);

        ResponseEntity<String> response = restTemplate.exchange(url, HttpMethod.POST, requestEntity, String.class);
        ObjectMapper objectMapper = new ObjectMapper();
        JsonNode jsonNode = objectMapper.readTree(response.getBody());

        return jsonNode.has("access_token") ? jsonNode.get("access_token").asText() : null;
    }

    // ✅ 2️⃣ Create Google Calendar Event Using Access Token
    // ✅ 2️⃣ Create Google Calendar Event Using Access Token
    public void createEventInGoogleCalendar(String email, String voyageTitle, java.util.Date voyageDate) throws IOException, GeneralSecurityException {
        System.out.println("🔵 Checking Google Calendar Authorization for: " + email);

        // ✅ Ensure the correct email is used for lookup
        Optional<Utilisateur> utilisateurOpt = utilisateurRepository.findByEmail(email);

        if (utilisateurOpt.isEmpty()) {
            System.out.println("❌ No user found in database for email: " + email);
            return;
        }

        Utilisateur utilisateur = utilisateurOpt.get();
        System.out.println("🔵 User found: " + utilisateur.getEmail());

        if (utilisateur.getGoogleRefreshToken() == null) {
            System.out.println("❌ User has not authorized Google Calendar.");
            return;
        }

        // ✅ Get the refresh token
        String refreshToken = utilisateur.getGoogleRefreshToken();

        // ✅ Get a new access token
        String accessToken = getAccessToken(refreshToken);

        if (accessToken == null) {
            System.out.println("❌ Failed to obtain access token for user: " + email);
            return;
        }

        System.out.println("🔹 Using Access Token: " + accessToken);

        Event event = new Event()
                .setSummary(voyageTitle)
                .setStart(new EventDateTime().setDateTime(new DateTime(voyageDate, TimeZone.getTimeZone("UTC"))).setTimeZone("UTC"))
                .setEnd(new EventDateTime().setDateTime(new DateTime(voyageDate, TimeZone.getTimeZone("UTC"))).setTimeZone("UTC"));

        // ✅ Create Calendar Service with Access Token
        HttpRequestInitializer requestInitializer = request -> request.getHeaders().setAuthorization("Bearer " + accessToken);

        Calendar calendarClient = new Calendar.Builder(
                GoogleNetHttpTransport.newTrustedTransport(),
                com.google.api.client.json.jackson2.JacksonFactory.getDefaultInstance(),
                requestInitializer)
                .setApplicationName(googleApiProperties.getApplicationName())
                .build();

        try {
            // ✅ Insert Event into the User's Primary Calendar
            calendarClient.events().insert("primary", event).execute();
            System.out.println("✅ Event successfully added to Google Calendar for " + email);
        } catch (Exception e) {
            System.out.println("❌ Failed to add event to Google Calendar: " + e.getMessage());
        }
    }


    // ✅ Exchange Authorization Code for Refresh Token and Store It
    public void exchangeCodeAndStoreTokens(String email, String code) throws IOException {
        String url = "https://oauth2.googleapis.com/token";
        RestTemplate restTemplate = new RestTemplate();

        // ✅ Fix: Properly format request body using MultiValueMap
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

        MultiValueMap<String, String> requestBody = new LinkedMultiValueMap<>();
        requestBody.add("code", code);
        requestBody.add("client_id", googleApiProperties.getClientId());
        requestBody.add("client_secret", googleApiProperties.getClientSecret());
        requestBody.add("redirect_uri", "http://localhost:8080/api/auth/callback"); // Ensure it matches Google Console
        requestBody.add("grant_type", "authorization_code");

        HttpEntity<MultiValueMap<String, String>> requestEntity = new HttpEntity<>(requestBody, headers);

        // ✅ Fix: Use exchange method to send properly formatted request
        ResponseEntity<String> response = restTemplate.exchange(url, HttpMethod.POST, requestEntity, String.class);

        System.out.println("🔍 Google OAuth Response: " + response.getBody());

        ObjectMapper objectMapper = new ObjectMapper();
        JsonNode jsonNode = objectMapper.readTree(response.getBody());

        String accessToken = jsonNode.has("access_token") ? jsonNode.get("access_token").asText() : null;
        String refreshToken = jsonNode.has("refresh_token") ? jsonNode.get("refresh_token").asText() : null;

        if (refreshToken == null) {
            System.out.println("❌ No refresh token received! Try using prompt=consent.");
            return;
        }

        // ✅ Store Tokens in the Database
        Optional<Utilisateur> utilisateurOpt = utilisateurRepository.findByEmail(email);
        if (utilisateurOpt.isPresent()) {
            Utilisateur utilisateur = utilisateurOpt.get();
            utilisateur.setGoogleAccessToken(accessToken);
            utilisateur.setGoogleRefreshToken(refreshToken);
            utilisateurRepository.save(utilisateur);
            System.out.println("✅ Stored Google Tokens for: " + email);
        } else {
            System.out.println("❌ User not found in the database!");
        }
    }


}
