package net.mikaboshi.jdbc.monitor.viewer;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.util.regex.PatternSyntaxException;

import javax.swing.BoxLayout;
import javax.swing.ButtonGroup;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JTabbedPane;
import javax.swing.JTextField;
import javax.swing.WindowConstants;

import net.mikaboshi.gui.GuiUtils;
import net.mikaboshi.gui.IntegerInputVerifier;
import net.mikaboshi.jdbc.monitor.AutoCommit;
import net.mikaboshi.jdbc.monitor.M17N;
import net.mikaboshi.jdbc.monitor.Result;
import net.mikaboshi.jdbc.monitor.ViewerConfig;
import net.mikaboshi.jdbc.monitor.viewer.LogTableFilter.CommandSqlFilter;
import net.mikaboshi.jdbc.monitor.viewer.LogTableFilter.NoSqlFilter;
import net.mikaboshi.jdbc.monitor.viewer.LogTableFilter.RegExSqlFilter;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class FilterConfigFrame extends JFrame {

	private static Log systemLogger = LogFactory.getLog(FilterConfigFrame.class);  //  @jve:decl-index=0:

	private static final long serialVersionUID = 1L;
	private JPanel jContentPane = null;
	private JPanel buttonAreaPanel = null;
	private JTabbedPane jTabbedPane = null;
	private JPanel logTypePanel = null;
	private JPanel sqlPanel = null;
	private JPanel othersPanel = null;
	private JButton okButton = null;
	private JButton cancelButton = null;
	private JButton defaultButton = null;
	private JCheckBox connectionOpenCheckBox = null;
	private JCheckBox connectionCloseCheckBox = null;
	private JCheckBox commitCheckBox = null;
	private JCheckBox rollbackCheckBox = null;
	private JCheckBox prepareStatementCheckBox = null;
	private JCheckBox executeStatementCheckBox = null;
	private JCheckBox executePreparedStatementCheckBox = null;
	private JCheckBox closeStatementCheckBox = null;
	private JCheckBox addBatchCheckBox = null;
	private JCheckBox executeBatchCheckBox = null;
	private JRadioButton sqlTypeAllRadioButton = null;
	private JRadioButton sqlTypeCommnadRadioButton = null;
	private JCheckBox sqlSelectCheckBox = null;
	private JCheckBox sqlInsertCheckBox = null;
	private JCheckBox sqlUpdateCheckBox = null;
	private JCheckBox sqlDeleteCheckBox = null;
	private JRadioButton sqlTypeRegexRadioButton = null;
	private JLabel sqlIncludePatternLabel = null;
	private JTextField sqlIncludePatternTextField = null;
	private JLabel sqlExcludePatternLabel = null;
	private JTextField sqlExcludePatternTextField = null;
	private JLabel threadNameLabel = null;
	private JLabel statementIdLabel = null;
	private JLabel tagLabel = null;
	private JLabel connectionIdLabel = null;
	private JLabel affectedRowsLabel = null;
	private JLabel elapsedTimeLabel = null;
	private JLabel resultLabel = null;
	private JLabel autoCommitLabel = null;
	private JComboBox threadNameComboBox = null;
	private JComboBox connectionIdComboBox = null;
	private JComboBox statementIdComboBox = null;
	private JComboBox tagComboBox = null;
	private JTextField affectedRowsLowTextField = null;
	private JLabel affectedRowsSepLabel = null;
	private JTextField affectedRowsHighTextField = null;
	private JTextField elapsedTimeLowTextField = null;
	private JLabel elapsedTimeSepLabel = null;
	private JTextField elapsedTimeHighTextField = null;
	private JRadioButton successRadioButton = null;
	private JRadioButton failureRadioButton = null;
	private JRadioButton allResultRadioButton = null;
	private JRadioButton autoCommitTrueRadioButton = null;
	private JRadioButton autoCommitFalseRadioButton = null;
	private JRadioButton autoCommitAllRadioButton = null;
	private JLabel paddingLabel = null;
	/**
	 * This method initializes buttonAreaPanel
	 *
	 * @return javax.swing.JPanel
	 */
	private JPanel getButtonAreaPanel() {
		if (buttonAreaPanel == null) {
			FlowLayout flowLayout = new FlowLayout();
			flowLayout.setAlignment(java.awt.FlowLayout.RIGHT);
			flowLayout.setHgap(10);
			buttonAreaPanel = new JPanel();
			buttonAreaPanel.setLayout(flowLayout);
			buttonAreaPanel.add(getOkButton(), null);
			buttonAreaPanel.add(getCancelButton(), null);
			buttonAreaPanel.add(getDefaultButton(), null);
		}
		return buttonAreaPanel;
	}

	/**
	 * This method initializes jTabbedPane
	 *
	 * @return javax.swing.JTabbedPane
	 */
	private JTabbedPane getJTabbedPane() {
		if (jTabbedPane == null) {
			jTabbedPane = new JTabbedPane();
			jTabbedPane.addTab("FilterConfigFrame.tab.log_type", null, getLogTypePanel(), null);
			jTabbedPane.addTab("FilterConfigFrame.tab.sql", null, getSqlPanel(), null);
			jTabbedPane.addTab("FilterConfigFrame.tab.other", null, getOthersPanel(), null);
		}
		return jTabbedPane;
	}

	/**
	 * This method initializes logTypePanel
	 *
	 * @return javax.swing.JPanel
	 */
	private JPanel getLogTypePanel() {
		if (logTypePanel == null) {
			logTypePanel = new JPanel();
			logTypePanel.setLayout(new BoxLayout(getLogTypePanel(), BoxLayout.Y_AXIS));
			logTypePanel.add(getConnectionOpenCheckBox(), null);
			logTypePanel.add(getConnectionCloseCheckBox(), null);
			logTypePanel.add(getCommitCheckBox(), null);
			logTypePanel.add(getRollbackCheckBox(), null);
			logTypePanel.add(getExecuteStatementCheckBox(), null);
			logTypePanel.add(getPrepareStatementCheckBox(), null);
			logTypePanel.add(getExecutePreparedStatementCheckBox(), null);
			logTypePanel.add(getCloseStatementCheckBox(), null);
			logTypePanel.add(getAddBatchCheckBox(), null);
			logTypePanel.add(getExecuteBatchCheckBox(), null);
		}
		return logTypePanel;
	}

	/**
	 * This method initializes sqlPanel
	 *
	 * @return javax.swing.JPanel
	 */
	private JPanel getSqlPanel() {
		if (sqlPanel == null) {
			GridBagConstraints gridBagConstraints10 = new GridBagConstraints();
			gridBagConstraints10.gridx = 1;
			gridBagConstraints10.gridwidth = 2;
			gridBagConstraints10.anchor = GridBagConstraints.WEST;
			gridBagConstraints10.insets = new Insets(10, 10, 5, 10);
			gridBagConstraints10.gridy = 0;
			GridBagConstraints gridBagConstraints9 = new GridBagConstraints();
			gridBagConstraints9.gridx = 1;
			gridBagConstraints9.gridwidth = 2;
			gridBagConstraints9.anchor = GridBagConstraints.WEST;
			gridBagConstraints9.insets = new Insets(10, 10, 5, 10);
			gridBagConstraints9.gridy = 6;
			GridBagConstraints gridBagConstraints8 = new GridBagConstraints();
			gridBagConstraints8.fill = GridBagConstraints.NONE;
			gridBagConstraints8.gridx = 2;
			gridBagConstraints8.gridy = 9;
			gridBagConstraints8.ipadx = 0;
			gridBagConstraints8.anchor = GridBagConstraints.WEST;
			gridBagConstraints8.weightx = 1.0;
			GridBagConstraints gridBagConstraints7 = new GridBagConstraints();
			gridBagConstraints7.gridx = 1;
			gridBagConstraints7.anchor = GridBagConstraints.WEST;
			gridBagConstraints7.insets = new Insets(5, 35, 10, 10);
			gridBagConstraints7.gridy = 9;
			GridBagConstraints gridBagConstraints6 = new GridBagConstraints();
			gridBagConstraints6.fill = GridBagConstraints.NONE;
			gridBagConstraints6.gridx = 2;
			gridBagConstraints6.gridy = 7;
			gridBagConstraints6.ipadx = 0;
			gridBagConstraints6.anchor = GridBagConstraints.WEST;
			gridBagConstraints6.weightx = 1.0;
			GridBagConstraints gridBagConstraints5 = new GridBagConstraints();
			gridBagConstraints5.gridx = 1;
			gridBagConstraints5.anchor = GridBagConstraints.WEST;
			gridBagConstraints5.insets = new Insets(5, 35, 5, 10);
			gridBagConstraints5.gridy = 7;
			GridBagConstraints gridBagConstraints4 = new GridBagConstraints();
			gridBagConstraints4.gridx = 1;
			gridBagConstraints4.anchor = GridBagConstraints.WEST;
			gridBagConstraints4.insets = new Insets(2, 35, 5, 10);
			gridBagConstraints4.gridy = 5;
			GridBagConstraints gridBagConstraints3 = new GridBagConstraints();
			gridBagConstraints3.gridx = 1;
			gridBagConstraints3.anchor = GridBagConstraints.WEST;
			gridBagConstraints3.insets = new Insets(2, 35, 2, 10);
			gridBagConstraints3.gridy = 4;
			GridBagConstraints gridBagConstraints2 = new GridBagConstraints();
			gridBagConstraints2.gridx = 1;
			gridBagConstraints2.anchor = GridBagConstraints.WEST;
			gridBagConstraints2.insets = new Insets(2, 35, 2, 10);
			gridBagConstraints2.gridy = 3;
			GridBagConstraints gridBagConstraints1 = new GridBagConstraints();
			gridBagConstraints1.gridx = 1;
			gridBagConstraints1.anchor = GridBagConstraints.WEST;
			gridBagConstraints1.insets = new Insets(5, 35, 2, 10);
			gridBagConstraints1.gridy = 2;
			GridBagConstraints gridBagConstraints = new GridBagConstraints();
			gridBagConstraints.anchor = GridBagConstraints.WEST;
			gridBagConstraints.gridy = 1;
			gridBagConstraints.gridwidth = 2;
			gridBagConstraints.insets = new Insets(10, 10, 5, 10);
			gridBagConstraints.gridx = 1;
			sqlExcludePatternLabel = new JLabel();
			sqlExcludePatternLabel.setText("FilterConfigFrame.exclude_pattern");
			sqlIncludePatternLabel = new JLabel();
			sqlIncludePatternLabel.setText("FilterConfigFrame.include_pattern");
			sqlPanel = new JPanel();
			sqlPanel.setLayout(new GridBagLayout());
			sqlPanel.add(getSqlTypeRegexRadioButton(), gridBagConstraints9);
			sqlPanel.add(getSqlTypeCommnadRadioButton(), gridBagConstraints);
			sqlPanel.add(getSqlTypeAllRadioButton(), gridBagConstraints10);
			sqlPanel.add(getSqlSelectCheckBox(), gridBagConstraints1);
			sqlPanel.add(getSqlInsertCheckBox(), gridBagConstraints2);
			sqlPanel.add(getSqlUpdateCheckBox(), gridBagConstraints3);
			sqlPanel.add(getSqlDeleteCheckBox(), gridBagConstraints4);
			sqlPanel.add(sqlIncludePatternLabel, gridBagConstraints5);
			sqlPanel.add(getSqlIncludePatternTextField(), gridBagConstraints6);
			sqlPanel.add(sqlExcludePatternLabel, gridBagConstraints7);
			sqlPanel.add(getSqlExcludePatternTextField(), gridBagConstraints8);

			ButtonGroup sqlTypeButtonGroup = new ButtonGroup();
			sqlTypeButtonGroup.add(sqlTypeAllRadioButton);
			sqlTypeButtonGroup.add(sqlTypeCommnadRadioButton);
			sqlTypeButtonGroup.add(sqlTypeRegexRadioButton);
		}
		return sqlPanel;
	}

	/**
	 * This method initializes othersPanel
	 *
	 * @return javax.swing.JPanel
	 */
	private JPanel getOthersPanel() {
		if (othersPanel == null) {
			GridBagConstraints gridBagConstraints26 = new GridBagConstraints();
			gridBagConstraints26.gridx = 0;
			gridBagConstraints26.fill = GridBagConstraints.NONE;
			gridBagConstraints26.gridheight = 1;
			gridBagConstraints26.ipady = 60;
			gridBagConstraints26.gridy = 9;
			paddingLabel = new JLabel();
			paddingLabel.setText("");

			// auto commit
			GridBagConstraints gridBagConstraints32 = new GridBagConstraints();
			gridBagConstraints32.gridx = 3;
			gridBagConstraints32.insets = new Insets(5, 5, 5, 10);
			gridBagConstraints32.anchor = GridBagConstraints.WEST;
			gridBagConstraints32.gridy = 8;
			GridBagConstraints gridBagConstraints33 = new GridBagConstraints();
			gridBagConstraints33.gridx = 2;
			gridBagConstraints33.insets = new Insets(5, 0, 5, 15);
			gridBagConstraints33.gridy = 8;
			GridBagConstraints gridBagConstraints34 = new GridBagConstraints();
			gridBagConstraints34.gridx = 1;
			gridBagConstraints34.anchor = GridBagConstraints.WEST;
			gridBagConstraints34.insets = new Insets(5, 0, 5, 0);
			gridBagConstraints34.gridy = 8;

			GridBagConstraints gridBagConstraints25 = new GridBagConstraints();
			gridBagConstraints25.gridx = 3;
			gridBagConstraints25.insets = new Insets(5, 5, 5, 10);
			gridBagConstraints25.anchor = GridBagConstraints.WEST;
			gridBagConstraints25.gridy = 7;
			GridBagConstraints gridBagConstraints24 = new GridBagConstraints();
			gridBagConstraints24.gridx = 2;
			gridBagConstraints24.insets = new Insets(5, 0, 5, 15);
			gridBagConstraints24.gridy = 7;
			GridBagConstraints gridBagConstraints23 = new GridBagConstraints();
			gridBagConstraints23.gridx = 1;
			gridBagConstraints23.anchor = GridBagConstraints.WEST;
			gridBagConstraints23.insets = new Insets(5, 0, 5, 0);
			gridBagConstraints23.gridy = 7;
			GridBagConstraints gridBagConstraints22 = new GridBagConstraints();
			gridBagConstraints22.fill = GridBagConstraints.NONE;
			gridBagConstraints22.gridy = 6;
			gridBagConstraints22.weightx = 1.0;
			gridBagConstraints22.insets = new Insets(5, 0, 5, 10);
			gridBagConstraints22.ipadx = 0;
			gridBagConstraints22.anchor = GridBagConstraints.WEST;
			gridBagConstraints22.gridx = 3;
			GridBagConstraints gridBagConstraints21 = new GridBagConstraints();
			gridBagConstraints21.gridx = 2;
			gridBagConstraints21.anchor = GridBagConstraints.WEST;
			gridBagConstraints21.insets = new Insets(5, 5, 5, 0);
			gridBagConstraints21.gridy = 6;
			elapsedTimeSepLabel = new JLabel();
			elapsedTimeSepLabel.setText("period_separator");
			GridBagConstraints gridBagConstraints20 = new GridBagConstraints();
			gridBagConstraints20.fill = GridBagConstraints.NONE;
			gridBagConstraints20.gridy = 6;
			gridBagConstraints20.weightx = 1.0;
			gridBagConstraints20.anchor = GridBagConstraints.WEST;
			gridBagConstraints20.insets = new Insets(5, 0, 5, 0);
			gridBagConstraints20.ipadx = 0;
			gridBagConstraints20.gridx = 1;
			GridBagConstraints gridBagConstraints19 = new GridBagConstraints();
			gridBagConstraints19.fill = GridBagConstraints.NONE;
			gridBagConstraints19.gridy = 5;
			gridBagConstraints19.weightx = 1.0;
			gridBagConstraints19.ipadx = 0;
			gridBagConstraints19.insets = new Insets(5, 0, 5, 10);
			gridBagConstraints19.anchor = GridBagConstraints.WEST;
			gridBagConstraints19.gridx = 3;
			GridBagConstraints gridBagConstraints18 = new GridBagConstraints();
			gridBagConstraints18.gridx = 2;
			gridBagConstraints18.insets = new Insets(5, 5, 5, 0);
			gridBagConstraints18.anchor = GridBagConstraints.WEST;
			gridBagConstraints18.gridy = 5;
			affectedRowsSepLabel = new JLabel();
			affectedRowsSepLabel.setText("period_separator");
			GridBagConstraints gridBagConstraints17 = new GridBagConstraints();
			gridBagConstraints17.fill = GridBagConstraints.NONE;
			gridBagConstraints17.gridy = 5;
			gridBagConstraints17.weightx = 1.0;
			gridBagConstraints17.insets = new Insets(5, 0, 5, 0);
			gridBagConstraints17.ipadx = 0;
			gridBagConstraints17.anchor = GridBagConstraints.WEST;
			gridBagConstraints17.gridx = 1;
			GridBagConstraints gridBagConstraints16 = new GridBagConstraints();
			gridBagConstraints16.fill = GridBagConstraints.NONE;
			gridBagConstraints16.gridy = 3;
			gridBagConstraints16.weightx = 1.0;
			gridBagConstraints16.anchor = GridBagConstraints.WEST;
			gridBagConstraints16.gridwidth = 3;
			gridBagConstraints16.ipadx = 0;
			gridBagConstraints16.insets = new Insets(5, 0, 5, 0);
			gridBagConstraints16.gridx = 1;
			GridBagConstraints gridBagConstraints28 = new GridBagConstraints();
			gridBagConstraints28.fill = GridBagConstraints.NONE;
			gridBagConstraints28.gridy = 1;
			gridBagConstraints28.weightx = 1.0;
			gridBagConstraints28.anchor = GridBagConstraints.WEST;
			gridBagConstraints28.insets = new Insets(10, 0, 5, 0);
			gridBagConstraints28.ipadx = 0;
			gridBagConstraints28.gridwidth = 3;
			gridBagConstraints28.gridx = 1;
			GridBagConstraints gridBagConstraints15 = new GridBagConstraints();
			gridBagConstraints15.fill = GridBagConstraints.NONE;
			gridBagConstraints15.gridy = 2;
			gridBagConstraints15.weightx = 1.0;
			gridBagConstraints15.anchor = GridBagConstraints.WEST;
			gridBagConstraints15.insets = new Insets(10, 0, 5, 0);
			gridBagConstraints15.ipadx = 0;
			gridBagConstraints15.gridwidth = 3;
			gridBagConstraints15.gridx = 1;

			GridBagConstraints gridBagConstraints29 = new GridBagConstraints();
			gridBagConstraints29.gridx = 0;
			gridBagConstraints29.anchor = GridBagConstraints.WEST;
			gridBagConstraints29.insets = new Insets(5, 10, 5, 10);
			gridBagConstraints29.gridy = 8;
			autoCommitLabel = new JLabel();
			autoCommitLabel.setText("auto_commit");
			GridBagConstraints gridBagConstraints14 = new GridBagConstraints();
			gridBagConstraints14.gridx = 0;
			gridBagConstraints14.anchor = GridBagConstraints.WEST;
			gridBagConstraints14.insets = new Insets(5, 10, 5, 10);
			gridBagConstraints14.gridy = 7;
			resultLabel = new JLabel();
			resultLabel.setText("result");
			GridBagConstraints gridBagConstraints13 = new GridBagConstraints();
			gridBagConstraints13.gridx = 0;
			gridBagConstraints13.anchor = GridBagConstraints.WEST;
			gridBagConstraints13.insets = new Insets(5, 10, 5, 10);
			gridBagConstraints13.gridy = 6;
			elapsedTimeLabel = new JLabel();
			elapsedTimeLabel.setText("FilterConfigFrame.elapsed_time");
			GridBagConstraints gridBagConstraints12 = new GridBagConstraints();
			gridBagConstraints12.gridx = 0;
			gridBagConstraints12.anchor = GridBagConstraints.WEST;
			gridBagConstraints12.insets = new Insets(5, 10, 5, 10);
			gridBagConstraints12.gridy = 5;
			affectedRowsLabel = new JLabel();
			affectedRowsLabel.setText("affected_rows");
			GridBagConstraints gridBagConstraints27 = new GridBagConstraints();
			gridBagConstraints27.gridx = 0;
			gridBagConstraints27.anchor = GridBagConstraints.WEST;
			gridBagConstraints27.insets = new Insets(10, 10, 5, 10);
			gridBagConstraints27.gridy = 1;
			threadNameLabel = new JLabel();
			threadNameLabel.setText("thread_name");
			GridBagConstraints gridBagConstraints11 = new GridBagConstraints();
			gridBagConstraints11.gridx = 0;
			gridBagConstraints11.anchor = GridBagConstraints.WEST;
			gridBagConstraints11.insets = new Insets(10, 10, 5, 10);
			gridBagConstraints11.gridy = 2;
			connectionIdLabel = new JLabel();
			connectionIdLabel.setText("connection_id");
			GridBagConstraints gridBagConstraints10 = new GridBagConstraints();
			gridBagConstraints10.gridx = 0;
			gridBagConstraints10.insets = new Insets(5, 10, 5, 10);
			gridBagConstraints10.anchor = GridBagConstraints.WEST;
			gridBagConstraints10.gridy = 3;
			GridBagConstraints gridBagConstraints30 = new GridBagConstraints();
			gridBagConstraints30.gridx = 0;
			gridBagConstraints30.insets = new Insets(5, 10, 5, 10);
			gridBagConstraints30.anchor = GridBagConstraints.WEST;
			gridBagConstraints30.gridy = 4;
			GridBagConstraints gridBagConstraints31 = new GridBagConstraints();
			gridBagConstraints31.fill = GridBagConstraints.NONE;
			gridBagConstraints31.gridy = 4;
			gridBagConstraints31.weightx = 1.0;
			gridBagConstraints31.anchor = GridBagConstraints.WEST;
			gridBagConstraints31.gridwidth = 3;
			gridBagConstraints31.ipadx = 0;
			gridBagConstraints31.insets = new Insets(5, 0, 5, 0);
			gridBagConstraints31.gridx = 1;
			statementIdLabel = new JLabel();
			statementIdLabel.setText("statement_id");
			tagLabel = new JLabel();
			tagLabel.setText("tag");
			othersPanel = new JPanel();
			othersPanel.setLayout(new GridBagLayout());
			othersPanel.add(threadNameLabel, gridBagConstraints27);
			othersPanel.add(statementIdLabel, gridBagConstraints10);
			othersPanel.add(connectionIdLabel, gridBagConstraints11);
			othersPanel.add(affectedRowsLabel, gridBagConstraints12);
			othersPanel.add(elapsedTimeLabel, gridBagConstraints13);
			othersPanel.add(resultLabel, gridBagConstraints14);
			othersPanel.add(autoCommitLabel, gridBagConstraints29);
			othersPanel.add(tagLabel, gridBagConstraints30);
			othersPanel.add(getThreadNameComboBox(), gridBagConstraints28);
			othersPanel.add(getConnectionIdComboBox(), gridBagConstraints15);
			othersPanel.add(getStatementIdComboBox(), gridBagConstraints16);
			othersPanel.add(getAffectedRowsLowTextField(), gridBagConstraints17);
			othersPanel.add(affectedRowsSepLabel, gridBagConstraints18);
			othersPanel.add(getAffectedRowsHighTextField(), gridBagConstraints19);
			othersPanel.add(getElapsedTimeLowTextField(), gridBagConstraints20);
			othersPanel.add(elapsedTimeSepLabel, gridBagConstraints21);
			othersPanel.add(getElapsedTimeHighTextField(), gridBagConstraints22);
			othersPanel.add(getSuccessRadioButton(), gridBagConstraints23);
			othersPanel.add(getFailureRadioButton(), gridBagConstraints24);
			othersPanel.add(getAllResultRadioButton(), gridBagConstraints25);
			othersPanel.add(getAutoCommitTrueRadioButton(), gridBagConstraints34);
			othersPanel.add(getAutoCommitFalseRadioButton(), gridBagConstraints33);
			othersPanel.add(getAutoCommitAllRadioButton(), gridBagConstraints32);

			othersPanel.add(paddingLabel, gridBagConstraints26);
			othersPanel.add(getTagComboBox(), gridBagConstraints31);

			ButtonGroup resultButtonGroup = new ButtonGroup();
			resultButtonGroup.add(successRadioButton);
			resultButtonGroup.add(failureRadioButton);
			resultButtonGroup.add(allResultRadioButton);

			ButtonGroup autoCommitButtonGroup = new ButtonGroup();
			autoCommitButtonGroup.add(autoCommitTrueRadioButton);
			autoCommitButtonGroup.add(autoCommitFalseRadioButton);
			autoCommitButtonGroup.add(autoCommitAllRadioButton);
		}
		return othersPanel;
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

					try {
						setConfig();
						parentFrame.updateLogTableFilter();
						dispose();

					} catch (PatternSyntaxException ex) {
						JOptionPane.showMessageDialog(null,
								M17N.get("FilterConfigFrame.message.illegal_regex"),
								"",
								JOptionPane.ERROR_MESSAGE);

					} catch (Exception ex) {
						systemLogger.error("Setting LogTableFilter failed.", ex);
						JOptionPane.showMessageDialog(null,
								M17N.get("FilterConfigFrame.message.illegal_error") + "\n" +
								ex.getMessage(),
								"",
								JOptionPane.ERROR_MESSAGE);
						dispose();

					}
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
	 * This method initializes defaultButton
	 *
	 * @return javax.swing.JButton
	 */
	private JButton getDefaultButton() {
		if (defaultButton == null) {
			defaultButton = new JButton();
			defaultButton.setText("button.default");

			defaultButton.addActionListener(new java.awt.event.ActionListener() {
				public void actionPerformed(java.awt.event.ActionEvent e) {
					setLogTableFilter(ViewerConfig.getDefault());
					sqlIncludePatternTextField.setText(null);
					sqlExcludePatternTextField.setText(null);
					affectedRowsLowTextField.setText(null);
					affectedRowsHighTextField.setText(null);
					elapsedTimeLowTextField.setText(null);
					elapsedTimeHighTextField.setText(null);
				}
			});
		}
		return defaultButton;
	}

	/**
	 * This method initializes connectionOpenCheckBox
	 *
	 * @return javax.swing.JCheckBox
	 */
	private JCheckBox getConnectionOpenCheckBox() {
		if (connectionOpenCheckBox == null) {
			connectionOpenCheckBox = new JCheckBox();
			connectionOpenCheckBox.setText("operation.open_connection");
			connectionOpenCheckBox.setMargin(new Insets(5, 10, 5, 10));
		}
		return connectionOpenCheckBox;
	}

	/**
	 * This method initializes connectionCloseCheckBox
	 *
	 * @return javax.swing.JCheckBox
	 */
	private JCheckBox getConnectionCloseCheckBox() {
		if (connectionCloseCheckBox == null) {
			connectionCloseCheckBox = new JCheckBox();
			connectionCloseCheckBox.setText("operation.close_connection");
			connectionCloseCheckBox.setMargin(new Insets(5, 10, 5, 10));
		}
		return connectionCloseCheckBox;
	}

	/**
	 * This method initializes commitCheckBox
	 *
	 * @return javax.swing.JCheckBox
	 */
	private JCheckBox getCommitCheckBox() {
		if (commitCheckBox == null) {
			commitCheckBox = new JCheckBox();
			commitCheckBox.setText("operation.commit");
			commitCheckBox.setMargin(new Insets(5, 10, 5, 10));
		}
		return commitCheckBox;
	}

	/**
	 * This method initializes rollbackCheckBox
	 *
	 * @return javax.swing.JCheckBox
	 */
	private JCheckBox getRollbackCheckBox() {
		if (rollbackCheckBox == null) {
			rollbackCheckBox = new JCheckBox();
			rollbackCheckBox.setText("operation.rollback");
			rollbackCheckBox.setMargin(new Insets(5, 10, 5, 10));
		}
		return rollbackCheckBox;
	}

	/**
	 * This method initializes prepareStatementCheckBox
	 *
	 * @return javax.swing.JCheckBox
	 */
	private JCheckBox getPrepareStatementCheckBox() {
		if (prepareStatementCheckBox == null) {
			prepareStatementCheckBox = new JCheckBox();
			prepareStatementCheckBox.setText("operation.create_prepared_statement");
			prepareStatementCheckBox.setMargin(new Insets(5, 10, 5, 10));
		}
		return prepareStatementCheckBox;
	}

	/**
	 * This method initializes executeStatementCheckBox
	 *
	 * @return javax.swing.JCheckBox
	 */
	private JCheckBox getExecuteStatementCheckBox() {
		if (executeStatementCheckBox == null) {
			executeStatementCheckBox = new JCheckBox();
			executeStatementCheckBox.setText("operation.execute_statement");
			executeStatementCheckBox.setMargin(new Insets(5, 10, 5, 10));
		}
		return executeStatementCheckBox;
	}

	/**
	 * This method initializes executePreparedStatementCheckBox
	 *
	 * @return javax.swing.JCheckBox
	 */
	private JCheckBox getExecutePreparedStatementCheckBox() {
		if (executePreparedStatementCheckBox == null) {
			executePreparedStatementCheckBox = new JCheckBox();
			executePreparedStatementCheckBox.setText("operation.execute_prepared_statement");
			executePreparedStatementCheckBox.setMargin(new Insets(5, 10, 5, 10));
		}
		return executePreparedStatementCheckBox;
	}

	/**
	 * This method initializes closeStatementCheckBox
	 *
	 * @return javax.swing.JCheckBox
	 */
	private JCheckBox getCloseStatementCheckBox() {
		if (closeStatementCheckBox == null) {
			closeStatementCheckBox = new JCheckBox();
			closeStatementCheckBox.setText("operation.close_statement");
			closeStatementCheckBox.setMargin(new Insets(5, 10, 5, 10));
		}
		return closeStatementCheckBox;
	}

	/**
	 * This method initializes addBatchCheckBox
	 *
	 * @return javax.swing.JCheckBox
	 */
	private JCheckBox getAddBatchCheckBox() {
		if (addBatchCheckBox == null) {
			addBatchCheckBox = new JCheckBox();
			addBatchCheckBox.setText("operation.add_batch");
			addBatchCheckBox.setMargin(new Insets(5, 10, 5, 10));
		}
		return addBatchCheckBox;
	}

	/**
	 * This method initializes executeBatchCheckBox
	 *
	 * @return javax.swing.JCheckBox
	 */
	private JCheckBox getExecuteBatchCheckBox() {
		if (executeBatchCheckBox == null) {
			executeBatchCheckBox = new JCheckBox();
			executeBatchCheckBox.setText("operation.execute_batch");
			executeBatchCheckBox.setMargin(new Insets(5, 10, 5, 10));
		}
		return executeBatchCheckBox;
	}

	/**
	 * This method initializes sqlTypeAllRadioButton
	 *
	 * @return javax.swing.JRadioButton
	 */
	private JRadioButton getSqlTypeAllRadioButton() {
		if (sqlTypeAllRadioButton == null) {
			sqlTypeAllRadioButton = new JRadioButton();
			sqlTypeAllRadioButton.setText("FilterConfigFrame.option.result.all");

			sqlTypeAllRadioButton
					.addActionListener(new java.awt.event.ActionListener() {
						public void actionPerformed(java.awt.event.ActionEvent e) {
							switchSqlFilterType();
						}
					});
		}
		return sqlTypeAllRadioButton;
	}

	/**
	 * This method initializes sqlTypeCommnadRadioButton
	 *
	 * @return javax.swing.JRadioButton
	 */
	private JRadioButton getSqlTypeCommnadRadioButton() {
		if (sqlTypeCommnadRadioButton == null) {
			sqlTypeCommnadRadioButton = new JRadioButton();
			sqlTypeCommnadRadioButton.setText("FilterConfigFrame.option.sql.command");

			sqlTypeCommnadRadioButton
					.addActionListener(new java.awt.event.ActionListener() {
						public void actionPerformed(java.awt.event.ActionEvent e) {
							switchSqlFilterType();
						}
					});
		}
		return sqlTypeCommnadRadioButton;
	}

	/**
	 * This method initializes sqlSelectCheckBox
	 *
	 * @return javax.swing.JCheckBox
	 */
	private JCheckBox getSqlSelectCheckBox() {
		if (sqlSelectCheckBox == null) {
			sqlSelectCheckBox = new JCheckBox();
			sqlSelectCheckBox.setText("FilterConfigFrame.sql_command.select");
		}
		return sqlSelectCheckBox;
	}

	/**
	 * This method initializes sqlInsertCheckBox
	 *
	 * @return javax.swing.JCheckBox
	 */
	private JCheckBox getSqlInsertCheckBox() {
		if (sqlInsertCheckBox == null) {
			sqlInsertCheckBox = new JCheckBox();
			sqlInsertCheckBox.setText("FilterConfigFrame.sql_command.insert");
		}
		return sqlInsertCheckBox;
	}

	/**
	 * This method initializes sqlUpdateCheckBox
	 *
	 * @return javax.swing.JCheckBox
	 */
	private JCheckBox getSqlUpdateCheckBox() {
		if (sqlUpdateCheckBox == null) {
			sqlUpdateCheckBox = new JCheckBox();
			sqlUpdateCheckBox.setText("FilterConfigFrame.sql_command.update");
		}
		return sqlUpdateCheckBox;
	}

	/**
	 * This method initializes sqlDeleteCheckBox
	 *
	 * @return javax.swing.JCheckBox
	 */
	private JCheckBox getSqlDeleteCheckBox() {
		if (sqlDeleteCheckBox == null) {
			sqlDeleteCheckBox = new JCheckBox();
			sqlDeleteCheckBox.setText("FilterConfigFrame.sql_command.delete");
		}
		return sqlDeleteCheckBox;
	}

	/**
	 * This method initializes sqlTypeRegexRadioButton
	 *
	 * @return javax.swing.JRadioButton
	 */
	private JRadioButton getSqlTypeRegexRadioButton() {
		if (sqlTypeRegexRadioButton == null) {
			sqlTypeRegexRadioButton = new JRadioButton();
			sqlTypeRegexRadioButton.setText("FilterConfigFrame.option.sql.regex");

			sqlTypeRegexRadioButton.addActionListener(new java.awt.event.ActionListener() {
				public void actionPerformed(java.awt.event.ActionEvent e) {
					switchSqlFilterType();
				}
			});
		}
		return sqlTypeRegexRadioButton;
	}

	/**
	 * This method initializes sqlIncludePatternTextField
	 *
	 * @return javax.swing.JTextField
	 */
	private JTextField getSqlIncludePatternTextField() {
		if (sqlIncludePatternTextField == null) {
			sqlIncludePatternTextField = new JTextField();
			Dimension size = new Dimension(150, 20);
			sqlIncludePatternTextField.setPreferredSize(size);
			sqlIncludePatternTextField.setMaximumSize(size);
			sqlIncludePatternTextField.setMinimumSize(size);
		}
		return sqlIncludePatternTextField;
	}

	/**
	 * This method initializes sqlExcludePatternTextField
	 *
	 * @return javax.swing.JTextField
	 */
	private JTextField getSqlExcludePatternTextField() {
		if (sqlExcludePatternTextField == null) {
			sqlExcludePatternTextField = new JTextField();
			Dimension size = new Dimension(150, 20);
			sqlExcludePatternTextField.setPreferredSize(size);
			sqlExcludePatternTextField.setMaximumSize(size);
			sqlExcludePatternTextField.setMinimumSize(size);
		}
		return sqlExcludePatternTextField;
	}

	/**
	 * This method initializes threadNameComboBox
	 *
	 * @return javax.swing.JComboBox
	 */
	private JComboBox getThreadNameComboBox() {
		if (threadNameComboBox == null) {
			threadNameComboBox = new JComboBox();
			threadNameComboBox.setEditable(true);
			threadNameComboBox.setFont(new Font("Dialog", Font.PLAIN, 12));
			Dimension size = new Dimension(150, 20);
			threadNameComboBox.setMinimumSize(size);
			threadNameComboBox.setMaximumSize(size);
			threadNameComboBox.setPreferredSize(size);
		}
		return threadNameComboBox;
	}

	/**
	 * This method initializes connectionIdComboBox
	 *
	 * @return javax.swing.JComboBox
	 */
	private JComboBox getConnectionIdComboBox() {
		if (connectionIdComboBox == null) {
			connectionIdComboBox = new JComboBox();
			connectionIdComboBox.setEditable(true);
			connectionIdComboBox.setFont(new Font("Dialog", Font.PLAIN, 12));
			Dimension size = new Dimension(150, 20);
			connectionIdComboBox.setMinimumSize(size);
			connectionIdComboBox.setMaximumSize(size);
			connectionIdComboBox.setPreferredSize(size);
		}
		return connectionIdComboBox;
	}

	/**
	 * This method initializes statementIdComboBox
	 *
	 * @return javax.swing.JComboBox
	 */
	private JComboBox getStatementIdComboBox() {
		if (statementIdComboBox == null) {
			statementIdComboBox = new JComboBox();
			statementIdComboBox.setEditable(true);
			statementIdComboBox.setFont(new Font("Dialog", Font.PLAIN, 12));
			Dimension size = new Dimension(150, 20);
			statementIdComboBox.setMinimumSize(size);
			statementIdComboBox.setMaximumSize(size);
			statementIdComboBox.setPreferredSize(size);
		}
		return statementIdComboBox;
	}

	/**
	 * This method initializes tagComboBox
	 *
	 * @return javax.swing.JComboBox
	 */
	private JComboBox getTagComboBox() {
		if (tagComboBox == null) {
			tagComboBox = new JComboBox();
			tagComboBox.setEditable(true);
			tagComboBox.setFont(new Font("Dialog", Font.PLAIN, 12));
			Dimension size = new Dimension(150, 20);
			tagComboBox.setMinimumSize(size);
			tagComboBox.setMaximumSize(size);
			tagComboBox.setPreferredSize(size);
		}
		return tagComboBox;
	}

	/**
	 * This method initializes affectedRowsLowTextField
	 *
	 * @return javax.swing.JTextField
	 */
	private JTextField getAffectedRowsLowTextField() {
		if (affectedRowsLowTextField == null) {
			affectedRowsLowTextField = new JTextField();
			Dimension size = new Dimension(50, 20);
			affectedRowsLowTextField.setMinimumSize(size);
			affectedRowsLowTextField.setMaximumSize(size);
			affectedRowsLowTextField.setPreferredSize(size);
			affectedRowsLowTextField.setInputVerifier(
					new IntegerInputVerifier(M17N.getWithKeyArgs("validate.integer", "affected_rows")));
		}
		return affectedRowsLowTextField;
	}

	/**
	 * This method initializes affectedRowsHighTextField
	 *
	 * @return javax.swing.JTextField
	 */
	private JTextField getAffectedRowsHighTextField() {
		if (affectedRowsHighTextField == null) {
			affectedRowsHighTextField = new JTextField();
			Dimension size = new Dimension(50, 20);
			affectedRowsHighTextField.setMinimumSize(size);
			affectedRowsHighTextField.setMaximumSize(size);
			affectedRowsHighTextField.setPreferredSize(size);
			affectedRowsHighTextField.setInputVerifier(
					new IntegerInputVerifier(M17N.getWithKeyArgs("validate.integer", "affected_rows")));
		}
		return affectedRowsHighTextField;
	}

	/**
	 * This method initializes elapsedTimeLowTextField
	 *
	 * @return javax.swing.JTextField
	 */
	private JTextField getElapsedTimeLowTextField() {
		if (elapsedTimeLowTextField == null) {
			elapsedTimeLowTextField = new JTextField();
			Dimension size = new Dimension(50, 20);
			elapsedTimeLowTextField.setMinimumSize(size);
			elapsedTimeLowTextField.setMaximumSize(size);
			elapsedTimeLowTextField.setPreferredSize(size);
			elapsedTimeLowTextField.setInputVerifier(
					new IntegerInputVerifier(M17N.getWithKeyArgs("validate.integer", "elapsed_time")));
		}
		return elapsedTimeLowTextField;
	}

	/**
	 * This method initializes elapsedTimeHighTextField
	 *
	 * @return javax.swing.JTextField
	 */
	private JTextField getElapsedTimeHighTextField() {
		if (elapsedTimeHighTextField == null) {
			elapsedTimeHighTextField = new JTextField();
			Dimension size = new Dimension(50, 20);
			elapsedTimeHighTextField.setMinimumSize(size);
			elapsedTimeHighTextField.setMaximumSize(size);
			elapsedTimeHighTextField.setPreferredSize(size);
			elapsedTimeHighTextField.setInputVerifier(
					new IntegerInputVerifier(M17N.getWithKeyArgs("validate.integer", "elapsed_time")));
		}
		return elapsedTimeHighTextField;
	}

	/**
	 * This method initializes successRadioButton
	 *
	 * @return javax.swing.JRadioButton
	 */
	private JRadioButton getSuccessRadioButton() {
		if (successRadioButton == null) {
			successRadioButton = new JRadioButton();
			successRadioButton.setText("result.success");
		}
		return successRadioButton;
	}

	/**
	 * This method initializes failureRadioButton
	 *
	 * @return javax.swing.JRadioButton
	 */
	private JRadioButton getFailureRadioButton() {
		if (failureRadioButton == null) {
			failureRadioButton = new JRadioButton();
			failureRadioButton.setText("result.failure");
		}
		return failureRadioButton;
	}

	/**
	 * This method initializes allResultRadioButton
	 *
	 * @return javax.swing.JRadioButton
	 */
	private JRadioButton getAllResultRadioButton() {
		if (allResultRadioButton == null) {
			allResultRadioButton = new JRadioButton();
			allResultRadioButton.setText("FilterConfigFrame.option.result.all");
		}
		return allResultRadioButton;
	}

	/**
	 * This method initializes autoCommitTrueRadioButton
	 *
	 * @return javax.swing.JRadioButton
	 */
	private JRadioButton getAutoCommitTrueRadioButton() {
		if (autoCommitTrueRadioButton == null) {
			autoCommitTrueRadioButton = new JRadioButton();
			autoCommitTrueRadioButton.setText("auto_commit.true");
		}
		return autoCommitTrueRadioButton;
	}

	/**
	 * This method initializes autoCommitFalseRadioButton
	 *
	 * @return javax.swing.JRadioButton
	 */
	private JRadioButton getAutoCommitFalseRadioButton() {
		if (autoCommitFalseRadioButton == null) {
			autoCommitFalseRadioButton = new JRadioButton();
			autoCommitFalseRadioButton.setText("auto_commit.false");
		}
		return autoCommitFalseRadioButton;
	}

	/**
	 * This method initializes autoCommitAllRadioButton
	 *
	 * @return javax.swing.JRadioButton
	 */
	private JRadioButton getAutoCommitAllRadioButton() {
		if (autoCommitAllRadioButton == null) {
			autoCommitAllRadioButton = new JRadioButton();
			autoCommitAllRadioButton.setText("auto_commit.all");
		}
		return autoCommitAllRadioButton;
	}

	private JdbcLogViewerFrame parentFrame;

	public FilterConfigFrame(
			JdbcLogViewerFrame parentFrame, int tabIndex) {
		super();
		initialize();
		this.parentFrame = parentFrame;
		getJTabbedPane().setSelectedIndex(tabIndex);
		setLogTableFilter(ViewerConfig.getInstance());
	}

	/**
	 * This method initializes this
	 *
	 * @return void
	 */
	private void initialize() {
		this.setSize(391, 430);
		this.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
		this.setContentPane(getJContentPane());
		this.setTitle("FilterConfigFrame.title");
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
			jContentPane.add(getJTabbedPane(), BorderLayout.CENTER);
			jContentPane.add(getButtonAreaPanel(), BorderLayout.SOUTH);

			// ESCで閉じる
			GuiUtils.closeByESC(this, jContentPane);
		}
		return jContentPane;
	}

	/**
	 * SQLフィルターのタイプ変更時、コンポーネントのenableを切り替える。
	 */
	private void switchSqlFilterType() {
		if (sqlTypeCommnadRadioButton.isSelected()) {
			sqlSelectCheckBox.setEnabled(true);
			sqlInsertCheckBox.setEnabled(true);
			sqlUpdateCheckBox.setEnabled(true);
			sqlDeleteCheckBox.setEnabled(true);
			sqlIncludePatternTextField.setEnabled(false);
			sqlExcludePatternTextField.setEnabled(false);

		} else if (sqlTypeRegexRadioButton.isSelected()) {
			sqlSelectCheckBox.setEnabled(false);
			sqlInsertCheckBox.setEnabled(false);
			sqlUpdateCheckBox.setEnabled(false);
			sqlDeleteCheckBox.setEnabled(false);
			sqlIncludePatternTextField.setEnabled(true);
			sqlExcludePatternTextField.setEnabled(true);
		} else {
			sqlSelectCheckBox.setEnabled(false);
			sqlInsertCheckBox.setEnabled(false);
			sqlUpdateCheckBox.setEnabled(false);
			sqlDeleteCheckBox.setEnabled(false);
			sqlIncludePatternTextField.setEnabled(false);
			sqlExcludePatternTextField.setEnabled(false);
		}
	}

	/**
	 * フィルター設定を画面に反映する。]
	 * @param config
	 */
	private void setLogTableFilter(ViewerConfig config) {

		ViewerConfig.Filter filter = config.getFilter();
		ViewerConfig.LogType logType = filter.getLogType();

		// 種別
		connectionOpenCheckBox.setSelected(logType.isOpen());
		connectionCloseCheckBox.setSelected(logType.isClose());
		commitCheckBox.setSelected(logType.isCommit());
		rollbackCheckBox.setSelected(logType.isRollback());
		executeStatementCheckBox.setSelected(logType.isExecuteStatement());
		prepareStatementCheckBox.setSelected(logType.isPrepareStatement());
		executePreparedStatementCheckBox.setSelected(logType.isExecutePrepared());
		closeStatementCheckBox.setSelected(logType.isCloseStatement());
		addBatchCheckBox.setSelected(logType.isAddBatch());
		executeBatchCheckBox.setSelected(logType.isExecuteBatch());

		// SQL
		if (filter.getCommandSqlFilter() != null) {
			sqlTypeCommnadRadioButton.setSelected(true);
			sqlSelectCheckBox.setSelected(
					filter.getCommandSqlFilter().isSelectVisible());
			sqlInsertCheckBox.setSelected(
					filter.getCommandSqlFilter().isInsertVisible());
			sqlUpdateCheckBox.setSelected(
					filter.getCommandSqlFilter().isUpdateVisible());
			sqlDeleteCheckBox.setSelected(
					filter.getCommandSqlFilter().isDeleteVisible());

		} else if (filter.getRegExSqlFilter() != null) {
			sqlTypeRegexRadioButton.setSelected(true);
			sqlIncludePatternTextField.setText(
					filter.getRegExSqlFilter().getIncludePattern());
			sqlExcludePatternTextField.setText(
					filter.getRegExSqlFilter().getExcludePattern());
		} else {
			sqlTypeAllRadioButton.setSelected(true);
		}

		switchSqlFilterType();

		// その他

		// スレッド名
		if (this.parentFrame.getThreadNameSet() != null) {
			for (String threadName : this.parentFrame.getThreadNameSet()) {
				threadNameComboBox.addItem(threadName);
			}
		}
		threadNameComboBox.setSelectedItem(filter.getThreadName());

		// コネクションID
		if (this.parentFrame.getConnectionIdSet() != null) {
			for (String connectionId : this.parentFrame.getConnectionIdSet()) {
				connectionIdComboBox.addItem(connectionId);
			}
		}
		connectionIdComboBox.setSelectedItem(filter.getConnectionId());

		// ステートメントID
		if (this.parentFrame.getStatementIdSet() != null) {
			for (String statementId : this.parentFrame.getStatementIdSet()) {
				statementIdComboBox.addItem(statementId);
			}
		}
		statementIdComboBox.setSelectedItem(filter.getStatementId());

		// タグ
		if (this.parentFrame.getTagSet() != null) {
			for (String tag : this.parentFrame.getTagSet()) {
				tagComboBox.addItem(tag);
			}
		}
		tagComboBox.setSelectedItem(filter.getTag());


		// 更新件数
		if (filter.getAffectedRowsMin() == Integer.MIN_VALUE) {
			affectedRowsLowTextField.setText("");
		} else {
			affectedRowsLowTextField.setText(String.valueOf(
					filter.getAffectedRowsMin()));
		}

		if (filter.getAffectedRowsMax() == Integer.MAX_VALUE) {
			affectedRowsHighTextField.setText("");
		} else {
			affectedRowsHighTextField.setText(String.valueOf(
					filter.getAffectedRowsMax()));
		}

		// 経過時間
		if (filter.getElapsedTimeMin() == Long.MIN_VALUE) {
			elapsedTimeLowTextField.setText("");
		} else {
			elapsedTimeLowTextField.setText(String.valueOf(
					filter.getElapsedTimeMin()));
		}

		if (filter.getElapsedTimeMax() == Long.MAX_VALUE) {
			elapsedTimeHighTextField.setText("");
		} else {
			elapsedTimeHighTextField.setText(String.valueOf(
					filter.getElapsedTimeMax()));
		}

		// 結果
		Result result = filter.getResult();
		if (result == Result.SUCCESS) {
			successRadioButton.setSelected(true);
		} else if (result == Result.FAILURE) {
			failureRadioButton.setSelected(true);
		} else {
			allResultRadioButton.setSelected(true);
		}

		// オートコミット
		AutoCommit autoCommit = filter.getAutoCommit();
		if (autoCommit == AutoCommit.TRUE) {
			autoCommitTrueRadioButton.setSelected(true);
		} else if (autoCommit == AutoCommit.FALSE) {
			autoCommitFalseRadioButton.setSelected(true);
		} else {
			autoCommitAllRadioButton.setSelected(true);
		}
	}

	/**
	 * 画面データを、AppConfigフィールドに設定する。
	 *
	 * @throws PatternSyntaxException 正規表現パターンが不正な場合
	 */
	private void setConfig() throws PatternSyntaxException {

		ViewerConfig.Filter filter = ViewerConfig.getInstance().getFilter();

		// 種別
		ViewerConfig.LogType logType = filter.getLogType();
		logType.setOpen(connectionOpenCheckBox.isSelected());
		logType.setClose(connectionCloseCheckBox.isSelected());
		logType.setCommit(commitCheckBox.isSelected());
		logType.setRollback(rollbackCheckBox.isSelected());
		logType.setExecuteStatement(executeStatementCheckBox.isSelected());
		logType.setPrepareStatement(prepareStatementCheckBox.isSelected());
		logType.setExecutePrepared(executePreparedStatementCheckBox.isSelected());
		logType.setCloseStatement(closeStatementCheckBox.isSelected());
		logType.setAddBatch(addBatchCheckBox.isSelected());
		logType.setExecuteBatch(executeBatchCheckBox.isSelected());

		// SQL
		if (sqlTypeCommnadRadioButton.isSelected()) {
			CommandSqlFilter sqlFilter = new CommandSqlFilter();
			sqlFilter.setSelectVisible(sqlSelectCheckBox.isSelected());
			sqlFilter.setInsertVisible(sqlInsertCheckBox.isSelected());
			sqlFilter.setUpdateVisible(sqlUpdateCheckBox.isSelected());
			sqlFilter.setDeleteVisible(sqlDeleteCheckBox.isSelected());
			filter.setCommandSqlFilter(sqlFilter);

		} else if (sqlTypeRegexRadioButton.isSelected()) {
			RegExSqlFilter sqlFilter = new RegExSqlFilter();
			sqlFilter.setIncludePattern(sqlIncludePatternTextField.getText());
			sqlFilter.setExcludePattern(sqlExcludePatternTextField.getText());
			filter.setRegExSqlFilter(sqlFilter);
		} else {
			filter.setNoSqlFilter(NoSqlFilter.INSTANCE);
		}

		// その他

		// スレッド名
		if (threadNameComboBox.getSelectedItem() != null) {
			filter.setThreadName(
					threadNameComboBox.getSelectedItem().toString());
		}

		// コネクションID
		if (connectionIdComboBox.getSelectedItem() != null) {
			filter.setConnectionId(
					connectionIdComboBox.getSelectedItem().toString());
		}

		// ステートメントID
		if (statementIdComboBox.getSelectedItem() != null) {
			filter.setStatementId(
					statementIdComboBox.getSelectedItem().toString());
		}

		// タグ
		if (tagComboBox.getSelectedItem() != null) {
			filter.setTag(
					tagComboBox.getSelectedItem().toString());
		}

		// 更新件数
		filter.setAffectedRowsMin(parseInt(
				affectedRowsLowTextField.getText(),
				Integer.MIN_VALUE));
		filter.setAffectedRowsMax(parseInt(
				affectedRowsHighTextField.getText(),
				Integer.MAX_VALUE));

		// 経過時間
		filter.setElapsedTimeMin( parseLong(
				elapsedTimeLowTextField.getText(),
				Long.MIN_VALUE));
		filter.setElapsedTimeMax(parseLong(
				elapsedTimeHighTextField.getText(),
				Long.MAX_VALUE));

		// 結果
		if (successRadioButton.isSelected()) {
			filter.setResult(Result.SUCCESS);
		} else if (failureRadioButton.isSelected()) {
			filter.setResult(Result.FAILURE);
		} else {
			filter.setResult(Result.ALL);
		}

		// オートコミット
		if (autoCommitTrueRadioButton.isSelected()) {
			filter.setAutoCommit(AutoCommit.TRUE);
		} else if (autoCommitFalseRadioButton.isSelected()) {
			filter.setAutoCommit(AutoCommit.FALSE);
		} else {
			filter.setAutoCommit(AutoCommit.ALL);
		}
	}

	private int parseInt(String s, int def) {
		if (StringUtils.isBlank(s)) {
			return def;
		}

		try {
			return Integer.parseInt(s);
		} catch (NumberFormatException e) {
			return def;
		}
	}

	private long parseLong(String s, long def) {
		if (StringUtils.isBlank(s)) {
			return def;
		}

		try {
			return Long.parseLong(s);
		} catch (NumberFormatException e) {
			return def;
		}
	}

}  //  @jve:decl-index=0:visual-constraint="10,10"
