package ch.unil.doplab.beeaware.repository;

import ch.unil.doplab.beeaware.Domain.Location;
import jakarta.ejb.Stateless;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityTransaction;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.TypedQuery;
import jakarta.transaction.Transactional;

import java.util.List;

@Stateless
public class LocationRepository{
    @PersistenceContext(unitName = "BeeAwarePU")
    private EntityManager entityManager;

    @Transactional
    public void addLocation(Location location) {
        entityManager.persist(location);
    }
    public Location findById(Long id) {
        return entityManager.find(Location.class, id);
    }

    public List<Location> findAll() {
        TypedQuery<Location> query = entityManager.createQuery("SELECT l FROM Location l", Location.class);
        return query.getResultList();
    }
    public Location checkLocation(int NPA) {
        TypedQuery<Location> query = entityManager.createQuery("SELECT l FROM Location l WHERE l.NPA=:NPA", Location.class);
        query.setParameter("NPA", NPA);
        List<Location> loc = query.getResultList();
        if (loc != null && loc.size() > 0) {
            return loc.get(0);
        }
        return null;
    }
    @Transactional
    public void deleteById(Long id) {
        EntityTransaction transaction = entityManager.getTransaction();
        try {
            transaction.begin();
            Location location = findById(id);
            if (location != null) {
                entityManager.remove(location);
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
    public void updateLocation(Location location) {
        EntityTransaction transaction = entityManager.getTransaction();
        try {
            transaction.begin();
            entityManager.merge(location);
            transaction.commit();
        } catch (Exception e) {
            if (transaction.isActive()) {
                transaction.rollback();
            }
            throw e;
        }
    }
}
