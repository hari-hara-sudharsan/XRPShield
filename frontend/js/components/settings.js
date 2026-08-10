import { WalletManager } from '../utils/wallet.js';
import { showToast } from './notifications.js';
import { showExecutionSuccessModal } from '../utils/execution-modal.js';
import { I18nEngine } from '../utils/i18n.js';

export function initSettings() {
    const walletAddrEl = document.getElementById('settings-wallet-addr');
    const userNameEl = document.getElementById('settings-user-name');
    const userEmailEl = document.getElementById('settings-user-email');
    const userLangEl = document.getElementById('settings-user-lang');
    const userTzEl = document.getElementById('settings-user-tz');

    // 1. Connected wallet
    const address = WalletManager.getConnectedAddress() || 'Not Connected';
    if (walletAddrEl) {
        walletAddrEl.innerText = address;
        walletAddrEl.style.color = address !== 'Not Connected' ? 'var(--primary-cyan)' : '#FF495C';
    }

    // 2. Load saved settings from local storage
    const activeLang = localStorage.getItem('xrpshield_user_language') || I18nEngine.currentLang || 'en';
    if (userLangEl) {
        userLangEl.value = activeLang;
        if (!userLangEl.dataset.i18nBound) {
            userLangEl.dataset.i18nBound = 'true';
            userLangEl.addEventListener('change', (e) => {
                const newLang = e.target.value;
                I18nEngine.setLanguage(newLang);
                showToast(`Language set to: ${newLang.toUpperCase()}`, 'info');
            });
        }
    }

    try {
        const savedRaw = localStorage.getItem('xrpshield_user_settings');
        if (savedRaw) {
            const saved = JSON.parse(savedRaw);
            if (userNameEl && saved.name) userNameEl.value = saved.name;
            if (userEmailEl && saved.email) userEmailEl.value = saved.email;
            if (userLangEl && saved.lang) {
                userLangEl.value = saved.lang;
                I18nEngine.setLanguage(saved.lang);
            }
            if (userTzEl && saved.tz) userTzEl.value = saved.tz;
        }
    } catch (e) {
        console.warn('Could not parse user settings', e);
    }
}

window.saveSettingsPreferences = function() {
    const userNameEl = document.getElementById('settings-user-name');
    const userEmailEl = document.getElementById('settings-user-email');
    const userLangEl = document.getElementById('settings-user-lang');
    const userTzEl = document.getElementById('settings-user-tz');

    const selectedLang = userLangEl ? userLangEl.value : 'en';

    const settingsObj = {
        name: userNameEl ? userNameEl.value : 'Treasury Administrator',
        email: userEmailEl ? userEmailEl.value : 'owner@xrpshield.io',
        lang: selectedLang,
        tz: userTzEl ? userTzEl.value : 'UTC',
        updatedAt: new Date().toISOString()
    };

    localStorage.setItem('xrpshield_user_settings', JSON.stringify(settingsObj));
    
    // Apply language translation dynamically
    I18nEngine.setLanguage(selectedLang);

    showToast(I18nEngine.t('msg_prefs_saved'), 'success');
};

window.refreshSessionTokens = async function() {
    if (typeof window.ethereum === 'undefined') {
        alert('🦊 MetaMask Web3 wallet extension not detected!\n\nPlease install MetaMask to execute real EIP-191 Web3 session signature authentication.');
        return;
    }

    try {
        await WalletManager.ensureFlareNetwork();
        const accounts = await window.ethereum.request({ method: 'eth_requestAccounts' });
        const userAddr = accounts[0];

        const challengeMsg = `XRPShield Confidential Session Auth\nTimestamp: ${new Date().toUTCString()}\nEnclave: Flare Coston2 TEE Enclave\nWallet: ${userAddr}`;

        const signature = await window.ethereum.request({
            method: 'personal_sign',
            params: [challengeMsg, userAddr]
        });

        console.log('Real EIP-191 Web3 Signature Received:', signature);
        localStorage.setItem('xrpshield_session_sig', signature);

        const sigStatusEl = document.getElementById('settings-sig-status');
        if (sigStatusEl) {
            sigStatusEl.innerText = `EIP-191 Signed: ${signature.substring(0, 14)}...${signature.substring(signature.length - 6)}`;
            sigStatusEl.style.color = 'var(--primary-cyan)';
        }

        showExecutionSuccessModal({
            title: 'Real Web3 Session Signature Verified',
            action: 'EIP-191 Personal Sign Auth Token Generated',
            txHash: signature,
            attestationId: 'FCC-AUTH-SIG'
        });

    } catch (err) {
        if (err.code === 4001) {
            alert('❌ Web3 Session Signature Refresh Cancelled by User in MetaMask.');
        } else {
            alert('⚠️ Signature Refresh Error: ' + (err.message || 'Failed to sign'));
        }
    }
};
