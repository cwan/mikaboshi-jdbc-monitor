package net.mikaboshi.jdbc.monitor;

import java.sql.Time;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.SortedMap;
import java.util.TreeMap;

import junit.framework.TestCase;

import org.junit.Test;

public class SqlUtilsTest extends TestCase {

	@Test
	public void testReplaceParameters_NoParameter() {
		SortedMap<Integer, Object> boundParameters = new TreeMap<Integer, Object>();

		String sql = "insert into hoge value (1, 'aaa')";

		assertEquals(sql, SqlUtils.replaceParameters(sql, boundParameters));
	}

	@Test
	public void testReplaceParameters_1Parameter() {
		SortedMap<Integer, Object> boundParameters = new TreeMap<Integer, Object>();
		boundParameters.put(1, 5);

		String sql		= "insert into hoge value (?, 'aaa')";
		String expected	= "insert into hoge value (5, 'aaa')";

		assertEquals(expected, SqlUtils.replaceParameters(sql, boundParameters));
	}

	@Test
	public void testReplaceParameters_2Parameters() {
		SortedMap<Integer, Object> boundParameters = new TreeMap<Integer, Object>();
		boundParameters.put(1, 5);
		boundParameters.put(2, "bbb");

		String sql		= "insert into hoge value (?, ?)";
		String expected	= "insert into hoge value (5, 'bbb')";

		assertEquals(expected, SqlUtils.replaceParameters(sql, boundParameters));
	}

	@Test
	public void testReplaceParameters_NullParameter() {
		SortedMap<Integer, Object> boundParameters = new TreeMap<Integer, Object>();
		boundParameters.put(1, 5);

		String sql		= "insert into hoge value (?, ?)";
		String expected	= "insert into hoge value (5, null)";

		assertEquals(expected, SqlUtils.replaceParameters(sql, boundParameters));
	}

	@Test
	public void testReplaceParameters_QuotationInParameter() {
		SortedMap<Integer, Object> boundParameters = new TreeMap<Integer, Object>();
		boundParameters.put(1, 5);
		boundParameters.put(2, "bb'b");

		String sql		= "insert into hoge value (?, ?)";
		String expected	= "insert into hoge value (5, 'bb''b')";

		assertEquals(expected, SqlUtils.replaceParameters(sql, boundParameters));
	}

	@Test
	public void testReplaceParameters_QuotationInLiteral() {
		SortedMap<Integer, Object> boundParameters = new TreeMap<Integer, Object>();
		boundParameters.put(1, 5);

		String sql		= "insert into hoge value (?, 'bb''b')";
		String expected	= "insert into hoge value (5, 'bb''b')";

		assertEquals(expected, SqlUtils.replaceParameters(sql, boundParameters));
	}

	@Test
	public void testReplaceParameters_QuestionInLiteral() {
		SortedMap<Integer, Object> boundParameters = new TreeMap<Integer, Object>();
		boundParameters.put(1, 5);
		boundParameters.put(2, "xxx");

		String sql		= "insert into hoge value (?, 'bb?b')";
		String expected	= "insert into hoge value (5, 'bb?b')";

		assertEquals(expected, SqlUtils.replaceParameters(sql, boundParameters));
	}

	@Test
	public void testReplaceParameters_TimeStamp() {

		long now = System.currentTimeMillis();
		Timestamp timestamp = new Timestamp(now);

		SortedMap<Integer, Object> boundParameters = new TreeMap<Integer, Object>();
		boundParameters.put(1, timestamp);

		String sql		= "insert into hoge value (?)";
		String expected	= "insert into hoge value ('" +  new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS000000").format(new java.util.Date(now)) + "')";

		assertEquals(expected, SqlUtils.replaceParameters(sql, boundParameters));
	}

	@Test
	public void testReplaceParameters_Time() {

		long now = System.currentTimeMillis();
		Time time = new Time(now);

		SortedMap<Integer, Object> boundParameters = new TreeMap<Integer, Object>();
		boundParameters.put(1, time);

		String sql		= "insert into hoge value (?)";
		String expected	= "insert into hoge value ('" +  new SimpleDateFormat("HH:mm:ss").format(new java.util.Date(now)) + "')";

		assertEquals(expected, SqlUtils.replaceParameters(sql, boundParameters));
	}

	@Test
	public void testReplaceParameters_sqlDate() {

		long now = System.currentTimeMillis();
		java.sql.Date date = new java.sql.Date(now);

		SortedMap<Integer, Object> boundParameters = new TreeMap<Integer, Object>();
		boundParameters.put(1, date);

		String sql		= "insert into hoge value (?)";
		String expected	= "insert into hoge value ('" +  new SimpleDateFormat("yyyy-MM-dd").format(new java.util.Date(now)) + "')";

		assertEquals(expected, SqlUtils.replaceParameters(sql, boundParameters));
	}

	@Test
	public void testReplaceParameters_utilDate() {

		long now = System.currentTimeMillis();
		java.util.Date date = new java.util.Date(now);

		SortedMap<Integer, Object> boundParameters = new TreeMap<Integer, Object>();
		boundParameters.put(1, date);

		String sql		= "insert into hoge value (?)";
		String expected	= "insert into hoge value ('" +  new SimpleDateFormat("yyyy-MM-dd").format(new java.util.Date(now)) + "')";

		assertEquals(expected, SqlUtils.replaceParameters(sql, boundParameters));
	}

}
