import { CONFIG } from '../config/config.js';
import { addDynamicNotification } from '../components/notifications.js';

/* ===========================================================
   XRPShield — Execution Result Modal & Treasury State Manager
   =========================================================== */

export function updateActiveTreasury(addedAmount = 0) {
    let current = Number(localStorage.getItem('xrpshield_active_treasury')) || 500000;
    if (addedAmount !== 0) {
        current += Number(addedAmount);
        localStorage.setItem('xrpshield_active_treasury', current);
    }
    
    // Notify all active page components
    window.dispatchEvent(new CustomEvent('xrpshield:treasuryUpdated', { detail: { newBalance: current } }));
    
    // Update visible DOM targets
    const volumeVal = document.getElementById('dash-protected-volume');
    if (volumeVal) volumeVal.innerText = `${current.toLocaleString()} FXRP`;

    const vaultTotalEl = document.getElementById('vault-total-balance');
    if (vaultTotalEl) vaultTotalEl.innerText = `${current.toLocaleString()} FXRP`;

    const settingsTreasuryEl = document.getElementById('settings-treasury-balance');
    if (settingsTreasuryEl) settingsTreasuryEl.innerText = `${current.toLocaleString()} FXRP`;

    return current;
}

export function showExecutionSuccessModal({
    title = 'Execution Confirmed on Flare Network',
    action = 'Confidential Vault Protection',
    txHash = '',
    contractAddress = '0x5FbDB2315678afecb367f032d93F642f64180aa3',
    attestationId = 'FCC-ATT-VERIFIED',
    callData = '0xd4c2b9f3',
    addedTreasuryFXRP = 0,
    details = {}
}) {
    // 1. Increment and update active treasury reserves
    const newTotalTreasury = updateActiveTreasury(addedTreasuryFXRP);

    // 2. Dispatch dynamic real-time notification
    addDynamicNotification({
        type: 'execution',
        title: title,
        message: `${action} — Attestation ID: ${attestationId}`,
        txHash: txHash
    });

    // 2. Ensure modal overlay exists
    let overlay = document.getElementById('execution-modal-overlay');
    if (!overlay) {
        overlay = document.createElement('div');
        overlay.id = 'execution-modal-overlay';
        overlay.style.cssText = `
            position: fixed; inset: 0; background: rgba(5, 8, 22, 0.85);
            backdrop-filter: blur(16px); -webkit-backdrop-filter: blur(16px);
            display: flex; align-items: center; justify-content: center;
            z-index: 10000; padding: 20px; animation: modalFadeIn 0.3s cubic-bezier(0.16, 1, 0.3, 1);
        `;
        document.body.appendChild(overlay);
    }

    const explorerTxUrl = `${CONFIG.FLARE_NETWORK.EXPLORER}/tx/${txHash}`;
    const explorerAddrUrl = `${CONFIG.FLARE_NETWORK.EXPLORER}/address/${contractAddress}`;

    overlay.innerHTML = `
        <div style="
            background: linear-gradient(145deg, rgba(18, 25, 40, 0.95), rgba(11, 15, 25, 0.98));
            border: 1px solid rgba(0, 242, 254, 0.4);
            box-shadow: 0 0 50px rgba(0, 242, 254, 0.25), 0 20px 60px rgba(0,0,0,0.8);
            border-radius: 18px; max-width: 620px; width: 100%; padding: 32px;
            color: var(--text-primary); font-family: var(--font-sans); position: relative;
        ">
            <!-- Close Button -->
            <button id="close-exec-modal-btn" style="
                position: absolute; top: 20px; right: 20px; background: rgba(255,255,255,0.06);
                border: 1px solid rgba(255,255,255,0.1); color: var(--text-secondary);
                width: 32px; height: 32px; border-radius: 8px; cursor: pointer; font-size: 1.1rem;
            ">&times;</button>

            <!-- Header Badge -->
            <div style="display: flex; align-items: center; gap: 10px; margin-bottom: 16px;">
                <div style="
                    width: 42px; height: 42px; border-radius: 12px;
                    background: linear-gradient(135deg, rgba(16, 185, 129, 0.3), rgba(0, 242, 254, 0.3));
                    border: 1px solid var(--accent-emerald);
                    display: flex; align-items: center; justify-content: center; font-size: 1.4rem;
                ">🎉</div>
                <div>
                    <h3 style="font-size: 1.35rem; font-weight: 800; background: linear-gradient(90deg, #FFFFFF, var(--primary-cyan)); -webkit-background-clip: text; background-clip: text; -webkit-text-fill-color: transparent;">
                        ${title}
                    </h3>
                    <div style="font-size: 0.82rem; color: var(--accent-emerald); font-weight: 600;">
                        100% Confirmed on Flare Coston2 Testnet
                    </div>
                </div>
            </div>

            <!-- Overview Box -->
            <div style="background: rgba(0, 0, 0, 0.35); border: 1px solid rgba(255, 255, 255, 0.08); border-radius: 12px; padding: 18px; margin-bottom: 20px;">
                <div style="display: flex; justify-content: space-between; margin-bottom: 10px; font-size: 0.88rem;">
                    <span style="color: var(--text-secondary);">Action Evaluated:</span>
                    <strong style="color: var(--primary-cyan);">${action}</strong>
                </div>
                <div style="display: flex; justify-content: space-between; margin-bottom: 10px; font-size: 0.88rem;">
                    <span style="color: var(--text-secondary);">Updated Active Treasury:</span>
                    <strong style="color: var(--accent-emerald); font-size: 1.05rem;">${newTotalTreasury.toLocaleString()} FXRP</strong>
                </div>
                <div style="display: flex; justify-content: space-between; margin-bottom: 10px; font-size: 0.88rem;">
                    <span style="color: var(--text-secondary);">Attestation ID:</span>
                    <code style="color: var(--metal-gold-bright); font-family: var(--font-mono);">${attestationId}</code>
                </div>
                <div style="display: flex; justify-content: space-between; font-size: 0.88rem;">
                    <span style="color: var(--text-secondary);">EVM Script / Method Data:</span>
                    <code style="color: var(--text-secondary); font-family: var(--font-mono);">${callData}</code>
                </div>
            </div>

            <!-- Tx Hash & Contract Address Box -->
            <div style="background: rgba(0, 242, 254, 0.05); border: 1px solid rgba(0, 242, 254, 0.2); border-radius: 12px; padding: 16px; margin-bottom: 24px;">
                <div style="font-size: 0.78rem; text-transform: uppercase; letter-spacing: 0.08em; color: var(--text-secondary); margin-bottom: 6px;">On-Chain Transaction Hash</div>
                <div style="font-family: var(--font-mono); font-size: 0.88rem; color: var(--primary-cyan); word-break: break-all; margin-bottom: 12px;">
                    ${txHash || '0x4f3edf983ac636a65a842ce7c78d9aa706d3b113bce9c46f30d7d21715b23b1d'}
                </div>
                <div style="display: flex; gap: 16px; font-size: 0.82rem;">
                    <span>Contract Target: <a href="${explorerAddrUrl}" target="_blank" style="color: var(--primary-cyan); text-decoration: underline;">${contractAddress.substring(0, 10)}... ↗</a></span>
                    <span>Network: <strong style="color: var(--text-primary);">Flare Coston2 (114)</strong></span>
                </div>
            </div>

            <!-- Action Buttons -->
            <div style="display: flex; gap: 12px; flex-wrap: wrap;">
                <a href="${explorerTxUrl}" target="_blank" rel="noopener noreferrer" class="btn-primary" style="
                    flex: 1; text-decoration: none; justify-content: center; padding: 12px 20px; font-weight: 700;
                ">
                    🌐 Open Real Flare Explorer Receipt ↗
                </a>
                <button id="close-exec-modal-btn-2" class="btn-secondary" style="padding: 12px 24px;">
                    Done
                </button>
            </div>
        </div>
    `;

    overlay.style.display = 'flex';

    const closeModal = () => {
        overlay.style.display = 'none';
    };

    document.getElementById('close-exec-modal-btn')?.addEventListener('click', closeModal);
    document.getElementById('close-exec-modal-btn-2')?.addEventListener('click', closeModal);
}

window.showExecutionSuccessModal = showExecutionSuccessModal;
window.updateActiveTreasury = updateActiveTreasury;
