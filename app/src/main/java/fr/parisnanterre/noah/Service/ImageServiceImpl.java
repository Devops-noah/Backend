package fr.parisnanterre.noah.Service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.ResourceAccessException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.util.UriComponentsBuilder;

import java.io.File;
import java.io.IOException;
import java.util.Base64;
import java.util.concurrent.TimeUnit;
import org.json.JSONObject;

@Service
public class ImageServiceImpl {

    private final RestTemplate restTemplate;

    @Autowired
    public ImageServiceImpl(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    private final String uploadDirectory = "C:\\Users\\dell\\Desktop\\Nanterre-miage\\Master 1\\semestre 1\\Methodes-outils-developpement-logiciel\\Model-devops-Damien\\Devops-noah\\Backend\\app/uploads/";

    @Value("${imgur.client-id:default-client-id}")
    private String clientId;


    private static final String IMGUR_UPLOAD_URL = "https://api.imgur.com/3/image";
    private static final String CLIENT_ID = "36a7180fcb9a12c"; // Replace with your Imgur client ID
    private static final int MAX_RETRY_ATTEMPTS = 3;

    public String uploadImageToImgur(byte[] imageBytes) {
        RestTemplate restTemplate = new RestTemplate();
        String base64Image = Base64.getEncoder().encodeToString(imageBytes);
        int retryCount = 0;

        while (retryCount < MAX_RETRY_ATTEMPTS) {
            try {
                // Set headers
                HttpHeaders headers = new HttpHeaders();
                headers.set("Authorization", "Client-ID " + CLIENT_ID);
                headers.set("Content-Type", "application/json");

                // Create the request body
                String requestBody = "{\"image\":\"" + base64Image + "\", \"type\":\"base64\"}";

                // Wrap headers and body in HttpEntity
                HttpEntity<String> entity = new HttpEntity<>(requestBody, headers);

                // Make the POST request
                ResponseEntity<String> response = restTemplate.postForEntity(IMGUR_UPLOAD_URL, entity, String.class);

                // Return the link if the upload is successful
                if (response.getStatusCode().is2xxSuccessful()) {
                    // Parse the JSON response to get the link
                    String responseBody = response.getBody();
                    JSONObject jsonResponse = new JSONObject(responseBody);

                    // Extract the image URL (link)
                    String imageLink = jsonResponse.getJSONObject("data").getString("link");

                    // Create a JSON object with the desired format
                    String jsonResponseFormatted = "{ \"link\": \"" + imageLink + "\" }";

                    // Return the formatted JSON string
                    return jsonResponseFormatted;
                }

            } catch (HttpClientErrorException ex) {
                // Handle client errors (4xx)
                System.err.println("Client error during image upload: " + ex.getStatusCode() + " - " + ex.getResponseBodyAsString());
                throw new IllegalStateException("Failed to upload image to Imgur: " + ex.getResponseBodyAsString(), ex);

            } catch (ResourceAccessException ex) {
                // Handle network issues
                System.err.println("Network issue: " + ex.getMessage());

            } catch (Exception ex) {
                // Handle other unexpected exceptions
                System.err.println("Unexpected error during image upload: " + ex.getMessage());
            }

            // Retry logic with exponential backoff
            retryCount++;
            try {
                long waitTime = (long) Math.pow(2, retryCount) * 1000; // Exponential backoff
                System.out.println("Retrying... Attempt: " + retryCount + " after waiting " + waitTime + " ms");
                TimeUnit.MILLISECONDS.sleep(waitTime);
            } catch (InterruptedException interruptedEx) {
                Thread.currentThread().interrupt();
                throw new IllegalStateException("Retry attempt interrupted", interruptedEx);
            }
        }

        // Throw exception if all retry attempts fail
        throw new IllegalStateException("Exceeded retry attempts to upload image to Imgur.");
    }

    private HttpHeaders createHeaders() {
        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", "Client-ID YOUR_CLIENT_ID");
        return headers;
    }

    private int getRetryAfter(HttpHeaders headers) {
        String retryAfter = headers.getFirst("Retry-After");
        return retryAfter != null ? Integer.parseInt(retryAfter) : 1; // Default to 1 second
    }

    // Method to parse Imgur API response
    private String parseImgurResponse(String responseBody) {
        // You can use a JSON parser like Jackson or Gson to extract the 'link' value
        // For now, return the string as is for simplicity
        // You can implement proper JSON parsing here
        return responseBody;  // This is just a placeholder
    }

    public byte[] updateProfileImage(MultipartFile file) throws IOException {
        // Convert the file into a byte array
        return file.getBytes();
    }

    public String updateProfileImage(MultipartFile file, Long userId, String existingFileName) throws IOException {
        // Ensure the upload directory exists
        File directory = new File(uploadDirectory);
        if (!directory.exists()) {
            directory.mkdirs();
        }

        // Delete the old image if it exists
        if (existingFileName != null && !existingFileName.isEmpty()) {
            File oldFile = new File(uploadDirectory + existingFileName);
            if (oldFile.exists()) {
                oldFile.delete();
            }
        }

        // Create a new file name (e.g., "profile-<userId>.jpg")
        String fileName = "profile-" + userId + "." + getFileExtension(file.getOriginalFilename());
        File destination = new File(uploadDirectory + fileName);

        // Save the new file to the upload directory
        file.transferTo(destination);

        // Return the new file name
        return fileName;
    }


    public String getDefaultProfileImage() {
        return "https://example.com/default-profile.png";
    }

    private String getFileExtension(String fileName) {
        return fileName.substring(fileName.lastIndexOf('.') + 1);
    }
}
