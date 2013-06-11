package net.mikaboshi.jdbc.monitor;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.RandomAccessFile;
import java.io.Reader;
import java.nio.charset.Charset;

import net.mikaboshi.csv.CSVStrategy;
import net.mikaboshi.csv.StandardCSVStrategy;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * JDBC Loggerのログファイルを読み込む。
 * @author Takuma Umezawa
 * @version 1.4.3
 */
public class LogFileAccessor {

	private static Log logger =
			LogFactory.getLog(LogFileAccessor.class);

	/**
	 * CSV行を読み直すときのバッファサイズ。（１論理行の最大値を設定）
	 */
	private static final int READ_AHEAD_LIMIT = 819200;

	private File logFile;
	private String logFileCharset;

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

	private Reader logFileReader;
	private InputStream logFileInputStream;
	private int lineCount = 0;
	private RandomAccessFile randomAccessLogFile;

	private long filePointer = 0L;

	public LogFileAccessor(String logFilePath, String logFileCharset) {
		this(new File(logFilePath), logFileCharset);
	}

	public LogFileAccessor(File logFile, String logFileCharset) {

		this.logFile = logFile;
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
		return this.logFile.exists();
	}

	/**
	 * 次のログファイルの論理行を読み込む。
	 * 「論理行」とは、物理的な改行コードではなく、１件のログが終わるまでを意味する（CSVの1行）。
	 * 次が無い場合はnullを返す。
	 *
	 * @return
	 * @throws IOException
	 */
	public synchronized LogEntry readNextLog() throws IOException {

		long logFileLength = this.logFile.length();

		// ファイルサイズが前回より増えていない場合は読み込まない
		if ( this.noLog && this.fileSize == logFileLength ) {
			return null;
		}

		if ( this.logFileReader == null ) {
			open();
		}

		// ファイルのサイズが減っていた場合は読み直す
		// （FileDialogを表示したときになぜか0になるため、それは除外する）
		if ( this.fileSize > logFileLength && logFileLength != 0 ) {
			close();
			this.filePointer = 0L;
			open();
		}

		if (logFileLength != 0) {
			this.fileSize = logFileLength;
		}

		if ( !this.csvLineIterator.hasNext() ) {
			this.noLog = true;
			unlock();
			return null;
		}

		this.csvLineIterator.mark(READ_AHEAD_LIMIT);

		String[] items = this.csvLineIterator.next();

		if (items == null) {
			this.noLog = true;
			unlock();
			return null;
		}

		this.lineCount++;

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

		if (items.length == 3 && LogWriter.LOGTYPE_CHARSET.equals(items[0])) {

			if (this.lineCount == 1
					&& !isSameCharset(items[1], this.logFileCharset)) {

				// 先頭に文字コードの指定がある場合は、読みなおす
				this.logFileCharset = items[1];

				close();
				this.filePointer = 0L;
				open();
			}

			return readNextLog();
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

	private boolean isSameCharset(String charset1, String charset2) {

		if (charset1 == null || charset2 == null) {
			return false;
		}

		try {
			return Charset.forName(charset1).equals(Charset.forName(charset2));
		} catch (Exception e) {
			return false;
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

	/**
	 * ログファイルの読み込みを開始する。
	 * @throws IOException
	 */
	public synchronized void open() throws IOException {

		if ( this.logFileReader != null ) {
			logger.warn("LogFileAccessor#open is called duplicately.");
			return;
		}

		for (int i = 1; i <= 5; i++) {
			// ローテーション時など一時的にファイルが存在しない場合があるため、リトライを行う

			try {
				this.randomAccessLogFile = new RandomAccessFile(this.logFile, "r");
				break;

			} catch (FileNotFoundException e) {
				if (i == 5) {
					throw e;
				}

				logger.warn("Log file is not found. (" + i + ")", e);

				try {
					Thread.sleep(100 * i);
				} catch (InterruptedException e1) {
					logger.error(e1.getMessage(), e1);
				}
			}
		}


		if (this.filePointer > this.randomAccessLogFile.length()) {
			this.filePointer = 0L;
		}

		if (this.filePointer > 0L) {
			this.randomAccessLogFile.seek(this.filePointer);
		}

		this.logFileInputStream = new BufferedInputStream(new FileInputStream(this.randomAccessLogFile.getFD()));

		String charset = StringUtils.isNotBlank(this.logFileCharset) ? this.logFileCharset : Charset.defaultCharset().name();

		this.logFileReader = new BufferedReader(
				new	InputStreamReader(this.logFileInputStream, charset));

		this.csvLineIterator = (StandardCSVStrategy.CSVIterator)
			this.csvStrategy.csvLines(this.logFileReader).iterator();

		if (this.filePointer == 0L) {
			this.fileSize = -1;
			this.lineCount = 0;
		}
	}

	/**
	 * ログファイルの読み込みを中止し、ファイルを閉じる。
	 */
	public synchronized void close() {

		IOUtils.closeQuietly(this.logFileReader);
		this.logFileReader = null;

		IOUtils.closeQuietly(this.logFileInputStream);
		this.logFileInputStream = null;

		IOUtils.closeQuietly(this.randomAccessLogFile);
		this.randomAccessLogFile = null;
	}

	/**
	 * ファイルを閉じてロックを解除する。
	 *
	 * @throws IOException
	 * @since 1.4.2
	 */
	private void unlock() throws IOException {

		if ( this.logFileReader == null ) {
			logger.warn("LogFileAccessor#unlock is called while the file is closed.");
			return;
		}

		this.filePointer = this.randomAccessLogFile.getFilePointer();

		close();
	}

}
