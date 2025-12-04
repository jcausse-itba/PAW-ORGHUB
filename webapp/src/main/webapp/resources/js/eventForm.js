import { setError, clearError, clearAll } from './utils.js';

document.addEventListener('DOMContentLoaded', () => {
    const descriptionField = document.getElementById('event_description');
    if (descriptionField) {
        descriptionField.value = descriptionField.value.trim();
    }

    function hookFilePreview(inputID) {
        const file = document.getElementById(inputID);
        const preview = document.getElementById(inputID + '_preview');
        if (!file || !preview) return;
        file.addEventListener('change', function () {
            const f = this.files && this.files[0];
            if (!f) {
                preview.style.display = 'none';
                preview.src = '';
                return;
            }
            if (!/^image\/(png|jpe?g)$/i.test(f.type)) {
                setError(this, window.i18n.validation.event.imageFormat || 'Formato de imagen inválido. Solo se permiten PNG y JPEG.');
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

    hookFilePreview('event_flyer');
    document.querySelectorAll('.datetime-picker-wrapper').forEach(wrapper => {
        const input = wrapper.querySelector('.datetime-picker-input');
        const display = wrapper.querySelector('.datetime-text');

        wrapper.addEventListener('click', () => {
            if (input.showPicker) {
                input.showPicker();
            } else {
                input.click();
            }
        });

        const updateDisplay = () => {
            if (input.value) {
                const date = new Date(input.value);
                const options = {
                    year: 'numeric',
                    month: 'long',
                    day: 'numeric',
                    hour: '2-digit',
                    minute: '2-digit'
                };
                display.textContent = date.toLocaleDateString('es-ES', options);
            } else {
                display.textContent = window.i18n.datePicker.select;
            }
        };

        input.addEventListener('change', updateDisplay);
        
        // Initialize display on page load if value exists
        updateDisplay();
    });

    document.addEventListener("click", function (event) {
        const dropdowns = document.querySelectorAll(".ms__details");

        dropdowns.forEach(function (dropdown) {
            if (!dropdown.contains(event.target)) {
                dropdown.removeAttribute("open");
            }
        });
    });
    const tagPickers = document.querySelectorAll('.ms');

    tagPickers.forEach(picker => {
        const checkboxes = picker.querySelectorAll('input[type="checkbox"]');
        const previewContainer = picker.parentNode.querySelector('.tags-preview');
        const maxTags = 3;

        const updatePreview = () => {
            const checkedCount = picker.querySelectorAll('input[type="checkbox"]:checked').length;

            previewContainer.innerHTML = '';
            const limitMessage = document.getElementById('tag-limit-message-' + picker.id);

            checkboxes.forEach(checkbox => {
                if (checkbox.checked) {
                    const tagName = checkbox.parentNode.querySelector('.ms__label').textContent;

                    const tagChip = document.createElement('div');
                    tagChip.className = 'badge';
                    tagChip.textContent = tagName;
                    previewContainer.appendChild(tagChip);
                }
            });

            if (checkedCount >= maxTags) {
                checkboxes.forEach(checkbox => {
                    if (!checkbox.checked) {
                        checkbox.disabled = true;
                    }
                });
                if (limitMessage) {
                    limitMessage.style.display = 'block';
                }
            } else {
                checkboxes.forEach(checkbox => {
                    checkbox.disabled = false;
                });
                if (limitMessage) {
                    limitMessage.style.display = 'none';
                }
            }
        };

        checkboxes.forEach(checkbox => {
            checkbox.addEventListener('change', updatePreview);
        });

        updatePreview();
    });

    const form = document.getElementById('eventForm');
    if (!form) return;

    form.addEventListener('submit', function (e) {
        clearAll(form);

        const title = document.getElementById('event_title');
        const description = document.getElementById('event_description');
        const location = document.getElementById('event_location');
        const eventDate = document.getElementById('event_date');

        let hasError = false;

        if (!title.value.trim()) {
            setError(title, window.i18n.validation.event.titleRequired);
            hasError = true;
        } else if (title.value.trim().length < 5 || title.value.trim().length > 100) {
            setError(title, window.i18n.validation.event.titleLength);
            hasError = true;
        }

        if (!description.value.trim()) {
            setError(description, window.i18n.validation.event.descriptionRequired);
            hasError = true;
        } else if (description.value.trim().length > 500) {
            setError(description, window.i18n.validation.event.descriptionMax);
            hasError = true;
        }

        if (!location.value.trim()) {
            setError(location, window.i18n.validation.event.locationRequired);
            hasError = true;
        } else if (location.value.trim().length > 100) {
            setError(location, window.i18n.validation.event.locationMax);
            hasError = true;
        }

        if (!eventDate.value) {
            setError(eventDate, window.i18n.validation.event.dateRequired);
            hasError = true;
        } else {
            const now = new Date();
            const selectedDate = new Date(eventDate.value);
            if (selectedDate <= now) {
                setError(eventDate, window.i18n.validation.event.dateFuture);
                hasError = true;
            }
        }

        if (hasError) {
            e.preventDefault();
            return;
        }

        const submitBtn = form.querySelector('button[type="submit"], input[type="submit"]');
        if (submitBtn) submitBtn.disabled = true;

        form.submit();
    });
});