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
 *
 */
public class SqlUtils {

	private static final Log logger = LogFactory.getLog(SqlUtils.class);

	private static final BlancoSqlFormatter formatter;

	private static final Format dateFormat = FastDateFormat.getInstance("yyyy-MM-dd");

	private static final Format timeFormat = FastDateFormat.getInstance("HH:mm:ss");

	private static final Format timestampFormat = FastDateFormat.getInstance("yyyy-MM-dd HH:mm:ss");

	private static final Format nanosFormat = new DecimalFormat("'.'000000000");

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

				} else if (param instanceof java.sql.Timestamp) {

					sb.append('\'');
					sb.append(timestampFormat.format(param));

					int nanos = ((Timestamp) param).getNanos();

					String s;

					synchronized (nanosFormat) {
						s = nanosFormat.format(nanos);
					}

					sb.append(s);
					sb.append('\'');

				} else if (param instanceof java.sql.Time) {

					sb.append('\'');
					sb.append(timeFormat.format(param));
					sb.append('\'');

				} else if (param instanceof java.sql.Date) {

					sb.append('\'');
					sb.append(dateFormat.format(param));
					sb.append('\'');

				} else if (param instanceof java.util.Date) {

					sb.append('\'');
					sb.append(dateFormat.format(param));
					sb.append('\'');

				} else {

					sb.append('\'');

					for (char ch : param.toString().toCharArray()) {

						if (ch == '\'') {
							sb.append('\'');
						}

						sb.append(ch);
					}

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
