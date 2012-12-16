package net.mikaboshi.jdbc.monitor;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Map;

import net.mikaboshi.jdbc.DbUtils;
import net.mikaboshi.jdbc.DmlExecutor;
import net.mikaboshi.jdbc.QueryExecutor;

public class OracleTestx {

	private Connection conn;
	
	public static void main(String[] args) throws Exception {
		new OracleTestx().execute();
	}
	
	public void execute() throws Exception {
		try {
			this.conn = DbUtils.getConnection("src/test/resources/jdbc_oracle.properties");
			this.conn.setAutoCommit(false);
			
			insert();
			query();
			delete();
			
			this.conn.commit();
			
		}catch (SQLException e) {
			DbUtils.rollbackQuietly(this.conn);
		} finally {
			DbUtils.closeQuietly(this.conn);
		}
	}
	
	private void insert() throws SQLException {
		PreparedStatement pstmt = null;
		
		try {
			pstmt = this.conn.prepareStatement("insert into b_m_user_b values (?, ?, ?, ?, ?, ?)");
			
			Object[] params = new Object[] {
					"aaaaaaaaa",
					"1",
					null,
					9999,
					"OracleTestx", 
					new SimpleDateFormat("yyyy/MM/dd|HH:mm:ss").format(new Date())
			};
			
			DmlExecutor.execute(pstmt, params);
			
		} finally {
			DbUtils.closeQuietly(pstmt);
		}
	}
	
	private void query() throws SQLException {
		List<Map<String, Object>> result = QueryExecutor.query(this.conn,
				"select * from b_m_user_b where user_cd = ?", new Object[] {"aaaaaaaaa"});
		
		for (Map<String, Object> row : result) {
			System.out.print("#query result: ");
			for (String key : row.keySet()) {
				System.out.printf("%s=%s ", key, row.get(key));
			}
			System.out.println("");
		}
		
	}
	
	private void delete() throws SQLException {
		PreparedStatement pstmt = null;
		
		try {
			pstmt = this.conn.prepareStatement("delete from b_m_user_b where user_cd = ?");
			
			DmlExecutor.execute(pstmt, new Object[] {"aaaaaaaaa"});
			
		} finally {
			DbUtils.closeQuietly(pstmt);
		}
	}

}
