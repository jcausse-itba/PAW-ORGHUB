package ar.edu.itba.paw.service;

import ar.edu.itba.paw.interfaces.enums.OrderCriteria;
import ar.edu.itba.paw.interfaces.persistence.*;
import ar.edu.itba.paw.interfaces.service.EmailService;
import ar.edu.itba.paw.interfaces.service.exception.InstitutionNotFoundException;
import ar.edu.itba.paw.interfaces.service.exception.OrganizationEmailExistsException;
import ar.edu.itba.paw.interfaces.service.exception.OrganizationNotFoundException;
import ar.edu.itba.paw.model.Event;
import ar.edu.itba.paw.model.Institution;
import ar.edu.itba.paw.model.OpenPosition;
import ar.edu.itba.paw.model.Organization;
import ar.edu.itba.paw.model.Post;
import ar.edu.itba.paw.model.User;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class OrganizationServiceImplTest {

    @Mock
    private OrganizationDao orgDao;

    @Mock
    private PostDao postDao;

    @Mock
    private EventDao eventDao;

    @Mock
    private OpenPositionDao openPositionDao;

    @Mock
    private EmailService emailService;

    @Mock
    private SubscriptionDao subscriptionDao;

    @Mock
    private InstitutionDao institutionDao;

    @InjectMocks
    private OrganizationServiceImpl organizationService;

    private Institution testInstitution;
    private Organization testOrganization;
    private User testUser;

    @Before
    public void setup() throws Exception {
        testInstitution = new Institution("ITBA", "itba.edu.ar", "admin@itba.edu.ar");
        setField(testInstitution, "institutionID", 1L);

        testOrganization = new Organization("org@itba.edu.ar", "Test Org", null, null, "Description", testInstitution);
        setField(testOrganization, "orgID", 1L);

        testUser = new User("user@itba.edu.ar", "User", "Name", "password", null, testInstitution);
        setField(testUser, "userID", 1L);
    }

    private void setField(Object obj, String fieldName, Object value) throws Exception {
        Field field = obj.getClass().getDeclaredField(fieldName);
        field.setAccessible(true);
        field.set(obj, value);
    }

    @Test
    public void testFindByID() {
        when(orgDao.findByID(1L)).thenReturn(java.util.Optional.of(testOrganization));

        final Organization result = organizationService.findByID(1L);

        Assert.assertNotNull(result);
        Assert.assertEquals(testOrganization, result);
        verify(orgDao, times(1)).findByID(1L);
    }

    @Test(expected = OrganizationNotFoundException.class)
    public void testFindByIDNonExistent() {
        when(orgDao.findByID(999L)).thenReturn(java.util.Optional.empty());

        organizationService.findByID(999L);
    }

    @Test
    public void testFindByEmail() {
        when(orgDao.findByEmail("org@itba.edu.ar")).thenReturn(java.util.Optional.of(testOrganization));

        final Organization result = organizationService.findByEmail("org@itba.edu.ar");

        Assert.assertNotNull(result);
        Assert.assertEquals(testOrganization, result);
        verify(orgDao, times(1)).findByEmail("org@itba.edu.ar");
    }

    @Test(expected = OrganizationNotFoundException.class)
    public void testFindByEmailNonExistent() {
        when(orgDao.findByEmail("nonexistent@itba.edu.ar")).thenReturn(java.util.Optional.empty());

        organizationService.findByEmail("nonexistent@itba.edu.ar");
    }

    @Test
    public void testFindAllForInstitution() {
        when(institutionDao.findByID(1L)).thenReturn(java.util.Optional.of(testInstitution));
        when(orgDao.findAllForInstitution(testInstitution, 0)).thenReturn(Arrays.asList(testOrganization));

        final List<Organization> result = organizationService.findAllForInstitution(1L, 0);

        Assert.assertEquals(1, result.size());
        Assert.assertEquals(testOrganization, result.get(0));
        verify(orgDao, times(1)).findAllForInstitution(testInstitution, 0);
    }

    @Test
    public void testGetPublishers() {
        when(orgDao.findByID(1L)).thenReturn(java.util.Optional.of(testOrganization));
        when(orgDao.getPublishers(testOrganization)).thenReturn(Arrays.asList(testUser));

        final List<User> result = organizationService.getPublishers(1L);

        Assert.assertEquals(1, result.size());
        Assert.assertEquals(testUser, result.get(0));
        verify(orgDao, times(1)).getPublishers(testOrganization);
    }

    @Test
    public void testGetManagers() {
        when(orgDao.findByID(1L)).thenReturn(java.util.Optional.of(testOrganization));
        when(orgDao.getManagers(testOrganization)).thenReturn(Arrays.asList(testUser));

        final List<User> result = organizationService.getManagers(1L);

        Assert.assertEquals(1, result.size());
        verify(orgDao, times(1)).getManagers(testOrganization);
    }

    @Test
    public void testGetSubscribers() {
        when(orgDao.findByID(1L)).thenReturn(java.util.Optional.of(testOrganization));
        when(subscriptionDao.getSubscribers(testOrganization)).thenReturn(Arrays.asList(testUser));

        final List<User> result = organizationService.getSubscribers(1L);

        Assert.assertEquals(1, result.size());
        verify(subscriptionDao, times(1)).getSubscribers(testOrganization);
    }

    @Test
    public void testCreate() throws Exception {
        final Organization createdOrg = new Organization("neworg@itba.edu.ar", "New Org", null, null, "Description", testInstitution);
        setField(createdOrg, "orgID", 1L);
        
        when(orgDao.existsEmail("neworg@itba.edu.ar")).thenReturn(false);
        when(institutionDao.findByID(1L)).thenReturn(java.util.Optional.of(testInstitution));
        when(orgDao.create(anyString(), anyString(), any(), any(), anyString(), any())).thenReturn(createdOrg);
        doNothing().when(emailService).sendCreationEmail(anyString(), anyString(), anyLong());

        final Organization result = organizationService.create("neworg@itba.edu.ar", "New Org", null, null, "Description", 1L);

        Assert.assertNotNull(result);
        verify(orgDao, times(1)).existsEmail("neworg@itba.edu.ar");
        verify(institutionDao, times(1)).findByID(1L);
        verify(orgDao, times(1)).create(eq("neworg@itba.edu.ar"), eq("New Org"), isNull(), isNull(), eq("Description"), eq(testInstitution));
        verify(emailService, times(1)).sendCreationEmail(anyString(), anyString(), anyLong());
    }

    @Test(expected = OrganizationEmailExistsException.class)
    public void testCreateWithExistingEmail() {
        when(orgDao.existsEmail("existing@itba.edu.ar")).thenReturn(true);

        organizationService.create("existing@itba.edu.ar", "New Org", null, null, "Description", 1L);

        verify(orgDao, times(1)).existsEmail("existing@itba.edu.ar");
        verify(orgDao, never()).create(anyString(), anyString(), any(), any(), anyString(), any());
    }

    @Test(expected = InstitutionNotFoundException.class)
    public void testCreateWithNonExistentInstitution() {
        when(orgDao.existsEmail("neworg@itba.edu.ar")).thenReturn(false);
        when(institutionDao.findByID(999L)).thenReturn(java.util.Optional.empty());

        organizationService.create("neworg@itba.edu.ar", "New Org", null, null, "Description", 999L);
    }

    @Test
    public void testGetPostsByOrgID() {
        final Post post = new Post();
        // Post ID is set by the database
        when(orgDao.findByID(1L)).thenReturn(java.util.Optional.of(testOrganization));
        when(postDao.findAllForOrganizations(Collections.singletonList(testOrganization), OrderCriteria.getDefault(), 0))
                .thenReturn(Arrays.asList(post));

        final List<Post> result = organizationService.getPostsByOrgID(1L, 0);

        Assert.assertEquals(1, result.size());
        verify(postDao, times(1)).findAllForOrganizations(Collections.singletonList(testOrganization), OrderCriteria.getDefault(), 0);
    }

    @Test
    public void testGetEventsByOrgID() {
        final Event event = new Event();
        // Event ID is set by the database
        when(orgDao.findByID(1L)).thenReturn(java.util.Optional.of(testOrganization));
        when(eventDao.findAllForOrganizations(Collections.singletonList(testOrganization), OrderCriteria.getDefault(), 0))
                .thenReturn(Arrays.asList(event));

        final List<Event> result = organizationService.getEventsByOrgID(1L, 0);

        Assert.assertEquals(1, result.size());
        verify(eventDao, times(1)).findAllForOrganizations(Collections.singletonList(testOrganization), OrderCriteria.getDefault(), 0);
    }

    @Test
    public void testGetOpenPositionsByOrgID() {
        final OpenPosition position = new OpenPosition();
        position.setPositionID(1L);
        when(orgDao.findByID(1L)).thenReturn(java.util.Optional.of(testOrganization));
        when(openPositionDao.findAllForOrganizations(Collections.singletonList(testOrganization), OrderCriteria.getDefault(), 0))
                .thenReturn(Arrays.asList(position));

        final List<OpenPosition> result = organizationService.getOpenPositionsByOrgID(1L, 0);

        Assert.assertEquals(1, result.size());
        verify(openPositionDao, times(1)).findAllForOrganizations(Collections.singletonList(testOrganization), OrderCriteria.getDefault(), 0);
    }

    @Test
    public void testGetAllOrganizations() {
        when(orgDao.findAll(0)).thenReturn(Arrays.asList(testOrganization));

        final List<Organization> result = organizationService.getAllOrganizations(0);

        Assert.assertEquals(1, result.size());
        verify(orgDao, times(1)).findAll(0);
    }

    @Test
    public void testExistsEmail() {
        when(orgDao.existsEmail("org@itba.edu.ar")).thenReturn(true);

        final boolean result = organizationService.existsEmail("org@itba.edu.ar");

        Assert.assertTrue(result);
        verify(orgDao, times(1)).existsEmail("org@itba.edu.ar");
    }

    @Test
    public void testGetInstitutionIDByID() {
        when(orgDao.findByID(1L)).thenReturn(java.util.Optional.of(testOrganization));

        final long result = organizationService.getInstitutionIDByID(1L);

        Assert.assertEquals(1L, result);
        verify(orgDao, times(1)).findByID(1L);
    }
}

