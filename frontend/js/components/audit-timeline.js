window.AuditTimeline = {
    async fetchTimelineData() {
        try {
            const res = await fetch('/api/proof/latest');
            if (!res.ok) return null;
            const json = await res.json();
            return json.auditTimeline || null;
        } catch (e) {
            console.error("Error fetching audit timeline data:", e);
            return null;
        }
    },

    renderTimeline(timeline) {
        if (!timeline || !Array.isArray(timeline)) {
            return '<p style="color:#ef4444;">Unable to load audit timeline.</p>';
        }

        return `
        <div style="position:relative; margin-left:20px; padding-left:24px; border-left:2px solid var(--accent-teal);">
            ${timeline.map((item, idx) => `
                <div style="position:relative; margin-bottom:24px;">
                    <div style="position:absolute; left:-31px; top:2px; width:16px; height:16px; border-radius:50%; background:var(--accent-teal); border:3px solid #0f172a;"></div>
                    <div class="card" style="padding:16px; background:rgba(15,23,42,0.6); border:1px solid var(--border-color);">
                        <div style="display:flex; justify-content:space-between; align-items:center; margin-bottom:6px;">
                            <h4 style="font-size:1rem; font-weight:700; color:#fff;">${item.stage}</h4>
                            <span class="badge" style="background:#10b98122; color:#10b981; border:1px solid #10b981;">VERIFIED ON-CHAIN</span>
                        </div>
                        <div style="font-size:0.85rem; color:var(--text-secondary); display:flex; flex-direction:column; gap:4px;">
                            <div><strong>Details:</strong> ${item.detail}</div>
                            <div><strong>Block Number:</strong> #${item.blockNumber} | <strong>Tx Hash:</strong> <code style="word-break:break-all;">${item.txHash}</code></div>
                            <div><strong>Explorer Verification:</strong> <a href="${item.explorerUrl}" target="_blank" style="color:var(--accent-teal);">View On Flare Coston2 Explorer ↗</a></div>
                        </div>
                    </div>
                </div>
            `).join('')}
        </div>
        `;
    }
};
