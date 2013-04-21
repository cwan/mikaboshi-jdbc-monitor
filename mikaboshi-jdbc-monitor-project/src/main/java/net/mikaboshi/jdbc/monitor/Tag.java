package net.mikaboshi.jdbc.monitor;

/**
 * JDBC Monitorのタグを設定/取得する。
 *
 * @author Takuma Umezawa
 * @since 1.4.2
 */
public final class Tag {

	private final static Tag INSTANCE = new Tag();

	private final ThreadLocal<String> threadLocal = new ThreadLocal<String>();

	private Tag() {
	}

	public static Tag getInstance() {
		return INSTANCE;
	}

	public String get() {
		return this.threadLocal.get();
	}

	public void set(String value) {
		this.threadLocal.set(value);
	}

	public void remove() {
		this.threadLocal.remove();
	}
}
