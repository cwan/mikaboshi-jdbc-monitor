package net.mikaboshi.jdbc.monitor;

import java.sql.Timestamp;
import java.text.DecimalFormat;
import java.text.Format;
import java.util.SortedMap;

import net.mikaboshi.jdbc.SQLFormatter;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.time.FastDateFormat;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import blanco.commons.sql.format.BlancoSqlFormatter;
import blanco.commons.sql.format.BlancoSqlFormatterException;
import blanco.commons.sql.format.BlancoSqlRule;

/**
 * SQLに関するユーティリティクラス。
 * @author Takuma Umezawa
 * @version 1.4.3
 */
public class SqlUtils {

	private static final Log logger = LogFactory.getLog(SqlUtils.class);

	private static final BlancoSqlFormatter SQL_FORMATTER;

	private static final Format DATE_FORMAT = FastDateFormat.getInstance("yyyy-MM-dd");

	private static final Format TIME_FORMAT = FastDateFormat.getInstance("HH:mm:ss");

	private static final Format TIMESTAMP_FORMAT = FastDateFormat.getInstance("yyyy-MM-dd HH:mm:ss");

	private static final Format NANOS_FORMAT = new DecimalFormat("'.'000000000");

	static {

		BlancoSqlRule blancoSqlRule = new BlancoSqlRule();
		blancoSqlRule.setKeywordCase(BlancoSqlRule.KEYWORD_UPPER_CASE);

		SQL_FORMATTER = new BlancoSqlFormatter(blancoSqlRule);
	}

	/**
	 * PreparedStatementの?を、実パラメータに変換する。
	 * ログ出力用の簡易機能なので、実際のSQL実行には使用してはならない。
	 *
	 * @param pstmt
	 * @return
	 */
	public static String replaceParameters(
			String sql, SortedMap<Integer, Object> boundParameters) {

		StringBuilder sb = new StringBuilder();

		int parameterIndex = 1;

		// 文字列リテラルの中かどうか
		boolean isInQuote = false;

		for (char c : sql.toCharArray()) {

			if (!isInQuote && c == '?') {

				Object param = boundParameters.get(parameterIndex++);
				sb.append(toLiteral(param, true));

			} else {
				sb.append(c);
			}

			if (c == '\'') {
				isInQuote = !isInQuote;
			}
		}

		return sb.toString();
	}

	/**
	 * オブジェクトをリテラル表現にする。
	 * @param arg
	 * @param quote
	 * @return
	 * @version 1.4.3
	 */
	public static String toLiteral(Object arg, boolean quote) {

		if (arg == null) {
			return "null";
		}

		if (arg instanceof Number) {
			return arg.toString();
		}

		StringBuilder sb = new StringBuilder();

		if (quote) {
			sb.append('\'');
		}

		if (arg instanceof java.util.Date) {

			if (arg instanceof java.sql.Timestamp) {

				sb.append(TIMESTAMP_FORMAT.format(arg));

				int nanos = ((Timestamp) arg).getNanos();

				synchronized (NANOS_FORMAT) {
					sb.append(NANOS_FORMAT.format(nanos));
				}

			} else if (arg instanceof java.sql.Time) {

				sb.append(TIME_FORMAT.format(arg));

			} else {

				sb.append(DATE_FORMAT.format(arg));
			}

		} else {

			if (quote) {
				// エスケープ要
				for (char ch : arg.toString().toCharArray()) {

					if (ch == '\'') {
						sb.append('\'');
					}

					sb.append(ch);
				}

			} else {
				sb.append(arg.toString());
			}
		}

		if (quote) {
			sb.append('\'');
		}

		return sb.toString();
	}

	/**
	 * SQL中の改行をスペースに変換する。
	 *
	 * @param sql
	 * @return
	 */
	public static String linize(String sql) {

		if (sql == null) {
			return StringUtils.EMPTY;
		}

		String[] tokenize = new SQLFormatter().tokenize(sql);

		return StringUtils.join(tokenize, ' ');
	}

	/**
	 * SQLをフォーマットする
	 * @param sql
	 * @return
	 * @since 1.4.1
	 */
	public static String format(String sql) {

		if (sql == null) {
			return StringUtils.EMPTY;
		}

		try {
			return SQL_FORMATTER.format(sql);

		} catch (BlancoSqlFormatterException e) {

			logger.debug("SQL format error: [" + sql + "]", e);

			return sql;
		}
	}
}
