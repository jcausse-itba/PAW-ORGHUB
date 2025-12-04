package ar.edu.itba.paw.service;

import ar.edu.itba.paw.interfaces.persistence.*;
import ar.edu.itba.paw.interfaces.service.EmailService;
import ar.edu.itba.paw.interfaces.service.exception.InstitutionNotFoundException;
import ar.edu.itba.paw.interfaces.service.exception.UserEmailExistsException;
import ar.edu.itba.paw.interfaces.service.exception.UserNotFoundException;
import ar.edu.itba.paw.model.Institution;
import ar.edu.itba.paw.model.Organization;
import ar.edu.itba.paw.model.Post;
import ar.edu.itba.paw.model.User;
import ar.edu.itba.paw.model.enums.Language;
import ar.edu.itba.paw.model.enums.Role;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class UserServiceImplTest {

    @Mock
    private UserDao userDao;

    @Mock
    private SubscriptionDao subscriptionDao;

    @Mock
    private InstitutionDao institutionDao;

    @Mock
    private UsersRolesDao usersRolesDao;

    @Mock
    private OrganigramDao organigramDao;

    @Mock
    private OrganizationDao organizationDao;

    @Mock
    private LikeDao likeDao;

    @Mock
    private EmailService emailService;

    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private UserServiceImpl userService;

    private User testUser;
    private Institution testInstitution;
    private Organization testOrganization;

    @Before
    public void setup() {
        testInstitution = new Institution("ITBA", "itba.edu.ar", "admin@itba.edu.ar");
        // Institution ID is set by the database

        testUser = new User("test@itba.edu.ar", "John", "Doe", "encodedPassword", Language.SPANISH, testInstitution);
        // User ID is set by the database

        testOrganization = new Organization("org@itba.edu.ar", "Test Org", null, null, "Description", testInstitution);
        // Organization ID is set by the database
    }

    @Test
    public void testFindByID() {
        when(userDao.findByID(1L)).thenReturn(Optional.of(testUser));

        final User result = userService.findByID(1L);

        Assert.assertNotNull(result);
        Assert.assertEquals(testUser, result);
        verify(userDao, times(1)).findByID(1L);
    }

    @Test(expected = UserNotFoundException.class)
    public void testFindByIDNonExistent() {
        when(userDao.findByID(999L)).thenReturn(Optional.empty());

        userService.findByID(999L);
    }

    @Test
    public void testFindByEmail() {
        when(userDao.findByEmail("test@itba.edu.ar")).thenReturn(Optional.of(testUser));

        final User result = userService.findByEmail("test@itba.edu.ar");

        Assert.assertNotNull(result);
        Assert.assertEquals(testUser, result);
        verify(userDao, times(1)).findByEmail("test@itba.edu.ar");
    }

    @Test(expected = UserNotFoundException.class)
    public void testFindByEmailNonExistent() {
        when(userDao.findByEmail("nonexistent@itba.edu.ar")).thenReturn(Optional.empty());

        userService.findByEmail("nonexistent@itba.edu.ar");
    }

    @Test
    public void testCreate() {
        when(userDao.existsEmail("newuser@itba.edu.ar")).thenReturn(false);
        when(institutionDao.findByDomainName("itba.edu.ar")).thenReturn(Optional.of(testInstitution));
        when(passwordEncoder.encode("password123")).thenReturn("encodedPassword");
        when(userDao.create(anyString(), anyString(), anyString(), anyString(), any(), any()))
                .thenReturn(testUser);

        final User result = userService.create("newuser@itba.edu.ar", "Jane", "Smith", "password123", Language.ENGLISH);

        Assert.assertNotNull(result);
        verify(userDao, times(1)).existsEmail("newuser@itba.edu.ar");
        verify(institutionDao, times(1)).findByDomainName("itba.edu.ar");
        verify(passwordEncoder, times(1)).encode("password123");
        verify(userDao, times(1)).create(eq("newuser@itba.edu.ar"), eq("Jane"), eq("Smith"), eq("encodedPassword"), eq(Language.ENGLISH), eq(testInstitution));
    }

    @Test
    public void testCreateWithDefaultLanguage() {
        when(userDao.existsEmail("newuser@itba.edu.ar")).thenReturn(false);
        when(institutionDao.findByDomainName("itba.edu.ar")).thenReturn(Optional.of(testInstitution));
        when(passwordEncoder.encode("password123")).thenReturn("encodedPassword");
        when(userDao.create(anyString(), anyString(), anyString(), anyString(), any(), any()))
                .thenReturn(testUser);

        final User result = userService.create("newuser@itba.edu.ar", "Jane", "Smith", "password123");

        Assert.assertNotNull(result);
        verify(userDao, times(1)).create(eq("newuser@itba.edu.ar"), eq("Jane"), eq("Smith"), eq("encodedPassword"), eq(Language.getDefault()), eq(testInstitution));
    }

    @Test(expected = UserEmailExistsException.class)
    public void testCreateWithExistingEmail() {
        when(userDao.existsEmail("existing@itba.edu.ar")).thenReturn(true);

        userService.create("existing@itba.edu.ar", "Jane", "Smith", "password123");

        verify(userDao, times(1)).existsEmail("existing@itba.edu.ar");
        verify(userDao, never()).create(anyString(), anyString(), anyString(), anyString(), any(), any());
    }

    @Test(expected = InstitutionNotFoundException.class)
    public void testCreateWithNonExistentInstitution() {
        when(userDao.existsEmail("user@unknown.edu.ar")).thenReturn(false);
        when(institutionDao.findByDomainName("unknown.edu.ar")).thenReturn(Optional.empty());

        userService.create("user@unknown.edu.ar", "Jane", "Smith", "password123");
    }

    @Test
    public void testIsSubscribed() {
        when(userDao.findByID(1L)).thenReturn(Optional.of(testUser));
        when(organizationDao.findByID(1L)).thenReturn(Optional.of(testOrganization));
        when(subscriptionDao.isSubscribed(testUser, testOrganization)).thenReturn(true);

        final boolean result = userService.isSubscribed(1L, 1L);

        Assert.assertTrue(result);
        verify(subscriptionDao, times(1)).isSubscribed(testUser, testOrganization);
    }

    @Test
    public void testAddSubscription() {
        when(userDao.findByID(1L)).thenReturn(Optional.of(testUser));
        when(organizationDao.findByID(1L)).thenReturn(Optional.of(testOrganization));
        when(subscriptionDao.isSubscribed(testUser, testOrganization)).thenReturn(false);
        doNothing().when(subscriptionDao).addSubscription(testUser, testOrganization);
        doNothing().when(emailService).sendSubscriptionNotificationEmail(anyString(), anyString(), anyString(), anyLong());

        userService.addSubscription(1L, 1L);

        verify(subscriptionDao, times(1)).addSubscription(testUser, testOrganization);
        verify(emailService, times(1)).sendSubscriptionNotificationEmail(anyString(), anyString(), anyString(), anyLong());
    }

    @Test
    public void testAddSubscriptionAlreadySubscribed() {
        when(userDao.findByID(1L)).thenReturn(Optional.of(testUser));
        when(organizationDao.findByID(1L)).thenReturn(Optional.of(testOrganization));
        when(subscriptionDao.isSubscribed(testUser, testOrganization)).thenReturn(true);

        userService.addSubscription(1L, 1L);

        verify(subscriptionDao, never()).addSubscription(any(), any());
        verify(emailService, never()).sendSubscriptionNotificationEmail(anyString(), anyString(), anyString(), anyLong());
    }

    @Test
    public void testRemoveSubscription() {
        when(userDao.findByID(1L)).thenReturn(Optional.of(testUser));
        when(organizationDao.findByID(1L)).thenReturn(Optional.of(testOrganization));
        when(subscriptionDao.isSubscribed(testUser, testOrganization)).thenReturn(true);
        doNothing().when(subscriptionDao).removeSubscription(testUser, testOrganization);

        userService.removeSubscription(1L, 1L);

        verify(subscriptionDao, times(1)).removeSubscription(testUser, testOrganization);
    }

    @Test
    public void testRemoveSubscriptionNotSubscribed() {
        when(userDao.findByID(1L)).thenReturn(Optional.of(testUser));
        when(organizationDao.findByID(1L)).thenReturn(Optional.of(testOrganization));
        when(subscriptionDao.isSubscribed(testUser, testOrganization)).thenReturn(false);

        userService.removeSubscription(1L, 1L);

        verify(subscriptionDao, never()).removeSubscription(any(), any());
    }

    @Test
    public void testGetSubscribedOrganizations() {
        when(userDao.findByID(1L)).thenReturn(Optional.of(testUser));
        when(subscriptionDao.getSubscribedOrganizations(testUser)).thenReturn(Arrays.asList(testOrganization));

        final List<Organization> result = userService.getSubscribedOrganizations(1L);

        Assert.assertEquals(1, result.size());
        Assert.assertEquals(testOrganization, result.get(0));
        verify(subscriptionDao, times(1)).getSubscribedOrganizations(testUser);
    }

    @Test
    public void testGetRoleForOrganizationByID() {
        when(userDao.findByID(1L)).thenReturn(Optional.of(testUser));
        when(organizationDao.findByID(1L)).thenReturn(Optional.of(testOrganization));
        when(usersRolesDao.getUserRole(testUser, testOrganization)).thenReturn(Role.PUBLISHER);

        final Role result = userService.getRoleForOrganizationByID(1L, 1L);

        Assert.assertEquals(Role.PUBLISHER, result);
        verify(usersRolesDao, times(1)).getUserRole(testUser, testOrganization);
    }

    @Test
    public void testAssignRoleForOrganizationByID() {
        when(userDao.findByID(1L)).thenReturn(Optional.of(testUser));
        when(organizationDao.findByID(1L)).thenReturn(Optional.of(testOrganization));
        doNothing().when(usersRolesDao).assignUserRole(testUser, testOrganization, Role.MANAGER);
        doNothing().when(emailService).sendAddedRoleEmail(anyString(), anyString(), anyBoolean(), anyLong());

        userService.assignRoleForOrganizationByID(1L, 1L, Role.MANAGER);

        verify(usersRolesDao, times(1)).assignUserRole(testUser, testOrganization, Role.MANAGER);
        verify(emailService, times(1)).sendAddedRoleEmail(anyString(), anyString(), anyBoolean(), anyLong());
    }

    @Test
    public void testAssignRoleUSER() {
        when(userDao.findByID(1L)).thenReturn(Optional.of(testUser));
        when(organizationDao.findByID(1L)).thenReturn(Optional.of(testOrganization));
        doNothing().when(usersRolesDao).deleteUserRole(testUser, testOrganization);

        userService.assignRoleForOrganizationByID(1L, 1L, Role.USER);

        verify(usersRolesDao, times(1)).deleteUserRole(testUser, testOrganization);
        verify(usersRolesDao, never()).assignUserRole(any(), any(), any());
    }

    @Test
    public void testDeleteRoleForOrganizationByID() {
        when(userDao.findByID(1L)).thenReturn(Optional.of(testUser));
        when(organizationDao.findByID(1L)).thenReturn(Optional.of(testOrganization));
        doNothing().when(usersRolesDao).deleteUserRole(testUser, testOrganization);

        userService.deleteRoleForOrganizationByID(1L, 1L);

        verify(usersRolesDao, times(1)).deleteUserRole(testUser, testOrganization);
    }

    @Test
    public void testUpdatePreferredLanguage() {
        when(userDao.findByID(1L)).thenReturn(Optional.of(testUser));
        doNothing().when(userDao).setPreferredLanguage(testUser, Language.ENGLISH);

        userService.updatePreferredLanguage(1L, Language.ENGLISH);

        verify(userDao, times(1)).setPreferredLanguage(testUser, Language.ENGLISH);
    }

    @Test
    public void testExistsEmail() {
        when(userDao.existsEmail("test@itba.edu.ar")).thenReturn(true);

        final boolean result = userService.existsEmail("test@itba.edu.ar");

        Assert.assertTrue(result);
        verify(userDao, times(1)).existsEmail("test@itba.edu.ar");
    }

    @Test
    public void testGetUserPositions() {
        when(userDao.findByID(1L)).thenReturn(Optional.of(testUser));
        when(organigramDao.findByUser(testUser)).thenReturn(Arrays.asList());

        final List result = userService.getUserPositions(1L);

        Assert.assertNotNull(result);
        verify(organigramDao, times(1)).findByUser(testUser);
    }

    @Test
    public void testFindLikedPostsByUser() {
        final Post post = new Post();
        // Post ID is set by the database
        when(userDao.findByID(1L)).thenReturn(Optional.of(testUser));
        when(likeDao.findLikedPostsByUser(testUser)).thenReturn(Arrays.asList(post));

        final List<Post> result = userService.findLikedPostsByUser(1L);

        Assert.assertEquals(1, result.size());
        Assert.assertEquals(post, result.get(0));
        verify(likeDao, times(1)).findLikedPostsByUser(testUser);
    }
}

