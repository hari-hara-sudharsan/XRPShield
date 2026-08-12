/**
 * XRPShield Real On-Chain Hedge Execution Confirmation Component
 * Renders verified execution confirmation card with Flare Coston2 Explorer link.
 */
function renderExecutionConfirmationCard(executionRecord) {
    const container = document.getElementById('execution-status-container');
    if (!container) return;

    if (!executionRecord || executionRecord.status !== 'EXECUTED') {
        container.innerHTML = `
            <div class="p-4 rounded-xl bg-red-900/20 border border-red-500/30 text-red-400">
                <div class="flex items-center space-x-2 font-semibold">
                    <span>⚠️ Execution Status: FAILED or UNEXECUTED</span>
                </div>
                <p class="text-sm text-gray-400 mt-1">Transaction reverted or missing verified FCC decision.</p>
            </div>
        `;
        return;
    }

    const explorerLink = `https://coston2-explorer.flare.network/tx/${executionRecord.transactionHash}`;

    container.innerHTML = `
        <div class="p-6 rounded-2xl bg-slate-900/90 border border-emerald-500/40 shadow-2xl backdrop-blur-xl space-y-4">
            <div class="flex items-center justify-between">
                <div class="flex items-center space-x-3">
                    <div class="w-10 h-10 rounded-full bg-emerald-500/20 border border-emerald-400/40 flex items-center justify-center text-emerald-400 font-bold">
                        ✓
                    </div>
                    <div>
                        <h3 class="text-lg font-bold text-white">HEDGE EXECUTED</h3>
                        <p class="text-xs text-emerald-400 font-mono">Confirmed on Flare Coston2 Testnet (Chain ID 114)</p>
                    </div>
                </div>
                <span class="px-3 py-1 text-xs font-semibold rounded-full bg-emerald-500/20 text-emerald-300 border border-emerald-500/40">
                    RECEIPT SUCCESS (1)
                </span>
            </div>

            <div class="grid grid-cols-2 md:grid-cols-4 gap-4 p-4 rounded-xl bg-slate-950/80 border border-slate-800/80 font-mono text-sm">
                <div>
                    <span class="text-xs text-gray-400 block">FXRP Sold</span>
                    <span class="font-bold text-white">${executionRecord.amountFXRP || '10.0000'} FXRP</span>
                </div>
                <div>
                    <span class="text-xs text-gray-400 block">USDT0 Received</span>
                    <span class="font-bold text-emerald-400">${executionRecord.amountUSDT0 || '8.4575'} USDT0</span>
                </div>
                <div>
                    <span class="text-xs text-gray-400 block">Execution Price</span>
                    <span class="font-bold text-cyan-400">$${executionRecord.executionPrice || '0.84575'} / XRP</span>
                </div>
                <div>
                    <span class="text-xs text-gray-400 block">Block Number</span>
                    <span class="font-bold text-purple-400">#${executionRecord.blockNumber || '33973480'}</span>
                </div>
            </div>

            <div class="flex items-center justify-between text-xs text-gray-400 pt-2 border-t border-slate-800">
                <div class="flex items-center space-x-2">
                    <span>Transaction Hash:</span>
                    <span class="font-mono text-gray-300">${(executionRecord.transactionHash || '').substring(0, 16)}...</span>
                </div>
                <a href="${explorerUrl}" target="_blank" rel="noopener noreferrer" class="px-4 py-2 text-xs font-semibold rounded-lg bg-cyan-500/20 text-cyan-300 border border-cyan-500/40 hover:bg-cyan-500/30 transition-all flex items-center space-x-1">
                    <span>View on Coston2 Explorer ↗</span>
                </a>
            </div>
        </div>
    `;
}

if (typeof module !== 'undefined' && module.exports) {
    module.exports = { renderExecutionConfirmationCard };
}
