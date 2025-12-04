import {setError, clearAll, isValidEmail} from './utils.js';

document.addEventListener('DOMContentLoaded', () => {
    document.querySelectorAll('[data-scroll-to-comment]').forEach(btn => {
        btn.addEventListener('click', (e) => {
            e.preventDefault();
            const target = document.getElementById('comment-box');
            if (target) {
                target.scrollIntoView({ behavior: 'smooth' });
            }
        });
    });

    document.addEventListener('submit', (e) => {
        const form = e.target.closest('form');
        if (!form || !form.classList.contains('js-comment-form')) return;

        clearAll(form);

        let hasError = false;

        const content = form.querySelector('[name="content"]');

        if (!content.value.trim()) {
            setError(content, window.i18n.validation.comment.required);
            hasError = true;
        } else if (content.value.trim().length < 3) {
            setError(content, window.i18n.validation.comment.min);
            hasError = true;
        } else if (content.value.trim().length > 500) {
            setError(content, window.i18n.validation.comment.max);
            hasError = true;
        }

        if (hasError) {
            e.preventDefault();
            return;
        }

        form.querySelector('[type="submit"]')?.setAttribute('disabled', 'disabled');
    });
});
