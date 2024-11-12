package ch.unil.doplab.beeaware.rest;

import ch.unil.doplab.beeaware.Domain.Beezzer;
import ch.unil.doplab.beeaware.domain.ApplicationState;
import ch.unil.doplab.beeaware.domain.Token;
import jakarta.inject.Inject;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

import java.math.BigInteger;
import java.security.SecureRandom;
import java.util.Calendar;
import java.util.Date;
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
            Long userId = authenticate(username, password);

            // Issue a token for the user
            String token = issueToken(userId);

            // Return the token on the response
            return Response.ok(token).build();

        } catch (Exception e) {
            logger.log( Level.SEVERE, e.getMessage());
            return Response.status(Response.Status.FORBIDDEN).build();
        }
    }

    private Logger logger = Logger.getLogger(AuthenticationEndpoint.class.getName());

    private Long authenticate(String username, String password) throws Exception {
        logger.log(Level.SEVERE, "Beezzer {0} trying to authenticate", username);
        for (Map.Entry<Long, Beezzer> bee: state.getBeezzerService().getBeezzers().entrySet()) {
            if (username != null && password != null &&
                    username.equals(bee.getValue().getUsername()) &&
                    checkPassword(password, bee.getValue().getPassword())) {
                logger.log(Level.INFO, "Beezzer {0} successfully connected", username);
                return bee.getValue().getId();
            }
        }
        logger.log( Level.SEVERE, "Error during authentication");
        throw new Exception("No match with user or password");
    }

    private String issueToken(Long beezzerId) {
        Random random = new SecureRandom();
        String tokenString = new BigInteger(130, random).toString(32);
        Date now = new Date();
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(now);
        calendar.add(Calendar.HOUR, 2);
        Date plusOneHour = calendar.getTime();
        Token token = new Token(tokenString, plusOneHour, beezzerId, state.getBeezzerService().getBeezzers().get(beezzerId).getRole());
        state.getTokenService().addToken(token);
        return tokenString;
    }
}