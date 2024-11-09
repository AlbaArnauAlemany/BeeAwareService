package ch.unil.doplab.beeaware.service;

import ch.unil.doplab.beeaware.domain.Token;
import lombok.Getter;
import lombok.Setter;
import org.jetbrains.annotations.NotNull;

import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

@Getter
@Setter
public class TokenService {
    private Long tokenId = 0L;
    private Map<Long, Token> tokens = new HashMap<>();
    private Logger logger = Logger.getLogger(LocationService.class.getName());

    public void addToken(@NotNull Token token) {


        logger.log( Level.INFO, "Adding token...");
        for (Map.Entry<Long, Token> tok: tokens.entrySet()) {
            if (tok.getValue().getBeezzerId() == token.getBeezzerId()) {
                logger.log(Level.WARNING, "Token for beezzer {0} already exists", tok.getValue().getBeezzerId());
                return;
            }
        }
        tokens.put(tokenId++, token);
        logger.log(Level.INFO, "New token added : {0}", token);
        logger.log(Level.INFO, "Token size : {0}", tokens.entrySet().size());
    }

    public boolean isDateValide(Token token){
        logger.log(Level.WARNING, "Token isn't valid");
        return token.getExpiration().before(new Date());
    }

    public boolean isAuthorizedToAccess(String token){
        logger.log(Level.WARNING, "Token size : {0}", tokens.entrySet().size());

        logger.log(Level.WARNING, "Start with TokenString : {0}", token);
        for (Map.Entry<Long, Token> tok: tokens.entrySet()) {
            logger.log(Level.WARNING, "TokenString : {0}", token);
            logger.log(Level.WARNING, "Saved token : {0}", tok.getValue().getKey());
            if(tok.getValue().getKey().equals(token)){
                logger.log(Level.WARNING, "Valid until : {0}", tok.getValue().getExpiration());
                if(isDateValide(tok.getValue())){
                    return true;
                }
            }
        }
        logger.log(Level.WARNING, "No valid token for given token");
        return false;
    }
}