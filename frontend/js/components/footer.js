export function renderFooter() {
    const container = document.getElementById('footer-container');
    if (!container) return;

    container.innerHTML = `
        <div>
            &copy; 2026 XRPShield — Privacy-Preserving Treasury Platform on Flare
        </div>
        <div>
            Attestation Engine: <span style="color: var(--accent-emerald); font-weight: 600;">Flare Confidential Compute (FCC) Ready</span>
        </div>
    `;
}
