package myUtil;


public class AESMessage extends Message{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private byte[] to = null;
	private byte[] howMuch = null;
	
	public void setQuery(byte[] to) {
		this.to = to;
	}
	public void setResponse(byte[] howMuch) {
		this.howMuch = howMuch;
	}
	
	public byte[] getResponse(){
		return howMuch;
	}
	
	public byte[] getQuery(){
		return to;
	}
	
	
	
	
}
