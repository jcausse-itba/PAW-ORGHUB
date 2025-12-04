// DONE

package ar.edu.itba.paw.service;

import ar.edu.itba.paw.interfaces.persistence.InstitutionDao;
import ar.edu.itba.paw.interfaces.service.EmailService;
import ar.edu.itba.paw.interfaces.service.InstitutionService;
import ar.edu.itba.paw.interfaces.service.exception.InstitutionAdminEmailAlreadyRegisteredException;
import ar.edu.itba.paw.interfaces.service.exception.InstitutionDomainNameExistsException;
import ar.edu.itba.paw.interfaces.service.exception.InstitutionNotFoundException;
import ar.edu.itba.paw.model.Institution;
import ar.edu.itba.paw.model.Organization;
import ar.edu.itba.paw.model.User;
import ar.edu.itba.paw.service.annotation.TransactionalRead;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@RequiredArgsConstructor
@Service
public class InstitutionServiceImpl implements InstitutionService {
    private static final Logger LOGGER = LoggerFactory.getLogger(InstitutionServiceImpl.class);

    private final InstitutionDao institutionDao;
    private final EmailService emailService;

    @Override
    @TransactionalRead
    public Institution findByID(final long institutionID) {
        return institutionDao.findByID(institutionID).orElseThrow(() -> new InstitutionNotFoundException(institutionID));
    }

    @Override
    @TransactionalRead
    public Institution findByDomainName(final String domainName) {
        return institutionDao.findByDomainName(domainName).orElseThrow(() -> new InstitutionNotFoundException("Institution with domain name \"" + domainName + "\" not found."));
    }

    @Override
    @TransactionalRead
    public Institution findByAdminEmail(final String adminEmail) {
        return institutionDao.findByAdminEmail(adminEmail).orElseThrow(() -> new InstitutionNotFoundException("Institution with admin email \"" + adminEmail + "\" not found."));
    }

    @Override
    @Transactional
    public Institution create(final String name, final String domainName, final String adminEmail) {
        LOGGER.debug("Creating institution with name: {}, domain: {}", name, domainName);
        
        if (institutionDao.existsDomainName(domainName)) {
            LOGGER.warn("Institution creation failed: domain name {} already exists.", domainName);
            throw new InstitutionDomainNameExistsException(domainName);
        }
        
        if (institutionDao.existsAdminEmail(adminEmail)) {
            LOGGER.warn("Institution creation failed: admin email {} already exists.", adminEmail);
            throw new InstitutionAdminEmailAlreadyRegisteredException(adminEmail);
        }
        
        Institution institution = institutionDao.create(name, domainName, adminEmail);
        emailService.sendInstitutionCreationEmail(adminEmail, name, domainName);
        LOGGER.info("Institution created successfully: {} (ID: {}).", name, institution.getInstitutionID());
        return institution;
    }

    @Override
    @TransactionalRead
    public List<Organization> getOrganizationsByID(final long institutionID, final int pageNum) {
        Institution institution = institutionDao.findByID(institutionID).orElseThrow(() -> new InstitutionNotFoundException(institutionID));
        return institutionDao.getOrganizations(institution, pageNum);
    }

    @Override
    @TransactionalRead
    public List<Organization> getOrganizationsByDomainName(final String domainName, final int pageNum) {
        Institution institution = institutionDao.findByDomainName(domainName).orElseThrow(() -> new InstitutionNotFoundException("Institution with domain name \"" + domainName + "\" not found."));
        return institutionDao.getOrganizations(institution, pageNum);
    }

    @Override
    @TransactionalRead
    public User getAdministratorByID(final long institutionID) {
        Institution institution = institutionDao.findByID(institutionID).orElseThrow(() -> new InstitutionNotFoundException(institutionID));
        return institutionDao.getAdministrator(institution);
    }

    @Override
    @TransactionalRead
    public User getAdministratorByDomainName(final String domainName) {
        Institution institution = institutionDao.findByDomainName(domainName).orElseThrow(() -> new InstitutionNotFoundException("Institution with domain name \"" + domainName + "\" not found."));
        return institutionDao.getAdministrator(institution);
    }

    @Override
    @TransactionalRead
    public boolean isAdmin(final User user) {
        Institution institution = user.getInstitution();
        if (institution == null) {
            return false;
        }
        return institution.getAdminEmail().equalsIgnoreCase(user.getUserEmail());
    }

    @Override
    @TransactionalRead
    public List<Institution> getAllInstitutions(final int pageNum) {
        return institutionDao.findAll(pageNum);
    }

    @Override
    @TransactionalRead
    public boolean existsDomainName(final String domainName) {
        return institutionDao.existsDomainName(domainName);
    }
}
