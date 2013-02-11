package net.mikaboshi.jdbc.monitor.viewer;

import static net.mikaboshi.jdbc.monitor.viewer.LogTableColumn.*;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.swing.table.AbstractTableModel;

import org.apache.commons.lang.StringUtils;

import net.mikaboshi.jdbc.monitor.LogEntry;
import net.mikaboshi.jdbc.monitor.LogType;
import net.mikaboshi.jdbc.monitor.M17N;
import net.mikaboshi.jdbc.monitor.Result;
import net.mikaboshi.jdbc.monitor.SqlUtils;
import net.mikaboshi.jdbc.monitor.ViewerConfig;

public class LogTableModel extends AbstractTableModel {

	private static final long serialVersionUID = 1L;

	public LogTableModel() {
		super();
	}

	public synchronized int getColumnCount() {
		if (this.visibleColumnNames == null) {
			refreshVisibleColumns();
		}

		return this.visibleColumnNames.length;
	}

	public synchronized int getRowCount() {
		return this.visibleLogList.size();
	}

	public synchronized Object getValueAt(int rowIndex, int columnIndex) {

		LogEntry entry = getLogEntry(rowIndex);
		int realColumnIndex = getRealColumnIndex(columnIndex);

		if (realColumnIndex == TIMESTAMP.getColumnIndex()) {
			return entry.getTime();
		}

		if (realColumnIndex == LOG_TYPE.getColumnIndex()) {
			return M17N.get(entry.getLogType().propertyName());
		}

		if (realColumnIndex == THREAD_NAME.getColumnIndex()) {
			return entry.getThreadName();
		}

		if (realColumnIndex == CONNECTION_ID.getColumnIndex()) {
			return entry.getConnectionId();
		}

		if (realColumnIndex == STATEMENT_ID.getColumnIndex()) {
			return entry.getStatementId();
		}

		if (realColumnIndex == SQL.getColumnIndex()) {
			return entry.getSql() == null
					? null
					: SqlUtils.linize(entry.getSql());
		}

		if (realColumnIndex == AFFECTED_ROWS.getColumnIndex()) {
			return entry.getAffectedRows();
		}

		if (realColumnIndex == ELAPSED_TIME.getColumnIndex()) {
			if (entry.getElapsedTime() == null) {
				return null;
			}

			// ミリ秒単位で表示する
			return entry.getElapsedTime() / 1000000;
		}

		if (realColumnIndex == RESULT.getColumnIndex()) {
			if (entry.getResult() == Result.SUCCESS) {
				return M17N.get("result.success");
			} else {
				return M17N.get("result.failure");
			}
		}

		if (realColumnIndex == AUTO_COMMIT.getColumnIndex()) {
			if (entry.getAutoCommit() == null) {
				return null;
			} else {
				return entry.getAutoCommit().toString();
			}
		}

		if (realColumnIndex == TAG.getColumnIndex()) {
			return entry.getTag();
		}

		throw new IllegalArgumentException("Invalid realColumnIndex : " + realColumnIndex);
	}

	@Override
	public synchronized String getColumnName(int column) {
		if (this.visibleColumnNames == null) {
			refreshVisibleColumns();
		}

		return this.visibleColumnNames[column];
	}

	/**
	 * 指定された行（表示されている行番号）より前の行を削除する。
	 * @param row
	 */
	public synchronized void clearBefore(int row) {
		LogEntry thisRow = getLogEntry(row);

		while ( !this.allLogList.isEmpty() &&
				this.allLogList.get(0) != thisRow ) {
			this.allLogList.remove(0);
		}

		while ( !this.visibleLogList.isEmpty() &&
				this.visibleLogList.get(0) != thisRow ) {
			this.visibleLogList.remove(0);
		}

		fireTableDataChanged();
	}

	/**
	 * 指定された行（表示されている行番号）より後の行を削除する。
	 * @param row
	 */
	public synchronized void clearAfter(int row) {
		LogEntry thisRow = getLogEntry(row);

		while ( !this.allLogList.isEmpty() &&
				this.allLogList.get(this.allLogList.size() - 1) != thisRow ) {
			this.allLogList.remove(this.allLogList.size() - 1);
		}

		while ( !this.visibleLogList.isEmpty() &&
				this.visibleLogList.get(this.visibleLogList.size() - 1) != thisRow ) {
			this.visibleLogList.remove(this.visibleLogList.size() - 1);
		}

		fireTableDataChanged();
	}

	/**
	 * 選択された行を削除する。
	 * @param selectedRows
	 */
	public synchronized void clearSelectedRows(int[] selectedRows) {
		if (selectedRows.length == 0) {
			return;
		}

		for (int row : selectedRows) {
			LogEntry e = this.visibleLogList.get(row);
			this.allLogList.remove(e);
			this.visibleLogList.remove(e);
		}

		fireTableDataChanged();
	}

	/**
	 * 選択された行以外を削除する。
	 * @param selectedRows
	 */
	public synchronized void clearExceptSelectedRows(int[] selectedRows) {
		if (selectedRows.length == 0) {
			return;
		}

		List<LogEntry> newAllList = new ArrayList<LogEntry>();
		List<LogEntry> newVisibleList = new ArrayList<LogEntry>();

		for (int row : selectedRows) {
			LogEntry e = this.visibleLogList.get(row);
			newAllList.add(e);
			newVisibleList.add(e);
		}

		this.allLogList = newAllList;
		this.visibleLogList = newVisibleList;

		fireTableDataChanged();
	}

	/**
	 * クローズされていないStatement、PreparedStatementを検出する。
	 * @since 1.4.0
	 */
	public synchronized void detectUnclosedStatement() {

		// まず、closeされたStatementのIDを取得する。
		// ※ 経過時間でソートされている場合があるので、allLogListは実行時順とは限らない
		Set<String> closedStatementId = new HashSet<String>();

		for (LogEntry logEntry : this.allLogList) {
			if (logEntry.getLogType() == LogType.CLOSE_STMT) {
				closedStatementId.add(logEntry.getStatementId());
			}
		}

		// closeされていないStatementのみ抽出する
		List<LogEntry> newAllList = new ArrayList<LogEntry>();
		List<LogEntry> newVisibleList = new ArrayList<LogEntry>();

		for (LogEntry logEntry : this.allLogList) {

			String statementId = logEntry.getStatementId();

			if (StringUtils.isEmpty(statementId) ||
				closedStatementId.contains(statementId)) {
				continue;
			}

			newAllList.add(logEntry);

			if (logEntry.isVisible()) {
				newVisibleList.add(logEntry);
			}
		}

		this.allLogList = newAllList;
		this.visibleLogList = newVisibleList;

		fireTableDataChanged();
	}

	/**
	 * クローズされていないコネクションに関するログイベントを検出する。
	 * @since 1.4.0
	 */
	public synchronized void detectUnclosedConnection() {

		// まず、closeされたConnectionのIDを取得する。
		// ※ 経過時間でソートされている場合があるので、allLogListは実行時順とは限らない
		Set<String> closedConnectionId = new HashSet<String>();

		for (LogEntry logEntry : this.allLogList) {
			if (logEntry.getLogType() == LogType.CONN_CLOSE) {
				closedConnectionId.add(logEntry.getConnectionId());
			}
		}

		// closeされていないStatementのみ抽出する
		List<LogEntry> newAllList = new ArrayList<LogEntry>();
		List<LogEntry> newVisibleList = new ArrayList<LogEntry>();

		for (LogEntry logEntry : this.allLogList) {

			String connectionId = logEntry.getConnectionId();

			if (StringUtils.isEmpty(connectionId) ||
					closedConnectionId.contains(connectionId)) {
				continue;
			}

			newAllList.add(logEntry);

			if (logEntry.isVisible()) {
				newVisibleList.add(logEntry);
			}
		}

		this.allLogList = newAllList;
		this.visibleLogList = newVisibleList;

		fireTableDataChanged();
	}


	/**
	 * 全てのログのリスト（フィルターされたものも含む）
	 */
	private List<LogEntry> allLogList = new ArrayList<LogEntry>();

	/**
	 * 表示されている行のみのリスト
	 */
	private List<LogEntry> visibleLogList = new ArrayList<LogEntry>();

	protected List<LogEntry> getVisibleLogList() {
		return this.visibleLogList;
	}

	/**
	 * テーブルデータを空にする。
	 */
	public synchronized void reset() {
		this.allLogList = new ArrayList<LogEntry>();
		this.visibleLogList = new ArrayList<LogEntry>();
		this.threadNameSet = new HashSet<String>();
		this.connectionIdSet = new HashSet<String>();
		this.statementIdSet = new HashSet<String>();
		this.tagSet = new HashSet<String>();
		fireTableDataChanged();
	}

	private LogTableFilter filter;

	/**
	 * フィルターを変更する。
	 * @param filter
	 */
	public synchronized void updateFilter(LogTableFilter filter) {
		this.filter = filter;

		this.visibleLogList = new ArrayList<LogEntry>();

		for (LogEntry entry : this.allLogList) {
			entry.setVisible(this.filter.isRowVisible(entry));

			if (entry.isVisible()) {
				this.visibleLogList.add(entry);
			}
		}

		fireTableDataChanged();
	}

	protected synchronized int getRealColumnIndex(int columnIndex) {

		if (this.columnIndexMap.get(columnIndex) == null) {
			throw new IllegalArgumentException("Invalid columnIndex : " + columnIndex);
		}

		return this.columnIndexMap.get(columnIndex).getColumnIndex();
	}

	/**
	 * 現在表示されている列のインデックスを取得する。
	 * 表示されていない場合は-1を返す。
	 * @param logTableColumn
	 * @return
	 */
	protected synchronized int getVisibleColumnIndex(LogTableColumn logTableColumn) {

		for (Map.Entry<Integer, LogTableColumn> entry : this.columnIndexMap.entrySet()) {
			if (entry.getValue() == logTableColumn) {
				return entry.getKey();
			}
		}

		return -1;
	}

	/** 存在するスレッド名 */
	private Set<String> threadNameSet = new HashSet<String>();

	public Set<String> getThreadNameSet() {
		return this.threadNameSet;
	}

	/** 存在するコネクションID */
	private Set<String> connectionIdSet = new HashSet<String>();

	public Set<String> getConnectionIdSet() {
		return this.connectionIdSet;
	}

	/** 存在するステートメントID */
	private Set<String> statementIdSet = new HashSet<String>();

	public Set<String> getStatementIdSet() {
		return this.statementIdSet;
	}

	/** 存在するタグ */
	private Set<String> tagSet = new HashSet<String>();

	public Set<String> getTagSet() {
		return this.tagSet;
	}

	/**
	 * テーブルの末尾に行を追加する。
	 *
	 * @param entry
	 */
	public synchronized void addLogEntry(LogEntry entry) {
		this.allLogList.add(entry);

		if (entry.getThreadName() != null) {
			this.threadNameSet.add(entry.getThreadName());
		}

		if (entry.getConnectionId() != null) {
			this.connectionIdSet.add(entry.getConnectionId());
		}

		if (entry.getStatementId() != null) {
			this.statementIdSet.add(entry.getStatementId());
		}

		if (entry.getTag() != null) {
			this.tagSet.add(entry.getTag());
		}

		if (this.filter != null) {
			entry.setVisible(this.filter.isRowVisible(entry));
		}

		if (entry.isVisible()) {
			this.visibleLogList.add(entry);
		}
	}

	/**
	 * テーブルの末尾に行を追加し、表示を更新する。
	 *
	 * @param entry
	 */
	public synchronized void addLogEntryWithFireUpdate(LogEntry entry) {
		addLogEntry(entry);
		int last = getRowCount() - 1;
		fireTableRowsInserted(last, last);
	}

	/**
	 * 指定行のログエントリオブジェクトを取得する。
	 *
	 * @param row
	 * @return
	 */
	public synchronized LogEntry getLogEntry(int row) {

		if (row < 0 || row >= this.visibleLogList.size()) {
			return null;
		}

		return this.visibleLogList.get(row);
	}

	/**
	 * 表示される列名
	 */
	private String[] visibleColumnNames;

	/**
	 * 表示される列の幅
	 */
	private Integer[] preferredWidth;

	/**
	 * 表示される列の幅を取得する
	 * @return
	 */
	public synchronized Integer[] getPreferredWidth() {
		if (this.preferredWidth == null) {
			refreshVisibleColumns();
		}

		return this.preferredWidth;
	}

	/**
	 * 表示されるテーブルの列番号から、ログエントリの順序番号を照会するためのマップ
	 */
	private Map<Integer, LogTableColumn> columnIndexMap
			= new HashMap<Integer, LogTableColumn>();

	/**
	 * 表示される列の情報を更新する
	 */
	protected synchronized void refreshVisibleColumns() {

		List<String> columnNameList = new ArrayList<String>();
		List<Integer> preferredWidthList = new ArrayList<Integer>();
		this.columnIndexMap = new HashMap<Integer, LogTableColumn>();

		ViewerConfig.LogTable logTable = ViewerConfig.getInstance().getLogTable();

		if (logTable.isTimestamp()) {
			columnNameList.add(M17N.get("JdbcLogViewerFrame.column.time"));
			preferredWidthList.add(14);
			this.columnIndexMap.put(0, TIMESTAMP);
		}

		if (logTable.isLogType()) {
			columnNameList.add(M17N.get("JdbcLogViewerFrame.column.log_type"));
			preferredWidthList.add(14);
			this.columnIndexMap.put(this.columnIndexMap.size(), LOG_TYPE);
		}

		if (logTable.isThreadName()) {
			columnNameList.add(M17N.get("JdbcLogViewerFrame.column.thread_name"));
			preferredWidthList.add(14);
			this.columnIndexMap.put(this.columnIndexMap.size(), THREAD_NAME);
		}

		if (logTable.isConnectionId()) {
			columnNameList.add(M17N.get("JdbcLogViewerFrame.column.connection_id"));
			preferredWidthList.add(14);
			this.columnIndexMap.put(this.columnIndexMap.size(), CONNECTION_ID);
		}

		if (logTable.isStatementId()) {
			columnNameList.add(M17N.get("JdbcLogViewerFrame.column.statement_id"));
			preferredWidthList.add(14);
			this.columnIndexMap.put(this.columnIndexMap.size(), STATEMENT_ID);
		}

		if (logTable.isSql()) {
			columnNameList.add(M17N.get("JdbcLogViewerFrame.column.sql"));
			preferredWidthList.add(180);
			this.columnIndexMap.put(this.columnIndexMap.size(), SQL);
		}

		if (logTable.isAffectedRows()) {
			columnNameList.add(M17N.get("JdbcLogViewerFrame.column.affected_rows"));
			preferredWidthList.add(16);
			this.columnIndexMap.put(this.columnIndexMap.size(), AFFECTED_ROWS);
		}

		if (logTable.isElapsedTime()) {
			columnNameList.add(M17N.get("JdbcLogViewerFrame.column.elapsed_time"));
			preferredWidthList.add(16);
			this.columnIndexMap.put(this.columnIndexMap.size(), ELAPSED_TIME);
		}

		if (logTable.isResult()) {
			columnNameList.add(M17N.get("JdbcLogViewerFrame.column.result"));
			preferredWidthList.add(8);
			this.columnIndexMap.put(this.columnIndexMap.size(), RESULT);
		}

		if (logTable.isAutoCommit()) {
			columnNameList.add(M17N.get("JdbcLogViewerFrame.column.auto_commit"));
			preferredWidthList.add(8);
			this.columnIndexMap.put(this.columnIndexMap.size(), AUTO_COMMIT);
		}

		if (logTable.isTag()) {
			columnNameList.add(M17N.get("JdbcLogViewerFrame.column.tag"));
			preferredWidthList.add(12);
			this.columnIndexMap.put(this.columnIndexMap.size(), TAG);
		}

		int size = columnNameList.size();
		this.visibleColumnNames = columnNameList.toArray(new String[size]);
		this.preferredWidth = preferredWidthList.toArray(new Integer[size]);
	}

	/**
	 * 経過時間の降順に並び替える
	 */
	public synchronized void orderByElapsedTimeDesc() {
		Collections.sort(this.allLogList, LogEntryElapsedTimeDescComparator.instance);
		Collections.sort(this.visibleLogList, LogEntryElapsedTimeDescComparator.instance);
		fireTableDataChanged();
	}

	/**
	 * 最後に追加されたログの時刻を取得する。
	 * @return
	 */
	public synchronized String getLastLogTime() {
		if (this.allLogList == null || this.allLogList.size() == 0) {
			return null;
		}

		LogEntry entry = this.allLogList.get(this.allLogList.size() - 1);
		return entry.getDate() + " " + entry.getTime();
	}

	/**
	 * 全ての行（フィルターで非表示になっている行も含む）の数を取得する。
	 * @return
	 */
	public synchronized int getAllRowsCount() {
		if (this.allLogList == null) {
			return 0;
		}
		return this.allLogList.size();
	}

	/**
	 * LogEntryをelapsedTimeの降順に並び替える
	 */
	private static class LogEntryElapsedTimeDescComparator implements Comparator<LogEntry> {

		private static LogEntryElapsedTimeDescComparator instance
			= new LogEntryElapsedTimeDescComparator();

		public int compare(LogEntry o1, LogEntry o2) {
			// nullは後ろ
			if (o2.getElapsedTime() == null) {
				return -1;
			}
			if (o1.getElapsedTime() == null) {
				return 1;
			}

			// 値が大きい方が先
			if (o1.getElapsedTime() > o2.getElapsedTime()) {
				return -1;
			} else if (o1.getElapsedTime() < o2.getElapsedTime()) {
				return 1;
			} else {
				return 0;
			}
		}

	}

}
