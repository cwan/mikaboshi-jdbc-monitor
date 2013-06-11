package net.mikaboshi.jdbc.monitor.viewer;

import static javax.swing.JOptionPane.*;
import static net.mikaboshi.jdbc.monitor.viewer.LogTableColumn.*;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import java.awt.dnd.DnDConstants;
import java.awt.dnd.DropTarget;
import java.awt.dnd.DropTargetAdapter;
import java.awt.dnd.DropTargetDropEvent;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.InputEvent;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Set;
import java.util.regex.PatternSyntaxException;

import javax.swing.BorderFactory;
import javax.swing.ButtonGroup;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JCheckBoxMenuItem;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JScrollBar;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTable;
import javax.swing.JTextArea;
import javax.swing.JToggleButton;
import javax.swing.KeyStroke;
import javax.swing.SwingUtilities;
import javax.swing.SwingWorker;
import javax.swing.UIManager;
import javax.swing.event.MouseInputAdapter;
import javax.swing.table.DefaultTableCellRenderer;

import net.mikaboshi.gui.GuiUtils;
import net.mikaboshi.jdbc.monitor.LogEntry;
import net.mikaboshi.jdbc.monitor.LogFileAccessor;
import net.mikaboshi.jdbc.monitor.M17N;
import net.mikaboshi.jdbc.monitor.Result;
import net.mikaboshi.jdbc.monitor.SqlUtils;
import net.mikaboshi.jdbc.monitor.ViewerConfig;
import net.mikaboshi.jdbc.monitor.ViewerConfig.Filter;
import net.mikaboshi.jdbc.monitor.ViewerConfig.FormatType;
import net.mikaboshi.util.ThreadSafeUtils;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * ログビューアフレーム
 *
 * @version 1.4.3
 */
public class JdbcLogViewerFrame extends JFrame implements LogEntryProvider {

	private static Log systemLogger = LogFactory.getLog(JdbcLogViewerFrame.class);  //  @jve:decl-index=0:

	private static final long serialVersionUID = 1L;

	private JPanel jContentPane1 = null;
	private JSplitPane mainSplitPane = null;
	private JPanel buttonsPanel = null;
	private JPanel tablePanel = null;
	private JMenuBar jJMenuBar = null;
	private JMenu fileMenu = null;
	private JMenuItem openFileMenuItem = null;
	private JMenuItem reopenMenuItem = null;
	private JMenuItem exportMenuItem = null;
	private JMenuItem exitMenuItem = null;
	private JButton clearButton = null;
	private JScrollPane logTableScrollPane = null;
	private JTable logTable = null;
	private JMenu viewMenu = null;
	private JCheckBoxMenuItem timeCheckBoxMenuItem = null;
	private JCheckBoxMenuItem logTypeCheckBoxMenuItem = null;
	private JCheckBoxMenuItem threadNameCheckBoxMenuItem = null;
	private JCheckBoxMenuItem connectionIdCheckBoxMenuItem = null;
	private JCheckBoxMenuItem statementIdCheckBoxMenuItem = null;
	private JCheckBoxMenuItem sqlCheckBoxMenuItem = null;
	private JCheckBoxMenuItem affectedRowsCheckBoxMenuItem = null;
	private JCheckBoxMenuItem elapsedTimeCheckBoxMenuItem = null;
	private JCheckBoxMenuItem resultCheckBoxMenuItem = null;
	private JCheckBoxMenuItem autoCommitCheckBoxMenuItem = null;
	private JCheckBoxMenuItem tagCheckBoxMenuItem = null;
	private JPopupMenu headerPopupMenu = null;
	private JMenuItem hideColumnMenuItem = null;
	private JPopupMenu selectSingleRowPopupMenu = null;
	private JMenuItem clearBeforeMenuItem = null;
	private JMenuItem clearAfterMenuItem = null;
	private JMenuItem chooseThreadNameMenuItem = null;
	private JMenuItem clearThreadNameFilterMenuItem = null;
	private JMenuItem chooseConnectionIdMenuItem = null;
	private JMenuItem clearConnectionIdFilterMenuItem = null;
	private JMenuItem chooseStatementIdMenuItem = null;
	private JMenuItem clearStatementIdFilterMenuItem = null;
	private JPopupMenu selectMultiRowsPopupMenu = null;
	private JMenuItem clearSelectedRowsMenuItem = null;
	private JMenuItem clearMultiSelectedRowsMenuItem = null;
	private JMenuItem clearExceptSelectedRowsMenuItem = null;
	private JMenuItem clearExceptMultiSelectedRowsMenuItem = null;
	private JMenu toolMenu = null;
	private JMenuItem preferencesMenuItem = null;
	private JButton filterButton = null;
	private JMenuItem sortElapsedTimeMenuItem = null;
	private JPanel statusBarPanel = null;
	private JLabel selectedRowsCountLabel = null;
	private JLabel rowsCountSeparatorLabel1 = null;
	private JLabel allRowsCountLabel = null;
	private JLabel lastLogTimeLabel = null;
	private JPanel lastLogTimePanel = null;
	private JPanel rowsCountPanel = null;
	private JLabel visibleRowsCountLabel = null;
	private JLabel rowsCountSeparatorLabel2 = null;
	private JCheckBoxMenuItem statusBarCheckBoxMenuItem = null;
	private SearchDialog searchDialog = null;
	private JLabel lastLogTimeNameLabel = null;
	private JLabel rowsCountLabel = null;
	private JToggleButton startReadToggleButton = null;
	private JToggleButton suspendToggleButton = null;
	private JCheckBoxMenuItem autoScrollCheckBoxMenuItem = null;
	private JButton reopenButton = null;
	private JMenu searchMenu = null;
	private JMenuItem openSearchDialogMenuItem = null;
	private JMenuItem searchForwardMenuItem = null;
	private JMenuItem searhBackMenuItem = null;
	private JMenuItem versionInfoMenuItem = null;
	private JMenuItem crudMenuItem = null;
	private JMenuItem detectUnclosedConnectionMenuItem = null;
	private JMenuItem detectUnclosedStatementMenuItem = null;
	private JMenuItem crudContextMenuItem = null;
	private JMenuItem crudMultiContextMenuItem = null;
	private JPanel markPanel = null;
	private JLabel markLabel = null;
	private JPanel totalElapsedTimePanel = null;
	private JLabel totalElapsedTimeLabel = null;
	private JTextArea sqlPreviewTextArea = null;
	private JScrollPane sqlPreviewScrollPane = null;
	private JMenuItem loggerConfigMenuItem = null; //  @jve:decl-index=0:
	private JPopupMenu sqlFormatPopupMenu = null;
	private JPanel logFileOpenPanel = null;

	private String searchWord;
	private boolean searchAndMark;
	private String searchPattern;  //  @jve:decl-index=0:
	private boolean searchCirculating;
	private boolean searchCaseSensitive;

	/**
	 * クリックされたヘッダの列番号（非表示状態でも固定）
	 */
	private int clickedHeaderColumnIndex;

	/**
	 * This method initializes jContentPane1
	 *
	 * @return javax.swing.JPanel
	 */
	private JPanel getJContentPane1() {
		if (jContentPane1 == null) {
			rowsCountSeparatorLabel1 = new JLabel();
			rowsCountSeparatorLabel1.setText("/");
			jContentPane1 = new JPanel();
			jContentPane1.setLayout(new BorderLayout());
			jContentPane1.add(getButtonsPanel(), BorderLayout.NORTH);
			jContentPane1.add(getLogFileOpenPanel(), BorderLayout.CENTER);
			jContentPane1.add(getStatusBarPanel(), BorderLayout.SOUTH);
			jContentPane1.setDoubleBuffered(true);
		}
		return jContentPane1;
	}

	/**
	 * This method initializes logFileOpenPanel
	 *
	 * @return javax.swing.JPanel
	 */
	private JPanel getLogFileOpenPanel() {
		if (logFileOpenPanel == null) {
			GridBagConstraints gridBagConstraints = new GridBagConstraints();
			gridBagConstraints.insets = new Insets(0, 0, 0, 0);
			gridBagConstraints.gridy = 0;
			gridBagConstraints.anchor = GridBagConstraints.CENTER;
			gridBagConstraints.gridx = 0;
			JLabel dropLogFileHereLabel = new JLabel();
			dropLogFileHereLabel.setText("JdbcLogViewerFrame.drop_log_file_here");
			dropLogFileHereLabel.setName("dropFileHereLabel");
			logFileOpenPanel = new JPanel();
			logFileOpenPanel.setLayout(new GridBagLayout());
			logFileOpenPanel.setName("fileOpenPanel");
			logFileOpenPanel.add(dropLogFileHereLabel, gridBagConstraints);
		}
		return logFileOpenPanel;
	}

	/**
	 * This method initializes mainSplitPane
	 *
	 * @return javax.swing.JSplitPane
	 */
	private JSplitPane getMainSplitPane() {
		if (mainSplitPane == null) {
			mainSplitPane = new JSplitPane();
			mainSplitPane.setOrientation(JSplitPane.VERTICAL_SPLIT);
			mainSplitPane.setName("mainSplitPane");
			mainSplitPane.setOneTouchExpandable(true);
			mainSplitPane.setTopComponent(getTablePanel());
			mainSplitPane.setBottomComponent(getSqlPreviewScrollPane());
			mainSplitPane.setDividerLocation((int) (getHeight() * 0.5d));
		}
		return mainSplitPane;
	}

	private JTextArea getSqlPreviewTextArea() {
		if (sqlPreviewTextArea == null) {
			sqlPreviewTextArea = new JTextArea();
			sqlPreviewTextArea.setFont(new Font(Font.SANS_SERIF, Font.PLAIN, 14));
			sqlPreviewTextArea.setTabSize(4);
			sqlPreviewTextArea.setEditable(false);
			sqlPreviewTextArea.setMargin(new Insets(10, 10, 10, 10));

			sqlPreviewTextArea.addMouseListener(new java.awt.event.MouseAdapter() {
				// 右クリックしたときに、SQLの整形方法をポップアップメニューで選択する
				public void mouseClicked(java.awt.event.MouseEvent e) {
					if (SwingUtilities.isRightMouseButton(e)) {
						getSqlFormatPopupMenu().show(e.getComponent(), e.getX(), e.getY());
					}
				}
			});
		}
		return sqlPreviewTextArea;
	}

	/**
	 * This method initializes sqlFormatPopupMenu
	 *
	 * @return javax.swing.JPopupMenu
	 */
	private JPopupMenu getSqlFormatPopupMenu() {
		if (sqlFormatPopupMenu == null) {
			sqlFormatPopupMenu = new SqlFormatPopupMenu(getSqlPreviewTextArea(), this);
		}
		return sqlFormatPopupMenu;
	}

	private JScrollPane getSqlPreviewScrollPane() {
		if (sqlPreviewScrollPane == null) {
			sqlPreviewScrollPane = new JScrollPane();
			sqlPreviewScrollPane.setViewportView(getSqlPreviewTextArea());
		}
		return sqlPreviewScrollPane;
	}

	/**
	 * This method initializes buttonsPanel
	 *
	 * @return javax.swing.JPanel
	 */
	private JPanel getButtonsPanel() {
		if (buttonsPanel == null) {
			FlowLayout flowLayout = new FlowLayout();
			flowLayout.setAlignment(java.awt.FlowLayout.LEFT);
			buttonsPanel = new JPanel();
			buttonsPanel.setLayout(flowLayout);
			buttonsPanel.add(getStartReadToggleButton(), null);
			buttonsPanel.add(getSuspendToggleButton(), null);
			buttonsPanel.add(getFilterButton(), null);
			buttonsPanel.add(getClearButton(), null);
			buttonsPanel.add(getReopenButton(), null);

			ButtonGroup switchReadGrp = new ButtonGroup();
			switchReadGrp.add(getStartReadToggleButton());
			switchReadGrp.add(getSuspendToggleButton());
		}
		return buttonsPanel;
	}

	/**
	 * This method initializes tablePanel
	 *
	 * @return javax.swing.JPanel
	 */
	private JPanel getTablePanel() {
		if (tablePanel == null) {
			GridBagConstraints gridBagConstraints = new GridBagConstraints();
			gridBagConstraints.gridx = 0;
			gridBagConstraints.gridy = 0;
			tablePanel = new JPanel();
			tablePanel.setLayout(new BorderLayout());
			tablePanel.add(getLogTableScrollPane(), BorderLayout.CENTER);
		}
		return tablePanel;
	}

	/**
	 * This method initializes jJMenuBar
	 *
	 * @return javax.swing.JMenuBar
	 */
	private JMenuBar getJJMenuBar() {
		if (jJMenuBar == null) {
			jJMenuBar = new JMenuBar();
			jJMenuBar.add(getFileMenu());
			jJMenuBar.add(getViewMenu());
			jJMenuBar.add(getSearchMenu());
			jJMenuBar.add(getToolMenu());
		}
		return jJMenuBar;
	}

	/**
	 * This method initializes fileMenu
	 *
	 * @return javax.swing.JMenu
	 */
	private JMenu getFileMenu() {
		if (fileMenu == null) {
			fileMenu = new JMenu();
			fileMenu.setText("JdbcLogViewerFrame.menu.file");
			fileMenu.setMnemonic(KeyEvent.VK_F);
			fileMenu.add(getOpenFileMenuItem());
			fileMenu.add(getReopenMenuItem());
			fileMenu.addSeparator();
			fileMenu.add(getExportMenuItem());
			fileMenu.addSeparator();
			fileMenu.add(getExitMenuItem());
		}
		return fileMenu;
	}

	/**
	 * 読み込む対象のログファイル。
	 */
	private File logFile;  //  @jve:decl-index=0:

	/**
	 * This method initializes openFileMenuItem
	 *
	 * @return javax.swing.JMenuItem
	 */
	private JMenuItem getOpenFileMenuItem() {
		if (openFileMenuItem == null) {
			openFileMenuItem = new JMenuItem();
			openFileMenuItem.setText("JdbcLogViewerFrame.menu.file.open");
			openFileMenuItem.setMnemonic(KeyEvent.VK_O);

			openFileMenuItem.addActionListener(new ActionListener() {
				// ログファイルを開く
				public void actionPerformed(ActionEvent e) {
					File selectedFile = LogFileDialogHelper.
						getSelectedLogFile(JdbcLogViewerFrame.this);

					if (selectedFile != null) {
						logFile = selectedFile;
						openLogFile();
					}
				}
			});
		}
		return openFileMenuItem;
	}

	/**
	 * This method initializes reopenMenuItem
	 *
	 * @return javax.swing.JMenuItem
	 */
	private JMenuItem getReopenMenuItem() {
		if (reopenMenuItem == null) {
			reopenMenuItem = new JMenuItem();
			reopenMenuItem.setText("JdbcLogViewerFrame.menu.file.reopen");
			reopenMenuItem.setMnemonic(KeyEvent.VK_R);

			reopenMenuItem.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					if (logFile != null) {
						openLogFile();
					}
				}
			});
		}

		updateReopenComponents();

		return reopenMenuItem;
	}

	/**
	 * This method initializes exportMenuItem
	 *
	 * @return javax.swing.JMenuItem
	 */
	private JMenuItem getExportMenuItem() {
		if (exportMenuItem == null) {
			exportMenuItem = new JMenuItem();
			exportMenuItem.setText("JdbcLogViewerFrame.menu.file.export");
			exportMenuItem.setMnemonic(KeyEvent.VK_X);

			exportMenuItem.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					// ファイルダイアログを表示し、現在のテーブルの内容を保存する。
					LogFileDialogHelper.saveLogFile(
							JdbcLogViewerFrame.this,
							getLogTableModel(),
							ViewerConfig.getInstance().getLogFile().getCharSet());
				}
			});
		}
		return exportMenuItem;
	}

	/**
	 * This method initializes exitMenuItem
	 *
	 * @return javax.swing.JMenuItem
	 */
	private JMenuItem getExitMenuItem() {
		if (exitMenuItem == null) {
			exitMenuItem = new JMenuItem();
			exitMenuItem.setText("JdbcLogViewerFrame.menu.file.exit");
			exitMenuItem.setMnemonic(KeyEvent.VK_E);

			exitMenuItem.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					System.exit(0);
				}
			});
		}
		return exitMenuItem;
	}

	/**
	 * This method initializes clearButton
	 *
	 * @return javax.swing.JButton
	 */
	private JButton getClearButton() {
		if (clearButton == null) {
			clearButton = new JButton();
			clearButton.setIcon(new ImageIcon(getClass().getResource("clear.png")));
			clearButton.setToolTipText("JdbcLogViewerFrame.button.clear");
			clearButton.setPreferredSize(new Dimension(30, 24));
			clearButton.setMnemonic(KeyEvent.VK_C);

			clearButton.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					getLogTableModel().reset();
					updateStatusBar();
					onChangeSelectRows();
					getSqlPreviewTextArea().setText("");
					repaintFrame();
				}
			});
		}
		return clearButton;
	}

	/**
	 * This method initializes logTableScrollPane
	 *
	 * @return javax.swing.JScrollPane
	 */
	private JScrollPane getLogTableScrollPane() {
		if (logTableScrollPane == null) {
			logTableScrollPane = new JScrollPane();
			logTableScrollPane.setViewportView(getLogTable());
		}
		return logTableScrollPane;
	}

	/**
	 * This method initializes logTable
	 *
	 * @return javax.swing.JTable
	 */
	JTable getLogTable() {
		if (this.logTable == null) {

			this.logTable = new JTable();

			this.logTable.setModel(new LogTableModel());

			this.logTable.setDefaultRenderer(
					Object.class, new ColoringTableCellRenderer());

			// テーブル本体に対するマウス操作
			this.logTable.addMouseListener(new MouseInputAdapter() {
				@Override
				public void mouseClicked(MouseEvent e) {
					// セルをダブルクリックしたとき、詳細画面を開く
					if (SwingUtilities.isLeftMouseButton(e) &&
							e.getClickCount() == 2) {
						showDetailLog(logTable.rowAtPoint(e.getPoint()));
						return;
					}

					// セルを右クリックしたとき、ポップアップメニュー表示
					if (SwingUtilities.isRightMouseButton(e)) {

						if (logTable.getSelectedRowCount() == 1) {
							getSelectSingleRowPopupMenu()
								.show(e.getComponent(), e.getX(), e.getY());
						} else if (logTable.getSelectedRowCount() >= 2) {
							getSelectMultiRowsPopupMenu()
								.show(e.getComponent(), e.getX(), e.getY());
						} else {
						}

						Filter filter = ViewerConfig.getInstance().getFilter();
						getClearThreadNameFilterMenuItem().setEnabled(StringUtils.isNotEmpty(filter.getThreadName()));
						getClearConnectionIdFilterMenuItem().setEnabled(StringUtils.isNotEmpty(filter.getConnectionId()));
						getClearStatementIdFilterMenuItem().setEnabled(StringUtils.isNotEmpty(filter.getStatementId()));

						return;
					}
				}

				@Override
				public void mouseReleased(MouseEvent e) {
					// ドラッグを離したとき、ステータスバーの選択行を更新する
					onChangeSelectRows();
				}
			});

			this.logTable.addKeyListener(new KeyAdapter() {

				@Override
				public void keyPressed(KeyEvent e) {
					if (e.getKeyCode() == KeyEvent.VK_ENTER) {
						// ENTERで詳細を開く
						showDetailLog(logTable.getSelectedRow());
						e.consume();
						return;
					}
				}

				@Override
				public void keyReleased(KeyEvent e) {
					// キーボードで操作したとき、ステータスバーの選択行を更新する
					onChangeSelectRows();
				}
			});

			// ヘッダに対するマウス操作
			this.logTable.getTableHeader().addMouseListener(new MouseAdapter() {
				public void mouseClicked(MouseEvent e) {

					int viewColumn =
						logTable.getTableHeader().columnAtPoint(e.getPoint());
					int modelColumn = logTable.convertColumnIndexToModel(viewColumn);

					clickedHeaderColumnIndex =
						getLogTableModel().getRealColumnIndex(modelColumn);

					// 右クリックしたとき、列の非表示ポップアップ
					if (SwingUtilities.isRightMouseButton(e)) {
						getHeaderPopupMenu().show(e.getComponent(), e.getX(), e.getY());
						return;
					}

					// ヘッダをダブルクリックしたとき、フィルタ設定画面を開く
					if (SwingUtilities.isLeftMouseButton(e) &&
							e.getClickCount() == 2) {

						openFilterConfigFrame(false);
					}
				}
			});

		}
		return this.logTable;
	}

	/**
	 * 行の選択を変更した時の処理
	 */
	private void onChangeSelectRows() {

		// 選択行数
		selectedRowsCountLabel.setText(
				String.valueOf(logTable.getSelectedRowCount()));

		// 経過時間合計
		long totalElapsedTime = 0L;

		for (int row : logTable.getSelectedRows()) {
			LogEntry logEntry = getLogTableModel().getLogEntry(row);
			Long elapsedTime = logEntry.getElapsedTime();
			if (elapsedTime != null) {
				totalElapsedTime += elapsedTime.longValue();
			}
		}

		totalElapsedTimeLabel.setText(
				String.format("%s%,d", M17N.get("JdbcLogViewerFrame.status_bar.total_elapsed_time"),
				totalElapsedTime / 1000000L));

		// SQLプレビュー
		String sql = StringUtils.EMPTY;

		if (logTable.getSelectedRowCount() == 1) {
			LogEntry logEntry = getCurrentLogEntry();

			if (logEntry.getSql() != null) {
				FormatType formatType = ViewerConfig.getInstance().getFormatTypeAsEnum();

				if (formatType == null || formatType == FormatType.FORMAT) {
					sql = SqlUtils.format(logEntry.getSql());
				} else if (formatType == FormatType.LINE) {
					sql = SqlUtils.linize(logEntry.getSql());
				} else {
					sql = logEntry.getSql();
				}
			}
		}

		getSqlPreviewTextArea().setText(sql);
		GuiUtils.setCeil(getSqlPreviewTextArea());
	}

	@Override
	public LogEntry getCurrentLogEntry() {
		return getLogTableModel().getLogEntry(logTable.getSelectedRow());
	}

	/**
	 * This method initializes viewMenu
	 *
	 * @return javax.swing.JMenu
	 */
	private JMenu getViewMenu() {
		if (viewMenu == null) {
			viewMenu = new JMenu();
			viewMenu.setText("JdbcLogViewerFrame.menu.view");
			viewMenu.setMnemonic(KeyEvent.VK_V);
			viewMenu.add(getTimeCheckBoxMenuItem());
			viewMenu.add(getLogTypeCheckBoxMenuItem());
			viewMenu.add(getThreadNameCheckBoxMenuItem());
			viewMenu.add(getConnectionIdCheckBoxMenuItem());
			viewMenu.add(getStatementIdCheckBoxMenuItem());
			viewMenu.add(getSqlCheckBoxMenuItem());
			viewMenu.add(getAffectedRowsCheckBoxMenuItem());
			viewMenu.add(getElapsedTimeCheckBoxMenuItem());
			viewMenu.add(getResultCheckBoxMenuItem());
			viewMenu.add(getAutoCommitCheckBoxMenuItem());
			viewMenu.add(getTagCheckBoxMenuItem());
			viewMenu.addSeparator();
			viewMenu.add(getStatusBarCheckBoxMenuItem());
			viewMenu.add(getAutoScrollCheckBoxMenuItem());
		}
		return viewMenu;
	}

	/**
	 * This method initializes timeCheckBoxMenuItem
	 *
	 * @return javax.swing.JCheckBoxMenuItem
	 */
	private JCheckBoxMenuItem getTimeCheckBoxMenuItem() {
		if (timeCheckBoxMenuItem == null) {
			timeCheckBoxMenuItem = new JCheckBoxMenuItem();
			timeCheckBoxMenuItem.setText("JdbcLogViewerFrame.column.time");
			timeCheckBoxMenuItem.setState(true);

			timeCheckBoxMenuItem.addItemListener(new ItemListener() {
				// 時刻列の表示/非表示切り替え
				public void itemStateChanged(ItemEvent e) {
					ViewerConfig.getInstance().getLogTable().setTimestamp(
							((JCheckBoxMenuItem) e.getItem()).getState());
					refreshColumns();
				}
			});
		}
		return timeCheckBoxMenuItem;
	}

	/**
	 * This method initializes logTypeCheckBoxMenuItem
	 *
	 * @return javax.swing.JCheckBoxMenuItem
	 */
	private JCheckBoxMenuItem getLogTypeCheckBoxMenuItem() {
		if (logTypeCheckBoxMenuItem == null) {
			logTypeCheckBoxMenuItem = new JCheckBoxMenuItem();
			logTypeCheckBoxMenuItem.setText("JdbcLogViewerFrame.column.log_type");
			logTypeCheckBoxMenuItem.setState(true);

			logTypeCheckBoxMenuItem.addItemListener(new ItemListener() {
				// ログ種別列の表示/非表示切り替え
				public void itemStateChanged(ItemEvent e) {
					ViewerConfig.getInstance().getLogTable().setLogType(
							((JCheckBoxMenuItem) e.getItem()).getState());
					refreshColumns();
				}
			});
		}
		return logTypeCheckBoxMenuItem;
	}

	/**
	 * This method initializes threadNameCheckBoxMenuItem
	 *
	 * @return javax.swing.JCheckBoxMenuItem
	 */
	private JCheckBoxMenuItem getThreadNameCheckBoxMenuItem() {
		if (threadNameCheckBoxMenuItem == null) {
			threadNameCheckBoxMenuItem = new JCheckBoxMenuItem();
			threadNameCheckBoxMenuItem.setText("JdbcLogViewerFrame.column.thread_name");
			threadNameCheckBoxMenuItem.setState(true);

			threadNameCheckBoxMenuItem.addItemListener(new ItemListener() {
				// スレッド名列の表示/非表示切り替え
				public void itemStateChanged(ItemEvent e) {
					ViewerConfig.getInstance().getLogTable().setThreadName(
							((JCheckBoxMenuItem) e.getItem()).getState());
					refreshColumns();
				}
			});
		}
		return threadNameCheckBoxMenuItem;
	}

	/**
	 * This method initializes connectionIdCheckBoxMenuItem
	 *
	 * @return javax.swing.JCheckBoxMenuItem
	 */
	private JCheckBoxMenuItem getConnectionIdCheckBoxMenuItem() {
		if (connectionIdCheckBoxMenuItem == null) {
			connectionIdCheckBoxMenuItem = new JCheckBoxMenuItem();
			connectionIdCheckBoxMenuItem.setText("JdbcLogViewerFrame.column.connection_id");
			connectionIdCheckBoxMenuItem.setState(true);

			connectionIdCheckBoxMenuItem.addItemListener(new ItemListener() {
				// コネクションID列の表示/非表示切り替え
				public void itemStateChanged(ItemEvent e) {
					ViewerConfig.getInstance().getLogTable().setConnectionId(
							((JCheckBoxMenuItem) e.getItem()).getState());
					refreshColumns();
				}
			});
		}
		return connectionIdCheckBoxMenuItem;
	}

	/**
	 * This method initializes statementIdCheckBoxMenuItem
	 *
	 * @return javax.swing.JCheckBoxMenuItem
	 */
	private JCheckBoxMenuItem getStatementIdCheckBoxMenuItem() {
		if (statementIdCheckBoxMenuItem == null) {
			statementIdCheckBoxMenuItem = new JCheckBoxMenuItem();
			statementIdCheckBoxMenuItem.setText("JdbcLogViewerFrame.column.statement_id");
			statementIdCheckBoxMenuItem.setState(true);

			statementIdCheckBoxMenuItem.addItemListener(new ItemListener() {
				// ステートメントID列の表示/非表示切り替え
				public void itemStateChanged(ItemEvent e) {
					ViewerConfig.getInstance().getLogTable().setStatementId(
							((JCheckBoxMenuItem) e.getItem()).getState());
					refreshColumns();
				}
			});
		}
		return statementIdCheckBoxMenuItem;
	}

	/**
	 * This method initializes sqlCheckBoxMenuItem
	 *
	 * @return javax.swing.JCheckBoxMenuItem
	 */
	private JCheckBoxMenuItem getSqlCheckBoxMenuItem() {
		if (sqlCheckBoxMenuItem == null) {
			sqlCheckBoxMenuItem = new JCheckBoxMenuItem();
			sqlCheckBoxMenuItem.setText("JdbcLogViewerFrame.column.sql");
			sqlCheckBoxMenuItem.setState(true);

			sqlCheckBoxMenuItem.addItemListener(new ItemListener() {
				// SQL列の表示/非表示切り替え
				public void itemStateChanged(ItemEvent e) {
					ViewerConfig.getInstance().getLogTable().setSql(
							((JCheckBoxMenuItem) e.getItem()).getState());
					refreshColumns();
				}
			});
		}
		return sqlCheckBoxMenuItem;
	}

	/**
	 * This method initializes affectedRowsCheckBoxMenuItem
	 *
	 * @return javax.swing.JCheckBoxMenuItem
	 */
	private JCheckBoxMenuItem getAffectedRowsCheckBoxMenuItem() {
		if (affectedRowsCheckBoxMenuItem == null) {
			affectedRowsCheckBoxMenuItem = new JCheckBoxMenuItem();
			affectedRowsCheckBoxMenuItem.setText("JdbcLogViewerFrame.column.affected_rows");
			affectedRowsCheckBoxMenuItem.setState(true);

			affectedRowsCheckBoxMenuItem.addItemListener(new ItemListener() {
				// 更新件数列の表示/非表示切り替え
				public void itemStateChanged(ItemEvent e) {
					ViewerConfig.getInstance().getLogTable().setAffectedRows(
							((JCheckBoxMenuItem) e.getItem()).getState());
					refreshColumns();
				}
			});
		}
		return affectedRowsCheckBoxMenuItem;
	}

	/**
	 * This method initializes elapsedTimeCheckBoxMenuItem
	 *
	 * @return javax.swing.JCheckBoxMenuItem
	 */
	private JCheckBoxMenuItem getElapsedTimeCheckBoxMenuItem() {
		if (elapsedTimeCheckBoxMenuItem == null) {
			elapsedTimeCheckBoxMenuItem = new JCheckBoxMenuItem();
			elapsedTimeCheckBoxMenuItem.setText("JdbcLogViewerFrame.column.elapsed_time");
			elapsedTimeCheckBoxMenuItem.setState(true);

			elapsedTimeCheckBoxMenuItem.addItemListener(new ItemListener() {
				// 経過時間列の表示/非表示切り替え
				public void itemStateChanged(ItemEvent e) {
					ViewerConfig.getInstance().getLogTable().setElapsedTime(
							((JCheckBoxMenuItem) e.getItem()).getState());
					refreshColumns();
				}
			});
		}
		return elapsedTimeCheckBoxMenuItem;
	}

	/**
	 * This method initializes resultCheckBoxMenuItem
	 *
	 * @return javax.swing.JCheckBoxMenuItem
	 */
	private JCheckBoxMenuItem getResultCheckBoxMenuItem() {
		if (resultCheckBoxMenuItem == null) {
			resultCheckBoxMenuItem = new JCheckBoxMenuItem();
			resultCheckBoxMenuItem.setText("JdbcLogViewerFrame.column.result");
			resultCheckBoxMenuItem.setState(true);

			resultCheckBoxMenuItem.addItemListener(new ItemListener() {
				// 結果列の表示/非表示切り替え
				public void itemStateChanged(ItemEvent e) {
					ViewerConfig.getInstance().getLogTable().setResult(
							((JCheckBoxMenuItem) e.getItem()).getState());
					refreshColumns();
				}
			});
		}
		return resultCheckBoxMenuItem;
	}

	/**
	 * This method initializes autoCommitCheckBoxMenuItem
	 *
	 * @return javax.swing.JCheckBoxMenuItem
	 */
	private JCheckBoxMenuItem getAutoCommitCheckBoxMenuItem() {
		if (autoCommitCheckBoxMenuItem == null) {
			autoCommitCheckBoxMenuItem = new JCheckBoxMenuItem();
			autoCommitCheckBoxMenuItem.setText("JdbcLogViewerFrame.column.auto_commit");
			autoCommitCheckBoxMenuItem.setState(true);

			autoCommitCheckBoxMenuItem.addItemListener(new ItemListener() {
				// 結果列の表示/非表示切り替え
				public void itemStateChanged(ItemEvent e) {
					ViewerConfig.getInstance().getLogTable().setAutoCommit(
							((JCheckBoxMenuItem) e.getItem()).getState());
					refreshColumns();
				}
			});
		}
		return autoCommitCheckBoxMenuItem;
	}

	/**
	 * This method initializes tagCheckBoxMenuItem
	 *
	 * @return javax.swing.JCheckBoxMenuItem
	 */
	private JCheckBoxMenuItem getTagCheckBoxMenuItem() {
		if (tagCheckBoxMenuItem == null) {
			tagCheckBoxMenuItem = new JCheckBoxMenuItem();
			tagCheckBoxMenuItem.setText("JdbcLogViewerFrame.column.tag");
			tagCheckBoxMenuItem.setState(true);

			tagCheckBoxMenuItem.addItemListener(new ItemListener() {
				// タグ列の表示/非表示切り替え
				public void itemStateChanged(ItemEvent e) {
					ViewerConfig.getInstance().getLogTable().setTag(
							((JCheckBoxMenuItem) e.getItem()).getState());
					refreshColumns();
				}
			});
		}
		return tagCheckBoxMenuItem;
	}

	/**
	 * This method initializes headerPopupMenu
	 *
	 * @return javax.swing.JPopupMenu
	 */
	private JPopupMenu getHeaderPopupMenu() {
		if (headerPopupMenu == null) {
			headerPopupMenu = new JPopupMenu();
			headerPopupMenu.add(getHideColumnMenuItem());
			headerPopupMenu.add(getSortElapsedTimeMenuItem());
		}

		headerPopupMenu.removeAll();
		headerPopupMenu.add(getHideColumnMenuItem());

		if (this.clickedHeaderColumnIndex == ELAPSED_TIME.getColumnIndex()) {
			headerPopupMenu.add(getSortElapsedTimeMenuItem());
		}

		return headerPopupMenu;
	}

	/**
	 * This method initializes hideColumnMenuItem
	 *
	 * @return javax.swing.JMenuItem
	 */
	private JMenuItem getHideColumnMenuItem() {
		if (hideColumnMenuItem == null) {
			hideColumnMenuItem = new JMenuItem();
			hideColumnMenuItem.setText("JdbcLogViewerFrame.popup_menu.hide_column");

			hideColumnMenuItem.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					hideThisColumn();
				}
			});
		}
		return hideColumnMenuItem;
	}

	/**
	 * This method initializes selectSingleRowPopupMenu
	 *
	 * @return javax.swing.JPopupMenu
	 */
	private JPopupMenu getSelectSingleRowPopupMenu() {
		selectSingleRowPopupMenu = new JPopupMenu();
		selectSingleRowPopupMenu.add(getClearBeforeMenuItem());
		selectSingleRowPopupMenu.add(getClearAfterMenuItem());
		selectSingleRowPopupMenu.add(getChooseThreadNameMenuItem());
		selectSingleRowPopupMenu.add(getChooseConnectionIdMenuItem());
		selectSingleRowPopupMenu.add(getChooseStatementIdMenuItem());
		selectSingleRowPopupMenu.add(getClearSelectedRowsMenuItem());
		selectSingleRowPopupMenu.add(getClearExceptSelectedRowsMenuItem());
		selectSingleRowPopupMenu.add(getCrudContextMenuItem());
		selectSingleRowPopupMenu.add(getClearThreadNameFilterMenuItem());
		selectSingleRowPopupMenu.add(getClearConnectionIdFilterMenuItem());
		selectSingleRowPopupMenu.add(getClearStatementIdFilterMenuItem());
		return selectSingleRowPopupMenu;
	}

	/**
	 * This method initializes clearBeforeMenuItem
	 *
	 * @return javax.swing.JMenuItem
	 */
	private JMenuItem getClearBeforeMenuItem() {
		if (clearBeforeMenuItem == null) {
			clearBeforeMenuItem = new JMenuItem();
			clearBeforeMenuItem.setText("JdbcLogViewerFrame.popup_menu.clear_rows_before");

			clearBeforeMenuItem.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					int[] selectedRows = getLogTable().getSelectedRows();
					if (selectedRows.length != 0) {
						getLogTableModel().clearBefore(selectedRows[0]);
						updateStatusBar();
						onChangeSelectRows();
						repaintFrame();
					}
				}
			});
		}
		return clearBeforeMenuItem;
	}

	/**
	 * This method initializes clearAfterMenuItem
	 *
	 * @return javax.swing.JMenuItem
	 */
	private JMenuItem getClearAfterMenuItem() {
		if (clearAfterMenuItem == null) {
			clearAfterMenuItem = new JMenuItem();
			clearAfterMenuItem.setText("JdbcLogViewerFrame.popup_menu.clear_rows_after");

			clearAfterMenuItem.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					int[] selectedRows = getLogTable().getSelectedRows();
					if (selectedRows.length != 0) {
						getLogTableModel().clearAfter(
								selectedRows[selectedRows.length - 1]);
						updateStatusBar();
						onChangeSelectRows();
						repaintFrame();
					}
				}
			});
		}
		return clearAfterMenuItem;
	}

	/**
	 * This method initializes chooseThreadNameMenuItem
	 *
	 * @return javax.swing.JMenuItem
	 */
	private JMenuItem getChooseThreadNameMenuItem() {
		if (chooseThreadNameMenuItem == null) {
			chooseThreadNameMenuItem = new JMenuItem();
			chooseThreadNameMenuItem.setText("JdbcLogViewerFrame.popup_menu.choose_thread_name");

			chooseThreadNameMenuItem.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					int[] selectedRows = getLogTable().getSelectedRows();
					if (selectedRows.length != 0) {
						LogEntry entry = getLogTableModel().getLogEntry(selectedRows[0]);
						ViewerConfig.getInstance().getFilter().setThreadName(entry.getThreadName());
						getLogTableModel().updateFilter(getLogTableFilter());
						updateStatusBar();
						onChangeSelectRows();
						getSqlPreviewTextArea().setText("");
						repaintFrame();
					}
				}
			});
		}
		return chooseThreadNameMenuItem;
	}

	/**
	 * This method initializes clearThreadNameFilterMenuItem
	 *
	 * @return javax.swing.JMenuItem
	 */
	private JMenuItem getClearThreadNameFilterMenuItem() {
		if (clearThreadNameFilterMenuItem == null) {
			clearThreadNameFilterMenuItem = new JMenuItem();
			clearThreadNameFilterMenuItem.setText("JdbcLogViewerFrame.popup_menu.clear_thread_name_filter");

			clearThreadNameFilterMenuItem.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					ViewerConfig.getInstance().getFilter().setThreadName(null);
					getLogTableModel().updateFilter(getLogTableFilter());
					updateStatusBar();
					onChangeSelectRows();
					getSqlPreviewTextArea().setText("");
					repaintFrame();
				}
			});
		}
		return clearThreadNameFilterMenuItem;
	}

	/**
	 * This method initializes chooseConnectionIdMenuItem
	 *
	 * @return javax.swing.JMenuItem
	 */
	private JMenuItem getChooseConnectionIdMenuItem() {
		if (chooseConnectionIdMenuItem == null) {
			chooseConnectionIdMenuItem = new JMenuItem();
			chooseConnectionIdMenuItem.setText("JdbcLogViewerFrame.popup_menu.choose_connection_id");

			chooseConnectionIdMenuItem.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					int[] selectedRows = getLogTable().getSelectedRows();
					if (selectedRows.length != 0) {
						LogEntry entry = getLogTableModel().getLogEntry(selectedRows[0]);
						ViewerConfig.getInstance().getFilter().setConnectionId(entry.getConnectionId());
						getLogTableModel().updateFilter(getLogTableFilter());
						updateStatusBar();
						onChangeSelectRows();
						getSqlPreviewTextArea().setText("");
						repaintFrame();
					}
				}
			});
		}
		return chooseConnectionIdMenuItem;
	}

	/**
	 * This method initializes clearConnectionIdFilterMenuItem
	 *
	 * @return javax.swing.JMenuItem
	 */
	private JMenuItem getClearConnectionIdFilterMenuItem() {
		if (clearConnectionIdFilterMenuItem == null) {
			clearConnectionIdFilterMenuItem = new JMenuItem();
			clearConnectionIdFilterMenuItem.setText("JdbcLogViewerFrame.popup_menu.clear_connection_id_filter");

			clearConnectionIdFilterMenuItem.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					ViewerConfig.getInstance().getFilter().setConnectionId(null);
					getLogTableModel().updateFilter(getLogTableFilter());
					updateStatusBar();
					onChangeSelectRows();
					getSqlPreviewTextArea().setText("");
					repaintFrame();
				}
			});
		}
		return clearConnectionIdFilterMenuItem;
	}

	/**
	 * This method initializes chooseStatementIdMenuItem
	 *
	 * @return javax.swing.JMenuItem
	 */
	private JMenuItem getChooseStatementIdMenuItem() {
		if (chooseStatementIdMenuItem == null) {
			chooseStatementIdMenuItem = new JMenuItem();
			chooseStatementIdMenuItem.setText("JdbcLogViewerFrame.popup_menu.choose_statement_id");

			chooseStatementIdMenuItem.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					int[] selectedRows = getLogTable().getSelectedRows();
					if (selectedRows.length != 0) {
						LogEntry entry = getLogTableModel().getLogEntry(selectedRows[0]);
						ViewerConfig.getInstance().getFilter().setStatementId(entry.getStatementId());
						getLogTableModel().updateFilter(getLogTableFilter());
						updateStatusBar();
						onChangeSelectRows();
						getSqlPreviewTextArea().setText("");
						repaintFrame();
					}
				}
			});
		}
		return chooseStatementIdMenuItem;
	}

	/**
	 * This method initializes clearStatementIdFilterMenuItem
	 *
	 * @return javax.swing.JMenuItem
	 */
	private JMenuItem getClearStatementIdFilterMenuItem() {
		if (clearStatementIdFilterMenuItem == null) {
			clearStatementIdFilterMenuItem = new JMenuItem();
			clearStatementIdFilterMenuItem.setText("JdbcLogViewerFrame.popup_menu.clear_statement_id_filter");

			clearStatementIdFilterMenuItem.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					ViewerConfig.getInstance().getFilter().setStatementId(null);
					getLogTableModel().updateFilter(getLogTableFilter());
					updateStatusBar();
					onChangeSelectRows();
					getSqlPreviewTextArea().setText("");
					repaintFrame();
				}
			});
		}
		return clearStatementIdFilterMenuItem;
	}

	/**
	 * This method initializes selectMultiRowsPopupMenu
	 *
	 * @return javax.swing.JPopupMenu
	 */
	private JPopupMenu getSelectMultiRowsPopupMenu() {
		selectMultiRowsPopupMenu = new JPopupMenu();
		selectMultiRowsPopupMenu.add(getClearMultiSelectedRowsMenuItem());
		selectMultiRowsPopupMenu.add(getClearExceptMultiSelectedRowsMenuItem());
		selectMultiRowsPopupMenu.add(getCrudMultiContextMenuItem());
		selectMultiRowsPopupMenu.add(getClearThreadNameFilterMenuItem());
		selectMultiRowsPopupMenu.add(getClearConnectionIdFilterMenuItem());
		selectMultiRowsPopupMenu.add(getClearStatementIdFilterMenuItem());
		return selectMultiRowsPopupMenu;
	}

	/**
	 * This method initializes clearSelectedRowsMenuItem
	 *
	 * @return javax.swing.JMenuItem
	 */
	private JMenuItem getClearSelectedRowsMenuItem() {
		if (clearSelectedRowsMenuItem == null) {
			clearSelectedRowsMenuItem = new JMenuItem();
			clearSelectedRowsMenuItem.setText("JdbcLogViewerFrame.popup_menu.clear_selected_rows");

			clearSelectedRowsMenuItem.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					getLogTableModel().clearSelectedRows(
							getLogTable().getSelectedRows());
					updateStatusBar();
					onChangeSelectRows();
					repaintFrame();
				}
			});
		}
		return clearSelectedRowsMenuItem;
	}

	/**
	 * This method initializes clearMultiSelectedRowsMenuItem
	 *
	 * @return javax.swing.JMenuItem
	 */
	private JMenuItem getClearMultiSelectedRowsMenuItem() {
		if (clearMultiSelectedRowsMenuItem == null) {
			clearMultiSelectedRowsMenuItem = new JMenuItem();
			clearMultiSelectedRowsMenuItem.setText("JdbcLogViewerFrame.popup_menu.clear_selected_rows");

			clearMultiSelectedRowsMenuItem.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					getLogTableModel().clearSelectedRows(
							getLogTable().getSelectedRows());
					updateStatusBar();
					onChangeSelectRows();
					repaintFrame();
				}
			});
		}
		return clearMultiSelectedRowsMenuItem;
	}

	/**
	 * This method initializes clearExceptSelectedRowsMenuItem
	 *
	 * @return javax.swing.JMenuItem
	 */
	private JMenuItem getClearExceptSelectedRowsMenuItem() {
		if (clearExceptSelectedRowsMenuItem == null) {
			clearExceptSelectedRowsMenuItem = new JMenuItem();
			clearExceptSelectedRowsMenuItem.setText("JdbcLogViewerFrame.popup_menu.clear_except_selected_rows");

			clearExceptSelectedRowsMenuItem.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					getLogTableModel().clearExceptSelectedRows(
							getLogTable().getSelectedRows());
					updateStatusBar();
					onChangeSelectRows();
					repaintFrame();
				}
			});
		}
		return clearExceptSelectedRowsMenuItem;
	}

	/**
	 * This method initializes clearExceptMultiSelectedRowsMenuItem
	 *
	 * @return javax.swing.JMenuItem
	 */
	private JMenuItem getClearExceptMultiSelectedRowsMenuItem() {
		if (clearExceptMultiSelectedRowsMenuItem == null) {
			clearExceptMultiSelectedRowsMenuItem = new JMenuItem();
			clearExceptMultiSelectedRowsMenuItem.setText("JdbcLogViewerFrame.popup_menu.clear_except_selected_rows");

			clearExceptMultiSelectedRowsMenuItem.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					getLogTableModel().clearExceptSelectedRows(
							getLogTable().getSelectedRows());
					updateStatusBar();
					onChangeSelectRows();
					repaintFrame();
				}
			});
		}
		return clearExceptMultiSelectedRowsMenuItem;
	}

	/**
	 * This method initializes toolMenu
	 *
	 * @return javax.swing.JMenu
	 */
	private JMenu getToolMenu() {
		if (toolMenu == null) {
			toolMenu = new JMenu();
			toolMenu.setText("JdbcLogViewerFrame.menu.tool");
			toolMenu.setMnemonic(KeyEvent.VK_T);
			toolMenu.add(getCrudMenuItem());
			toolMenu.add(getDetectUnclosedConnectionMenuItem());
			toolMenu.add(getDetectUnclosedStatementMenuItem());
			toolMenu.addSeparator();
			toolMenu.add(getPreferencesMenuItem());
			toolMenu.add(getLoggerConfigMenuItem());
			toolMenu.add(getVersionInfoMenuItem());
		}
		return toolMenu;
	}

	/**
	 * This method initializes preferencesMenuItem
	 *
	 * @return javax.swing.JMenuItem
	 */
	private JMenuItem getPreferencesMenuItem() {
		if (preferencesMenuItem == null) {
			preferencesMenuItem = new JMenuItem();
			preferencesMenuItem.setText("JdbcLogViewerFrame.menu.tool.preferences");

			preferencesMenuItem.addActionListener(new java.awt.event.ActionListener() {
				public void actionPerformed(java.awt.event.ActionEvent e) {
					JFrame prefFrame = new PreferencesFrame();
					prefFrame.setLocationRelativeTo(JdbcLogViewerFrame.this);
					prefFrame.setVisible(true);
				}
			});
		}
		return preferencesMenuItem;
	}

	/**
	 * This method initializes filterButton
	 *
	 * @return javax.swing.JButton
	 */
	private JButton getFilterButton() {
		if (filterButton == null) {
			filterButton = new JButton();
			filterButton.setIcon(new ImageIcon(getClass().getResource("filter.png")));
			filterButton.setToolTipText("JdbcLogViewerFrame.button.filter");
			filterButton.setPreferredSize(new Dimension(30, 24));
			filterButton.setMnemonic(KeyEvent.VK_L);

			filterButton.addActionListener(new java.awt.event.ActionListener() {
				// フィルタ設定ダイアログ表示
				public void actionPerformed(java.awt.event.ActionEvent e) {
					openFilterConfigFrame(true);
				}
			});
		}
		return filterButton;
	}

	/**
	 * This method initializes sortElapsedTimeMenuItem
	 *
	 * @return javax.swing.JMenuItem
	 */
	private JMenuItem getSortElapsedTimeMenuItem() {
		if (sortElapsedTimeMenuItem == null) {
			sortElapsedTimeMenuItem = new JMenuItem();
			sortElapsedTimeMenuItem.setText("JdbcLogViewerFrame.popup_menu.sort_elapsed_time_desc");

			sortElapsedTimeMenuItem.addActionListener(new java.awt.event.ActionListener() {
				// 経過時間が大きい順に並び替える
				public void actionPerformed(java.awt.event.ActionEvent e) {
					// ログ取得は停止する
					getStartReadToggleButton().setSelected(false);
					getSuspendToggleButton().setSelected(true);

					getLogTableModel().orderByElapsedTimeDesc();
				}
			});
		}
		return sortElapsedTimeMenuItem;
	}

	/**
	 * This method initializes statusBarPanel
	 *
	 * @return javax.swing.JPanel
	 */
	private JPanel getStatusBarPanel() {
		if (statusBarPanel == null) {
			FlowLayout flowLayout1 = new FlowLayout();
			flowLayout1.setAlignment(FlowLayout.LEFT);
			flowLayout1.setVgap(0);
			flowLayout1.setHgap(0);
			statusBarPanel = new JPanel();
			statusBarPanel.setLayout(flowLayout1);
			statusBarPanel.setBorder(BorderFactory.createEmptyBorder(0, 0, 0, 0));
			statusBarPanel.add(getLastLogTimePanel(), null);
			statusBarPanel.add(getRowsCountPanel(), null);
			statusBarPanel.add(getMarkPanel(), null);
			statusBarPanel.add(getTotalElapsedTimePanel(), null);
		}
		return statusBarPanel;
	}

	/**
	 * This method initializes lastLogTimePanel
	 *
	 * @return javax.swing.JPanel
	 */
	private JPanel getLastLogTimePanel() {
		if (lastLogTimePanel == null) {
			lastLogTimeNameLabel = new JLabel();
			lastLogTimeNameLabel.setText("JdbcLogViewerFrame.status_bar.last_log_time");
			FlowLayout flowLayout2 = new FlowLayout();
			flowLayout2.setAlignment(FlowLayout.LEFT);
			flowLayout2.setHgap(10);
			flowLayout2.setVgap(2);
			lastLogTimePanel = new JPanel();
			lastLogTimePanel.setBorder(BorderFactory.createLineBorder(Color.gray, 1));
			lastLogTimePanel.setLayout(flowLayout2);
			lastLogTimeLabel = new JLabel();
			lastLogTimeLabel.setText("");
			lastLogTimeLabel.setDisplayedMnemonic(KeyEvent.VK_UNDEFINED);
			lastLogTimePanel.add(lastLogTimeNameLabel, null);
			lastLogTimePanel.add(lastLogTimeLabel, null);
		}
		return lastLogTimePanel;
	}

	/**
	 * This method initializes rowsCountPanel
	 *
	 * @return javax.swing.JPanel
	 */
	private JPanel getRowsCountPanel() {
		if (rowsCountPanel == null) {
			rowsCountLabel = new JLabel();
			rowsCountLabel.setText("JdbcLogViewerFrame.status_bar.rows_count");
			allRowsCountLabel = new JLabel();
			allRowsCountLabel.setText("0");
			selectedRowsCountLabel = new JLabel();
			selectedRowsCountLabel.setText("0");
			rowsCountSeparatorLabel2 = new JLabel();
			rowsCountSeparatorLabel2.setText("/");
			visibleRowsCountLabel = new JLabel();
			visibleRowsCountLabel.setText("0");
			FlowLayout flowLayout3 = new FlowLayout();
			flowLayout3.setAlignment(FlowLayout.LEFT);
			flowLayout3.setHgap(10);
			flowLayout3.setVgap(2);
			rowsCountPanel = new JPanel();
			rowsCountPanel.setBorder(BorderFactory.createLineBorder(Color.gray, 1));
			rowsCountPanel.setLayout(flowLayout3);
			rowsCountPanel.add(rowsCountLabel, null);
			rowsCountPanel.add(selectedRowsCountLabel, null);
			rowsCountPanel.add(rowsCountSeparatorLabel1, null);
			rowsCountPanel.add(visibleRowsCountLabel, null);
			rowsCountPanel.add(rowsCountSeparatorLabel2, null);
			rowsCountPanel.add(allRowsCountLabel, null);
		}
		return rowsCountPanel;
	}

	/**
	 * This method initializes statusBarCheckBoxMenuItem
	 *
	 * @return javax.swing.JCheckBoxMenuItem
	 */
	private JCheckBoxMenuItem getStatusBarCheckBoxMenuItem() {
		if (statusBarCheckBoxMenuItem == null) {
			statusBarCheckBoxMenuItem = new JCheckBoxMenuItem();
			statusBarCheckBoxMenuItem.setText("JdbcLogViewerFrame.menu.view.status_bar");
			statusBarCheckBoxMenuItem.addItemListener(new java.awt.event.ItemListener() {
				public void itemStateChanged(java.awt.event.ItemEvent e) {
					statusBarPanel.setVisible(statusBarCheckBoxMenuItem.isSelected());
				}
			});
		}
		return statusBarCheckBoxMenuItem;
	}

	/**
	 * This method initializes startReadToggleButton
	 *
	 * @return javax.swing.JToggleButton
	 */
	private JToggleButton getStartReadToggleButton() {
		if (startReadToggleButton == null) {
			startReadToggleButton = new JToggleButton();
			startReadToggleButton.setSelected(false);
			startReadToggleButton.setIcon(new ImageIcon(getClass().getResource("start.png")));
			startReadToggleButton.setPreferredSize(new Dimension(30, 24));
			startReadToggleButton.setToolTipText("JdbcLogViewerFrame.button.start_read");
		}
		return startReadToggleButton;
	}

	/**
	 * This method initializes suspendToggleButton
	 *
	 * @return javax.swing.JToggleButton
	 */
	private JToggleButton getSuspendToggleButton() {
		if (suspendToggleButton == null) {
			suspendToggleButton = new JToggleButton();
			suspendToggleButton.setSelected(true);
			suspendToggleButton.setIcon(new ImageIcon(getClass().getResource("suspend.png")));
			suspendToggleButton.setPreferredSize(new Dimension(30, 24));
			suspendToggleButton.setToolTipText("JdbcLogViewerFrame.button.suspend_read");
		}
		return suspendToggleButton;
	}

	/**
	 * This method initializes autoScrollCheckBoxMenuItem
	 *
	 * @return javax.swing.JCheckBoxMenuItem
	 */
	private JCheckBoxMenuItem getAutoScrollCheckBoxMenuItem() {
		if (autoScrollCheckBoxMenuItem == null) {
			autoScrollCheckBoxMenuItem = new JCheckBoxMenuItem();
			autoScrollCheckBoxMenuItem.setText("JdbcLogViewerFrame.menu.view.auto_scroll");
		}
		return autoScrollCheckBoxMenuItem;
	}

	/**
	 * This method initializes reopenButton
	 *
	 * @return javax.swing.JButton
	 */
	private JButton getReopenButton() {
		if (reopenButton == null) {
			reopenButton = new JButton();
			reopenButton.setIcon(new ImageIcon(getClass().getResource("reopen.png")));
			reopenButton.setToolTipText("JdbcLogViewerFrame.button.reopen");
			reopenButton.setMnemonic(KeyEvent.VK_R);
			reopenButton.setPreferredSize(new Dimension(30, 24));

			reopenButton.addActionListener(new java.awt.event.ActionListener() {
				public void actionPerformed(java.awt.event.ActionEvent e) {
					if (logFile != null) {
						openLogFile();
					}
				}
			});
		}

		updateReopenComponents();

		return reopenButton;
	}

	/**
	 * This method initializes searchMenu
	 *
	 * @return javax.swing.JMenu
	 */
	private JMenu getSearchMenu() {
		if (searchMenu == null) {
			searchMenu = new JMenu();
			searchMenu.setText("JdbcLogViewerFrame.menu.search");
			searchMenu.setMnemonic(KeyEvent.VK_S);
			searchMenu.add(getOpenSearchDialogMenuItem());
			searchMenu.add(getSearchForwardMenuItem());
			searchMenu.add(getSearhBackMenuItem());
		}
		return searchMenu;
	}

	/**
	 * This method initializes openSearchDialogMenuItem
	 *
	 * @return javax.swing.JMenuItem
	 */
	private JMenuItem getOpenSearchDialogMenuItem() {
		if (openSearchDialogMenuItem == null) {
			openSearchDialogMenuItem = new JMenuItem();
			openSearchDialogMenuItem.setText("JdbcLogViewerFrame.menu.search.search");
			openSearchDialogMenuItem.setToolTipText("JdbcLogViewerFrame.menu.search.search.tooltip");
			openSearchDialogMenuItem.setMnemonic(KeyEvent.VK_S);

			openSearchDialogMenuItem.addActionListener(new ActionListener() {
				// 検索ダイアログを開く
				public void actionPerformed(ActionEvent e) {
					openSearchDialog();
				}
			});

			// Ctrl+F のショートカットキー
			openSearchDialogMenuItem.setAccelerator(
					KeyStroke.getKeyStroke(KeyEvent.VK_F, InputEvent.CTRL_DOWN_MASK));
		}
		return openSearchDialogMenuItem;
	}

	/**
	 * This method initializes searchForwardMenuItem
	 *
	 * @return javax.swing.JMenuItem
	 */
	private JMenuItem getSearchForwardMenuItem() {
		if (searchForwardMenuItem == null) {
			searchForwardMenuItem = new JMenuItem();
			searchForwardMenuItem.setText("JdbcLogViewerFrame.menu.search.forward");
			searchForwardMenuItem.setMnemonic(KeyEvent.VK_F);
			searchForwardMenuItem.setToolTipText("JdbcLogViewerFrame.menu.search.forward.tooltip");
			searchForwardMenuItem.setEnabled(false);

			searchForwardMenuItem.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					search(searchWord, searchPattern != null, true, searchCirculating, searchCaseSensitive);
				}
			});

			// F3 のショートカットキー
			searchForwardMenuItem.setAccelerator(
					KeyStroke.getKeyStroke(KeyEvent.VK_F3, 0));
		}
		return searchForwardMenuItem;
	}

	/**
	 * This method initializes searhBackMenuItem
	 *
	 * @return javax.swing.JMenuItem
	 */
	private JMenuItem getSearhBackMenuItem() {
		if (searhBackMenuItem == null) {
			searhBackMenuItem = new JMenuItem();
			searhBackMenuItem.setText("JdbcLogViewerFrame.menu.search.back");
			searhBackMenuItem.setToolTipText("JdbcLogViewerFrame.menu.search.back.tooltip");
			searhBackMenuItem.setEnabled(false);
			searhBackMenuItem.setMnemonic(KeyEvent.VK_B);

			searhBackMenuItem.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					search(searchWord, searchPattern != null, false, searchCirculating, searchCaseSensitive);
				}
			});

			// Shift+F3 のショートカットキー
			searhBackMenuItem.setAccelerator(
					KeyStroke.getKeyStroke(KeyEvent.VK_F3, InputEvent.SHIFT_DOWN_MASK));
		}
		return searhBackMenuItem;
	}

	/**
	 * This method initializes versionInfoMenuItem
	 *
	 * @return javax.swing.JMenuItem
	 */
	private JMenuItem getVersionInfoMenuItem() {
		if (versionInfoMenuItem == null) {
			versionInfoMenuItem = new JMenuItem();
			versionInfoMenuItem.setText("JdbcLogViewerFrame.menu.tool.versionInfo");

			versionInfoMenuItem.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					JDialog dialog = new VersionInfoDialog(JdbcLogViewerFrame.this);
					dialog.setLocationRelativeTo(JdbcLogViewerFrame.this);
					dialog.setVisible(true);
				}
			});
		}
		return versionInfoMenuItem;
	}

	/**
	 * This method initializes crudMenuItem
	 *
	 * @return javax.swing.JMenuItem
	 */
	private JMenuItem getCrudMenuItem() {
		if (crudMenuItem == null) {
			crudMenuItem = new JMenuItem();
			crudMenuItem.setText("JdbcLogViewerFrame.menu.tool.CRUD");
			crudMenuItem.setToolTipText("JdbcLogViewerFrame.menu.tool.CRUD.tooltip");

			crudMenuItem.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					JFrame crudView = new CRUDViewFrame(getLogTable(), getLogTableModel(), true);
					crudView.setLocationRelativeTo(JdbcLogViewerFrame.this);
					crudView.setVisible(true);
				}
			});
		}
		return crudMenuItem;
	}

	/**
	 * This method initializes detectUnclosedConnectionMenuItem
	 *
	 * @return javax.swing.JMenuItem
	 */
	private JMenuItem getDetectUnclosedConnectionMenuItem() {
		if (detectUnclosedConnectionMenuItem == null) {
			detectUnclosedConnectionMenuItem = new JMenuItem();
			detectUnclosedConnectionMenuItem.setText("JdbcLogViewerFrame.menu.tool.unclosedConnection");
			detectUnclosedConnectionMenuItem.setToolTipText("JdbcLogViewerFrame.menu.tool.unclosedConnection.tooltip");

			detectUnclosedConnectionMenuItem.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					getLogTableModel().detectUnclosedConnection();
					updateStatusBar();
					onChangeSelectRows();
					repaintFrame();
				}
			});
		}
		return detectUnclosedConnectionMenuItem;
	}

	/**
	 * This method initializes detectUnclosedStatementMenuItem
	 *
	 * @return javax.swing.JMenuItem
	 */
	private JMenuItem getDetectUnclosedStatementMenuItem() {
		if (detectUnclosedStatementMenuItem == null) {
			detectUnclosedStatementMenuItem = new JMenuItem();
			detectUnclosedStatementMenuItem.setText("JdbcLogViewerFrame.menu.tool.unclosedStatement");
			detectUnclosedStatementMenuItem.setToolTipText("JdbcLogViewerFrame.menu.tool.unclosedStatement.tooltip");

			detectUnclosedStatementMenuItem.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					getLogTableModel().detectUnclosedStatement();
					updateStatusBar();
					onChangeSelectRows();
					repaintFrame();
				}
			});
		}
		return detectUnclosedStatementMenuItem;
	}

	/**
	 * This method initializes crudContextMenuItem
	 *
	 * @return javax.swing.JMenuItem
	 */
	private JMenuItem getCrudContextMenuItem() {
		if (crudContextMenuItem == null) {
			crudContextMenuItem = new JMenuItem();

			crudContextMenuItem.setText("JdbcLogViewerFrame.popup_menu.crud");

			crudContextMenuItem.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					JFrame crudView = new CRUDViewFrame(getLogTable(), getLogTableModel(), false);
					crudView.setLocationRelativeTo(JdbcLogViewerFrame.this);
					crudView.setVisible(true);
				}
			});
		}
		return crudContextMenuItem;
	}

	/**
	 * This method initializes crudMultiContextMenuItem
	 *
	 * @return javax.swing.JMenuItem
	 */
	private JMenuItem getCrudMultiContextMenuItem() {
		if (crudMultiContextMenuItem == null) {
			crudMultiContextMenuItem = new JMenuItem();

			crudMultiContextMenuItem.setText("JdbcLogViewerFrame.popup_menu.crud");

			crudMultiContextMenuItem.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					JFrame crudView = new CRUDViewFrame(getLogTable(), getLogTableModel(), false);
					crudView.setLocationRelativeTo(JdbcLogViewerFrame.this);
					crudView.setVisible(true);
				}
			});
		}
		return crudMultiContextMenuItem;
	}

	/**
	 * This method initializes markPanel
	 *
	 * @return javax.swing.JPanel
	 */
	private JPanel getMarkPanel() {
		if (markPanel == null) {
			FlowLayout flowLayout4 = new FlowLayout();
			flowLayout4.setHgap(10);
			flowLayout4.setVgap(2);
			markLabel = new JLabel();
			markLabel.setText("JdbcLogViewerFrame.status_bar.mark");
			markPanel = new JPanel();
			markPanel.setLayout(flowLayout4);
			markPanel.setBorder(BorderFactory.createLineBorder(Color.gray, 1));
			markPanel.add(markLabel, null);
		}
		return markPanel;
	}

	/**
	 * This method initializes markPanel
	 *
	 * @return javax.swing.JPanel
	 */
	private JPanel getTotalElapsedTimePanel() {
		if (totalElapsedTimePanel == null) {
			FlowLayout flowLayout4 = new FlowLayout();
			flowLayout4.setHgap(10);
			flowLayout4.setVgap(2);
			totalElapsedTimeLabel = new JLabel();
			totalElapsedTimeLabel.setText("JdbcLogViewerFrame.status_bar.total_elapsed_time");
			totalElapsedTimeLabel.setToolTipText("JdbcLogViewerFrame.status_bar.total_elapsed_time.detail");
			totalElapsedTimePanel = new JPanel();
			totalElapsedTimePanel.setLayout(flowLayout4);
			totalElapsedTimePanel.setBorder(BorderFactory.createLineBorder(Color.gray, 1));
			totalElapsedTimePanel.add(totalElapsedTimeLabel, null);
		}
		return totalElapsedTimePanel;
	}

	/**
	 * This method initializes loggerConfigMenuItem
	 *
	 * @return javax.swing.JMenuItem
	 */
	private JMenuItem getLoggerConfigMenuItem() {
		if (loggerConfigMenuItem == null) {
			loggerConfigMenuItem = new JMenuItem();

			loggerConfigMenuItem.setText("JdbcLogViewerFrame.menu.tool.loggerConfig");

			loggerConfigMenuItem.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {

					try {
						// クラスパスにtools.jarがあるかチェックする
						Class.forName("com.sun.tools.attach.VirtualMachine");
					} catch (ClassNotFoundException ex) {
						JOptionPane.showMessageDialog(
								null,
								M17N.get("JdbcLogViewerFrame.error.no_tools"),
								"",
								JOptionPane.ERROR_MESSAGE);
						return;
					}

					JavaProcessDialog dialog = new JavaProcessDialog(JdbcLogViewerFrame.this);

					if (dialog.isLoadSuccess()) {
						dialog.setLocationRelativeTo(JdbcLogViewerFrame.this);
						dialog.setVisible(true);
					} else {
						dialog.dispose();
					}
				}
			});
		}
		return loggerConfigMenuItem;
	}

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		UIManager.put("swing.boldMetal", Boolean.FALSE);

		final File initLogFile;
		if (args.length >= 1) {
			initLogFile = new File(args[0]);
		} else {
			initLogFile = null;
		}

		SwingUtilities.invokeLater(new Runnable() {
			public void run() {
				JdbcLogViewerFrame frame =
					new JdbcLogViewerFrame(initLogFile);
				frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
				frame.setLocationRelativeTo(null);
				frame.setVisible(true);
			}
		});
	}

	private File initLogFile;

	public JdbcLogViewerFrame(File initLogFile) {
		super();
		this.initLogFile = initLogFile;
		initialize();
		restorePreviousState();
		openInitLogFile();
	}

	/**
	 * This method initializes this
	 *
	 * @return void
	 */
	private void initialize() {
		this.setSize(695, 380);
		this.setJMenuBar(getJJMenuBar());
		this.setContentPane(getJContentPane1());
		this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		this.setTitle("JdbcLogViewerFrame.title");

		this.addWindowListener(new java.awt.event.WindowAdapter() {
			public void windowClosing(java.awt.event.WindowEvent e) {
				// 終了時に設定を保存する

				ViewerConfig config = ViewerConfig.getInstance();
				ViewerConfig.LogTable logTable = config.getLogTable();
				ViewerConfig.Frame frame = config.getFrame();

				// 列の表示状態
				logTable.setTimestamp(getTimeCheckBoxMenuItem().isSelected());
				logTable.setLogType(getLogTypeCheckBoxMenuItem().isSelected());
				logTable.setThreadName(getThreadNameCheckBoxMenuItem().isSelected());
				logTable.setConnectionId(getConnectionIdCheckBoxMenuItem().isSelected());
				logTable.setStatementId(getStatementIdCheckBoxMenuItem().isSelected());
				logTable.setSql(getSqlCheckBoxMenuItem().isSelected());
				logTable.setAffectedRows(getAffectedRowsCheckBoxMenuItem().isSelected());
				logTable.setElapsedTime(getElapsedTimeCheckBoxMenuItem().isSelected());
				logTable.setResult(getResultCheckBoxMenuItem().isSelected());
				logTable.setAutoCommit(getAutoCommitCheckBoxMenuItem().isSelected());
				logTable.setTag(getTagCheckBoxMenuItem().isSelected());

				// ウィンドウサイズ
				if (getExtendedState() == JFrame.MAXIMIZED_BOTH) {
					frame.setMaximized(true);
				} else {
					frame.setMaximized(false);
					frame.setDimension(getSize());
				}

				// スプリット位置
				frame.setDividerLocation(getMainSplitPane().getDividerLocation());

				// 自動スクロールモード
				logTable.setAutoScroll(getAutoScrollCheckBoxMenuItem().isSelected());

				// ステータスバー
				frame.setStatusBar(getStatusBarCheckBoxMenuItem().isSelected());

				// 検索
				if (JdbcLogViewerFrame.this.searchDialog != null) {
					JdbcLogViewerFrame.this.searchDialog.saveConfig();
				}

				config.store();
			}
		});

		new DropTarget(this, new LogFileDropTargetAdapter());
	}

	/**
	 * 前回起動時の状態を復元する
	 */
	private void restorePreviousState() {

		ViewerConfig config = ViewerConfig.getInstance();

		// 前回開いたログファイルの復元
		if (config.getLogFile().getPath() != null) {
			this.logFile = new File(config.getLogFile().getPath());

			if (!this.logFile.exists() || !this.logFile.isFile()) {
				this.logFile = null;
			}
		}

		updateReopenComponents();

		// 列の表示状態の復元
		ViewerConfig.LogTable logTable = config.getLogTable();
		getTimeCheckBoxMenuItem().setState(logTable.isTimestamp());
		getLogTypeCheckBoxMenuItem().setState(logTable.isLogType());
		getThreadNameCheckBoxMenuItem().setState(logTable.isThreadName());
		getConnectionIdCheckBoxMenuItem().setState(logTable.isConnectionId());
		getStatementIdCheckBoxMenuItem().setState(logTable.isStatementId());
		getSqlCheckBoxMenuItem().setState(logTable.isSql());
		getElapsedTimeCheckBoxMenuItem().setState(logTable.isElapsedTime());
		getAffectedRowsCheckBoxMenuItem().setState(logTable.isAffectedRows());
		getResultCheckBoxMenuItem().setState(logTable.isResult());
		getAutoCommitCheckBoxMenuItem().setState(logTable.isAutoCommit());
		getTagCheckBoxMenuItem().setState(logTable.isTag());

		refreshColumns();

		// ウィンドウの大きさの復元
		ViewerConfig.Frame frame = config.getFrame();

		if (frame.isMaximized()) {
			setExtendedState(JFrame.MAXIMIZED_BOTH);
		} else {
			this.setSize(frame.getDimension());
		}

		// スプリットペインの分割位置
		getMainSplitPane().setDividerLocation(frame.getDividerLocation());

		// フィルタの復元
		getLogTableModel().updateFilter(getLogTableFilter());

		// 自動スクロールモードの復元
		getAutoScrollCheckBoxMenuItem().setSelected(
				logTable.isAutoScroll());

		// ステータスバーの復元
		getStatusBarCheckBoxMenuItem().setSelected(frame.isStatusBar());
		getStatusBarPanel().setVisible(frame.isStatusBar());
	}

	private LogTableFilter logTableFilter;  //  @jve:decl-index=0:

	void updateLogTableFilter() {
		this.logTableFilter = new LogTableFilter();
		getLogTableModel().updateFilter(this.logTableFilter);
		updateStatusBar();
		onChangeSelectRows();
		getSqlPreviewTextArea().setText("");
		repaintFrame();
	}

	private LogTableFilter getLogTableFilter() {
		if (this.logTableFilter == null) {
			this.logTableFilter = new LogTableFilter();
		}
		return this.logTableFilter;
	}

	Set<String> getThreadNameSet() {
		return getLogTableModel().getThreadNameSet();
	}

	Set<String> getConnectionIdSet() {
		return getLogTableModel().getConnectionIdSet();
	}

	Set<String> getStatementIdSet() {
		return getLogTableModel().getStatementIdSet();
	}

	Set<String> getTagSet() {
		return getLogTableModel().getTagSet();
	}

	/**
	 * ログの詳細を別ウィンドウで開く
	 * @param row
	 */
	private void showDetailLog(int row) {
		LogDetailFrame logDetailFrame = new LogDetailFrame(this, row);
		logDetailFrame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		logDetailFrame.setLocationRelativeTo(this);
		logDetailFrame.setVisible(true);
	}

	/**
	 * ログテーブルの列幅を更新する。
	 */
	private void updateColumnWidth() {
		for (int i = 0; i < getLogTableModel().getColumnCount(); i++) {
			getLogTable().getColumnModel().getColumn(i)
					.setPreferredWidth(getLogTableModel().getPreferredWidth()[i]);
		}
	}

	/**
	 * ログテーブルの列表示を更新する
	 */
	private void refreshColumns() {
		getLogTableModel().refreshVisibleColumns();
		getLogTableModel().fireTableStructureChanged();
		updateColumnWidth();
	}
	/**
	 * ログテーブルに設定されたLogTableModelオブジェクトを取得する。
	 * @return
	 */
	LogTableModel getLogTableModel() {
		return (LogTableModel) getLogTable().getModel();
	}

	/** ログファイルを順次読み込む */
	volatile private LogFileAccessor logAccessor;

	/**
	 * ログファイルをテーブルに読み込む。
	 */
	private void openLogFile() {

		getJContentPane1().remove(getLogFileOpenPanel());
		getJContentPane1().add(getMainSplitPane(), BorderLayout.CENTER);
		getJContentPane1().validate();
		repaintFrame();

		// 既存の行をクリアする
		getLogTableModel().reset();
		onChangeSelectRows();
		getSqlPreviewTextArea().setText("");

		logAccessor = new LogFileAccessor(
				logFile,
				ViewerConfig.getInstance().getLogFile().getCharSet());

		// 先頭から読み込むかどうか確認
		boolean loadOnOpen = showConfirmDialog(
				null,
				M17N.get("JdbcLogViewerFrame.confirm.read_from_head",
							this.logFile.getAbsolutePath()),
	            "",
	            YES_NO_OPTION) == YES_OPTION;

		getStartReadToggleButton().setSelected(true);
		getSuspendToggleButton().setSelected(false);

		// すでに出力されているログを読み込む
		while (true) {
			try {
				LogEntry entry = logAccessor.readNextLog();

				if (entry == null) {
					break;
				}

				if (loadOnOpen) {
					getLogTableModel().addLogEntry(entry);
				}

			} catch (IOException e) {
				systemLogger.error("Loading logfile failed.", e);

				showMessageDialog(null,
						M17N.get("message.unexpected_error", e.getMessage()),
						"",
						ERROR_MESSAGE);

				logAccessor.close();
				logAccessor = null;
				break;
			}
		}

		getLogTable().setModel(getLogTableModel());
		getLogTableModel().fireTableDataChanged();
		updateColumnWidth();

		ViewerConfig.getInstance().getLogFile().setPath(this.logFile.getAbsolutePath());
		updateReopenComponents();

		if (logAccessor == null) {
			// 最初の読み込み時にエラーがあった場合は更新しない
			return;
		}

		final boolean[] updated = new boolean[] { true };

		// 更新されたログを読み込む
		new SwingWorker<Object, Object>() {
		    @Override
		    protected Object doInBackground() throws Exception {

				LogFileAccessor ownAccessor = logAccessor;

				while (true) {
					if (ownAccessor != logAccessor) {
						// ログファイルが変わった/開き直された場合、このスレッドは終了
						break;
					}

					LogEntry entry = null;

					try {
						entry = logAccessor.readNextLog();

					} catch (IOException e) {
						systemLogger.error("Loading log file failed.", e);

						showMessageDialog(null,
								M17N.get("message.unexpected_error", e.getMessage()),
								"",
								ERROR_MESSAGE);

						break;
					}

					if (entry != null && getStartReadToggleButton().isSelected()) {
						getLogTableModel().addLogEntryWithFireUpdate(entry);
						updated[0] = true;
						continue;
					}

					try {
						Thread.sleep(
								ViewerConfig.getInstance().getLogFile().getReadInterval());

					} catch (InterruptedException e) {
						systemLogger.error(e);
						break;
					}

					if (updated[0]) {
						repaintFrame();	// 先にrepaintしないとスクロールの高さが確定しない
						updateScroll();
						repaintFrame();	// スクロール更新後にrepaintしないと表示が崩れたままになる
						updateStatusBar();
						updated[0] = false;
					}
				}

				ownAccessor.close();
				return null;
			}
		}.execute();
	}

	/**
	 * ログファイルをエクスプローラからドラッグ＆ドロップで開けるようにする
	 */
	private class LogFileDropTargetAdapter extends DropTargetAdapter {

		@SuppressWarnings("unchecked")
		public void drop(DropTargetDropEvent dtde) {
			Transferable transfer = dtde.getTransferable();

			if (!transfer.isDataFlavorSupported(DataFlavor.javaFileListFlavor)) {
				return;
			}

			dtde.acceptDrop(DnDConstants.ACTION_COPY_OR_MOVE);

			try {
				List<File> fileList = (List<File>)
					transfer.getTransferData(DataFlavor.javaFileListFlavor);

				if (fileList != null && !fileList.isEmpty()) {
					logFile = fileList.get(0);
					openLogFile();
				}

			} catch (Exception e) {
				systemLogger.error(
						"Loading log file vie D&D failed.", e);
			}
		}

	}

	/**
	 * フィルタ設定画面を開く
	 */
	private void openFilterConfigFrame(boolean fromFilterButton) {

		// 最初に開くタブ
		int tabIndex;

		if (fromFilterButton) {
			// フィルターボタン押下時：種別タブを開く
			tabIndex = 0;
		} else {
			if (clickedHeaderColumnIndex == LOG_TYPE.getColumnIndex()) {
				// 種別
				tabIndex = 0;
			} else if (clickedHeaderColumnIndex == SQL.getColumnIndex()) {
				// SQL
				tabIndex = 1;
			} else {
				// その他
				tabIndex = 2;
			}
		}

		FilterConfigFrame filterConfigFrame =
			new FilterConfigFrame(JdbcLogViewerFrame.this, tabIndex);
		filterConfigFrame.setDefaultCloseOperation(DISPOSE_ON_CLOSE);
		filterConfigFrame.setLocationRelativeTo(this);
		filterConfigFrame.setVisible(true);
	}

	/**
	 * 右クリックされたカラムを非表示にする。
	 */
	private void hideThisColumn() {

		if (this.clickedHeaderColumnIndex == TIMESTAMP.getColumnIndex()) {
			getTimeCheckBoxMenuItem().setState(false);
			return;
		}

		if (this.clickedHeaderColumnIndex == LOG_TYPE.getColumnIndex()) {
			getLogTypeCheckBoxMenuItem().setState(false);
			return;
		}

		if (this.clickedHeaderColumnIndex == THREAD_NAME.getColumnIndex()) {
			getThreadNameCheckBoxMenuItem().setState(false);
			return;
		}

		if (this.clickedHeaderColumnIndex == CONNECTION_ID.getColumnIndex()) {
			getConnectionIdCheckBoxMenuItem().setState(false);
			return;
		}

		if (this.clickedHeaderColumnIndex == STATEMENT_ID.getColumnIndex()) {
			getStatementIdCheckBoxMenuItem().setState(false);
			return;
		}

		if (this.clickedHeaderColumnIndex == SQL.getColumnIndex()) {
			getSqlCheckBoxMenuItem().setState(false);
			return;
		}

		if (this.clickedHeaderColumnIndex == AFFECTED_ROWS.getColumnIndex()) {
			getAffectedRowsCheckBoxMenuItem().setState(false);
			return;
		}

		if (this.clickedHeaderColumnIndex == ELAPSED_TIME.getColumnIndex()) {
			getElapsedTimeCheckBoxMenuItem().setState(false);
			return;
		}

		if (this.clickedHeaderColumnIndex == RESULT.getColumnIndex()) {
			getResultCheckBoxMenuItem().setState(false);
			return;
		}

		if (this.clickedHeaderColumnIndex == AUTO_COMMIT.getColumnIndex()) {
			getAutoCommitCheckBoxMenuItem().setState(false);
			return;
		}

		if (this.clickedHeaderColumnIndex == TAG.getColumnIndex()) {
			getTagCheckBoxMenuItem().setState(false);
			return;
		}

		throw new IllegalArgumentException("realColumnIndexが不正:" + clickedHeaderColumnIndex);
	}

	/**
	 * ステータスバーの表示内容を更新する。
	 */
	private void updateStatusBar() {
		if (!getStatusBarPanel().isVisible()) {
			return;
		}

		this.lastLogTimeLabel.setText(getLogTableModel().getLastLogTime());
		this.selectedRowsCountLabel.setText("0");
		this.visibleRowsCountLabel.setText(
				String.valueOf(getLogTableModel().getRowCount()));
		this.allRowsCountLabel.setText(
				String.valueOf(getLogTableModel().getAllRowsCount()));
	}

	private void updateScroll() {
		if (getAutoScrollCheckBoxMenuItem().isSelected()) {
			JScrollBar verticalScrollBar = getLogTableScrollPane().getVerticalScrollBar();
			verticalScrollBar.repaint();
			verticalScrollBar.setValue(Integer.MAX_VALUE);
		}
	}

	/**
	 * ログファイルを開き直しに関するコンポーネントの表示設定
	 */
	private void updateReopenComponents() {

		if (this.reopenButton == null || this.reopenMenuItem == null) {
			return;
		}

		if (this.logFile != null) {
			this.reopenButton.setEnabled(true);
			this.reopenButton.setToolTipText(
					M17N.get("JdbcLogViewerFrame.button.reopen") + " " +
					this.logFile.getAbsolutePath());
			this.reopenMenuItem.setEnabled(true);
			this.reopenMenuItem.setToolTipText(this.logFile.getAbsolutePath());
		} else {
			this.reopenButton.setEnabled(false);
			this.reopenButton.setToolTipText(
					M17N.get("JdbcLogViewerFrame.button.reopen"));
			this.reopenMenuItem.setEnabled(false);
			this.reopenMenuItem.setToolTipText("");
		}
	}

	private void repaintFrame() {
		this.repaint();
	}

	/**
	 * コマンドライン引数で与えられたファイルを開く
	 */
	private void openInitLogFile() {
		if (this.initLogFile == null) {
			// 引数がない場合
			return;
		}

		if (!this.initLogFile.exists()) {
			// 引数のファイルが存在しない場合
			systemLogger.warn("Log file does not exist."
				+ this.initLogFile.getAbsolutePath());
		}

		this.logFile = this.initLogFile;
		updateReopenComponents();
		openLogFile();
	}

	/**
	 * 検索ダイアログを開く
	 */
	private void openSearchDialog() {
		if (this.searchDialog == null) {
			this.searchDialog = new SearchDialog(this);
		}
		this.searchDialog.setLocationRelativeTo(this);
		this.searchDialog.setVisible(true);
	}

	void search(
			String word,
			boolean regexp,
			boolean forward,
			boolean circulating,
			boolean caseSensitive) {

		// SQL列が表示されていない場合は表示する
		getSqlCheckBoxMenuItem().setState(true);

		setSearchWord(word);
		this.searchCirculating = circulating;
		this.searchCaseSensitive = caseSensitive;

		// 検索開始行
		int selectedRow = getLogTable().getSelectedRow();
		int row = selectedRow;

		if (row < 0) {
			row = 0;
		}

		int rowCount = getLogTable().getRowCount();

		if (regexp) {
			setSearchPattern(word);

			if (this.searchPattern == null) {
				return;
			}
		}

		while (true) {
			if (forward) {
				row++;
			} else {
				row--;
			}

			if (row < 0 || row > rowCount - 1) {
				break;
			}

			String sql = getSqlValue(row);

			if (searchMatches(sql)) {
				getLogTable().changeSelection(row, SQL.getColumnIndex(), false, false);
				onChangeSelectRows();
				return;
			}
		}

		if (circulating) {
			// 循環検索有効の場合、先頭/末尾に戻って検索
			if (forward) {
				row = 0;
			} else {
				row = rowCount -1;
			}

			while (true) {
				String sql = getSqlValue(row);

				if (searchMatches(sql)) {
					getLogTable().changeSelection(row, SQL.getColumnIndex(), false, false);
					return;
				}

				if (row == selectedRow) {
					// 最初の行に戻ったので終了
					break;
				}

				if (forward) {
					row++;
				} else {
					row--;
				}

				if (row < 0 || row > rowCount - 1) {
					break;
				}
			}

		}

		showMessageDialog(
				null,
				M17N.get("JdbcLogViewerFrame.search.not_found"),
				"",
				INFORMATION_MESSAGE);
	}

	/**
	 * 指定行のSQLを取得する。
	 * @param row
	 * @return
	 */
	private String getSqlValue(int row) {
		LogEntry entry = getLogTableModel().getLogEntry(row);

		if (entry == null) {
			return null;
		}

		return entry.getSql();
	}

	/**
	 * SQLを検索し、該当行に色をつける
	 * @param word 検索文字列
	 * @param regexp 正規表現で検索するならばtrue
	 * @param circulating 循環検索するならばtrue
	 * @param caseSensitive 大文字と小文字を区別するならばtrue
	 */
	void searchAndMark(String word, boolean regexp, boolean circulating, boolean caseSensitive) {
		getSqlCheckBoxMenuItem().setState(true);

		this.searchAndMark = true;

		this.markLabel.setText(
				M17N.get("JdbcLogViewerFrame.status_bar.mark") +
				word);

		setSearchWord(word);

		// ここでは使わないが、ショートカットキーで再検索するときのために保存しておく
		this.searchCirculating = circulating;

		this.searchCaseSensitive = caseSensitive;

		if (regexp) {
			setSearchPattern(word);
		} else {
			this.searchPattern = null;
		}

		repaintFrame();
	}

	private void setSearchPattern(String word) {
		try {
			// 構文チェック
			ThreadSafeUtils.matches("", word, true, this.searchCaseSensitive);
			this.searchPattern = word;
		} catch (PatternSyntaxException e) {
			showMessageDialog(
					null,
					M17N.get("JdbcLogViewerFrame.message.illegal_regex"),
					"",
					ERROR_MESSAGE);
			this.searchPattern = null;
		}
	}

	private void setSearchWord(String word) {
		this.searchWord = word;
		boolean enabled = StringUtils.isNotEmpty(this.searchWord);

		getSearchForwardMenuItem().setEnabled(enabled);
		getSearhBackMenuItem().setEnabled(enabled);
	}

	/**
	 * マーク行に色をつけるDefaultTableCellRenderer
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
				if (!searchAndMark) {
					setBackground(Color.WHITE);
				} else {
					String sql = getSqlValue(row);

					if (searchMatches(sql)) {
						setBackground(Color.YELLOW);
					} else {
						setBackground(Color.WHITE);
					}
				}
			}

			LogEntry entry = getLogTableModel().getLogEntry(row);

			if (entry != null && entry.getResult() == Result.FAILURE) {
				// 失敗行の文字色を赤にする
				setForeground(Color.RED);
			} else {
				setForeground(Color.BLACK);
			}

			return this;
		}
	}

	private boolean searchMatches(String sql) {

		if (sql == null) {
			return false;
		} else if (StringUtils.isEmpty(this.searchWord)) {
			return false;
		} else if (this.searchPattern != null) {
			return ThreadSafeUtils.matches(sql, this.searchPattern, true, this.searchCaseSensitive);
		} else {
			if (this.searchCaseSensitive) {
				return sql.contains(this.searchWord);
			} else {
				return sql.toLowerCase().contains(this.searchWord.toLowerCase());
			}
		}
	}

}  // @jve:decl-index=0:visual-constraint="-5,3"
