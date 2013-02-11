package net.mikaboshi.jdbc.monitor;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.RandomAccessFile;
import java.io.Reader;

import net.mikaboshi.csv.CSVStrategy;
import net.mikaboshi.csv.StandardCSVStrategy;
import net.mikaboshi.io.RandomAccessFileInputStream;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * JDBC Loggerのログファイルを読み込む。
 * @author Takuma Umezawa
 *
 */
public class LogFileAccessor {

	private static Log logger =
			LogFactory.getLog(LogFileAccessor.class);

	/**
	 * CSV行を読み直すときのバッファサイズ。（１論理行の最大値を設定）
	 */
	private static final int READ_AHEAD_LIMIT = 819200;

	private String logFilePath;
	private String logFileCharset;

	public LogFileAccessor(String logFilePath, String logFileCharset) {
		this(new File(logFilePath), logFileCharset);
	}

	public LogFileAccessor(File logFile, String logFileCharset) {
		this.logFilePath = logFile.getAbsolutePath();
		this.logFileCharset = logFileCharset;

		// シャットダウン時にファイルを閉じる
		Runtime.getRuntime().addShutdownHook(new Thread() {
			 public void run() {
				 if (logFileReader != null) {
					 logger.trace(
							 "LogFileAccessor closes the reader at shutdown");
					 close();
				 }
			 }
		});
	}

	/**
	 * 読み込み対象のログファイルが存在するか確かめる
	 * @return
	 */
	public boolean existsLogFile() {
		return new File(this.logFilePath).exists();
	}

	/**
	 * 前回読み込んだときのファイルサイズ。（読み直しのチェックに使う）
	 */
	private long fileSize = -1L;

	/**
	 * ログの１論理行を解析する
	 */
	private CSVStrategy csvStrategy = new StandardCSVStrategy();

	private StandardCSVStrategy.CSVIterator csvLineIterator;

	/**
	 * 前回ログが取得できなかった場合はtrue
	 */
	private boolean noLog = false;

	/**
	 * 不完全なログを読み込んだ時のリトライ
	 */
	private int retryCount = 0;

	/**
	 * 次のログファイルの論理行を読み込む。
	 * 「論理行」とは、物理的な改行コードではなく、１件のログが終わるまでを意味する（CSVの1行）。
	 * 次が無い場合はnullを返す。
	 *
	 * @return
	 * @throws IOException
	 */
	public synchronized LogEntry readNextLog() throws IOException {

		if ( this.logFileReader == null ) {
			open();
		}

		long logFileLength = new File(this.logFilePath).length();

		// ファイルサイズが前回より増えていない場合は読み込まない
		if ( this.noLog && this.fileSize == logFileLength ) {
			return null;
		}

		// ファイルのサイズが減っていた場合は読み直す
		// （FileDialogを表示したときになぜか0になるため、それは除外する）
		if ( this.fileSize > logFileLength && logFileLength != 0 ) {
			close();
			open();
		}

		if (logFileLength != 0) {
			this.fileSize = logFileLength;
		}

		if ( !this.csvLineIterator.hasNext() ) {
			this.noLog = true;
			return null;
		}

		this.csvLineIterator.mark(READ_AHEAD_LIMIT);

		String[] items = this.csvLineIterator.next();

		if ( items == null) {
			this.noLog = true;
			return null;
		}

		this.noLog = false;

		// ログが不完全だった場合、5回までは再取得する
		if ( items.length < 1 ||
				!LogEntry.END_OF_LINE.equals(items[items.length - 1]) ) {
			if (++this.retryCount < 5) {

				try {
					Thread.sleep(100);
				} catch (InterruptedException e) {
					logger.warn(e.getMessage(), e);
				}

				return readNextLog();
			}
		}

		ViewerConfig config = ViewerConfig.getInstance();

		if (items.length == 3 && config.isLoadConnectInfoFromLog()) {
			// DB接続情報
			if (LogWriter.LOGTYPE_DRIVER_CLASS.equals(items[0])) {
				config.getConnectInfo().setDriver(items[1]);
			} else if (LogWriter.LOGTYPE_DRIVER_VERSION.equals(items[0])) {
				config.getConnectInfo().setDriverVersion(items[1]);
			} else if (LogWriter.LOGTYPE_URL.equals(items[0])) {
				config.getConnectInfo().setUrl(items[1]);
			} else if (LogWriter.LOGTYPE_USER.equals(items[0])) {
				config.getConnectInfo().setUser(items[1]);
			} else if (LogWriter.LOGTYPE_PASSWORD.equals(items[0])) {
				config.getConnectInfo().setPassword(items[1]);
			}

			return readNextLog();
		}

		if (items.length != LogEntry.ITEM_SIZE) {
			return null;
		}

		try {
			return createLogEntry(items);

		} catch (IllegalLogException e) {
			if (logger.isErrorEnabled()) {
				logger.error("Invalid log : <" + StringUtils.join(items, ",") + ">");
			}

			return null;
		}
	}

	private LogEntry createLogEntry(String[] items)
			throws IllegalLogException {

		try {
			LogEntry entry = new LogEntry();
			int index = 0;
			entry.setDate(items[index++]);
			entry.setTime(items[index++]);
			entry.setLogType(LogType.fromString(items[index++]));
			entry.setThreadId(Long.parseLong(items[index++]));
			entry.setThreadName(items[index++]);
			entry.setConnectionId(items[index++]);
			entry.setStatementId(items[index++]);
			entry.setSql(this.csvStrategy.unescape(items[index++]));
			entry.setAffectedRows(items[index++]);
			entry.setElapsedTime(items[index++]);
			entry.setResult(items[index++]);
			entry.setAutoCommit(items[index++]);
			entry.setException(this.csvStrategy.unescape(items[index++]));
			entry.setCallStack(this.csvStrategy.unescape(items[index++]));
			entry.setTag(this.csvStrategy.unescape(items[index++]));
			return entry;
		} catch (Exception e) {
			throw new IllegalLogException("unexpected items", e);
		}
	}

	private Reader logFileReader;

	private InputStream logFileInputStream;

	/**
	 * ログファイルの読み込みを開始する。
	 * @throws IOException
	 */
	public synchronized void open() throws IOException {

		if ( this.logFileReader != null ) {
			logger.warn("LogFileAccessor#open is called duplicately.");
			return;
		}

		RandomAccessFile logFile = new RandomAccessFile(this.logFilePath, "r");

		this.logFileInputStream = new RandomAccessFileInputStream(logFile);



		this.logFileReader = new BufferedReader(new
				InputStreamReader(logFileInputStream, this.logFileCharset));

		this.csvLineIterator = (StandardCSVStrategy.CSVIterator)
			this.csvStrategy.csvLines(this.logFileReader).iterator();

		this.fileSize = -1;
	}

	/**
	 * ログファイルの読み込みを中止し、ファイルを閉じる。
	 */
	public synchronized void close() {

		if ( !new File(this.logFilePath).exists() ) {
			return;
		}

		if ( this.logFileReader != null ) {
			try {
				this.logFileReader.close();
				this.logFileReader = null;

			} catch (IOException e) {
				logger.error("error occurs in closing logFileReader", e);
			}
		}

		if ( this.logFileInputStream != null ) {
			try {
				this.logFileInputStream.close();
				this.logFileInputStream = null;

			} catch (IOException e) {
				logger.error("error occurs in closing logFileInputStream", e);
			}
		}
	}


}
