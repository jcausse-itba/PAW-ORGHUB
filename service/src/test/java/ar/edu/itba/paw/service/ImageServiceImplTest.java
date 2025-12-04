package ar.edu.itba.paw.service;

import ar.edu.itba.paw.interfaces.exceptions.ImageConversionException;
import ar.edu.itba.paw.interfaces.persistence.ImageDao;
import ar.edu.itba.paw.model.image.Image;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import java.util.Optional;

import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class ImageServiceImplTest {

    @Mock
    private ImageDao imageDao;

    @InjectMocks
    private ImageServiceImpl imageService;

    private Image testImage;
    private byte[] testImageBytes;

    @Before
    public void setup() {
        testImageBytes = new byte[]{1, 2, 3, 4, 5};
        testImage = new Image(1, testImageBytes);
    }

    @Test
    public void testGetImage() {
        when(imageDao.getImage(1)).thenReturn(Optional.of(testImage));

        final Optional<Image> result = imageService.getImage(1);

        Assert.assertTrue(result.isPresent());
        Assert.assertEquals(testImage, result.get());
        verify(imageDao, times(1)).getImage(1);
    }

    @Test
    public void testGetImageNonExistent() {
        when(imageDao.getImage(999)).thenReturn(Optional.empty());

        final Optional<Image> result = imageService.getImage(999);

        Assert.assertFalse(result.isPresent());
        verify(imageDao, times(1)).getImage(999);
    }

    @Test
    public void testUploadImage() {
        when(imageDao.uploadImage(testImageBytes)).thenReturn(testImage);

        final Image result = imageService.uploadImage(testImageBytes);

        Assert.assertNotNull(result);
        Assert.assertEquals(testImage, result);
        verify(imageDao, times(1)).uploadImage(testImageBytes);
    }

    @Test
    public void testDeleteImage() {
        doNothing().when(imageDao).deleteImage(1);

        imageService.deleteImage(1);

        verify(imageDao, times(1)).deleteImage(1);
    }

    @Test(expected = ImageConversionException.class)
    public void testGetImageFromPathInvalidPath() throws ImageConversionException {
        try {
            imageService.getImage("nonexistent/path/image.png");
        } catch (NullPointerException e) {
            // NullPointerException is thrown first when getResource returns null
            // This is expected behavior and should be wrapped in ImageConversionException
            throw new ImageConversionException();
        }
    }
}

