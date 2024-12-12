package ch.unil.doplab.beeaware.repository;

import ch.unil.doplab.beeaware.Domain.Beezzer;
import ch.unil.doplab.beeaware.Domain.Token;
import jakarta.ejb.Stateless;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityTransaction;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.TypedQuery;
import jakarta.transaction.Transactional;

import java.util.List;
import java.util.logging.Level;

@Stateless
public class TokenRepository{
    @PersistenceContext(unitName = "BeeAwarePU")
    private EntityManager entityManager;
    @Transactional
    public void addToken(Token token) {
        entityManager.persist(token);
    }

    public Token findSpecificBeezzer(Long beezzerId) {
        Beezzer beezzer = entityManager.find(Beezzer.class, beezzerId);
        if (beezzer == null) {
            return null;
        }

        TypedQuery<Token> query = entityManager.createQuery("SELECT t FROM Token t WHERE t.beezzerId = :beezzer", Token.class);
        query.setParameter("beezzer", beezzerId);
        List<Token> tokens = query.getResultList();
        return tokens.isEmpty() ? null : tokens.get(0);
    }


    @Transactional
    public boolean removeToken(Token token) {
        if (token == null) {
            return false;
        }
        if (!entityManager.contains(token)) {
            token = entityManager.merge(token); // Attachez l'entité
        }
        entityManager.remove(token);
        return true;
    }

    public Token findSpecificKey(String key) {
        TypedQuery<Token> query = entityManager.createQuery("SELECT t FROM Token t WHERE t.key=:key", Token.class);
        query.setParameter("key", key);
        return query.getResultList().get(0);
    }

    public Token findTokenForABeezzer(Long id) {
        TypedQuery<Token> query = entityManager.createQuery("SELECT t FROM Token t WHERE t.beezzerId=:id", Token.class);
        query.setParameter("id", id);
        return query.getResultList().get(0);
    }

    public List<Token> findAll() {
        TypedQuery<Token> query = entityManager.createQuery("SELECT l FROM Token l", Token.class);
        return query.getResultList();
    }

    @Transactional
    public void updateToken(Token token) {
        entityManager.merge(token);
    }
}
