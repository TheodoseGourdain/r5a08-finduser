package r5a08.finduser.ms_location.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "locations")
public class Location {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String userId;

    @Column(nullable = false)
    private Double latitude;

    @Column(nullable = false)
    private Double longitude;

    @Column(nullable = false)
    private LocalDateTime dateHeure;

    @Column(nullable = false)
    private String nom;

    protected Location() {
        // requis par JPA
    }

    public Location(String userId, Double latitude, Double longitude, LocalDateTime dateHeure, String nom) {
        this.setUserId(userId);
        this.setLatitude(latitude);
        this.setLongitude(longitude);
        this.setDateHeure(dateHeure);
        this.setNom(nom);
    }

    public Long getId() {
        return this.id;
    }

    public String getUserId() {
        return this.userId;
    }

    public Double getLatitude() {
        return this.latitude;
    }

    public Double getLongitude() {
        return this.longitude;
    }

    public LocalDateTime getDateHeure() {
        return this.dateHeure;
    }

    public String getNom() {
        return this.nom;
    }

    // Setter privé, uniquement utilisé pour le constructeur
    private void setUserId(String userId) {
        if (userId == null || userId.isBlank()) {
            throw new IllegalArgumentException("L'identifiant d'un utilisateur d'une localisation ne peut pas être vide.");
        }
        this.userId = userId;
    }

    public void setLatitude(Double latitude) {
        if (latitude == null) {
            throw new IllegalArgumentException("La latitude d'une localisation ne peut pas être nulle.");
        }
        if (latitude < -90 || latitude > 90) {
            throw new IllegalArgumentException("La latitude doit être comprise entre -90 et 90.");
        }
        this.latitude = latitude;
    }

    public void setLongitude(Double longitude) {
        if (longitude == null) {
            throw new IllegalArgumentException("La longitude d'une localisation ne peut pas être nulle.");
        }
        if (longitude < -180 || longitude > 180) {
            throw new IllegalArgumentException("La longitude doit être comprise entre -180 et 180.");
        }
        this.longitude = longitude;
    }

    public void setDateHeure(LocalDateTime dateHeure) {
        if (dateHeure == null) {
            throw new IllegalArgumentException("La date d'une localisation ne peut pas être nulle.");
        }
        this.dateHeure = dateHeure;
    }

    public void setNom(String nom) {
        if (nom == null || nom.isBlank()) {
            throw new IllegalArgumentException("Le nom d'une localisation ne peut pas être vide.");
        }
        this.nom = nom;
    }
}