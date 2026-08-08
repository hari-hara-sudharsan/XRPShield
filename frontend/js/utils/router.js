export class Router {
    constructor(routes) {
        this.routes = routes;
        this.currentRoute = null;
        window.addEventListener('hashchange', () => this.handleRoute());
    }

    init() {
        this.handleRoute();
    }

    async handleRoute() {
        const hash = window.location.hash.slice(1) || 'home';
        const route = this.routes[hash] || this.routes['home'];

        if (route) {
            this.currentRoute = hash;
            this.updateActiveNav(hash);
            const viewContainer = document.getElementById('router-view');
            if (viewContainer) {
                try {
                    const response = await fetch(route.page);
                    if (response.ok) {
                        viewContainer.innerHTML = await response.text();
                    } else {
                        viewContainer.innerHTML = `<div class="card"><h2>Page Not Found</h2></div>`;
                    }
                } catch (e) {
                    console.error('Failed to load route page:', e);
                }
            }
        }
    }

    updateActiveNav(activeHash) {
        document.querySelectorAll('.nav-item').forEach(item => {
            const page = item.getAttribute('data-page');
            if (page === activeHash) {
                item.classList.add('active');
            } else {
                item.classList.remove('active');
            }
        });
    }
}
