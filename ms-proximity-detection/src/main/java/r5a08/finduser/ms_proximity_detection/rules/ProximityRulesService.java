package r5a08.finduser.ms_proximity_detection.rules;

import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

/**
 * Lecture des règles d'éloignement dans Redis (code repris du sujet).
 *
 * restricted:users:{userId}                    → Set des utilisateurs dont userId doit rester éloigné
 * restricted:threshold:{userId}:{otherUserId}  → seuil en mètres (String contenant un nombre)
 */
@Service
public class ProximityRulesService {

    private static final Logger log = LoggerFactory.getLogger(ProximityRulesService.class);

    // StringRedisTemplate est un RedisTemplate<String, String> fourni par Spring Boot
    private final StringRedisTemplate redisTemplate;

    public ProximityRulesService(StringRedisTemplate redisTemplate) {
        this.redisTemplate = redisTemplate;
    }

    /** Utilisateurs dont userId doit rester éloigné (ensemble vide s'il n'y a aucune règle). */
    public Set<String> getRestrictedUsers(String userId) {
        Set<String> members = redisTemplate.opsForSet()
                .members("restricted:users:" + userId);
        return members != null ? members : Set.of();
    }

    /** Seuil à respecter entre deux utilisateurs, ou null s'il n'est pas défini (ou invalide). */
    public Double getThreshold(String userId, String otherUserId) {
        String value = redisTemplate.opsForValue()
                .get("restricted:threshold:" + userId + ":" + otherUserId);
        if (value == null) {
            return null;
        }
        try {
            return Double.parseDouble(value);
        } catch (NumberFormatException ex) {
            log.warn("Seuil invalide dans Redis pour {} / {} : {}", userId, otherUserId, value);
            return null;
        }
    }
}
