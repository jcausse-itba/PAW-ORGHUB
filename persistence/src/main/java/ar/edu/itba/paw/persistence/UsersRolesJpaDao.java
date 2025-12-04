package ar.edu.itba.paw.persistence;

import ar.edu.itba.paw.interfaces.persistence.UsersRolesDao;
import ar.edu.itba.paw.model.enums.Role;
import ar.edu.itba.paw.model.Organization;
import ar.edu.itba.paw.model.User;
import ar.edu.itba.paw.model.UsersRoles;
import ar.edu.itba.paw.model.UsersRolesCompositeID;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.util.Optional;

@RequiredArgsConstructor
@Repository
public class UsersRolesJpaDao implements UsersRolesDao {
    @PersistenceContext private EntityManager em;

    @Override
    public Role getUserRole(User user, Organization org) {
        return Optional.ofNullable(em.find(UsersRoles.class, new UsersRolesCompositeID(user, org)))
                .map(UsersRoles::getRole).orElse(Role.USER);
    }

    @Override
    public void assignUserRole(User user, Organization org, Role role) {
        if (role == Role.USER) {                        // If role is Role.USER, delete existing role
            deleteUserRole(user, org);
            return;
        }
        final UsersRoles existing = em.find(UsersRoles.class, new UsersRolesCompositeID(user, org));
        if (existing != null) {                         // If there is an existing role, overwrite it
            existing.setRole(role);
            return;
        }
        em.persist(new UsersRoles(user, org, role));    // If there is no existing role, create one
    }

    @Override
    public void deleteUserRole(User user, Organization org) {
        Optional.ofNullable(em.find(UsersRoles.class, new UsersRolesCompositeID(user, org))).ifPresent(em::remove);
    }
}
