package ar.edu.itba.paw.persistence;

import ar.edu.itba.paw.interfaces.persistence.SubscriptionDao;
import ar.edu.itba.paw.model.Organization;
import ar.edu.itba.paw.model.manytomany.Subscription;
import ar.edu.itba.paw.model.manytomany.SubscriptionCompositeID;
import ar.edu.itba.paw.model.User;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.util.List;
import java.util.Optional;

@Repository
public class SubscriptionJpaDao implements SubscriptionDao {
    @PersistenceContext private EntityManager em;

    @Override
    public boolean isSubscribed(final User user, final Organization org) {
        return em.find(Subscription.class, new SubscriptionCompositeID(user, org)) != null;
    }

    @Override
    public void addSubscription(final User user, final Organization org) {
        em.persist(new Subscription(user, org));
    }

    @Override
    public void removeSubscription(final User user, final Organization org) {
        Optional.ofNullable(em.find(Subscription.class, new SubscriptionCompositeID(user, org))).ifPresent(em::remove);
    }

    @Override
    public List<Organization> getSubscribedOrganizations(final User user) {
        final String hql = "SELECT s.organization FROM Subscription s WHERE s.user = :user";
        return em.createQuery(hql, Organization.class).setParameter("user", user).getResultList();
    }

    @Override
    public List<User> getSubscribers(final Organization org) {
        final String hql = "SELECT s.user FROM Subscription s WHERE s.organization = :org";
        return em.createQuery(hql, User.class).setParameter("org", org).getResultList();
    }
}
