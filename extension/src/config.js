const dotenv = require('dotenv');
dotenv.config();

module.exports = {
    PORT: process.env.FCC_PORT || 8090,
    FLARE_RPC_URL: process.env.FLARE_RPC_URL || 'https://coston2-api.flare.network/ext/C/rpc',
    CHAIN_ID: parseInt(process.env.CHAIN_ID || '114', 10),
    // Extension TEE Signing Key
    SIGNER_PRIVATE_KEY: process.env.FCC_SIGNER_PRIVATE_KEY || '0x4c0883a69102937d6231471b5dbb6204fe5129617082792ae468d01a6f362331',
    FTSOV2_CONTRACT_ADDRESS: '0xC4e9c78EA53db782E28f28Fdf80BaF59336B304d',
    XRP_USD_FEED_ID: '0x015852502f55534400000000000000000000000000'
};
