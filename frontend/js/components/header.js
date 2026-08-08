export function renderHeader() {
    const container = document.getElementById('header-container');
    if (!container) return;

    container.innerHTML = `
        <div style="font-weight: 600; font-size: 1.1rem; color: var(--text-primary);">
            XRP Treasury & Risk Management
        </div>
        <div class="header-status">
            <div class="badge">
                <span class="badge-dot"></span> Flare Coston2 Testnet (Chain ID 114)
            </div>
            <button class="btn-connect" id="btn-wallet-connect">Connect MetaMask</button>
        </div>
    `;
}
