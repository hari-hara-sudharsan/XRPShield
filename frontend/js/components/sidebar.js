export function renderSidebar() {
    const container = document.getElementById('sidebar-container');
    if (!container) return;

    container.innerHTML = `
        <div class="sidebar-logo">
            <span>🛡️ XRPShield</span>
        </div>
        <div class="sidebar-tag">Flare FCC TEE Enclave</div>
        <ul class="nav-list">
            <li class="nav-item" data-page="landing">
                <a href="#landing">
                    <span>🏠</span> Landing Overview
                </a>
            </li>
            <li class="nav-item" data-page="dashboard">
                <a href="#dashboard">
                    <span>📊</span> Dashboard
                </a>
            </li>
            <li class="nav-item" data-page="vaults">
                <a href="#vaults">
                    <span>🔒</span> Treasury Vaults
                </a>
            </li>
            <li class="nav-item" data-page="policies">
                <a href="#policies">
                    <span>🛡️</span> Confidential Policies
                </a>
            </li>
            <li class="nav-item" data-page="decisions">
                <a href="#decisions">
                    <span>⭐</span> Decision Center
                </a>
            </li>
            <li class="nav-item" data-page="executions">
                <a href="#executions">
                    <span>🚀</span> Executions History
                </a>
            </li>
            <li class="nav-item" data-page="ai-assistant">
                <a href="#ai-assistant">
                    <span>🤖</span> AI Assistant & Reports
                </a>
            </li>
            <li class="nav-item" data-page="platform-status">
                <a href="#platform-status">
                    <span>⚡</span> System & Network
                </a>
            </li>
            <li class="nav-item" data-page="settings">
                <a href="#settings">
                    <span>⚙️</span> Settings
                </a>
            </li>
        </ul>
        <div class="sidebar-foot">
            <div class="enclave-dot"></div>
            <span>FCC Enclave Active</span>
        </div>
    `;
}
