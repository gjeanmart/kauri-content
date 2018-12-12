// BridgeToken.sol
pragma solidity ^0.4.24;

import "zeppelin-solidity/contracts/token/ERC20/MintableToken.sol";

contract BridgeToken is MintableToken {
    string public constant name = "Bridge Token";
    string public constant symbol = "BRT";
    uint8 public constant decimals = 18;
}