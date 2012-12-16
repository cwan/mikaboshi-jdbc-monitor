/**
 * <p>
 * WrapperモードでJDBCログを取得するときに、このパッケージのクラスを使用する。
 * </p>
 * <p>
 * JDBCの接続情報として、JDBCのドライバとURLを以下のように指定する。
 * <ul>
 *   <li>ドライバ： {@code net.mikaboshi.JdbcLogDriver}</li>
 *   <li>URL： {@code 本物のURL?driver=本物のドライバクラス&logfile=ログファイルパス}</li>
 * </ul>
 * </p>
 * <p>
 * ※ logfileクエリパラメータは省略可
 *    （省略時は、デフォルトのログファイルパスが適用される）
 * </p>
 * <p>
 * 例： {@code jdbc:oracle:thin:@localhost:1521:orcl?driver=oracle.jdbc.driver.OracleDriver&logfile=../jdbc-tracer.log}
 * </p>		
 */
package net.mikaboshi.jdbc.monitor.log.wrapper;
