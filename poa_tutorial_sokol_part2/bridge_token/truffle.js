require('dotenv').config();
const HDWalletProvider = require("truffle-hdwallet-provider");

module.exports = {
  networks: {
    development: {
      host: "localhost",
      port: 8545,
      network_id: "*"
    },
    rinkeby: {
        provider: new HDWalletProvider(process.env.MNEMONIC, "https://rinkeby.infura.io/" + process.env.INFURA_API_KEY),
        network_id: 4,
        gas: 4500000
    }
  }
};