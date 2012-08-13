package net.mikaboshi.jdbc.monitor.viewer;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.ComponentOrientation;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTabbedPane;
import javax.swing.JTable;
import javax.swing.JTextArea;
import javax.swing.JToolBar;
import javax.swing.SwingUtilities;
import javax.swing.table.TableModel;

import net.mikaboshi.gui.GuiUtils;
import net.mikaboshi.jdbc.SQLFormatter;
import net.mikaboshi.jdbc.monitor.LogEntry;
import net.mikaboshi.jdbc.monitor.M17N;
import net.mikaboshi.jdbc.monitor.SqlUtils;
import net.mikaboshi.jdbc.monitor.ViewerConfig;

import org.apache.commons.lang.StringUtils;

public class LogDetailFrame extends JFrame {

	private static final long serialVersionUID = 1L;
	private JPanel jContentPane = null;
	private JScrollPane sqlScrollPane = null;
	private JScrollPane infoScrollPane = null;
	private JTextArea sqlTextArea = null;
	private JTable infoTable = null;

	private LogEntry logEntry;
	private JPopupMenu sqlFormatPopupMenu = null;  //  @jve:decl-index=0:visual-constraint="545,234"

	private JdbcLogViewerFrame caller;
	private int row;
	private JPanel buttonPanel = null;
	private JButton previousButton = null;
	private JButton nextButton = null;
	private JSplitPane jSplitPane = null;
	private JTabbedPane jTabbedPane = null;
	private JPanel exceptionPanel = null;
	private JTextArea exceptionTextArea = null;
	private JPanel callStackPanel = null;
	private JTextArea callStackTextArea = null;
	private JScrollPane exceptionScrollPane = null;
	private JScrollPane callStackScrollPane = null;
	private JPanel sqlTabBodyPanel = null;
	private JToolBar sqlToolBar = null;
	private JButton executeSqlButton = null;
	private JButton connectConfigButton = null;
	public LogDetailFrame(JdbcLogViewerFrame caller, int row) {
		super();
		this.caller = caller;
		this.row = row;
		this.logEntry = this.caller.getLogTableModel().getLogEntry(row);
		initialize();
	}

	/**
	 * This method initializes this
	 *
	 * @return void
	 */
	private void initialize() {
		this.setSize(474, 540);
		this.setContentPane(getJContentPane());
		this.setTitle("LogDetailFrame.title");

		// 最初/最後の行の場合に、ボタンを使用不可にする
		getPreviousButton().setEnabled(this.row != 0);
		getNextButton().setEnabled(
				this.row != this.caller.getLogTableModel().getRowCount() - 1);
	}

	/**
	 * This method initializes jContentPane
	 *
	 * @return javax.swing.JPanel
	 */
	private JPanel getJContentPane() {
		if (jContentPane == null) {
			GridBagConstraints gridBagConstraints11 = new GridBagConstraints();
			gridBagConstraints11.fill = GridBagConstraints.BOTH;
			gridBagConstraints11.weighty = 1.0;
			gridBagConstraints11.weightx = 1.0;
			jContentPane = new JPanel();
			jContentPane.setLayout(new BorderLayout());
			jContentPane.add(getJTabbedPane(), BorderLayout.CENTER);
			jContentPane.add(getButtonPanel(), BorderLayout.SOUTH);

			// ESCで閉じる
			GuiUtils.closeByESC(this, jContentPane);
		}
		return jContentPane;
	}

	/**
	 * This method initializes sqlScrollPane
	 *
	 * @return javax.swing.JScrollPane
	 */
	private JScrollPane getSqlScrollPane() {
		if (sqlScrollPane == null) {
			sqlScrollPane = new JScrollPane();
			sqlScrollPane.setPreferredSize(new Dimension(3, 290));
			sqlScrollPane.setViewportView(getSqlTextArea());
		}
		return sqlScrollPane;
	}

	/**
	 * This method initializes infoScrollPane
	 *
	 * @return javax.swing.JScrollPane
	 */
	private JScrollPane getInfoScrollPane() {
		if (infoScrollPane == null) {
			infoScrollPane = new JScrollPane();
			infoScrollPane.setPreferredSize(new Dimension(3, 3));
			infoScrollPane.setViewportView(getInfoTable());
		}
		return infoScrollPane;
	}

	/**
	 * This method initializes sqlTextArea
	 *
	 * @return javax.swing.JTextArea
	 */
	private JTextArea getSqlTextArea() {
		if (sqlTextArea == null) {
			sqlTextArea = new JTextArea();

			if (ViewerConfig.getInstance().isDetailSqlFormat()) {
				sqlTextArea.setText(
						new SQLFormatter().format(this.logEntry.getSql()));
			} else {
				sqlTextArea.setText(SqlUtils.replaceCrLf(this.logEntry.getSql()));
			}

			getExecuteSqlButton().setEnabled(
					StringUtils.isNotBlank(sqlTextArea.getText()));

			sqlTextArea.setFont(new Font("Monospaced", Font.PLAIN, 12));
			sqlTextArea.setTabSize(4);

			sqlTextArea.addMouseListener(new java.awt.event.MouseAdapter() {
				// 右クリックしたときに、SQLを整形するか1行表示するか、ポップアップメニューで選択する
				public void mouseClicked(java.awt.event.MouseEvent e) {
					if (SwingUtilities.isRightMouseButton(e)) {
						getSqlFormatPopupMenu().show(e.getComponent(), e.getX(), e.getY());
					}
				}
			});

			sqlTextArea.addKeyListener(new KeyAdapter() {

				@Override
				public void keyPressed(KeyEvent e) {

					// Ctrl + PgDown : 次のログ
					// Ctrl + PgUp : 前のログ

					if (e.getModifiers() != KeyEvent.CONTAINER_EVENT_MASK) {
						return;
					}

					if (e.getKeyCode() == KeyEvent.VK_PAGE_DOWN) {
						row++;
						rewrite();
						e.consume();
					} else if (e.getKeyCode() == KeyEvent.VK_PAGE_UP) {
						row--;
						rewrite();
						e.consume();
					}
				}
			});
		}

		GuiUtils.setCeil(sqlTextArea);

		return sqlTextArea;
	}

	private void rewrite() {

		if (this.row < 0) {
			this.row = 0;
			return;
		}

		if (this.row >= this.caller.getLogTableModel().getRowCount()) {
			this.row = this.caller.getLogTableModel().getRowCount() - 1;
			return;
		}

		getPreviousButton().setEnabled(this.row != 0);
		getNextButton().setEnabled(
				this.row != this.caller.getLogTableModel().getRowCount() - 1);

		this.logEntry =
			this.caller.getLogTableModel().getLogEntry(this.row);

		if (ViewerConfig.getInstance().isDetailSqlFormat()) {
			sqlTextArea.setText(
					new SQLFormatter().format(this.logEntry.getSql()));
		} else {
			sqlTextArea.setText(SqlUtils.replaceCrLf(this.logEntry.getSql()));
		}

		getExecuteSqlButton().setEnabled(
				StringUtils.isNotBlank(sqlTextArea.getText()));

		exceptionTextArea.setText(this.logEntry.getException());
		callStackTextArea.setText(this.logEntry.getCallStack());

		int rowIndex = 0;
		final TableModel tableModel = this.infoTable.getModel();
		final int colIndex = 1;

		tableModel.setValueAt(
				this.logEntry.getDateTime(), rowIndex++, colIndex);
		tableModel.setValueAt(
				M17N.get(this.logEntry.getLogType().propertyName()), rowIndex++, colIndex);
		tableModel.setValueAt(
				this.logEntry.getThreadId(), rowIndex++, colIndex);
		tableModel.setValueAt(
				this.logEntry.getThreadName(), rowIndex++, colIndex);
		tableModel.setValueAt(
				this.logEntry.getConnectionId(), rowIndex++, colIndex);
		tableModel.setValueAt(
				this.logEntry.getStatementId(), rowIndex++, colIndex);
		tableModel.setValueAt(
				this.logEntry.getAffectedRows(), rowIndex++, colIndex);
		tableModel.setValueAt(
				this.logEntry.getElapsedTime(), rowIndex++, colIndex);
		tableModel.setValueAt(
				this.logEntry.getResult().getLable(), rowIndex++, colIndex);
		tableModel.setValueAt(
				this.logEntry.getAutoCommit().getLabel(), rowIndex++, colIndex);
		tableModel.setValueAt(
				this.logEntry.getTag(), rowIndex++, colIndex);

		this.caller.getLogTable().changeSelection(this.row, 0, false, false);

		GuiUtils.setCeil(sqlTextArea);
		GuiUtils.setCeil(exceptionTextArea);
		GuiUtils.setCeil(callStackTextArea);
	}

	/**
	 * This method initializes infoTable
	 *
	 * @return javax.swing.JTable
	 */
	private JTable getInfoTable() {
		if (infoTable == null) {

			Object[][] info = {
					{M17N.get("LogDetailFrame.time"), this.logEntry.getDateTime()},
					{M17N.get("LogDetailFrame.log_type"), M17N.get(this.logEntry.getLogType().propertyName())},
					{M17N.get("LogDetailFrame.thread_id"), this.logEntry.getThreadId()},
					{M17N.get("LogDetailFrame.thread_name"), this.logEntry.getThreadName()},
					{M17N.get("LogDetailFrame.connection_id"), this.logEntry.getConnectionId()},
					{M17N.get("LogDetailFrame.statement_id"), this.logEntry.getStatementId()},
					{M17N.get("LogDetailFrame.affected_rows"), this.logEntry.getAffectedRows()},
					{M17N.get("LogDetailFrame.elapsed_time"), this.logEntry.getElapsedTime()},
					{M17N.get("LogDetailFrame.result"), this.logEntry.getResult().getLable()},
					{M17N.get("LogDetailFrame.auto_commit"), this.logEntry.getAutoCommit().getLabel()},
					{M17N.get("LogDetailFrame.tag"), this.logEntry.getTag()},
			};

			infoTable = new JTable(info, new Object[]{"", ""});
			infoTable.setCellSelectionEnabled(false);
			infoTable.setSize(infoTable.getPreferredSize());
		}
		return infoTable;
	}

	/**
	 * This method initializes sqlFormatPopupMenu
	 *
	 * @return javax.swing.JPopupMenu
	 */
	private JPopupMenu getSqlFormatPopupMenu() {
		if (sqlFormatPopupMenu == null) {
			sqlFormatPopupMenu = new SqlFormatPopupMenu(getSqlTextArea());
		}
		return sqlFormatPopupMenu;
	}

	/**
	 * This method initializes buttonPanel
	 *
	 * @return javax.swing.JPanel
	 */
	private JPanel getButtonPanel() {
		if (buttonPanel == null) {
			GridBagConstraints gridBagConstraints4 = new GridBagConstraints();
			gridBagConstraints4.insets = new Insets(0, 10, 0, 0);
			GridBagConstraints gridBagConstraints2 = new GridBagConstraints();
			gridBagConstraints2.insets = new Insets(0, 0, 0, 10);
			buttonPanel = new JPanel();
			buttonPanel.setLayout(new GridBagLayout());
			buttonPanel.add(getPreviousButton(), gridBagConstraints2);
			buttonPanel.add(getNextButton(), gridBagConstraints4);
		}
		return buttonPanel;
	}

	/**
	 * This method initializes previousButton
	 *
	 * @return javax.swing.JButton
	 */
	private JButton getPreviousButton() {
		if (previousButton == null) {
			previousButton = new JButton();
			previousButton.setText("");
			previousButton.setIcon(new ImageIcon(getClass().getResource("/net/mikaboshi/jdbc/monitor/viewer/previous.png")));
			previousButton.setPreferredSize(new Dimension(24, 24));
			previousButton.setToolTipText("LogDetailFrame.button.previous");
			previousButton.addActionListener(new java.awt.event.ActionListener() {
				public void actionPerformed(java.awt.event.ActionEvent e) {
					row--;
					rewrite();
				}
			});
		}
		return previousButton;
	}

	/**
	 * This method initializes nextButton
	 *
	 * @return javax.swing.JButton
	 */
	private JButton getNextButton() {
		if (nextButton == null) {
			nextButton = new JButton();
			nextButton.setText("");
			nextButton.setPreferredSize(new Dimension(24, 24));
			nextButton.setIcon(new ImageIcon(getClass().getResource("/net/mikaboshi/jdbc/monitor/viewer/next.png")));
			nextButton.setToolTipText("LogDetailFrame.button.next");
			nextButton.addActionListener(new java.awt.event.ActionListener() {
				public void actionPerformed(java.awt.event.ActionEvent e) {
					row++;
					rewrite();
				}
			});
		}
		return nextButton;
	}

	/**
	 * This method initializes jSplitPane
	 *
	 * @return javax.swing.JSplitPane
	 */
	private JSplitPane getJSplitPane() {
		if (jSplitPane == null) {
			jSplitPane = new JSplitPane(JSplitPane.VERTICAL_SPLIT);
			jSplitPane.setTopComponent(getSqlScrollPane());
			jSplitPane.setBottomComponent(getInfoScrollPane());
		}
		return jSplitPane;
	}

	/**
	 * This method initializes jTabbedPane
	 *
	 * @return javax.swing.JTabbedPane
	 */
	private JTabbedPane getJTabbedPane() {
		if (jTabbedPane == null) {
			jTabbedPane = new JTabbedPane();
			jTabbedPane.addTab("LogDetailFrame.tab.sql", null, getSqlTabBodyPanel(), null);
			jTabbedPane.addTab("LogDetailFrame.tab.callstack", null, getCallStackPanel(), null);
			jTabbedPane.addTab("LogDetailFrame.tab.exception", null, getExceptionPanel(), null);
		}
		return jTabbedPane;
	}

	/**
	 * This method initializes exceptionPanel
	 *
	 * @return javax.swing.JPanel
	 */
	private JPanel getExceptionPanel() {
		if (exceptionPanel == null) {
			GridBagConstraints gridBagConstraints12 = new GridBagConstraints();
			gridBagConstraints12.fill = GridBagConstraints.BOTH;
			gridBagConstraints12.weighty = 1.0;
			gridBagConstraints12.weightx = 1.0;
			exceptionPanel = new JPanel();
			exceptionPanel.setLayout(new GridBagLayout());
			exceptionPanel.add(getExceptionScrollPane(), gridBagConstraints12);
		}
		return exceptionPanel;
	}

	/**
	 * This method initializes exceptionTextArea
	 *
	 * @return javax.swing.JTextArea
	 */
	private JTextArea getExceptionTextArea() {
		if (exceptionTextArea == null) {
			exceptionTextArea = new JTextArea();
			exceptionTextArea.setText(this.logEntry.getException());
			exceptionTextArea.setForeground(Color.RED);

			GuiUtils.setCeil(exceptionTextArea);
		}
		return exceptionTextArea;
	}

	/**
	 * This method initializes callStackPanel
	 *
	 * @return javax.swing.JPanel
	 */
	private JPanel getCallStackPanel() {
		if (callStackPanel == null) {
			GridBagConstraints gridBagConstraints = new GridBagConstraints();
			gridBagConstraints.fill = GridBagConstraints.BOTH;
			gridBagConstraints.weighty = 1.0;
			gridBagConstraints.weightx = 1.0;
			callStackPanel = new JPanel();
			callStackPanel.setLayout(new GridBagLayout());
			callStackPanel.add(getCallStackScrollPane(), gridBagConstraints);
		}
		return callStackPanel;
	}

	/**
	 * This method initializes callStackTextArea
	 *
	 * @return javax.swing.JTextArea
	 */
	private JTextArea getCallStackTextArea() {
		if (callStackTextArea == null) {
			callStackTextArea = new JTextArea();
			callStackTextArea.setText(this.logEntry.getCallStack());

			GuiUtils.setCeil(callStackTextArea);
		}
		return callStackTextArea;
	}

	/**
	 * This method initializes exceptionScrollPane
	 *
	 * @return javax.swing.JScrollPane
	 */
	private JScrollPane getExceptionScrollPane() {
		if (exceptionScrollPane == null) {
			exceptionScrollPane = new JScrollPane();
			exceptionScrollPane.setViewportView(getExceptionTextArea());
		}
		return exceptionScrollPane;
	}

	/**
	 * This method initializes callStackScrollPane
	 *
	 * @return javax.swing.JScrollPane
	 */
	private JScrollPane getCallStackScrollPane() {
		if (callStackScrollPane == null) {
			callStackScrollPane = new JScrollPane();
			callStackScrollPane.setViewportView(getCallStackTextArea());
		}
		return callStackScrollPane;
	}

	/**
	 * This method initializes sqlTabBodyPanel
	 *
	 * @return javax.swing.JPanel
	 */
	private JPanel getSqlTabBodyPanel() {
		if (sqlTabBodyPanel == null) {
			BorderLayout borderLayout = new BorderLayout();
			borderLayout.setVgap(0);
			sqlTabBodyPanel = new JPanel();
			sqlTabBodyPanel.setLayout(borderLayout);
			sqlTabBodyPanel.add(getSqlToolBar(), BorderLayout.NORTH);
			sqlTabBodyPanel.add(getJSplitPane(), BorderLayout.CENTER);
		}
		return sqlTabBodyPanel;
	}

	/**
	 * This method initializes sqlToolBar
	 *
	 * @return javax.swing.JToolBar
	 */
	private JToolBar getSqlToolBar() {
		if (sqlToolBar == null) {
			sqlToolBar = new JToolBar();
			sqlToolBar.setOrientation(JToolBar.HORIZONTAL);
			sqlToolBar.setComponentOrientation(ComponentOrientation.RIGHT_TO_LEFT);
			sqlToolBar.add(getConnectConfigButton());
			sqlToolBar.setFloatable(false);
			sqlToolBar.add(getExecuteSqlButton());
		}
		return sqlToolBar;
	}

	/**
	 * This method initializes executeSqlButton
	 *
	 * @return javax.swing.JButton
	 */
	private JButton getExecuteSqlButton() {
		if (executeSqlButton == null) {
			executeSqlButton = new JButton();
			executeSqlButton.setText("LogDetailFrame.toolbar.execute_sql");

			executeSqlButton.addActionListener(new java.awt.event.ActionListener() {
				public void actionPerformed(java.awt.event.ActionEvent e) {
					ViewerConfig.ConnectInfo connectInfo = ViewerConfig.getInstance().getConnectInfo();

					if (StringUtils.isBlank(connectInfo.getDriver()) ||
						StringUtils.isBlank(connectInfo.getUrl()) ) {

						JOptionPane.showMessageDialog(
								null,
								M17N.get("LogDetailFrame.no_connect_config"),
								"",
								JOptionPane.WARNING_MESSAGE);

						return;
					}

					JFrame frame = new SqlExecuteFrame(
							getSqlTextArea().getText());
					frame.setLocationRelativeTo(LogDetailFrame.this);
					frame.setVisible(true);
				}
			});
		}
		return executeSqlButton;
	}

	/**
	 * This method initializes connectConfigButton
	 *
	 * @return javax.swing.JButton
	 */
	private JButton getConnectConfigButton() {
		if (connectConfigButton == null) {
			connectConfigButton = new JButton();
			connectConfigButton.setText("LogDetailFrame.toolbar.connect_config");
			connectConfigButton.addActionListener(new java.awt.event.ActionListener() {
				public void actionPerformed(java.awt.event.ActionEvent e) {
					JDialog dialog = new ConnectConfigDialog(LogDetailFrame.this);
					dialog.setLocationRelativeTo(LogDetailFrame.this);
					dialog.setVisible(true);
				}
			});
		}
		return connectConfigButton;
	}

}  //  @jve:decl-index=0:visual-constraint="10,10"
