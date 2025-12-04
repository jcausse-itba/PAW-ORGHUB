// DONE

package ar.edu.itba.paw.interfaces.persistence;

import ar.edu.itba.paw.model.Organization;
import ar.edu.itba.paw.model.User;
import ar.edu.itba.paw.model.enums.Role;

public interface UsersRolesDao {
    /**
     * Get the Role that a User has within an Organization. If the User has no special Role for that Organization,
     * {@code Role.USER} is returned.
     * @param user              The User.
     * @param org               The Organization.
     * @return                  The requested Role.
     */
    Role getUserRole(User user, Organization org);

    /**
     * Assign a Role for a (User, Organization) pair. Assigning a new Role will overwrite any existing Role. Assigning
     * {@code Role.USER} will delete any other Role, as {@code Role.USER} has no special permissions.
     * @param user              The User.
     * @param org               The Organization.
     * @param role              The Role to assign.
     */
    void assignUserRole(User user, Organization org, Role role);

    /**
     * Delete any role that a User might have within an Organization. If that User has no Role in that Organization,
     * no operation is performed.
     * @param user              The User.
     * @param org               The Organization.
     */
    void deleteUserRole(User user, Organization org);
}
