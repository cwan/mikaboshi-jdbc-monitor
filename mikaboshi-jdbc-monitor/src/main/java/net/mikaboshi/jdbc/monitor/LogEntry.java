package net.mikaboshi.jdbc.monitor;

import java.io.Serializable;
import java.sql.Connection;
import java.sql.Statement;
import java.util.Date;

import net.mikaboshi.csv.CSVStrategy;
import net.mikaboshi.csv.StandardCSVStrategy;
import net.mikaboshi.util.ThreadSafeUtils;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.exception.ExceptionUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * 1件のJDBCログ
 *
 * @author Takuma Umezawa
 *
 */
public class LogEntry implements Serializable {

	private static final long serialVersionUID = 1L;

	private static Log systemLogger = LogFactory.getLog(LogEntry.class);

	/** ログ項目数 */
	public static final int ITEM_SIZE = 16;

	public static final String END_OF_LINE = Character.toString((char) 0x1E);

	/**
	 * タグのシステムプロパティ名
	 */
	public static final String TAG_SYSTEM_PROPERTY_NAME = "net.mikaboshi.jdbc.monitor.TAG_SYSTEM_PROPERTY_NAME";

	public LogEntry() {}

	public LogEntry(String logType) {
		this(LogType.fromString(logType));
	}

	public LogEntry(LogType logType) {
		this.logType = logType;

		Date now = new Date();
		this.date = ThreadSafeUtils.formatDate(now, "yyyy-MM-dd");
		this.time = ThreadSafeUtils.formatDate(now, "HH-mm-ss.SSS");

		this.threadId = Thread.currentThread().getId();
		this.threadName = Thread.currentThread().getName();
	}

	/**
	 * 日付(yyyy-MM-dd)
	 */
	private String date = StringUtils.EMPTY;

	/**
	 * 時刻(HH-mm-ss.SSS)
	 */
	private String time = StringUtils.EMPTY;

	/**
	 * ログ種別
	 */
	private LogType logType = null;

	/**
	 * スレッドID
	 */
	private long threadId;

	/**
	 * スレッド名
	 */
	private String threadName;

	/**
	 * コネクションの識別子<p>
	 * {@code Integer.toHexString(java.sql.Connection#hashCode())}
	 */
	private String connectionId = StringUtils.EMPTY;

	/**
	 * ステートメントの識別子<p>
	 * {@code Integer.toHexString(java.sql.Statement#hashCode())}
	 */
	private String statementId = StringUtils.EMPTY;

	/**
	 * SQL文
	 */
	private String sql = StringUtils.EMPTY;

	/**
	 * 更新系SQLを実行したときの影響したレコード数
	 */
	private Integer affectedRows;

	/**
	 * 経過時間（ナノ秒）
	 */
	private Long elapsedTime;

	/**
	 * 処理結果
	 */
	private Result result = Result.FAILURE;

	/**
	 * オートコミットモード
	 */
	private AutoCommit autoCommit;

	/**
	 * ログが表示されるか（表示される場合はtrue）
	 * <p>
	 * ログ監視画面で、フィルタ設定により随時変更される。
	 * そのため、ログ出力・読込時にはこのフィールドは無視する。
	 */
	private Boolean visible = true;

	/**
	 * 例外スタックトレース（失敗時のみ）
	 */
	private String exception = StringUtils.EMPTY;

	/**
	 * コールスタック
	 */
	private String callStack =  StringUtils.EMPTY;

	/**
	 * タグ
	 */
	private String tag = StringUtils.EMPTY;

	/**
	 * 日付(yyyy-MM-dd)を取得します。
	 * @return 日付(yyyy-MM-dd)
	 */
	public String getDate() {
	    return date;
	}

	/**
	 * 日付(yyyy-MM-dd)を設定します。
	 * @param date 日付(yyyy-MM-dd)
	 */
	public void setDate(String date) {
	    this.date = date;
	}

	/**
	 * 時刻(HH-mm-ss.SSS)を取得します。
	 * @return 時刻(HH-mm-ss.SSS)
	 */
	public String getTime() {
	    return time;
	}

	/**
	 * 時刻(HH-mm-ss.SSS)を設定します。
	 * @param time 時刻(HH-mm-ss.SSS)
	 */
	public void setTime(String time) {
	    this.time = time;
	}

	/**
	 * ログ種別を取得します。
	 * @return ログ種別
	 */
	public LogType getLogType() {
	    return logType;
	}

	/**
	 * ログ種別を設定します。
	 * @param logType ログ種別
	 */
	public void setLogType(LogType logType) {
	    this.logType = logType;
	}

	/**
	 * スレッドIDを取得します。
	 * @return スレッドID
	 */
	public long getThreadId() {
		return threadId;
	}

	/**
	 * スレッドIDを設定します。
	 * @param threadId スレッドID
	 */
	public void setThreadId(long threadId) {
		this.threadId = threadId;
	}

	/**
	 * スレッド名を取得します。
	 * @return スレッド名
	 */
	public String getThreadName() {
		return threadName;
	}

	/**
	 * スレッド名を設定します。
	 * @param threadName スレッド名
	 */
	public void setThreadName(String threadName) {
		this.threadName = threadName;
	}

	/**
	 * コネクションの識別子<p>を取得します。
	 * @return コネクションの識別子<p>
	 */
	public String getConnectionId() {
	    return connectionId;
	}

	/**
	 * コネクションの識別子<p>を設定します。
	 * @param connectionId コネクションの識別子<p>
	 */
	public void setConnectionId(String connectionId) {
	    this.connectionId = connectionId;
	}

	/**
	 * ステートメントの識別子<p>を取得します。
	 * @return ステートメントの識別子<p>
	 */
	public String getStatementId() {
	    return statementId;
	}

	/**
	 * ステートメントの識別子<p>を設定します。
	 * @param statementId ステートメントの識別子<p>
	 */
	public void setStatementId(String statementId) {
	    this.statementId = statementId;
	}

	/**
	 * SQL文を取得します。
	 * @return SQL文
	 */
	public String getSql() {
	    return sql;
	}

	/**
	 * SQL文を設定します。
	 * @param sql SQL文
	 */
	public void setSql(String sql) {
	    this.sql = sql;
	}

	/**
	 * 更新系SQLを実行したときの影響したレコード数を取得します。
	 * @return 更新系SQLを実行したときの影響したレコード数
	 */
	public Integer getAffectedRows() {
	    return affectedRows;
	}

	/**
	 * 更新系SQLを実行したときの影響したレコード数を設定します。
	 * @param affectedRows 更新系SQLを実行したときの影響したレコード数
	 */
	public void setAffectedRows(Integer affectedRows) {
	    this.affectedRows = affectedRows;
	}

	/**
	 * 経過時間（ナノ秒）を取得します。
	 * @return 経過時間（ナノ秒）
	 */
	public Long getElapsedTime() {
	    return elapsedTime;
	}

	/**
	 * 経過時間（ナノ秒）を設定します。
	 * @param elapsedTime 経過時間（ナノ秒）
	 */
	public void setElapsedTime(Long elapsedTime) {
	    this.elapsedTime = elapsedTime;
	}

	/**
	 * 処理結果を取得します。
	 * @return 処理結果
	 */
	public Result getResult() {
	    return result;
	}

	/**
	 * 処理結果を設定します。
	 * @param result 処理結果
	 */
	public void setResult(Result result) {
	    this.result = result;
	}

	/**
	 * オートコミットモードを取得します。
	 * @return
	 */
	public AutoCommit getAutoCommit() {
		return autoCommit;
	}

	/**
	 * オートコミットモードを設定します。
	 * @param autoCommit
	 */
	public void setAutoCommit(AutoCommit autoCommit) {
		this.autoCommit = autoCommit;
	}

	/**
	 * オートコミットモードを設定します。
	 * @param autoCommit
	 */
	public void setAutoCommit(String autoCommit) {

		if ("true".equalsIgnoreCase(autoCommit)) {
			this.autoCommit = AutoCommit.TRUE;
		} else if ("false".equalsIgnoreCase(autoCommit)) {
			this.autoCommit = AutoCommit.FALSE;
		} else {
			this.autoCommit = AutoCommit.UNKNOWN;
		}
	}

	/**
	 * タグを設定します。
	 * @param tag
	 */
	public void setTag(String tag) {
		this.tag = tag;
	}

	/**
	 * タグを取得します。
	 * @return
	 */
	public String getTag() {
		return tag;
	}

	/**
	 * ログが表示されるか（表示される場合はtrue）を取得します。
	 * @return ログが表示されるか（表示される場合はtrue）
	 */
	public Boolean isVisible() {
	    return visible;
	}

	/**
	 * ログが表示されるか（表示される場合はtrue）を設定します。
	 * @param visible ログが表示されるか（表示される場合はtrue）
	 */
	public void setVisible(Boolean visible) {
	    this.visible = visible;
	}

	private static final CSVStrategy csvStrategy =
		new StandardCSVStrategy();

	protected String escapeItem(String str) {
		return csvStrategy.escape(str);
	}

	/**
	 * ログ出力文字列を取得する（CSV形式）。
	 * @return
	 */
	public String toLogString() {

		if (this.stopWatch != null) {
			setElapsedTime(this.stopWatch.getTime());
		}

		String separator = csvStrategy.getDelimiter();

		return new StringBuilder()
			.append(getDate())
			.append(separator)
			.append(getTime())
			.append(separator)
			.append(getLogType())
			.append(separator)
			.append(getThreadId())
			.append(separator)
			.append(getThreadName())
			.append(separator)
			.append(getConnectionId())
			.append(separator)
			.append(getStatementId())
			.append(separator)
			.append(escapeItem(getSql()))
			.append(separator)
			.append(nullValue(getAffectedRows()))
			.append(separator)
			.append(nullValue(getElapsedTime()))
			.append(separator)
			.append(nullValue(getResult()))
			.append(separator)
			.append(nullValue(getAutoCommit()))
			.append(separator)
			.append(escapeItem(getException()))
			.append(separator)
			.append(escapeItem(getCallStack()))
			.append(separator)
			.append(escapeItem(getTag()))
			.append(separator)
			.append(END_OF_LINE)
			.toString();
	}

	private String nullValue(Object o) {
		if (o == null) {
			return StringUtils.EMPTY;
		} else {
			return o.toString();
		}
	}

	/**
	 * ConnectionオブジェクトをもとにconnectionIdを設定する
	 * @param connection
	 */
	public void setConnection(Connection connection) {

		try {
			this.connectionId = Integer.toHexString(connection.hashCode());
			this.autoCommit = AutoCommit.fromBoolean(connection.getAutoCommit());
		} catch (Exception e) {
			systemLogger.warn(e.getMessage(), e);
		}
	}

	/**
	 * StatementオブジェクトをもとにconnectionIdとstatementIdを設定する
	 * @param statement
	 */
	public void setStatement(Statement statement) {

		try {
			setConnection(statement.getConnection());
			this.statementId = Integer.toHexString(statement.hashCode());
			this.autoCommit = AutoCommit.fromBoolean(statement.getConnection().getAutoCommit());
		} catch (Exception e) {
			systemLogger.warn(e.getMessage(), e);
		}

	}

	/**
	 * 日付、時刻を結合した文字列 "yyyy-MM-dd HH-mm-ss.SSS" を取得する。
	 * @return
	 */
	public String getDateTime() {
		return getDate() + " " + getTime();
	}

	/**
	 * 	更新件数を設定する
	 * @see #affectedRows(Integer)
	 * @param affectedRows 整数を表す文字列
	 * @throws NumberFormatException
	 */
	public void setAffectedRows(String affectedRows)
			throws NumberFormatException {

		if (StringUtils.isBlank(affectedRows)) {
			return;
		}

		setAffectedRows(Integer.parseInt(affectedRows));
	}

	/**
	 * 経過時間を設定する
	 * @see #elapsedTime(Long)
	 * @param elapsedTime 整数を表す文字列
	 * @throws NumberFormatException
	 */
	public void setElapsedTime(String elapsedTime)
			throws NumberFormatException {

		if (StringUtils.isBlank(elapsedTime)) {
			return;
		}

		setElapsedTime(Long.parseLong(elapsedTime));
	}

	/**
	 * 処理結果を設定する
	 * @see #setResult(Result)
	 * @param result 処理結果を表す文字列
	 */
	public void setResult(String result) {
		setResult(Result.fromString(result));
	}

	/**
	 * 例外スタックトレースを取得します。
	 * @return
	 */
	public String getException() {
		return exception;
	}

	/**
	 * 例外スタックトレースを設定します。
	 * @param stackTrace
	 */
	public void setException(String exception) {
		this.exception = exception;
	}

	/**
	 * 例外オブジェクトからスタックトレースを設定します。
	 * @param e
	 */
	public void setException(Throwable e) {
		this.exception = ExceptionUtils.getFullStackTrace(e);
	}

	/**
	 * コールスタックを取得する。
	 * @return
	 */
	public String getCallStack() {
		return callStack;
	}

	/**
	 * コールスタックを設定する。
	 * @param callStack
	 */
	public void setCallStack(String callStack) {
		this.callStack = callStack;
	}

	public void setCallStack(StackTraceElement[] stackTraceElements) {
		if (stackTraceElements == null) {
			this.callStack = StringUtils.EMPTY;
			return;
		}

		StringBuilder buf = new StringBuilder();

		int callStackLevel = LogMode.getInstance().getCallStackLevel();

		// 1つめは、Thread#getStackTraceなので除く
		for (int i = 1; i < stackTraceElements.length &&
			i <= callStackLevel; i++) {

			StackTraceElement e = stackTraceElements[i];
			buf.append(e.toString());
			buf.append(IOUtils.LINE_SEPARATOR);
		}

		int abbreviatedLines = stackTraceElements.length - callStackLevel;

		if (abbreviatedLines > 0) {
			buf.append(M17N.get("LogEntry.abbreviated_lines", abbreviatedLines));
		}

		this.callStack = buf.toString();
	}

	private NanoStopWatch stopWatch;

	/**
	 * 経過時間の計測を開始する。
	 */
	public void start() {
		this.stopWatch = new NanoStopWatch();
		this.stopWatch.start();
	}

	/**
	 * 経過時間の計測を終了する。
	 */
	public void stop() {
		this.stopWatch.stop();
	}

	/**
	 * ナノ秒単位で計測するストップウォッチ
	 *
	 */
	private static class NanoStopWatch {

		private long startTime = -1L;
		private long stopTime = -1L;

		public NanoStopWatch() {
			this.startTime = System.nanoTime();
		}

		public void start() {
			this.startTime = System.nanoTime();
		}

		public void stop() {
			this.stopTime = System.nanoTime();
		}

		public long getTime() {
			if (this.stopTime == -1L) {
				return System.nanoTime() - this.startTime;
			}
			return this.stopTime - this.startTime;
		}
	}
}
