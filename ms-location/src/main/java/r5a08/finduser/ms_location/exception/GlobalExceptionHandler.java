package r5a08.finduser.ms_location.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Traduit les exceptions levées par les controllers en réponses HTTP (format RFC 9457).
 * Le code HTTP de la réponse est celui porté par le ProblemDetail renvoyé.
 */
@RestControllerAdvice
public class GlobalExceptionHandler {

    /** Localisation introuvable : 404 */
    @ExceptionHandler(LocationNotFoundException.class)
    public ProblemDetail handleLocationNotFound(LocationNotFoundException exception) {
        ProblemDetail problem = ProblemDetail.forStatusAndDetail(HttpStatus.NOT_FOUND, exception.getMessage());
        problem.setTitle("Localisation introuvable");
        return problem;
    }

    /** Utilisateur introuvable (réponse exists = false de MS-User) : 404 */
    @ExceptionHandler(UserNotFoundException.class)
    public ProblemDetail handleUserNotFound(UserNotFoundException exception) {
        ProblemDetail problem = ProblemDetail.forStatusAndDetail(HttpStatus.NOT_FOUND, exception.getMessage());
        problem.setTitle("Utilisateur introuvable");
        return problem;
    }

    /** Échec de @Valid sur un DTO : 400, avec la liste des champs en erreur */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ProblemDetail handleValidation(MethodArgumentNotValidException exception) {
        Map<String, String> errors = new LinkedHashMap<>();
        for (FieldError error : exception.getBindingResult().getFieldErrors()) {
            // putIfAbsent : si un champ a plusieurs erreurs, on garde la première
            errors.putIfAbsent(error.getField(), error.getDefaultMessage());
        }

        ProblemDetail problem = ProblemDetail.forStatusAndDetail(HttpStatus.BAD_REQUEST, "Données invalides");
        problem.setTitle("Requête invalide");
        problem.setProperty("errors", errors);
        return problem;
    }

    /** Valeur refusée par un setter de l'entité : 400 */
    @ExceptionHandler(IllegalArgumentException.class)
    public ProblemDetail handleIllegalArgument(IllegalArgumentException exception) {
        ProblemDetail problem = ProblemDetail.forStatusAndDetail(HttpStatus.BAD_REQUEST, exception.getMessage());
        problem.setTitle("Requête invalide");
        return problem;
    }

    /** MS-User injoignable (statut gRPC UNAVAILABLE / DEADLINE_EXCEEDED) : 503 */
    @ExceptionHandler(UserServiceUnavailableException.class)
    public ProblemDetail handleUserServiceUnavailable(UserServiceUnavailableException exception) {
        ProblemDetail problem = ProblemDetail.forStatusAndDetail(HttpStatus.SERVICE_UNAVAILABLE, exception.getMessage());
        problem.setTitle("Service utilisateurs indisponible");
        return problem;
    }
}
