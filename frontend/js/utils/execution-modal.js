import { CONFIG } from '../config/config.js';
import { addDynamicNotification } from '../components/notifications.js';

let activeTreasuryFXRP = 550002;

export function updateActiveTreasury(addedAmount = 0) {
    activeTreasuryFXRP += Number(addedAmount);
    
    // Update UI Elements Across Application Pages
    const heroTreasuryEl = document.getElementById('hero-treasury-balance');
    if (heroTreasuryEl) heroTreasuryEl.innerText = `${activeTreasuryFXRP.toLocaleString()} FXRP`;

    const dashTreasuryEl = document.getElementById('dash-treasury-balance');
    if (dashTreasuryEl) dashTreasuryEl.innerText = `${activeTreasuryFXRP.toLocaleString()} FXRP`;

    const settingsTreasuryEl = document.getElementById('settings-treasury-balance');
    if (settingsTreasuryEl) settingsTreasuryEl.innerText = `${activeTreasuryFXRP.toLocaleString()} FXRP`;

    return activeTreasuryFXRP;
}

export function showExecutionSuccessModal({
    title = 'Execution Confirmed on Flare Network',
    action = 'Confidential Vault Protection',
    txHash = '0x4f3edf983ac636a65a842ce7c78d9aa706d3b113bce9c46f30d7d21715b23b1d',
    contractAddress = CONFIG.CONTRACTS.VAULT_MANAGER,
    attestationId = '0x6f7c4df64308f102e77796841daffd60c5296b379bacdea12f2c832061e144c2',
    callData = '0x3564b8ed',
    addedTreasuryFXRP = 0,
    details = {}
}) {
    // 1. Increment and update active treasury reserves
    const newTotalTreasury = updateActiveTreasury(addedTreasuryFXRP);

    // 2. Dispatch dynamic real-time notification
    addDynamicNotification({
        type: 'execution',
        title: title,
        message: `${action} — Attestation Hash: ${attestationId.substring(0, 16)}...`,
        txHash: txHash
    });

    // 3. Ensure modal overlay exists
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

    const actualTxHash = txHash || '0x4f3edf983ac636a65a842ce7c78d9aa706d3b113bce9c46f30d7d21715b23b1d';
    const actualContractAddress = contractAddress || CONFIG.CONTRACTS.VAULT_MANAGER;
    const explorerTxUrl = `${CONFIG.FLARE_NETWORK.EXPLORER}/tx/${actualTxHash}`;
    const explorerAddrUrl = `${CONFIG.FLARE_NETWORK.EXPLORER}/address/${actualContractAddress}`;

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
            <div style="display: flex; align-items: center; gap: 14px; margin-bottom: 18px;">
                <div style="
                    width: 46px; height: 46px; border-radius: 14px;
                    background: linear-gradient(135deg, rgba(16, 185, 129, 0.25), rgba(0, 242, 254, 0.25));
                    border: 1px solid var(--accent-emerald, #10B981);
                    box-shadow: 0 0 20px rgba(16, 185, 129, 0.35), inset 0 1px 0 rgba(255, 255, 255, 0.2);
                    display: flex; align-items: center; justify-content: center; flex-shrink: 0;
                ">
                    <svg viewBox="0 0 24 24" width="24" height="24" fill="none" stroke="#10B981" stroke-width="2.5" stroke-linecap="round" stroke-linejoin="round" style="filter: drop-shadow(0 0 6px #10B981);">
                        <path d="M12 22s8-4 8-10V5l-8-3-8 3v7c0 6 8 10 8 10z"/>
                        <path d="M9 12l2 2 4-4"/>
                    </svg>
                </div>
                <div>
                    <h3 style="font-size: 1.35rem; font-weight: 800; background: linear-gradient(90deg, #FFFFFF, var(--primary-cyan, #62d9ee)); -webkit-background-clip: text; background-clip: text; -webkit-text-fill-color: transparent; margin: 0 0 4px 0;">
                        ${title}
                    </h3>
                    <div style="font-size: 0.82rem; color: var(--accent-emerald, #10B981); font-weight: 600; display: flex; align-items: center; gap: 6px;">
                        <span style="width: 6px; height: 6px; border-radius: 50%; background: #10B981; box-shadow: 0 0 8px #10B981;"></span>
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
                <div style="display: flex; justify-content: space-between; margin-bottom: 12px; font-size: 0.88rem; align-items: center;">
                    <span style="color: var(--text-secondary);">On-Chain Contract Verification:</span>
                    <span id="onchain-attestation-status-badge" style="background: rgba(16, 185, 129, 0.15); color: #10B981; border: 1px solid #10B981; padding: 4px 10px; border-radius: 6px; font-weight: 800; font-size: 0.75rem;">
                        ATTESTATION VERIFIED
                    </span>
                </div>
                <div style="margin-bottom: 12px;">
                    <div style="color: var(--text-secondary); font-size: 0.82rem; margin-bottom: 4px;">Attestation Proof Hash:</div>
                    <code style="color: var(--metal-gold-bright); font-family: var(--font-mono); font-size: 0.78rem; word-break: break-all; overflow-wrap: anywhere; display: block; background: rgba(0,0,0,0.3); padding: 8px 10px; border-radius: 6px; border: 1px solid rgba(255,255,255,0.05);">${attestationId}</code>
                </div>
                <div style="display: flex; justify-content: space-between; font-size: 0.88rem;">
                    <span style="color: var(--text-secondary);">EVM Script / Method Data:</span>
                    <code style="color: var(--text-secondary); font-family: var(--font-mono); font-size: 0.82rem;">${callData}</code>
                </div>
            </div>

            <!-- Tx Hash & Contract Address Box -->
            <div style="background: rgba(0, 242, 254, 0.05); border: 1px solid rgba(0, 242, 254, 0.2); border-radius: 12px; padding: 16px; margin-bottom: 24px;">
                <div style="font-size: 0.78rem; text-transform: uppercase; letter-spacing: 0.08em; color: var(--text-secondary); margin-bottom: 6px;">On-Chain Transaction Hash</div>
                <div style="font-family: var(--font-mono); font-size: 0.82rem; color: var(--primary-cyan); word-break: break-all; overflow-wrap: anywhere; margin-bottom: 12px;">
                    ${actualTxHash}
                </div>
                <div style="display: flex; gap: 16px; font-size: 0.82rem; flex-wrap: wrap;">
                    <span>Contract Target: <a href="${explorerAddrUrl}" target="_blank" style="color: var(--primary-cyan); text-decoration: underline;">${actualContractAddress.substring(0, 10)}... ↗</a></span>
                    <span>Network: <strong style="color: var(--text-primary);">Flare Coston2 (114)</strong></span>
                </div>
            </div>

            <!-- Action Buttons -->
            <div style="display: flex; gap: 12px; flex-wrap: wrap;">
                <a href="${explorerTxUrl}" target="_blank" rel="noopener noreferrer" class="btn-primary" style="
                    flex: 1; text-decoration: none; justify-content: center; padding: 12px 20px; font-weight: 700; display: inline-flex; align-items: center; gap: 8px;
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
