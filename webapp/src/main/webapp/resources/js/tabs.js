document.addEventListener('DOMContentLoaded', () => {
    const container = document.querySelector('.tabs-container');
    if (!container) return;

    const tabs = container.querySelectorAll('.tab-button');
    const contents = container.querySelectorAll('.tab-content');
    const STORAGE_KEY = container.dataset.storageKey || 'activeTab';

    const savedTab = localStorage.getItem(STORAGE_KEY);
    if (savedTab) {
        const savedTabButton = container.querySelector(`.tab-button[data-tab="${savedTab}"]`);
        const savedTabContent = container.querySelector(`#${savedTab}`);

        if (savedTabButton && savedTabContent) {
            tabs.forEach(t => t.classList.remove('active'));
            contents.forEach(c => c.classList.remove('active'));

            savedTabButton.classList.add('active');
            savedTabContent.classList.add('active');
        }
    }

    function toggleAdminActions(activeTab) {
        const postForm = document.querySelector('.admin-action-posts');
        const eventForm = document.querySelector('.admin-action-events');
        const openPositionForm =  document.querySelector('.admin-action-openPositions');

        if (postForm && eventForm && openPositionForm) {
            postForm.style.display = activeTab === 'posts' ? 'block' : 'none';
            eventForm.style.display = activeTab === 'events' ? 'block' : 'none';
            openPositionForm.style.display = activeTab === 'openPositions' ? 'block' : 'none';
        }
    }

    tabs.forEach(tab => {
        tab.addEventListener('click', () => {
            tabs.forEach(t => t.classList.remove('active'));
            contents.forEach(c => c.classList.remove('active'));

            tab.classList.add('active');
            const targetId = tab.getAttribute('data-tab');
            container.querySelector(`#${targetId}`).classList.add('active');

            localStorage.setItem(STORAGE_KEY, targetId);

            toggleAdminActions(targetId);
        });
    });

    if (savedTab) {
        toggleAdminActions(savedTab);
    } else {
        toggleAdminActions('posts');
    }
});

