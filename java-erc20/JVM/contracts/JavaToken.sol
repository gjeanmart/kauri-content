// JavaToken.sol
pragma solidity ^0.5.8;

import "openzeppelin-solidity/contracts/token/ERC20/ERC20Mintable.sol";

contract JavaToken is ERC20Mintable {
    string public constant name = "Java Token";
    string public constant symbol = "JVM";
    uint8 public constant decimals = 0;
}
