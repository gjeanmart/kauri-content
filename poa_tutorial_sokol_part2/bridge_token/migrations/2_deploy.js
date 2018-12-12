const BridgeToken = artifacts.require("./BridgeToken.sol");

module.exports = function(deployer, network, accounts) {
	// Deploy the "BridgeToken" smart contract 
  	deployer.deploy(BridgeToken).then(function(instance) {
  		// Mint 100 tokens to the deployer
  		return instance.mint(accounts[0], 100000000000000000000, {from: accounts[0]});
  	}); 
};