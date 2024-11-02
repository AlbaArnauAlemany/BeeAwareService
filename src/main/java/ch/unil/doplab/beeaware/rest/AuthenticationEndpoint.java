package ch.unil.doplab.beeaware.rest;

import ch.unil.doplab.beeaware.Domain.Beezzer;
import ch.unil.doplab.beeaware.domain.ApplicationState;
import jakarta.inject.Inject;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

import java.math.BigInteger;
import java.security.SecureRandom;
import java.util.Map;
import java.util.Random;
import java.util.logging.Level;
import java.util.logging.Logger;

import static ch.unil.doplab.beeaware.Domain.PasswordUtilis.checkPassword;

@Path("/authentication")
public class AuthenticationEndpoint {
    @Inject
    private ApplicationState state;
    @POST
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_FORM_URLENCODED)
    public Response authenticateUser(@FormParam("username") String username,
                                     @FormParam("password") String password) {

        try {

            // Authenticate the user using the credentials provided
            authenticate(username, password);

            // Issue a token for the user
            String token = issueToken(username);

            // Return the token on the response
            return Response.ok(token).build();

        } catch (Exception e) {
            logger.log( Level.SEVERE, e.getMessage());
            return Response.status(Response.Status.FORBIDDEN).build();
        }
    }

    private Logger logger = Logger.getLogger(AuthenticationEndpoint.class.getName());

    private void authenticate(String username, String password) throws Exception {
        for (Map.Entry<Long, Beezzer> bee: state.getBeezzers().entrySet()) {
            logger.log( Level.SEVERE, username);
            logger.log( Level.SEVERE, password);
            logger.log( Level.SEVERE, bee.getValue().getUsername());
            logger.log( Level.SEVERE, bee.getValue().getPassword());
            if (username != null && password != null &&
                    username.equals(bee.getValue().getUsername()) &&
                    checkPassword(password, bee.getValue().getPassword())) {
                logger.log(Level.FINE, "User {0} successfully connected", username);
                return;
            }
        }
        logger.log( Level.SEVERE, "Error during authentication");
        throw new Exception("No match with user or password");
    }

    private String issueToken(String username) {
        Random random = new SecureRandom();
        String token = new BigInteger(130, random).toString(32);
        return token;
    }
}