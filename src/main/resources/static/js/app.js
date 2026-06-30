function setSidebarCookie(collapsed) {
    document.cookie = "sidebarCollapsed=" + (collapsed ? "true" : "false") + ";path=/;max-age=86400";
}

function getSidebarCookie() {
    var match = document.cookie.match(/(?:^|;\s*)sidebarCollapsed=([^;]*)/);
    return match ? match[1] === 'true' : false;
}

document.addEventListener('DOMContentLoaded', function() {
    var sidebar = document.getElementById('sidebar');
    var sidebarToggle = document.getElementById('sidebarToggle');
    var sidebarOverlay = document.getElementById('sidebarOverlay');

    document.querySelectorAll('.sidebar-nav .nav-item').forEach(function(item) {
        var href = item.getAttribute('href');
        if (href && window.location.pathname.startsWith(href)) {
            item.classList.add('active');
        }
    });

    // Sync collapsed state from cookie (server already rendered class, but ensure consistency)
    if (sidebar && getSidebarCookie()) {
        sidebar.classList.add('collapsed');
    }

    if (sidebarToggle) {
        sidebarToggle.addEventListener('click', function() {
            if (window.innerWidth < 768) {
                sidebar.classList.toggle('mobile-open');
                sidebarOverlay.classList.toggle('show');
            } else {
                sidebar.classList.toggle('collapsed');
                setSidebarCookie(sidebar.classList.contains('collapsed'));
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
        if (!sidebar.classList.contains('collapsed')) {
            sidebar.classList.add('collapsed');
            setSidebarCookie(true);
        }
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
});
