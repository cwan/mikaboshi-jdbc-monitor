package net.mikaboshi.jdbc.monitor.viewer;

import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;
import javax.swing.JTextArea;
import javax.swing.SwingUtilities;

import net.mikaboshi.gui.GuiUtils;
import net.mikaboshi.jdbc.monitor.SqlUtils;
import net.mikaboshi.jdbc.monitor.ViewerConfig;
import net.mikaboshi.jdbc.monitor.ViewerConfig.FormatType;

/**
 * ログ整形ポップアップメニュー
 *
 * @since 1.4.0
 * @version 1.4.3
 */
public class SqlFormatPopupMenu extends JPopupMenu {

	private static final long serialVersionUID = 1L;

	private final JTextArea sqlTextArea;
	private JMenuItem formatMenuItem = null;
	private JMenuItem linizeMenuItem = null;
	private JMenuItem rawMenuItem = null;
	private final LogEntryProvider logEntryProvider;

	public SqlFormatPopupMenu(JTextArea sqlTextArea, LogEntryProvider logEntryProvider) {
		this.sqlTextArea = sqlTextArea;
		this.logEntryProvider = logEntryProvider;

		sqlTextArea.addMouseListener(new java.awt.event.MouseAdapter() {
			// 右クリックしたときに、SQLの整形タイプをポップアップメニューで選択する
			public void mouseClicked(java.awt.event.MouseEvent e) {
				if (SwingUtilities.isRightMouseButton(e)) {
					SqlFormatPopupMenu.this.show(e.getComponent(), e.getX(), e.getY());
				}
			}
		});

		add(getFormatMenuItem());
		add(getRawMenuItem());
		add(getLinezeMenuItem());
	}

	/**
	 * This method initializes formatMenuItem
	 *
	 * @return javax.swing.JMenuItem
	 */
	private JMenuItem getFormatMenuItem() {
		if (formatMenuItem == null) {
			formatMenuItem = new JMenuItem();
			formatMenuItem.setText("PreferencesFrame.format.format");

			formatMenuItem.addActionListener(new java.awt.event.ActionListener() {
				public void actionPerformed(java.awt.event.ActionEvent e) {
					sqlTextArea.setText(
							SqlUtils.format(logEntryProvider.getCurrentLogEntry().getSql()));
					GuiUtils.setCeil(sqlTextArea);

					ViewerConfig.getInstance().setFormatTypeByEnum(FormatType.FORMAT);
				}
			});
		}
		return formatMenuItem;
	}

	/**
	 * This method initializes linizeMenuItem
	 *
	 * @return javax.swing.JMenuItem
	 */
	private JMenuItem getLinezeMenuItem() {
		if (linizeMenuItem == null) {
			linizeMenuItem = new JMenuItem();
			linizeMenuItem.setText("PreferencesFrame.format.line");

			linizeMenuItem.addActionListener(new java.awt.event.ActionListener() {
				public void actionPerformed(java.awt.event.ActionEvent e) {
					sqlTextArea.setText(
							SqlUtils.linize(logEntryProvider.getCurrentLogEntry().getSql()));

					ViewerConfig.getInstance().setFormatTypeByEnum(FormatType.LINE);
				}
			});
		}
		return linizeMenuItem;
	}

	/**
	 * This method initializes rawMenuItem
	 *
	 * @return javax.swing.JMenuItem
	 */
	private JMenuItem getRawMenuItem() {
		if (rawMenuItem == null) {
			rawMenuItem = new JMenuItem();
			rawMenuItem.setText("PreferencesFrame.format.raw");

			rawMenuItem.addActionListener(new java.awt.event.ActionListener() {
				public void actionPerformed(java.awt.event.ActionEvent e) {
					sqlTextArea.setText(logEntryProvider.getCurrentLogEntry().getSql());

					ViewerConfig.getInstance().setFormatTypeByEnum(FormatType.RAW);
				}
			});
		}
		return rawMenuItem;
	}
}
