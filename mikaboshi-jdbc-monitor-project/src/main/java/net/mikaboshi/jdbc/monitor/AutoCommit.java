package net.mikaboshi.jdbc.monitor;

import java.util.HashMap;
import java.util.Map;

/**
 * オートコミットの種別を表す。
 *
 * @author Takuma Umezawa
 * @since 1.3.3
 */
public enum AutoCommit {

	/** 成功 */
	TRUE("true"),

	/** 失敗 */
	FALSE("false"),

	/** 不明 */
	UNKNOWN("unknown"),

	/** 両方 */
	ALL("all");

	private String name;

	private AutoCommit(String name) {
		this.name = name;
	}

	@Override
	public String toString() {
		return this.name;
	}

	public String getLabel() {
		return M17N.get("auto_commit." + this.name);
	}

	private static final Map<String, AutoCommit> stringToEnum
		= new HashMap<String, AutoCommit>();

	static {
		for (AutoCommit e : values()) {
			stringToEnum.put(e.toString(), e);
		}
	}

	/**
	 * 文字列に対する AutoCommit を返す。
	 * 文字列が不正ならば null を返す。
	 * @param symbol autoCommit を表す文字列。{@link #toString()} で得られる値。
	 * @return 文字列に対する autoCommit
	 */
	public static AutoCommit fromString(String symbol) {
		return stringToEnum.get(symbol);
	}

	/**
	 * boolean に対する AutoCommit を返す。
	 * @param b
	 * @return
	 */
	public static AutoCommit fromBoolean(boolean b) {
		return b ? TRUE : FALSE;
	}

}
