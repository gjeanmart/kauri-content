package io.kauri.tutorials.java_ipfs;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import io.ipfs.api.IPFS;
import io.ipfs.api.IPFS.PinType;
import io.ipfs.api.KeyInfo;
import io.ipfs.api.MerkleNode;
import io.ipfs.api.NamedStreamable;
import io.ipfs.multihash.Multihash;

public class Main {


	public static void main(String[] args) {

		try {
			
			// Connect to a local node and Infura public node
			System.out.println("=======================");
			System.out.println("Connecting...");
			IPFS localIPFS = new IPFS("localhost", 5001);
			System.out.println("Connected to localhost. Node version: " + localIPFS.version());

			IPFS infuraIPFS = new IPFS("/dnsaddr/ipfs.infura.io/tcp/5001/https");
			System.out.println("Connected to Infura. Node version: " + infuraIPFS.version());
			

			// Write file to the local node
			System.out.println("=======================");
			System.out.println("Add File");
			NamedStreamable.FileWrapper file = new NamedStreamable.FileWrapper(new File("/home/gjeanmart/Documents/hello.txt"));
			MerkleNode addFileResponse = localIPFS.add(file).get(0);
			System.out.println("Hash (base 58): " + addFileResponse.name.get() + " - " + addFileResponse.hash.toBase58());

			
			System.out.println("=======================");
			System.out.println("Add InputStream");
			NamedStreamable.InputStreamWrapper is = new NamedStreamable.InputStreamWrapper(new FileInputStream("/home/gjeanmart/Documents/hello.txt"));
			MerkleNode addISResponse = localIPFS.add(is).get(0);
			System.out.println("Hash (base 58): " + addISResponse.hash.toBase58());

			
			System.out.println("=======================");
			System.out.println("Add ByteArray");
			NamedStreamable.ByteArrayWrapper bytearray = new NamedStreamable.ByteArrayWrapper("hello".getBytes());
			MerkleNode addBAResponse = localIPFS.add(bytearray).get(0);
			System.out.println("Hash (base 58): " + addBAResponse.hash.toBase58());

			
			System.out.println("=======================");
			System.out.println("Add Directory");
			NamedStreamable.FileWrapper file1 = new NamedStreamable.FileWrapper(new File("/home/gjeanmart/Documents/hello.txt"));
			NamedStreamable.FileWrapper file2 = new NamedStreamable.FileWrapper(new File("/home/gjeanmart/Documents/hello2.txt"));
			NamedStreamable.DirWrapper directory = new NamedStreamable.DirWrapper("folder", Arrays.asList(file1, file2));
			List<MerkleNode> addDirResponse = localIPFS.add(directory);
			addDirResponse.forEach(merkleNode -> System.out.println("Hash (base 58): " + merkleNode.name.get() + " - " + merkleNode.hash.toBase58()));

			System.out.println("=======================");
			System.out.println("Multihash");
			Multihash multihash = Multihash.fromBase58("QmT78zSuBmuS4z925WZfrqQ1qHaJ56DQaTfyMUF7F8ff5o");
			System.out.println("base 58: " + multihash.toBase58());
			System.out.println("base 16: " + multihash.toHex());
			
			
			// Access file
			System.out.println("=======================");
			System.out.println("Read hello");
			Multihash filePointer = Multihash.fromBase58("QmWfVY9y3xjsixTgbd9AorQxH7VtMpzfx2HaWtsoUYecaX");
			byte[] baContent = localIPFS.cat(filePointer);
			System.out.println("Content of QmWfVY9y3xjsixTgbd9AorQxH7VtMpzfx2HaWtsoUYecaX: " + new String(baContent));
			
			System.out.println("=======================");
			System.out.println("Read hello world2");
			Multihash filePointer2 = Multihash.fromBase58("QmNoQbeckeCN7FWt6mVcxTf7CAyyHUMsqtCWtMLFdsUayN");
			byte[] baContent2 = localIPFS.cat(filePointer2, "/hello2.txt");
			System.out.println("Content of QmNoQbeckeCN7FWt6mVcxTf7CAyyHUMsqtCWtMLFdsUayN/hello2.txt: " + new String(baContent2));
			
	
//			System.out.println("=======================");
//			System.out.println("Read file");
//			Multihash filePointer2 = Multihash.fromBase58("QmT78zSuBmuS4z925WZfrqQ1qHaJ56DQaTfyMUF7F8ff5o");
//			InputStream isContent = infuraIPFS.catStream(filePointer2);
//			Files.copy(isContent, Paths.get("/home/gjeanmart/Documents/helloResult.txt"));
//			System.out.println("Content wrote into /home/gjeanmart/Documents/helloResult.txt\"");

			
			// pin
			localIPFS.pin.add(filePointer2);
			Map<Multihash, Object> list =localIPFS.pin.ls(PinType.all);
			list.forEach((hash, type) -> System.out.println("Multihash: " + hash + " - type: " + type));
			
			
			// IPNS
			Multihash hash = Multihash.fromBase58("QmWfVY9y3xjsixTgbd9AorQxH7VtMpzfx2HaWtsoUYecaX");
			String keyName ="myarticle";
			Optional<String> keyType = Optional.of("rsa");
			Optional<String> keySize = Optional.of("2048");
			
			localIPFS.key.rm(keyName);
			
			KeyInfo keyInfo = localIPFS.key.gen(keyName, keyType, keySize);
			System.out.println("keyInfo name: " + keyInfo.name);
			System.out.println("keyInfo.hash: " + keyInfo.id);
			
			List<KeyInfo> listKeys = localIPFS.key.list();
			listKeys.forEach(keyinfo -> System.out.println("keyInfo: name=" + keyinfo.name + ", hash=" + keyinfo.id));
			
			
			Map publishResponse = localIPFS.name.publish(hash, Optional.of(keyName));
			System.out.println("publish(hash="+hash+", key="+keyName+"): \n" + publishResponse);
			
			String resolveResponse = localIPFS.name.resolve(keyInfo.id);
			System.out.println("resolve(key="+keyInfo.id+"): " + resolveResponse);
			
			byte[] content = localIPFS.cat(Multihash.fromBase58(resolveResponse.substring(6)));
			System.out.println("Content: " + new String(content));
			
			
			// Other operations
			System.out.println("=======================");
			System.out.println("Node version: " + localIPFS.version());
			System.out.println("Node peers: " + localIPFS.refs.local());
			
			//localIPFS.dag.put(inputFormat, object)
			
		} catch (IOException ex) {
			throw new RuntimeException("Error whilst communicating with the IPFS node", ex);
		}
	}
	
}
