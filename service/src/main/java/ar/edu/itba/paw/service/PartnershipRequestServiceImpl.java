// DONE

package ar.edu.itba.paw.service;

import ar.edu.itba.paw.interfaces.persistence.PartnershipRequestDao;
import ar.edu.itba.paw.interfaces.service.InstitutionService;
import ar.edu.itba.paw.interfaces.service.PartnershipRequestService;
import ar.edu.itba.paw.interfaces.service.exception.PartnershipRequestNotFoundException;
import ar.edu.itba.paw.model.PartnershipRequest;
import ar.edu.itba.paw.model.enums.Status;
import ar.edu.itba.paw.service.annotation.TransactionalRead;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@RequiredArgsConstructor
@Service
public class PartnershipRequestServiceImpl implements PartnershipRequestService {
    private static final Logger LOGGER = LoggerFactory.getLogger(PartnershipRequestServiceImpl.class);

    private final PartnershipRequestDao partnershipRequestDao;
    private final InstitutionService institutionService;

    @Override
    @Transactional
    public PartnershipRequest createRequest(final String institutionName, final String domainName, final String adminEmail) {
        LOGGER.debug("Creating partnership request for institution: {}, domain: {}", institutionName, domainName);
        
        PartnershipRequest request = partnershipRequestDao.create(institutionName, domainName, adminEmail);
        LOGGER.info("Partnership request created successfully: {} (ID: {}).", institutionName, request.getRequestID());
        return request;
    }

    @Override
    @TransactionalRead
    public List<PartnershipRequest> getAllRequests() {
        return partnershipRequestDao.findAll();
    }

    @Override
    @TransactionalRead
    public List<PartnershipRequest> getRequestsByStatus(final Status status) {
        return partnershipRequestDao.findByStatus(status);
    }

    @Override
    @TransactionalRead
    public PartnershipRequest getRequestByID(final long requestID) {
        return partnershipRequestDao.findByID(requestID).orElseThrow(() -> new PartnershipRequestNotFoundException(requestID));
    }

    @Override
    @Transactional
    public void approveRequest(final long requestID) {
        LOGGER.debug("Approving partnership request {}.", requestID);
        
        PartnershipRequest request = partnershipRequestDao.findByID(requestID).orElseThrow(() -> new PartnershipRequestNotFoundException(requestID));
        
        partnershipRequestDao.updateStatus(request, Status.ACCEPTED);
        institutionService.create(request.getInstitutionName(), request.getDomainName(), request.getAdminEmail());
        
        LOGGER.info("Partnership request {} approved and institution created.", requestID);
    }

    @Override
    @Transactional
    public void rejectRequest(final long requestID) {
        LOGGER.debug("Rejecting partnership request {}.", requestID);
        
        PartnershipRequest request = partnershipRequestDao.findByID(requestID).orElseThrow(() -> new PartnershipRequestNotFoundException(requestID));
        
        partnershipRequestDao.updateStatus(request, Status.REJECTED);
        LOGGER.info("Partnership request {} rejected.", requestID);
    }

    @Override
    @TransactionalRead
    public boolean domainNameExists(final String domainName) {
        return partnershipRequestDao.existsByDomainName(domainName);
    }
}
