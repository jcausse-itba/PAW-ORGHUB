import { setError, clearError, clearAll } from './utils.js';

document.addEventListener('DOMContentLoaded', () => {
    const postField = document.getElementById('post_body');
    if (postField) {
        postField.value = postField.value.trim();
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
                setError(this, window.i18n.validation.post.imageFormat);
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

    hookFilePreview('post_image');

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

    const form = document.getElementById('postForm');
    if (form) {
        form.addEventListener('input', (e) => {
            const el = e.target;
            if (el.matches('.input, input, textarea, select')) clearError(el);
        });

        form.addEventListener('submit', function (e) {
            clearAll(form);

            const title = document.getElementById('post_title');
            const body = document.getElementById('post_body');

            let hasError = false;

            if (!title.value.trim()) {
                setError(title, window.i18n.validation.post.titleRequired);
                hasError = true;
            } else if (title.value.trim().length < 3) {
                setError(title, window.i18n.validation.post.titleMin);
                hasError = true;
            } else if (title.value.trim().length > 150) {
                setError(title, window.i18n.validation.post.titleMax);
                hasError = true;
            }

            if (!body.value.trim()) {
                setError(body, window.i18n.validation.post.bodyRequired);
                hasError = true;
            } else if (body.value.trim().length < 10) {
                setError(body, window.i18n.validation.post.bodyMin);
                hasError = true;
            } else if (body.value.trim().length > 5000) {
                setError(body, window.i18n.validation.post.bodyMax);
                hasError = true;
            }

            if (hasError) {
                e.preventDefault();
                return;
            }
            const submitBtn = form.querySelector('button[type="submit"], input[type="submit"]');
            if (submitBtn) submitBtn.disabled = true;

            form.submit();
        });
    }
})