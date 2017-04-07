import javax.crypto.*;
import java.security.*;
import java.util.Base64.*;
import java.security.spec.*;
import java.util.*;
import java.io.*;


public class DiffieH {

	public String handShake(ObjectInputStream inputStream, ObjectOutputStream outputStream) throws Exception {

		// Generate my Keys
		KeyPairGenerator keyGen = KeyPairGenerator.getInstance("DH");
		keyGen.initialize(2048);
		KeyPair keyPair = keyGen.generateKeyPair();
		
		// Get public and private
		PrivateKey privateKey = keyPair.getPrivate();
		PublicKey publicKey = keyPair.getPublic();
		byte[] publicKeyBytes = publicKey.getEncoded();
		outputStream.writeObject(publicKeyBytes); // send to server

		publicKeyBytes = (byte[]) inputStream.readObject(); //  recieve other public key
		// Convert Bytes
		X509EncodedKeySpec x509KeySpec = new X509EncodedKeySpec(publicKeyBytes);
		KeyFactory keyFac = KeyFactory.getInstance("DH");
		publicKey = keyFac.generatePublic(x509KeySpec);

		// generate secret key with private and public key of other party
		KeyAgreement ka = KeyAgreement.getInstance("DH");
		ka.init(privateKey);
		ka.doPhase(publicKey, true);

		// type of key
		String algorithm = "DES";
		SecretKey secretKey = ka.generateSecret(algorithm);
		String key = Base64.getEncoder().encodeToString(secretKey.getEncoded());
		return key;
	}
}