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

    CONTRACTS: {
        VAULT_MANAGER: '0xb7902ebdce1d31ddcef6e7f789c1a5611186e8a9',
        HEDGE_EXECUTOR: '0x70997970C51812dc3A010C7d01b50e0d17dc79C8',
        TEE_SIGNER: '0x70997970C51812dc3A010C7d01b50e0d17dc79C8',
        FXRP_TOKEN: '0xC04E1A9D4e2f6B72A6bca2626e2E505A415c81b4',
        FTSOV2: '0xC4e9c78EA53db782E28f28Fdf80BaF59336B304d',
        XRP_USD_FEED_ID: '0x015852502f55534400000000000000000000000000',
        // Common Function Selectors for Web3 eth_call & eth_sendTransaction
        SELECTORS: {
            // ERC-20
            BALANCE_OF: '0x70a08231',    // balanceOf(address)
            ALLOWANCE: '0xdd62ed3e',     // allowance(address,address)
            APPROVE: '0x095ea7b3',       // approve(address,uint256)
            TRANSFER: '0xa9059cbb',      // transfer(address,uint256)
            MINT: '0x40c10f19',          // mint(address,uint256)
            // VaultManager
            DEPOSIT_FXRP: '0xb6b42b10',  // depositFXRP(uint256)
            WITHDRAW_FXRP: '0x2e1a7d4d', // withdrawFXRP(uint256)
            GET_USER_FXRP_BALANCE: '0x444d3701', // getUserFXRPBalance(address)
            REGISTER_POLICY_COMMITMENT_V2: '0xfffdfeb4', // registerPolicyCommitmentV2(...)
            VERIFY_POLICY_COMMITMENT: '0x2ca6f5ef', // verifyPolicyCommitment(address,bytes32)
            VERIFY_ATTESTATION_ON_CHAIN: '0x3564b8ed', // verifyAttestationOnChain(address,bytes32,bytes32)
            EXECUTE_HEDGE_SWAP: '0xbcb4c617', // executeHedgeSwap(address,uint256,uint256,address,uint256)
            EXECUTE_HEDGE: '0x19767ff2', // executeHedge(address,uint256,uint256,address,uint256,bytes32,bytes32,string)
            // FTSOv2
            GET_FEED_BY_ID: '0x93e9f806' // getFeedById(bytes21)
        }
    },
    APP_VERSION: '1.0.0'
};

