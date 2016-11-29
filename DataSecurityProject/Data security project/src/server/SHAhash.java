package server;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class SHAhash {
	/**
	 * @param args
	 * @throws NoSuchAlgorithmException
	 */
//	public static void main(String[] args) throws NoSuchAlgorithmException {
//		System.out.println(sha256("test string to sha256"));
//	}

	static String sha256(String input) throws NoSuchAlgorithmException {
		MessageDigest digest = MessageDigest.getInstance("SHA-256");
		byte[] hash = digest.digest(input.getBytes(StandardCharsets.UTF_8));
		// MessageDigest mDigest = MessageDigest.getInstance("SHA256");
		// byte[] result = mDigest.digest(input.getBytes());
		StringBuffer sb = new StringBuffer();
		for (int i = 0; i < hash.length; i++) {
			sb.append(Integer.toString((hash[i] & 0xff) + 0x100, 16).substring(1));
		}
		return sb.toString();
	}
}