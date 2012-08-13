package net.mikaboshi.jdbc.monitor;


import java.util.List;

import junit.framework.TestCase;

public class CRUDModelTest extends TestCase {

	public void testSimpleCreate() {
		
		CRUDModel model = new CRUDModel();
		
		model.add("insert into hoge (a, b) values ('select', '') ");
		
		List<CRUDModel.CRUDEntry> list = model.getEntryList();
		
		assertEquals(1, list.size());
		assertEquals("hoge:1/0/0/0", list.get(0).toString());
	}
	
	public void testSimpleSelect() {
		
		CRUDModel model = new CRUDModel();
		
		model.add("select a, b from hoge where a = 'x' order by a, b");
		
		List<CRUDModel.CRUDEntry> list = model.getEntryList();
		
		assertEquals(1, list.size());
		assertEquals("hoge:0/1/0/0", list.get(0).toString());
	}
	
	public void testSimpleUpdate() {
		
		CRUDModel model = new CRUDModel();
		
		model.add("update hoge set x = 'x' where x = 'y'");
		
		List<CRUDModel.CRUDEntry> list = model.getEntryList();
		
		assertEquals(1, list.size());
		assertEquals("hoge:0/0/1/0", list.get(0).toString());
	}
	
	public void testSimpleDelete() {
		
		CRUDModel model = new CRUDModel();
		
		model.add("delete from hoge where x = 'x' or x = 'y'");
		
		List<CRUDModel.CRUDEntry> list = model.getEntryList();
		
		assertEquals(1, list.size());
		assertEquals("hoge:0/0/0/1", list.get(0).toString());
	}
	
	public void testSimpleTruncate() {
		
		CRUDModel model = new CRUDModel();
		
		model.add("truncate  table hoge");
		
		List<CRUDModel.CRUDEntry> list = model.getEntryList();
		
		assertEquals(1, list.size());
		assertEquals("hoge:0/0/0/1", list.get(0).toString());
	}
	
	public void testInsertSelect() {
		
		CRUDModel model = new CRUDModel();
		
		model.add("insert into hoge (x, y) select a, b from fuga where a is not null");
		
		List<CRUDModel.CRUDEntry> list = model.getEntryList();
		
		assertEquals(2, list.size());
		assertEquals("fuga:0/1/0/0", list.get(0).toString());
		assertEquals("hoge:1/0/0/0", list.get(1).toString());
	}
	
	public void testComplexSelect() {
		
		CRUDModel model = new CRUDModel();
		
		model.add("select * from foo t1, baz t2, bar t3 where t1.a = t2.a and t2.b = t3.b and t3.c in (1, 2, 3)");
		
		List<CRUDModel.CRUDEntry> list = model.getEntryList();
		
		assertEquals(3, list.size());
		assertEquals("bar:0/1/0/0", list.get(0).toString());
		assertEquals("baz:0/1/0/0", list.get(1).toString());
		assertEquals("foo:0/1/0/0", list.get(2).toString());
	}
	
	public void testJoinSelect() {
		
		CRUDModel model = new CRUDModel();
		
		model.add("select * from foo t1 inner join bar t2 on (t1.a = t2.a) " +
				"left outer join baz t3 on t3.x in (1, 2, 3)");
		
		List<CRUDModel.CRUDEntry> list = model.getEntryList();
		
		assertEquals(3, list.size());
		assertEquals("bar:0/1/0/0", list.get(0).toString());
		assertEquals("baz:0/1/0/0", list.get(1).toString());
		assertEquals("foo:0/1/0/0", list.get(2).toString());
	}
	
	public void testComplexUpdate() {
		
		CRUDModel model = new CRUDModel();
		
		model.add("update foo set a = 'x' where b in (select b from bar where b is null)");
		
		List<CRUDModel.CRUDEntry> list = model.getEntryList();
		
		assertEquals(2, list.size());
		assertEquals("bar:0/1/0/0", list.get(0).toString());
		assertEquals("foo:0/0/1/0", list.get(1).toString());
	}
}
