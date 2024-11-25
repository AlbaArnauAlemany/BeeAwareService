package ch.unil.doplab.beeaware.Utilis;

import ch.unil.doplab.beeaware.Domain.Beezzer;
import ch.unil.doplab.beeaware.domain.ApplicationState;
import ch.unil.doplab.beeaware.Domain.Token;
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
//https://stackoverflow.com/questions/26777083/how-to-implement-rest-token-based-authentication-with-jax-rs-and-jersey
@Path("/authentication")
public class AuthenticationEndpoint {
    @Inject
    private ApplicationState state;
    private final Logger logger = Logger.getLogger(AuthenticationEndpoint.class.getName());

    @POST
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_FORM_URLENCODED)
    public Response authenticateUser(@FormParam("username") String username, @FormParam("password") String password) {
        try {
            Long userId = authenticate(username, password);
            Token token = issueToken(userId);

            return Response.ok(token).build();

        } catch (Exception e) {
            logger.log(Level.SEVERE, e.getMessage());
            return Response.status(Response.Status.FORBIDDEN).build();
        }
    }

    private Long authenticate(String username, String password) throws Exception {
        logger.log(Level.INFO, "Beezzer {0} trying to authenticate", username);
        for (Map.Entry<Long, Beezzer> bee : state.getBeezzerService().getBeezzers().entrySet()) {
            if (username != null && password != null && username.equals(bee.getValue().getUsername()) && checkPassword(password, bee.getValue().getPassword())) {
                logger.log(Level.INFO, "Beezzer {0} successfully connected", username);
                return bee.getValue().getId();
            }
        }
        logger.log(Level.WARNING, "Error during authentication");
        throw new Exception("No match with user or password");
    }

    private Token isTokenAlreadyExistForSpecificBeezzer(Long beezzerId){
        for (Map.Entry<Long, Token> tok : state.getTokenService().getTokens().entrySet()) {
            if (tok.getValue().getBeezzerId() == beezzerId && state.getTokenService().isDateValide(tok.getValue().getExpiration())) {
                logger.log(Level.WARNING, "Token for beezzer {0} already exists", tok.getValue().getBeezzerId());
                return tok.getValue();
            }
        }
        return null;
    }

    private Token issueToken(Long beezzerId) throws Exception {
        Token oldToken = isTokenAlreadyExistForSpecificBeezzer(beezzerId);
        if(oldToken == null) {
            Random random = new SecureRandom();
            String tokenString = new BigInteger(130, random).toString(32);
            Date now = new Date();
            Calendar calendar = Calendar.getInstance();
            calendar.setTime(now);
            calendar.add(Calendar.HOUR, 2);
            Date plusOneHour = calendar.getTime();
            Token token = new Token(tokenString, plusOneHour, beezzerId, state.getBeezzerService().getBeezzers().get(beezzerId).getRole());
            state.getTokenService().addToken(token);
            return token;
        } else {
            return oldToken;
        }
    }

    @POST
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    @Secured
    @SameID
    @Path("/{id}")
    public boolean logout(@PathParam("id") Long id) {
        Long idToken = null;
        for (Map.Entry<Long, Token> tok : state.getTokenService().getTokens().entrySet()) {
            if (tok.getValue().getBeezzerId() == id ) {
                idToken = tok.getKey();
                logger.log(Level.WARNING, "Token for beezzer {0} already exists", tok.getValue().getBeezzerId());
                break;
            }
        }
        if (idToken != null){
            state.getTokenService().getTokens().remove(idToken);
            return true;
        }
        return false;
    }
}