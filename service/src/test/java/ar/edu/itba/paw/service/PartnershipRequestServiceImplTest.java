package ar.edu.itba.paw.service;

import ar.edu.itba.paw.interfaces.persistence.PartnershipRequestDao;
import ar.edu.itba.paw.interfaces.service.InstitutionService;
import ar.edu.itba.paw.interfaces.service.exception.PartnershipRequestNotFoundException;
import ar.edu.itba.paw.model.Institution;
import ar.edu.itba.paw.model.PartnershipRequest;
import ar.edu.itba.paw.model.enums.Status;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class PartnershipRequestServiceImplTest {

    @Mock
    private PartnershipRequestDao partnershipRequestDao;

    @Mock
    private InstitutionService institutionService;

    @InjectMocks
    private PartnershipRequestServiceImpl partnershipRequestService;

    private PartnershipRequest testRequest;
    private Institution testInstitution;

    @Before
    public void setup() {
        testRequest = new PartnershipRequest("ITBA", "itba.edu.ar", "admin@itba.edu.ar", Status.PENDING, java.time.LocalDateTime.now());
        // Request ID is set by the database

        testInstitution = new Institution("ITBA", "itba.edu.ar", "admin@itba.edu.ar");
        // Institution ID is set by the database
    }

    @Test
    public void testCreateRequest() {
        when(partnershipRequestDao.create("New Institution", "new.edu.ar", "admin@new.edu.ar"))
                .thenReturn(testRequest);

        final PartnershipRequest result = partnershipRequestService.createRequest("New Institution", "new.edu.ar", "admin@new.edu.ar");

        Assert.assertNotNull(result);
        Assert.assertEquals(testRequest, result);
        verify(partnershipRequestDao, times(1)).create("New Institution", "new.edu.ar", "admin@new.edu.ar");
    }

    @Test
    public void testGetAllRequests() {
        when(partnershipRequestDao.findAll()).thenReturn(Arrays.asList(testRequest));

        final List<PartnershipRequest> result = partnershipRequestService.getAllRequests();

        Assert.assertEquals(1, result.size());
        Assert.assertEquals(testRequest, result.get(0));
        verify(partnershipRequestDao, times(1)).findAll();
    }

    @Test
    public void testGetRequestsByStatus() {
        when(partnershipRequestDao.findByStatus(Status.PENDING)).thenReturn(Arrays.asList(testRequest));

        final List<PartnershipRequest> result = partnershipRequestService.getRequestsByStatus(Status.PENDING);

        Assert.assertEquals(1, result.size());
        verify(partnershipRequestDao, times(1)).findByStatus(Status.PENDING);
    }

    @Test
    public void testGetRequestByID() {
        when(partnershipRequestDao.findByID(1L)).thenReturn(Optional.of(testRequest));

        final PartnershipRequest result = partnershipRequestService.getRequestByID(1L);

        Assert.assertNotNull(result);
        Assert.assertEquals(testRequest, result);
        verify(partnershipRequestDao, times(1)).findByID(1L);
    }

    @Test(expected = PartnershipRequestNotFoundException.class)
    public void testGetRequestByIDNonExistent() {
        when(partnershipRequestDao.findByID(999L)).thenReturn(Optional.empty());

        partnershipRequestService.getRequestByID(999L);
    }

    @Test
    public void testApproveRequest() {
        when(partnershipRequestDao.findByID(1L)).thenReturn(Optional.of(testRequest));
        doNothing().when(partnershipRequestDao).updateStatus(testRequest, Status.ACCEPTED);
        when(institutionService.create("ITBA", "itba.edu.ar", "admin@itba.edu.ar")).thenReturn(testInstitution);

        partnershipRequestService.approveRequest(1L);

        verify(partnershipRequestDao, times(1)).updateStatus(testRequest, Status.ACCEPTED);
        verify(institutionService, times(1)).create("ITBA", "itba.edu.ar", "admin@itba.edu.ar");
    }

    @Test
    public void testRejectRequest() {
        when(partnershipRequestDao.findByID(1L)).thenReturn(Optional.of(testRequest));
        doNothing().when(partnershipRequestDao).updateStatus(testRequest, Status.REJECTED);

        partnershipRequestService.rejectRequest(1L);

        verify(partnershipRequestDao, times(1)).updateStatus(testRequest, Status.REJECTED);
        verify(institutionService, never()).create(anyString(), anyString(), anyString());
    }

    @Test
    public void testDomainNameExists() {
        when(partnershipRequestDao.existsByDomainName("itba.edu.ar")).thenReturn(true);

        final boolean result = partnershipRequestService.domainNameExists("itba.edu.ar");

        Assert.assertTrue(result);
        verify(partnershipRequestDao, times(1)).existsByDomainName("itba.edu.ar");
    }
}

