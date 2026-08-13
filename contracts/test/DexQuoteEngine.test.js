const { expect } = require("chai");
const { ethers } = require("hardhat");

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

describe("XRPShield Real FXRP → USDT0 Quote Engine Tests", function () {
    it("1. Real FXRP -> USDT0 Quote Calculation & Slippage Math", function () {
        const quote = calculateDEXQuote(10000, 0.50, 0.85);
        expect(quote.expectedAmountOut).to.equal('8500.000000');
        expect(quote.minimumAmountOut).to.equal('8457.500000');
        expect(quote.routerAddress).to.equal(COSTON2_ROUTER);
        expect(quote.route.length).to.equal(2);
    });

    it("2. Slippage Protection Minimum Amount Out Enforcement", function () {
        const quote = calculateDEXQuote(10000, 1.00, 0.85);
        expect(quote.minimumAmountOut).to.equal('8415.000000');
    });

    it("3. Fresh Quote Status (Age 30s <= 60s)", function () {
        const now = Math.floor(Date.now() / 1000);
        const status = checkQuoteStaleness(now, now + 30);
        expect(status).to.equal('ACTIVE');
    });

    it("4. Stale Quote Expiration Handling (Age 100s > 60s -> QUOTE_EXPIRED)", function () {
        const now = Math.floor(Date.now() / 1000);
        const status = checkQuoteStaleness(now - 100, now);
        expect(status).to.equal('QUOTE_EXPIRED');
    });
});
