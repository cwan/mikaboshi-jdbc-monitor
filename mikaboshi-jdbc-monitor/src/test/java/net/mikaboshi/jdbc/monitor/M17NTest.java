package net.mikaboshi.jdbc.monitor;

import net.mikaboshi.jdbc.monitor.M17N;
import junit.framework.TestCase;

public class M17NTest extends TestCase {

	public void testNoSuchKey() {
		assertEquals("xxx", M17N.get("xxx"));
	}
	
	public void testNoParameter() {
		assertEquals("経過時間", M17N.get("elapsed_time"));
	}
	
	public void test1Parameter() {
		assertEquals("予期せぬエラーが発生しました。\nあああ",
				M17N.get("message.unexpected_error", "あああ"));
	}
}
