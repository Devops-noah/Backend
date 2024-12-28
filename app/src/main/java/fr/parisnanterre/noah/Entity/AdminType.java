package fr.parisnanterre.noah.Entity;

import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Entity
@Data
@EqualsAndHashCode(callSuper = false) // Add this to remove the warning
@DiscriminatorValue("admin")
public class AdminType extends Utilisateur {
    // Admin-specific fields (if needed) and behaviors

    /* Will add */
//    public void deactivateUser(Utilisateur user) {
//        user.setEnabled(false); // Disable the user account
//        // Add logic to save the change in the database
//    }
//
//    public void approveAnnonce(Annonce annonce) {
//        annonce.setApproved(true); // Mark the annonce as approved
//        // Save the status change
//    }
}
