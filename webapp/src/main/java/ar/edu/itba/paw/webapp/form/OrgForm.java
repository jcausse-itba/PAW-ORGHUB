package ar.edu.itba.paw.webapp.form;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.*;

import ar.edu.itba.paw.webapp.form.validators.EmailDomainNameMustMatchUserEmailDomainName;
import ar.edu.itba.paw.webapp.form.validators.OrganizationWithEmailMustNotExist;

//import org.springframework.web.multipart.MultipartFile;

import ar.edu.itba.paw.webapp.form.validators.UserWithEmailMustExist;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.validator.constraints.URL;

public class OrgForm {

    @Getter @Setter
    @NotBlank(message = "{validation.org.name.required}")
    @Size(min = 3, max = 50, message = "{validation.org.name.length}")
    private String orgName;

    @Getter @Setter
    @NotBlank(message = "{validation.org.email.required}")
    @Size(max = 50, message = "{validation.org.email.max}")
    @Email(message = "{validation.org.email.invalid}")
    @OrganizationWithEmailMustNotExist
    @EmailDomainNameMustMatchUserEmailDomainName
    private String orgEmail;

    @Getter @Setter
    @NotBlank(message = "{validation.org.manager.email.required}")
    @Size(max = 50, message = "{validation.org.manager.email.max}")
    @Email(message = "{validation.org.manager.email.invalid}")
    @UserWithEmailMustExist
    @EmailDomainNameMustMatchUserEmailDomainName
    private String managerEmail;

    @Getter @Setter
    @NotBlank(message = "{validation.org.description.required}")
    @Size(min = 10, max = 500, message = "{validation.org.description.length}")
    private String description;

    @Getter @Setter
    @Size(max = 150, message = "{validation.org.instagram.max}")
    @URL(message = "{validation.org.instagram.url}")
    private String socialLink1;


    @Getter @Setter
    @Size(max = 150, message = "{validation.org.twitter.max}")
    @URL(message = "{validation.org.twitter.url}")
    private String socialLink2;

    public OrgForm() {
        orgName = "";
        orgEmail = "";
        description = "";
        socialLink1 = "";
        socialLink2 = "";
    }
}
