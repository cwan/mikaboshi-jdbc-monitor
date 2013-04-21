package net.mikaboshi.jdbc.monitor.viewer;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Frame;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.KeyEvent;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;

import net.mikaboshi.gui.GuiUtils;
import net.mikaboshi.jdbc.monitor.ViewerConfig;
import net.mikaboshi.jdbc.monitor.ViewerConfig.LogTableSearch;

import org.apache.commons.lang.StringUtils;

public class SearchDialog extends JDialog {

	private static final long serialVersionUID = 1L;
	private JPanel jContentPane = null;
	private JPanel mainPanel = null;
	private JPanel buttonsPanel = null;
	private JButton searchForwardButton = null;
	private JButton searchBackButton = null;
	private JButton markButton = null;
	private JButton closeButton = null;
	private JLabel searcWordLabel = null;
	private JCheckBox regularExpressionCheckBox = null;
	private JComboBox searchWordComboBox = null;
	private JCheckBox circulatingCheckBox = null;
	private JdbcLogViewerFrame opener = null;
	private JCheckBox caseSensitiveCheckBox = null;

	/**
	 * This method initializes regularExpressionCheckBox
	 *
	 * @return javax.swing.JCheckBox
	 */
	private JCheckBox getRegularExpressionCheckBox() {
		if (regularExpressionCheckBox == null) {
			regularExpressionCheckBox = new JCheckBox();
			regularExpressionCheckBox.setText("SearchDialog.regularExpressionCheckBox");
			regularExpressionCheckBox.setMnemonic(KeyEvent.VK_X);
			regularExpressionCheckBox.setToolTipText("");
		}
		return regularExpressionCheckBox;
	}


	/**
	 * This method initializes searchWordComboBox
	 *
	 * @return javax.swing.JComboBox
	 */
	private JComboBox getSearchWordComboBox() {
		if (searchWordComboBox == null) {
			searchWordComboBox = new JComboBox();
			searchWordComboBox.setEditable(true);
		}
		return searchWordComboBox;
	}


	/**
	 * This method initializes circulatingCheckBox
	 *
	 * @return javax.swing.JCheckBox
	 */
	private JCheckBox getCirculatingCheckBox() {
		if (circulatingCheckBox == null) {
			circulatingCheckBox = new JCheckBox();
			circulatingCheckBox.setText("SearchDialog.circulatingCheckBox");
			circulatingCheckBox.setMnemonic(KeyEvent.VK_R);
			circulatingCheckBox.setToolTipText("SearchDialog.circulatingCheckBox.tooltip");
		}
		return circulatingCheckBox;
	}

	/**
	 * @param owner
	 */
	public SearchDialog(Frame owner) {
		super(owner);
		setDefaultCloseOperation(HIDE_ON_CLOSE);
		this.opener = (JdbcLogViewerFrame) owner;
		initialize();
	}

	/**
	 * This method initializes this
	 *
	 * @return void
	 */
	private void initialize() {
		this.setSize(461, 198);
		this.setTitle("SearchDialog.title");

		this.setContentPane(getJContentPane());

		this.addWindowListener(new java.awt.event.WindowAdapter() {
			public void windowActivated(java.awt.event.WindowEvent e) {
				getSearchWordComboBox().requestFocusInWindow();
			}
		});

		// 設定を読み込む
		LogTableSearch config = ViewerConfig.getInstance().getLogTableSearch();

		if (!config.getSearchWord().isEmpty()) {

			for (String word : config.getSearchWord()) {
				getSearchWordComboBox().addItem(word);
			}
		}

		getRegularExpressionCheckBox().setSelected(config.isRegularExpression());
		getCirculatingCheckBox().setSelected(config.isCirculating());
		getCaseSensitiveCheckBox().setSelected(config.isCaseSensitive());
	}

	/**
	 * This method initializes jContentPane
	 *
	 * @return javax.swing.JPanel
	 */
	private JPanel getJContentPane() {
		if (jContentPane == null) {
			BorderLayout borderLayout = new BorderLayout();
			borderLayout.setHgap(0);
			borderLayout.setVgap(0);
			jContentPane = new JPanel();
			jContentPane.setLayout(borderLayout);
			jContentPane.add(getMainPanel(), BorderLayout.CENTER);
			jContentPane.add(getButtonsPanel(), BorderLayout.SOUTH);

			// ESCで閉じる
			GuiUtils.closeByESC(this, jContentPane);
		}
		return jContentPane;
	}

	/**
	 * This method initializes mainPanel
	 *
	 * @return javax.swing.JPanel
	 */
	private JPanel getMainPanel() {
		if (mainPanel == null) {
			GridBagConstraints gridBagConstraints8 = new GridBagConstraints();
			gridBagConstraints8.insets = new Insets(0, 20, 0, 0);
			gridBagConstraints8.fill = GridBagConstraints.HORIZONTAL;
			gridBagConstraints8.gridwidth = 0;
			GridBagConstraints gridBagConstraints7 = new GridBagConstraints();
			gridBagConstraints7.gridx = 0;
			gridBagConstraints7.gridwidth = 0;
			gridBagConstraints7.insets = new Insets(0, 20, 0, 0);
			gridBagConstraints7.fill = GridBagConstraints.HORIZONTAL;
			gridBagConstraints7.gridy = 2;
			GridBagConstraints gridBagConstraints5 = new GridBagConstraints();
			gridBagConstraints5.fill = GridBagConstraints.HORIZONTAL;
			gridBagConstraints5.gridy = 0;
			gridBagConstraints5.weightx = 1.0;
			gridBagConstraints5.ipady = 0;
			gridBagConstraints5.insets = new Insets(0, 0, 0, 20);
			gridBagConstraints5.gridx = 2;
			GridBagConstraints gridBagConstraints6 = new GridBagConstraints();
			gridBagConstraints6.gridx = 0;
			gridBagConstraints6.gridwidth = 0;
			gridBagConstraints6.insets = new Insets(5, 20, 0, 0);
			gridBagConstraints6.fill = GridBagConstraints.HORIZONTAL;
			gridBagConstraints6.gridy = 1;
			GridBagConstraints gridBagConstraints4 = new GridBagConstraints();
			gridBagConstraints4.gridx = 0;
			gridBagConstraints4.gridwidth = 1;
			gridBagConstraints4.insets = new Insets(0, 20, 0, 10);
			gridBagConstraints4.fill = GridBagConstraints.HORIZONTAL;
			gridBagConstraints4.gridy = 0;
			searcWordLabel = new JLabel();
			searcWordLabel.setText("SearchDialog.searcWordLabel");
			mainPanel = new JPanel();
			mainPanel.setLayout(new GridBagLayout());
			mainPanel.add(searcWordLabel, gridBagConstraints4);
			mainPanel.add(getRegularExpressionCheckBox(), gridBagConstraints6);
			mainPanel.add(getSearchWordComboBox(), gridBagConstraints5);
			mainPanel.add(getCirculatingCheckBox(), gridBagConstraints7);
			mainPanel.add(getCaseSensitiveCheckBox(), gridBagConstraints8);
		}
		return mainPanel;
	}

	/**
	 * This method initializes buttonsPanel
	 *
	 * @return javax.swing.JPanel
	 */
	private JPanel getButtonsPanel() {
		if (buttonsPanel == null) {
			GridBagConstraints gridBagConstraints3 = new GridBagConstraints();
			gridBagConstraints3.insets = new Insets(0, 5, 0, 5);
			GridBagConstraints gridBagConstraints2 = new GridBagConstraints();
			gridBagConstraints2.insets = new Insets(0, 5, 0, 5);
			GridBagConstraints gridBagConstraints1 = new GridBagConstraints();
			gridBagConstraints1.insets = new Insets(0, 5, 0, 5);
			GridBagConstraints gridBagConstraints = new GridBagConstraints();
			gridBagConstraints.ipady = 0;
			gridBagConstraints.insets = new Insets(0, 5, 0, 5);
			buttonsPanel = new JPanel();
			buttonsPanel.setLayout(new GridBagLayout());
			buttonsPanel.setPreferredSize(new Dimension(358, 35));
			buttonsPanel.add(getSearchForwardButton(), gridBagConstraints);
			buttonsPanel.add(getSearchBackButton(), gridBagConstraints1);
			buttonsPanel.add(getMarkButton(), gridBagConstraints2);
			buttonsPanel.add(getCloseButton(), gridBagConstraints3);
		}
		return buttonsPanel;
	}

	/**
	 * This method initializes searchForwardButton
	 *
	 * @return javax.swing.JButton
	 */
	private JButton getSearchForwardButton() {
		if (searchForwardButton == null) {
			searchForwardButton = new JButton();
			searchForwardButton.setText("SearchDialog.searchForwardButton");
			searchForwardButton.setMnemonic(KeyEvent.VK_F);
			searchForwardButton.setToolTipText("SearchDialog.searchForwardButton.tooltip");

			searchForwardButton.addActionListener(new java.awt.event.ActionListener() {
				public void actionPerformed(java.awt.event.ActionEvent e) {
					opener.search(
							getSearchWordComboBox().getSelectedItem().toString(),
							getRegularExpressionCheckBox().isSelected(),
							true,
							getCirculatingCheckBox().isSelected(),
							getCaseSensitiveCheckBox().isSelected());
					addCurrentItem();
				}
			});
		}
		return searchForwardButton;
	}

	/**
	 * This method initializes searchBackButton
	 *
	 * @return javax.swing.JButton
	 */
	private JButton getSearchBackButton() {
		if (searchBackButton == null) {
			searchBackButton = new JButton();
			searchBackButton.setText("SearchDialog.searchBackButton");
			searchBackButton.setMnemonic(KeyEvent.VK_B);
			searchBackButton.setToolTipText("SearchDialog.searchBackButton.tooltip");

			searchBackButton.addActionListener(new java.awt.event.ActionListener() {
				public void actionPerformed(java.awt.event.ActionEvent e) {
					opener.search(
							getSearchWordComboBox().getSelectedItem().toString(),
							getRegularExpressionCheckBox().isSelected(),
							false,
							getCirculatingCheckBox().isSelected(),
							getCaseSensitiveCheckBox().isSelected());
					addCurrentItem();
				}
			});
		}
		return searchBackButton;
	}

	/**
	 * This method initializes markButton
	 *
	 * @return javax.swing.JButton
	 */
	private JButton getMarkButton() {
		if (markButton == null) {
			markButton = new JButton();
			markButton.setText("SearchDialog.markButton");
			markButton.setToolTipText("SearchDialog.markButton.tooltip");
			markButton.setMnemonic(KeyEvent.VK_M);

			markButton.addActionListener(new java.awt.event.ActionListener() {
				public void actionPerformed(java.awt.event.ActionEvent e) {
					opener.searchAndMark(
							getSearchWordComboBox().getSelectedItem().toString(),
							getRegularExpressionCheckBox().isSelected(),
							getCirculatingCheckBox().isSelected(),
							getCaseSensitiveCheckBox().isSelected());
					addCurrentItem();
				}
			});
		}
		return markButton;
	}

	/**
	 * This method initializes closeButton
	 *
	 * @return javax.swing.JButton
	 */
	private JButton getCloseButton() {
		if (closeButton == null) {
			closeButton = new JButton();
			closeButton.setText("SearchDialog.closeButton");
			closeButton.setMnemonic(KeyEvent.VK_C);
			closeButton.addActionListener(new java.awt.event.ActionListener() {
				public void actionPerformed(java.awt.event.ActionEvent e) {
					setVisible(false);
				}
			});
		}
		return closeButton;
	}

	private void addCurrentItem() {

		if (getSearchWordComboBox().getSelectedIndex() != -1) {
			return;
		}

		String word = getSearchWordComboBox().getSelectedItem().toString();

		if (StringUtils.isEmpty(word)) {
			return;
		}

		// 既存項目のチェック
		for (int i = 0; i < getSearchWordComboBox().getItemCount(); i++) {
			String item = getSearchWordComboBox().getItemAt(i).toString();

			if (word.equals(item)) {
				getSearchWordComboBox().remove(i);
				break;
			}
		}

		// 先頭に追加する
		getSearchWordComboBox().insertItemAt(word, 0);
	}


	/**
	 * This method initializes caseSensitiveCheckBox
	 *
	 * @return javax.swing.JCheckBox
	 */
	private JCheckBox getCaseSensitiveCheckBox() {
		if (caseSensitiveCheckBox == null) {
			caseSensitiveCheckBox = new JCheckBox();
			caseSensitiveCheckBox.setMnemonic(KeyEvent.VK_S);
			caseSensitiveCheckBox.setToolTipText("");
			caseSensitiveCheckBox.setText("SearchDialog.caseSensitiveCheckBox");
		}
		return caseSensitiveCheckBox;
	}

	/**
	 * 設定を保存する。
	 * @since 1.4.2
	 */
	protected void saveConfig() {

		// 設定を読み込む
		LogTableSearch config = ViewerConfig.getInstance().getLogTableSearch();

		List<String> searchWord = new ArrayList<String>();

		// 16個まで保存する
		for (int i = 0, len = getSearchWordComboBox().getItemCount(); i < len && i < 16; i++) {

			searchWord.add(getSearchWordComboBox().getItemAt(i).toString());
		}

		config.setSearchWord(searchWord);
		config.setRegularExpression(getRegularExpressionCheckBox().isSelected());
		config.setCirculating(getCirculatingCheckBox().isSelected());
		config.setCaseSensitive(getCaseSensitiveCheckBox().isSelected());
	}

}  //  @jve:decl-index=0:visual-constraint="10,24"
