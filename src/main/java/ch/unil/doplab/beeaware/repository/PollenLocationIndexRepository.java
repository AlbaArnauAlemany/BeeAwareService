package ch.unil.doplab.beeaware.repository;

import ch.unil.doplab.beeaware.Domain.Location;
import ch.unil.doplab.beeaware.Domain.PollenLocationIndex;
import jakarta.ejb.Stateless;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityTransaction;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.TypedQuery;
import jakarta.transaction.Transactional;

import java.time.LocalDate;
import java.util.Calendar;
import java.util.Date;
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

    public long countPollenToday() {
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.HOUR_OF_DAY, 0);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MILLISECOND, 0);
        Date startOfDay = calendar.getTime();

        // Obtenir la fin de la journée
        calendar.add(Calendar.DATE, 1);
        Date endOfDay = calendar.getTime();

        TypedQuery<Long> query = entityManager.createQuery(
                "SELECT COUNT(l) FROM PollenLocationIndex l WHERE l.date >= :startOfDay AND l.date < :endOfDay",
                Long.class);
        query.setParameter("startOfDay", startOfDay);
        query.setParameter("endOfDay", endOfDay);

        Long pollenLocationIndexSize = query.getSingleResult();
        System.out.println("Today, there are " + pollenLocationIndexSize + " pollen records.");
        return pollenLocationIndexSize;
    }


    @Transactional
    public void deleteById(Long id) {
        PollenLocationIndex pollenLocationIndex = findById(id);
        if (pollenLocationIndex != null) {
            entityManager.remove(pollenLocationIndex);
        }
    }

    public boolean existsByPollenNameAndLocationAndDate(String pollenName, Location location, Date date) {
        TypedQuery<Long> query = entityManager.createQuery(
                "SELECT COUNT(p) FROM PollenLocationIndex p WHERE p.displayName = :pollenName AND p.location = :location AND p.date = :date",
                Long.class);
        query.setParameter("pollenName", pollenName);
        query.setParameter("location", location);
        query.setParameter("date", date);
        Long count = query.getSingleResult();
        return count != null && count > 0;
    }

    @Transactional
    public void updatePollenLocationIndex(PollenLocationIndex pollenLocationIndex) {
        entityManager.merge(pollenLocationIndex);
    }

}
