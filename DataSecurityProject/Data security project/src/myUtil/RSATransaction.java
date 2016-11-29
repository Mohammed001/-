package myUtil;


public class RSATransaction extends Message {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public RSATransaction() {

	}

	public byte[] from = null;
	public byte[] to = null;
	public byte[] howMuch = null;
	public byte[] completeCipherMessage = null;

	public RSATransaction(byte[] from, byte[] to, byte[] howMuch, byte[] completeCipherMessage) {
		this.from = from;
		this.to = to;
		this.howMuch = howMuch;
		this.completeCipherMessage = completeCipherMessage;
	}
}
