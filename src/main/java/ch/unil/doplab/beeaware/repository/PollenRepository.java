package ch.unil.doplab.beeaware.repository;

import ch.unil.doplab.beeaware.Domain.Pollen;
import ch.unil.doplab.beeaware.Domain.Symptom;
import jakarta.ejb.Stateless;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityTransaction;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.TypedQuery;
import jakarta.transaction.Transactional;

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
        Pollen pollen = findById(id);
        if (pollen != null) {
            entityManager.remove(pollen);
        }
    }

    public Pollen findPollenByName(String name) {
        TypedQuery<Pollen> query = entityManager.createQuery("SELECT p FROM Pollen p WHERE p.pollenNameEN LIKE :name", Pollen.class);
        query.setParameter("name", name);
        List<Pollen> pollens = query.getResultList();
        return !pollens.isEmpty() ? pollens.get(0) : null;
    }


    @Transactional
    public void updatePollen(Pollen pollen) {
        entityManager.merge(pollen);
    }
}
