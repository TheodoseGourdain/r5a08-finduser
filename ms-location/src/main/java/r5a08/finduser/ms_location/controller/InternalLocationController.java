package r5a08.finduser.ms_location.controller;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import io.swagger.v3.oas.annotations.Hidden;

import r5a08.finduser.ms_location.dto.LocationPutRequest;
import r5a08.finduser.ms_location.dto.LocationPatchRequest;
import r5a08.finduser.ms_location.dto.LocationRequest;
import r5a08.finduser.ms_location.dto.LocationResponse;
import r5a08.finduser.ms_location.service.LocationService;

@RestController
@RequestMapping("/internal")
@Hidden   // absent de Swagger UI : ce n'est pas l'API publique
public class InternalLocationController {

    private final LocationService locationService;

    public InternalLocationController(LocationService locationService) {
        this.locationService = locationService;
    }

    /** Appelé par MS-User avant la suppression d'un utilisateur. */
    @DeleteMapping("/users/{userId}/locations")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteLocationsOfUser(@PathVariable Long userId) {
        this.locationService.deleteLocationsByUserId(userId);
    }
}
