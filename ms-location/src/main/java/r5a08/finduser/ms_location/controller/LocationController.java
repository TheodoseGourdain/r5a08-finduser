package r5a08.finduser.ms_location.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import r5a08.finduser.ms_location.dto.LocationPutRequest;
import r5a08.finduser.ms_location.dto.LocationPatchRequest;
import r5a08.finduser.ms_location.dto.LocationRequest;
import r5a08.finduser.ms_location.dto.LocationResponse;
import r5a08.finduser.ms_location.service.LocationService;

import java.util.List;

@RestController
@RequestMapping("/locations")
@Tag(name = "Localisations", description = "Gestion des localisations GPS des utilisateurs")
public class LocationController {

    private final LocationService locationService;

    public LocationController(LocationService locationService) {
        this.locationService = locationService;
    }

    /** GET /locations et GET /locations?userId=... */
    @GetMapping
    @Operation(
            summary = "Lister les localisations",
            description = "Renvoie toutes les localisations, ou seulement celles d'un utilisateur si userId est fourni."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Liste des localisations (éventuellement vide)"),
            @ApiResponse(responseCode = "404", description = "Utilisateur introuvable (si userId est fourni)",
                    content = @Content)
    })
    public List<LocationResponse> getLocations(
            @Parameter(description = "Identifiant de l'utilisateur pour filtrer les résultats", example = "2")
            @RequestParam(required = false) String userId) {
        if (userId == null) {
            return this.locationService.getAllLocations();
        }
        return this.locationService.getLocationsByUserId(userId);
    }

    /** GET /locations/{id} */
    @GetMapping("/{id}")
    @Operation(summary = "Lire une localisation")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Localisation trouvée"),
            @ApiResponse(responseCode = "404", description = "Localisation introuvable", content = @Content)
    })
    public LocationResponse getLocationById(
            @Parameter(description = "Identifiant de la localisation", example = "1")
            @PathVariable Long id) {
        return this.locationService.getLocationById(id);
    }

    /** POST /locations : 201 Created */
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @Operation(
            summary = "Enregistrer une localisation",
            description = "Vérifie auprès de MS-User que l'utilisateur existe avant l'enregistrement."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Localisation créée"),
            @ApiResponse(responseCode = "400", description = "Données invalides", content = @Content),
            @ApiResponse(responseCode = "404", description = "Utilisateur introuvable", content = @Content)
    })
    public LocationResponse addLocation(@Valid @RequestBody LocationRequest request) {
        return this.locationService.addLocation(request);
    }

    /** PUT /locations/{id} */
    @PutMapping("/{id}")
    @Operation(
            summary = "Réécrire une localisation",
            description = "Remplace toutes les données de la localisation."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Localisation modifiée"),
            @ApiResponse(responseCode = "400", description = "Données invalides", content = @Content),
            @ApiResponse(responseCode = "404", description = "Localisation introuvable",
                    content = @Content)
    })
    public LocationResponse updateLocation(
            @Parameter(description = "Identifiant de la localisation", example = "1")
            @PathVariable Long id,
            @Valid @RequestBody LocationPutRequest request) {
        return this.locationService.updateLocation(id, request);
    }

    /** PATCH /locations/{id} */
    @PatchMapping("/{id}")
    @Operation(
            summary = "Modifier (partiellement) une localisation",
            description = "Remplace les données fournies de la localisation."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Localisation modifiée"),
            @ApiResponse(responseCode = "400", description = "Données invalides", content = @Content),
            @ApiResponse(responseCode = "404", description = "Localisation introuvable",
                    content = @Content)
    })
    public LocationResponse patchLocation(
            @Parameter(description = "Identifiant de la localisation", example = "1")
            @PathVariable Long id,
            @Valid @RequestBody LocationPatchRequest request) {
        return this.locationService.patchLocation(id, request);
    }

    /** DELETE /locations/{id} : 204 No Content */
    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @Operation(summary = "Supprimer une localisation")
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "Localisation supprimée"),
            @ApiResponse(responseCode = "404", description = "Localisation introuvable", content = @Content)
    })
    public void deleteLocation(
            @Parameter(description = "Identifiant de la localisation", example = "1")
            @PathVariable Long id) {
        this.locationService.deleteLocation(id);
    }
}