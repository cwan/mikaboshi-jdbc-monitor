package net.mikaboshi.jdbc.monitor;

import java.io.IOException;

import net.mikaboshi.csv.CSVStrategy;
import net.mikaboshi.csv.StandardCSVStrategy;
import net.mikaboshi.log.SimpleFileLogger;

import org.apache.commons.io.FileUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * <p>
 * {@link LogEntry}をログファイルに書き出す。
 * </p><p>
 * ログファイルは、デフォルトではカレントディレクトリの {@code jdbc.log} であるが、
 * システムプロパティ {@code net.mikaboshi.jdbc_monitor.logfile} でパスを指定可能。
 * </p>
 *
 * @author Takuma Umezawa
 *
 */
public class LogWriter {

	public static final String PROP_NAME = "net.mikaboshi.jdbc_monitor.logfile";

	public static final String PROP_DELETE_ON_BOOT = "net.mikaboshi.jdbc_monitor.logfile.delete_on_boot";

	public static final String PROP_ROTATE_MB = "net.mikaboshi.jdbc_monitor.rotate_mb";

	public static final String PROP_WRITE_PASSWORD = "net.mikaboshi.jdbc_monitor.write_password";

	public static final String PROP_WRITE_CONNECT = "net.mikaboshi.jdbc_monitor.write_connect";

	public static final String LOGTYPE_DRIVER_CLASS = "driver";

	public static final String LOGTYPE_DRIVER_VERSION = "driverVersion";

	public static final String LOGTYPE_URL = "url";

	public static final String LOGTYPE_USER = "user";

	public static final String LOGTYPE_PASSWORD = "password";

	private static Log systemLogger = LogFactory.getLog(LogWriter.class);

	private final SimpleFileLogger logger;

	private static final CSVStrategy csvStrategy = new StandardCSVStrategy();

	private LogWriter() throws IOException {
		String path =
			System.getProperty(PROP_NAME, "jdbc.log");

		boolean append = true;
		boolean autoFlush = false;

		this.logger = new SimpleFileLogger(path, append, autoFlush, 32768);

		if ("true".equals(System.getProperty(PROP_DELETE_ON_BOOT))) {
			// 既存のログファイルを削除する
			this.logger.clean();
		}

		String rotateMb = System.getProperty(PROP_ROTATE_MB);

		if (rotateMb != null) {
			long size = Long.parseLong(rotateMb);
			this.logger.setRotetaSize(size * FileUtils.ONE_MB);
		}
	}

	private static LogWriter instance;

	static {
		try {
			instance = new LogWriter();
		} catch (IOException e) {
			systemLogger.error("creatting LogWriter instance failed.", e);
		}
	}

	/**
	 * 引数のLogEntryをログに書き出す。
	 * タグ情報は、このメソッド内でシステムプロパティから取得する。
	 *
	 * @param entry
	 */
	public static synchronized void put(LogEntry entry) {
		if (instance != null) {
			try {
				String tag = System.getProperty(LogEntry.TAG_SYSTEM_PROPERTY_NAME);

				if (tag != null) {
					entry.setTag(tag);
				}

				instance.logger.put(entry.toLogString());
				instance.logger.flush();
			} catch (IOException e) {
				systemLogger.error("Writing log failed.", e);
			}
		}
	}

	/**
	 * ドライバクラスをログに書き出す。
	 * @param driverClass
	 */
	public static synchronized void putDriver(String driverClass) {
		purConnectInfo(LOGTYPE_DRIVER_CLASS, driverClass);
	}

	/**
	 * ドライババージョンログに書き出す。
	 * @param majorVersion
	 * @param minorVersion
	 */
	public static synchronized void putDriverVersion(int majorVersion, int minorVersion) {
		purConnectInfo(LOGTYPE_DRIVER_VERSION,
				new StringBuilder().append(majorVersion).append(".").append(minorVersion).toString());
	}

	/**
	 * URLをログに書き出す。
	 * @param url
	 */
	public static void putUrl(String url) {
		purConnectInfo(LOGTYPE_URL, url);
	}

	/**
	 * ユーザIDをログに書き出す。
	 * @param user
	 */
	public static void putUser(String user) {
		purConnectInfo(LOGTYPE_USER, user);
	}

	/**
	 * システムプロパティ「net.mikaboshi.jdbc_monitor.write_password」と
	 * 「net.mikaboshi.jdbc_monitor.write_connect」の両方がtrueに設定されている場合、
	 * パスワードをログに書き出す。
	 * @param password
	 */
	public static void putPassword(String password) {
		if ("true".equals(System.getProperty(PROP_WRITE_PASSWORD, "false"))) {
			purConnectInfo(LOGTYPE_PASSWORD, password);
		}
	}

	/**
	 * システムプロパティ「net.mikaboshi.jdbc_monitor.write_connect」がtrueに
	 * 設定されている場合、接続情報をログに書き出す。（デフォルトはtrue）
	 * @param logType
	 * @param value
	 */
	private static synchronized void purConnectInfo(String logType, String value) {

		if ("false".equals(System.getProperty(PROP_WRITE_CONNECT, "true"))) {
			return;
		}

		if (instance != null) {

			String log = new StringBuilder()
				.append(logType)
				.append(csvStrategy.getDelimiter())
				.append(csvStrategy.escape(value))
				.append(csvStrategy.getDelimiter())
				.append(LogEntry.END_OF_LINE)
				.toString();

			try {
				instance.logger.put(log);
				instance.logger.flush();
			} catch (IOException e) {
				systemLogger.error("Writing log failed.", e);
			}
		}
	}
}
