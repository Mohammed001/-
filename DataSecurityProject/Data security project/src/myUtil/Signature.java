package myUtil;

import java.io.Serializable;

public class Signature implements Serializable{
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	public byte[] sig= null;
	public Signature(byte[] sig){
		this.sig = sig;
	}
}
