package net.mikaboshi.jdbc.monitor;

import java.util.HashMap;
import java.util.Map;


/**
 * <p>
 * ログの種別（すなわちログに出力となるJDBCのイベント）を表す。
 * </p>
 * 
 * @author Takuma Umezawa
 */
public enum LogType {

	/** バッチ実行するリストにSQLを追加する */
	ADD_BATCH("add_batch"),
	
	/** トランザクションをコミットする */
	COMMIT("commit"),
	
	/** コネクションをクローズする */
	CONN_CLOSE("close"),
	
	/** コネクションをオープンする */
	CONN_OPEN("open"),
	
	/** バッチリストのSQLを一括実行する */
	EXE_BATCH("execute_batch"),
	
	/** PreparedStatementを実行する */
	EXE_PSTMT("execute_prepared"),
	
	/** Statementを実行する */
	EXE_STMT("execute_statement"),
	
	/** PreparedStatementを生成する */
	PREPARE_STMT("prepare"),
	
	/** トランザクションをロールバックする */
	ROLLBACK("rollback"),
	
	/** Statement, PreparedStatementをクローズする */
	CLOSE_STMT("close_statement");
	
	private String code;
	
	private LogType(String code) {
		this.code = code;
	}
	
	/**
	 * ログファイルに出力されるログ種別文字列を返す。
	 */
	@Override
	public String toString() {
		return this.code;
	}
	
	/**
	 * プロパティのキーを取得する。
	 * @return　プロパティのキー
	 */
	public String propertyName() {
		return "LogType." + this.code;
	}
	
	private static final Map<String, LogType> stringToEnum
		= new HashMap<String, LogType>();
	static {
		for (LogType e : values()) {
			stringToEnum.put(e.toString(), e);
		}
	}
	
	/**
	 * 文字列に対する LogType を返す。
	 * 文字列が不正ならば null を返す。
	 * @param symbol LogType を表す文字列。{@link #toString()} で得られる値。
	 * @return 文字列に対する LogType
	 */
	public static LogType fromString(String symbol) {
		return stringToEnum.get(symbol);
	}
}
