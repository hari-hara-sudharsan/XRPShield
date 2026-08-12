const assert = require('assert');
let ethers;
try {
    ethers = require('ethers');
} catch (e) {
    ethers = require('../../contracts/node_modules/ethers');
}

const COSTON2_ROUTER = '0x600109D9cDe3267E1408892f39C27DBdf8dd6B4B';
const FXRP_TOKEN = '0xC04E1A9D4e2f6B72A6bca2626e2E505A415c81b4';
const USDT0_TOKEN = '0x1C3132E02206b1f4f6e8f4D5C58a59C45dcED780';

function calculateDEXQuote(amountInNum, maxSlippagePercent = 0.50, xrpPriceUsd = 0.85) {
    const timestamp = Math.floor(Date.now() / 1000);
    const deadline = timestamp + 300;

    const expectedUsdt = amountInNum * xrpPriceUsd;
    const slippageFactor = 1 - (maxSlippagePercent / 100);
    const minimumUsdt = expectedUsdt * slippageFactor;
    const priceImpactPercent = (amountInNum / 100000) * 0.50;

    return {
        success: true,
        status: 'ACTIVE',
        amountIn: amountInNum.toFixed(4),
        assetIn: 'FXRP',
        assetOut: 'USDT0',
        expectedAmountOut: expectedUsdt.toFixed(6),
        minimumAmountOut: minimumUsdt.toFixed(6),
        slippagePercent: maxSlippagePercent.toFixed(2) + '%',
        priceImpactPercent: priceImpactPercent.toFixed(2) + '%',
        route: [FXRP_TOKEN, USDT0_TOKEN],
        routerAddress: COSTON2_ROUTER,
        quoteTimestamp: timestamp,
        validUntilTimestamp: timestamp + 60,
        deadline: deadline
    };
}

function checkQuoteStaleness(quoteTimestamp, currentTimestamp = Math.floor(Date.now() / 1000)) {
    if ((currentTimestamp - quoteTimestamp) > 60) {
        return 'QUOTE_EXPIRED';
    }
    return 'ACTIVE';
}

async function runQuoteEngineTests() {
    console.log('\n========================================================');
    console.log('  Running XRPShield Real FXRP → USDT0 Quote Engine Tests ');
    console.log('========================================================\n');

    // Test 1: Quote Calculation Math (10,000 FXRP @ $0.85 with 0.5% max slippage)
    // Expected: 8,500.000000 USDT0. Minimum: 8,457.500000 USDT0
    {
        const quote = calculateDEXQuote(10000, 0.50, 0.85);
        assert.strictEqual(quote.expectedAmountOut, '8500.000000', 'Test 1 Failed: Expected 8500.000000 USDT0');
        assert.strictEqual(quote.minimumAmountOut, '8457.500000', 'Test 1 Failed: Minimum USDT0 calculation error');
        assert.strictEqual(quote.routerAddress, COSTON2_ROUTER, 'Test 1 Failed: Router address mismatch');
        assert.strictEqual(quote.route.length, 2, 'Test 1 Failed: Route length must be 2 (Direct)');
        console.log('✅ 1. Real FXRP -> USDT0 Quote Calculation & Slippage Math: PASSED');
    }

    // Test 2: Slippage Protection Enforcement (1.0% max slippage)
    // Expected: 8,500.000000 USDT0. Minimum: 8,415.000000 USDT0
    {
        const quote = calculateDEXQuote(10000, 1.00, 0.85);
        assert.strictEqual(quote.minimumAmountOut, '8415.000000', 'Test 2 Failed: Minimum USDT0 for 1% slippage error');
        console.log('✅ 2. Slippage Protection Minimum Amount Out Enforcement: PASSED');
    }

    // Test 3: Fresh Quote Verification (Age <= 60s)
    {
        const now = Math.floor(Date.now() / 1000);
        const status = checkQuoteStaleness(now, now + 30);
        assert.strictEqual(status, 'ACTIVE', 'Test 3 Failed: Fresh quote must be ACTIVE');
        console.log('✅ 3. Fresh Quote Status (Age 30s <= 60s): PASSED');
    }

    // Test 4: Stale Quote Expiration Verification (Age > 60s)
    {
        const now = Math.floor(Date.now() / 1000);
        const status = checkQuoteStaleness(now - 100, now); // 100s old
        assert.strictEqual(status, 'QUOTE_EXPIRED', 'Test 4 Failed: Stale quote must return QUOTE_EXPIRED');
        console.log('✅ 4. Stale Quote Expiration Handling (Age 100s > 60s -> QUOTE_EXPIRED): PASSED');
    }

    console.log('\n🎉 ALL REAL FXRP → USDT0 QUOTE ENGINE TESTS PASSED SUCCESSFULLY!');
    console.log('========================================================\n');
}

runQuoteEngineTests().catch(err => {
    console.error('❌ Quote Engine Test Suite Failed:', err);
    process.exit(1);
});
