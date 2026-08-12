import { CONFIG } from '../config/config.js';
import { ApiClient } from '../utils/api.js';

export async function fetchLiveFtsoXrpUsdPrice() {
    // 1. Try Backend REST API first
    try {
        const response = await ApiClient.get('/market/xrp-usd');
        if (response && response.success && response.data) {
            return response.data;
        }
    } catch (e) {
        console.warn('Backend FTSOv2 REST endpoint offline, querying Coston2 RPC directly...', e);
    }

    // 2. Direct Web3 RPC eth_call fallback
    try {
        const paddedFeedId = CONFIG.CONTRACTS.XRP_USD_FEED_ID.substring(2) + "00000000000000000000000000";
        const calldata = CONFIG.CONTRACTS.SELECTORS.GET_FEED_BY_ID + paddedFeedId;

        const rpcRes = await fetch(CONFIG.FLARE_NETWORK.RPC_URL, {
            method: 'POST',
            headers: { 'Content-Type': 'application/json' },
            body: JSON.stringify({
                jsonrpc: '2.0',
                method: 'eth_call',
                params: [{ to: CONFIG.CONTRACTS.FTSOV2, data: calldata }, 'latest'],
                id: 1
            })
        });

        const json = await rpcRes.json();
        if (json.result && json.result !== '0x' && json.result.length >= 194) {
            const hex = json.result.substring(2);
            const rawPrice = BigInt('0x' + hex.substring(0, 64));
            const decimals = Number(BigInt('0x' + hex.substring(64, 128)));
            const timestamp = Number(BigInt('0x' + hex.substring(128, 192)));

            const priceNum = Number(rawPrice) / Math.pow(10, decimals);
            const currentEpoch = Math.floor(Date.now() / 1000);
            const freshnessSeconds = Math.max(0, currentEpoch - timestamp);

            return {
                price: priceNum,
                rawPrice: rawPrice.toString(),
                decimals: decimals,
                timestamp: timestamp,
                feedId: CONFIG.CONTRACTS.XRP_USD_FEED_ID,
                source: 'Flare FTSOv2',
                stale: freshnessSeconds > 180,
                freshnessSeconds: freshnessSeconds
            };
        }
    } catch (err) {
        console.error('Direct FTSOv2 Coston2 RPC Query Failed:', err);
    }

    return null; // Return null if FTSOv2 cannot be read — DO NOT substitute fake price!
}

export function renderFtsoPriceWidget(containerId = 'ftso-xrp-widget') {
    const container = document.getElementById(containerId);
    if (!container) return;

    container.innerHTML = `
        <div class="ftso-widget-card" style="background: rgba(18, 26, 43, 0.85); border: 1px solid rgba(0, 242, 254, 0.3); border-radius: 12px; padding: 14px 18px; display: flex; align-items: center; justify-content: space-between; gap: 16px; backdrop-filter: blur(10px);">
            <div>
                <div style="font-size: 0.75rem; color: var(--text-muted); text-transform: uppercase; font-weight: 700; letter-spacing: 0.5px; display: flex; align-items: center; gap: 6px;">
                    <span style="display: inline-block; width: 8px; height: 8px; border-radius: 50%; background: #00F2FE; box-shadow: 0 0 8px #00F2FE;"></span>
                    LIVE XRP/USD (Flare FTSOv2)
                </div>
                <div id="ftso-price-display" style="font-size: 1.4rem; font-weight: 800; color: #FFFFFF; font-family: 'Outfit', monospace; margin-top: 4px;">
                    <span class="pulse-skeleton">Loading FTSOv2...</span>
                </div>
            </div>
            <div style="text-align: right;">
                <div id="ftso-badge" style="font-size: 0.7rem; padding: 4px 8px; border-radius: 6px; font-weight: 700; background: rgba(0, 242, 254, 0.15); color: #00F2FE; border: 1px solid rgba(0, 242, 254, 0.4); display: inline-block;">
                    CONNECTING
                </div>
                <div id="ftso-time-display" style="font-size: 0.7rem; color: var(--text-muted); margin-top: 4px;">
                    --
                </div>
            </div>
        </div>
    `;

    updateFtsoWidget();
    setInterval(updateFtsoWidget, 15000); // Refresh every 15 seconds
}

async function updateFtsoWidget() {
    const priceEl = document.getElementById('ftso-price-display');
    const badgeEl = document.getElementById('ftso-badge');
    const timeEl = document.getElementById('ftso-time-display');

    if (!priceEl) return;

    const data = await fetchLiveFtsoXrpUsdPrice();

    if (!data) {
        priceEl.innerHTML = `<span style="color: #FF495C; font-size: 1.1rem;">ERROR: FTSOv2 Feed Unreachable</span>`;
        if (badgeEl) {
            badgeEl.innerText = 'ERROR';
            badgeEl.style.cssText = 'font-size: 0.7rem; padding: 4px 8px; border-radius: 6px; font-weight: 700; background: rgba(255, 73, 92, 0.15); color: #FF495C; border: 1px solid #FF495C;';
        }
        if (timeEl) {
            timeEl.innerText = 'Coston2 RPC Offline';
        }
        return;
    }

    const formattedPrice = '$' + Number(data.price).toFixed(4);
    priceEl.innerText = formattedPrice;

    if (badgeEl) {
        if (data.stale) {
            badgeEl.innerText = 'STALE DATA';
            badgeEl.style.cssText = 'font-size: 0.7rem; padding: 4px 8px; border-radius: 6px; font-weight: 700; background: rgba(245, 158, 11, 0.15); color: #F59E0B; border: 1px solid #F59E0B;';
        } else {
            badgeEl.innerText = 'FRESH • FTSOv2';
            badgeEl.style.cssText = 'font-size: 0.7rem; padding: 4px 8px; border-radius: 6px; font-weight: 700; background: rgba(16, 185, 129, 0.15); color: #10B981; border: 1px solid #10B981;';
        }
    }

    if (timeEl) {
        const timeStr = new Date(data.timestamp * 1000).toLocaleTimeString();
        timeEl.innerText = `Updated: ${timeStr} (${data.freshnessSeconds}s ago)`;
    }
}
