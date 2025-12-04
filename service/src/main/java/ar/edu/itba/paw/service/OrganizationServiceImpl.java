// DONE

package ar.edu.itba.paw.service;

import ar.edu.itba.paw.interfaces.enums.OrderCriteria;
import ar.edu.itba.paw.interfaces.persistence.EventDao;
import ar.edu.itba.paw.interfaces.persistence.InstitutionDao;
import ar.edu.itba.paw.interfaces.persistence.OpenPositionDao;
import ar.edu.itba.paw.interfaces.persistence.OrganizationDao;
import ar.edu.itba.paw.interfaces.persistence.PostDao;
import ar.edu.itba.paw.interfaces.persistence.SubscriptionDao;
import ar.edu.itba.paw.interfaces.service.OrganizationService;
import ar.edu.itba.paw.interfaces.service.exception.InstitutionNotFoundException;
import ar.edu.itba.paw.interfaces.service.exception.OrganizationEmailExistsException;
import ar.edu.itba.paw.interfaces.service.exception.OrganizationNotFoundException;
import ar.edu.itba.paw.interfaces.service.EmailService;
import ar.edu.itba.paw.model.Event;
import ar.edu.itba.paw.model.Institution;
import ar.edu.itba.paw.model.OpenPosition;
import ar.edu.itba.paw.model.Organization;
import ar.edu.itba.paw.model.Post;
import ar.edu.itba.paw.model.User;
import ar.edu.itba.paw.service.annotation.TransactionalRead;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collections;
import java.util.List;

@RequiredArgsConstructor
@Service
public class OrganizationServiceImpl implements OrganizationService {
    private static final Logger LOGGER = LoggerFactory.getLogger(OrganizationServiceImpl.class);

    private final OrganizationDao orgDao;
    private final PostDao postDao;
    private final EventDao eventDao;
    private final OpenPositionDao openPositionDao;
    private final EmailService emailService;
    private final SubscriptionDao subscriptionDao;
    private final InstitutionDao institutionDao;

    @Override
    @TransactionalRead
    public Organization findByID(final long orgID) {
        return orgDao.findByID(orgID).orElseThrow(() -> new OrganizationNotFoundException(orgID));
    }

    @Override
    @TransactionalRead
    public Organization findByEmail(final String email) {
        return orgDao.findByEmail(email).orElseThrow(() -> new OrganizationNotFoundException(email));
    }

    @Override
    @TransactionalRead
    public List<Organization> findAllForInstitution(final long institutionID, final int pageNum) {
        Institution institution = institutionDao.findByID(institutionID).orElseThrow(() -> new InstitutionNotFoundException(institutionID));
        return orgDao.findAllForInstitution(institution, pageNum);
    }

    @Override
    @TransactionalRead
    public List<User> getPublishers(final long orgID) {
        Organization organization = orgDao.findByID(orgID).orElseThrow(() -> new OrganizationNotFoundException(orgID));
        return orgDao.getPublishers(organization);
    }

    @Override
    @TransactionalRead
    public List<User> getManagers(final long orgID) {
        Organization organization = orgDao.findByID(orgID).orElseThrow(() -> new OrganizationNotFoundException(orgID));
        return orgDao.getManagers(organization);
    }

    @Override
    @TransactionalRead
    public List<User> getSubscribers(final long orgID) {
        Organization organization = orgDao.findByID(orgID).orElseThrow(() -> new OrganizationNotFoundException(orgID));
        return subscriptionDao.getSubscribers(organization);
    }

    @Override
    @Transactional
    public Organization create(
        final String orgEmail,
        final String orgName,
        final String socialLink1,
        final String socialLink2, 
        final String description, 
        final long institutionID
    ) {
        LOGGER.debug("Creating organization '{}' with email '{}' for institution {}.", orgName, orgEmail, institutionID);
        
        if (orgDao.existsEmail(orgEmail)) {
            LOGGER.warn("Organization creation failed: email {} already exists.", orgEmail);
            throw new OrganizationEmailExistsException(orgEmail);
        }
        
        Institution institution = institutionDao.findByID(institutionID).orElseThrow(() -> new InstitutionNotFoundException(institutionID));
        Organization org = orgDao.create(orgEmail, orgName, socialLink1, socialLink2, description, institution);
        emailService.sendCreationEmail(org.getOrgEmail(), org.getOrgName(), org.getOrgID());
        LOGGER.info("Organization '{}' created successfully with ID {}.", orgName, org.getOrgID());
        return org;
    }

    @Override
    @TransactionalRead
    public List<Post> getPostsByOrgID(final long orgID, final int pageNum) {
        Organization organization = orgDao.findByID(orgID).orElseThrow(() -> new OrganizationNotFoundException(orgID));
        return postDao.findAllForOrganizations(Collections.singletonList(organization), OrderCriteria.getDefault(), pageNum);
    }

    @Override
    @TransactionalRead
    public List<Event> getEventsByOrgID(final long orgID, final int pageNum) {
        Organization organization = orgDao.findByID(orgID).orElseThrow(() -> new OrganizationNotFoundException(orgID));
        return eventDao.findAllForOrganizations(Collections.singletonList(organization), OrderCriteria.getDefault(), pageNum);
    }

    @Override
    @TransactionalRead
    public List<OpenPosition> getOpenPositionsByOrgID(final long orgID, final int pageNum) {
        Organization organization = orgDao.findByID(orgID).orElseThrow(() -> new OrganizationNotFoundException(orgID));
        return openPositionDao.findAllForOrganizations(Collections.singletonList(organization), OrderCriteria.getDefault(), pageNum);
    }

    @Override
    @TransactionalRead
    public List<Organization> getAllOrganizations(final int pageNum) {
        return orgDao.findAll(pageNum);
    }

    @Override
    @TransactionalRead
    public boolean existsEmail(final String email) {
        return orgDao.existsEmail(email);
    }

    @Override
    @TransactionalRead
    public long getInstitutionIDByID(final long orgID) {
        Organization organization = orgDao.findByID(orgID).orElseThrow(() -> new OrganizationNotFoundException(orgID));
        return organization.getInstitution().getInstitutionID();
    }
}
