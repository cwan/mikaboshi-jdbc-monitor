package net.mikaboshi.jdbc.monitor.viewer;

import net.mikaboshi.jdbc.monitor.LogEntry;

/**
 *
 * @since 1.4.3
 * @version 1.4.3
 *
 */
public interface LogEntryProvider {

	public LogEntry getCurrentLogEntry();
}
