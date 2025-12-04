import { setError, clearError, isValidEmail, isValidUrl } from './utils.js';

document.addEventListener('DOMContentLoaded', () => {
    const orgDescription = document.getElementById('org_desc');
    if (orgDescription) {
        orgDescription.value = orgDescription.value.trim();
    }
    const file = document.getElementById('org_logo');
    const preview = document.getElementById('org_logo_preview');
    if (file && preview) {
        file.addEventListener('change', function () {
            const f = this.files && this.files[0];
            if (!f) {
                preview.style.display = 'none';
                preview.src = '';
                return;
            }
            if (!/^image\/(png|jpe?g)$/i.test(f.type)) {
                setError(this, window.i18n.validation.org.logoFormat);
                this.value = '';
                preview.style.display = 'none';
                return;
            }
            clearError(this);
            const reader = new FileReader();
            reader.onload = e => {
                preview.src = e.target.result;
                preview.style.display = 'inline-block';
            };
            reader.readAsDataURL(f);
        });
    }

    const form = document.getElementById('orgForm');
    if (!form) return;

    form.addEventListener('input', function (e) {
        const el = e.target;
        if (el.matches('.input, input, textarea, select')) {
            clearError(el);
        }
    });

    form.addEventListener('submit', function (e) {
        const name = document.getElementById('org_name');
        const desc = document.getElementById('org_desc');
        const email = document.getElementById('org_email');
        const managerEmail = document.getElementById('manager_email');
        const ig = document.getElementById('org_ig');
        const tw = document.getElementById('org_tw');

        [name, desc, email, managerEmail, ig, tw].forEach(clearError);

        let hasError = false;

        if (!name.value.trim()) {
            setError(name, window.i18n.validation.org.nameRequired);
            hasError = true;
        } else if (name.value.trim().length < 3) {
            setError(name, window.i18n.validation.org.nameMin);
            hasError = true;
        } else if (name.value.trim().length > 50) {
            setError(name, window.i18n.validation.org.nameMax);
            hasError = true;
        }

        if (!email.value.trim()) {
            setError(email, window.i18n.validation.org.emailRequired);
            hasError = true;
        } else if (!isValidEmail(email.value)) {
            setError(email, window.i18n.validation.org.emailInvalid);
            hasError = true;
        } else if (email.value.trim().length > 50) {
            setError(email, window.i18n.validation.org.emailMax);
            hasError = true;
        }

        if (!managerEmail.value.trim()) {
            setError(managerEmail, window.i18n.validation.org.managerEmailRequired);
            hasError = true;
        } else if (!isValidEmail(managerEmail.value)) {
            setError(managerEmail, window.i18n.validation.org.managerEmailInvalid);
            hasError = true;
        } else if (managerEmail.value.trim().length > 50) {
            setError(managerEmail, window.i18n.validation.org.managerEmailMax);
            hasError = true;
        }

        if (!desc.value.trim()) {
            setError(desc, window.i18n.validation.org.descriptionRequired);
            hasError = true;
        } else if (desc.value.trim().length < 10) {
            setError(desc, window.i18n.validation.org.descriptionMin);
            hasError = true;
        } else if (desc.value.trim().length > 500) {
            setError(desc, window.i18n.validation.org.descriptionMax);
            hasError = true;
        }

        if (ig.value.trim()) {
            if (!isValidUrl(ig.value)) {
                setError(ig, window.i18n.validation.org.instagramUrl);
                hasError = true;
            } else if (ig.value.trim().length > 150) {
                setError(ig, window.i18n.validation.org.instagramMax);
                hasError = true;
            }
        }

        if (tw.value.trim()) {
            if (!isValidUrl(tw.value)) {
                setError(tw, window.i18n.validation.org.twitterUrl);
                hasError = true;
            } else if (tw.value.trim().length > 150) {
                setError(tw, window.i18n.validation.org.twitterMax);
                hasError = true;
            }
        }

        if (hasError){
            e.preventDefault();
            return;
        }
        const submitBtn = form.querySelector('button[type="submit"], input[type="submit"]');
        if (submitBtn) submitBtn.disabled = true;

        form.submit();
    });
});