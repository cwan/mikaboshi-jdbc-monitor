package net.mikaboshi.jdbc.monitor.viewer;

import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.nio.charset.Charset;

import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.WindowConstants;

import net.mikaboshi.gui.GuiUtils;
import net.mikaboshi.gui.IntegerInputVerifier;
import net.mikaboshi.jdbc.monitor.M17N;
import net.mikaboshi.jdbc.monitor.ViewerConfig;

public class PreferencesFrame extends JFrame {

	private static final long serialVersionUID = 1L;
	private JPanel jContentPane = null;
	private JLabel encodingLabel = null;
	private JComboBox encodingComboBox = null;
	private JLabel readInvervalLabel = null;
	private JTextField readIntervalTextField = null;
	private JButton okButton = null;
	private JButton cancelButton = null;

	private JLabel detailSqlFormatLabel = null;
	private JCheckBox formatSqlCheckBox = null;

	/**
	 * This is the default constructor
	 */
	public PreferencesFrame() {
		super();
		initialize();
	}

	/**
	 * This method initializes this
	 *
	 * @return void
	 */
	private void initialize() {
		this.setSize(410, 200);
		this.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
		this.setContentPane(getJContentPane());
		this.setTitle("PreferencesFrame.title");
	}

	/**
	 * This method initializes jContentPane
	 *
	 * @return javax.swing.JPanel
	 */
	private JPanel getJContentPane() {
		if (jContentPane == null) {
			GridBagConstraints gridBagConstraints21 = new GridBagConstraints();
			gridBagConstraints21.gridx = 1;
			gridBagConstraints21.insets = new Insets(5, 10, 20, 10);
			gridBagConstraints21.anchor = GridBagConstraints.WEST;
			gridBagConstraints21.gridy = 2;
			GridBagConstraints gridBagConstraints12 = new GridBagConstraints();
			gridBagConstraints12.gridx = 0;
			gridBagConstraints12.anchor = GridBagConstraints.WEST;
			gridBagConstraints12.insets = new Insets(5, 10, 20, 10);
			gridBagConstraints12.gridy = 2;
			detailSqlFormatLabel = new JLabel();
			detailSqlFormatLabel.setText("PreferencesFrame.detail_sql");
			GridBagConstraints gridBagConstraints5 = new GridBagConstraints();
			gridBagConstraints5.gridx = 1;
			gridBagConstraints5.anchor = GridBagConstraints.WEST;
			gridBagConstraints5.insets = new Insets(5, 10, 10, 0);
			gridBagConstraints5.gridy = 3;
			GridBagConstraints gridBagConstraints4 = new GridBagConstraints();
			gridBagConstraints4.gridx = 0;
			gridBagConstraints4.anchor = GridBagConstraints.EAST;
			gridBagConstraints4.ipadx = 40;
			gridBagConstraints4.insets = new Insets(5, 0, 10, 10);
			gridBagConstraints4.gridy = 3;
			GridBagConstraints gridBagConstraints2 = new GridBagConstraints();
			gridBagConstraints2.fill = GridBagConstraints.NONE;
			gridBagConstraints2.gridy = 1;
			gridBagConstraints2.weightx = 1.0;
			gridBagConstraints2.anchor = GridBagConstraints.WEST;
			gridBagConstraints2.insets = new Insets(5, 10, 5, 10);
			gridBagConstraints2.ipady = 2;
			gridBagConstraints2.ipadx = 2;
			gridBagConstraints2.gridx = 1;
			GridBagConstraints gridBagConstraints11 = new GridBagConstraints();
			gridBagConstraints11.gridx = 0;
			gridBagConstraints11.anchor = GridBagConstraints.WEST;
			gridBagConstraints11.insets = new Insets(5, 10, 5, 10);
			gridBagConstraints11.gridy = 1;
			readInvervalLabel = new JLabel();
			readInvervalLabel.setText("PreferencesFrame.log_file.read_interval.ms");
			GridBagConstraints gridBagConstraints1 = new GridBagConstraints();
			gridBagConstraints1.fill = GridBagConstraints.NONE;
			gridBagConstraints1.gridx = 1;
			gridBagConstraints1.gridy = 0;
			gridBagConstraints1.ipadx = 0;
			gridBagConstraints1.ipady = 0;
			gridBagConstraints1.weightx = 1.0;
			gridBagConstraints1.anchor = GridBagConstraints.WEST;
			gridBagConstraints1.gridwidth = 1;
			gridBagConstraints1.insets = new Insets(10, 10, 5, 10);
			GridBagConstraints gridBagConstraints = new GridBagConstraints();
			gridBagConstraints.gridx = 0;
			gridBagConstraints.ipadx = 0;
			gridBagConstraints.ipady = 0;
			gridBagConstraints.anchor = GridBagConstraints.WEST;
			gridBagConstraints.insets = new Insets(10, 10, 5, 10);
			gridBagConstraints.gridy = 0;
			encodingLabel = new JLabel();
			encodingLabel.setText("PreferencesFrame.log_file.encoding");
			jContentPane = new JPanel();
			jContentPane.setLayout(new GridBagLayout());
			jContentPane.add(encodingLabel, gridBagConstraints);
			jContentPane.add(getEncodingComboBox(), gridBagConstraints1);
			jContentPane.add(readInvervalLabel, gridBagConstraints11);
			jContentPane.add(getReadIntervalTextField(), gridBagConstraints2);
			jContentPane.add(getOkButton(), gridBagConstraints4);
			jContentPane.add(getCancelButton(), gridBagConstraints5);
			jContentPane.add(detailSqlFormatLabel, gridBagConstraints12);
			jContentPane.add(getFormatSqlCheckBox(), gridBagConstraints21);

			// ESCで閉じる
			GuiUtils.closeByESC(this, jContentPane);
		}
		return jContentPane;
	}

	/**
	 * This method initializes encodingComboBox
	 *
	 * @return javax.swing.JComboBox
	 */
	private JComboBox getEncodingComboBox() {
		if (encodingComboBox == null) {
			encodingComboBox = new JComboBox();
			encodingComboBox.setPreferredSize(new Dimension(150, 20));
			encodingComboBox.setFont(new Font("Dialog", Font.PLAIN, 12));

			setAllCharset();
		}

		encodingComboBox.setSelectedItem(
				ViewerConfig.getInstance().getLogFile().getCharSet());

		return encodingComboBox;
	}

	/**
	 * コンボボックスに全ての文字セットを設定する
	 */
	private void setAllCharset() {
		// デフォルトを先頭
		String defaultCharset = Charset.defaultCharset().name();
		this.encodingComboBox.addItem(defaultCharset);

		for (String name : Charset.availableCharsets().keySet()) {
			this.encodingComboBox.addItem(name);
		}

	}

	/**
	 * This method initializes readIntervalTextField
	 *
	 * @return javax.swing.JTextField
	 */
	private JTextField getReadIntervalTextField() {
		if (readIntervalTextField == null) {
			readIntervalTextField = new JTextField();
			readIntervalTextField.setPreferredSize(new Dimension(80, 18));
		}

		readIntervalTextField.setText(
				String.valueOf(
						ViewerConfig.getInstance().getLogFile().getReadInterval()));

		readIntervalTextField.setInputVerifier(
				new IntegerInputVerifier(M17N.getWithKeyArgs("validate.integer", "PreferencesFrame.log_file.read_interval")) );

		return readIntervalTextField;
	}

	/**
	 * This method initializes okButton
	 *
	 * @return javax.swing.JButton
	 */
	private JButton getOkButton() {
		if (okButton == null) {
			okButton = new JButton();
			okButton.setText("button.ok");

			okButton.addActionListener(new java.awt.event.ActionListener() {
				public void actionPerformed(java.awt.event.ActionEvent e) {
					ViewerConfig.LogFile logFileConfig = ViewerConfig.getInstance().getLogFile();

					logFileConfig.setCharSet(
							encodingComboBox.getSelectedItem().toString());
					logFileConfig.setReadInterval(
							Long.parseLong(readIntervalTextField.getText()));

					ViewerConfig.getInstance().setDetailSqlFormat(
							formatSqlCheckBox.isSelected());

					dispose();
				}
			});
		}
		return okButton;
	}

	/**
	 * This method initializes cancelButton
	 *
	 * @return javax.swing.JButton
	 */
	private JButton getCancelButton() {
		if (cancelButton == null) {
			cancelButton = new JButton();
			cancelButton.setText("button.cancel");
			cancelButton.addActionListener(new java.awt.event.ActionListener() {
				public void actionPerformed(java.awt.event.ActionEvent e) {
					dispose();
				}
			});
		}
		return cancelButton;
	}

	/**
	 * This method initializes formatSqlCheckBox
	 *
	 * @return javax.swing.JCheckBox
	 */
	private JCheckBox getFormatSqlCheckBox() {
		if (formatSqlCheckBox == null) {
			formatSqlCheckBox = new JCheckBox();
			formatSqlCheckBox.setText("PreferencesFrame.detail_sql_format");

			formatSqlCheckBox.setSelected(
					ViewerConfig.getInstance().isDetailSqlFormat());
		}
		return formatSqlCheckBox;
	}

}  //  @jve:decl-index=0:visual-constraint="1,9"
