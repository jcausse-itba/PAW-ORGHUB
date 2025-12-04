(function() {
    const btn = document.getElementById('hamburger-menu-btn');
    const modal = document.getElementById('hamburger-menu-modal');
    const backdrop = modal?.querySelector('.hamburger-menu-backdrop');
    const closeBtn = modal?.querySelector('.hamburger-menu-close');

    if (!btn || !modal || !backdrop || !closeBtn) {
        console.warn('Hamburger menu elements not found');
        return;
    }

    function openMenu() {
        modal.classList.add('is-open');
        modal.setAttribute('aria-hidden', 'false');
        document.body.style.overflow = 'hidden';
        
        setTimeout(() => {
            closeBtn.focus();
        }, 100);
    }

    function closeMenu() {
        modal.classList.remove('is-open');
        modal.setAttribute('aria-hidden', 'true');
        document.body.style.overflow = '';
        
        btn.focus();
    }

    btn.addEventListener('click', openMenu);
    closeBtn.addEventListener('click', closeMenu);
    backdrop.addEventListener('click', closeMenu);

    document.addEventListener('keydown', function(e) {
        if (e.key === 'Escape' && modal.classList.contains('is-open')) {
            closeMenu();
        }
    });

    modal.querySelectorAll('.hamburger-menu-link').forEach(link => {
        link.addEventListener('click', closeMenu);
    });

    // Handle dropdown toggles
    modal.querySelectorAll('.hamburger-menu-dropdown-header').forEach(header => {
        header.addEventListener('click', function(e) {
            e.preventDefault();
            e.stopPropagation();
            const dropdown = this.closest('.hamburger-menu-dropdown');
            const isOpen = dropdown.classList.contains('is-open');
            
            // Close all other dropdowns
            modal.querySelectorAll('.hamburger-menu-dropdown').forEach(d => {
                d.classList.remove('is-open');
            });
            
            // Toggle current dropdown
            if (!isOpen) {
                dropdown.classList.add('is-open');
            }
        });
    });

    // Close dropdown when clicking on dropdown links
    modal.querySelectorAll('.hamburger-menu-dropdown-link').forEach(link => {
        link.addEventListener('click', function() {
            closeMenu();
        });
    });
})();
