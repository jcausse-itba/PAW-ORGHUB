import {setError, clearError, clearAll, isValidEmail} from './utils.js';

function openModal(modal) {
    clearAll(modal);
    
    document.body.appendChild(modal);
    
    modal.classList.add('is-open');
    modal.setAttribute('aria-hidden', 'false');
    document.body.style.overflow = 'hidden';
    requestAnimationFrame(() => {
        const first = modal.querySelector('input,textarea,button');
        if (first) first.focus();
    });
}

function closeModal(modal) {
    modal.classList.remove('is-open');
    modal.setAttribute('aria-hidden', 'true');
    const form = modal.querySelector('form');
    if (form) {
        form.reset();
        form.querySelector('[type="submit"]')?.removeAttribute('disabled');
    }
    clearAll(modal);
    document.body.style.overflow = '';
}

document.addEventListener('click', (e) => {
    const opener = e.target.closest('[data-modal-target]');
    if (opener) {
        e.preventDefault();
        const selector = opener.getAttribute('data-modal-target');
        const modal = document.querySelector(selector);
        if (modal) openModal(modal);
        return;
    }

    const closer = e.target.closest('[data-close]');
    if (closer) {
        const modal = closer.closest('.modal');
        if (modal) closeModal(modal);
        return;
    }

    if (e.target.classList?.contains('modal')) closeModal(e.target);
});

document.addEventListener('keydown', (e) => {
    if (e.key === 'Escape') {
        document.querySelectorAll('.modal.is-open').forEach(closeModal);
    }
});

document.addEventListener('input', (e) => {
    const form = e.target.closest('.js-comment-form');
    if (!form) return;
    if (e.target.matches('input, textarea')) clearError(e.target);
});

document.addEventListener('submit', (e) => {
    const form = e.target.closest('form');
    if (!form) return;
    if (!form.classList.contains('js-comment-form')) return;
    clearAll(form);

    let hasError = false;

    if (form.classList.contains('js-comment-form')) {
        const content = form.querySelector('[name="content"]');

        if (!content.value.trim()) {
            setError(content, window.i18n.validation.comment.empty);
            hasError = true;
        } else if (content.value.trim().length < 3) {
            setError(content, window.i18n.validation.comment.min);
            hasError = true;
        } else if (content.value.trim().length > 500) {
            setError(content, window.i18n.validation.comment.max);
            hasError = true;
        }
    }

    if (hasError) {
        e.preventDefault();
        return;
    }
    form.querySelector('[type="submit"]')?.setAttribute('disabled', 'disabled');
});

function resetAllModalsAndButtons() {
    document.querySelectorAll('.modal').forEach(m => {
        m.classList.remove('is-open');
        m.setAttribute('aria-hidden', 'true');
        const f = m.querySelector('form');
        if (f) {
            f.reset();
            f.querySelector('[type="submit"]')?.removeAttribute('disabled');
        }
        clearAll(m);
    });
    document.body.style.overflow = '';
}
window.addEventListener('DOMContentLoaded', resetAllModalsAndButtons);
window.addEventListener('pageshow', resetAllModalsAndButtons);