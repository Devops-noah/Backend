/*
 * This source file was generated by the Gradle 'init' task
 */
package fr.parisnanterre.noah;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;

@SpringBootApplication(scanBasePackages = "fr.parisnanterre.noah")
public class TravelCarryApplication {

    public static void main(String[] args) {
        SpringApplication.run(TravelCarryApplication.class, args);
    }
}
