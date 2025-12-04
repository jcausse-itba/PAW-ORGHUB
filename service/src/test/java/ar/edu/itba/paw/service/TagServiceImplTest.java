package ar.edu.itba.paw.service;

import ar.edu.itba.paw.interfaces.persistence.TagDao;
import ar.edu.itba.paw.interfaces.service.exception.TagNameExistsException;
import ar.edu.itba.paw.model.Tag;
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

import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class TagServiceImplTest {

    @Mock
    private TagDao tagDao;

    @InjectMocks
    private TagServiceImpl tagService;

    private Tag testTag;

    @Before
    public void setup() {
        testTag = new Tag("TestTag");
        // Tag ID is set by the database, not manually in tests
    }

    @Test
    public void testFindByID() {
        when(tagDao.findByID(1L)).thenReturn(Optional.of(testTag));

        final Optional<Tag> result = tagService.findByID(1L);

        Assert.assertTrue(result.isPresent());
        Assert.assertEquals(testTag, result.get());
        verify(tagDao, times(1)).findByID(1L);
    }

    @Test
    public void testFindByIDNonExistent() {
        when(tagDao.findByID(999L)).thenReturn(Optional.empty());

        final Optional<Tag> result = tagService.findByID(999L);

        Assert.assertFalse(result.isPresent());
        verify(tagDao, times(1)).findByID(999L);
    }

    @Test
    public void testFindByName() {
        when(tagDao.findByName("TestTag")).thenReturn(Optional.of(testTag));

        final Optional<Tag> result = tagService.findByName("TestTag");

        Assert.assertTrue(result.isPresent());
        Assert.assertEquals(testTag, result.get());
        verify(tagDao, times(1)).findByName("TestTag");
    }

    @Test
    public void testFindByNameNonExistent() {
        when(tagDao.findByName("NonExistent")).thenReturn(Optional.empty());

        final Optional<Tag> result = tagService.findByName("NonExistent");

        Assert.assertFalse(result.isPresent());
        verify(tagDao, times(1)).findByName("NonExistent");
    }

    @Test
    public void testFindMultipleTagsByName() {
        final Tag tag1 = new Tag("Tag1");
        final Tag tag2 = new Tag("Tag2");

        when(tagDao.findByName("tag1")).thenReturn(Optional.of(tag1));
        when(tagDao.findByName("tag2")).thenReturn(Optional.of(tag2));
        when(tagDao.findByName("nonexistent")).thenReturn(Optional.empty());

        final List<Tag> result = tagService.findMultipleTagsByName(Arrays.asList("Tag1", "Tag2", "NonExistent", null, ""));

        Assert.assertEquals(2, result.size());
        Assert.assertTrue(result.contains(tag1));
        Assert.assertTrue(result.contains(tag2));
    }

    @Test
    public void testFindMultipleTagsByNameEmptyList() {
        final List<Tag> result = tagService.findMultipleTagsByName(Arrays.asList());

        Assert.assertTrue(result.isEmpty());
        verify(tagDao, never()).findByName(anyString());
    }

    @Test
    public void testFindMultipleTagsByID() {
        final Tag tag1 = new Tag("Tag1");
        final Tag tag2 = new Tag("Tag2");

        when(tagDao.findByID(1L)).thenReturn(Optional.of(tag1));
        when(tagDao.findByID(2L)).thenReturn(Optional.of(tag2));
        when(tagDao.findByID(999L)).thenReturn(Optional.empty());

        final List<Tag> result = tagService.findMultipleTagsByID(Arrays.asList(1L, 2L, 999L));

        Assert.assertEquals(2, result.size());
        Assert.assertTrue(result.contains(tag1));
        Assert.assertTrue(result.contains(tag2));
    }

    @Test
    public void testFindAll() {
        final Tag tag1 = new Tag("Tag1");
        final Tag tag2 = new Tag("Tag2");
        when(tagDao.findAll()).thenReturn(Arrays.asList(tag1, tag2));

        final List<Tag> result = tagService.findAll();

        Assert.assertEquals(2, result.size());
        verify(tagDao, times(1)).findAll();
    }

    @Test
    public void testCreate() {
        when(tagDao.existsTagName("NewTag")).thenReturn(false);
        when(tagDao.create("NewTag")).thenReturn(testTag);

        final Tag result = tagService.create("NewTag");

        Assert.assertNotNull(result);
        Assert.assertEquals(testTag, result);
        verify(tagDao, times(1)).existsTagName("NewTag");
        verify(tagDao, times(1)).create("NewTag");
    }

    @Test(expected = TagNameExistsException.class)
    public void testCreateWithExistingName() {
        when(tagDao.existsTagName("ExistingTag")).thenReturn(true);

        tagService.create("ExistingTag");

        verify(tagDao, times(1)).existsTagName("ExistingTag");
        verify(tagDao, never()).create(anyString());
    }
}

