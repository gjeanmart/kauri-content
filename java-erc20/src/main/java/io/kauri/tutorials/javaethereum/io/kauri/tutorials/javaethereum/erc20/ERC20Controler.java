package io.kauri.tutorials.javaethereum.io.kauri.tutorials.javaethereum.erc20;

import java.math.BigInteger;
import java.util.List;

import org.web3j.contracts.eip20.generated.ERC20;
import org.web3j.contracts.eip20.generated.ERC20.TransferEventResponse;
import org.web3j.contracts.token.ERC20Interface;
import org.web3j.crypto.Credentials;
import org.web3j.protocol.Web3j;
import org.web3j.protocol.core.DefaultBlockParameterName;
import org.web3j.protocol.core.methods.request.EthFilter;
import org.web3j.protocol.core.methods.response.TransactionReceipt;
import org.web3j.protocol.http.HttpService;
import org.web3j.tx.gas.DefaultGasProvider;

public class ERC20Controler {

	public static void main(String[] args) throws Exception {
		
		// Connect Web3j to the Blockchain
		String rpcEndpoint = "http://localhost:8545";
		Web3j web3j = Web3j.build(new HttpService(rpcEndpoint));
		
		// Prepare a wallet
		String pk = "0x5bbbef76458bf30511c9ee6ed56783644eb339258d02656755c68098c4809130"; // Dummy ganache private key
		Credentials credentials = Credentials.create(pk);

		// Load the contract
		String contractAddress = "0xe4F275cE131eF87Cb8bF425E02D9215055e9F875";
		ERC20 javaToken = ERC20.load(contractAddress, web3j, credentials,  new DefaultGasProvider());
		
		// Get information
		String symbol = javaToken.symbol().send();
		String name = javaToken.name().send();
		BigInteger decimal = javaToken.decimals().send();
		System.out.println("symbol: " + symbol);
		System.out.println("name: " + name);
		System.out.println("decimal: " + decimal.intValueExact());
		
		
		BigInteger balance1 = javaToken.balanceOf("0x1583c05d6304b6651a7d9d723a5c32830f53a12f").send();
		System.out.println("balance (0x1583c05d6304b6651a7d9d723a5c32830f53a12f)="+balance1.toString());

		BigInteger balance2 = javaToken.balanceOf("0x0db6b797e64666d4b36b13e5dc6fcd4661893ac8").send();
		System.out.println("balance (0x0db6b797e64666d4b36b13e5dc6fcd4661893ac8)="+balance2.toString());
		
		
		// Transfer tokens
		TransactionReceipt receipt = javaToken.transfer("0x0db6b797e64666d4b36b13e5dc6fcd4661893ac8", new BigInteger("1")).send();
		System.out.println("Transaction hash: "+receipt.getTransactionHash());
		
		
		balance1 = javaToken.balanceOf("0x1583c05d6304b6651a7d9d723a5c32830f53a12f").send();
		System.out.println("balance (0x1583c05d6304b6651a7d9d723a5c32830f53a12f)="+balance1.toString());

		balance2 = javaToken.balanceOf("0x0db6b797e64666d4b36b13e5dc6fcd4661893ac8").send();
		System.out.println("balance (0x0db6b797e64666d4b36b13e5dc6fcd4661893ac8)="+balance2.toString());
		
		// Get events for a given transaction
		List<TransferEventResponse> events = javaToken.getTransferEvents(receipt);
		events.forEach(event 
				-> System.out.println("from: " + event._from + ", to: " + event._to + ", value: " + event._value));
		
		
		// Subscribe to event on the fly
		javaToken.transferEventFlowable(DefaultBlockParameterName.EARLIEST, DefaultBlockParameterName.LATEST)
			.subscribe(event 
					-> System.out.println("from: " + event._from + ", to: " + event._to + ", value: " + event._value));

		javaToken.transfer("0x0db6b797e64666d4b36b13e5dc6fcd4661893ac8", new BigInteger("1")).send();
		javaToken.transfer("0x0db6b797e64666d4b36b13e5dc6fcd4661893ac8", new BigInteger("1")).send();
		javaToken.transfer("0x0db6b797e64666d4b36b13e5dc6fcd4661893ac8", new BigInteger("1")).send();
		javaToken.transfer("0x0db6b797e64666d4b36b13e5dc6fcd4661893ac8", new BigInteger("1")).send();
		
	}

}
