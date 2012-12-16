package net.mikaboshi.jdbc.monitor;

/**
 * 不正なログデータであることをあらわす例外クラス。
 * @author Takuma Umezawa
 *
 */
public class IllegalLogException extends Exception {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	public IllegalLogException() {
	}

	public IllegalLogException(String message) {
		super(message);
	}

	public IllegalLogException(Throwable cause) {
		super(cause);
	}

	public IllegalLogException(String message, Throwable cause) {
		super(message, cause);
	}

}
