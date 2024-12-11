package ch.unil.doplab.beeaware.repository;

import ch.unil.doplab.beeaware.Domain.Symptom;
import jakarta.ejb.Stateless;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityTransaction;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.TypedQuery;
import jakarta.transaction.Transactional;
import lombok.NoArgsConstructor;

import java.util.List;

@Stateless
public class SymptomRepository{
    @PersistenceContext(unitName = "BeeAwarePU")
    private EntityManager entityManager;
    @Transactional
    public void addSymptom(Symptom symptom) {
        entityManager.persist(symptom);
    }
    public Symptom findById(Long id) {
        return entityManager.find(Symptom.class, id);
    }

    public List<Symptom> findAll() {
        TypedQuery<Symptom> query = entityManager.createQuery("SELECT l FROM Symptom l", Symptom.class);
        return query.getResultList();
    }

    @Transactional
    public void deleteById(Long id) {
        EntityTransaction transaction = entityManager.getTransaction();
        try {
            transaction.begin();
            Symptom symptom = findById(id);
            if (symptom != null) {
                entityManager.remove(symptom);
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
    public void updateSymptom(Symptom symptom) {
        EntityTransaction transaction = entityManager.getTransaction();
        try {
            transaction.begin();
            entityManager.merge(symptom);
            transaction.commit();
        } catch (Exception e) {
            if (transaction.isActive()) {
                transaction.rollback();
            }
            throw e;
        }
    }
}
