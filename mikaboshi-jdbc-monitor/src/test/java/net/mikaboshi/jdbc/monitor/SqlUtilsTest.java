package net.mikaboshi.jdbc.monitor;

import java.util.SortedMap;
import java.util.TreeMap;

import net.mikaboshi.jdbc.monitor.SqlUtils;

import org.junit.Test;

import junit.framework.TestCase;

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
	
}
