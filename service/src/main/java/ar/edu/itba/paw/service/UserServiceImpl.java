// DONE

package ar.edu.itba.paw.service;

import ar.edu.itba.paw.interfaces.persistence.*;
import ar.edu.itba.paw.interfaces.service.EmailService;
import ar.edu.itba.paw.interfaces.service.PermissionService;
import ar.edu.itba.paw.interfaces.service.UserService;
import ar.edu.itba.paw.interfaces.service.exception.InstitutionNotFoundException;
import ar.edu.itba.paw.interfaces.service.exception.OrganizationNotFoundException;
import ar.edu.itba.paw.interfaces.service.exception.UserEmailExistsException;
import ar.edu.itba.paw.interfaces.service.exception.UserNotFoundException;
import ar.edu.itba.paw.model.Institution;
import ar.edu.itba.paw.model.Organigram;
import ar.edu.itba.paw.model.Organization;
import ar.edu.itba.paw.model.Post;
import ar.edu.itba.paw.model.User;
import ar.edu.itba.paw.model.image.Image;
import ar.edu.itba.paw.model.enums.Language;
import ar.edu.itba.paw.model.enums.Role;
import ar.edu.itba.paw.service.annotation.TransactionalRead;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@RequiredArgsConstructor
@Service
public class UserServiceImpl implements UserService {
    private static final Logger LOGGER = LoggerFactory.getLogger(UserServiceImpl.class);

    private final PasswordEncoder passwordEncoder;

    private final UserDao userDao;
    private final SubscriptionDao subscriptionDao;
    private final InstitutionDao institutionDao;
    private final UsersRolesDao usersRolesDao;
    private final OrganigramDao organigramDao;
    private final OrganizationDao organizationDao;
    private final ImageDao imageDao;
    private final LikeDao likeDao;

    private final EmailService emailService;

    @Override
    @TransactionalRead
    public User findByID(final long userID) {
        Optional<User> maybeUser = userID == PermissionService.SYSADMIN_USER_ID
                ? userDao.findWithoutInstitution(userID)
                : userDao.findByID(userID);
        return maybeUser.orElseThrow(() -> new UserNotFoundException(userID));
    }

    @Override
    @TransactionalRead
    public User findByEmail(final String email) {
        Optional<User> maybeUser = email.equals(PermissionService.SYSADMIN_EMAIL)
                ? userDao.findWithoutInstitution(email)
                : userDao.findByEmail(email);
        return maybeUser.orElseThrow(() -> new UserNotFoundException(email));
    }

    @Override
    @Transactional
    public User create(
            final String userEmail,
            final String firstName,
            final String lastName,
            final String password,
            final Language preferredLanguage
    ) {
        LOGGER.debug("Creating user with email: {}", userEmail);
        
        if (userDao.existsEmail(userEmail)) {
            LOGGER.warn("User creation failed: email {} already exists.", userEmail);
            throw new UserEmailExistsException(userEmail);
        }
        
        final String domainName = userEmail.strip().split("@")[1];
        final Institution institution = institutionDao.findByDomainName(domainName).orElseThrow(() -> {
            LOGGER.error("User creation failed: institution not found for domain {}.", domainName);
            return new InstitutionNotFoundException(domainName);
        });
        
        User user = userDao.create(userEmail, firstName, lastName, passwordEncoder.encode(password), preferredLanguage, institution);
        LOGGER.info("User created successfully: {} (ID: {}).", userEmail, user.getUserID());
        return user;
    }

    @Override
    @Transactional
    public User create(
            final String userEmail,
            final String firstName,
            final String lastName,
            final String password
    ) {
        return create(userEmail, firstName, lastName, password, Language.getDefault());
    }

    @Override
    @TransactionalRead
    public boolean isSubscribed(final long userID, final long orgID) {
        User user = userDao.findByID(userID).orElseThrow(() -> new UserNotFoundException(userID));
        Organization organization = organizationDao.findByID(orgID).orElseThrow(() -> new OrganizationNotFoundException(orgID));
        return subscriptionDao.isSubscribed(user, organization);
    }

    @Override
    @Transactional
    public void addSubscription(final long userID, final long orgID) {
        LOGGER.debug("Adding subscription for user {} to organization {}.", userID, orgID);
        
        User user = userDao.findByID(userID).orElseThrow(() -> new UserNotFoundException(userID));
        Organization organization = organizationDao.findByID(orgID).orElseThrow(() -> new OrganizationNotFoundException(orgID));
        
        if (!subscriptionDao.isSubscribed(user, organization)) {
            subscriptionDao.addSubscription(user, organization);
            emailService.sendSubscriptionNotificationEmail(organization.getOrgEmail(), user.getUserEmail(), organization.getOrgName(), orgID);
            LOGGER.info("Subscription added: user {} subscribed to organization {}.", user.getUserEmail(), organization.getOrgName());
        } else {
            LOGGER.debug("User {} already subscribed to organization {}.", userID, orgID);
        }
    }

    @Override
    @Transactional
    public void removeSubscription(final long userID, final long orgID) {
        LOGGER.debug("Removing subscription for user {} from organization {}.", userID, orgID);
        
        User user = userDao.findByID(userID).orElseThrow(() -> new UserNotFoundException(userID));
        Organization organization = organizationDao.findByID(orgID).orElseThrow(() -> new OrganizationNotFoundException(orgID));
        
        if (subscriptionDao.isSubscribed(user, organization)) {
            subscriptionDao.removeSubscription(user, organization);
            LOGGER.info("Subscription removed: user {} unsubscribed from organization {}.", userID, orgID);
        } else {
            LOGGER.debug("User {} was not subscribed to organization {}.", userID, orgID);
        }
    }

    @Override
    @TransactionalRead
    public List<Organization> getSubscribedOrganizations(final long userID) {
        User user = userDao.findByID(userID).orElseThrow(() -> new UserNotFoundException(userID));
        return subscriptionDao.getSubscribedOrganizations(user);
    }

    @Override
    @TransactionalRead
    public Role getRoleForOrganizationByID(final long userID, final long orgID) {
        User user = userDao.findByID(userID).orElseThrow(() -> new UserNotFoundException(userID));
        Organization organization = organizationDao.findByID(orgID).orElseThrow(() -> new OrganizationNotFoundException(orgID));
        return usersRolesDao.getUserRole(user, organization);
    }

    @Override
    @Transactional
    public void assignRoleForOrganizationByID(final long userID, final long orgID, final Role role) {
        if (role.equals(Role.USER)) {
            LOGGER.debug("Assigning role {} removes previous role (as role {} has not special permissions).", Role.USER.getRoleName(), Role.USER.getRoleName());
            deleteRoleForOrganizationByID(userID, orgID);
            return;
        }

        LOGGER.debug("Assigning role {} to user {} for organization {}.", role.getRoleName(), userID, orgID);
        User user = userDao.findByID(userID).orElseThrow(() -> new UserNotFoundException(userID));
        Organization organization = organizationDao.findByID(orgID).orElseThrow(() -> new OrganizationNotFoundException(orgID));
        usersRolesDao.assignUserRole(user, organization, role);
        LOGGER.info("Role assigned: user {} now has role {} in organization {}.", userID, role.getRoleName(), orgID);
        
        emailService.sendAddedRoleEmail(
            user.getUserEmail(),
            organization.getOrgName(),
            role.equals(Role.MANAGER),
            orgID
        );
    }

    @Override
    @Transactional
    public void deleteRoleForOrganizationByID(final long userID, final long orgID) {
        LOGGER.debug("Deleting role for user {} from organization {}.", userID, orgID);
        User user = userDao.findByID(userID).orElseThrow(() -> new UserNotFoundException(userID));
        Organization organization = organizationDao.findByID(orgID).orElseThrow(() -> new OrganizationNotFoundException(orgID));
        usersRolesDao.deleteUserRole(user, organization);
    }

    @Override
    @Transactional
    public void updatePreferredLanguage(final long userID, final Language language) {
        LOGGER.debug("Updating preferred language for user {} to {}.", userID, language);
        User user = userDao.findByID(userID).orElseThrow(() -> new UserNotFoundException(userID));
        userDao.setPreferredLanguage(user, language);
        LOGGER.info("Preferred language updated for user {} to {}.", userID, language);
    }

    @Override
    @TransactionalRead
    public boolean existsEmail(final String email) {
        return userDao.existsEmail(email);
    }

    @Override
    @TransactionalRead
    public List<Organigram> getUserPositions(final long userID) {
        User user = userDao.findByID(userID).orElseThrow(() -> new UserNotFoundException(userID));
        return organigramDao.findByUser(user);
    }

    @Override
    @TransactionalRead
    public List<Organigram> getUserPositionsInOrganization(final long userID, final long orgID) {
        User user = userDao.findByID(userID).orElseThrow(() -> new UserNotFoundException(userID));
        Organization organization = organizationDao.findByID(orgID).orElseThrow(() -> new OrganizationNotFoundException(orgID));
        return organigramDao.findByUserAndOrganization(user, organization);
    }

    @Override
    @TransactionalRead
    public List<Post> findLikedPostsByUser(final long userID) {
        User user = userDao.findByID(userID).orElseThrow(() -> new UserNotFoundException(userID));
        return likeDao.findLikedPostsByUser(user);
    }

    @Override
    @TransactionalRead
    public boolean canUserAccessImage(final User user, final Image image) {
        if (user == null || image == null) {
            return false;
        }
        return imageDao.canUserAccessImage(user, image);
    }
}
