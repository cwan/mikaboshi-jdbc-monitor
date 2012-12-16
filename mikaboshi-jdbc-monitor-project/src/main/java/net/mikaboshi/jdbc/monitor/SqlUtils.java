package net.mikaboshi.jdbc.monitor;

import java.math.BigDecimal;
import java.sql.Date;
import java.sql.Time;
import java.sql.Timestamp;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.SortedMap;

import org.apache.commons.lang.StringUtils;

/**
 * SQLに関するユーティリティクラス。
 * @author Takuma Umezawa
 *
 */
public class SqlUtils {
	
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
					sb.append('\'');
					sb.append(StringUtils.replace(param.toString(), "'", "''"));
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
	public static String replaceCrLf(String sql) {
		StringBuilder sb = new StringBuilder();
		char previous = 0;
		
		for (char c : sql.toCharArray()) {
			if (c == '\r' || c == '\n') {
				if (!Character.isWhitespace(previous)) {
					// 2つ以上続けてスペースは入らないようにする
					sb.append(' ');
				}
			} else {
				sb.append(c);
			}
			
			previous = c;
		}
		
		return sb.toString();
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
}
