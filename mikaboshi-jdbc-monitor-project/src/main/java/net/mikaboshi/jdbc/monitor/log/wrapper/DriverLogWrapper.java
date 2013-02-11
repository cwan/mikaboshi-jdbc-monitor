package net.mikaboshi.jdbc.monitor.log.wrapper;

import java.sql.Connection;
import java.sql.Driver;
import java.sql.DriverManager;
import java.sql.DriverPropertyInfo;
import java.sql.SQLException;
import java.util.Properties;

import net.mikaboshi.jdbc.monitor.LogMode;
import net.mikaboshi.jdbc.monitor.LogWriter;
import net.mikaboshi.util.ThreadSafeUtils;

import org.apache.commons.lang.StringUtils;

/**
 * <p>
 * JDBCログを取得するための {@link Driver} ラッパークラス。
 * 本来の処理は全てURLに指定した {@code Driver} オブジェクトに委譲する。
 * <p></p>
 * 接続情報として、以下のようなURLを指定する。<br>
 * {@code <本物のURL>?driver=<本物のドライバクラス>&logfile=<ログファイルパス>&delete=<trueまたはfalse>&rotate=<ローテーションサイズ>&callstack=<整数>&write-connect=<trueまたはfalse>&write-password=<trueまたはfalse>}
 * <p></p>
 * パラメータ {@code logfile} はオプションである。省略した場合、デフォルトパスにログファイルが作成される。（{@link LogWriter} 参照）
 * <br/>
 * パラメータ {@code delete} はオプションである。{@code "true"} が指定された場合、起動時に既存のログファイルを削除する。
 * <br/>
 * パラメータ {@code rotate} はオプションである。整数値が指定された場合、そのサイズ（mega byte）でローテーションを行う。
 * <br/>
 * パラメータ {@code callstack} はオプションである。整数値が指定された場合、SQL 実行時のコールスタックトレースがその行数だけログに出力される。
 * <br/>
 * パラメータ {@code write-connect} はオプションである。「true」を指定すると、データベース接続情報（ドライバクラス、URL、ユーザ名）がログに出力される。
 * <br/>
 * パラメータ {@code write-password} はオプションである。「true」を指定すると、データベース接続情報のパスワードがログに出力される（write-connect も true である必要がある）。
 * </p>
 *
 * @author Takuma Umezawa
 */
public class DriverLogWrapper implements Driver {

	static {
        try {
			DriverManager.registerDriver(new DriverLogWrapper());
		} catch (SQLException e) {
			throw new RuntimeException("Registering JDBC Logger driver failed.", e);
		}
	}

	private Driver driver;
	String url;
	String driverClassName;

	public DriverLogWrapper() {}

	@Override
	public boolean acceptsURL(String url) throws SQLException {
		parseUrl(url);
		createDriver();

		return this.driver.acceptsURL(this.url);
	}

	@Override
	public Connection connect(String url, Properties info) throws SQLException {
		parseUrl(url);
		createDriver();

		logProperties(info);

		return new ConnectionWrapper(this.driver.connect(this.url, info));
	}

	@Override
	public int getMajorVersion() {
		return this.driver.getMajorVersion();
	}

	@Override
	public int getMinorVersion() {
		return this.driver.getMinorVersion();
	}

	@Override
	public DriverPropertyInfo[] getPropertyInfo(String url, Properties info)
			throws SQLException {

		parseUrl(url);
		createDriver();

		logProperties(info);

		return this.getPropertyInfo(this.url, info);
	}

	@Override
	public boolean jdbcCompliant() {
		return this.driver.jdbcCompliant();
	}

	private void createDriver() {
		if (this.driver != null) {
			return;
		}

		try {
			Class<?> clazz = Class.forName(this.driverClassName);
			this.driver = (Driver) clazz.newInstance();

			// バージョンをログに出力する
			LogWriter.putDriverVersion(this.driver.getMajorVersion(), this.driver.getMinorVersion());

		} catch (Exception e) {
			throw new RuntimeException("Creating JDBC Logger driver failed.", e);
		}
	}

	void parseUrl(String url) throws SQLException {

		if (this.url != null) {
			return;
		}

		String[] match = ThreadSafeUtils.match(url, "(.+)\\?(.+)");

		if (match == null) {
			throw new SQLException("Invalid URL : " + url);
		}

		this.url = match[0];

		StringBuilder properParams = new StringBuilder();

		try {
			for (String query : match[1].split("&")) {
				String[] param = query.split("=");

				if (param[0].equals("driver")) {
					this.driverClassName = param[1];
				} else if (param[0].equals("logfile")) {
					System.setProperty(LogWriter.PROP_NAME, param[1]);
				} else if (param[0].equals("delete")) {
					System.setProperty(LogWriter.PROP_DELETE_ON_BOOT, param[1]);
				} else if (param[0].equals("rotate")) {
					System.setProperty(LogWriter.PROP_ROTATE_MB, param[1]);
				} else if (param[0].equals("callstack")) {
					LogMode.getInstance().setCallStackLevel(Integer.parseInt(param[1]));
				} else if (param[0].equals("write-password")) {
					System.setProperty(LogWriter.PROP_WRITE_PASSWORD, param[1]);
				} else if (param[0].equals("write-connect")) {
					System.setProperty(LogWriter.PROP_WRITE_CONNECT, param[1]);
				} else {
					// 本来のパラメータ
					if (properParams.length() == 0) {
						properParams.append("?");
					} else {
						properParams.append("&");
					}

					properParams.append(query);
				}
			}
		} catch (Exception e) {
			throw new SQLException("Invalid URL : " + url);
		}

		if (this.driverClassName == null) {
			throw new SQLException("Invalid URL : " + url);
		}

		if (properParams.length() != 0) {
			this.url += properParams.toString();
		}

		// 接続情報ログ出力
		LogWriter.putDriver(this.driverClassName);
		LogWriter.putUrl(this.url);
	}

	private void logProperties(Properties info) {
		LogWriter.putUser(info.getProperty("user", StringUtils.EMPTY));
		LogWriter.putPassword(info.getProperty("password", StringUtils.EMPTY));
	}
}
