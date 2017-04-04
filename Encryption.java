import java.nio.ByteBuffer;
import java.nio.charset.Charset;
import java.util.Arrays;


public class Encryption {

	static {
		System.loadLibrary("Encryption");
	}

	public native byte[] encryptArray(byte[] v, byte[] key);
	public native byte[] decryptArray(byte[] v, byte[] key);

	public byte[] encrypt(String s, String key) {
		return encrypt(s.getBytes(), key);
	}

	public byte[] encrypt(byte[] s, String key) {
		int len = s.length + 4;
		int pad = 0;
		// find the proper padding needed
		while((len+pad) % 16 != 0) {
			pad++;
		}

		byte[] v = ByteBuffer.allocate(len+pad).putInt(pad).put(s).array();
		byte[] k = ByteBuffer.allocate(32).put(key.getBytes()).array();
		
		return encryptArray(v, k);
	}


	public byte[] decrypt(byte[] v, String k) {

		byte[] key = ByteBuffer.allocate(32).put(k.getBytes()).array();
		byte[] decrypted = decryptArray(v, key);

		int pad = ByteBuffer.wrap(Arrays.copyOfRange(decrypted, 0, 4)).getInt();

		return Arrays.copyOfRange(decrypted, 4, decrypted.length-pad);
	} 
}