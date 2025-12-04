import { setError, clearError, clearAll } from './utils.js';

document.addEventListener('DOMContentLoaded', () => {
    setTimeout(() => {
        const tagPickers = document.querySelectorAll('.ms');

        tagPickers.forEach(picker => {
            const checkboxes = picker.querySelectorAll('input[type="checkbox"]');
            
            if (checkboxes.length === 0) return;
            
            const formRow = picker.closest('.form-row');
            const previewContainer = formRow ? formRow.querySelector('.tags-preview') : null;
            
            if (!previewContainer) {
                console.warn('No se encontró .tags-preview para el picker:', picker.id);
                return;
            }
            
            const maxTags = 3;

        const updatePreview = () => {
            const checkedCount = picker.querySelectorAll('input[type="checkbox"]:checked').length;

            if (previewContainer) {
                previewContainer.innerHTML = '';
            }
            const limitMessage = document.getElementById('tag-limit-message-' + picker.id);

            checkboxes.forEach(checkbox => {
                if (checkbox.checked && previewContainer) {
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
    }, 100);

    document.addEventListener("click", function (event) {
        const dropdowns = document.querySelectorAll(".ms__details");

        dropdowns.forEach(function (dropdown) {
            if (!dropdown.contains(event.target)) {
                dropdown.removeAttribute("open");
                const picker = dropdown.closest('.ms');
                if (picker) {
                    picker.classList.remove('ms--open');
                }
            }
        });
    });

    const form = document.getElementById('openPositionForm');
    if (form) {
        form.addEventListener('input', (e) => {
            const el = e.target;
            if (el.matches('.input, input, textarea, select')) {
                clearError(el);
                if (el.name === 'questions') {
                    clearError(el);
                }
            }
        });

        form.addEventListener('submit', function (e) {
            clearAll(form);

            const title = document.getElementById('position_title');
            const description = document.getElementById('position_description');
            const requirements = document.getElementById('position_requirements');

            let hasError = false;

            if (!title.value.trim()) {
                setError(title, window.i18n.validation.position.titleRequired);
                hasError = true;
            } else if (title.value.trim().length < 5) {
                setError(title, window.i18n.validation.position.titleMin);
                hasError = true;
            } else if (title.value.trim().length > 150) {
                setError(title, window.i18n.validation.position.titleMax);
                hasError = true;
            }

            if (!description.value.trim()) {
                setError(description, window.i18n.validation.position.descriptionRequired);
                hasError = true;
            } else if (description.value.trim().length > 5000) {
                setError(description, window.i18n.validation.position.descriptionMax);
                hasError = true;
            }

            if (!requirements.value.trim()) {
                setError(requirements, window.i18n.validation.position.requirementsRequired);
                hasError = true;
            } else if (requirements.value.trim().length > 5000) {
                setError(requirements, window.i18n.validation.position.requirementsMax);
                hasError = true;
            }

            const questionInputs = document.querySelectorAll('#questions-container input[name="questions"]');

            const firstQuestion = questionInputs[0];
            const isFirstEmpty = firstQuestion && !firstQuestion.value.trim();

            if (isFirstEmpty) {
                questionInputs.forEach((input, index) => {
                    if (input.value.trim()) {
                        setError(input, window.i18n.validation.position.questionsAllEmpty);
                        hasError = true;
                    }
                });
            } else {
                questionInputs.forEach((input, index) => {
                    if (!input.value.trim()) {
                        const msg = window.i18n.validation.position.questionsEmpty.replace('{0}', index + 1);
                        setError(input, msg);
                        hasError = true;
                    } else if (input.value.trim().length > 500) {
                        const msg = window.i18n.validation.position.questionsMax.replace('{0}', index + 1);
                        setError(input, msg);
                        hasError = true;
                    }
                });
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
});

let questionCount = 1;

function addQuestion() {
    const existingInputs = document.querySelectorAll('#questions-container input[name="questions"]');
    for (let i = 0; i < existingInputs.length; i++) {
        const input = existingInputs[i];
        if (!input.value.trim()) {
            setError(input, window.i18n.validation.position.questionsCompleteAll);
            return;
        }
    }

    const container = document.getElementById('questions-container');
    const template = container.querySelector('.question-item');

    if (!template) return;

    const questionCount = container.querySelectorAll('.question-item').length + 1;

    const newQuestion = template.cloneNode(true);

    newQuestion.querySelector('.question-label').textContent = 'Pregunta ' + questionCount;

    const input = newQuestion.querySelector('input, textarea');
    if (input) {
        input.id = 'question_' + questionCount;
        input.name = 'questions';
        input.value = '';
    }

    const inputWithButton = newQuestion.querySelector('.input-with-button');
    const removeBtn = document.createElement('button');
    removeBtn.type = 'button';
    removeBtn.className = 'remove-btn';
    removeBtn.textContent = '×';
    removeBtn.onclick = function () { removeQuestion(this); };
    inputWithButton.appendChild(removeBtn);

    container.appendChild(newQuestion);
}

function removeQuestion(button) {
    button.closest('.question-item').remove();
    renumberQuestions();
}

function renumberQuestions() {
    const allQuestions = document.querySelectorAll('#questions-container .question-item');
    allQuestions.forEach((item, index) => {
        const newNumber = index + 1;
        item.querySelector('.question-label').textContent = 'Pregunta ' + newNumber;
        const input = item.querySelector('input[name="questions"]');
        if (input) {
            input.id = 'question_' + newNumber;
        }
    });
    questionCount = allQuestions.length;
}

window.addQuestion = addQuestion;
window.removeQuestion = removeQuestion;
window.renumberQuestions = renumberQuestions;