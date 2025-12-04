// DONE

package ar.edu.itba.paw.service;

import ar.edu.itba.paw.interfaces.persistence.OpenPositionDao;
import ar.edu.itba.paw.interfaces.persistence.OrganigramDao;
import ar.edu.itba.paw.interfaces.persistence.OrganizationDao;
import ar.edu.itba.paw.interfaces.service.OrganigramService;
import ar.edu.itba.paw.interfaces.service.UserService;
import ar.edu.itba.paw.interfaces.service.exception.OrganizationNotFoundException;
import ar.edu.itba.paw.model.OpenPosition;
import ar.edu.itba.paw.model.Organigram;
import ar.edu.itba.paw.model.Organization;
import ar.edu.itba.paw.model.User;
import ar.edu.itba.paw.service.annotation.TransactionalRead;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@RequiredArgsConstructor
@Service
public class OrganigramServiceImpl implements OrganigramService {
    private static final Logger LOGGER = LoggerFactory.getLogger(OrganigramServiceImpl.class);

    private final OrganigramDao organigramDao;
    private final UserService userService;
    private final OrganizationDao organizationDao;
    private final OpenPositionDao openPositionDao;

    @Override
    @TransactionalRead
    public Optional<Organigram> findByID(final long organigramID) {
        return organigramDao.findByID(organigramID);
    }

    @Override
    @TransactionalRead
    public List<Organigram> findByOrganizationID(final long organizationID) {
        Organization organization = organizationDao.findByID(organizationID).orElseThrow(() -> new OrganizationNotFoundException(organizationID));
        return organigramDao.findByOrganization(organization);
    }

    @Override
    @TransactionalRead
    public List<Organigram> findRootPositionsByOrganizationID(final long organizationID) {
        Organization organization = organizationDao.findByID(organizationID).orElseThrow(() -> new OrganizationNotFoundException(organizationID));
        return organigramDao.findRootPositionsByOrganization(organization);
    }

    @Override
    @TransactionalRead
    public List<Organigram> findChildPositions(final long parentPositionID) {
        Organigram parentPosition = organigramDao.findByID(parentPositionID).orElseThrow(() -> new RuntimeException("Parent position not found"));
        return organigramDao.findChildPositions(parentPosition);
    }

    @Override
    @Transactional
    public Organigram createPosition(final long organizationID, final String positionName, final String description, final Long parentPositionID, final int sortOrder, final String assignedUserEmail) {
        LOGGER.debug("Creating organigram position '{}' for organization {}.", positionName, organizationID);
        
        Organization organization = organizationDao.findByID(organizationID).orElseThrow(() -> new OrganizationNotFoundException(organizationID));
        Organigram parentPosition = parentPositionID != null ? organigramDao.findByID(parentPositionID).orElse(null) : null;
        
        // Calculate level automatically: 0 if root, parent.getLevel() + 1 if has parent
        int level;
        if (parentPosition == null) {
            level = 0;
        } else {
            level = parentPosition.getLevel() + 1;
        }
        
        Organigram createdPosition = organigramDao.create(organization, positionName, description, parentPosition, level, sortOrder);
        
        if (assignedUserEmail != null && !assignedUserEmail.trim().isEmpty()) {
            User assignedUser = userService.findByEmail(assignedUserEmail.trim());
            createdPosition.setAssignedUser(assignedUser);
        }
        
        LOGGER.info("Organigram position created successfully: {} (ID: {}).", positionName, createdPosition.getOrganigramID());
        return createdPosition;
    }

    @Override
    @Transactional
    public void updatePosition(final long organigramID, final String positionName, final String description, final Long parentPositionID, final int sortOrder, final String assignedUserEmail) {
        LOGGER.debug("Updating organigram position {} with name '{}'.", organigramID, positionName);
        
        Organigram position = organigramDao.findByID(organigramID).orElseThrow(() -> new RuntimeException("Position not found"));
        Organigram newParentPosition = parentPositionID != null ? organigramDao.findByID(parentPositionID).orElse(null) : null;
        
        position.setPositionName(positionName);
        position.setDescription(description);
        position.setParentPosition(newParentPosition);
        
        // Calculate level automatically: 0 if root, parent.getLevel() + 1 if has parent
        int level;
        if (newParentPosition == null) {
            level = 0;
        } else {
            level = newParentPosition.getLevel() + 1;
        }
        position.setLevel(level);
        position.setSortOrder(sortOrder);
        
        if (assignedUserEmail != null && !assignedUserEmail.trim().isEmpty()) {
            User assignedUser = userService.findByEmail(assignedUserEmail.trim());
            position.setAssignedUser(assignedUser);
        } else {
            position.setAssignedUser(null);
        }
        
        LOGGER.info("Organigram position {} updated successfully.", organigramID);
    }

    @Override
    @Transactional
    public void deletePosition(final long organigramID) {
        LOGGER.debug("Deleting organigram position {}.", organigramID);
        Organigram position = organigramDao.findByID(organigramID).orElseThrow(() -> new RuntimeException("Position not found"));
        organigramDao.delete(position);
        LOGGER.info("Organigram position {} deleted successfully.", organigramID);
    }

    @Override
    @TransactionalRead
    public boolean existsPositionNameInOrganization(final String positionName, final long organizationID) {
        Organization organization = organizationDao.findByID(organizationID).orElseThrow(() -> new OrganizationNotFoundException(organizationID));
        return organigramDao.existsPositionNameInOrganization(positionName, organization);
    }

    @Override
    @TransactionalRead
    public boolean hasChildPositions(final long organigramID) {
        Organigram position = organigramDao.findByID(organigramID).orElseThrow(() -> new RuntimeException("Position not found"));
        return organigramDao.hasChildPositions(position);
    }

    @Override
    @TransactionalRead
    public List<Organigram> getOrganigramTree(final long organizationID) {
        Organization organization = organizationDao.findByID(organizationID).orElseThrow(() -> new OrganizationNotFoundException(organizationID));
        return organigramDao.findByOrganization(organization);
    }

    @Override
    @TransactionalRead
    public List<Organigram> getVacantPositions(final long organizationID) {
        Organization organization = organizationDao.findByID(organizationID).orElseThrow(() -> new OrganizationNotFoundException(organizationID));
        return organigramDao.findVacantPositionsByOrganization(organization);
    }

    @Override
    @Transactional
    public void assignUserToPosition(final long organigramID, final long userID) {
        LOGGER.debug("Assigning user {} to organigram position {}.", userID, organigramID);
        Organigram position = organigramDao.findByID(organigramID).orElseThrow(() -> new RuntimeException("Position not found"));
        User user = userService.findByID(userID);
        position.setAssignedUser(user);
        LOGGER.info("User {} assigned to organigram position {} successfully.", userID, organigramID);
    }

    @Override
    @Transactional
    public void unassignUserFromPosition(final long organigramID) {
        LOGGER.debug("Unassigning user from organigram position {}.", organigramID);
        Organigram position = organigramDao.findByID(organigramID).orElseThrow(() -> new RuntimeException("Position not found"));
        position.setAssignedUser(null);
        LOGGER.info("User unassigned from organigram position {} successfully.", organigramID);
    }

    @Override
    @TransactionalRead
    public boolean existsSortOrderInLevel(final int level, final int sortOrder, final long organizationID) {
        Organization organization = organizationDao.findByID(organizationID).orElseThrow(() -> new OrganizationNotFoundException(organizationID));
        return organigramDao.existsSortOrderInLevel(level, sortOrder, organization);
    }

    @Override
    @TransactionalRead
    public boolean existsSortOrderInParent(final Long parentPositionID, final int sortOrder, final long organizationID) {
        Organization organization = organizationDao.findByID(organizationID).orElseThrow(() -> new OrganizationNotFoundException(organizationID));
        Organigram parentPosition = parentPositionID != null ? organigramDao.findByID(parentPositionID).orElse(null) : null;
        return organigramDao.existsSortOrderInParent(parentPosition, sortOrder, organization);
    }

    @Override
    @TransactionalRead
    public Map<Long, Long> getOrganigramToOpenPositionMap(final long organizationID) {
        final Organization organization = organizationDao.findByID(organizationID).orElseThrow(() -> new OrganizationNotFoundException(organizationID));
        final List<OpenPosition> activeOpenPositions = openPositionDao.findActiveByOrganizationWithOrganigram(organization);
        final Map<Long, Long> organigramToOpenPositionMap = new HashMap<>();
        
        for (OpenPosition openPosition : activeOpenPositions) {
            final Organigram organigram = openPosition.getOrganigramPosition();
            if (organigram != null && organigram.getAssignedUser() == null) {
                organigramToOpenPositionMap.put(organigram.getOrganigramID(), openPosition.getPositionID());
            }
        }
        
        return organigramToOpenPositionMap;
    }
}
