package net.mikaboshi.jdbc.monitor;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import net.mikaboshi.jdbc.SQLFormatter;

import org.apache.commons.lang.StringUtils;

/**
 * CRUD (Create/Read/Update/Delete) 情報モデル。
 * 複数の SQL をインプットとし、そこに含まれるテーブルと CRUD 種別を解析する。
 * 
 * @author Takuma Umezawa
 * @since 1.3.0
 */
public class CRUDModel {

	private Map<String, CRUDEntry> crudMap = new HashMap<String, CRUDEntry>();
	
	private SQLFormatter sqlFormatter = new SQLFormatter();
	
	public CRUDModel() {
	}
	
	/**
	 * SQLを追加し、解析を行う。
	 * @param sql
	 */
	public void add(String sql) {
		if (StringUtils.isBlank(sql)) {
			return;
		}
		
		String[] tokens = this.sqlFormatter.tokenize(sql);
		
		boolean inFrom = false;
		
		for (int i = 0; i < tokens.length; i++) {
			String token = tokens[i].toLowerCase();
			
			if (token.equals("select")) {
				// SELECTの開始
				inFrom = false;
				continue;
			}
			
			if ("from".equals(token)) {
				// FROMの開始
				inFrom = true;
				continue;
			}
			
			if ("where".equals(token) || "group".equals(token) ||
				"having".equals(token) || "order".equals(token) ) {
				// FROMの終了
				inFrom = false;
				continue;
			}
			
			String prev1 = (i < 1) ? null : tokens[i - 1].toLowerCase();
			String prev2 = (i < 2) ? null : tokens[i - 2].toLowerCase();
			
			// Create
			if ("insert".equals(prev2) && "into".equals(prev1)) {
				getEntry(token).createCount++;
				continue;
			}
			
			// Update
			if ("update".equals(prev1)) {
				getEntry(token).updateCount++;
				continue;
			}
			
			// Delete
			if ("delete".equals(prev2) && "from".equals(prev1)) {
				getEntry(token).deleteCount++;
				continue;
			}
			
			// Delete (truncate table)
			if ("truncate".equals(prev2) &&	"table".equals(prev1)) {
				getEntry(token).deleteCount++;
				continue;
			}
			
			// Read
			if (inFrom) {
				if ("(".equals(token)) {
					// テーブル名の位置に括弧でサブクエリがくることがある
					continue;
				}
				
				if ("from".equals(prev1)) {
					getEntry(token).readCount++;
					continue;
				}
				
				if (",".equals(prev1)) {
					// さかのぼって最初に表れるのが「on」または「using」ならばJOINの条件である
					boolean isTable = true;
					for (int j = i - 1; j >= 0; j--) {
						String t = tokens[j].toLowerCase();
						if ("on".equals(t) || "using".equals(t)) {
							isTable = false;
							break;
						}
						if ("from".equals(t)) {
							break;
						}
					}
					
					if (isTable) {
						getEntry(token).readCount++;
					}
					continue;
				}
				
				if ("join".equals(prev1)) {
					getEntry(token).readCount++;
					continue;
				}
			}
			
			// TODO 相関サブクエリの外側のテーブルが"Read"の対象にならない
		}
	}
	
	/**
	 * 解析結果のリストを取得する。
	 * リストは、テーブル名の昇順でソートされる。
	 * @return
	 */
	public List<CRUDEntry> getEntryList() {
		
		List<CRUDEntry> result = 
			new ArrayList<CRUDEntry>(this.crudMap.values());
		
		Collections.sort(result);
		
		return result;
	}
	
	private CRUDEntry getEntry(String tableName) {
		CRUDEntry entry = this.crudMap.get(tableName);
		
		if (entry == null) {
			entry = new CRUDEntry(tableName);
			this.crudMap.put(tableName, entry);
		}
		
		return entry;
	}

	/**
	 * 1つのテーブルに関するCRUD情報。
	 */
	public static class CRUDEntry implements Comparable<CRUDEntry> {
		
		CRUDEntry(String tableName) {
			this.tableName = tableName;
		}
		
		private String tableName;
		private int createCount = 0;
		private int readCount = 0;
		private int updateCount = 0;
		private int deleteCount = 0;
		
		public String getTableName() {
			return tableName;
		}
		
		public int getCreateCount() {
			return createCount;
		}
		
		public int getReadCount() {
			return readCount;
		}
		
		public int getUpdateCount() {
			return updateCount;
		}
		
		public int getDeleteCount() {
			return deleteCount;
		}
		
		@Override
		public int hashCode() {
			return this.tableName.hashCode();
		}
		
		@Override
		public boolean equals(Object obj) {
			return obj != null && obj instanceof CRUDModel && 
				this.tableName.equals(((CRUDEntry) obj).tableName);
		}

		@Override
		public int compareTo(CRUDEntry o) {
			if (o == null) {
				return -1;
			}
			
			return this.tableName.compareTo(o.tableName);
		}
		
		@Override
		public String toString() {
			return new StringBuilder()
				.append(this.tableName)
				.append(":")
				.append(this.createCount)
				.append("/")
				.append(this.readCount)
				.append("/")
				.append(this.updateCount)
				.append("/")
				.append(this.deleteCount)
				.toString();
		}
	}
}
