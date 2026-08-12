// SPDX-License-Identifier: MIT
pragma solidity 0.8.24;

/**
 * @title IFtsoV2
 * @dev Interface for Flare Time Series Oracle v2 (FTSOv2) block-latency feeds on Flare Network
 */
interface IFtsoV2 {
    /**
     * @notice Fetch live price data for specified feed ID
     * @param _feedId 21-byte Feed Identifier (e.g. 0x015852502f55534400000000000000000000000000 for XRP/USD)
     * @return _value Raw price value
     * @return _decimals Number of decimal places
     * @return _timestamp Unix timestamp when feed was updated
     */
    function getFeedById(bytes21 _feedId) external view returns (uint256 _value, int8 _decimals, uint64 _timestamp);
}
