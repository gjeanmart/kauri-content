// 2_deploy_contract.js
const JavaToken = artifacts.require("./JavaToken.sol");

module.exports = function(deployer, network, accounts) {
    // Deploy the smart contract
    deployer.deploy(JavaToken, {from: accounts[0]}).then(function(instance) {
        // Mint 100 tokens
        return instance.mint(accounts[0], web3.utils.toBN("100"), {from: accounts[0]});
    }); 
};
