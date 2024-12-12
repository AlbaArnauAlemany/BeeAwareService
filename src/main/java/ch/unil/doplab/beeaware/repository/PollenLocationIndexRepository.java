package ch.unil.doplab.beeaware.repository;

import ch.unil.doplab.beeaware.Domain.PollenLocationIndex;
import jakarta.ejb.Stateless;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityTransaction;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.TypedQuery;
import jakarta.transaction.Transactional;

import java.util.List;

@Stateless
public class PollenLocationIndexRepository{
    @PersistenceContext(unitName = "BeeAwarePU")
    private EntityManager entityManager;

    @Transactional
    public void addPollenLocationIndex(PollenLocationIndex pollenLocationIndex) {
        entityManager.persist(pollenLocationIndex);
    }
    public PollenLocationIndex findById(Long id) {
        return entityManager.find(PollenLocationIndex.class, id);
    }

    public List<PollenLocationIndex> findAll() {
        TypedQuery<PollenLocationIndex> query = entityManager.createQuery("SELECT l FROM PollenLocationIndex l", PollenLocationIndex.class);
        return query.getResultList();
    }

    @Transactional
    public void deleteById(Long id) {
        PollenLocationIndex pollenLocationIndex = findById(id);
        if (pollenLocationIndex != null) {
            entityManager.remove(pollenLocationIndex);
        }
    }

    @Transactional
    public void updatePollenLocationIndex(PollenLocationIndex pollenLocationIndex) {
        entityManager.merge(pollenLocationIndex);
    }

}
