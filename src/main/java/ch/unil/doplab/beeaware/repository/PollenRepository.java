package ch.unil.doplab.beeaware.repository;

import ch.unil.doplab.beeaware.Domain.Pollen;
import jakarta.ejb.Stateless;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityTransaction;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.TypedQuery;
import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;

import java.util.List;

@ApplicationScoped
public class PollenRepository{

    @PersistenceContext(unitName = "BeeAwarePU")
    private EntityManager entityManager;


    @Transactional
    public void addPollen(Pollen pollen) {
        entityManager.persist(pollen);
    }
    public Pollen findById(Long id) {
        return entityManager.find(Pollen.class, id);
    }

    public List<Pollen> findAll() {
        TypedQuery<Pollen> query = entityManager.createQuery("SELECT l FROM Pollen l", Pollen.class);
        return query.getResultList();
    }

    @Transactional
    public void deleteById(Long id) {
        EntityTransaction transaction = entityManager.getTransaction();
        try {
            transaction.begin();
            Pollen pollen = findById(id);
            if (pollen != null) {
                entityManager.remove(pollen);
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
    public void updatePollen(Pollen pollen) {
        EntityTransaction transaction = entityManager.getTransaction();
        try {
            transaction.begin();
            entityManager.merge(pollen);
            transaction.commit();
        } catch (Exception e) {
            if (transaction.isActive()) {
                transaction.rollback();
            }
            throw e;
        }
    }
}
