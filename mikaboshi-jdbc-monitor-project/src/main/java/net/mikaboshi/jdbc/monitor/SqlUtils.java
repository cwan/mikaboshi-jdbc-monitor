package net.mikaboshi.jdbc.monitor;

import java.math.BigDecimal;
import java.sql.Date;
import java.sql.Time;
import java.sql.Timestamp;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.SortedMap;

import net.mikaboshi.jdbc.SQLFormatter;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import blanco.commons.sql.format.BlancoSqlFormatter;
import blanco.commons.sql.format.BlancoSqlFormatterException;
import blanco.commons.sql.format.BlancoSqlRule;

/**
 * SQLに関するユーティリティクラス。
 * @author Takuma Umezawa
 *
 */
public class SqlUtils {

	private static final Log logger = LogFactory.getLog(SqlUtils.class);

	private static final BlancoSqlFormatter formatter;

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

				if (param == null) {
					sb.append("null");

				} else if (param instanceof Number) {
					sb.append(param);

				} else {

					String value;

					if (param instanceof java.sql.Timestamp) {

						int nanos = ((Timestamp) param).getNanos();

						synchronized (timestampFormat) {
							value = timestampFormat.format(param) + nanosFormat.format(nanos);
						}

					} else if (param instanceof java.sql.Time) {

						synchronized(timeFormat) {
							value = timeFormat.format(param);
						}

					} else if (param instanceof java.sql.Date) {

						synchronized(timestampFormat) {
							value = timestampFormat.format(param);
						}

					} else if (param instanceof java.util.Date) {

						synchronized(dateFormat) {
							value = dateFormat.format(param);
						}

					} else {
						value = StringUtils.replace(param.toString(), "'", "''");
					}

					sb.append('\'');
					sb.append(value);
					sb.append('\'');
				}

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

	private static final SimpleDateFormat dateFormat =
		new SimpleDateFormat("yyyy-MM-dd");

	private static final SimpleDateFormat timeFormat =
		new SimpleDateFormat("HH:mm:ss");

	private static final SimpleDateFormat timestampFormat =
		new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

	private static final DecimalFormat nanosFormat =
		new DecimalFormat("'.'000000000");

	/**
	 * 日時や数値のパラメータをSQL形式の文字列に変換する。
	 * @param o
	 * @return
	 */
	public static String formatParameter(Object o) {

		if (o == null) {
			return "null";
		}

		if (o instanceof Date) {
			synchronized(dateFormat) {
				return dateFormat.format(o);
			}
		}

		if (o instanceof Time) {
			synchronized(timeFormat) {
				return timeFormat.format(o);
			}
		}

		if (o instanceof Timestamp) {
			int nanos = ((Timestamp) o).getNanos();

			synchronized (timestampFormat) {
				return timestampFormat.format(o) + nanosFormat.format(nanos);
			}
		}

		if (o instanceof BigDecimal) {
			return ((BigDecimal) o).toPlainString();
		}

		return o.toString();
	}

	static {

		BlancoSqlRule blancoSqlRule = new BlancoSqlRule();
		blancoSqlRule.setKeywordCase(BlancoSqlRule.KEYWORD_UPPER_CASE);

		formatter = new BlancoSqlFormatter(blancoSqlRule);

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
			return formatter.format(sql);

		} catch (BlancoSqlFormatterException e) {

			logger.debug("SQL format error: [" + sql + "]", e);

			return sql;
		}
	}
}
