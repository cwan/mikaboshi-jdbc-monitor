package net.mikaboshi.jdbc.monitor.log.wrapper;

import java.sql.SQLException;

import org.junit.Test;

import net.mikaboshi.jdbc.monitor.LogWriter;
import net.mikaboshi.jdbc.monitor.log.wrapper.DriverLogWrapper;
import junit.framework.TestCase;

public class DriverLogWrapperTest extends TestCase {

	@Test
	public void testParseUrl_NoDriver() {
		String url = "jdbc:oracle:thin:@localhost:1521:orcl";
		
		DriverLogWrapper dlw = new DriverLogWrapper();
		try {
			dlw.parseUrl(url);
			fail();
		} catch (SQLException e) {
			assertTrue(true);
		}
	}
	
	@Test
	public void testParseUrl_NoLogfile() throws SQLException {
		String url = "jdbc:oracle:thin:@localhost:1521:orcl?driver=oracle.jdbc.driver.OracleDriver";
		
		DriverLogWrapper dlw = new DriverLogWrapper();
		dlw.parseUrl(url);
		
		assertEquals("jdbc:oracle:thin:@localhost:1521:orcl", dlw.url);
		assertEquals("oracle.jdbc.driver.OracleDriver", dlw.driverClassName);
	}
	
	@Test
	public void testParseUrl_WithLogfile() throws SQLException {
		String url = "jdbc:oracle:thin:@localhost:1521:orcl?driver=oracle.jdbc.driver.OracleDriver&logfile=../jdbclog.txt";
		
		DriverLogWrapper dlw = new DriverLogWrapper();
		dlw.parseUrl(url);
		
		assertEquals("jdbc:oracle:thin:@localhost:1521:orcl", dlw.url);
		assertEquals("oracle.jdbc.driver.OracleDriver", dlw.driverClassName);
		
		assertEquals("../jdbclog.txt", System.getProperty(LogWriter.PROP_NAME));
	}
	
}
