package ar.edu.itba.paw.interfaces.persistence;

import ar.edu.itba.paw.model.Organization;
import ar.edu.itba.paw.model.User;
import java.util.List;

public interface SubscriptionDao {
    /**
     * Check if a user is subscribed to an Organization.
     * @param user      The User to check. Must not be null.
     * @param org       The Organization to check. Must not be null.
     * @return          Boolean indicating whether the user is subscribed to the organization.
     */
    boolean isSubscribed(User user, Organization org);

    /**
     * Create a subscription relationship between a User and an Organization.
     * @param user      The User subscribing. Must not be null.
     * @param org       The Organization being subscribed to. Must not be null.
     */
    void addSubscription(User user, Organization org);

    /**
     * Remove a subscription relationship between a User and an Organization.
     * @param user      The User unsubscribing. Must not be null.
     * @param org       The Organization being unsubscribed from. Must not be null.
     */
    void removeSubscription(User user, Organization org);

    /**
     * Get all organizations a user is subscribed to.
     * @param user      The User. Must not be null.
     * @return          A List of Organizations (that might be empty).
     */
    List<Organization> getSubscribedOrganizations(User user);

    /**
     * Get all users subscribed to an organization.
     * @param org       The Organization. Must not be null.
     * @return          A List of User objects (that might be empty).
     */
    List<User> getSubscribers(Organization org);
}
