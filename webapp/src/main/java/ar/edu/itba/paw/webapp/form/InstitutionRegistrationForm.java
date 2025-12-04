package ar.edu.itba.paw.webapp.form;

import ar.edu.itba.paw.webapp.form.validators.DomainNameMustNotExist;
import ar.edu.itba.paw.webapp.form.validators.EmailMatchesDomainName;
import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;

@EmailMatchesDomainName
public class InstitutionRegistrationForm implements EmailMatchesDomainNameConstraint {
    @NotBlank(message = "{validation.institution.name.required}")
    @Setter @Getter private String name;

    @NotBlank(message = "{validation.institution.domainName.required}")
    @DomainNameMustNotExist
    @Setter private String domainName;

    @NotBlank(message = "{validation.institution.adminEmail.required}")
    @Email(message = "{validation.org.email.invalid}")
    @Setter @Getter private String adminEmail;

    @Override
    public String getEmail() {
        return adminEmail;
    }

    @Override
    public String getDomainName() {
        return domainName;
    }

    @Override
    public String getEmailPropertyName() {
        return "adminEmail";
    }
}
