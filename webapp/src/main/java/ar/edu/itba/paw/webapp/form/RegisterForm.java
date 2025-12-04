package ar.edu.itba.paw.webapp.form;

import ar.edu.itba.paw.webapp.form.validators.EmailDomainNameMustExist;
import ar.edu.itba.paw.webapp.form.validators.UserWithEmailMustNotExist;
import ar.edu.itba.paw.webapp.form.validators.PasswordMatches;
import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

@Getter
@Setter
@PasswordMatches
public class RegisterForm {
    @NotBlank(message = "{validation.register.email.required}")
    @Email(message = "{validation.register.email.invalid}")
    @Size(max = 99, message = "{validation.register.email.max}")
    @UserWithEmailMustNotExist
    @EmailDomainNameMustExist
    private String email;

    @NotBlank(message = "{validation.register.password.required}")
    @Size(min = 8, max = 50, message = "{validation.register.password.length}")
    private String password;

    @NotBlank(message = "{validation.register.confirmPassword.required}")
    private String confirmPassword;

    @NotBlank(message = "{validation.register.firstName.required}")
    @Size(min = 3, max = 50, message = "{validation.register.firstName.length}")
    private String firstName;

    @NotBlank(message = "{validation.register.lastName.required}")
    @Size(max = 50, message = "{validation.register.lastName.max}")
    private String lastName;

    private boolean rememberMe;
}
