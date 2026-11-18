package r5a08.finduser.ms_location.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.data.domain.Sort;

import r5a08.finduser.ms_location.dto.LocationPatchRequest;
import r5a08.finduser.ms_location.dto.LocationPutRequest;
import r5a08.finduser.ms_location.dto.LocationRequest;
import r5a08.finduser.ms_location.dto.LocationResponse;
import r5a08.finduser.ms_location.model.Location;
import r5a08.finduser.ms_location.repository.LocationRepository;
import r5a08.finduser.ms_location.user.UserVerifier;
import r5a08.finduser.ms_location.exception.UserNotFoundException;
import r5a08.finduser.ms_location.exception.LocationNotFoundException;

import java.util.List;

@Service
public class LocationService {

    private final LocationRepository locationRepository;
    private final UserVerifier userVerifier;

    public LocationService(LocationRepository locationRepository, UserVerifier userVerifier) {
        this.locationRepository = locationRepository;
        this.userVerifier = userVerifier;
    }

    /** GET /locations */
    @Transactional(readOnly = true)
    public List<LocationResponse> getAllLocations() {
        List<Location> locations = this.locationRepository.findAll(Sort.by("id"));

        return locations.stream()
                .map(LocationResponse::fromEntity)
                .toList();
    }

    /** GET /locations/{id} */
    @Transactional(readOnly = true)
    public LocationResponse getLocationById(Long id) {
        Location location = this.findLocation(id);

        return LocationResponse.fromEntity(location);
    }

    /** GET /locations?userId=... */
    @Transactional(readOnly = true)
    public List<LocationResponse> getLocationsByUserId(Long userId) {
        this.checkUser(userId);
        List<Location> locations = this.locationRepository.findByUserId(userId);

        return locations.stream()
                .map(LocationResponse::fromEntity)
                .toList();
    }

    /** POST /locations : vérifier l'utilisateur, puis enregistrer. */
    @Transactional
    public LocationResponse addLocation(LocationRequest request) {
        this.checkUser(request.userId());
    
        Location saved = this.locationRepository.save(request.toEntity());
        return LocationResponse.fromEntity(saved);
    }

    /** PUT /locations/{id} : chercher, modifier, enregistrer. */
    @Transactional
    public LocationResponse updateLocation(Long id, LocationPutRequest request) {
        Location location = this.findLocation(id);

        request.applyTo(location);
        Location modified = this.locationRepository.save(location);
    
        return LocationResponse.fromEntity(modified);
    }

    /** PATCH /locations/{id} : chercher, modifier, enregistrer. */
    @Transactional
    public LocationResponse patchLocation(Long id, LocationPatchRequest request) {
        Location location = this.findLocation(id);

        request.applyTo(location);
        Location modified = this.locationRepository.save(location);
    
        return LocationResponse.fromEntity(modified);
    }

    /** DELETE /locations/{id} */
    @Transactional
    public void deleteLocation(Long id) {
        Location location = this.findLocation(id);

        this.locationRepository.delete(location);
    }

    /** DELETE /locations?userId={id} */
    @Transactional
    public void deleteLocationsByUserId(Long userId) {
        this.locationRepository.deleteByUserId(userId);
    }

    /** Méthode utilitaire : lever l'exception « utilisateur introuvable » si besoin. */
    private void checkUser(Long userId) {
        if (!this.userVerifier.userExists(userId)) {
            throw new UserNotFoundException(userId);
        }
    }

    /** Méthode utilitaire : charger une entité ou lever l'exception « localisation introuvable ». */
    private Location findLocation(Long id) {
        return this.locationRepository.findById(id)
            .orElseThrow(() -> new LocationNotFoundException(id));
    }
}