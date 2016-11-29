// الصف الرئيسي في هذه الباكيج هو الصف Client

package client;



//import javax.swing.*;
import java.security.SecureRandom;
import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;

class AES {
	byte[] skey = new byte[1000];
	String skeyString;
	static byte[] raw;
	
	// هذا المفتاح السري عند العميل و الخادم
	String privateKey = "open sesemi";

	byte[] doEncryption(String somePlainText) {
		byte[] ibyte = somePlainText.getBytes();
		byte[] ebyte = null;
		try {
			ebyte = encrypt(raw, ibyte);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return ebyte;
	}

	String doDecryption(byte[] someCipherText) {
		byte[] dbyte = null;
		try {
			dbyte = decrypt(raw, someCipherText);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return new String(dbyte);
	}

	public AES() {
		generateSymmetricKey();	}

	void generateSymmetricKey() {
		try {

			byte[] knumb = privateKey.getBytes();
			skey = getRawKey(knumb);
			skeyString = new String(skey);
			// System.out.println("AES Symmetric key = " + skeyString);
		} catch (Exception e) {
			System.out.println(e);
		}
	}

	private static byte[] getRawKey(byte[] seed) throws Exception {
		KeyGenerator kgen = KeyGenerator.getInstance("AES");
		SecureRandom sr = SecureRandom.getInstance("SHA1PRNG");
		sr.setSeed(seed);
		kgen.init(128, sr); // 192 and 256 bits may not be available
		SecretKey skey = kgen.generateKey();
		raw = skey.getEncoded();
		return raw;
	}

	private static byte[] encrypt(byte[] raw, byte[] clear) throws Exception {
		SecretKeySpec skeySpec = new SecretKeySpec(raw, "AES");
		Cipher cipher = Cipher.getInstance("AES");
		cipher.init(Cipher.ENCRYPT_MODE, skeySpec);
		byte[] encrypted = cipher.doFinal(clear);
		return encrypted;
	}

	private static byte[] decrypt(byte[] raw, byte[] encrypted) throws Exception {
		SecretKeySpec skeySpec = new SecretKeySpec(raw, "AES");
		Cipher cipher = Cipher.getInstance("AES");
		cipher.init(Cipher.DECRYPT_MODE, skeySpec);
		byte[] decrypted = cipher.doFinal(encrypted);
		return decrypted;
	}
//
//	public static void main(String args[]) {
//		AES aes1 = new AES();
//		AES aes2 = new AES();
//		System.out.println(aes1.skeyString + " ******** " + aes2.skeyString);
//		String msg = "mohammed";	
//		byte[] b = aes1.doEncryption(msg);
//		System.out.println(aes1.doDecryption(b));
//		System.out.println(aes2.doDecryption(b));
//		
//	}
}