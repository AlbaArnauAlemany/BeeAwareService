package ch.unil.doplab.beeaware.repository;

import ch.unil.doplab.beeaware.Domain.Beezzer;
import ch.unil.doplab.beeaware.Domain.Pollen;
import ch.unil.doplab.beeaware.Domain.Role;
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
    private EntityManager entityManager;


    @Transactional
    public void addBeezzer(Beezzer beezzer) {
        entityManager.persist(beezzer);
    }
    public Beezzer findById(Long id) {
        return entityManager.find(Beezzer.class, id);
    }

    public Beezzer findByUsername(String username) {
        TypedQuery<Beezzer> query = entityManager.createQuery("SELECT b FROM Beezzer b WHERE b.username=:username", Beezzer.class);
        query.setParameter("username", username);
        List<Beezzer> bee = query.getResultList();
        if(bee != null && bee.size() > 0){
            return bee.get(0);
        }
        return null;
    }

    public Long countBeezzerRole() {
        TypedQuery<Long> query = entityManager.createQuery(
                "SELECT COUNT(b) FROM Beezzer b WHERE b.role = :role", Long.class);
        query.setParameter("role", Role.BEEZZER);
        Long count = query.getSingleResult();
        return count != null ? count.longValue() : 0L;
    }


    public List<Pollen> findPollensForBeezzer(Long beezzerId) {
        TypedQuery<Pollen> query = entityManager.createQuery(
                "SELECT p FROM Beezzer b JOIN b.allergens p WHERE b.id = :beezzerId",
                Pollen.class
        );
        query.setParameter("beezzerId", beezzerId);
        return query.getResultList();
    }


    public List<Beezzer> findAll() {
        TypedQuery<Beezzer> query = entityManager.createQuery("SELECT b FROM Beezzer b", Beezzer.class);
        return query.getResultList();
    }

    @Transactional
    public void deleteById(Long id) {
        Beezzer beezzer = findById(id);
        if (beezzer != null) {
            entityManager.remove(beezzer);
        }
    }

    @Transactional
    public void updateBeezzer(Beezzer beezzer) {
            entityManager.merge(beezzer);
    }
}
