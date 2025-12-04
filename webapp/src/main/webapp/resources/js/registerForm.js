import { setError, clearError, clearAll } from './utils.js';

(function () {
    const form = document.getElementById('registerForm');
    const emailInput = document.getElementById('register_email');
    const passwordInput = document.getElementById('register_password');
    const confirmPasswordInput = document.getElementById('register_confirmPassword');
    const firstNameInput = document.getElementById('register_firstName');
    const lastNameInput = document.getElementById('register_lastName');

    function validate() {
        let isValid = true;
        clearAll(form);

        // Validar Email
        if (emailInput.value.trim() === '') {
            setError(emailInput, window.i18n.validation.register.emailRequired);
            isValid = false;
        } else if (!isValidEmail(emailInput.value)) {
            setError(emailInput, window.i18n.validation.register.emailInvalid);
            isValid = false;
        } else if (emailInput.value.length > 50) {
            setError(emailInput, window.i18n.validation.register.emailMax);
            isValid = false;
        }

        if (passwordInput.value.trim() === '') {
            setError(passwordInput, window.i18n.validation.register.passwordRequired);
            isValid = false;
        } else if (passwordInput.value.length < 8 || passwordInput.value.length > 50) {
            setError(passwordInput, window.i18n.validation.register.passwordLength);
            isValid = false;
        }

        if (confirmPasswordInput.value.trim() === '') {
            setError(confirmPasswordInput, window.i18n.validation.register.confirmPasswordRequired);
            isValid = false;
        } else if (passwordInput.value !== confirmPasswordInput.value) {
            setError(confirmPasswordInput, window.i18n.validation.register.confirmPasswordMatch);
            isValid = false;
        }

        if (firstNameInput.value.trim() === '') {
            setError(firstNameInput, window.i18n.validation.register.firstNameRequired);
            isValid = false;
        } else if (firstNameInput.value.length < 3 || firstNameInput.value.length > 50) {
            setError(firstNameInput, window.i18n.validation.register.firstNameLength);
            isValid = false;
        }

        if (lastNameInput.value.trim() === '') {
            setError(lastNameInput, window.i18n.validation.register.lastNameRequired);
            isValid = false;
        } else if (lastNameInput.value.length > 50) {
            setError(lastNameInput, window.i18n.validation.register.lastNameMax);
            isValid = false;
        }

        return isValid;
    }

    form.addEventListener('submit', function (event) {
        if (!validate()) {
            event.preventDefault();
        } else {
        const submitBtn = form.querySelector('button[type="submit"], input[type="submit"]');
        if (submitBtn) {
            submitBtn.disabled = true;
        }
    }
    });

    [emailInput, passwordInput, confirmPasswordInput, firstNameInput, lastNameInput].forEach(input => {
        input.addEventListener('input', () => {
            clearError(input);
        });
    });

})();


