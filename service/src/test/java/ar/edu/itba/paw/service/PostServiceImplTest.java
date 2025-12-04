package ar.edu.itba.paw.service;

import ar.edu.itba.paw.interfaces.enums.OrderCriteria;
import ar.edu.itba.paw.interfaces.enums.TagFilterCriteria;
import ar.edu.itba.paw.interfaces.persistence.*;
import ar.edu.itba.paw.interfaces.service.EmailService;
import ar.edu.itba.paw.interfaces.service.OrganizationService;
import ar.edu.itba.paw.interfaces.service.UserService;
import ar.edu.itba.paw.interfaces.service.exception.PostNotFoundException;
import ar.edu.itba.paw.model.Comment;
import ar.edu.itba.paw.model.Institution;
import ar.edu.itba.paw.model.Organization;
import ar.edu.itba.paw.model.Post;
import ar.edu.itba.paw.model.Tag;
import ar.edu.itba.paw.model.User;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import java.lang.reflect.Field;
import java.time.LocalDateTime;
import java.util.*;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class PostServiceImplTest {

    @Mock
    private PostDao postDao;

    @Mock
    private TagDao tagDao;

    @Mock
    private OrganizationDao orgDao;

    @Mock
    private InstitutionDao institutionDao;

    @Mock
    private CommentDao commentDao;

    @Mock
    private LikeDao likeDao;

    @Mock
    private EmailService emailService;

    @Mock
    private OrganizationService organizationService;

    @Mock
    private UserService userService;

    @InjectMocks
    private PostServiceImpl postService;

    private Institution testInstitution;
    private Organization testOrganization;
    private Post testPost;
    private User testUser;
    private Tag testTag;

    @Before
    public void setup() throws Exception {
        testInstitution = new Institution("ITBA", "itba.edu.ar", "admin@itba.edu.ar");
        setField(testInstitution, "institutionID", 1L);

        testOrganization = new Organization("org@itba.edu.ar", "Test Org", null, null, "Description", testInstitution);
        setField(testOrganization, "orgID", 1L);

        testPost = new Post(testOrganization, "Test Post", "Test Body", LocalDateTime.now(), new java.util.HashSet<>());
        setField(testPost, "postID", 1L);

        testUser = new User("user@itba.edu.ar", "User", "Name", "password", null, testInstitution);
        setField(testUser, "userID", 1L);

        testTag = new Tag("TestTag");
        setField(testTag, "tagID", 1L);
    }

    private void setField(Object obj, String fieldName, Object value) throws Exception {
        Field field = obj.getClass().getDeclaredField(fieldName);
        field.setAccessible(true);
        field.set(obj, value);
    }

    @Test
    public void testFindByID() {
        when(postDao.findByID(1L)).thenReturn(Optional.of(testPost));

        final Post result = postService.findByID(1L);

        Assert.assertNotNull(result);
        Assert.assertEquals(testPost, result);
        verify(postDao, times(1)).findByID(1L);
    }

    @Test(expected = PostNotFoundException.class)
    public void testFindByIDNonExistent() {
        when(postDao.findByID(999L)).thenReturn(Optional.empty());

        postService.findByID(999L);
    }

    @Test
    public void testFindAllForInstitution() {
        when(institutionDao.findByID(1L)).thenReturn(Optional.of(testInstitution));
        when(postDao.findAllForInstitution(testInstitution, OrderCriteria.getDefault(), 0))
                .thenReturn(Arrays.asList(testPost));

        final List<Post> result = postService.findAllForInstitution(1L, OrderCriteria.getDefault(), 0);

        Assert.assertEquals(1, result.size());
        verify(postDao, times(1)).findAllForInstitution(testInstitution, OrderCriteria.getDefault(), 0);
    }

    @Test
    public void testFindAllForTags() {
        when(institutionDao.findByID(1L)).thenReturn(Optional.of(testInstitution));
        when(tagDao.findByID(1L)).thenReturn(Optional.of(testTag));
        when(postDao.findAllForTags(testInstitution, Arrays.asList(testTag), TagFilterCriteria.ANY, OrderCriteria.getDefault(), 0))
                .thenReturn(Arrays.asList(testPost));

        final List<Post> result = postService.findAllForTags(1L, Arrays.asList(1L), TagFilterCriteria.ANY, OrderCriteria.getDefault(), 0);

        Assert.assertEquals(1, result.size());
        verify(postDao, times(1)).findAllForTags(testInstitution, Arrays.asList(testTag), TagFilterCriteria.ANY, OrderCriteria.getDefault(), 0);
    }

    @Test
    public void testFindAllForOrganizations() {
        when(orgDao.findByID(1L)).thenReturn(Optional.of(testOrganization));
        when(postDao.findAllForOrganizations(Arrays.asList(testOrganization), OrderCriteria.getDefault(), 0))
                .thenReturn(Arrays.asList(testPost));

        final List<Post> result = postService.findAllForOrganizations(Arrays.asList(1L), OrderCriteria.getDefault(), 0);

        Assert.assertEquals(1, result.size());
        verify(postDao, times(1)).findAllForOrganizations(Arrays.asList(testOrganization), OrderCriteria.getDefault(), 0);
    }

    @Test
    public void testCreate() throws Exception {
        final Post createdPost = new Post(testOrganization, "New Post", "Body", LocalDateTime.now(), new java.util.HashSet<>());
        setField(createdPost, "postID", 1L);
        
        when(orgDao.findByID(1L)).thenReturn(Optional.of(testOrganization));
        when(tagDao.findByID(1L)).thenReturn(Optional.of(testTag));
        when(postDao.create(any(), anyString(), anyString(), any(), any())).thenReturn(createdPost);
        when(organizationService.getSubscribers(1L)).thenReturn(Arrays.asList(testUser));
        doNothing().when(emailService).sendPostNotificationEmail(anyString(), anyString(), anyString(), anyString(), anyLong());

        final Post result = postService.create(1L, "New Post", "Body", LocalDateTime.now(), Arrays.asList(1L));

        Assert.assertNotNull(result);
        verify(postDao, times(1)).create(eq(testOrganization), eq("New Post"), eq("Body"), any(), any());
        verify(emailService, times(1)).sendPostNotificationEmail(anyString(), anyString(), anyString(), anyString(), anyLong());
    }

    @Test
    public void testUpdate() {
        when(postDao.findByID(1L)).thenReturn(Optional.of(testPost));
        when(tagDao.findByID(1L)).thenReturn(Optional.of(testTag));

        postService.update(1L, "Updated Title", "Updated Body", Arrays.asList(1L));

        Assert.assertEquals("Updated Title", testPost.getTitle());
        Assert.assertEquals("Updated Body", testPost.getBody());
        verify(postDao, times(1)).findByID(1L);
    }

    @Test
    public void testDelete() {
        when(postDao.findByID(1L)).thenReturn(Optional.of(testPost));
        doNothing().when(likeDao).deleteAllLikesForPost(testPost);
        doNothing().when(commentDao).deleteAllCommentsByPost(testPost);
        doNothing().when(postDao).delete(testPost);

        postService.delete(1L);

        verify(likeDao, times(1)).deleteAllLikesForPost(testPost);
        verify(commentDao, times(1)).deleteAllCommentsByPost(testPost);
        verify(postDao, times(1)).delete(testPost);
    }

    @Test
    public void testFindAllCommentsByID() {
        final Comment comment = new Comment(testUser, testPost, "Test comment");
        // Comment ID is set by the database
        when(postDao.findByID(1L)).thenReturn(Optional.of(testPost));
        when(postDao.getComments(testPost, 0)).thenReturn(Arrays.asList(comment));

        final List<Comment> result = postService.findAllCommentsByID(1L, 0);

        Assert.assertEquals(1, result.size());
        verify(postDao, times(1)).getComments(testPost, 0);
    }

    @Test
    public void testToggleLikePost() {
        when(postDao.findByID(1L)).thenReturn(Optional.of(testPost));
        when(userService.findByID(1L)).thenReturn(testUser);
        when(likeDao.isPostLikedByUser(testUser, testPost)).thenReturn(false);
        doNothing().when(likeDao).likePost(testUser, testPost);

        postService.toggleLikePost(1L, 1L);

        verify(likeDao, times(1)).likePost(testUser, testPost);
    }

    @Test
    public void testToggleLikePostAlreadyLiked() {
        when(postDao.findByID(1L)).thenReturn(Optional.of(testPost));
        when(userService.findByID(1L)).thenReturn(testUser);
        when(likeDao.isPostLikedByUser(testUser, testPost)).thenReturn(true);
        doNothing().when(likeDao).unlikePost(testUser, testPost);

        postService.toggleLikePost(1L, 1L);

        verify(likeDao, times(1)).unlikePost(testUser, testPost);
    }

    @Test
    public void testCreateComment() {
        final Comment comment = new Comment();
        // Comment ID is set by the database
        when(postDao.findByID(1L)).thenReturn(Optional.of(testPost));
        when(userService.findByID(1L)).thenReturn(testUser);
        when(commentDao.create(testUser, testPost, "Comment content")).thenReturn(comment);
        doNothing().when(emailService).sendCommentNotificationEmail(anyString(), anyString(), anyString(), anyLong());

        final Comment result = postService.createComment(1L, 1L, "Comment content");

        Assert.assertNotNull(result);
        verify(commentDao, times(1)).create(testUser, testPost, "Comment content");
        verify(emailService, times(1)).sendCommentNotificationEmail(anyString(), anyString(), anyString(), anyLong());
    }

    @Test
    public void testDeleteComment() {
        final Comment comment = new Comment(testUser, testPost, "Test comment");
        // Comment ID is set by the database
        when(commentDao.findByID(1L)).thenReturn(Optional.of(comment));
        doNothing().when(commentDao).delete(comment);

        postService.deleteComment(1L);

        verify(commentDao, times(1)).delete(comment);
    }

    @Test
    public void testIsPostLikedByUser() {
        when(postDao.findByID(1L)).thenReturn(Optional.of(testPost));
        when(userService.findByID(1L)).thenReturn(testUser);
        when(likeDao.isPostLikedByUser(testUser, testPost)).thenReturn(true);

        final boolean result = postService.isPostLikedByUser(1L, 1L);

        Assert.assertTrue(result);
        verify(likeDao, times(1)).isPostLikedByUser(testUser, testPost);
    }

    @Test
    public void testFindPopularPostsForInstitution() {
        when(institutionDao.findByID(1L)).thenReturn(Optional.of(testInstitution));
        when(postDao.findPopularPostsForInstitution(testInstitution, OrderCriteria.getDefault(), 10))
                .thenReturn(Arrays.asList(testPost));

        final List<Post> result = postService.findPopularPostsForInstitution(1L, OrderCriteria.getDefault(), 10);

        Assert.assertEquals(1, result.size());
        verify(postDao, times(1)).findPopularPostsForInstitution(testInstitution, OrderCriteria.getDefault(), 10);
    }

    @Test
    public void testGetOrganizationIDByID() {
        when(postDao.findByID(1L)).thenReturn(Optional.of(testPost));

        final long result = postService.getOrganizationIDByID(1L);

        Assert.assertEquals(1L, result);
    }
}

