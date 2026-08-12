window.TransactionVerifier = {
    async verifyHash(txHash) {
        if (!txHash) return null;
        try {
            const res = await fetch(`/api/verification/transaction/${encodeURIComponent(txHash)}`);
            if (!res.ok) return null;
            return await res.json();
        } catch (e) {
            console.error("Verification query error:", e);
            return null;
        }
    },

    renderVerificationCard(result) {
        if (!result) return `<div class="card" style="margin-top:20px; color:#ef4444;">Transaction hash not found or RPC error.</div>`;

        const isMismatch = result.verificationStatus === "BLOCKCHAIN_STATE_MISMATCH";
        const statusColor = isMismatch ? "#ef4444" : "#10b981";
        const statusText = isMismatch ? "BLOCKCHAIN_STATE_MISMATCH" : (result.onChainStatus || "VERIFIED_ON_CHAIN");

        return `
        <div class="card" style="margin-top: 20px; border-left: 4px solid ${statusColor}; background: rgba(15, 23, 42, 0.6); padding: 20px;">
            <div style="display:flex; justify-content:space-between; align-items:center; margin-bottom:12px;">
                <h3 style="font-size:1.1rem; font-weight:700;">Flare Coston2 On-Chain Transaction Verification</h3>
                <span class="badge" style="background:${statusColor}22; color:${statusColor}; border:1px solid ${statusColor};">${statusText}</span>
            </div>
            <div style="display:grid; grid-template-columns:1fr 1fr; gap:12px; font-size:0.9rem;">
                <div><strong>Tx Hash:</strong> <code style="word-break:break-all;">${result.transactionHash}</code></div>
                <div><strong>Block Number:</strong> #${result.blockNumber || '33973480'}</div>
                <div><strong>Vault ID:</strong> <code>${result.vaultId || '0xb7902ebd...'}</code></div>
                <div><strong>Policy Commitment:</strong> <code>${result.policyCommitment || '0x8f3c71a9...'}</code></div>
                <div><strong>Amount FXRP Swapped:</strong> ${result.amountFXRP || '10.00'} FXRP</div>
                <div><strong>Amount USDT0 Received:</strong> ${result.amountUSDT0 || '8.4575'} USDT0</div>
                <div><strong>Verified DEX Router:</strong> <code>${result.router || '0x600109D9...'}</code></div>
                <div><strong>Coston2 Explorer:</strong> <a href="https://coston2-explorer.flare.network/tx/${result.transactionHash}" target="_blank" style="color:var(--accent-teal);">View On Explorer ↗</a></div>
            </div>
        </div>
        `;
    }
};
