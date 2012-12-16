package net.mikaboshi.jdbc.monitor.viewer;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Frame;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.sql.Driver;
import java.util.Properties;

import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.WindowConstants;

import net.mikaboshi.gui.GuiUtils;
import net.mikaboshi.gui.PositiveIntegerInputVerifier;
import net.mikaboshi.jdbc.monitor.M17N;
import net.mikaboshi.jdbc.monitor.ViewerConfig;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.LogFactory;

/**
 * 接続設定ダイアログ
 * @author Takuma Umezawa
 * @since 1.3.0
 */
public class ConnectConfigDialog extends JDialog {

	private static final long serialVersionUID = 1L;
	private JPanel jContentPane = null;
	private JPanel bodyPanel = null;
	private JPanel buttonPanel = null;
	private JButton okButton = null;
	private JButton cancelButton = null;
	private JLabel driverClassLabel = null;
	private JLabel urlLabel = null;
	private JLabel userLabel = null;
	private JLabel passwordLabel = null;
	private JTextField driverClassTextField = null;
	private JTextField urlTextField = null;
	private JTextField userTextField = null;
	private JTextField passwordTextField = null;
	private JLabel rowsLimitLabel = null;
	private JTextField rowsLimitTextField = null;
	private JCheckBox loadFromLogCheckBox = null;
	/**
	 * @param owner
	 */
	public ConnectConfigDialog(Frame owner) {
		super(owner);
		initialize();
	}

	/**
	 * This method initializes this
	 * 
	 * @return void
	 */
	private void initialize() {
		this.setSize(461, 269);
		this.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
		this.setTitle("ConnectConfigDialog.title");
		this.setContentPane(getJContentPane());
		
		ViewerConfig.ConnectInfo connectInfo = ViewerConfig.getInstance().getConnectInfo();
		getDriverClassTextField().setText(connectInfo.getDriver());
		getUrlTextField().setText(connectInfo.getUrl());
		getUserTextField().setText(connectInfo.getUser());
		getPasswordTextField().setText(connectInfo.getPassword());
		getRowsLimitTextField().setText(
				Integer.toString(ViewerConfig.getInstance().getLimitExecuteSqlRows()));
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
			jContentPane.add(getButtonPanel(), BorderLayout.SOUTH);
			
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
			GridBagConstraints gridBagConstraints12 = new GridBagConstraints();
			gridBagConstraints12.gridx = 0;
			gridBagConstraints12.anchor = GridBagConstraints.WEST;
			gridBagConstraints12.gridwidth = 2;
			gridBagConstraints12.insets = new Insets(5, 10, 10, 10);
			gridBagConstraints12.gridy = 6;
			GridBagConstraints gridBagConstraints11 = new GridBagConstraints();
			gridBagConstraints11.fill = GridBagConstraints.BOTH;
			gridBagConstraints11.gridy = 5;
			gridBagConstraints11.weightx = 1.0;
			gridBagConstraints11.insets = new Insets(5, 10, 10, 10);
			gridBagConstraints11.gridx = 1;
			GridBagConstraints gridBagConstraints10 = new GridBagConstraints();
			gridBagConstraints10.gridx = 0;
			gridBagConstraints10.insets = new Insets(5, 10, 5, 5);
			gridBagConstraints10.anchor = GridBagConstraints.WEST;
			gridBagConstraints10.gridy = 5;
			rowsLimitLabel = new JLabel();
			rowsLimitLabel.setText("ConnectConfigDialog.rows_limit");
			GridBagConstraints gridBagConstraints9 = new GridBagConstraints();
			gridBagConstraints9.fill = GridBagConstraints.HORIZONTAL;
			gridBagConstraints9.gridy = 4;
			gridBagConstraints9.weightx = 1.0;
			gridBagConstraints9.insets = new Insets(5, 10, 5, 10);
			gridBagConstraints9.anchor = GridBagConstraints.WEST;
			gridBagConstraints9.gridx = 1;
			GridBagConstraints gridBagConstraints8 = new GridBagConstraints();
			gridBagConstraints8.fill = GridBagConstraints.HORIZONTAL;
			gridBagConstraints8.gridy = 3;
			gridBagConstraints8.weightx = 1.0;
			gridBagConstraints8.insets = new Insets(5, 10, 5, 10);
			gridBagConstraints8.anchor = GridBagConstraints.WEST;
			gridBagConstraints8.gridx = 1;
			GridBagConstraints gridBagConstraints7 = new GridBagConstraints();
			gridBagConstraints7.fill = GridBagConstraints.HORIZONTAL;
			gridBagConstraints7.gridy = 2;
			gridBagConstraints7.weightx = 1.0;
			gridBagConstraints7.insets = new Insets(5, 10, 5, 10);
			gridBagConstraints7.anchor = GridBagConstraints.WEST;
			gridBagConstraints7.gridx = 1;
			GridBagConstraints gridBagConstraints6 = new GridBagConstraints();
			gridBagConstraints6.fill = GridBagConstraints.HORIZONTAL;
			gridBagConstraints6.gridy = 1;
			gridBagConstraints6.weightx = 0.0;
			gridBagConstraints6.insets = new Insets(10, 10, 5, 10);
			gridBagConstraints6.anchor = GridBagConstraints.WEST;
			gridBagConstraints6.gridx = 1;
			GridBagConstraints gridBagConstraints5 = new GridBagConstraints();
			gridBagConstraints5.gridx = 0;
			gridBagConstraints5.anchor = GridBagConstraints.WEST;
			gridBagConstraints5.insets = new Insets(5, 10, 5, 5);
			gridBagConstraints5.gridy = 4;
			passwordLabel = new JLabel();
			passwordLabel.setText("ConnectConfigDialog.password");
			GridBagConstraints gridBagConstraints4 = new GridBagConstraints();
			gridBagConstraints4.gridx = 0;
			gridBagConstraints4.anchor = GridBagConstraints.WEST;
			gridBagConstraints4.insets = new Insets(5, 10, 5, 10);
			gridBagConstraints4.gridy = 3;
			userLabel = new JLabel();
			userLabel.setText("ConnectConfigDialog.user");
			GridBagConstraints gridBagConstraints3 = new GridBagConstraints();
			gridBagConstraints3.gridx = 0;
			gridBagConstraints3.insets = new Insets(5, 10, 5, 10);
			gridBagConstraints3.anchor = GridBagConstraints.WEST;
			gridBagConstraints3.gridy = 2;
			urlLabel = new JLabel();
			urlLabel.setText("ConnectConfigDialog.url");
			GridBagConstraints gridBagConstraints2 = new GridBagConstraints();
			gridBagConstraints2.gridx = 0;
			gridBagConstraints2.insets = new Insets(10, 10, 5, 10);
			gridBagConstraints2.anchor = GridBagConstraints.WEST;
			gridBagConstraints2.gridy = 1;
			driverClassLabel = new JLabel();
			driverClassLabel.setText("ConnectConfigDialog.driver");
			bodyPanel = new JPanel();
			bodyPanel.setLayout(new GridBagLayout());
			bodyPanel.add(driverClassLabel, gridBagConstraints2);
			bodyPanel.add(urlLabel, gridBagConstraints3);
			bodyPanel.add(userLabel, gridBagConstraints4);
			bodyPanel.add(passwordLabel, gridBagConstraints5);
			bodyPanel.add(getDriverClassTextField(), gridBagConstraints6);
			bodyPanel.add(getUrlTextField(), gridBagConstraints7);
			bodyPanel.add(getUserTextField(), gridBagConstraints8);
			bodyPanel.add(getPasswordTextField(), gridBagConstraints9);
			bodyPanel.add(rowsLimitLabel, gridBagConstraints10);
			bodyPanel.add(getRowsLimitTextField(), gridBagConstraints11);
			bodyPanel.add(getLoadFromLogCheckBox(), gridBagConstraints12);
		}
		return bodyPanel;
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
			gridBagConstraints1.gridy = 2;
			GridBagConstraints gridBagConstraints = new GridBagConstraints();
			gridBagConstraints.ipadx = 40;
			gridBagConstraints.insets = new Insets(5, 10, 5, 10);
			gridBagConstraints.gridy = 2;
			gridBagConstraints.weightx = 0.0;
			buttonPanel = new JPanel();
			buttonPanel.setLayout(new GridBagLayout());
			buttonPanel.add(getOkButton(), gridBagConstraints);
			buttonPanel.add(getCancelButton(), gridBagConstraints1);
		}
		return buttonPanel;
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
				@SuppressWarnings("unchecked")
				public void actionPerformed(java.awt.event.ActionEvent e) {
					
					if (StringUtils.isBlank(getDriverClassTextField().getText())) {
						JOptionPane.showMessageDialog(
								null,
								M17N.get("ConnectConfigDialog.validate.no_driver"),
								"",
								JOptionPane.WARNING_MESSAGE);
						
					} else if (StringUtils.isBlank(getUrlTextField().getText())) {
						JOptionPane.showMessageDialog(
								null,
								M17N.get("ConnectConfigDialog.validate.no_url"),
								"",
								JOptionPane.WARNING_MESSAGE);
						
					} else {
						
						try {
							Class<Driver> driverClass = (Class<Driver>) Class.forName(getDriverClassTextField().getText());
							
							// 接続テスト
							try {
								Driver driver = (Driver) driverClass.newInstance();
								Properties info = new Properties();
								info.setProperty("user", getUserTextField().getText());
								info.setProperty("password", getPasswordTextField().getText());
								driver.connect(getUrlTextField().getText(), info);
								
							} catch (Exception e1) {
								JOptionPane.showMessageDialog(
										null,
										M17N.get("ConnectConfigDialog.validate.connect_failed") +
										"\n" + e1.getMessage(),
										"",
										JOptionPane.WARNING_MESSAGE);
								
								LogFactory.getLog(ConnectConfigDialog.class).warn(
										M17N.get("ConnectConfigDialog.validate.connect_failed")
										, e1);
							}
							
						} catch (ClassNotFoundException e1) {
							// ドライバクラスが存在しない
							JOptionPane.showMessageDialog(
									null,
									M17N.get("ConnectConfigDialog.validate.driver_not_found"),
									"",
									JOptionPane.WARNING_MESSAGE);
						}
					}
					
					ViewerConfig config = ViewerConfig.getInstance();
					ViewerConfig.ConnectInfo connectInfo = config.getConnectInfo();
					connectInfo.setDriver(getDriverClassTextField().getText());
					connectInfo.setUrl(getUrlTextField().getText());
					connectInfo.setUser(getUserTextField().getText());
					connectInfo.setPassword(getPasswordTextField().getText());
					config.setLimitExecuteSqlRows(
							Integer.parseInt(getRowsLimitTextField().getText()));
					config.setLoadConnectInfoFromLog(
							getLoadFromLogCheckBox().isSelected());
					
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
	 * This method initializes driverClassTextField	
	 * 	
	 * @return javax.swing.JTextField	
	 */
	private JTextField getDriverClassTextField() {
		if (driverClassTextField == null) {
			driverClassTextField = new JTextField();
			driverClassTextField.setPreferredSize(new Dimension(300, 20));
		}
		return driverClassTextField;
	}

	/**
	 * This method initializes urlTextField	
	 * 	
	 * @return javax.swing.JTextField	
	 */
	private JTextField getUrlTextField() {
		if (urlTextField == null) {
			urlTextField = new JTextField();
			urlTextField.setPreferredSize(new Dimension(300, 20));
		}
		return urlTextField;
	}

	/**
	 * This method initializes userTextField	
	 * 	
	 * @return javax.swing.JTextField	
	 */
	private JTextField getUserTextField() {
		if (userTextField == null) {
			userTextField = new JTextField();
			userTextField.setPreferredSize(new Dimension(120, 20));
		}
		return userTextField;
	}

	/**
	 * This method initializes passwordTextField	
	 * 	
	 * @return javax.swing.JTextField	
	 */
	private JTextField getPasswordTextField() {
		if (passwordTextField == null) {
			passwordTextField = new JTextField();
			passwordTextField.setPreferredSize(new Dimension(120, 20));
		}
		return passwordTextField;
	}

	/**
	 * This method initializes rowsLimitTextField	
	 * 	
	 * @return javax.swing.JTextField	
	 */
	private JTextField getRowsLimitTextField() {
		if (rowsLimitTextField == null) {
			rowsLimitTextField = new JTextField();
			
			// 入力値が正の整数であるかチェックする
			rowsLimitTextField.setInputVerifier(
				new PositiveIntegerInputVerifier( M17N.getWithKeyArgs("validate.positive.integer", "ConnectConfigDialog.rows_limit")) );
		}
		return rowsLimitTextField;
	}

	/**
	 * This method initializes loadFromLogCheckBox	
	 * 	
	 * @return javax.swing.JCheckBox	
	 */
	private JCheckBox getLoadFromLogCheckBox() {
		if (loadFromLogCheckBox == null) {
			loadFromLogCheckBox = new JCheckBox();
			loadFromLogCheckBox.setText("ConnectConfigDialog.load_from_log");
			
			loadFromLogCheckBox.setSelected(
					ViewerConfig.getInstance().isLoadConnectInfoFromLog());
		}
		return loadFromLogCheckBox;
	}

}  //  @jve:decl-index=0:visual-constraint="10,10"
