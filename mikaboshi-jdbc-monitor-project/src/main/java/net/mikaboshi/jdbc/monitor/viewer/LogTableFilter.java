package net.mikaboshi.jdbc.monitor.viewer;

import java.util.regex.PatternSyntaxException;

import net.mikaboshi.jdbc.SQLFormatter;
import net.mikaboshi.jdbc.monitor.AutoCommit;
import net.mikaboshi.jdbc.monitor.LogEntry;
import net.mikaboshi.jdbc.monitor.Result;
import net.mikaboshi.jdbc.monitor.SqlUtils;
import net.mikaboshi.jdbc.monitor.ViewerConfig;
import net.mikaboshi.util.ThreadSafeUtils;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.builder.ToStringBuilder;

/**
 * ログテーブルのフィルター
 * @author Takuma Umezawa
 *
 */
public class LogTableFilter {

	/**
	 *
	 * @param config フィルタ情報が格納されたオブジェクト
	 */
	public LogTableFilter() {
	}

	private boolean isIdVisible(String filter, String data) {

		if (StringUtils.isBlank(filter)) {
			return true;
		}

		return filter.equals(data);
	}

	/**
	 * 引数の行（ログ）を表示するか判定する。
	 *
	 * @param entry
	 * @return 表示するときは、trueを返す。
	 */
	public boolean isRowVisible(LogEntry entry) {

		ViewerConfig.Filter filter = ViewerConfig.getInstance().getFilter();

		// 種別
		if (!filter.getLogType().isVisible(entry.getLogType())) {
			return false;
		}

		// スレッド名
		if (!isIdVisible(
				filter.getThreadName(),
				entry.getThreadName())) {
			return false;
		}

		// コネクションID
		if (!isIdVisible(
				filter.getConnectionId(),
				entry.getConnectionId())) {
			return false;
		}

		// ステートメントID
		if (!isIdVisible(
				filter.getStatementId(),
				entry.getStatementId())) {
			return false;
		}

		// SQL
		if (!filter.getSqlFilter().isVisible(entry.getSql())) {
			return false;
		}

		// 更新件数
		if (entry.getAffectedRows() != null) {
			if (filter.getAffectedRowsMin() > entry.getAffectedRows()) {
				return false;
			}
			if (filter.getAffectedRowsMax() < entry.getAffectedRows()) {
				return false;
			}
		}

		// 経過時間
		if (entry.getElapsedTime() != null) {
			long elapsedTime = entry.getElapsedTime() / 1000000;

			if (filter.getElapsedTimeMin() > elapsedTime) {
				return false;
			}
			if (filter.getElapsedTimeMax() < elapsedTime) {
				return false;
			}
		}

		// 結果
		if (filter.getResult() != Result.ALL) {
			if (filter.getResult() != entry.getResult()) {
				return false;
			}
		}

		// オートコミット
		if (filter.getAutoCommit() != AutoCommit.ALL) {
			if (filter.getAutoCommit() != entry.getAutoCommit()) {
				return false;
			}
		}

		// タグ
		if (!isIdVisible(
				filter.getTag(),
				entry.getTag())) {
			return false;
		}

		return true;
	}

	public static interface SqlFilter {

		/**
		 * 引数のSQL文を、現在のフィルタ設定で表示するかどうかを判定する。
		 * @param sql
		 * @return
		 */
		public boolean isVisible(String sql);
	}

	/**
	 * 全てのSQLを表示する。
	 */
	public static class NoSqlFilter implements SqlFilter {

		private NoSqlFilter() {}

		public static NoSqlFilter INSTANCE = new NoSqlFilter();

		public boolean isVisible(String sql) {
			return true;
		}
	}

	/**
	 * コマンド種別（select, insert等）でフィルタする
	 */
	public static class CommandSqlFilter implements SqlFilter {

		public CommandSqlFilter() {}

		/** SELECT文を表示するならばtrue */
		private boolean selectVisible = true;

		public void setSelectVisible(boolean b) {
			this.selectVisible = b;
		}

		public boolean isSelectVisible() {
			return this.selectVisible;
		}

		/** UPDATE文を表示するならばtrue */
		private boolean updateVisible = true;

		public void setUpdateVisible(boolean b) {
			this.updateVisible = b;
		}

		public boolean isUpdateVisible() {
			return this.updateVisible;
		}

		/** DELETE文を表示するならばtrue */
		private boolean deleteVisible = true;

		public void setDeleteVisible(boolean b) {
			this.deleteVisible = b;
		}

		public boolean isDeleteVisible() {
			return this.deleteVisible;
		}

		/** INSERT文を表示するならばtrue */
		private boolean insertVisible = true;

		public void setInsertVisible(boolean b) {
			this.insertVisible = b;
		}

		public boolean isInsertVisible() {
			return this.insertVisible;
		}

		public boolean isVisible(String sql) {
			if (StringUtils.isBlank(sql)) {
				return false;
			}

			boolean isDml = false;

			for (String token : new SQLFormatter().tokenize(sql)) {
				token = token.toLowerCase();

				if ("update".equals(token)) {
					isDml = true;
					if (this.updateVisible) {
						return true;
					}
				}

				if ("insert".equals(token)) {
					isDml = true;
					if (insertVisible) {
						return true;
					}
				}

				if ("delete".equals(token)) {
					isDml = true;
					if (deleteVisible) {
						return true;
					}
				}

				if ("select".equals(token)) {
					if (!isDml && selectVisible) {
						return true;
					}
				}
			}

			return false;
		}

		@Override
		public String toString() {
			return ToStringBuilder.reflectionToString(this);
		}
	}

	/**
	 * 正規表現でフィルタする
	 */
	public static class RegExSqlFilter implements SqlFilter {

		public RegExSqlFilter() {}

		private String includeRegex;

		public void setIncludePattern(String regex)	throws PatternSyntaxException {
			this.includeRegex = regex;
			// 構文チェック
			ThreadSafeUtils.matches("", this.includeRegex, true, false);
		}

		public String getIncludePattern() {
			return includeRegex;
		}

		private String excludeRegex;

		public void setExcludePattern(String regex) throws PatternSyntaxException {
			this.excludeRegex = regex;
			// 構文チェック
			ThreadSafeUtils.matches("", this.excludeRegex, true, false);
		}

		public String getExcludePattern() {
			return this.excludeRegex;
		}

		public boolean isVisible(String sql) {

			if (StringUtils.isBlank(sql)) {
				return false;
			}

			if ( StringUtils.isEmpty(this.includeRegex) && StringUtils.isEmpty(this.excludeRegex) ) {
				// パターン指定なしならば、全て表示
				return true;
			}

			// 不要なスペースを取り除く
			sql = SqlUtils.linize(sql);

			if ( StringUtils.isEmpty(this.excludeRegex) ) {
				// excludePatternが無指定ならば、includePatternにマッチするもののみ表示
				return ThreadSafeUtils.matches(sql, this.includeRegex, true, false);
			}

			if ( StringUtils.isEmpty(this.includeRegex) ) {
				// includePatternが無指定ならば、excludePatternにマッチしないもののみ表示
				return !ThreadSafeUtils.matches(sql, this.excludeRegex, true, false);
			}

			// 両方が指定されている場合、includePatternにマッチし、かつexcludePatternに
			// マッチしないもののみを表示
			return ThreadSafeUtils.matches(sql, this.includeRegex, true, false) &&
					!ThreadSafeUtils.matches(sql, this.excludeRegex, true, false);
		}

		@Override
		public String toString() {
			return ToStringBuilder.reflectionToString(this);
		}

	}
}
