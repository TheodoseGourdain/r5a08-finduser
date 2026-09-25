package r5a08.finduser.ms_location.user;

import org.springframework.stereotype.Component;

/**
 * MS-User de substitution le temps que MS-User soit en place.
 */
@Component 
public class StubUserVerifier implements UserVerifier {
    
    @Override 
    public boolean userExists(String userId) {

        if (userId.endsWith("0") || userId.endsWith("2") || userId.endsWith("4") || userId.endsWith("6") || userId.endsWith("8") ) {
            return true;
        }
        return false;
    }
}
