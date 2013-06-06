package net.mikaboshi.jdbc.monitor;

import java.io.IOException;
import java.nio.charset.Charset;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

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
 * @version 1.4.3
 *
 */
public class LogWriter {

	public static final String PROP_NAME = "net.mikaboshi.jdbc_monitor.logfile";

	public static final String PROP_DELETE_ON_BOOT = "net.mikaboshi.jdbc_monitor.logfile.delete_on_boot";

	public static final String PROP_ROTATE_MB = "net.mikaboshi.jdbc_monitor.rotate_mb";

	public static final String PROP_WRITE_PASSWORD = "net.mikaboshi.jdbc_monitor.write_password";

	public static final String PROP_WRITE_CONNECT = "net.mikaboshi.jdbc_monitor.write_connect";

	public static final String LOGTYPE_CHARSET = "charset";

	public static final String LOGTYPE_DRIVER_CLASS = "driver";

	public static final String LOGTYPE_DRIVER_VERSION = "driverVersion";

	public static final String LOGTYPE_URL = "url";

	public static final String LOGTYPE_USER = "user";

	public static final String LOGTYPE_PASSWORD = "password";

	private static Log systemLogger = LogFactory.getLog(LogWriter.class);

	private static boolean outputCharset = false;

	private final SimpleFileLogger logger;

	private static final CSVStrategy csvStrategy = new StandardCSVStrategy();

	private static final Queue<Object> logQueue = new ConcurrentLinkedQueue<Object>();

	private static final ExecutorService threadPool = Executors.newFixedThreadPool(1);

	private LogWriter() throws IOException {
		String path =
			System.getProperty(PROP_NAME, "jdbc.log");

		boolean append = true;
		boolean autoFlush = false;

		this.logger = new SimpleFileLogger(path, append, autoFlush, 32768);
		this.logger.setCloseOnShutdown(false);


		// プロセス停止時にキューのログを出し切る
		Runtime.getRuntime().addShutdownHook(new Thread() {

			@Override
			public void run() {

				Object log = null;

				while ((log = logQueue.poll()) != null) {

					write(log);
				}

				logger.close();
			}
		});

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

		// 非同期でログを出力する
		threadPool.execute(new Runnable() {

			@Override
			public void run() {

				while (true) {

					Object log = logQueue.poll();

					if (log == null) {
						try {
							Thread.sleep(100);
						} catch (InterruptedException e) {
							systemLogger.error(e.getMessage(), e);
						}

						continue;
					}

					write(log);
				}
			}
		});
	}

	/**
	 * 引数のLogEntryをログに書き出す。
	 * タグ情報は、このメソッド内でシステムプロパティから取得する。
	 *
	 * @param entry
	 */
	public static void put(LogEntry entry) {

		if (instance == null) {
			return;
		}

		String tag = null;

		tag = Tag.getInstance().get();

		if (tag == null || tag.isEmpty()) {
			// 非推奨の方法
			tag = System.getProperty(LogEntry.TAG_SYSTEM_PROPERTY_NAME);
		}

		if (tag != null) {
			entry.setTag(tag);
		}

		logQueue.offer(entry);
	}

	/**
	 * ドライバクラスをログに書き出す。
	 * @param driverClass
	 */
	public static void putDriver(String driverClass) {

		if (!outputCharset) {
			putSimpleInfo(LOGTYPE_CHARSET, Charset.defaultCharset().name());
			outputCharset = true;
		}

		putConnectInfo(LOGTYPE_DRIVER_CLASS, driverClass);
	}

	/**
	 * ドライババージョンログに書き出す。
	 * @param majorVersion
	 * @param minorVersion
	 */
	public static void putDriverVersion(int majorVersion, int minorVersion) {
		putConnectInfo(LOGTYPE_DRIVER_VERSION,
				new StringBuilder().append(majorVersion).append(".").append(minorVersion).toString());
	}

	/**
	 * URLをログに書き出す。
	 * @param url
	 */
	public static void putUrl(String url) {
		putConnectInfo(LOGTYPE_URL, url);
	}

	/**
	 * ユーザIDをログに書き出す。
	 * @param user
	 */
	public static void putUser(String user) {
		putConnectInfo(LOGTYPE_USER, user);
	}

	/**
	 * システムプロパティ「net.mikaboshi.jdbc_monitor.write_password」と
	 * 「net.mikaboshi.jdbc_monitor.write_connect」の両方がtrueに設定されている場合、
	 * パスワードをログに書き出す。
	 * @param password
	 */
	public static void putPassword(String password) {
		if ("true".equals(System.getProperty(PROP_WRITE_PASSWORD, "false"))) {
			putConnectInfo(LOGTYPE_PASSWORD, password);
		}
	}

	/**
	 * システムプロパティ「net.mikaboshi.jdbc_monitor.write_connect」がtrueに
	 * 設定されている場合、接続情報をログに書き出す。（デフォルトはtrue）
	 * @param logType
	 * @param value
	 */
	private static void putConnectInfo(String logType, String value) {

		if ("false".equals(System.getProperty(PROP_WRITE_CONNECT, "true"))) {
			return;
		}

		putSimpleInfo(logType, value);
	}

	private static void putSimpleInfo(String logType, String value) {

		if (instance == null) {
			return;
		}

		String log = new StringBuilder()
			.append(logType)
			.append(csvStrategy.getDelimiter())
			.append(csvStrategy.escape(value))
			.append(csvStrategy.getDelimiter())
			.append(LogEntry.END_OF_LINE)
			.toString();

		logQueue.offer(log);
	}

	private static void write(Object log) {

		try {
			if (log instanceof LogEntry) {
				instance.logger.put(((LogEntry) log).toLogString());
			} else if (log instanceof String) {
				instance.logger.put((String) log);
			}

			instance.logger.flush();

		} catch (IOException e) {
			systemLogger.error("Writing log failed.", e);
		}
	}

}
