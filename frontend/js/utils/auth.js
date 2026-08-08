import { CONFIG } from '../config/config.js';
import { ApiClient } from './api.js';

export class AuthManager {
    static getAccessToken() {
        return localStorage.getItem('xrpshield_access_token');
    }

    static setSession(authData) {
        if (authData && authData.accessToken) {
            localStorage.setItem('xrpshield_access_token', authData.accessToken);
            localStorage.setItem('xrpshield_refresh_token', authData.refreshToken);
            localStorage.setItem('xrpshield_user', JSON.stringify(authData));
        }
    }

    static logout() {
        localStorage.removeItem('xrpshield_access_token');
        localStorage.removeItem('xrpshield_refresh_token');
        localStorage.removeItem('xrpshield_user');
        window.location.hash = 'login';
    }

    static getUser() {
        const userStr = localStorage.getItem('xrpshield_user');
        return userStr ? JSON.parse(userStr) : null;
    }

    static isAuthenticated() {
        return !!this.getAccessToken();
    }

    static async connectMetaMask() {
        if (!window.ethereum) {
            alert('MetaMask browser extension is not installed. Please install MetaMask to continue.');
            return;
        }

        try {
            const accounts = await window.ethereum.request({ method: 'eth_requestAccounts' });
            const address = accounts[0];

            // 1. Fetch challenge nonce from backend
            const nonceResponse = await ApiClient.post('/wallet/nonce', { address });
            if (!nonceResponse || !nonceResponse.data) {
                throw new Error('Failed to retrieve authentication nonce from server');
            }

            const { nonce, messageToSign } = nonceResponse.data;

            // 2. Request EIP-191 personal signature from MetaMask
            const signature = await window.ethereum.request({
                method: 'personal_sign',
                params: [messageToSign, address]
            });

            // 3. Verify signature with backend
            const authResponse = await ApiClient.post('/wallet/verify', {
                address,
                signature,
                nonce
            });

            if (authResponse && authResponse.success) {
                this.setSession(authResponse.data);
                alert(`MetaMask Wallet Verified Successfully!\nConnected: ${address}`);
                window.location.hash = 'dashboard';
                location.reload();
            }
        } catch (error) {
            console.error('MetaMask wallet authentication error:', error);
            alert('Wallet authentication failed: ' + (error.message || 'User rejected signature request'));
        }
    }
}
