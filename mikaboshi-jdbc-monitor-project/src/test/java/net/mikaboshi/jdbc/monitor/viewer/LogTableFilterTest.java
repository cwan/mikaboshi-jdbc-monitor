package net.mikaboshi.jdbc.monitor.viewer;

import junit.framework.TestCase;
import net.mikaboshi.jdbc.monitor.viewer.LogTableFilter.CommandSqlFilter;
import net.mikaboshi.jdbc.monitor.viewer.LogTableFilter.NoSqlFilter;
import net.mikaboshi.jdbc.monitor.viewer.LogTableFilter.RegExSqlFilter;
import net.mikaboshi.jdbc.monitor.viewer.LogTableFilter.SqlFilter;

import org.apache.commons.lang.math.IntRange;

public class LogTableFilterTest extends TestCase {

	/**
	 * SQLフィルタで、フィルタ設定なしの場合。
	 */
	public void testNoSqlFilter() {
		SqlFilter sqlFilter = NoSqlFilter.INSTANCE;

		assertTrue(sqlFilter.isVisible(null));
		assertTrue(sqlFilter.isVisible(""));
		assertTrue(sqlFilter.isVisible("aa"));
		assertTrue(sqlFilter.isVisible("  b  x   w"));
	}

	/**
	 * 正規表現SQLフィルタで、includeのみの場合。
	 */
	public void testRegExSqlFilter_includeOnly() {
		RegExSqlFilter sqlFilter = new RegExSqlFilter();

		sqlFilter.setIncludePattern("^select.*");

		assertFalse(sqlFilter.isVisible(null));
		assertFalse(sqlFilter.isVisible(""));
		assertFalse(sqlFilter.isVisible("aa"));
		assertTrue(sqlFilter.isVisible("  select * from hoge"));
		assertTrue(sqlFilter.isVisible("select * from hoge"));
		assertTrue(sqlFilter.isVisible("SELECT * from hoge"));

		assertEquals("^select.*", sqlFilter.getIncludePattern());
	}

	/**
	 * 正規表現SQLフィルタで、excludeのみの場合。
	 */
	public void testRegExSqlFilter_excludeOnly() {
		RegExSqlFilter sqlFilter = new RegExSqlFilter();

		sqlFilter.setExcludePattern("^select.*");

		assertFalse(sqlFilter.isVisible(null));
		assertFalse(sqlFilter.isVisible(""));
		assertTrue(sqlFilter.isVisible("aa"));
		assertFalse(sqlFilter.isVisible("  select * from hoge"));
		assertFalse(sqlFilter.isVisible("select * from hoge"));
		assertFalse(sqlFilter.isVisible("SELECT * from hoge"));

		assertEquals("^select.*", sqlFilter.getExcludePattern());
	}

	/**
	 * 正規表現SQLフィルタで、SQLフィルタで、include, exclude両方の場合。
	 */
	public void testRegExSqlFilter_includeExclude() {
		RegExSqlFilter sqlFilter = new RegExSqlFilter();

		sqlFilter.setIncludePattern("select");
		sqlFilter.setExcludePattern("hoge");

		assertFalse(sqlFilter.isVisible(null));
		assertFalse(sqlFilter.isVisible(""));
		assertFalse(sqlFilter.isVisible("aa"));
		assertFalse(sqlFilter.isVisible("  select * from hoge"));
		assertTrue(sqlFilter.isVisible("select * from fuga"));

		assertEquals("select", sqlFilter.getIncludePattern());
		assertEquals("hoge", sqlFilter.getExcludePattern());
	}

	/**
	 * 正規表現SQLフィルタで、改行ありの場合。
	 */
	public void testRegExSqlFilter_newLineExists() {
		RegExSqlFilter sqlFilter = new RegExSqlFilter();

		sqlFilter.setIncludePattern("select.*hoge");

		assertTrue(sqlFilter.isVisible("select * from hoge"));
		assertTrue(sqlFilter.isVisible("select * \r\nfrom hoge"));

		assertEquals("select.*hoge", sqlFilter.getIncludePattern());
	}

	public void testAffectedRows() {
		IntRange range = new IntRange(0, 99);
		assertFalse(range.containsInteger(-1));
		assertTrue(range.containsInteger(0));
		assertTrue(range.containsInteger(50));
		assertTrue(range.containsInteger(99));
		assertFalse(range.containsInteger(100));
		assertFalse(range.containsInteger(null));
	}

	public void testCommandSqlFilter_update() {
		CommandSqlFilter filter = new CommandSqlFilter();

		filter.setUpdateVisible(true);
		filter.setInsertVisible(false);
		filter.setDeleteVisible(false);
		filter.setSelectVisible(false);

		assertTrue(filter.isVisible("update hoge set a = 'A'"));
		assertFalse(filter.isVisible("delete from hoge"));
	}

	public void testCommandSqlFilter_insert() {
		CommandSqlFilter filter = new CommandSqlFilter();

		filter.setUpdateVisible(false);
		filter.setInsertVisible(true);
		filter.setDeleteVisible(false);
		filter.setSelectVisible(false);

		assertTrue(filter.isVisible("INSERT INTO hoge values (1 ,2 ,3)"));
		assertFalse(filter.isVisible("delete from hoge"));
	}

	public void testCommandSqlFilter_delete() {
		CommandSqlFilter filter = new CommandSqlFilter();

		filter.setUpdateVisible(false);
		filter.setInsertVisible(false);
		filter.setDeleteVisible(true);
		filter.setSelectVisible(false);

		assertTrue(filter.isVisible(" delete\tfrom  hoge where a = 1"));
		assertFalse(filter.isVisible("update hoge set a = 'A'"));
	}

	public void testCommandSqlFilter_select() {
		CommandSqlFilter filter = new CommandSqlFilter();

		filter.setUpdateVisible(false);
		filter.setInsertVisible(false);
		filter.setDeleteVisible(false);
		filter.setSelectVisible(true);

		assertTrue(filter.isVisible("select * from hoge"));
		assertFalse(filter.isVisible("update hoge set a = (select x from x where id = 1)"));
	}

	public void testCommandSqlFilter_all() {
		CommandSqlFilter filter = new CommandSqlFilter();

		filter.setUpdateVisible(true);
		filter.setInsertVisible(true);
		filter.setDeleteVisible(true);
		filter.setSelectVisible(true);

		assertTrue(filter.isVisible("select * from hoge"));
		assertTrue(filter.isVisible("update hoge set a = (select x from x where id = 1)"));
		assertTrue(filter.isVisible("INSERT INTO hoge values (1 ,2 ,3)"));
		assertTrue(filter.isVisible("delete from hoge"));
	}

}
