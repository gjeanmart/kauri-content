
This article is part of the **POA serie**:

- [POA - Part 1 - Develop and deploy a smart contract]( https://beta.kauri.io/article/549b50d2318741dbba209110bb9e350e)
- POA - Part 2 - Bridge assets between POA sidechain and a mainchain
- POA - Part 3 - Meta-transaction [Coming soon!]

-----------------------------------------------------

# Introduction 

[The POA Bridge](https://bridge.poa.net/) is a solution to transfer asset tokens (Native and ERC20/ERC677 tokens) between two Ethereum chains. 

An asset token usually has two purposes:
- A monetary use where a token can be traded, exchanged or just kept as a long term investment
- An application use where a token can be employed on a Dapp (voting, stacking, playing, etc...)

Both usages require different network properties to enable the best experience, the monetary use may need a strong  network security and liveness and an access to a large network of assets to facilitate trade while the application use needs faster and cheaper transactions for a better user experience.

As part of the layer 2 scalability solutions, sidechain and bridges implement this paradigm of two chains for two usages and try to solve the scalability and UX issues due to the Ethereum mainnet being usually considered too slow (15 tx/sec) and too expensive on gas fees to enable a good user experience for most of the use cases (games or social apps). In this context, the general flow is the following:

1. User buys token x on the mainchain
2. User transfers his tokens to the sidechain via the bridge (double representation: locked on the mainchain and minted on the sidechain)
3. User uses the tokens in a fast and efficient way. Perhaps earn or lose some tokens from other users
4. User decides to exit his tokens from the application and transfer back to the mainchain via the bridge (token unlocked on the mainchain and burned on the sidechain)
5. User sells his tokens on the mainchain

![](https://api.beta.kauri.io:443/ipfs/QmWH44b3xpJk1yEiLok7xPZw991J3SgxGaoQ6nj6a8GUhn)

In this tutorial, we will learn how to deploy a token on the two networks (RinkeBy network as mainchain and POA Sokol as sidechain) and then deploy and use the bridge (ERC20 <> ERC20) to let a user transfers his assets from one network to another.

-----------------------------------------------------

# Requirements

In order to start, you will need the following programs installed on your machine:
- Github
- NodeJS
- NPM
- [Metamask](https://metamask.io/) (Browser extension)
- [Truffle](https://truffleframework.com/)
```
$ npm install -g truffle
```


<br />
# Step 1: Deploy an ERC20 token called BRidge Token `BRT` on the mainchain (Rinkeby network)

1. Let's first create a project folder for our ERC20 `BRT` and initialize a Truffle project.
```
$ mkdir bridge_token
$ cd bridge_token
$ truffle init
```

<br />

2. Then we will install the [Open-Zeppelin Solidity library](https://github.com/OpenZeppelin/openzeppelin-solidity) which contains a lot of high-quality, fully tested and audited reusable smart contracts
```
$ npm init -y
$ npm install zeppelin-solidity --save-exact
```

<br />

3. Create a contract file `./contacts/BridgeToken.sol` containing
```
// BridgeToken.sol
pragma solidity ^0.4.24;

import "zeppelin-solidity/contracts/token/ERC20/MintableToken.sol";

contract BridgeToken is MintableToken {
    string public constant name = "Bridge Token";
    string public constant symbol = "BRT";
    uint8 public constant decimals = 18;
}
```

That's it and a big Thanks to the Zeppelin team for all the work they done for Ethereum. Basically our smart contract inherits from [MintableToken](https://openzeppelin.org/api/docs/token_ERC20_MintableToken.html) which offers all the ERC-20 standard functionalities as well as functions to mint tokens. We only have to specify our token name "Bridge Token", its symbol "BRT" and the number of decimals (divisibility).

To make sure your smart contract compiles, you can run `truffle compile`.

<br />

4. Deploy the smart contract on the RinkeBy network

*Note:* Make sure the account used to deployed the contract is funded with RinkeBy ethers (see [faucet](https://faucet.rinkeby.io/)).

Once our smart contract compile, we need to deploy it. To do so, we need first to complete the migration script, create a file `./migrations/2_deploy_contract.js`

```
// 2_deploy_contract.js
const BridgeToken = artifacts.require("./BridgeToken.sol");

module.exports = function(deployer, network, accounts) {
    // Deploy the smart contract
    deployer.deploy(BridgeToken).then(function(instance) {
        // Mint 100 tokens
        return instance.mint(accounts[0], 100000000000000000000, {from: accounts[0]});
    }); 
};
```
The migration script deploys the contract and additionally mint and distribute 100 BRT tokens to the deployer account.


Next step consists in configuring a connection to the RinkeBy network in order to deploy a smart contract.

Install the following dependencies ([dotenv](https://www.npmjs.com/package/dotenv) to manage environment variables and [truffle-hdwallet-provider](https://github.com/trufflesuite/truffle-hdwallet-provider) to sign transactions from an account derived from a mnemonic)
```
$ npm install --save dotenv truffle-hdwallet-provider
```

Create the file `./.env` to store some private information we do not want to share anywhere (**gitignore this file**)
- [Infura](https://infura.io/) is a public gateway to Ethereum. If we don't already have an account, I recommend you to create one and past your API key in this file.
- A mnemonic a 12 words phrase that symbolize a private key. You can find it in Metamask (_Settings / Reveal seed words_)

```
// .env
INFURA_API_KEY=ABCABC123123
MNEMONIC=twelve words you can find in metamask/settings/reveal seed words 
```

Finally let's configure the connection to the RinkeBy network. Edit the file `./truffle.js`
```
// truffle.js
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
    },
  }
};
```

To deploy the smart contract on the RinkeBy network, run the command:
```
$ truffle migrate --network rinkeby
Using network 'rinkeby'.

Running migration: 1_initial_migration.js
  Replacing Migrations...
  ... 0x691457f76fb8003f0e8c483891cf49b642859a431013b43868504b948b7ae829
  Migrations: 0x5e7d11f06ecc7b1c86a27d09bac3b31ea348173f
Saving successful migration to network...
  ... 0xcb01107f840a1db14430768eff3e7cdd419d6ceb7d0b7fec23aa020dea299f46
Saving artifacts...
Running migration: 2_deploy.js
  Replacing BridgeToken...
  ... 0x7c6499672c442590538df72d36e78c45cc885d97cb9bfd0420be190addbe5fc7
  BridgeToken: 0x38954e61cc43893c6d4aa1b735c902fd78f85a2f
Saving successful migration to network...
  ... 0x39e13195b76ca4d5bb44dd16a7f4571295be6e334fd3ef7e4b8c38d9d8cbb3a3
```

As a result, we can identify our Smart Contract has been deployed at the address `0x38954e61cc43893c6d4aa1b735c902fd78f85a2f` (see [block explorer](https://rinkeby.etherscan.io/address/0x38954e61cc43893c6d4aa1b735c902fd78f85a2f))


<br />
# Step 2: Configure and Deploy the bridge contracts 

In this second steps, we will deploy the necessary contract to enable a ERC20 to ERC20 bridge.

![](https://api.beta.kauri.io:443/ipfs/QmaVm2rdtf78fHsG7pRh2GYGXeRdos6tNkWrWtU2AZAT8w)

1. Clone the POA Bridge repo
```
cd ../
$ git clone git@github.com:poanetwork/token-bridge.git bridge-contracts
cd ./bridge-contracts/
```

<br />
2. Create a configuration file in `./bridge-contracts/deploy/.env`

*Note 1*: the following properties to change
- `<PRIVATE_KEY>` Account responsible for deploying the contracts
- `<ACCOUNT_ADMIN>` Account responsible for deploying the contracts
- `<ERC20_TOKEN_ADDRESS>` Address of the ERC20 token deployed above.

*Note 2*: For the reason of the tutorial, we decided to simplify the configuration as much as possible (one account administrating and validating)

*Note 3:* Make sure the account `ACCOUNT_ADMIN` is funded with RinkeBy ethers and POA sokol ethers.

```
# The type of bridge. Defines set of contracts to be deployed.
BRIDGE_MODE=ERC_TO_ERC

# The private key hex value of the account responsible for contracts
# deployments and initial configuration. The account's balance must contain
# funds from both networks.
DEPLOYMENT_ACCOUNT_PRIVATE_KEY=<PRIVATE_KEY>
# The "gas" parameter set in every deployment/configuration transaction.
DEPLOYMENT_GAS_LIMIT=4000000
# The "gasPrice" parameter set in every deployment/configuration transaction on
# Home network (in Wei).
HOME_DEPLOYMENT_GAS_PRICE=10000000000
# The "gasPrice" parameter set in every deployment/configuration transaction on
# Foreign network (in Wei).
FOREIGN_DEPLOYMENT_GAS_PRICE=10000000000
# The timeout limit to wait for receipt of the deployment/configuration
# transaction.
GET_RECEIPT_INTERVAL_IN_MILLISECONDS=3000

# The name of the ERC677 token to be deployed on the Home network.
BRIDGEABLE_TOKEN_NAME="Bridge Token"
# The symbol name of the ERC677 token to be deployed on the Home network.
BRIDGEABLE_TOKEN_SYMBOL="BRT"
# The number of supportable decimal digits after the "point" in the ERC677 token
# to be deployed on the Home network.
BRIDGEABLE_TOKEN_DECIMALS=18

# The RPC channel to a Home node able to handle deployment/configuration
# transactions.
HOME_RPC_URL=https://sokol.poa.network
# The address of an administrator on the Home network who can change bridge
# parameters and a validator's contract. For extra security we recommended using
# a multi-sig wallet contract address here.
HOME_OWNER_MULTISIG=<ACCOUNT_ADMIN>
# The address from which a validator's contract can be upgraded on Home.
HOME_UPGRADEABLE_ADMIN_VALIDATORS=<ACCOUNT_ADMIN>
# The address from which the bridge's contract can be upgraded on Home.
HOME_UPGRADEABLE_ADMIN_BRIDGE=<ACCOUNT_ADMIN>
# The daily transaction limit in Wei. As soon as this limit is exceeded, any
# transaction which requests to relay assets will fail.
HOME_DAILY_LIMIT=30000000000000000000000000
# The maximum limit for one transaction in Wei. If a single transaction tries to
# relay funds exceeding this limit it will fail.
HOME_MAX_AMOUNT_PER_TX=1500000000000000000000000
# The minimum limit for one transaction in Wei. If a transaction tries to relay
# funds below this limit it will fail. This is required to prevent dryout
# validator accounts.
HOME_MIN_AMOUNT_PER_TX=500000000000000000
# The finalization threshold. The number of blocks issued after the block with
# the corresponding deposit transaction to guarantee the transaction will not be
# rolled back.
HOME_REQUIRED_BLOCK_CONFIRMATIONS=1
# The default gas price (in Wei) used to send Home Network signature
# transactions for deposit or withdrawal confirmations. This price is used if
# the Gas price oracle is unreachable.
HOME_GAS_PRICE=1000000000

# The RPC channel to a Foreign node able to handle deployment/configuration
# transactions.
FOREIGN_RPC_URL=https://rinkeby.infura.io
# The address of an administrator on the Foreign network who can change bridge
# parameters and the validator's contract. For extra security we recommended
# using a multi-sig wallet contract address here.
FOREIGN_OWNER_MULTISIG=<ACCOUNT_ADMIN>
# The address from which a validator's contract can be upgraded on Foreign.
FOREIGN_UPGRADEABLE_ADMIN_VALIDATORS=<ACCOUNT_ADMIN>
# The address from which the bridge's contract can be upgraded on Foreign.
FOREIGN_UPGRADEABLE_ADMIN_BRIDGE=<ACCOUNT_ADMIN>
# These three parameters are not used in this mode, but the deployment script
# requires it to be set to some value.
FOREIGN_DAILY_LIMIT=0
FOREIGN_MAX_AMOUNT_PER_TX=0
FOREIGN_MIN_AMOUNT_PER_TX=0
# The finalization threshold. The number of blocks issued after the block with
# the corresponding deposit transaction to guarantee the transaction will not be
# rolled back.
FOREIGN_REQUIRED_BLOCK_CONFIRMATIONS=8
# The default gas price (in Wei) used to send Foreign network transactions
# finalizing asset deposits. This price is used if the Gas price oracle is
# unreachable.
FOREIGN_GAS_PRICE=10000000000
# The address of the existing ERC20 compatible token in the Foreign network to
# be exchanged to the ERC20/ERC677 token deployed on Home.
ERC20_TOKEN_ADDRESS=<ERC20_TOKEN_ADDRESS>

# The minimum number of validators required to send their signatures confirming
# the relay of assets. The same number of validators is expected on both sides
# of the bridge.
REQUIRED_NUMBER_OF_VALIDATORS=1
# The set of validators' addresses. It is assumed that signatures from these
# addresses are collected on the Home side. The same addresses will be used on
# the Foreign network to confirm that the finalized agreement was transferred
# correctly to the Foreign network.
VALIDATORS=<ACCOUNT_ADMIN>
```

<br />
3. Install the dependencies
```
$ npm install
```

<br />
4. Deploy the Bridge configuration
```
$ ./deploys.sh
(...)
Deployment has been completed.


[   Home  ] HomeBridge: 0x2fED8cf9C20f7D409670751d4bbfEf28400362aA at block 6071895
[   Home  ] ERC677 Bridgeable Token: 0x9ef00ee05bd04b4cb84906D6b436a036Fd20b27a
[ Foreign ] ForeignBridge: 0x555c5a15C89d56FF357cE003eDFBE185E1483d69 at block 3500840
[ Foreign ] ERC20 Token: 0x38954e61cc43893c6d4aa1b735c902fd78f85a2f
Contracts Deployment have been saved to `bridgeDeploymentResults.json`
{
    "homeBridge": {
        "address": "0x2fED8cf9C20f7D409670751d4bbfEf28400362aA",
        "deployedBlockNumber": 6071895,
        "erc677": {
            "address": "0x9ef00ee05bd04b4cb84906D6b436a036Fd20b27a"
        }
    },
    "foreignBridge": {
        "address": "0x555c5a15C89d56FF357cE003eDFBE185E1483d69",
        "deployedBlockNumber": 3500840
    }
}
```

**Save the JSON information above.**

[github](https://github.com/poanetwork/poa-bridge-contracts)

<br />
# Step 3: Configure and deploy the Bridge Oracle

1. Checkout the Bridge Oracle code
```
$ cd ../
$ git clone git@github.com:poanetwork/token-bridge.git bridge-oracle
$ cd bridge-oracle
```

<br />
2. Create a configuration file in `./.env`

*Note 1* : Open the saved JSON file `bridgeDeploymentResults.json` to get the home and foreign bridge contract address and deployment block numbers.

```
{
    "homeBridge": {
        "address": "0x2fED8cf9C20f7D409670751d4bbfEf28400362aA",
        "deployedBlockNumber": 6071895,
        "erc677": {
            "address": "0x9ef00ee05bd04b4cb84906D6b436a036Fd20b27a"
        }
    },
    "foreignBridge": {
        "address": "0x555c5a15C89d56FF357cE003eDFBE185E1483d69",
        "deployedBlockNumber": 3500840
    }
}
```

*Note 2* : the following properties to change
- `<PRIVATE_KEY>` Account responsible for deploying the contracts and validating
- `<ACCOUNT_ADMIN>` Account responsible for deploying the contracts
- `<ERC20_TOKEN_ADDRESS>` Address of the ERC20 token deployed above.

*Note 3* : For the reason of the tutorial, we decided to simplify the configuration as much as possible (one account administrating and validating)

*Note 4* : Make sure the account `ACCOUNT_ADMIN` is funded with RinkeBy ethers and POA sokol ethers.

```
BRIDGE_MODE=ERC_TO_ERC
HOME_POLLING_INTERVAL=5000
FOREIGN_POLLING_INTERVAL=1000
ALLOW_HTTP=yes
HOME_RPC_URL=https://sokol.poa.network
FOREIGN_RPC_URL=https://rinkeby.infura.io
HOME_BRIDGE_ADDRESS=<bridgeDeploymentResults.json / homeBridge / address>
FOREIGN_BRIDGE_ADDRESS=<bridgeDeploymentResults.json / foreignBridge / address>
ERC20_TOKEN_ADDRESS=<ERC20_TOKEN_ADDRESS>

VALIDATOR_ADDRESS=<ACCOUNT_ADMIN>
VALIDATOR_ADDRESS_PRIVATE_KEY=<PRIVATE_KEY>

HOME_GAS_PRICE_ORACLE_URL=https://gasprice.poa.network/
HOME_GAS_PRICE_SPEED_TYPE=standard
HOME_GAS_PRICE_FALLBACK=1000000000
HOME_GAS_PRICE_UPDATE_INTERVAL=600000

FOREIGN_GAS_PRICE_ORACLE_URL=https://gasprice.poa.network/
FOREIGN_GAS_PRICE_SPEED_TYPE=standard
FOREIGN_GAS_PRICE_FALLBACK=1000000000
FOREIGN_GAS_PRICE_UPDATE_INTERVAL=600000

QUEUE_URL=amqp://127.0.0.1
REDIS_URL=redis://127.0.0.1:6379
REDIS_LOCK_TTL=1000

HOME_START_BLOCK=<bridgeDeploymentResults.json / homeBridge / deployedBlockNumber>
FOREIGN_START_BLOCK=<bridgeDeploymentResults.json / foreignBridge / deployedBlockNumber>

LOG_LEVEL=info
MAX_PROCESSING_TIME=20000

#testing accs
USER_ADDRESS=
USER_ADDRESS_PRIVATE_KEY=
HOME_MIN_AMOUNT_PER_TX=0.001
FOREIGN_MIN_AMOUNT_PER_TX=0.001
HOME_TEST_TX_GAS_PRICE=1000000000
FOREIGN_TEST_TX_GAS_PRICE=1000000000
```

<br />
3. Build the Bridge Oracle (using [Docker](https://www.docker.com/) and Docker-compose)
```
$ docker-compose build
```

<br />
4. Run the Bridge Oracle composed of 6 processes
```
$ docker-compose up 
$ docker-compose run bridge npm run watcher:signature-request
$ docker-compose run bridge npm run watcher:collected-signatures
$ docker-compose run bridge npm run watcher:affirmation-request
$ docker-compose run bridge npm run sender:home
$ docker-compose run bridge npm run sender:foreign
```

![](https://i.imgur.com/D6TFeWr.gif)

[github](https://github.com/poanetwork/poa-bridge-contracts)

<br />
# Step 4: Configure and Deploy the bridge UI

1. Checkout the Bridge UI code
```
$ cd ../
$ git clone https://github.com/poanetwork/bridge-ui.git brige-ui
$ cd bridge-ui
```

<br />
2. Update the Git submodules
```
$ git submodule update --init --recursive --remote
```

<br />
3. Install dependencies
```
$ npm install
```

<br />
4. Create a configuration file in `./.env`

*Note 1*: Open the saved JSON file `bridgeDeploymentResults.json` to get the home and foreign bridge contract address and deployment block numbers.

```
{
    "homeBridge": {
        "address": "0x2fED8cf9C20f7D409670751d4bbfEf28400362aA",
        "deployedBlockNumber": 6071895,
        "erc677": {
            "address": "0x9ef00ee05bd04b4cb84906D6b436a036Fd20b27a"
        }
    },
    "foreignBridge": {
        "address": "0x555c5a15C89d56FF357cE003eDFBE185E1483d69",
        "deployedBlockNumber": 3500840
    }
}
```

```
# HomeBridge address in bridgeDeploymentResults.json
REACT_APP_HOME_BRIDGE_ADDRESS=<bridgeDeploymentResults.json / homeBridge / address>
# ForeignBridge address in bridgeDeploymentResults.json
REACT_APP_FOREIGN_BRIDGE_ADDRESS=<bridgeDeploymentResults.json / foreignBridge / address>
# https public RPC node for Foreign network
REACT_APP_FOREIGN_HTTP_PARITY_URL=https://rinkeby.infura.io/mew
# public RPC node for Home network 
REACT_APP_HOME_HTTP_PARITY_URL=https://sokol.poa.network 
```

<br />
5. Run Bridge UI
```
$ npm start
```

<br />
6. Open your Internet Browser, unlock Metamask on the Rinkeby network with the account used to deploy the BRT token and go to Bridge-UI on http://localhost:3000/

![](https://api.beta.kauri.io:443/ipfs/QmPNXRHicnw5ZVFiyiZVN7rDEwR8rtVtWsMYq1bLEMovor)

If you are on the RinkeBy network, you should see that you own 100 BRT token on the mainchain (RinkeBy) and 0 on the sidechain (POA Sokol)

![](https://api.beta.kauri.io:443/ipfs/QmPbXEgc9q2xZ31hJjLJS4mvbFUy5fLj8SoFP89v25AMeq)

You can now transfer BRT token between the mainchain and the sidechain:

![](https://i.imgur.com/cXPDt8y.gif)

[github](https://github.com/poanetwork/bridge-ui)

<br />
# Step 5. Programmatically transfer tokens

**TODO**


<br />
<br />
-----------------------------------------------------

# References:

- [Introducing the ERC20 to ERC20 TokenBridge](https://medium.com/poa-network/introducing-the-erc20-to-erc20-tokenbridge-ce266cc1a2d0) (November 2018)  
- [Introducing POA Bridge and POA20](https://medium.com/poa-network/introducing-poa-bridge-and-poa20-55d8b78058ac) (April 2018) 


