/**
 * <p>
 * AspectJモードでJDBCログを取得するときに、このパッケージのクラスを使用する。
 * </p><p>
 * Javaの実行時オプションを、以下のように指定する。
 * {@code -javaagent:path_to/aspectjweaver.jar -javaagent:-Dnet.mikaboshi.jdbc_monitor.logfile=path_to_logfile}
 * </p>
 * <p>
 * ※ jdbc.logger.logfileシステムプロパティは省略可
 *    （省略時は、デフォルトのログファイルパスが適用される）
 * </p>
 * <p>
 * 例： {@code java -javaagent:lib/aspectjweaver.jar -Dnet.mikaboshi.jdbc_monitor.logfile=../jdbc.log org.example.Main}
 * </p>		
 */
package net.mikaboshi.jdbc.monitor.log.aspect;
