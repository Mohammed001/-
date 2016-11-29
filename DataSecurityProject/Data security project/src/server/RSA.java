package server;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.PrivateKey;
import java.security.PublicKey;

import javax.crypto.Cipher;

/**
 * @author JavaDigest
 * 
 */
public class RSA {
	public static KeyPair key;
	/**
	 * String to hold name of the encryption algorithm.
	 */
	public static final String ALGORITHM = "RSA";

	/**
	 * String to hold the name of the private key file.
	 */
	public static final String SERVER_PRIVATE_KEY_FILE = "src/server/keys/serverPrivate.key";

	/**
	 * String to hold name of the public key file.
	 */
	public static final String SERVER_PUBLIC_KEY_FILE = "src/server/keys/serverPublic.key";

	public static final String CLIENT_PUBLIC_KEY_FILE = "src/server/keys/clientPublic.key";

	public static void otherSidePublicKey(PublicKey ospk) {

		// File publicKey = new File(CLIENT_PUBLIC_KEY_FILE);
		// if (publicKey.exists()) {
		// return;
		// }

		File publicKeyFile = new File(CLIENT_PUBLIC_KEY_FILE);
		if (publicKeyFile.getParentFile() != null) {
			publicKeyFile.getParentFile().mkdirs();
		}

		try {

			publicKeyFile.createNewFile();

			// Saving the Public key in a file
			ObjectOutputStream publicKeyOS = new ObjectOutputStream(new FileOutputStream(publicKeyFile));
			publicKeyOS.writeObject(ospk);
			publicKeyOS.close();

		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * Generate key which contains a pair of private and public key using 1024
	 * bytes. Store the set of keys in Prvate.key and Public.key files.
	 * 
	 * @throws NoSuchAlgorithmException
	 * @throws IOException
	 * @throws FileNotFoundException
	 */
	public static void doGenerateKey() {
		try {
			final KeyPairGenerator keyGen = KeyPairGenerator.getInstance(ALGORITHM);
			keyGen.initialize(1024);
			key = keyGen.generateKeyPair();

			File privateKeyFile = new File(SERVER_PRIVATE_KEY_FILE);
			File publicKeyFile = new File(SERVER_PUBLIC_KEY_FILE);

			// Create files to store public and private key
			if (privateKeyFile.getParentFile() != null) {
				privateKeyFile.getParentFile().mkdirs();
			}
			privateKeyFile.createNewFile();

			if (publicKeyFile.getParentFile() != null) {
				publicKeyFile.getParentFile().mkdirs();
			}
			publicKeyFile.createNewFile();

			// Saving the Public key in a file
			ObjectOutputStream publicKeyOS = new ObjectOutputStream(new FileOutputStream(publicKeyFile));
			publicKeyOS.writeObject(key.getPublic());
			publicKeyOS.close();

			// Saving the Private key in a file
			ObjectOutputStream privateKeyOS = new ObjectOutputStream(new FileOutputStream(privateKeyFile));
			privateKeyOS.writeObject(key.getPrivate());
			privateKeyOS.close();
		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	/**
	 * The method checks if the pair of public and private key has been
	 * generated.
	 * 
	 * @return flag indicating if the pair of keys were generated.
	 */
	public static boolean areKeysPresent() {

		File privateKey = new File(SERVER_PRIVATE_KEY_FILE);
		File publicKey = new File(SERVER_PUBLIC_KEY_FILE);

		if (privateKey.exists() && publicKey.exists()) {
			return true;
		}
		return false;
	}

	/**
	 * Encrypt the plain text using public key.
	 * 
	 * @param text
	 *            : original plain text
	 * @param key
	 *            :The public key
	 * @return Encrypted text
	 * @throws java.lang.Exception
	 */

	public static byte[] encrypt(String text, PublicKey key) {
		byte[] cipherText = null;
		try {
			// get an RSA cipher object and print the provider
			final Cipher cipher = Cipher.getInstance(ALGORITHM);
			// encrypt the plain text using the public key
			cipher.init(Cipher.ENCRYPT_MODE, key);
			cipherText = cipher.doFinal(text.getBytes());
		} catch (Exception e) {
			e.printStackTrace();
		}
		return cipherText;
	}

	public static byte[] encrypt(String text, PrivateKey key) {
		byte[] cipherText = null;
		try {
			// get an RSA cipher object and print the provider
			final Cipher cipher = Cipher.getInstance(ALGORITHM);
			// encrypt the plain text using the public key
			cipher.init(Cipher.ENCRYPT_MODE, key);
			cipherText = cipher.doFinal(text.getBytes());
		} catch (Exception e) {
			e.printStackTrace();
		}
		return cipherText;
	}

	/**
	 * Decrypt text using private key.
	 * 
	 * @param text
	 *            :encrypted text
	 * @param key
	 *            :The private key
	 * @return plain text
	 * @throws java.lang.Exception
	 */

	public static String decrypt(byte[] text, PrivateKey key) {
		byte[] dectyptedText = null;
		try {
			// get an RSA cipher object and print the provider
			final Cipher cipher = Cipher.getInstance(ALGORITHM);

			// decrypt the text using the private key
			cipher.init(Cipher.DECRYPT_MODE, key);
			dectyptedText = cipher.doFinal(text);

		} catch (Exception ex) {
			ex.printStackTrace();
		}

		return new String(dectyptedText);
	}

	public static String decrypt(byte[] text, PublicKey key) {
		byte[] dectyptedText = null;
		try {
			// get an RSA cipher object and print the provider
			final Cipher cipher = Cipher.getInstance(ALGORITHM);

			// decrypt the text using the private key
			cipher.init(Cipher.DECRYPT_MODE, key);
			dectyptedText = cipher.doFinal(text);

		} catch (Exception ex) {
			ex.printStackTrace();
		}

		return new String(dectyptedText);
	}

	/**
	 * RSA the EncryptionUtil
	 */

	public static void generateKey() {

		// Check if the pair of keys are present else generate those.
		if (!areKeysPresent()) {
			// Method generates a pair of keys using the RSA algorithm and
			// stores it
			// in their respective files
			doGenerateKey();
		} else {
			try {
				@SuppressWarnings("resource")
				ObjectInputStream publicKeyIS = new ObjectInputStream(new FileInputStream(SERVER_PUBLIC_KEY_FILE));
				@SuppressWarnings("resource")
				ObjectInputStream privateKeyIS = new ObjectInputStream(new FileInputStream(SERVER_PRIVATE_KEY_FILE));
				PublicKey pubkey = (PublicKey) publicKeyIS.readObject();
				PrivateKey prkey = (PrivateKey) privateKeyIS.readObject();
				key = new KeyPair(pubkey, prkey);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

	public static byte[] doEncryption(String originalText, String dir, boolean flag) { // Text
		// to
		// be
		// encrypted
		byte[] cipherText = null;
		try {
			// Encrypt the string using the public key
			@SuppressWarnings("resource")
			ObjectInputStream inputStream = new ObjectInputStream(new FileInputStream(dir));
			if (flag) {
				final PublicKey publicKey = (PublicKey) inputStream.readObject();
				cipherText = encrypt(originalText, publicKey);
			} else {
				final PrivateKey privateKey = (PrivateKey) inputStream.readObject();
				cipherText = encrypt(originalText, privateKey);
			}

		} catch (Exception e) {
			e.printStackTrace();
		}
		return cipherText;
	}

	public static String doDecryption(byte[] cipherText, String dir, boolean flag) {
		String plainText = null;
		try {
			// Decrypt the cipher text using the private key.
			@SuppressWarnings("resource")
			ObjectInputStream inputStream = new ObjectInputStream(new FileInputStream(dir));
			if (flag) {
				final PrivateKey privateKey = (PrivateKey) inputStream.readObject();
				plainText = decrypt(cipherText, privateKey);
			} else {
				final PublicKey privateKey = (PublicKey) inputStream.readObject();
				plainText = decrypt(cipherText, privateKey);
			}

		} catch (Exception e) {
			e.printStackTrace();
		}
		return plainText;
	}

//	public static void main(String[] args) {
//		generateKey();
//		String plainB = "peace be upon you";
//		byte[] cipher1 = doEncryption(plainB, SERVER_PUBLIC_KEY_FILE, true);
//		String plainA1 = doDecryption(cipher1, SERVER_PRIVATE_KEY_FILE, true);
//
//		byte[] cipher2 = doEncryption(plainB, SERVER_PRIVATE_KEY_FILE, false);
//		String plainA2 = doDecryption(cipher2, SERVER_PUBLIC_KEY_FILE, false);
//		// Printing the Original, Encrypted and Decrypted Text
//		System.out.println("Original: " + plainB);
//		System.out.println("Encrypted: " + cipher1.toString());
//		System.out.println("Decrypted: " + plainA1);
//		System.out.println("Encrypted: " + cipher2.toString());
//		System.out.println("Decrypted: " + plainA2);
//
//	}

}
