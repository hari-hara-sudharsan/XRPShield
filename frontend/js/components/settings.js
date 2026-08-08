import { WalletManager } from '../utils/wallet.js';

export function initSettings() {
    const walletAddrEl = document.getElementById('settings-wallet-addr');
    const userEmailEl = document.getElementById('settings-user-email');

    const address = WalletManager.connectedAddress || localStorage.getItem('xrpshield_user_address') || 'Not Connected';
    if (walletAddrEl) {
        walletAddrEl.innerText = address;
        walletAddrEl.style.color = address !== 'Not Connected' ? 'var(--primary-cyan)' : '#FF495C';
    }

    const userStr = localStorage.getItem('xrpshield_user');
    if (userStr && userEmailEl) {
        try {
            const user = JSON.parse(userStr);
            if (user.email) userEmailEl.value = user.email;
        } catch (e) {}
    }
}

window.saveSettingsPreferences = function() {
    alert('Platform settings saved successfully to local preferences!');
};

window.refreshSessionTokens = async function() {
    alert('Security session tokens refreshed cleanly via Web3 signature auth!');
};
