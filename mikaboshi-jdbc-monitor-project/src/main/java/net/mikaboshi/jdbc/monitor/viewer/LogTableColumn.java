package net.mikaboshi.jdbc.monitor.viewer;

import java.util.HashMap;
import java.util.Map;

/**
 * ログテーブルの列を表す。
 *
 * @author Takuma Umezawa
 * @since 0.1.5
 */
public enum LogTableColumn {
	TIMESTAMP("timestamp", 0),
	LOG_TYPE("log_type", 1),
	THREAD_NAME("thread_name", 2),
	CONNECTION_ID("connection_id", 3),
	STATEMENT_ID("statement_id", 4),
	SQL("sql", 5),
	AFFECTED_ROWS("affected_rows", 6),
	ELAPSED_TIME("elapsed_time", 7),
	RESULT("result", 8),
	AUTO_COMMIT("auto_commit", 9),
	TAG("tag", 10);

	private String name;
	private int columnIndex;

	private LogTableColumn(String name, int columnIndex) {
		this.name = name;
		this.columnIndex = columnIndex;
	}

	/**
	 * 列番号を返す。
	 * @return 列番号 (0以上の整数)
	 */
	public int getColumnIndex() {
		return this.columnIndex;
	}

	@Override
	public String toString() {
		return this.name;
	}

	private static final Map<String, LogTableColumn> stringToEnum
		= new HashMap<String, LogTableColumn>();
	static {
		for (LogTableColumn e : values()) {
			stringToEnum.put(e.toString(), e);
		}
	}

	/**
	 * 文字列に対する LogTableColumn を返す。
	 * 文字列が不正ならば null を返す。
	 * @param symbol LogTableColumn を表す文字列。{@link #toString()} で得られる値。
	 * @return 文字列に対する LogTableColumn
	 */
	public static LogTableColumn fromString(String symbol) {
		return stringToEnum.get(symbol);
	}
}
