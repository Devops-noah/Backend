package fr.parisnanterre.noah.Service;

import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;

@Service
public class ImageServiceImpl {
    private final String uploadDirectory = "C:\\Users\\dell\\Desktop\\Nanterre-miage\\Master 1\\semestre 1\\Methodes-outils-developpement-logiciel\\Model-devops-Damien\\Devops-noah\\Backend\\app/uploads/";

    public String saveProfileImage(MultipartFile file, Long userId) throws IOException {
        // Ensure the upload directory exists
        System.out.println("upload: " + uploadDirectory);
        File directory = new File(uploadDirectory);
        if (!directory.exists()) {
            directory.mkdirs();
        }

        // Create a unique file name (e.g., "profile-<userId>.jpg")
        String fileName = "profile-" + userId + "." + getFileExtension(file.getOriginalFilename());
        File destination = new File(uploadDirectory + fileName);

        // Save the file to the upload directory
        file.transferTo(destination);

        // Return the file's URL or path
        return fileName;
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
