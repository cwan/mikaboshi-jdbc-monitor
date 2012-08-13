package net.mikaboshi.jdbc.monitor;

import java.util.HashMap;
import java.util.Map;

/**
 * 処理結果の種別を表す。
 *
 * @author Takuma Umezawa
 * @since 0.1.5
 */
public enum Result {
	/** 成功 */
	SUCCESS("success"),

	/** 失敗 */
	FAILURE("failure"),

	/** 成功・失敗両方 */
	ALL("all");

	private String name;

	private Result(String name) {
		this.name = name;
	}

	@Override
	public String toString() {
		return this.name;
	}

	public String getLable() {
		return M17N.get("result." + this.name);
	}

	private static final Map<String, Result> stringToEnum
		= new HashMap<String, Result>();
	static {
		for (Result e : values()) {
			stringToEnum.put(e.toString(), e);
		}
	}

	/**
	 * 文字列に対する Result を返す。
	 * 文字列が不正ならば null を返す。
	 * @param symbol Result を表す文字列。{@link #toString()} で得られる値。
	 * @return 文字列に対する Result
	 */
	public static Result fromString(String symbol) {
		return stringToEnum.get(symbol);
	}
}
