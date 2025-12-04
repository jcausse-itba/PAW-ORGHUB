# Validators

---

### @DomainNameMustNotExist
Given a Domain Name, verifies that no Institution has said Domain Name associated to it.

---

### @EmailDomainNameMustExist
Given an Email Address, verifies that the Domain Name in it belongs to a registered Institution.

---

### @EmailDomainNameMustMatchUserEmailDomainName
Given an Email Address, verifies that the Domain Name in it matches the one on the currently logged-in User's Email Address.
This is done to prevent Institution Administrators from creating Organizations using Domain Names from other Institutions.

---

### @EmailMatchesDomainName
Given an InstitutionForm, verifies that the Domain Name in it and the Domain Name that is part of the Email of the form match.

---

### @OrganizationWithEmailMustNotExist
Given an Email Address, verifies that no Organization is associated to it.

---

### @PasswordMatches
Given a RegisterForm, verifies that the Password and the Confirm Password match.

---

### @UserWithEmailMustExist
Given an Email Address, verifies that there exists a User for that Email Address.

---

### @UserWithEmailMustNotExist
Given an Email Address, verifies that no User is associated to that Email Address.
