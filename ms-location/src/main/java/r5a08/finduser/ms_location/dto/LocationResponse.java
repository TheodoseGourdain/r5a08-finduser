package r5a08.finduser.ms_location.dto;

import r5a08.finduser.ms_location.model.Location;

import java.time.LocalDateTime;

/**
 * Représentation JSON renvoyée par l'API.
 * Découple le format de l'API de l'entité JPA.
 */
public record LocationResponse(
        Long id,
        String userId,
        Double latitude,
        Double longitude,
        LocalDateTime dateHeure,
        String nom
) {

    public static LocationResponse fromEntity(Location location) {
        return new LocationResponse(
                location.getId(),
                location.getUserId(),
                location.getLatitude(),
                location.getLongitude(),
                location.getDateHeure(),
                location.getNom()
        );
    }
}