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

@Stateless
public class TokenRepository{
    @PersistenceContext(unitName = "BeeAwarePU")
    private EntityManager entityManager;
    @Transactional
    public void addToken(Token token) {
        entityManager.persist(token);
    }
    public Token findById(Long id) {
        return entityManager.find(Token.class, id);
    }

    public Token findSpecificBeezzer(Long beezzerId) {
        Beezzer beezzer = entityManager.find(Beezzer.class, beezzerId);
        if (beezzer == null) {
            return null;
        }

        TypedQuery<Token> query = entityManager.createQuery("SELECT t FROM Token t WHERE t.beezzer = :beezzer", Token.class);
        query.setParameter("beezzer", beezzer);
        List<Token> tokens = query.getResultList();
        return tokens.isEmpty() ? null : tokens.get(0);
    }


    @Transactional
    public boolean removeToken(Long id) {
        Token token = findById(id);
        if (token == null) {
            return false;
        }
        token = entityManager.merge(token);
        entityManager.remove(token);
        return true;
    }

    public Token findSpecificKey(String key) {
        TypedQuery<Token> query = entityManager.createQuery("SELECT t FROM Token t WHERE t.key=:key", Token.class);
        query.setParameter("key", key);
        return query.getResultList().get(0);
    }

    public List<Token> findAll() {
        TypedQuery<Token> query = entityManager.createQuery("SELECT l FROM Token l", Token.class);
        return query.getResultList();
    }

    @Transactional
    public void deleteById(Long id) {
        EntityTransaction transaction = entityManager.getTransaction();
        try {
            transaction.begin();
            Token token = findById(id);
            if (token != null) {
                entityManager.remove(token);
            }
            transaction.commit();
        } catch (Exception e) {
            if (transaction.isActive()) {
                transaction.rollback();
            }
            throw e;
        }
    }

    @Transactional
    public void updateToken(Token token) {
        EntityTransaction transaction = entityManager.getTransaction();
        try {
            transaction.begin();
            entityManager.merge(token);
            transaction.commit();
        } catch (Exception e) {
            if (transaction.isActive()) {
                transaction.rollback();
            }
            throw e;
        }
    }
}
