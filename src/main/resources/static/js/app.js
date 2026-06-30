document.addEventListener('DOMContentLoaded', function() {
    var sidebar = document.getElementById('sidebar');
    var sidebarToggle = document.getElementById('sidebarToggle');
    var sidebarOverlay = document.getElementById('sidebarOverlay');
    var mainContent = document.getElementById('mainContent');

    document.querySelectorAll('.sidebar-nav .nav-item').forEach(function(item) {
        var href = item.getAttribute('href');
        if (href && window.location.pathname.startsWith(href)) {
            item.classList.add('active');
        }
    });

    if (sidebarToggle) {
        sidebarToggle.addEventListener('click', function() {
            if (window.innerWidth < 768) {
                sidebar.classList.toggle('mobile-open');
                sidebarOverlay.classList.toggle('show');
            } else {
                sidebar.classList.toggle('collapsed');
                localStorage.setItem('sidebarCollapsed', sidebar.classList.contains('collapsed'));
            }
        });
    }

    if (sidebarOverlay) {
        sidebarOverlay.addEventListener('click', function() {
            sidebar.classList.remove('mobile-open');
            sidebarOverlay.classList.remove('show');
        });
    }

    if (window.innerWidth >= 768 && window.innerWidth < 1200) {
        sidebar.classList.add('collapsed');
    }

    const savedState = localStorage.getItem('sidebarCollapsed');
    if (savedState === 'true' && window.innerWidth >= 1200) {
        sidebar.classList.add('collapsed');
    }

    window.addEventListener('resize', function() {
        if (window.innerWidth >= 1200) {
            sidebar.classList.remove('mobile-open');
            sidebarOverlay.classList.remove('show');
        }
        if (window.innerWidth < 768) {
            sidebar.classList.remove('collapsed');
        }
    });

    if (mainContent) {
        mainContent.classList.add('fade-in');
    }

    document.querySelectorAll('a[onclick*="confirm"]').forEach(function(link) {
        var originalOnclick = link.getAttribute('onclick');
        link.setAttribute('onclick', originalOnclick.replace("return confirm('", "return confirm('"));
    });
});