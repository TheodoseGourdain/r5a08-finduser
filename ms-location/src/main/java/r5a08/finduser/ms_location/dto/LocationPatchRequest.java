package r5a08.finduser.ms_location.dto;

import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.PastOrPresent;
import jakarta.validation.constraints.Pattern;
import r5a08.finduser.ms_location.model.Location;

import java.time.LocalDateTime;

/**
 * Corps JSON attendu pour PATCH /locations/{id}.
 * Tous les champs sont optionnels ; un champ absent n'est pas modifié.
 */
public record LocationPatchRequest(

        @DecimalMin(value = "-90.0", message = "La latitude doit être supérieure ou égale à -90.")
        @DecimalMax(value = "90.0", message = "La latitude doit être inférieure ou égale à 90.")
        Double latitude,

        @DecimalMin(value = "-180.0", message = "La longitude doit être supérieure ou égale à -180.")
        @DecimalMax(value = "180.0", message = "La longitude doit être inférieure ou égale à 180.")
        Double longitude,

        @PastOrPresent(message = "La date ne peut pas être dans le futur.")
        LocalDateTime dateHeure,

        @Pattern(regexp = ".*\\S.*", message = "Le nom ne peut pas être vide.")
        String nom
) {

    /** Applique la requête sur une entité existante (utilisé pour le PATCH). */
    public void applyTo(Location location) {
        if (latitude != null) {
            location.setLatitude(latitude);
        }
        if (longitude != null) {
            location.setLongitude(longitude);
        }
        if (dateHeure != null) {
            location.setDateHeure(dateHeure);
        }
        if (nom != null) {
            location.setNom(nom);
        }
    }
}