export const CONFIG = {
    API_BASE_URL: (window.CONFIG && window.CONFIG.API_BASE_URL) 
        ? window.CONFIG.API_BASE_URL 
        : (window.location.hostname === 'localhost' || window.location.hostname === '127.0.0.1')
            ? 'http://localhost:8081/api/v1'
            : 'https://xrpshield-production.up.railway.app/api/v1',

    FLARE_NETWORK: {
        NAME: 'Flare Coston2 Testnet',
        CHAIN_ID: 114,
        RPC_URL: 'https://coston2-api.flare.network/ext/C/rpc',
        EXPLORER: 'https://coston2-explorer.flare.network'
    },
    APP_VERSION: '1.0.0'
};

