/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package socialsky.codingtest.app;

import com.github.toastshaman.dropwizard.auth.jwt.JsonWebTokenValidator;
import com.github.toastshaman.dropwizard.auth.jwt.model.JsonWebToken;
import com.github.toastshaman.dropwizard.auth.jwt.validator.ExpiryValidator;
import com.google.common.base.Optional;
import io.dropwizard.auth.AuthenticationException;
import io.dropwizard.auth.Authenticator;
import java.security.Principal;


/**
 *
 * @author ivan
 */
class AuthClient implements Authenticator<JsonWebToken, Principal> {

    public AuthClient() {
    }

    @Override
    public Optional<Principal> authenticate(JsonWebToken token) throws AuthenticationException {

        final JsonWebTokenValidator expiryValidator = new ExpiryValidator();
        expiryValidator.validate(token);
        if (token.claim().subject().equals("verified")) {
            final Principal principal = new Principal() {
                public String getName() {
                    return "verified";
                }
            };
            return Optional.of(principal);
        }
        return Optional.absent();
    
    }
    
}
