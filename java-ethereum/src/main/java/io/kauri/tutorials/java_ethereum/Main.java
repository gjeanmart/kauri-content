package io.kauri.tutorials.java_ethereum;

import java.io.IOException;

import org.web3j.protocol.Web3j;
import org.web3j.protocol.http.HttpService;

public class Main {

	private static final String INFURA_API_KEY = "CHANGE_ME";
	
	public static void main(String[] args) throws IOException {
		System.out.println("Connecting to Ethereum ...");
		Web3j web3 = Web3j.build(new HttpService("https://mainnet.infura.io/v3/" + INFURA_API_KEY));
		System.out.println("Successfuly connected to Ethereum");
		
		System.out.println("Node version: " + web3.web3ClientVersion().send().getWeb3ClientVersion());
		System.out.println("Number of peers: " + web3.netPeerCount().send().getQuantity().toString());
		System.out.println("Latest block: " + web3.ethBlockNumber().send().getBlockNumber().toString());
		System.out.println("Gas price: " + web3.ethGasPrice().send().getGasPrice().toString());
		
		
	}

}
