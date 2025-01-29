package fr.parisnanterre.noah.DTO;

import lombok.Data;

@Data
public class AuthenticationRequest {
    private String email;
    private String motDePasse;
}
