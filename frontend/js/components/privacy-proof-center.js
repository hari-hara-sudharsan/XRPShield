window.PrivacyProofCenter = {
    async fetchProofData() {
        try {
            const res = await fetch('/api/proof/latest');
            if (!res.ok) return null;
            return await res.json();
        } catch (e) {
            console.error("Error fetching privacy proof data:", e);
            return null;
        }
    },

    renderProofCenter(data) {
        if (!data) return '<p style="color:#ef4444;">Unable to load Privacy Proof telemetry.</p>';

        const p = data.policyCommitment;
        const f = data.fccAttestation;
        const e = data.onChainExecution;
        const b = data.privacyBoundary;

        return `
        <div style="display:grid; grid-template-columns: 1fr 1fr; gap:20px; margin-bottom:24px;">
            <!-- 1. POLICY COMMITMENT -->
            <div class="card" style="padding:20px; background:rgba(15,23,42,0.6); border:1px solid var(--border-color);">
                <div style="display:flex; justify-content:space-between; align-items:center; margin-bottom:14px;">
                    <h3 style="font-size:1.1rem; font-weight:700; color:var(--accent-teal);">1. Policy Commitment</h3>
                    <span class="badge" style="background:#10b98122; color:#10b981; border:1px solid #10b981;">VERIFIED HASH</span>
                </div>
                <div style="font-size:0.9rem; display:flex; flex-direction:column; gap:8px;">
                    <div><strong>Commitment Hash:</strong> <code style="word-break:break-all;">${p.commitmentHash}</code></div>
                    <div><strong>Vault Address:</strong> <a href="https://coston2-explorer.flare.network/address/${p.vaultAddress}" target="_blank" style="color:var(--accent-teal); word-break:break-all;">${p.vaultAddress} ↗</a></div>
                    <div><strong>Policy Version:</strong> ${p.policyVersion}</div>
                    <div><strong>Publicly Revealed:</strong> <span style="color:#10b981;">${p.revealedPublicly}</span></div>
                    <div><strong>Confidential Private Parameters:</strong></div>
                    <ul style="margin-left:20px; color:var(--text-secondary); font-size:0.85rem;">
                        ${p.confidentialPrivate.map(item => `<li style="color:#10b981;">✓ ${item}</li>`).join('')}
                    </ul>
                </div>
            </div>

            <!-- 2. FCC ENCLAVE ATTESTATION -->
            <div class="card" style="padding:20px; background:rgba(15,23,42,0.6); border:1px solid var(--border-color);">
                <div style="display:flex; justify-content:space-between; align-items:center; margin-bottom:14px;">
                    <h3 style="font-size:1.1rem; font-weight:700; color:var(--accent-teal);">2. Confidential Compute (FCC)</h3>
                    <span class="badge" style="background:#10b98122; color:#10b981; border:1px solid #10b981;">STATUS: ${f.resultStatus}</span>
                </div>
                <div style="font-size:0.9rem; display:flex; flex-direction:column; gap:8px;">
                    <div><strong>Instruction ID:</strong> <code style="word-break:break-all;">${f.instructionId}</code></div>
                    <div><strong>Verification Status:</strong> <span style="color:#10b981;">${f.verificationStatus}</span></div>
                    <div><strong>Attestation Signer:</strong> <a href="https://coston2-explorer.flare.network/address/${f.attestationSigner}" target="_blank" style="color:var(--accent-teal); word-break:break-all;">${f.attestationSigner} ↗</a></div>
                    <div><strong>Approved Max Hedge Cap:</strong> ${f.approvedHedgeCap}</div>
                    <div><strong>TEE Memory State:</strong> Hardware Isolated (Intel SGX / AMD SEV)</div>
                </div>
            </div>
        </div>

        <!-- 3. ON-CHAIN EXECUTION & EXPLORER -->
        <div class="card" style="padding:20px; margin-bottom:24px; background:rgba(15,23,42,0.6); border:1px solid var(--border-color);">
            <div style="display:flex; justify-content:space-between; align-items:center; margin-bottom:14px;">
                <h3 style="font-size:1.1rem; font-weight:700; color:var(--accent-teal);">3. On-Chain DEX Execution & Token Transfers</h3>
                <a href="${e.explorerUrl}" target="_blank" class="btn btn-secondary" style="padding:6px 14px; font-size:0.85rem;">View On Coston2 Explorer ↗</a>
            </div>
            <div style="display:grid; grid-template-columns: 1fr 1fr 1fr; gap:16px; font-size:0.9rem;">
                <div><strong>Transaction Hash:</strong> <code style="word-break:break-all;">${e.transactionHash}</code></div>
                <div><strong>Block Number:</strong> #${e.blockNumber}</div>
                <div><strong>Execution Status:</strong> <span style="color:#10b981;">${e.executionStatus}</span></div>
                <div><strong>FXRP Swapped:</strong> ${e.amountFXRP} FXRP</div>
                <div><strong>USDT0 Received:</strong> ${e.amountUSDT0} USDT0</div>
                <div><strong>Verified DEX Router:</strong> <a href="https://coston2-explorer.flare.network/address/${e.routerAddress}" target="_blank" style="color:var(--accent-teal); word-break:break-all;">0x600109D9... ↗</a></div>
            </div>
        </div>

        <!-- 4. PRIVACY BOUNDARY TABLE -->
        <div class="card" style="padding:20px; background:rgba(15,23,42,0.6); border:1px solid var(--border-color);">
            <h3 style="font-size:1.1rem; font-weight:700; margin-bottom:14px; color:var(--accent-teal);">4. Privacy Boundary: Public Verifiability vs Private Confidentiality</h3>
            <table class="data-table">
                <thead>
                    <tr>
                        <th>System Parameter / Field</th>
                        <th>Privacy Boundary</th>
                        <th>Cryptographic & Architecture Explanation</th>
                    </tr>
                </thead>
                <tbody>
                    ${b.map(item => `
                        <tr>
                            <td><strong>${item.field}</strong></td>
                            <td><span class="badge" style="background:${item.visibility === 'PUBLIC' ? 'rgba(59,130,246,0.2)' : 'rgba(16,185,129,0.2)'}; color:${item.visibility === 'PUBLIC' ? '#3b82f6' : '#10b981'}; border:1px solid ${item.visibility === 'PUBLIC' ? '#3b82f6' : '#10b981'};">${item.visibility}</span></td>
                            <td style="color:var(--text-secondary); font-size:0.85rem;">${item.explanation}</td>
                        </tr>
                    `).join('')}
                </tbody>
            </table>
        </div>
        `;
    }
};
