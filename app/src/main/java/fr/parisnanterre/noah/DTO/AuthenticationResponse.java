package fr.parisnanterre.noah.DTO;

import fr.parisnanterre.noah.Entity.RoleType;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class AuthenticationResponse {
    private String jwt;
    private String role;  // This should be String, not RoleType
    private String userType;
    private Long userId;

    public AuthenticationResponse(String jwt, RoleType role, String userType, Long userId) {
        this.jwt = jwt;
        this.role = role.name();  // Convert RoleType to String
        this.userType = userType;
        this.userId = userId;
    }
}
