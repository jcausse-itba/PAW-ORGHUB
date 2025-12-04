package ar.edu.itba.paw.webapp.controller;

import ar.edu.itba.paw.interfaces.service.ImageService;
import ar.edu.itba.paw.model.image.Image;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.Optional;

@RequiredArgsConstructor
@Controller
public class ImageController {
    private static final Logger LOGGER = LoggerFactory.getLogger(ImageController.class);

    private final ImageService imageService;

    @GetMapping("/image/{imageID}")
    @PreAuthorize("@webPermissionService.canAccessImage(authentication, #imageID)")
    public ResponseEntity<byte[]> getImage(@PathVariable("imageID") int imageID) {
        Optional<Image> imageOpt = imageService.getImage(imageID);
        
        if (imageOpt.isEmpty()) {
            LOGGER.debug("Image with ID {} not found.", imageID);
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
        
        Image image = imageOpt.get();
        byte[] imageBytes = image.getImageBlob();
        
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.IMAGE_JPEG);
        headers.setContentLength(imageBytes.length);
        
        return new ResponseEntity<>(imageBytes, headers, HttpStatus.OK);
    }
}

