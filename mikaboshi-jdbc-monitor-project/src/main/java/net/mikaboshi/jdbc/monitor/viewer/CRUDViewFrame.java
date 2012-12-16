package net.mikaboshi.jdbc.monitor.viewer;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.FileDialog;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.SwingConstants;
import javax.swing.WindowConstants;
import javax.swing.table.DefaultTableCellRenderer;

import net.mikaboshi.csv.CSVStrategy;
import net.mikaboshi.csv.StandardCSVStrategy;
import net.mikaboshi.gui.GuiUtils;
import net.mikaboshi.jdbc.monitor.CRUDModel;
import net.mikaboshi.jdbc.monitor.LogEntry;
import net.mikaboshi.jdbc.monitor.M17N;
import net.mikaboshi.jdbc.monitor.CRUDModel.CRUDEntry;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * CRUD画面
 * @author Takuma Umezawa
 * @since 1.3.0
 */
public class CRUDViewFrame extends JFrame {

	private static final long serialVersionUID = 1L;
	private static Log logger = LogFactory.getLog(JdbcLogViewerFrame.class);  //  @jve:decl-index=0:
	private JPanel jContentPane = null;
	private JPanel buttonPanel = null;
	private JScrollPane tableScrollPane = null;
	private JTable crudTable = null;
	private JButton csvButton = null;
	private JButton closeButton = null;
	private String[][] tableData;
	private boolean isAllRow;
	
	private JTable logTable;
	private LogTableModel logTableModel;

	/**
	 */
	public CRUDViewFrame(JTable logTable, LogTableModel logTableModel, boolean isAllRow) {
		this.logTable = logTable;
		this.logTableModel = logTableModel;
		this.isAllRow = isAllRow;
		initialize();
	}

	/**
	 * This method initializes this
	 * 
	 * @return void
	 */
	private void initialize() {
		this.setSize(500, 300);
		this.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
		this.setTitle("CRUDViewFrame.title");
		this.setContentPane(getJContentPane());
	}

	/**
	 * This method initializes jContentPane
	 * 
	 * @return javax.swing.JPanel
	 */
	private JPanel getJContentPane() {
		if (jContentPane == null) {
			jContentPane = new JPanel();
			jContentPane.setLayout(new BorderLayout());
			jContentPane.add(getButtonPanel(), BorderLayout.SOUTH);
			jContentPane.add(getTableScrollPane(), BorderLayout.CENTER);
			
			// ESCで閉じる
			GuiUtils.closeByESC(this, jContentPane);
		}
		return jContentPane;
	}

	/**
	 * This method initializes buttonPanel	
	 * 	
	 * @return javax.swing.JPanel	
	 */
	private JPanel getButtonPanel() {
		if (buttonPanel == null) {
			GridBagConstraints gridBagConstraints1 = new GridBagConstraints();
			gridBagConstraints1.insets = new Insets(5, 10, 5, 10);
			gridBagConstraints1.ipadx = 0;
			gridBagConstraints1.gridwidth = 1;
			GridBagConstraints gridBagConstraints = new GridBagConstraints();
			gridBagConstraints.insets = new Insets(5, 10, 5, 10);
			gridBagConstraints.weightx = 0.0;
			gridBagConstraints.ipadx = 0;
			gridBagConstraints.gridwidth = 1;
			buttonPanel = new JPanel();
			buttonPanel.setLayout(new GridBagLayout());
			buttonPanel.add(getCsvButton(), gridBagConstraints);
			buttonPanel.add(getCloseButton(), gridBagConstraints1);
		}
		return buttonPanel;
	}

	/**
	 * This method initializes tableScrollPane	
	 * 	
	 * @return javax.swing.JScrollPane	
	 */
	private JScrollPane getTableScrollPane() {
		if (tableScrollPane == null) {
			tableScrollPane = new JScrollPane();
			tableScrollPane.setViewportView(getCrudTable());
		}
		return tableScrollPane;
	}

	/**
	 * This method initializes crudTable	
	 * 	
	 * @return javax.swing.JTable	
	 */
	private JTable getCrudTable() {
		if (crudTable == null) {
			String[] columnNames = {
					M17N.get("CRUDViewFrame.columnName.table"),
					M17N.get("CRUDViewFrame.columnName.create"),
					M17N.get("CRUDViewFrame.columnName.read"),
					M17N.get("CRUDViewFrame.columnName.update"),
					M17N.get("CRUDViewFrame.columnName.delete") };
			this.tableData = createTableData();
			
			crudTable = new JTable(this.tableData, columnNames);
			
			// CRUD列のセルを右寄せにする
			DefaultTableCellRenderer renderer = new DefaultTableCellRenderer();
			renderer.setHorizontalAlignment(SwingConstants.RIGHT);
			crudTable.getColumnModel().getColumn(1).setCellRenderer(renderer);
			crudTable.getColumnModel().getColumn(2).setCellRenderer(renderer);
			crudTable.getColumnModel().getColumn(3).setCellRenderer(renderer);
			crudTable.getColumnModel().getColumn(4).setCellRenderer(renderer);
		
			// テーブル名列の幅
			crudTable.getColumnModel().getColumn(0).setPreferredWidth(270);
			// シングルクリックで編集できないようにする
			crudTable.putClientProperty("JTable.autoStartsEdit", Boolean.FALSE);
		}
		return crudTable;
	}
	
	private String[][] createTableData() {
		
		CRUDModel crud = new CRUDModel();
		
		if (this.isAllRow) {
			// すべての行
			for (int row = 0; row < this.logTable.getRowCount(); row++) {
				LogEntry logEntry = this.logTableModel.getLogEntry(row);
				crud.add(logEntry.getSql());
			}
		} else {
			// 選択行のみ
			for (int row : this.logTable.getSelectedRows()) {
				LogEntry logEntry = this.logTableModel.getLogEntry(row);
				crud.add(logEntry.getSql());
			}
		}
		
		List<String[]> result = new ArrayList<String[]>();
		
		for (CRUDEntry entry : crud.getEntryList()) {
			result.add(new String[] {
					entry.getTableName(),
					Integer.toString(entry.getCreateCount()),
					Integer.toString(entry.getReadCount()),
					Integer.toString(entry.getUpdateCount()),
					Integer.toString(entry.getDeleteCount()) });
		}
		
		return result.toArray(new String[result.size()][2]);
	}

	/**
	 * This method initializes csvButton	
	 * 	
	 * @return javax.swing.JButton	
	 */
	private JButton getCsvButton() {
		if (csvButton == null) {
			csvButton = new JButton();
			csvButton.setText("CRUDViewFrame.csvButton");
			csvButton.setPreferredSize(new Dimension(120, 26));
			
			csvButton.addActionListener(new java.awt.event.ActionListener() {
				public void actionPerformed(java.awt.event.ActionEvent e) {
					File file = getExportFile();
					
					if (file == null) {
						return;
					}
					
					OutputStream os = null;
					PrintWriter writer = null;
						
					try {
						CSVStrategy csvStrategy = new StandardCSVStrategy();
						
						os = FileUtils.openOutputStream(file);
						writer = new PrintWriter(new OutputStreamWriter(os));
						
						String[] header = {
								M17N.get("CRUDViewFrame.columnName.table"),
								M17N.get("CRUDViewFrame.columnName.create"),
								M17N.get("CRUDViewFrame.columnName.read"),
								M17N.get("CRUDViewFrame.columnName.update"),
								M17N.get("CRUDViewFrame.columnName.delete") };
						csvStrategy.printLine(header, writer);
						
						for (String[] line : tableData) {
							csvStrategy.printLine(line, writer);
						}
						
						JOptionPane.showMessageDialog(
								null,
								M17N.get("CRUDViewFrame.csvExport.complete"),
								"",
								JOptionPane.INFORMATION_MESSAGE);
						
					} catch (IOException ex) {
						logger.error("CSV file export failure", ex);
						
						JOptionPane.showMessageDialog(
								null,
								M17N.get("CRUDViewFrame.csvExport.faulure"),
								"",
								JOptionPane.ERROR_MESSAGE);
						
					} finally {
						IOUtils.closeQuietly(writer);
						IOUtils.closeQuietly(os);
					}
				} 
			});
		}
		return csvButton;
	}
	
	/**
	 * CSV出力先のファイルを取得する。
	 * @param parent
	 * @return
	 */
	private File getExportFile() {
		FileDialog fileDialog = new FileDialog(
				this, 
				M17N.get("CRUDViewFrame.csvExport.prompt"),
				FileDialog.SAVE);
		
		fileDialog.setVisible(true);
		
		String file = fileDialog.getFile();
		String dir = fileDialog.getDirectory();
		fileDialog.dispose();
		
		if (file == null) {
			return null;
		}
		
		return new File(dir + file);
	}

	/**
	 * This method initializes closeButton	
	 * 	
	 * @return javax.swing.JButton	
	 */
	private JButton getCloseButton() {
		if (closeButton == null) {
			closeButton = new JButton();
			closeButton.setText("button.close");
			closeButton.setPreferredSize(new Dimension(120, 26));
			closeButton.addActionListener(new java.awt.event.ActionListener() {
				public void actionPerformed(java.awt.event.ActionEvent e) {
					dispose();
				}
			});
		}
		return closeButton;
	}

}  //  @jve:decl-index=0:visual-constraint="10,10"
