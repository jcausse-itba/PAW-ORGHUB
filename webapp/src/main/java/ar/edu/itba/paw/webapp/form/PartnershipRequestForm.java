package ar.edu.itba.paw.webapp.form;

import ar.edu.itba.paw.webapp.form.validators.DomainNameMustNotExist;
import ar.edu.itba.paw.webapp.form.validators.EmailMatchesDomainName;
import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;

@Setter
@Getter
@EmailMatchesDomainName
public class PartnershipRequestForm implements EmailMatchesDomainNameConstraint {
    @NotBlank(message = "{validation.partnership.name.required}")
    private String requestedName;

    @NotBlank(message = "{validation.partnership.domainName.required}")
    @DomainNameMustNotExist
    private String requestedDomainName;

    @NotBlank(message = "{validation.partnership.adminEmail.required}")
    @Email(message = "{validation.org.email.invalid}")
    private String requestedAdminEmail;

//    @NotBlank(message = "El email de contacto es obligatorio.")
//    @Email
//    private String contactEmail;

    @Override
    public String getEmail() {
        return requestedAdminEmail;
    }

    @Override
    public String getDomainName() {
        return requestedDomainName;
    }

    @Override
    public String getEmailPropertyName() {
        return "requestedAdminEmail";
    }
}
