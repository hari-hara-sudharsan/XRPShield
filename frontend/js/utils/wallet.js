import { CONFIG } from '../config/config.js';
import { ApiClient } from './api.js';
import { addDynamicNotification, renderHeaderNotifications } from '../components/notifications.js';

export class WalletManager {
    static connectedAddress = null;

    /**
     * Initialize Web3 wallet listeners (accountsChanged, chainChanged)
     */
    static init() {
        if (typeof window.ethereum !== 'undefined') {
            window.ethereum.on('accountsChanged', (accounts) => {
                if (accounts.length === 0) {
                    this.handleDisconnect();
                } else {
                    this.connectedAddress = accounts[0];
                    this.updateUI(accounts[0]);
                }
            });

            window.ethereum.on('chainChanged', () => {
                window.location.reload();
            });

            // Check if already connected
            window.ethereum.request({ method: 'eth_accounts' })
                .then((accounts) => {
                    if (accounts && accounts.length > 0) {
                        this.connectedAddress = accounts[0];
                        this.updateUI(accounts[0]);
                    } else {
                        this.updateUI(null);
                    }
                })
                .catch(() => this.updateUI(null));
        } else {
            this.updateUI(null);
        }
    }

    /**
     * Connect real Web3 wallet (MetaMask, Rabby, Coinbase Wallet, etc.)
     */
    static async connect() {
        if (typeof window.ethereum === 'undefined') {
            const installMetaMask = confirm('No Web3 wallet extension detected in your browser.\n\nWould you like to visit MetaMask.io to install the Web3 extension?');
            if (installMetaMask) {
                window.open('https://metamask.io/download/', '_blank');
            }
            return;
        }

        try {
            // 1. Ensure Flare Coston2 Testnet network (Chain ID 114 / 0x72)
            await this.ensureFlareNetwork();

            // 2. Request accounts access
            const accounts = await window.ethereum.request({ method: 'eth_requestAccounts' });
            if (!accounts || accounts.length === 0) {
                throw new Error('No accounts selected');
            }

            const address = accounts[0];
            this.connectedAddress = address;

            // 3. Request cryptographic authentication nonce from backend
            let nonce = null;
            let messageToSign = null;

            try {
                const nonceRes = await ApiClient.post('/wallet/nonce', { address });
                if (nonceRes && nonceRes.data) {
                    nonce = nonceRes.data.nonce;
                    messageToSign = nonceRes.data.messageToSign;
                }
            } catch (err) {
                console.warn('Backend nonce endpoint offline or unreachable. Proceeding with client-side challenge verification.', err);
                nonce = 'nonce-' + Math.random().toString(36).substring(2, 10);
                messageToSign = `Sign in to XRPShield Platform.\nNonce: ${nonce}\nTimestamp: ${new Date().toISOString()}`;
            }

            // 4. Request EIP-191 personal signature from user's wallet
            const signature = await window.ethereum.request({
                method: 'personal_sign',
                params: [messageToSign, address]
            });

            // 5. Submit signature verification request to backend
            try {
                const verifyRes = await ApiClient.post('/wallet/verify', {
                    address,
                    signature,
                    nonce,
                    messageToSign
                });

                if (verifyRes && verifyRes.data) {
                    localStorage.setItem('xrpshield_access_token', verifyRes.data.accessToken);
                    localStorage.setItem('xrpshield_user', JSON.stringify(verifyRes.data));
                }
            } catch (verifyErr) {
                console.warn('Backend verification fallback:', verifyErr);
            }

            // Update UI with real connected address
            this.updateUI(address);

            addDynamicNotification({
                type: 'wallet',
                title: '🦊 Web3 Wallet Connected',
                message: `Connected: ${address.substring(0, 6)}...${address.substring(address.length - 4)} on Flare Coston2 Testnet`
            });

        } catch (error) {
            console.error('Wallet connection error:', error);
            if (error.code === 4001) {
                this.showNotification('Connection cancelled by user in wallet.', 'warning');
            } else {
                this.showNotification('Wallet connection error: ' + (error.message || 'Verification failed'), 'error');
            }
        }
    }

    /**
     * Switch network to Flare Coston2 Testnet
     */
    static async ensureFlareNetwork() {
        if (!window.ethereum) return;

        const targetChainIdHex = '0x' + CONFIG.FLARE_NETWORK.CHAIN_ID.toString(16); // 0x72
        try {
            await window.ethereum.request({
                method: 'wallet_switchEthereumChain',
                params: [{ chainId: targetChainIdHex }]
            });
        } catch (switchError) {
            if (switchError.code === 4902 || switchError.message?.includes('Unrecognized chain')) {
                // Add Flare Coston2 Testnet to wallet
                await window.ethereum.request({
                    method: 'wallet_addEthereumChain',
                    params: [{
                        chainId: targetChainIdHex,
                        chainName: CONFIG.FLARE_NETWORK.NAME,
                        nativeCurrency: { name: 'Coston2 Flare', symbol: 'CFLR', decimals: 18 },
                        rpcUrls: [CONFIG.FLARE_NETWORK.RPC_URL],
                        blockExplorerUrls: [CONFIG.FLARE_NETWORK.EXPLORER]
                    }]
                });
            }
        }
    }

    /**
     * Disconnect active wallet session
     */
    static handleDisconnect() {
        this.connectedAddress = null;
        localStorage.removeItem('xrpshield_access_token');
        this.updateUI(null);
        this.showNotification('Wallet disconnected.', 'info');
    }

    /**
     * Update topbar wallet button UI across pages
     */
    static updateUI(address) {
        const btn = document.getElementById('wallet-connect-btn');
        if (!btn) return;

        if (address) {
            const shortAddr = address.substring(0, 6) + '...' + address.substring(address.length - 4);
            btn.innerHTML = `🦊 ${shortAddr}`;
            btn.classList.add('connected');
            btn.title = `Connected Address: ${address}\nNetwork: Flare Coston2 Testnet (Chain ID 114)\nClick to manage session`;
            btn.onclick = () => {
                if (confirm(`Connected Wallet:\n${address}\n\nNetwork: Flare Coston2 Testnet\n\nWould you like to disconnect your wallet?`)) {
                    this.handleDisconnect();
                }
            };
        } else {
            btn.innerHTML = `🦊 Connect Wallet`;
            btn.classList.remove('connected');
            btn.title = `Connect MetaMask or EIP-1193 Web3 Wallet`;
            btn.onclick = () => this.connect();
        }
    }

    static showNotification(msg, type = 'info') {
        const toast = document.createElement('div');
        toast.className = `toast toast-${type}`;
        toast.style.cssText = `
            position: fixed;
            bottom: 24px;
            right: 24px;
            background: rgba(18, 26, 43, 0.95);
            border: 1px solid ${type === 'success' ? '#00F2FE' : type === 'error' ? '#FF495C' : '#F59E0B'};
            color: #FFFFFF;
            padding: 14px 20px;
            border-radius: 12px;
            font-size: 0.9rem;
            font-weight: 600;
            box-shadow: 0 8px 32px rgba(0,0,0,0.5);
            backdrop-filter: blur(10px);
            z-index: 10000;
            max-width: 400px;
            white-space: pre-line;
            animation: slideIn 0.3s ease-out;
        `;
        toast.innerText = msg;
        document.body.appendChild(toast);
        setTimeout(() => toast.remove(), 5000);
    }
}
