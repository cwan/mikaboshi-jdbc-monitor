package net.mikaboshi.jdbc.monitor.viewer;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.sql.Connection;
import java.sql.Driver;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

import javax.swing.AbstractAction;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTable;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.KeyStroke;
import javax.swing.WindowConstants;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.TableModel;

import net.mikaboshi.gui.GuiUtils;
import net.mikaboshi.jdbc.DbUtils;
import net.mikaboshi.jdbc.monitor.M17N;
import net.mikaboshi.jdbc.monitor.SqlUtils;
import net.mikaboshi.jdbc.monitor.ViewerConfig;
import net.mikaboshi.util.ThreadSafeUtils;

import org.apache.commons.lang.exception.ExceptionUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * SQL実行ダイアログ
 * @author Takuma Umezawa
 * @since 1.3.0
 * @version 1.4.3
 */
public class SqlExecuteFrame extends JFrame {

	private static final long serialVersionUID = 1L;
	@SuppressWarnings("unused")
	private static Log logger = LogFactory.getLog(SqlExecuteFrame.class);  //  @jve:decl-index=0:
	private JPanel jContentPane = null;
	private JPanel bodyPanel = null;
	private JPanel searchPanel = null;
	private JSplitPane jSplitPane = null;
	private JScrollPane consoleScrollPane = null;
	private JTextArea consoleTextArea = null;
	private JScrollPane recordScrollPane = null;
	private JTable recordTable = null;
	private String sql = null;
	private JTextField searchWordTextField = null;
	private JLabel searchLabel = null;
	private JButton searchForwardButton = null;
	private JButton searchBackwardButton = null;
	private JLabel searchResultLabel = null;
	/**
	 */
	public SqlExecuteFrame(String sql) {
		this.sql = sql;
		initialize();
	}

	/**
	 * This method initializes this
	 *
	 * @return void
	 */
	private void initialize() {
		this.setSize(600, 400);
		this.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
		this.setTitle("SqlExecuteFrame.title");
		this.setContentPane(getJContentPane());

		// テキストエリアの先頭にフォーカス
		GuiUtils.setCeil(getConsoleTextArea());
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
			jContentPane.add(getBodyPanel(), BorderLayout.CENTER);
			jContentPane.add(getSearchPanel(), BorderLayout.SOUTH);

			// ESCで閉じる
			GuiUtils.closeByESC(this, jContentPane);
		}
		return jContentPane;
	}

	/**
	 * This method initializes bodyPanel
	 *
	 * @return javax.swing.JPanel
	 */
	private JPanel getBodyPanel() {
		if (bodyPanel == null) {
			GridBagConstraints gridBagConstraints1 = new GridBagConstraints();
			gridBagConstraints1.fill = GridBagConstraints.BOTH;
			gridBagConstraints1.weighty = 1.0;
			gridBagConstraints1.weightx = 1.0;
			bodyPanel = new JPanel();
			bodyPanel.setLayout(new GridBagLayout());
			bodyPanel.add(getJSplitPane(), gridBagConstraints1);
		}
		return bodyPanel;
	}

	/**
	 * This method initializes searchPanel
	 *
	 * @return javax.swing.JPanel
	 */
	private JPanel getSearchPanel() {
		if (searchPanel == null) {
			searchResultLabel = new JLabel();
			searchResultLabel.setText("");
			FlowLayout flowLayout = new FlowLayout();
			flowLayout.setAlignment(java.awt.FlowLayout.LEFT);
			flowLayout.setVgap(5);
			flowLayout.setHgap(5);
			searchLabel = new JLabel();
			searchLabel.setText("SqlExecuteFrame.searchLabel");
			searchPanel = new JPanel();
			searchPanel.setLayout(flowLayout);
			searchPanel.add(searchLabel, null);
			searchPanel.add(getSearchWordTextField(), null);
			searchPanel.add(getSearchForwardButton(), null);
			searchPanel.add(getSearchBackwardButton(), null);
			searchPanel.add(searchResultLabel, null);
		}
		return searchPanel;
	}

	/**
	 * This method initializes jSplitPane
	 *
	 * @return javax.swing.JSplitPane
	 */
	private JSplitPane getJSplitPane() {
		if (jSplitPane == null) {
			jSplitPane = new JSplitPane();
			jSplitPane.setOrientation(JSplitPane.VERTICAL_SPLIT);
			jSplitPane.setBottomComponent(getRecordScrollPane());
			jSplitPane.setTopComponent(getConsoleScrollPane());
		}
		return jSplitPane;
	}

	/**
	 * This method initializes consoleScrollPane
	 *
	 * @return javax.swing.JScrollPane
	 */
	private JScrollPane getConsoleScrollPane() {
		if (consoleScrollPane == null) {
			consoleScrollPane = new JScrollPane();
			consoleScrollPane.setPreferredSize(new Dimension(403, 73));
			consoleScrollPane.setViewportView(getConsoleTextArea());
		}
		return consoleScrollPane;
	}

	/**
	 * This method initializes consoleTextArea
	 *
	 * @return javax.swing.JTextArea
	 */
	private JTextArea getConsoleTextArea() {
		if (consoleTextArea == null) {
			consoleTextArea = new JTextArea();
			consoleTextArea.setEditable(false);
			consoleTextArea.setSize(new Dimension(564, 70));
			consoleTextArea.setPreferredSize(new Dimension(400, 300));
		}
		return consoleTextArea;
	}

	/**
	 * This method initializes recordScrollPane
	 *
	 * @return javax.swing.JScrollPane
	 */
	private JScrollPane getRecordScrollPane() {
		if (recordScrollPane == null) {
			recordScrollPane = new JScrollPane();
			recordScrollPane.setPreferredSize(new Dimension(3, 200));
			recordScrollPane.setViewportView(getRecordTable());
		}
		return recordScrollPane;
	}

	/**
	 * This method initializes recordTable
	 *
	 * @return javax.swing.JTable
	 */
	private JTable getRecordTable() {
		if (recordTable == null) {
			recordTable = createRecordTable();

			// 横スクロールも有効にする
			recordTable.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
			recordTable.setCellSelectionEnabled(true);

			// 検索一致セルに色をつける
			recordTable.setDefaultRenderer(
					Object.class, new ColoringTableCellRenderer());

			// ヘッダカラムで列幅調整
			GuiUtils.adjustHeader(recordTable);
		}
		return recordTable;
	}

	@SuppressWarnings("unchecked")
	private JTable createRecordTable() {

		ViewerConfig.ConnectInfo connectInfo = ViewerConfig.getInstance().getConnectInfo();

		Class<Driver> driverClass = null;

		try {
			driverClass = (Class<Driver>) Class.forName(connectInfo.getDriver());
		} catch (ClassNotFoundException e) {
			getConsoleTextArea().setText(ExceptionUtils.getFullStackTrace(e));
			getConsoleTextArea().setForeground(Color.RED);
			return new JTable();
		}

		Driver driver = null;
		try {
			driver = (Driver) driverClass.newInstance();
		} catch (Exception e) {
			getConsoleTextArea().setText(ExceptionUtils.getFullStackTrace(e));
			getConsoleTextArea().setForeground(Color.RED);
			return new JTable();
		}

		Properties info = new Properties();
		info.setProperty("user", connectInfo.getUser());
		info.setProperty("password", connectInfo.getPassword());

		Connection connection = null;
		Statement statement = null;
		ResultSet resultSet = null;

		try {
			connection = driver.connect(connectInfo.getUrl(), info);
			connection.setAutoCommit(false);

			statement = connection.createStatement();

			long startTime = System.nanoTime();

			resultSet = statement.executeQuery(this.sql);

			long finishTime = System.nanoTime();

			// ヘッダをメタデータから取得
			ResultSetMetaData meta = resultSet.getMetaData();

			int columnCount = meta.getColumnCount();

			String[] header = new String[columnCount];

			for (int i = 0; i < columnCount; i++) {
				header[i] = meta.getColumnName(i + 1);
			}

			List<String[]> list = new ArrayList<String[]>();

			boolean limitOver = false;
			int limit = ViewerConfig.getInstance().getLimitExecuteSqlRows();

			while (resultSet.next()) {

				if (list.size() >= limit) {
					limitOver = true;
					break;
				}

				String[] rowData = new String[columnCount];

				for (int i = 0; i < columnCount; i++) {
					rowData[i] = SqlUtils.toLiteral(resultSet.getObject(i + 1), false);
				}

				list.add(rowData);
			}

			StringBuilder message = new StringBuilder();

			if (limitOver) {
				message.append(
						M17N.get("SqlExecuteFrame.result_message.hit_over", limit + 1));
			} else {
				message.append(
						M17N.get("SqlExecuteFrame.result_message.hit", list.size()));
			}

			message.append("\n");

			message.append(
					ThreadSafeUtils.formatNumber(
							(finishTime - startTime), "#,##0"));
			message.append(" nano sec.");

			getConsoleTextArea().setText(message.toString());

			return new JTable(
					list.toArray(new String[list.size()][columnCount]),
					header);

		} catch (SQLException e) {
			getConsoleTextArea().setText(ExceptionUtils.getFullStackTrace(e));
			getConsoleTextArea().setForeground(Color.RED);
			return new JTable();
		} finally {
			DbUtils.closeQuietly(resultSet);
			DbUtils.closeQuietly(statement);
			DbUtils.rollbackQuietly(connection);
			DbUtils.closeQuietly(connection);
		}

	}

	/**
	 * This method initializes searchWordTextField
	 *
	 * @return javax.swing.JTextField
	 */
	private JTextField getSearchWordTextField() {
		if (searchWordTextField == null) {
			searchWordTextField = new JTextField();
			searchWordTextField.setPreferredSize(new Dimension(120, 20));
			searchWordTextField.setToolTipText("SqlExecuteFrame.searchWordTextField.tooktip");

			searchWordTextField.addActionListener(new ActionListener() {

				@Override
				public void actionPerformed(ActionEvent e) {

					// Enterキー押下で次を探す

					String searchWord = getSearchWordTextField().getText();

					if (searchWord.length() != 0) {
						searchForward();
					} else {
						searchResultLabel.setText("");
					}

					recordTable.repaint();
					searchResultLabel.repaint();
				}
			});

			searchWordTextField.addKeyListener(new KeyAdapter() {

				@Override
				public void keyReleased(KeyEvent e) {

					String searchWord = getSearchWordTextField().getText();

					if (searchWord.length() != 0) {
						// 現在選択セルがマッチしない場合は次を探す
						int col = recordTable.getSelectedColumn();
						int row = recordTable.getSelectedRow();

						if (col < 0 || row < 0) {
							col = 0;
							row = 0;
						}

						String selectedValue = recordTable.getValueAt(row, col).toString().toLowerCase();

						if (!selectedValue.contains(searchWord.toLowerCase())) {
							searchForward();
						} else {
							searchResultLabel.setText("");
						}
					} else {
						searchResultLabel.setText("");
					}

					recordTable.repaint();
					searchResultLabel.repaint();
				}
			});

			// Ctrl+Fで検索テキストにフォーカスを合わせる
			searchWordTextField.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW)
					.put(KeyStroke.getKeyStroke(KeyEvent.VK_F, KeyEvent.CTRL_MASK), "f");

			searchWordTextField.getActionMap().put("f", new AbstractAction() {
				private static final long serialVersionUID = 1L;

				public void actionPerformed(ActionEvent e) {
					searchWordTextField.requestFocus();
				}
			});
		}
		return searchWordTextField;
	}

	/**
	 * 検索ワードに一致するセルに色をつけるDefaultTableCellRenderer
	 */
	private class ColoringTableCellRenderer extends DefaultTableCellRenderer {

		private static final long serialVersionUID = 1L;

		public ColoringTableCellRenderer() {
			super();
		}

		@Override
		public Component getTableCellRendererComponent(
				JTable table,
				Object value,
				boolean isSelected,
				boolean hasFocus,
				int row,
				int column) {

			super.getTableCellRendererComponent(
					table, value, isSelected, hasFocus,	row, column);

			if (isSelected) {
				// こうしないと、rowSelectionAllowedやselectionModeが無効になる
				setForeground(table.getSelectionForeground());
				setBackground(table.getSelectionBackground());
			} else {
				String searchWord = getSearchWordTextField().getText();

				if (searchWord.length() == 0) {
					setBackground(Color.WHITE);
					return this;
				}

				if (value != null && value.toString().toLowerCase()
										.contains(searchWord.toLowerCase())) {
					setBackground(Color.YELLOW);
				} else {
					setBackground(Color.WHITE);
				}
			}

			return this;
		}
	}

	/**
	 * This method initializes searchForwardButton
	 *
	 * @return javax.swing.JButton
	 */
	private JButton getSearchForwardButton() {
		if (searchForwardButton == null) {
			searchForwardButton = new JButton();
			searchForwardButton.setText("SqlExecuteFrame.searchForwardButton");
			searchForwardButton.setMnemonic(KeyEvent.VK_N);

			searchForwardButton.addActionListener(new java.awt.event.ActionListener() {
				public void actionPerformed(java.awt.event.ActionEvent e) {
					searchForward();
					recordTable.repaint();
					searchResultLabel.repaint();
				}
			});

		}
		return searchForwardButton;
	}

	/**
	 * This method initializes searchBackwardButton
	 *
	 * @return javax.swing.JButton
	 */
	private JButton getSearchBackwardButton() {
		if (searchBackwardButton == null) {
			searchBackwardButton = new JButton();
			searchBackwardButton.setText("SqlExecuteFrame.searchBackwardButton");
			searchBackwardButton.setMnemonic(KeyEvent.VK_P);
			searchBackwardButton.addActionListener(new java.awt.event.ActionListener() {
				public void actionPerformed(java.awt.event.ActionEvent e) {
					searchBackward();
					recordTable.repaint();
					searchResultLabel.repaint();
				}
			});
		}
		return searchBackwardButton;
	}

	/**
	 * 下方向に検索してマッチするセルを選択する
	 */
	private void searchForward() {

		TableModel model = recordTable.getModel();
		String searchWord = getSearchWordTextField().getText().toLowerCase();

		if (model.getColumnCount() == 0 ||
			model.getRowCount() == 0 ||
			searchWord.length() == 0) {

			searchResultLabel.setText("");
			return;
		}

		int startCol = recordTable.getSelectedColumn();
		int startRow = recordTable.getSelectedRow();

		if (startCol < 0 || startRow < 0) {
			startCol = model.getColumnCount() - 1;
			startRow = model.getRowCount() - 1;
		}

		// 選択位置の次から検索
		for (int row = startRow; row < model.getRowCount(); row++) {
			int firstCol = row == startRow ? startCol + 1 : 0;

			for (int col = firstCol; col < model.getColumnCount(); col++) {
				if (model.getValueAt(row, col).toString().toLowerCase().contains(searchWord)) {
					recordTable.changeSelection(row, col, false, false);
					searchResultLabel.setText("");
					return;
				}
			}
		}

		// 先頭に戻って検索
		for (int row = 0; row <= startRow; row++) {
			for (int col = 0; col < model.getColumnCount(); col++) {
				if (model.getValueAt(row, col).toString().toLowerCase().contains(searchWord)) {
					recordTable.changeSelection(row, col, false, false);
					searchResultLabel.setText("");
					return;
				}

				if (row == startRow && col == startCol) {
					searchResultLabel.setText("SqlExecuteFrame.search.not_found");
					return;
				}
			}
		}

	}

	/**
	 * 上方向に検索してマッチするセルを選択する
	 */
	private void searchBackward() {

		TableModel model = recordTable.getModel();
		String searchWord = getSearchWordTextField().getText().toLowerCase();

		if (model.getColumnCount() == 0 ||
			model.getRowCount() == 0 ||
			searchWord.length() == 0) {

			searchResultLabel.setText("");
			return;
		}

		int startCol = recordTable.getSelectedColumn();
		int startRow = recordTable.getSelectedRow();

		if (startCol < 0 || startRow < 0) {
			startCol = 0;
			startRow = 0;
		}

		// 選択位置の次から検索
		for (int row = startRow; row >= 0; row--) {
			int firstCol = row == startRow ? startCol - 1 : model.getColumnCount() - 1;

			for (int col = firstCol; col >= 0; col--) {
				if (model.getValueAt(row, col).toString().toLowerCase().contains(searchWord)) {
					recordTable.changeSelection(row, col, false, false);
					searchResultLabel.setText("");
					return;
				}
			}
		}

		// 末尾に戻って検索
		for (int row = model.getRowCount() - 1; row >= startRow; row--) {
			for (int col = model.getColumnCount() - 1; col >= 0; col--) {
				if (model.getValueAt(row, col).toString().toLowerCase().contains(searchWord)) {
					recordTable.changeSelection(row, col, false, false);
					searchResultLabel.setText("");
					return;
				}

				if (row == startRow && col == startCol) {
					searchResultLabel.setText("SqlExecuteFrame.search.not_found");
					return;
				}
			}
		}

	}

}  //  @jve:decl-index=0:visual-constraint="10,10"
