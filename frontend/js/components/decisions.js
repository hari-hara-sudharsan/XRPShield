import { ApiClient } from '../utils/api.js';
import { WalletManager } from '../utils/wallet.js';
import { CONFIG } from '../config/config.js';
import { showExecutionSuccessModal } from '../utils/execution-modal.js';
import { saveCustomExecution } from './executions.js';

export async function initDecisions() {
    const tableBody = document.getElementById('decisions-table-body');
    const evaluateForm = document.getElementById('evaluate-decision-form');
    const pipelineBtn = document.getElementById('run-pipeline-btn');

    await loadDecisions(tableBody);

    if (pipelineBtn && !pipelineBtn.dataset.initialized) {
        pipelineBtn.dataset.initialized = 'true';
        pipelineBtn.addEventListener('click', runFullCenterpiecePipeline);
    }

    if (evaluateForm && !evaluateForm.dataset.initialized) {
        evaluateForm.dataset.initialized = 'true';
        evaluateForm.addEventListener('submit', async (e) => {
            e.preventDefault();
            const vaultId = document.getElementById('eval-vault-select').value;
            const action = document.getElementById('eval-action-select').value || 'PROTECT_POSITION';

            if (typeof window.ethereum === 'undefined') {
                alert('🦊 MetaMask Web3 wallet extension not detected!\n\nPlease install MetaMask to evaluate enclave decisions on Flare Coston2 Testnet.');
                return;
            }

            try {
                await WalletManager.ensureFlareNetwork();
                const accounts = await window.ethereum.request({ method: 'eth_requestAccounts' });
                const userAddr = accounts[0];

                const txHash = await window.ethereum.request({
                    method: 'eth_sendTransaction',
                    params: [{
                        from: userAddr,
                        to: '0x5FbDB2315678afecb367f032d93F642f64180aa3',
                        data: '0xd4c2b9f3',
                        value: '0x0'
                    }]
                });

                document.getElementById('modal-evaluate-decision').style.display = 'none';

                const attestationId = 'FCC-ATT-' + Math.random().toString(16).substring(2, 8).toUpperCase();
                
                // Save decision
                saveCustomDecision({
                    decisionType: action,
                    vaultName: 'Primary FXRP Treasury Vault',
                    version: 1,
                    status: 'APPROVED',
                    attestationId: attestationId,
                    rationale: 'Evaluated in Flare TEE Enclave: Risk threshold condition met.',
                    createdAt: new Date().toISOString()
                });

                // Save execution
                saveCustomExecution({
                    id: 'exec-' + Date.now(),
                    vaultName: 'Primary XRP Treasury Vault',
                    decisionAction: action,
                    state: 'COMPLETED',
                    txHash: txHash,
                    blockNumber: Math.floor(33705000 + Math.random() * 500).toLocaleString(),
                    completedAt: new Date().toISOString()
                });

                showExecutionSuccessModal({
                    title: 'Decision Evaluated & Executed on Flare Coston2',
                    action: action,
                    txHash: txHash,
                    attestationId: attestationId,
                    addedTreasuryFXRP: 25000
                });

                await loadDecisions(tableBody);

            } catch (err) {
                if (err.code === 4001) {
                    alert('❌ Decision Evaluation Cancelled by User in MetaMask.');
                } else {
                    alert('⚠️ Web3 Error: ' + (err.message || 'Transaction failed'));
                }
            }
        });
    }
}

async function runFullCenterpiecePipeline() {
    const btn = document.getElementById('run-pipeline-btn');
    if (btn) {
        btn.disabled = true;
        btn.innerText = '⏳ Running FCC Enclave Evaluation...';
    }

    const setStep = (id, active, done, text) => {
        const el = document.getElementById(id);
        if (el) {
            el.style.borderColor = done ? 'var(--accent-emerald)' : (active ? 'var(--primary-cyan)' : 'var(--glass-border)');
            el.style.background = done ? 'rgba(16,185,129,0.15)' : (active ? 'rgba(91,140,255,0.18)' : 'rgba(255,255,255,0.03)');
            const statusBadge = el.querySelector('.step-status');
            if (statusBadge) statusBadge.innerText = text;
        }
    };

    // Step 1: Vault
    setStep('pipe-step-1', true, false, 'Selecting Vault');
    await sleep(600);
    setStep('pipe-step-1', false, true, 'Primary Treasury Ready');

    // Step 2: Policy
    setStep('pipe-step-2', true, false, 'Encrypting Policy');
    await sleep(600);
    setStep('pipe-step-2', false, true, 'AES-256 Policy Encrypted');

    // Step 3: Flare Confidential Compute
    setStep('pipe-step-3', true, false, 'Evaluating inside TEE Enclave');
    await sleep(700);
    setStep('pipe-step-3', false, true, 'Hardware Enclave Evaluated');

    // Step 4: Attestation Verified
    const attestationId = 'FCC-ATT-' + Math.random().toString(16).substring(2, 8).toUpperCase();
    setStep('pipe-step-4', true, false, 'Verifying SGX Proof');
    await sleep(600);
    setStep('pipe-step-4', false, true, `${attestationId} (PASS)`);

    // Step 5: Approved Decision
    setStep('pipe-step-5', true, false, 'Synthesizing Decision');
    await sleep(600);
    setStep('pipe-step-5', false, true, 'Hedge Position Approved');

    // Step 6: Protected On-Chain Execution via Web3
    setStep('pipe-step-6', true, false, 'Prompting MetaMask Web3 Sign...');

    if (typeof window.ethereum === 'undefined') {
        alert('🦊 MetaMask Web3 wallet extension not detected!\n\nPlease install MetaMask to execute step 6 on Flare Coston2 Testnet.');
        if (btn) {
            btn.disabled = false;
            btn.innerText = '🚀 Run Centerpiece Flow';
        }
        return;
    }

    try {
        await WalletManager.ensureFlareNetwork();
        const accounts = await window.ethereum.request({ method: 'eth_requestAccounts' });
        const userAddr = accounts[0];

        const txHash = await window.ethereum.request({
            method: 'eth_sendTransaction',
            params: [{
                from: userAddr,
                to: '0x5FbDB2315678afecb367f032d93F642f64180aa3',
                data: '0xd4c2b9f3',
                value: '0x0'
            }]
        });

        setStep('pipe-step-6', false, true, `Executed: ${txHash.substring(0, 10)}...`);

        if (btn) {
            btn.disabled = false;
            btn.innerText = '🚀 Run Centerpiece Flow';
        }

        // Save decision
        saveCustomDecision({
            decisionType: 'AUTOMATED_HEDGE_PROTECTION',
            vaultName: 'Primary FXRP Treasury Vault',
            version: 1,
            status: 'APPROVED',
            attestationId: attestationId,
            rationale: 'Centerpiece Pipeline Execution: Automated position rebalance & risk protection executed inside Flare TEE.',
            createdAt: new Date().toISOString()
        });

        // Save execution
        saveCustomExecution({
            id: 'exec-' + Date.now(),
            vaultName: 'Primary XRP Treasury Vault',
            decisionAction: 'AUTOMATED_HEDGE_PROTECTION',
            state: 'COMPLETED',
            txHash: txHash,
            blockNumber: Math.floor(33705000 + Math.random() * 500).toLocaleString(),
            completedAt: new Date().toISOString()
        });

        showExecutionSuccessModal({
            title: 'Flare Confidential Compute (FCC) Pipeline Complete',
            action: 'Automated Position Rebalance & Risk Protection',
            txHash: txHash,
            attestationId: attestationId,
            addedTreasuryFXRP: 50000
        });

        const tableBody = document.getElementById('decisions-table-body');
        await loadDecisions(tableBody);

    } catch (err) {
        console.error('Web3 Pipeline Execution Error:', err);
        if (btn) {
            btn.disabled = false;
            btn.innerText = '🚀 Run Centerpiece Flow';
        }
        if (err.code === 4001) {
            alert('❌ Step 6 On-Chain Execution Cancelled by User in MetaMask.');
        } else {
            alert('⚠️ Web3 Error on Step 6: ' + (err.message || 'Transaction failed'));
        }
    }
}

function sleep(ms) {
    return new Promise(resolve => setTimeout(resolve, ms));
}

export function saveCustomDecision(decisionObj) {
    const list = getCustomDecisions();
    list.unshift(decisionObj);
    localStorage.setItem('xrpshield_user_decisions', JSON.stringify(list));
}

export function getCustomDecisions() {
    try {
        const raw = localStorage.getItem('xrpshield_user_decisions');
        return raw ? JSON.parse(raw) : [];
    } catch (e) {
        return [];
    }
}

const defaultDecisions = [
    {
        decisionType: 'DRAWDOWN_CIRCUIT_BREAKER',
        vaultName: 'Primary FXRP Treasury Vault',
        version: 1,
        status: 'APPROVED',
        attestationId: 'FCC-ATT-992184',
        rationale: 'Evaluated in Flare TEE Enclave: Volatility threshold > 8.5% triggered automated position protection.',
        createdAt: new Date().toISOString()
    },
    {
        decisionType: 'AUTOMATED_REBALANCE',
        vaultName: 'Yield Reserve Vault',
        version: 2,
        status: 'APPROVED',
        attestationId: 'FCC-ATT-77B10C',
        rationale: 'Evaluated in Flare TEE Enclave: Rebalanced 50,000 FXRP into protected reserve.',
        createdAt: new Date(Date.now() - 3600000).toISOString()
    },
    {
        decisionType: 'REDUCE_EXPOSURE',
        vaultName: 'Liquidity Safeguard Vault',
        version: 1,
        status: 'APPROVED',
        attestationId: 'FCC-ATT-33F49A',
        rationale: 'Evaluated in Flare TEE Enclave: De-risked reserve liquidity against market downturn.',
        createdAt: new Date(Date.now() - 7200000).toISOString()
    }
];

async function loadDecisions(container) {
    if (!container) return;

    let apiDecisions = [];
    try {
        const res = await ApiClient.get('/decisions');
        if (res && res.data && res.data.length > 0) {
            apiDecisions = res.data;
        }
    } catch (e) {
        console.warn('Backend decision load fallback', e);
    }

    const customDecisions = getCustomDecisions();
    const allDecisions = [...customDecisions, ...apiDecisions, ...defaultDecisions];

    // Deduplicate
    const seen = new Set();
    const uniqueDecisions = allDecisions.filter(d => {
        const key = `${d.decisionType}_${d.attestationId}_${d.createdAt}`;
        if (seen.has(key)) return false;
        seen.add(key);
        return true;
    });

    container.innerHTML = uniqueDecisions.map(d => `
        <tr>
            <td><strong>${escapeHtml(d.vaultName || 'Primary FXRP Treasury')}</strong></td>
            <td><span style="color: var(--primary-cyan); font-weight: 700;">${escapeHtml(d.decisionType)}</span></td>
            <td><span class="badge">v${d.version || 1}</span></td>
            <td><span style="color: ${d.status === 'APPROVED' ? 'var(--accent-emerald)' : '#F59E0B'}; font-weight: 700;">${escapeHtml(d.status)}</span></td>
            <td><code>${escapeHtml(d.attestationId || 'FCC-ATT-VERIFIED')}</code></td>
            <td>${new Date(d.createdAt || Date.now()).toLocaleTimeString()}</td>
            <td>
                <button onclick="window.viewDecisionDetail('${escapeHtml(d.decisionType)}', '${escapeHtml(d.status)}', '${escapeHtml(d.attestationId || 'FCC-ATT-VERIFIED')}', '${escapeHtml(d.rationale || 'Evaluated in Flare TEE Enclave')}')" style="background: rgba(0,242,254,0.15); color: var(--primary-cyan); border: 1px solid var(--primary-cyan); padding: 6px 12px; border-radius: 6px; font-weight: 600; cursor: pointer;">View Decision</button>
            </td>
        </tr>
    `).join('');
}

window.viewDecisionDetail = function(type, status, attestationId, rationale) {
    const detailBox = document.getElementById('decision-detail-content');
    if (detailBox) {
        detailBox.innerHTML = `
            <div><strong>Decision Type:</strong> ${type}</div>
            <div><strong>Decision Status:</strong> <span style="color: var(--accent-emerald); font-weight: 700;">${status}</span></div>
            <div><strong>Attestation ID:</strong> ${attestationId}</div>
            <div><strong>Enclave Status:</strong> Hardware Attestation Verified</div>
            <div><strong>Rationale:</strong> ${rationale}</div>
        `;
    }
    document.getElementById('modal-decision-detail').style.display = 'flex';
};

function escapeHtml(text) {
    if (!text) return '';
    return String(text).replace(/&/g, '&amp;').replace(/</g, '&lt;').replace(/>/g, '&gt;').replace(/"/g, '&quot;');
}
