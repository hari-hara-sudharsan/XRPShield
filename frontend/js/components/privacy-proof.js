import { CONFIG } from '../config/config.js';
import { showExecutionSuccessModal } from '../utils/execution-modal.js';

export function initPrivacyProof() {
    console.log('Initializing Privacy Proof Component...');

    const sampleTxHash = '0x4f3edf983ac636a65a842ce7c78d9aa706d3b113bce9c46f30d7d21715b23b1d';
    const sampleAttestationHash = '0x6f7c4df64308f102e77796841daffd60c5296b379bacdea12f2c832061e144c2';
    const samplePolicyHash = '0xa47f89e200000000000000000000000000000000000000000000000000000000';

    // 1. View Transaction Button
    document.getElementById('proof-btn-view-tx')?.addEventListener('click', () => {
        window.open(`${CONFIG.FLARE_NETWORK.EXPLORER}/tx/${sampleTxHash}`, '_blank');
    });

    // 2. View Attestation Button
    const handleAttestationView = () => {
        showExecutionSuccessModal({
            title: 'Flare Confidential Compute Attestation Proof',
            action: 'EIP-712 TEE Signature On-Chain Verification',
            txHash: sampleTxHash,
            attestationId: sampleAttestationHash,
            callData: CONFIG.CONTRACTS.SELECTORS.VERIFY_ATTESTATION_ON_CHAIN
        });
    };

    document.getElementById('proof-btn-view-attestation')?.addEventListener('click', handleAttestationView);
    document.getElementById('proof-btn-view-attestation-2')?.addEventListener('click', handleAttestationView);

    // 3. View Policy Commitment Hash Button
    document.getElementById('proof-btn-view-commitment')?.addEventListener('click', () => {
        alert(`📜 On-Chain Policy Commitment Hash:\n\n${samplePolicyHash}\n\nRegistered on VaultManager.sol at address:\n${CONFIG.CONTRACTS.VAULT_MANAGER}\n\nThis 32-byte Keccak256 hash proves the exact policy evaluated inside the TEE enclave without exposing drawdown rules or internal thresholds!`);
    });

    // 4. View Execution Receipt Button
    document.getElementById('proof-btn-view-execution')?.addEventListener('click', () => {
        showExecutionSuccessModal({
            title: 'Flare Coston2 On-Chain DEX Execution Receipt',
            action: 'DEX Hedge Swap (100 FXRP → 102.25 USD₮0)',
            txHash: sampleTxHash,
            attestationId: 'FCC-DEX-SWAP-EXECUTED',
            callData: CONFIG.CONTRACTS.SELECTORS.EXECUTE_HEDGE
        });
    });
}
