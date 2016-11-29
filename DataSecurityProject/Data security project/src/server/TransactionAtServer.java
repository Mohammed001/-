package server;

import java.text.SimpleDateFormat;
import java.util.Date;

import myUtil.RSATransaction;
import myUtil.Signature;

public class TransactionAtServer extends myUtil.RSATransaction {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	String plainFrom, plainTo, plainHowMuch;
	public String date = null;

	Signature sig = null;

	public String toString() {
		return "client" + plainFrom + " transfere " + plainHowMuch + "$" + " to client" + plainTo + "\n";
	}

	TransactionAtServer(RSATransaction t, Signature s) {
		super(t.from, t.to, t.howMuch, t.completeCipherMessage);
		this.sig = s;
		plainFrom = RSA.doDecryption(from, RSA.SERVER_PRIVATE_KEY_FILE, true);
		plainTo = RSA.doDecryption(to, RSA.SERVER_PRIVATE_KEY_FILE, true);
		plainHowMuch = RSA.doDecryption(howMuch, RSA.SERVER_PRIVATE_KEY_FILE, true);
		date = new String(new SimpleDateFormat("MM/dd/yy").format(new Date()).toString());
	}
}
