package net.mikaboshi.jdbc.monitor;

import java.lang.management.ManagementFactory;

import javax.management.MBeanServer;
import javax.management.ObjectName;

import net.mikaboshi.validator.SimpleValidator;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * <p>ログ取得モード。</p>
 * 
 * @author Takuma Umezawa
 */
public class LogMode implements LogModeMBean {
	
	private static Log systemLogger = LogFactory.getLog(LogMode.class);

	private LogMode() {
		
		// JMXの登録
		MBeanServer mBeanServer = ManagementFactory.getPlatformMBeanServer();
		try {
			mBeanServer.registerMBean(this, new ObjectName(JMX_OBJECT_NAME));
		} catch (Exception e) {
			systemLogger.error("Registering JMX failed.", e);
		}		
	}
	
	private static LogMode instance = new LogMode();
	
	public static LogMode getInstance() {
		return instance;
	}
	
	// 以下、loggableのgetter/setter
	
	/** バッチ追加のログを出力するか */
	private Boolean addBatchLoggable = true;
	
	/** コミットのログを出力するか */
	private Boolean commitLoggable = true;
	
	/** コネクションクローズのログを出力するか */
	private Boolean connectionCloseLoggable = true;
	
	/** コネクションオープンのログを出力するか */
	private Boolean connectionOpenLoggable = true;
	
	/** バッチ実行のログを出力するか */
	private Boolean executeBatchLoggable = true;
	
	/** PreparedStatement実行のログを出力するか */
	private Boolean executePreparedStatementLoggable = true;
	
	/** Statement実行のログを出力するか */
	private Boolean executeStatementLoggable = true;
	
	/** PreparedStatement実行のログを出力するか */
	private Boolean prepareStatementLoggable = true;
	
	/** ロールバックのログを出力するか */
	private Boolean rollbackLoggable = true;
	
	/** Statement, PreparedStatementのクローズをログに出力するか */
	private Boolean closeStatementLoggable = true;
	
	/** コールスタックレベル */
	private Integer callStackLevel = 0;

	/**
	 * バッチ追加のログを出力するかを取得します。
	 * @return バッチ追加のログを出力するか
	 */
	public Boolean getAddBatchLoggable() {
	    return addBatchLoggable;
	}

	/**
	 * バッチ追加のログを出力するかを設定します。
	 * @param addBatchLoggable バッチ追加のログを出力するか
	 */
	public void setAddBatchLoggable(Boolean addBatchLoggable) {
	    this.addBatchLoggable = addBatchLoggable;
	}

	/**
	 * コミットのログを出力するかを取得します。
	 * @return コミットのログを出力するか
	 */
	public Boolean getCommitLoggable() {
	    return commitLoggable;
	}

	/**
	 * コミットのログを出力するかを設定します。
	 * @param commitLoggable コミットのログを出力するか
	 */
	public void setCommitLoggable(Boolean commitLoggable) {
	    this.commitLoggable = commitLoggable;
	}

	/**
	 * コネクションクローズのログを出力するかを取得します。
	 * @return コネクションクローズのログを出力するか
	 */
	public Boolean getConnectionCloseLoggable() {
	    return connectionCloseLoggable;
	}

	/**
	 * コネクションクローズのログを出力するかを設定します。
	 * @param connectionCloseLoggable コネクションクローズのログを出力するか
	 */
	public void setConnectionCloseLoggable(Boolean connectionCloseLoggable) {
	    this.connectionCloseLoggable = connectionCloseLoggable;
	}

	/**
	 * コネクションオープンのログを出力するかを取得します。
	 * @return コネクションオープンのログを出力するか
	 */
	public Boolean getConnectionOpenLoggable() {
	    return connectionOpenLoggable;
	}

	/**
	 * コネクションオープンのログを出力するかを設定します。
	 * @param connectionOpenLoggable コネクションオープンのログを出力するか
	 */
	public void setConnectionOpenLoggable(Boolean connectionOpenLoggable) {
	    this.connectionOpenLoggable = connectionOpenLoggable;
	}

	/**
	 * バッチ実行のログを出力するかを取得します。
	 * @return バッチ実行のログを出力するか
	 */
	public Boolean getExecuteBatchLoggable() {
	    return executeBatchLoggable;
	}

	/**
	 * バッチ実行のログを出力するかを設定します。
	 * @param executeBatchLoggable バッチ実行のログを出力するか
	 */
	public void setExecuteBatchLoggable(Boolean executeBatchLoggable) {
	    this.executeBatchLoggable = executeBatchLoggable;
	}

	/**
	 * PreparedStatement実行のログを出力するかを取得します。
	 * @return PreparedStatement実行のログを出力するか
	 */
	public Boolean getExecutePreparedStatementLoggable() {
	    return executePreparedStatementLoggable;
	}

	/**
	 * PreparedStatement実行のログを出力するかを設定します。
	 * @param executePreparedStatementLoggable PreparedStatement実行のログを出力するか
	 */
	public void setExecutePreparedStatementLoggable(Boolean executePreparedStatementLoggable) {
	    this.executePreparedStatementLoggable = executePreparedStatementLoggable;
	}

	/**
	 * Statement実行のログを出力するかを取得します。
	 * @return Statement実行のログを出力するか
	 */
	public Boolean getExecuteStatementLoggable() {
	    return executeStatementLoggable;
	}

	/**
	 * Statement実行のログを出力するかを設定します。
	 * @param executeStatementLoggable Statement実行のログを出力するか
	 */
	public void setExecuteStatementLoggable(Boolean executeStatementLoggable) {
	    this.executeStatementLoggable = executeStatementLoggable;
	}

	/**
	 * PreparedStatement実行のログを出力するかを取得します。
	 * @return PreparedStatement実行のログを出力するか
	 */
	public Boolean getPrepareStatementLoggable() {
	    return prepareStatementLoggable;
	}

	/**
	 * PreparedStatement実行のログを出力するかを設定します。
	 * @param prepareStatementLoggable PreparedStatement実行のログを出力するか
	 */
	public void setPrepareStatementLoggable(Boolean prepareStatementLoggable) {
	    this.prepareStatementLoggable = prepareStatementLoggable;
	}

	/**
	 * ロールバックのログを出力するかを取得します。
	 * @return ロールバックのログを出力するか
	 */
	public Boolean getRollbackLoggable() {
	    return rollbackLoggable;
	}

	/**
	 * ロールバックのログを出力するかを設定します。
	 * @param rollbackLoggable ロールバックのログを出力するか
	 */
	public void setRollbackLoggable(Boolean rollbackLoggable) {
	    this.rollbackLoggable = rollbackLoggable;
	}
	
	/**
	 * コールスタックレベルを取得します。
	 * @return
	 */
	public Integer getCallStackLevel() {
		return callStackLevel;
	}
	
	/**
	 * コールスタックレベルを設定します。
	 * @param callStackLevel
	 */
	public void setCallStackLevel(Integer callStackLevel) {
		SimpleValidator.validatePositiveOrZero(
				callStackLevel, 
				"callStackLevel", 
				IllegalArgumentException.class);
		
		this.callStackLevel = callStackLevel;
	}
	
	/**
	 * Statement, PreparedStatementのクローズをログに出力するかを設定します。
	 * @param closeStatementLoggable
	 * @since 1.4.0
	 */
	public void setCloseStatementLoggable(Boolean closeStatementLoggable) {
		this.closeStatementLoggable = closeStatementLoggable;
	}
	
	/**
	 * Statement, PreparedStatementのクローズをログに出力するかを取得します。
	 * @return
	 * @since 1.4.0
	 */
	public Boolean getCloseStatementLoggable() {
		return closeStatementLoggable;
	}
}
