/* ===========================================================
   XRPShield — Toast & Notification Center Widget
   =========================================================== */

export function showToast(message, type = 'info') {
    let container = document.getElementById('toast-container');
    if (!container) {
        container = document.createElement('div');
        container.id = 'toast-container';
        document.body.appendChild(container);
    }

    const toast = document.createElement('div');
    toast.className = `toast toast-${type}`;
    
    let icon = 'ℹ️';
    if (type === 'success') icon = '✅';
    if (type === 'warning') icon = '⚠️';
    if (type === 'error') icon = '❌';

    toast.innerHTML = `<span>${icon}</span> <span>${message}</span>`;
    container.appendChild(toast);

    setTimeout(() => {
        toast.style.opacity = '0';
        toast.style.transform = 'translateX(100%)';
        setTimeout(() => toast.remove(), 300);
    }, 4000);
}

export function toggleNotificationDrawer() {
    let drawer = document.getElementById('notification-drawer');
    if (!drawer) {
        drawer = document.createElement('div');
        drawer.id = 'notification-drawer';
        drawer.style.cssText = `
            position: fixed; top: 70px; right: 24px; width: 360px; max-height: 480px;
            background: var(--bg-card); border: 1px solid var(--border-glow);
            border-radius: 12px; z-index: 1000; box-shadow: 0 20px 40px rgba(0,0,0,0.6);
            overflow-y: auto; padding: 20px; display: none; backdrop-filter: blur(16px);
        `;
        drawer.innerHTML = `
            <div style="display:flex; justify-content:space-between; align-items:center; margin-bottom:16px;">
                <h4 style="font-size:1rem; font-weight:700;">Platform Notifications</h4>
                <span onclick="document.getElementById('notification-drawer').style.display='none'" style="cursor:pointer; color:var(--text-muted);">&times;</span>
            </div>
            <div style="display:flex; flex-direction:column; gap:10px;">
                <div style="background:rgba(0,242,254,0.08); padding:10px; border-radius:8px; border:1px solid rgba(0,242,254,0.2); font-size:0.85rem;">
                    <strong>[INFO] System Status:</strong> All 6 sub-components active and healthy.
                </div>
                <div style="background:rgba(16,185,129,0.08); padding:10px; border-radius:8px; border:1px solid rgba(16,185,129,0.2); font-size:0.85rem;">
                    <strong>[EXECUTION] Completed:</strong> Transaction hash 0x7f82ab19 confirmed on Flare.
                </div>
            </div>
        `;
        document.body.appendChild(drawer);
    }
    drawer.style.display = drawer.style.display === 'none' ? 'block' : 'none';
}
