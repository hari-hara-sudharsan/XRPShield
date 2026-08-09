import { WalletManager } from './utils/wallet.js';
import { updateActiveTreasury } from './utils/execution-modal.js';

/* ===========================================================
   XRPShield — Main Frontend Single Page Application Router
   =========================================================== */


const routes = {
    'landing': 'pages/landing.html',
    'dashboard': 'pages/dashboard.html',
    'vaults': 'pages/vault.html',
    'policies': 'pages/policies.html',
    'decisions': 'pages/decisions.html',
    'executions': 'pages/executions.html',
    'ai-assistant': 'pages/ai-assistant.html',
    'platform-status': 'pages/platform-status.html',
    'settings': 'pages/settings.html'
};

async function navigateTo(routeKey) {
    const targetRoute = routes[routeKey] || routes['landing'];

    const container = document.getElementById('page-render-container');
    
    if (!container) return;

    try {
        const response = await fetch(targetRoute);
        if (!response.ok) throw new Error(`HTTP ${response.status}`);
        const html = await response.text();
        container.innerHTML = html;

        // Dispatch page component initialization
        dispatchComponentInit(routeKey);

        // Update nav active classes
        document.querySelectorAll('.nav-item').forEach(item => {
            const link = item.querySelector('a');
            if (link && link.getAttribute('href') === `#${routeKey}`) {
                item.classList.add('active');
            } else {
                item.classList.remove('active');
            }
        });

    } catch (err) {
        container.innerHTML = `
            <div class="card" style="padding: 40px; text-align: center;">
                <h3 style="color: var(--accent-rose); margin-bottom: 12px;">Error Loading Page View</h3>
                <p style="color: var(--text-secondary); font-size: 0.95rem;">${err.message}</p>
                <button onclick="window.location.hash='#dashboard'" class="btn-primary" style="margin-top: 16px;">Return to Dashboard</button>
            </div>
        `;
    }
}

function dispatchComponentInit(routeKey) {
    switch (routeKey) {
        case 'dashboard':
            import('./components/dashboard.js').then(m => m.initDashboard()).catch(e => console.warn(e));
            break;
        case 'ai-assistant':

            import('./components/ai-assistant.js').then(m => m.initAIAssistant()).catch(e => console.warn(e));
            break;
        case 'decisions':
            import('./components/decisions.js').then(m => m.initDecisions()).catch(e => console.warn(e));
            break;
        case 'executions':
            import('./components/executions.js').then(m => m.initExecutions()).catch(e => console.warn(e));
            break;
        case 'platform-status':
            import('./components/platform-status.js').then(m => m.initPlatformStatus()).catch(e => console.warn(e));
            break;
        case 'settings':
            import('./components/settings.js').then(m => m.initSettings()).catch(e => console.warn(e));
            break;
        case 'vaults':
            import('./components/vaults.js').then(m => m.initVaults()).catch(e => console.warn(e));
            break;
        case 'policies':
            import('./components/policies.js').then(m => m.initPolicies()).catch(e => console.warn(e));
            break;
    }
}

function initHeaderNotifications() {
    const notifBtn = document.getElementById('notif-btn');
    const notifDrawer = document.getElementById('notif-drawer');

    if (notifBtn && notifDrawer && !notifBtn.dataset.initialized) {
        notifBtn.dataset.initialized = 'true';
        notifBtn.addEventListener('click', (e) => {
            e.stopPropagation();
            notifDrawer.classList.toggle('open');
        });
        document.addEventListener('click', (e) => {
            if (!notifDrawer.contains(e.target) && !notifBtn.contains(e.target)) {
                notifDrawer.classList.remove('open');
            }
        });
    }
}

function handleHashChange() {
    const hash = window.location.hash.replace('#', '') || 'dashboard';
    navigateTo(hash);
}

window.addEventListener('hashchange', handleHashChange);
window.addEventListener('DOMContentLoaded', () => {
    handleHashChange();
    WalletManager.init();
    initHeaderNotifications();
    updateActiveTreasury(0);
});


