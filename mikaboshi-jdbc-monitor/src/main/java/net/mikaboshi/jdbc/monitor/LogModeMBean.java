package net.mikaboshi.jdbc.monitor;

/**
 * 種別毎にログを取得するかどうかの設定。
 * 
 * @author Takuma Umezawa
 *
 */
public interface LogModeMBean {
	
	public static final String JMX_OBJECT_NAME = "net.mikaboshi.jdbc.monitor:type=LogMode";

	/**
	 * バッチ追加のログを出力するかを取得します。
	 * @return バッチ追加のログを出力するか
	 */
	public Boolean getAddBatchLoggable();

	/**
	 * バッチ追加のログを出力するかを設定します。
	 * @param addBatchLoggable バッチ追加のログを出力するか
	 */
	public void setAddBatchLoggable(Boolean addBatchLoggable);

	/**
	 * コミットのログを出力するかを取得します。
	 * @return コミットのログを出力するか
	 */
	public Boolean getCommitLoggable();

	/**
	 * コミットのログを出力するかを設定します。
	 * @param commitLoggable コミットのログを出力するか
	 */
	public void setCommitLoggable(Boolean commitLoggable);

	/**
	 * コネクションクローズのログを出力するかを取得します。
	 * @return コネクションクローズのログを出力するか
	 */
	public Boolean getConnectionCloseLoggable();

	/**
	 * コネクションクローズのログを出力するかを設定します。
	 * @param connectionCloseLoggable コネクションクローズのログを出力するか
	 */
	public void setConnectionCloseLoggable(Boolean connectionCloseLoggable);

	/**
	 * コネクションオープンのログを出力するかを取得します。
	 * @return コネクションオープンのログを出力するか
	 */
	public Boolean getConnectionOpenLoggable();

	/**
	 * コネクションオープンのログを出力するかを設定します。
	 * @param connectionOpenLoggable コネクションオープンのログを出力するか
	 */
	public void setConnectionOpenLoggable(Boolean connectionOpenLoggable);

	/**
	 * バッチ実行のログを出力するかを取得します。
	 * @return バッチ実行のログを出力するか
	 */
	public Boolean getExecuteBatchLoggable();

	/**
	 * バッチ実行のログを出力するかを設定します。
	 * @param executeBatchLoggable バッチ実行のログを出力するか
	 */
	public void setExecuteBatchLoggable(Boolean executeBatchLoggable);

	/**
	 * PreparedStatement実行のログを出力するかを取得します。
	 * @return PreparedStatement実行のログを出力するか
	 */
	public Boolean getExecutePreparedStatementLoggable();

	/**
	 * PreparedStatement実行のログを出力するかを設定します。
	 * @param executePreparedStatementLoggable PreparedStatement実行のログを出力するか
	 */
	public void setExecutePreparedStatementLoggable(Boolean executePreparedStatementLoggable);

	/**
	 * Statement実行のログを出力するかを取得します。
	 * @return Statement実行のログを出力するか
	 */
	public Boolean getExecuteStatementLoggable();

	/**
	 * Statement実行のログを出力するかを設定します。
	 * @param executeStatementLoggable Statement実行のログを出力するか
	 */
	public void setExecuteStatementLoggable(Boolean executeStatementLoggable);

	/**
	 * PreparedStatement実行のログを出力するかを取得します。
	 * @return PreparedStatement実行のログを出力するか
	 */
	public Boolean getPrepareStatementLoggable();

	/**
	 * PreparedStatement実行のログを出力するかを設定します。
	 * @param prepareStatementLoggable PreparedStatement実行のログを出力するか
	 */
	public void setPrepareStatementLoggable(Boolean prepareStatementLoggable);

	/**
	 * ロールバックのログを出力するかを取得します。
	 * @return ロールバックのログを出力するか
	 */
	public Boolean getRollbackLoggable();

	/**
	 * ロールバックのログを出力するかを設定します。
	 * @param rollbackLoggable ロールバックのログを出力するか
	 */
	public void setRollbackLoggable(Boolean rollbackLoggable);
	
	/**
	 * コールスタックレベルを取得します。
	 * @return
	 */
	public Integer getCallStackLevel();
	
	/**
	 * コールスタックレベルを設定します。
	 */
	public void setCallStackLevel(Integer callStackLevel);
	
	/**
	 * Statement, PreparedStatementのクローズをログに出力するかを設定します。
	 * @param closeStatementLoggable
	 * @since 1.4.0
	 */
	public void setCloseStatementLoggable(Boolean closeStatementLoggable);
	
	/**
	 * Statement, PreparedStatementのクローズをログに出力するかを取得します。
	 * @return
	 * @since 1.4.0
	 */
	public Boolean getCloseStatementLoggable();
}
