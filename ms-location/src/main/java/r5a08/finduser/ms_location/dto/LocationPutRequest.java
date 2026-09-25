package r5a08.finduser.ms_location.dto;

import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PastOrPresent;
import r5a08.finduser.ms_location.model.Location;

import java.time.LocalDateTime;

/**
 * Corps JSON attendu pour PUT /locations/{id}.
 * Tous les champs sont obligatoires ; le userId n'est pas modifiable.
 */
public record LocationPutRequest(

        @NotNull(message = "La latitude est obligatoire.")
        @DecimalMin(value = "-90.0", message = "La latitude doit être supérieure ou égale à -90.")
        @DecimalMax(value = "90.0", message = "La latitude doit être inférieure ou égale à 90.")
        Double latitude,

        @NotNull(message = "La longitude est obligatoire.")
        @DecimalMin(value = "-180.0", message = "La longitude doit être supérieure ou égale à -180.")
        @DecimalMax(value = "180.0", message = "La longitude doit être inférieure ou égale à 180.")
        Double longitude,

        @NotNull(message = "La date et l'heure sont obligatoires.")
        @PastOrPresent(message = "La date ne peut pas être dans le futur.")
        LocalDateTime dateHeure,

        @NotBlank(message = "Le nom est obligatoire.")
        String nom
) {

    /** Applique la requête sur une entité existante (utilisé pour le PUT). */
    public void applyTo(Location location) {
        location.setLatitude(latitude);
        location.setLongitude(longitude);
        location.setDateHeure(dateHeure);
        location.setNom(nom);
    }
}