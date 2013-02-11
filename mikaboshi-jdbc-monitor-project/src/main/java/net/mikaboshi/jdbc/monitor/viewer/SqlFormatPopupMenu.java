package net.mikaboshi.jdbc.monitor.viewer;

import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;
import javax.swing.JTextArea;
import javax.swing.SwingUtilities;

import net.mikaboshi.gui.GuiUtils;
import net.mikaboshi.jdbc.monitor.SqlUtils;
import net.mikaboshi.jdbc.monitor.ViewerConfig;

/**
 *
 * @since 1.4.0
 */
public class SqlFormatPopupMenu extends JPopupMenu {

	private static final long serialVersionUID = 1L;

	private final JTextArea sqlTextArea;
	private JMenuItem formatSqlMenuItem = null;
	private JMenuItem serializeSqlMenuItem = null;

	public SqlFormatPopupMenu(JTextArea sqlTextArea) {
		this.sqlTextArea = sqlTextArea;

		sqlTextArea.addMouseListener(new java.awt.event.MouseAdapter() {
			// 右クリックしたときに、SQLを整形するか1行表示するか、ポップアップメニューで選択する
			public void mouseClicked(java.awt.event.MouseEvent e) {
				if (SwingUtilities.isRightMouseButton(e)) {
					SqlFormatPopupMenu.this.show(e.getComponent(), e.getX(), e.getY());
				}
			}
		});

		add(getFormatSqlMenuItem());
		add(getSerializeSqlMenuItem());
	}

	/**
	 * This method initializes formatSqlMenuItem
	 *
	 * @return javax.swing.JMenuItem
	 */
	private JMenuItem getFormatSqlMenuItem() {
		if (formatSqlMenuItem == null) {
			formatSqlMenuItem = new JMenuItem();
			formatSqlMenuItem.setText("LogDetailFrame.menu.format");

			formatSqlMenuItem.addActionListener(new java.awt.event.ActionListener() {
				public void actionPerformed(java.awt.event.ActionEvent e) {
					sqlTextArea.setText(
							SqlUtils.format(sqlTextArea.getText()));
					GuiUtils.setCeil(sqlTextArea);

					ViewerConfig.getInstance().setDetailSqlFormat(true);
				}
			});
		}
		return formatSqlMenuItem;
	}

	/**
	 * This method initializes serializeSqlMenuItem
	 *
	 * @return javax.swing.JMenuItem
	 */
	private JMenuItem getSerializeSqlMenuItem() {
		if (serializeSqlMenuItem == null) {
			serializeSqlMenuItem = new JMenuItem();
			serializeSqlMenuItem.setText("LogDetailFrame.menu.line");

			serializeSqlMenuItem.addActionListener(new java.awt.event.ActionListener() {
				public void actionPerformed(java.awt.event.ActionEvent e) {
					sqlTextArea.setText(
							SqlUtils.replaceCrLf(sqlTextArea.getText()));

					ViewerConfig.getInstance().setDetailSqlFormat(false);
				}
			});
		}
		return serializeSqlMenuItem;
	}
}
