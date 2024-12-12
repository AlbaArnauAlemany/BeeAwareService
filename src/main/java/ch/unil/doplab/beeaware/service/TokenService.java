package ch.unil.doplab.beeaware.service;

import ch.unil.doplab.beeaware.Domain.Beezzer;
import ch.unil.doplab.beeaware.Domain.Token;
import ch.unil.doplab.beeaware.repository.TokenRepository;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import lombok.*;
import org.jetbrains.annotations.NotNull;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

@Data
@ApplicationScoped
public class TokenService {
    @Inject
    private TokenRepository tokenRepository;
    @Inject
    private BeezzerService beezzerService;


    public TokenService() {}
    private Logger logger = Logger.getLogger(LocationService.class.getName());

    public boolean isDateValide(Token token) {
        return token.getExpiration().after(new Date());
    }

    public boolean isDateValide(Date date) {
        return date.after(new Date());
    }

    public TokenService(TokenRepository tokenRepository) {
        this.tokenRepository = tokenRepository;
    }

    public boolean removeToken(Long id){
        if (id == 0) {
            System.out.println("No id");
            return false;
        }
        System.out.println("id : " + id);
        Beezzer beezzer = beezzerService.getBeezzerIfExist(id);
        if(beezzer == null){
            System.out.println("No beezzer");
            return false;
        }
        System.out.println("id : " + id);
        Token token = tokenRepository.findTokenForABeezzer(id);
        if (token == null) {
            System.out.println("No token");
            return false;
        }
        System.out.println("token : " + token);
        return tokenRepository.removeToken(token);
    }
    public void addToken(Token token){
        tokenRepository.addToken(token);
    }
    public boolean isAuthorizedToAccess(String token) {
        Token tok = tokenRepository.findSpecificKey(token);
        if(tokenRepository.findSpecificKey(token) != null){
            if (isDateValide(tok.getExpiration())) {
                return true;
            }
        }
        logger.log(Level.WARNING, "No valid token for given token");
        return false;
    }
}
