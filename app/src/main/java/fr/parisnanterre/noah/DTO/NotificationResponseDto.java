package fr.parisnanterre.noah.DTO;

import lombok.Data;

import java.util.Date;

@Data
public class NotificationResponseDto {
    private Long id;
    private String message;
    private boolean isRead;
    private Date createdAt;
    private Long expediteurId;
    private DemandeResponse demande; // Nested DTO for Demande

}
