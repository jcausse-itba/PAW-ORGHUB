package ar.edu.itba.paw.webapp.form;


import lombok.Getter;
import lombok.Setter;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.multipart.MultipartFile;

import javax.validation.constraints.Future;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.time.LocalDateTime;
import java.util.List;

@Getter @Setter
public class EventForm {

    @NotBlank(message = "{validation.event.title.required}")
    @Size(min = 5, max = 100, message = "{validation.event.title.length}")
    private String title;

    @NotBlank(message = "{validation.event.description.required}")
    @Size(max = 500, message = "{validation.event.description.max}")
    private String description;

    @NotBlank(message = "{validation.event.location.required}")
    @Size(max = 100, message = "{validation.event.location.max}")
    private String location;

    @NotNull(message = "{validation.event.date.required}")
    @DateTimeFormat(pattern = "yyyy-MM-dd'T'HH:mm")
    @Future(message = "{validation.event.date.future}")
    private LocalDateTime eventDate;

    @Size(max = 3, message = "{validation.tags.max}")
    private List <Long> tags;

    private MultipartFile flyer;

    public EventForm() {
        title = "";
        description = "";
        location="";
        eventDate= null;
        tags= List.of();
    }
}
