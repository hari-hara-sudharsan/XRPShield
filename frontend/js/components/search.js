export function initGlobalSearch() {
    const searchInput = document.getElementById('global-search-input');
    if (!searchInput || searchInput.dataset.initialized) return;
    searchInput.dataset.initialized = 'true';

    const searchBox = searchInput.closest('.search-box');
    if (!searchBox) return;

    // Create dropdown overlay
    const dropdown = document.createElement('div');
    dropdown.id = 'search-dropdown';
    dropdown.style.cssText = `
        position: absolute;
        top: 48px;
        left: 0;
        right: 0;
        background: var(--bg-surface-2, #111726);
        border: 1px solid var(--border-strong, rgba(0,242,254,0.3));
        border-radius: 10px;
        box-shadow: 0 16px 40px rgba(0,0,0,0.6);
        max-height: 320px;
        overflow-y: auto;
        z-index: 250;
        display: none;
        padding: 8px 0;
    `;
    searchBox.style.position = 'relative';
    searchBox.appendChild(dropdown);

    const routesList = [
        { name: 'Primary FXRP Vault', route: 'vaults', desc: 'Vault management & balance reserves' },
        { name: 'Yield Reserve Vault', route: 'vaults', desc: 'Automated yield generation reserve' },
        { name: 'Max Drawdown Circuit Breaker', route: 'policies', desc: '8% drawdown protection policy' },
        { name: 'Liquidity Safeguard Policy', route: 'policies', desc: '500k FXRP minimum liquidity rule' },
        { name: 'Automated Position Protection', route: 'decisions', desc: 'Flare TEE decision engine' },
        { name: 'On-Chain Execution History', route: 'executions', desc: 'Cryptographically signed audit trail' },
        { name: 'AI Policy Assistant', route: 'ai-assistant', desc: 'Natural language risk parameter inference' },
        { name: 'Platform Settings & Profile', route: 'settings', desc: 'Language, timezone & Web3 signature' }
    ];

    searchInput.addEventListener('input', () => {
        const query = searchInput.value.trim().toLowerCase();
        
        // Filter table rows on current page
        filterCurrentPage(query);

        if (!query) {
            dropdown.style.display = 'none';
            return;
        }

        const matches = routesList.filter(item => 
            item.name.toLowerCase().includes(query) || 
            item.desc.toLowerCase().includes(query) ||
            item.route.toLowerCase().includes(query)
        );

        if (matches.length === 0) {
            dropdown.innerHTML = `<div style="padding: 12px 16px; font-size: 0.85rem; color: var(--text-tertiary);">No matching vaults, policies, or decisions found for "${escapeHtml(query)}"</div>`;
        } else {
            dropdown.innerHTML = matches.map(m => `
                <div class="search-item" data-route="${m.route}" style="padding: 10px 16px; cursor: pointer; border-bottom: 1px solid rgba(255,255,255,0.05); transition: background 0.15s;">
                    <div style="font-size: 0.88rem; font-weight: 600; color: var(--primary-cyan, #00F2FE);">${escapeHtml(m.name)}</div>
                    <div style="font-size: 0.76rem; color: var(--text-secondary); margin-top: 2px;">${escapeHtml(m.desc)} — <span style="color: var(--gold-bright);">#${m.route}</span></div>
                </div>
            `).join('');

            dropdown.querySelectorAll('.search-item').forEach(el => {
                el.addEventListener('mouseenter', () => el.style.background = 'rgba(255,255,255,0.06)');
                el.addEventListener('mouseleave', () => el.style.background = 'transparent');
                el.addEventListener('click', () => {
                    const route = el.dataset.route;
                    window.location.hash = route;
                    dropdown.style.display = 'none';
                    searchInput.value = '';
                });
            });
        }

        dropdown.style.display = 'block';
    });

    searchInput.addEventListener('keydown', (e) => {
        if (e.key === 'Enter') {
            const firstMatch = dropdown.querySelector('.search-item');
            if (firstMatch) {
                firstMatch.click();
            }
        } else if (e.key === 'Escape') {
            dropdown.style.display = 'none';
        }
    });

    document.addEventListener('click', (e) => {
        if (!searchBox.contains(e.target)) {
            dropdown.style.display = 'none';
        }
    });
}

function filterCurrentPage(query) {
    const rows = document.querySelectorAll('#page-render-container tr');
    rows.forEach(row => {
        if (row.parentElement.tagName === 'THEAD') return;
        const text = row.innerText.toLowerCase();
        if (!query || text.includes(query)) {
            row.style.display = '';
        } else {
            row.style.display = 'none';
        }
    });
}

function escapeHtml(text) {
    if (!text) return '';
    return String(text).replace(/&/g, '&amp;').replace(/</g, '&lt;').replace(/>/g, '&gt;').replace(/"/g, '&quot;');
}
