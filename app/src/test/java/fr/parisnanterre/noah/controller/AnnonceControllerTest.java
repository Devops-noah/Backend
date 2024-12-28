package fr.parisnanterre.noah.controller;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import fr.parisnanterre.noah.Controller.AnnonceController;
import fr.parisnanterre.noah.DTO.AnnonceResponse;
import fr.parisnanterre.noah.Repository.AnnonceRepository;
import fr.parisnanterre.noah.Repository.UtilisateurRepository;
import fr.parisnanterre.noah.Service.AnnonceServiceImpl;
import fr.parisnanterre.noah.Service.CustomUserDetailsService;
import fr.parisnanterre.noah.util.JwtUtil;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;

@ExtendWith(SpringExtension.class)
@WebMvcTest(AnnonceController.class)
public class AnnonceControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private AnnonceServiceImpl annonceServiceImpl; // Mock the service layer

    @MockBean
    private JwtUtil jwtUtil; // Mock JwtUtil for authentication/authorization

    @MockBean
    private CustomUserDetailsService customUserDetailsService; // Mock CustomUserDetailsService for user details

    @MockBean
    private UtilisateurRepository utilisateurRepository; // Mock UtilisateurRepository for the controller

    @MockBean
    private AnnonceRepository annonceRepository; // Mock AnnonceRepository for the controller

    @WithMockUser(username = "testuser", roles = "Voyageur")
    @Test
    public void testCreateAnnonce_Success() throws Exception {
        // Prepare mock data
        AnnonceResponse mockResponse = new AnnonceResponse();
        mockResponse.setId(1L);
        mockResponse.setPoidsDisponible(10.0);

        when(annonceServiceImpl.createAnnonce(any(), any(), any(), anyInt())).thenReturn(mockResponse);

        // Mock JwtUtil behavior (if needed)
        when(jwtUtil.validateToken(anyString(), any())).thenReturn(true);

        // Perform a POST request
        mockMvc.perform(post("/api/annonces/{voyageId}", 1)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{ \"datePublication\": \"2024-12-26\", \"poidsDisponible\": 10.0 }"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.poidsDisponible").value(10.0));
    }

    @Test
    public void testCreateAnnonce_Error() throws Exception {
        // Simulate an error during service call
        when(annonceServiceImpl.createAnnonce(any(), any(), any(), anyInt()))
                .thenThrow(new RuntimeException("Error creating Annonce"));

        // Perform a POST request and assert the response
        mockMvc.perform(post("/api/annonces/{voyageId}", 1)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{ \"datePublication\": \"2024-12-26\", \"poidsDisponible\": 10.0 }"))
                .andExpect(status().isBadRequest())
                .andExpect(content().string("Error: Error creating Annonce"));
    }
}
