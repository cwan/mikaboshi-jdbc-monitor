package net.mikaboshi.jdbc.monitor;

import java.awt.Dimension;
import java.io.File;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;

import net.mikaboshi.jdbc.monitor.viewer.LogTableFilter.CommandSqlFilter;
import net.mikaboshi.jdbc.monitor.viewer.LogTableFilter.NoSqlFilter;
import net.mikaboshi.jdbc.monitor.viewer.LogTableFilter.RegExSqlFilter;
import net.mikaboshi.jdbc.monitor.viewer.LogTableFilter.SqlFilter;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.builder.ToStringBuilder;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * ログビューアの設定
 * @author Takuma Umezawa
 * @since 1.3.0
 * @version 1.4.3
 */
@XmlRootElement(name = "config")
public class ViewerConfig {

	public enum FormatType {
		FORMAT,
		RAW,
		LINE
	}

	private static ViewerConfig INSTANCE;

	private static Log logger = LogFactory.getLog(ViewerConfig.class);

	/** 永続化に使用するファイル */
	private static final File CONFIG_FILE =
		new File( System.getProperty(
				"net.mikaboshi.jdbc_log_viewer.config",
				"jdbc_log_viewer.xml") );

	private ViewerConfig() {};

	/** SQLのフォーマット種別 */
	private FormatType formatType = FormatType.FORMAT;

	/** SQL実行の上限 */
	private int limitExecuteSqlRows = 100;

	/** ログから設定情報を読み込むかどうか */
	private boolean loadConnectInfoFromLog = true;

	/** フィルタの設定  */
	private Filter filter = new Filter();

	/** JdbcLogViewerFrameの設定 */
	private Frame frame = new Frame();

	/** ログファイルの設定 */
	private LogFile logFile = new LogFile();

	/** ログテーブルの設定 */
	private LogTable logTable = new LogTable();

	/** DB接続設定 */
	private ConnectInfo connectInfo = new ConnectInfo();

	/** ログテーブル検索設定 */
	private LogTableSearch logTableSearch = new LogTableSearch();

	public static synchronized ViewerConfig getInstance() {

		if (INSTANCE == null) {
			INSTANCE = new ViewerConfig();
			INSTANCE.load();
		}

		return INSTANCE;
	}

	public static ViewerConfig getDefault() {
		return new ViewerConfig();
	}

	/**
	 * 設定をファイルから読み込む
	 */
	public synchronized void load() {

		if (!CONFIG_FILE.exists()) {
			// 初回起動時などには存在しないので、異常ではない
			logger.debug("Config file does not exist. " + CONFIG_FILE.getAbsolutePath());
			return;
		}

		try {
			JAXBContext context = JAXBContext.newInstance(ViewerConfig.class);
			Unmarshaller unmarshaller = context.createUnmarshaller();
			INSTANCE = (ViewerConfig) unmarshaller.unmarshal(CONFIG_FILE);

		} catch (JAXBException e) {
			logger.warn("Could not load the viewer configuration from a file. " + CONFIG_FILE.getAbsolutePath(), e);
			INSTANCE = new ViewerConfig();
		}
	}

	/**
	 * 設定をファイルに書き出す
	 */
	public synchronized void store() {

		try {
			JAXBContext context = JAXBContext.newInstance(ViewerConfig.class);
			Marshaller marshaller = context.createMarshaller();
			marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, Boolean.TRUE);
			marshaller.marshal(INSTANCE, CONFIG_FILE);
		} catch (JAXBException e) {
			logger.warn("Could not save the viewer configuration to a file. " + CONFIG_FILE.getAbsolutePath(), e);
		}
	}

	@XmlTransient
	public FormatType getFormatTypeAsEnum() {
		return formatType;
	}

	@XmlTransient
	public void setFormatTypeByEnum(FormatType formatType) {
		this.formatType = formatType;
	}

	public String getFormatType() {
		return formatType.toString();
	}

	public void setFormatType(String arg) {

		for (FormatType formatType : FormatType.values()) {
			if (formatType.toString().equals(arg)) {
				this.formatType = formatType;
				return;
			}
		}

		this.formatType = null;
	}

	public int getLimitExecuteSqlRows() {
		return limitExecuteSqlRows;
	}

	public void setLimitExecuteSqlRows(int limitExecuteSqlRows) {
		this.limitExecuteSqlRows = limitExecuteSqlRows;
	}

	public boolean isLoadConnectInfoFromLog() {
		return loadConnectInfoFromLog;
	}

	public void setLoadConnectInfoFromLog(boolean loadConnectInfoFromLog) {
		this.loadConnectInfoFromLog = loadConnectInfoFromLog;
	}

	public Filter getFilter() {
		return filter;
	}

	public void setFilter(Filter filter) {
		this.filter = filter;
	}

	public Frame getFrame() {
		return frame;
	}

	public void setFrame(Frame frame) {
		this.frame = frame;
	}

	public LogFile getLogFile() {
		return logFile;
	}

	public void setLogFile(LogFile logFile) {
		this.logFile = logFile;
	}

	public LogTable getLogTable() {
		return logTable;
	}

	public void setLogTable(LogTable logTable) {
		this.logTable = logTable;
	}

	@XmlTransient
	public ConnectInfo getConnectInfo() {
		return connectInfo;
	}

	public void setConnectInfo(ConnectInfo connectInfo) {
		this.connectInfo = connectInfo;
	}

	public LogTableSearch getLogTableSearch() {
		return logTableSearch;
	}

	public void setLogTableSearch(LogTableSearch logTableSearch) {
		this.logTableSearch = logTableSearch;
	}

	@Override
	public String toString() {
		return ToStringBuilder.reflectionToString(this);
	}

	/**
	 * フィルタの設定
	 */
	public static class Filter {

		/** 更新件数の下限  */
		private int affectedRowsMin = Integer.MIN_VALUE;

		/** 更新件数の上限  */
		private int affectedRowsMax = Integer.MAX_VALUE;

		/** コネクションID */
		private String connectionId = StringUtils.EMPTY;

		/** 経過時間の下限 */
		private long elapsedTimeMin = Long.MIN_VALUE;

		/** 経過時間の上限 */
		private long elapsedTimeMax = Long.MAX_VALUE;

		/** ログ種別 */
		private LogType logType = new LogType();

		/** 結果 */
		private Result result = Result.ALL;

		/** オートコミット */
		private AutoCommit autoCommit = AutoCommit.ALL;

		/** タグ */
		private String tag =  StringUtils.EMPTY;

		/** コマンドSQLフィルタ */
		private CommandSqlFilter commandSqlFilter = null;

		/** 正規表現SQLフィルタ */
		private RegExSqlFilter regExSqlFilter = null;

		/** ステートメントID */
		private String statementId = StringUtils.EMPTY;

		/** スレッド名 */
		private String threadName = StringUtils.EMPTY;

		public int getAffectedRowsMin() {
			return affectedRowsMin;
		}

		public void setAffectedRowsMin(int affectedRowsMin) {
			this.affectedRowsMin = affectedRowsMin;
		}

		public int getAffectedRowsMax() {
			return affectedRowsMax;
		}

		public void setAffectedRowsMax(int affectedRowsMax) {
			this.affectedRowsMax = affectedRowsMax;
		}

		public String getConnectionId() {
			return connectionId;
		}

		public void setConnectionId(String connectionId) {
			this.connectionId = connectionId;
		}

		public long getElapsedTimeMin() {
			return elapsedTimeMin;
		}

		public void setElapsedTimeMin(long elapsedTimeMin) {
			this.elapsedTimeMin = elapsedTimeMin;
		}

		public long getElapsedTimeMax() {
			return elapsedTimeMax;
		}

		public void setElapsedTimeMax(long elapsedTimeMax) {
			this.elapsedTimeMax = elapsedTimeMax;
		}

		public LogType getLogType() {
			return logType;
		}

		public void setLogType(LogType logType) {
			this.logType = logType;
		}

		public Result getResult() {
			return result;
		}

		public void setResult(Result result) {
			this.result = result;
		}

		public AutoCommit getAutoCommit() {
			return autoCommit;
		}

		public void setAutoCommit(AutoCommit autoCommit) {
			this.autoCommit = autoCommit;
		}

		public String getTag() {
			return tag;
		}

		public void setTag(String tag) {
			this.tag = tag;
		}

		public NoSqlFilter getNoSqlFilter() {
			if (this.commandSqlFilter == null &&
					this.regExSqlFilter == null) {
				return NoSqlFilter.INSTANCE;
			} else {
				return null;
			}
		}

		public CommandSqlFilter getCommandSqlFilter() {
			return commandSqlFilter;
		}

		public RegExSqlFilter getRegExSqlFilter() {
			return regExSqlFilter;
		}

		/**
		 * 現在有効なSQLフィルタを取得する。
		 * @return
		 */
		@XmlTransient
		public SqlFilter getSqlFilter() {

			if (this.commandSqlFilter != null) {
				return this.commandSqlFilter;
			} else if (this.regExSqlFilter != null) {
				return this.regExSqlFilter;
			} else {
				return NoSqlFilter.INSTANCE;
			}
		}

		public void setNoSqlFilter(NoSqlFilter noSqlFilter) {
			this.commandSqlFilter = null;
			this.regExSqlFilter = null;
		}

		public void setCommandSqlFilter(CommandSqlFilter commandSqlFilter) {
			this.commandSqlFilter = commandSqlFilter;
			this.regExSqlFilter = null;
		}

		public void setRegExSqlFilter(RegExSqlFilter regExSqlFilter) {
			this.commandSqlFilter = null;
			this.regExSqlFilter = regExSqlFilter;
		}

		public String getStatementId() {
			return statementId;
		}

		public void setStatementId(String statementId) {
			this.statementId = statementId;
		}

		public String getThreadName() {
			return threadName;
		}

		public void setThreadName(String threadName) {
			this.threadName = threadName;
		}

		@Override
		public String toString() {
			return ToStringBuilder.reflectionToString(this);
		}
	}

	/**
	 * フィルタのログ種別
	 */
	public static class LogType {

		/** バッチ追加 */
		private boolean addBatch = true;

		/** コネクションクローズ */
		private boolean close = true;

		/** コミット */
		private boolean commit = true;

		/** バッチ実行 */
		private boolean executeBatch = true;

		/** PreparedStatement実行 */
		private boolean executePrepared = true;

		/** Statement実行 */
		private boolean executeStatement = true;

		/** コネクションオープン */
		private boolean open = true;

		/** PreparedStatement生成 */
		private boolean prepareStatement = true;

		/** Statementクローズ */
		private boolean closeStatement = true;

		/** ロールバック */
		private boolean rollback = true;

		public boolean isAddBatch() {
			return addBatch;
		}

		public void setAddBatch(boolean addBatch) {
			this.addBatch = addBatch;
		}

		public boolean isClose() {
			return close;
		}

		public void setClose(boolean close) {
			this.close = close;
		}

		public boolean isCommit() {
			return commit;
		}

		public void setCommit(boolean commit) {
			this.commit = commit;
		}

		public boolean isExecuteBatch() {
			return executeBatch;
		}

		public void setExecuteBatch(boolean executeBatch) {
			this.executeBatch = executeBatch;
		}

		public boolean isExecutePrepared() {
			return executePrepared;
		}

		public void setExecutePrepared(boolean executePrepared) {
			this.executePrepared = executePrepared;
		}

		public boolean isExecuteStatement() {
			return executeStatement;
		}

		public void setExecuteStatement(boolean executeStatement) {
			this.executeStatement = executeStatement;
		}

		public boolean isCloseStatement() {
			return closeStatement;
		}

		public void setCloseStatement(boolean closeStatement) {
			this.closeStatement = closeStatement;
		}

		public boolean isOpen() {
			return open;
		}

		public void setOpen(boolean open) {
			this.open = open;
		}

		public boolean isPrepareStatement() {
			return prepareStatement;
		}

		public void setPrepareStatement(boolean prepareStatement) {
			this.prepareStatement = prepareStatement;
		}

		public boolean isRollback() {
			return rollback;
		}

		public void setRollback(boolean rollback) {
			this.rollback = rollback;
		}

		public boolean isVisible(net.mikaboshi.jdbc.monitor.LogType logType) {
			if (logType == net.mikaboshi.jdbc.monitor.LogType.ADD_BATCH) {
				return this.isAddBatch();
			}
			if (logType == net.mikaboshi.jdbc.monitor.LogType.COMMIT) {
				return this.isCommit();
			}
			if (logType == net.mikaboshi.jdbc.monitor.LogType.CONN_CLOSE) {
				return this.isClose();
			}
			if (logType == net.mikaboshi.jdbc.monitor.LogType.CONN_OPEN) {
				return this.isOpen();
			}
			if (logType == net.mikaboshi.jdbc.monitor.LogType.EXE_BATCH) {
				return this.isExecuteBatch();
			}
			if (logType == net.mikaboshi.jdbc.monitor.LogType.EXE_PSTMT) {
				return this.isExecutePrepared();
			}
			if (logType == net.mikaboshi.jdbc.monitor.LogType.EXE_STMT) {
				return this.isExecuteStatement();
			}
			if (logType == net.mikaboshi.jdbc.monitor.LogType.PREPARE_STMT) {
				return this.isPrepareStatement();
			}
			if (logType == net.mikaboshi.jdbc.monitor.LogType.CLOSE_STMT) {
				return this.isCloseStatement();
			}
			if (logType == net.mikaboshi.jdbc.monitor.LogType.ROLLBACK) {
				return this.isRollback();
			}

			return false;
		}

		@Override
		public String toString() {
			return ToStringBuilder.reflectionToString(this);
		}
	}

	/**
	 * JdbcLogViewerFrameの設定
	 */
	public static class Frame {

		/** 最大化 */
		private boolean maximized = false;

		/** 高さ */
		private int height = 380;

		/** 横幅 */
		private int width = 650;

		/**
		 * スプリットペインの分割位置
		 */
		private int dividerLocation = 190;

		/** ステータスバーの表示 */
		private boolean statusBar = true;

		public boolean isMaximized() {
			return maximized;
		}

		public void setMaximized(boolean maximized) {
			this.maximized = maximized;
		}

		public int getHeight() {
			return height;
		}

		public void setHeight(int height) {
			this.height = height;
		}

		public int getWidth() {
			return width;
		}

		public void setWidth(int width) {
			this.width = width;
		}

		@XmlTransient
		public Dimension getDimension() {
			return new Dimension(this.width, this.height);
		}

		public void setDimension(Dimension dimension) {
			this.width = dimension.width;
			this.height = dimension.height;
		}

		public boolean isStatusBar() {
			return statusBar;
		}

		public void setStatusBar(boolean statusBar) {
			this.statusBar = statusBar;
		}

		public void setDividerLocation(int dividerLocation) {
			this.dividerLocation = dividerLocation;
		}

		public int getDividerLocation() {
			return dividerLocation;
		}

		@Override
		public String toString() {
			return ToStringBuilder.reflectionToString(this);
		}
	}

	/**
	 * ログファイルに関する設定
	 */
	public static class LogFile {

		private String charSet = Charset.defaultCharset().name();

		private String path = null;

		private long readInterval = 100L;

		public String getCharSet() {
			return charSet;
		}

		public void setCharSet(String charSet) {
			this.charSet = charSet;
		}

		public String getPath() {
			return path;
		}

		public void setPath(String path) {
			this.path = path;
		}

		public long getReadInterval() {
			return readInterval;
		}

		public void setReadInterval(long readInterval) {
			this.readInterval = readInterval;
		}

		@Override
		public String toString() {
			return ToStringBuilder.reflectionToString(this);
		}
	}

	/**
	 * ログテーブルに関する設定
	 */
	public static class LogTable {

		/** 自動スクロースを行うかどうか */
		private boolean autoScroll = true;

		/** 更新件数列を表示するかどうか */
		private boolean affectedRows = true;

		/** コネクションID列を表示するかどうか */
		private boolean connectionId = true;

		/** 経過時間列を表示するかどうか */
		private boolean elapsedTime = true;

		/** ログ種別列を表示するかどうか */
		private boolean logType = true;

		/** 結果列を表示するかどうか */
		private boolean result = false;

		/** オートコミット列を表示するかどうか */
		private boolean autoCommit = false;

		/** SQL列を表示するかどうか */
		private boolean sql = true;

		/** ステートメントID列を表示するかどうか */
		private boolean statementId = true;

		/** スレッド名列を表示するかどうか */
		private boolean threadName = true;

		/** タイムスタンプ列を表示するかどうか */
		private boolean timestamp = true;

		/** タグ列を表示するかどうか */
		private boolean tag = true;

		public boolean isAutoScroll() {
			return autoScroll;
		}

		public void setAutoScroll(boolean autoScroll) {
			this.autoScroll = autoScroll;
		}

		public boolean isAffectedRows() {
			return affectedRows;
		}

		public void setAffectedRows(boolean affectedRows) {
			this.affectedRows = affectedRows;
		}

		public boolean isConnectionId() {
			return connectionId;
		}

		public void setConnectionId(boolean connectionId) {
			this.connectionId = connectionId;
		}

		public boolean isElapsedTime() {
			return elapsedTime;
		}

		public void setElapsedTime(boolean elapsedTime) {
			this.elapsedTime = elapsedTime;
		}

		public boolean isLogType() {
			return logType;
		}

		public void setLogType(boolean logType) {
			this.logType = logType;
		}

		public boolean isResult() {
			return result;
		}

		public void setResult(boolean result) {
			this.result = result;
		}

		public boolean isAutoCommit() {
			return autoCommit;
		}

		public void setAutoCommit(boolean autoCommit) {
			this.autoCommit = autoCommit;
		}

		public boolean isSql() {
			return sql;
		}

		public void setSql(boolean sql) {
			this.sql = sql;
		}

		public boolean isStatementId() {
			return statementId;
		}

		public void setStatementId(boolean statementId) {
			this.statementId = statementId;
		}

		public boolean isThreadName() {
			return threadName;
		}

		public void setThreadName(boolean threadName) {
			this.threadName = threadName;
		}

		public boolean isTimestamp() {
			return timestamp;
		}

		public void setTimestamp(boolean timestamp) {
			this.timestamp = timestamp;
		}

		public void setTag(boolean tag) {
			this.tag = tag;
		}

		public boolean isTag() {
			return tag;
		}

		@Override
		public String toString() {
			return ToStringBuilder.reflectionToString(this);
		}
	}

	/**
	 * DB接続情報
	 */
	public static class ConnectInfo {

		private String driver = StringUtils.EMPTY;

		private String driverVersion = StringUtils.EMPTY;

		private String url = StringUtils.EMPTY;

		private String user = StringUtils.EMPTY;

		private String password = StringUtils.EMPTY;

		public String getDriver() {
			return driver;
		}

		public void setDriver(String driver) {
			this.driver = driver;
		}

		public String getDriverVersion() {
			return driverVersion;
		}

		public void setDriverVersion(String driverVersion) {
			this.driverVersion = driverVersion;
		}

		public String getUrl() {
			return url;
		}

		public void setUrl(String url) {
			this.url = url;
		}

		public String getUser() {
			return user;
		}

		public void setUser(String user) {
			this.user = user;
		}

		public String getPassword() {
			return password;
		}

		public void setPassword(String password) {
			this.password = password;
		}

		@Override
		public String toString() {
			return ToStringBuilder.reflectionToString(this);
		}
	}

	/**
	 * ログテーブル検索ダイアログの設定
	 * @since 1.4.2
	 */
	public static class LogTableSearch {

		/** 検索ワード */
		private List<String> searchWord = new ArrayList<String>();

		/** 正規表現 */
		private boolean regularExpression = false;

		/** 循環検索 */
		private boolean circulating = false;

		/** 大文字/小文字を区別する */
		private boolean caseSensitive = false;

		public List<String> getSearchWord() {
			return searchWord;
		}

		public void setSearchWord(List<String> searchWord) {
			this.searchWord = searchWord;
		}

		public boolean isRegularExpression() {
			return regularExpression;
		}

		public void setRegularExpression(boolean regularExpression) {
			this.regularExpression = regularExpression;
		}

		public boolean isCirculating() {
			return circulating;
		}

		public void setCirculating(boolean circulating) {
			this.circulating = circulating;
		}

		public boolean isCaseSensitive() {
			return caseSensitive;
		}

		public void setCaseSensitive(boolean caseSensitive) {
			this.caseSensitive = caseSensitive;
		}

		@Override
		public String toString() {
			return ToStringBuilder.reflectionToString(this);
		}
	}

}
