package ch.unil.doplab.beeaware.repository;

import ch.unil.doplab.beeaware.Domain.Beezzer;
import jakarta.ejb.Stateless;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityTransaction;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.TypedQuery;
import jakarta.transaction.Transactional;
import lombok.NoArgsConstructor;

import java.util.List;

@Stateless
public class BeezzerRepository{
    @PersistenceContext(unitName = "BeeAwarePU")
    private final EntityManager entityManager;
    public BeezzerRepository(EntityManager entityManager) {
        this.entityManager = entityManager;
    }


    @Transactional
    public void addBeezzer(Beezzer beezzer) {
        EntityTransaction transaction = entityManager.getTransaction();
        try {
            transaction.begin();
            entityManager.persist(beezzer);
            transaction.commit();
        } catch (Exception e) {
            if (transaction.isActive()) {
                transaction.rollback();
            }
            throw e; // Repropager l'exception pour gestion
        }
    }
    public Beezzer findById(Long id) {
        return entityManager.find(Beezzer.class, id);
    }

    public Beezzer findByUsername(String username) {
        TypedQuery<Beezzer> query = entityManager.createQuery("SELECT b FROM Beezzer b WHERE b.username=:username", Beezzer.class);
        return query.getResultList().get(0);
    }

    public List<Beezzer> findAll() {
        TypedQuery<Beezzer> query = entityManager.createQuery("SELECT b FROM Beezzer b", Beezzer.class);
        return query.getResultList();
    }

    @Transactional
    public void deleteById(Long id) {
        EntityTransaction transaction = entityManager.getTransaction();
        try {
            transaction.begin();
            Beezzer beezzer = findById(id);
            if (beezzer != null) {
                entityManager.remove(beezzer);
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
    public void updateBeezzer(Beezzer beezzer) {
        EntityTransaction transaction = entityManager.getTransaction();
        try {
            transaction.begin();
            entityManager.merge(beezzer);
            transaction.commit();
        } catch (Exception e) {
            if (transaction.isActive()) {
                transaction.rollback();
            }
            throw e;
        }
    }
}
