// error helpers
export function setError(input, msg) {
    const row = input.closest('.form-row');
    if (!row) return;
    row.querySelectorAll('.form-error').forEach(n => n.remove());
    input.classList.add('input--error');
    const div = document.createElement('div');
    div.className = 'form-error';
    div.textContent = msg;
    row.appendChild(div);
}

export function clearError(input) {
    const row = input.closest('.form-row');
    if (!row) return;
    input.classList.remove('input--error');
    row.querySelectorAll('.form-error').forEach(n => n.remove());
}

export function clearAll(container) {
    container.querySelectorAll('.input--error').forEach(el => el.classList.remove('input--error'));
    container.querySelectorAll('.form-error').forEach(el => el.remove());
}

// validation helpers
export function isValidEmail(v) {
    return /^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\.[a-zA-Z]{2,6}$/.test(v);
}

export function isValidUrl(str) {
    try {
        new URL(str);
        return true;
    } catch (_) {
        return false;
    }
}