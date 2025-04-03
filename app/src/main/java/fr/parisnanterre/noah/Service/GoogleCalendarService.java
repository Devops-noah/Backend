package fr.parisnanterre.noah.Service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import fr.parisnanterre.noah.DTO.GoogleCalendarEventDto;
import fr.parisnanterre.noah.Entity.Utilisateur;
import fr.parisnanterre.noah.Repository.UtilisateurRepository;
import fr.parisnanterre.noah.GoogleApiProperties;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;
        import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

import java.util.Calendar;

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

        // ✅ Use MultiValueMap instead of raw string
        MultiValueMap<String, String> formData = new LinkedMultiValueMap<>();
        formData.add("client_id", googleApiProperties.getClientId());
        formData.add("client_secret", googleApiProperties.getClientSecret());
        formData.add("refresh_token", refreshToken);
        formData.add("grant_type", "refresh_token");

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

        HttpEntity<MultiValueMap<String, String>> requestEntity = new HttpEntity<>(formData, headers);

        ResponseEntity<String> response = restTemplate.exchange(
                url,
                HttpMethod.POST,
                requestEntity,
                String.class
        );

        ObjectMapper objectMapper = new ObjectMapper();
        JsonNode jsonNode = objectMapper.readTree(response.getBody());

        return jsonNode.has("access_token") ? jsonNode.get("access_token").asText() : null;
    }




    public String createEventInGoogleCalendar(String email, String summary, Date startDate) throws IOException {
        Utilisateur user = utilisateurRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("Utilisateur non trouvé: " + email));

        String accessToken = getAccessToken(user.getGoogleRefreshToken());

        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(accessToken);
        headers.setContentType(MediaType.APPLICATION_JSON);

        // Construct the event JSON
        ObjectMapper mapper = new ObjectMapper();

        ObjectNode event = mapper.createObjectNode();
        event.put("summary", "[TravelCarry] " + summary); // ✅ prefix added here

        ObjectNode start = event.putObject("start");
        start.put("dateTime", new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssXXX").format(startDate));
        start.put("timeZone", "Europe/Paris");

        ObjectNode end = event.putObject("end");
        Calendar cal = Calendar.getInstance();
        cal.setTime(startDate);
        cal.add(Calendar.HOUR, 2); // 2-hour duration
        end.put("dateTime", new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssXXX").format(cal.getTime()));
        end.put("timeZone", "Europe/Paris");

        HttpEntity<String> request = new HttpEntity<>(mapper.writeValueAsString(event), headers);

        ResponseEntity<String> response = restTemplate.exchange(
                "https://www.googleapis.com/calendar/v3/calendars/primary/events",
                HttpMethod.POST,
                request,
                String.class
        );

        JsonNode eventJson = new ObjectMapper().readTree(response.getBody());
        return eventJson.get("id").asText(); // <-- ✅ return eventId

    }



    // ✅ Exchange Authorization Code for Refresh Token and Store It
    public void exchangeCodeAndStoreTokens(String email, String code) throws IOException {
        String url = "https://oauth2.googleapis.com/token";
        RestTemplate restTemplate = new RestTemplate();

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

        MultiValueMap<String, String> requestBody = new LinkedMultiValueMap<>();
        requestBody.add("code", code);
        requestBody.add("client_id", googleApiProperties.getClientId());
        requestBody.add("client_secret", googleApiProperties.getClientSecret());
        requestBody.add("redirect_uri", "http://localhost:8080/api/auth/callback");
        requestBody.add("grant_type", "authorization_code");

        HttpEntity<MultiValueMap<String, String>> requestEntity = new HttpEntity<>(requestBody, headers);

        // ✅ PRINT EVERYTHING before the request
        System.out.println("📥 AUTH CODE: " + code);
        System.out.println("📤 Sending request to: " + url);
        System.out.println("🔑 client_id: " + googleApiProperties.getClientId());
        System.out.println("🔒 client_secret: " + googleApiProperties.getClientSecret());
        System.out.println("↩️ redirect_uri: http://localhost:8080/api/auth/callback");
        System.out.println("📦 requestBody: " + requestBody);

        try {
            ResponseEntity<String> response = restTemplate.exchange(
                    url, HttpMethod.POST, requestEntity, String.class);

            System.out.println("✅ Google OAuth Token Response: " + response.getBody());

            ObjectMapper objectMapper = new ObjectMapper();
            JsonNode jsonNode = objectMapper.readTree(response.getBody());

            String accessToken = jsonNode.has("access_token") ? jsonNode.get("access_token").asText() : null;
            String refreshToken = jsonNode.has("refresh_token") ? jsonNode.get("refresh_token").asText() : null;

            System.out.println("🪪 access_token: " + accessToken);
            System.out.println("🔁 refresh_token: " + refreshToken);


            if (refreshToken == null) {
                System.out.println("❌ No refresh token received! Try using prompt=consent.");
                return;
            }

            // Save tokens
            Optional<Utilisateur> utilisateurOpt = utilisateurRepository.findByEmail(email);
            if (utilisateurOpt.isPresent()) {
                Utilisateur utilisateur = utilisateurOpt.get();
                utilisateur.setGoogleAccessToken(accessToken);
                utilisateur.setGoogleRefreshToken(refreshToken);
                utilisateurRepository.save(utilisateur);
                System.out.println("✅ Tokens stored for: " + email);
            } else {
                System.out.println("❌ User not found for email: " + email);
            }

        } catch (HttpClientErrorException e) {
            System.out.println("❌ HTTP Error while requesting access token: " + e.getStatusCode());
            System.out.println("❌ Response body: " + e.getResponseBodyAsString());
        } catch (Exception e) {
            System.out.println("❌ General error during token exchange: " + e.getMessage());
        }
    }

    public List<GoogleCalendarEventDto> getCalendarEvents(String accessToken) {
        String url = "https://www.googleapis.com/calendar/v3/calendars/primary/events";

        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(accessToken);
        HttpEntity<Void> request = new HttpEntity<>(headers);

        ResponseEntity<String> response = restTemplate.exchange(url, HttpMethod.GET, request, String.class);
        ObjectMapper mapper = new ObjectMapper();

        List<GoogleCalendarEventDto> events = new ArrayList<>();

        try {
            JsonNode root = mapper.readTree(response.getBody());
            for (JsonNode eventNode : root.get("items")) {
                events.add(new GoogleCalendarEventDto(
                        eventNode.get("summary").asText(),
                        eventNode.get("start").get("dateTime").asText(),
                        eventNode.get("end").get("dateTime").asText(),
                        eventNode.get("htmlLink").get("dateTime").asText()
                ));
            }
        } catch (JsonProcessingException e) {
            System.out.println("❌ Failed to parse Google Calendar response: " + e.getMessage());
        }

        return events;
    }

    public List<GoogleCalendarEventDto> getEvents(String email) throws IOException {
        // Step 1: Retrieve user
        Utilisateur user = utilisateurRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("Utilisateur non trouvé: " + email));

        String refreshToken = user.getGoogleRefreshToken();
        if (refreshToken == null) {
            throw new RuntimeException("Aucun refresh token trouvé pour l'utilisateur: " + email);
        }

        // Step 2: Get access token
        String accessToken = getAccessToken(refreshToken);
        System.out.println("✅ Fetched access token: " + accessToken);

        // Step 3: Correct timeMin format - ISO 8601
        ZonedDateTime now = ZonedDateTime.now(ZoneOffset.UTC).minusMonths(6); // Optional: look back 6 months
        String timeMin = now.format(DateTimeFormatter.ISO_OFFSET_DATE_TIME);

        System.out.println("🕒 timeMin = " + timeMin);


        // Step 4: Build the Calendar API URL without manual encoding
        String calendarApiUrl = "https://www.googleapis.com/calendar/v3/calendars/primary/events"
                + "?timeMin=" + timeMin
                + "&singleEvents=true"
                + "&orderBy=startTime";

        System.out.println("📡 Requesting URL: " + calendarApiUrl);

        // Step 5: Make HTTP request
        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(accessToken);
        headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));

        HttpEntity<Void> request = new HttpEntity<>(headers);
        ResponseEntity<String> response = restTemplate.exchange(
                calendarApiUrl,
                HttpMethod.GET,
                request,
                String.class
        );
        System.out.println("🧾 Raw JSON Response: " + response.getBody());

        // Step 6: Parse JSON response
        ObjectMapper mapper = new ObjectMapper();
        JsonNode items = mapper.readTree(response.getBody()).get("items");

        List<GoogleCalendarEventDto> events = new ArrayList<>();
        if (items != null && items.isArray()) {
            for (JsonNode item : items) {
                String summary = item.has("summary") ? item.get("summary").asText() : "";
                System.out.println("📌 Event: " + summary);

                if (summary.toLowerCase().contains("voyage")) {
                    GoogleCalendarEventDto dto = new GoogleCalendarEventDto();
                    dto.setSummary(summary);
                    dto.setStart(item.path("start").path("dateTime").asText(""));
                    dto.setEnd(item.path("end").path("dateTime").asText(""));
                    dto.setHtmlLink(item.path("htmlLink").asText(""));
                    events.add(dto);
                }
            }
        }

        return events;
    }

    public void deleteEventById(String email, String eventId) throws IOException {
        Utilisateur user = utilisateurRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("Utilisateur non trouvé"));

        String accessToken = getAccessToken(user.getGoogleRefreshToken());

        String deleteUrl = "https://www.googleapis.com/calendar/v3/calendars/primary/events/" + eventId;

        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(accessToken);
        headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));

        HttpEntity<Void> request = new HttpEntity<>(headers);
        restTemplate.exchange(deleteUrl, HttpMethod.DELETE, request, Void.class);

        System.out.println("✅ Google Calendar event deleted with ID: " + eventId);
    }












}
