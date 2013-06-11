package net.mikaboshi.jdbc.monitor.viewer;

import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;

import javax.swing.BoxLayout;
import javax.swing.ButtonGroup;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JTextField;
import javax.swing.WindowConstants;

import net.mikaboshi.gui.GuiUtils;
import net.mikaboshi.gui.IntegerInputVerifier;
import net.mikaboshi.jdbc.monitor.M17N;
import net.mikaboshi.jdbc.monitor.ViewerConfig;
import net.mikaboshi.jdbc.monitor.ViewerConfig.FormatType;

/**
 * 設定フレーム
 *
 * @version 1.4.3
 */
public class PreferencesFrame extends JFrame {

	private static final long serialVersionUID = 1L;
	private JPanel jContentPane = null;
	private JLabel readInvervalLabel = null;
	private JTextField readIntervalTextField = null;
	private JButton okButton = null;
	private JButton cancelButton = null;

	private JLabel detailSqlFormatLabel = null;
	private JRadioButton formatSqlRadioButton = null;
	private JRadioButton linezeSqlRadioButton = null;
	private JRadioButton rawSqlRadioButton = null;
	private JPanel formatSqlRadioButtonsPanel;

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
		this.setSize(440, 200);
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
			gridBagConstraints21.gridy = 1;
			GridBagConstraints gridBagConstraints12 = new GridBagConstraints();
			gridBagConstraints12.gridx = 0;
			gridBagConstraints12.anchor = GridBagConstraints.WEST;
			gridBagConstraints12.insets = new Insets(5, 10, 20, 10);
			gridBagConstraints12.gridy = 1;
			detailSqlFormatLabel = new JLabel();
			detailSqlFormatLabel.setText("PreferencesFrame.detail_sql");
			GridBagConstraints gridBagConstraints5 = new GridBagConstraints();
			gridBagConstraints5.gridx = 1;
			gridBagConstraints5.anchor = GridBagConstraints.WEST;
			gridBagConstraints5.insets = new Insets(5, 10, 10, 0);
			gridBagConstraints5.gridy = 2;
			GridBagConstraints gridBagConstraints4 = new GridBagConstraints();
			gridBagConstraints4.gridx = 0;
			gridBagConstraints4.anchor = GridBagConstraints.EAST;
			gridBagConstraints4.ipadx = 40;
			gridBagConstraints4.insets = new Insets(5, 0, 10, 10);
			gridBagConstraints4.gridy = 2;
			GridBagConstraints gridBagConstraints2 = new GridBagConstraints();
			gridBagConstraints2.fill = GridBagConstraints.NONE;
			gridBagConstraints2.gridy = 0;
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
			gridBagConstraints11.gridy = 0;
			readInvervalLabel = new JLabel();
			readInvervalLabel.setText("PreferencesFrame.log_file.read_interval.ms");

			jContentPane = new JPanel();
			jContentPane.setLayout(new GridBagLayout());
			jContentPane.add(readInvervalLabel, gridBagConstraints11);
			jContentPane.add(getReadIntervalTextField(), gridBagConstraints2);
			jContentPane.add(getOkButton(), gridBagConstraints4);
			jContentPane.add(getCancelButton(), gridBagConstraints5);
			jContentPane.add(detailSqlFormatLabel, gridBagConstraints12);

			formatSqlRadioButtonsPanel = new JPanel();
			formatSqlRadioButtonsPanel.setLayout(new BoxLayout(formatSqlRadioButtonsPanel, BoxLayout.X_AXIS));
			formatSqlRadioButtonsPanel.add(getFormatSqlRadioButton());
			formatSqlRadioButtonsPanel.add(getRawSqlRadioButton());
			formatSqlRadioButtonsPanel.add(getLinizeSqlRadioButton());

			jContentPane.add(formatSqlRadioButtonsPanel, gridBagConstraints21);

			ButtonGroup formatSqlButtonGroup = new ButtonGroup();
			formatSqlButtonGroup.add(getFormatSqlRadioButton());
			formatSqlButtonGroup.add(getRawSqlRadioButton());
			formatSqlButtonGroup.add(getLinizeSqlRadioButton());

			// ESCで閉じる
			GuiUtils.closeByESC(this, jContentPane);
		}
		return jContentPane;
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

					ViewerConfig.getInstance().getLogFile().setReadInterval(
							Long.parseLong(readIntervalTextField.getText()));

					if (getFormatSqlRadioButton().isSelected()) {
						ViewerConfig.getInstance().setFormatTypeByEnum(FormatType.FORMAT);
					} else if (getRawSqlRadioButton().isSelected()) {
						ViewerConfig.getInstance().setFormatTypeByEnum(FormatType.RAW);
					} else if (getLinizeSqlRadioButton().isSelected()) {
						ViewerConfig.getInstance().setFormatTypeByEnum(FormatType.LINE);
					}

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

	private JRadioButton getFormatSqlRadioButton() {
		if (formatSqlRadioButton == null) {
			formatSqlRadioButton = new JRadioButton();
			formatSqlRadioButton.setText("PreferencesFrame.format.format");

			formatSqlRadioButton.setSelected(
					ViewerConfig.getInstance().getFormatTypeAsEnum() == FormatType.FORMAT);
		}

		return formatSqlRadioButton;
	}

	private JRadioButton getRawSqlRadioButton() {
		if (rawSqlRadioButton == null) {
			rawSqlRadioButton = new JRadioButton();
			rawSqlRadioButton.setText("PreferencesFrame.format.raw");

			rawSqlRadioButton.setSelected(
					ViewerConfig.getInstance().getFormatTypeAsEnum() == FormatType.RAW);
		}

		return rawSqlRadioButton;
	}

	private JRadioButton getLinizeSqlRadioButton() {
		if (linezeSqlRadioButton == null) {
			linezeSqlRadioButton = new JRadioButton();
			linezeSqlRadioButton.setText("PreferencesFrame.format.line");

			linezeSqlRadioButton.setSelected(
					ViewerConfig.getInstance().getFormatTypeAsEnum() == FormatType.LINE);
		}

		return linezeSqlRadioButton;
	}

}  //  @jve:decl-index=0:visual-constraint="1,9"
