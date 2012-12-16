package net.mikaboshi.jdbc.monitor.viewer;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Frame;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.io.File;
import java.io.IOException;
import java.util.Set;

import javax.management.Attribute;
import javax.management.MBeanServerConnection;
import javax.management.MalformedObjectNameException;
import javax.management.ObjectInstance;
import javax.management.ObjectName;
import javax.management.remote.JMXConnector;
import javax.management.remote.JMXConnectorFactory;
import javax.management.remote.JMXServiceURL;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.WindowConstants;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import net.mikaboshi.gui.GuiUtils;
import net.mikaboshi.gui.IntegerInputVerifier;
import net.mikaboshi.jdbc.monitor.LogModeMBean;
import net.mikaboshi.jdbc.monitor.M17N;

import com.sun.tools.attach.AgentInitializationException;
import com.sun.tools.attach.AgentLoadException;
import com.sun.tools.attach.AttachNotSupportedException;
import com.sun.tools.attach.VirtualMachine;

/**
 * JMXでロガーの設定をする
 * @author Takuma Umezawa
 * @since 1.3.0
 */
public class LoggerConsoleDialog extends JDialog {

	private static final long serialVersionUID = 1L;
	private static final String CONNECTOR_ADDRESS = "com.sun.management.jmxremote.localConnectorAddress";  //  @jve:decl-index=0:
	private static Log logger = LogFactory.getLog(LoggerConsoleDialog.class);  //  @jve:decl-index=0:
	private JPanel jContentPane = null;
	private JPanel bodyPanel = null;
	private JPanel buttonPanel = null;
	private JButton applyButton = null;
	private JButton cancelButton = null;
	private JLabel callStackLevelLabel = null;
	private JTextField callStackLevelTextField = null;
	private JCheckBox addBatchLoggableCheckBox = null;
	private JCheckBox connectionOpenLoggableCheckBox = null;
	private JCheckBox connectionCloseLoggableCheckBox = null;
	private JCheckBox commitLoggableCheckBox = null;
	private JCheckBox rollbackLoggableCheckBox = null;
	private JCheckBox executeStatementLoggableCheckBox = null;
	private JCheckBox executePreparedStatementLoggableCheckBox = null;
	private JCheckBox prepareStatementLoggableCheckBox = null;
	private JCheckBox closeStatementLoggableCheckBox = null;
	private JCheckBox executeBatchLoggableCheckBox = null;

	/**
	 * @param owner
	 */
	public LoggerConsoleDialog(Frame owner) {
		super(owner);
		initialize();
	}

	/**
	 * This method initializes this
	 * 
	 * @return void
	 */
	private void initialize() {
		this.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
		this.setSize(new Dimension(391, 470));
		this.setTitle("LoggerConsoleDialog.title");
		this.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
		this.setContentPane(getJContentPane());
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
			GridBagConstraints gridBagConstraints10 = new GridBagConstraints();
			gridBagConstraints10.anchor = GridBagConstraints.WEST;
			gridBagConstraints10.insets = new Insets(10, 10, 5, 5);
			GridBagConstraints gridBagConstraints9 = new GridBagConstraints();
			gridBagConstraints9.gridx = 0;
			gridBagConstraints9.anchor = GridBagConstraints.WEST;
			gridBagConstraints9.gridwidth = 2;
			gridBagConstraints9.fill = GridBagConstraints.HORIZONTAL;
			gridBagConstraints9.insets = new Insets(5, 10, 10, 5);
			gridBagConstraints9.gridy = 10;
			GridBagConstraints gridBagConstraints7 = new GridBagConstraints();
			gridBagConstraints7.gridx = 0;
			gridBagConstraints7.gridwidth = 2;
			gridBagConstraints7.anchor = GridBagConstraints.WEST;
			gridBagConstraints7.fill = GridBagConstraints.HORIZONTAL;
			gridBagConstraints7.insets = new Insets(5, 10, 5, 10);
			gridBagConstraints7.gridy = 6;
			GridBagConstraints gridBagConstraints6 = new GridBagConstraints();
			gridBagConstraints6.gridx = 0;
			gridBagConstraints6.gridwidth = 2;
			gridBagConstraints6.anchor = GridBagConstraints.WEST;
			gridBagConstraints6.fill = GridBagConstraints.HORIZONTAL;
			gridBagConstraints6.insets = new Insets(5, 10, 5, 10);
			gridBagConstraints6.gridy = 8;
			GridBagConstraints gridBagConstraints5 = new GridBagConstraints();
			gridBagConstraints5.gridx = 0;
			gridBagConstraints5.anchor = GridBagConstraints.WEST;
			gridBagConstraints5.gridwidth = 2;
			gridBagConstraints5.fill = GridBagConstraints.HORIZONTAL;
			gridBagConstraints5.insets = new Insets(5, 10, 5, 10);
			gridBagConstraints5.gridy = 5;
			GridBagConstraints gridBagConstraints4 = new GridBagConstraints();
			gridBagConstraints4.gridx = 0;
			gridBagConstraints4.gridwidth = 2;
			gridBagConstraints4.anchor = GridBagConstraints.WEST;
			gridBagConstraints4.fill = GridBagConstraints.HORIZONTAL;
			gridBagConstraints4.insets = new Insets(5, 10, 5, 10);
			gridBagConstraints4.gridy = 4;
			GridBagConstraints gridBagConstraints31 = new GridBagConstraints();
			gridBagConstraints31.gridx = 0;
			gridBagConstraints31.gridwidth = 2;
			gridBagConstraints31.anchor = GridBagConstraints.WEST;
			gridBagConstraints31.fill = GridBagConstraints.HORIZONTAL;
			gridBagConstraints31.insets = new Insets(5, 10, 5, 10);
			gridBagConstraints31.gridy = 3;
			GridBagConstraints gridBagConstraints21 = new GridBagConstraints();
			gridBagConstraints21.gridx = 0;
			gridBagConstraints21.anchor = GridBagConstraints.WEST;
			gridBagConstraints21.fill = GridBagConstraints.HORIZONTAL;
			gridBagConstraints21.gridwidth = 2;
			gridBagConstraints21.insets = new Insets(5, 10, 5, 10);
			gridBagConstraints21.gridy = 2;
			GridBagConstraints gridBagConstraints11 = new GridBagConstraints();
			gridBagConstraints11.gridx = 0;
			gridBagConstraints11.gridwidth = 2;
			gridBagConstraints11.fill = GridBagConstraints.HORIZONTAL;
			gridBagConstraints11.anchor = GridBagConstraints.WEST;
			gridBagConstraints11.insets = new Insets(5, 10, 5, 10);
			gridBagConstraints11.gridy = 1;
			GridBagConstraints gridBagConstraints1 = new GridBagConstraints();
			gridBagConstraints1.gridx = 0;
			gridBagConstraints1.anchor = GridBagConstraints.WEST;
			gridBagConstraints1.gridwidth = 2;
			gridBagConstraints1.fill = GridBagConstraints.HORIZONTAL;
			gridBagConstraints1.insets = new Insets(5, 10, 5, 10);
			gridBagConstraints1.gridy = 9;
			GridBagConstraints gridBagConstraints12 = new GridBagConstraints();
			gridBagConstraints12.gridx = 0;
			gridBagConstraints12.anchor = GridBagConstraints.WEST;
			gridBagConstraints12.gridwidth = 2;
			gridBagConstraints12.fill = GridBagConstraints.HORIZONTAL;
			gridBagConstraints12.insets = new Insets(5, 10, 5, 10);
			gridBagConstraints12.gridy = 7;
			GridBagConstraints gridBagConstraints = new GridBagConstraints();
			gridBagConstraints.fill = GridBagConstraints.BOTH;
			gridBagConstraints.gridy = 0;
			gridBagConstraints.weightx = 1.0;
			gridBagConstraints.insets = new Insets(10, 5, 5, 10);
			gridBagConstraints.fill = GridBagConstraints.VERTICAL;
			gridBagConstraints.ipadx = 20;
			gridBagConstraints.anchor = GridBagConstraints.WEST;
			gridBagConstraints.ipadx = 50;
			gridBagConstraints.gridx = 1;
			callStackLevelLabel = new JLabel();
			callStackLevelLabel.setText("LoggerConsoleDialog.callStackLevelLabel");
			bodyPanel = new JPanel();
			bodyPanel.setLayout(new GridBagLayout());
			bodyPanel.add(callStackLevelLabel, gridBagConstraints10);
			bodyPanel.add(getCallStackLevelTextField(), gridBagConstraints);
			bodyPanel.add(getAddBatchLoggableCheckBox(), gridBagConstraints1);
			bodyPanel.add(getConnectionOpenLoggableCheckBox(), gridBagConstraints11);
			bodyPanel.add(getConnectionCloseLoggableCheckBox(), gridBagConstraints21);
			bodyPanel.add(getCommitLoggableCheckBox(), gridBagConstraints31);
			bodyPanel.add(getRollbackLoggableCheckBox(), gridBagConstraints4);
			bodyPanel.add(getExecuteStatementLoggableCheckBox(), gridBagConstraints5);
			bodyPanel.add(getExecutePreparedStatementLoggableCheckBox(), gridBagConstraints6);
			bodyPanel.add(getPrepareStatementLoggableCheckBox(), gridBagConstraints7);
			bodyPanel.add(getCloseStatementLoggableCheckBox(), gridBagConstraints12);
			bodyPanel.add(getExecuteBatchLoggableCheckBox(), gridBagConstraints9);
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
			FlowLayout flowLayout = new FlowLayout();
			flowLayout.setHgap(10);
			flowLayout.setVgap(10);
			GridBagConstraints gridBagConstraints3 = new GridBagConstraints();
			gridBagConstraints3.fill = GridBagConstraints.HORIZONTAL;
			gridBagConstraints3.gridy = 0;
			gridBagConstraints3.weightx = 1.0;
			gridBagConstraints3.weighty = 1.0;
			gridBagConstraints3.insets = new Insets(10, 5, 5, 10);
			gridBagConstraints3.gridx = 1;
			GridBagConstraints gridBagConstraints2 = new GridBagConstraints();
			gridBagConstraints2.gridx = 0;
			gridBagConstraints2.anchor = GridBagConstraints.WEST;
			gridBagConstraints2.insets = new Insets(10, 10, 5, 10);
			gridBagConstraints2.gridy = 0;
			buttonPanel = new JPanel();
			buttonPanel.setLayout(new FlowLayout());
			buttonPanel.setLayout(flowLayout);
			buttonPanel.setLayout(flowLayout);
			buttonPanel.add(getApplyButton(), null);
			buttonPanel.add(getCancelButton(), null);
		}
		return buttonPanel;
	}

	/**
	 * This method initializes applyButton	
	 * 	
	 * @return javax.swing.JButton	
	 */
	private JButton getApplyButton() {
		if (applyButton == null) {
			applyButton = new JButton();
			applyButton.setText("LoggerConsoleDialog.applyButton");
			
			applyButton.addActionListener(new java.awt.event.ActionListener() {
				public void actionPerformed(java.awt.event.ActionEvent e) {
					storeConfig();
					dispose();
				}
			});
		}
		return applyButton;
	}

	/**
	 * This method initializes cancelButton	
	 * 	
	 * @return javax.swing.JButton	
	 */
	private JButton getCancelButton() {
		if (cancelButton == null) {
			cancelButton = new JButton();
			cancelButton.setText("LoggerConsoleDialog.cancelButton");
			
			cancelButton.addActionListener(new java.awt.event.ActionListener() {
				public void actionPerformed(java.awt.event.ActionEvent e) {
					dispose();
				}
			});
		}
		return cancelButton;
	}

	/**
	 * This method initializes callStackLevelTextField	
	 * 	
	 * @return javax.swing.JTextField	
	 */
	private JTextField getCallStackLevelTextField() {
		if (callStackLevelTextField == null) {
			callStackLevelTextField = new JTextField();
			callStackLevelTextField.setInputVerifier(
					new IntegerInputVerifier(M17N.getWithKeyArgs("validate.integer", "LoggerConsoleDialog.callStackLevelLabel")));
		}
		return callStackLevelTextField;
	}

	/**
	 * This method initializes addBatchLoggableCheckBox	
	 * 	
	 * @return javax.swing.JCheckBox	
	 */
	private JCheckBox getAddBatchLoggableCheckBox() {
		if (addBatchLoggableCheckBox == null) {
			addBatchLoggableCheckBox = new JCheckBox();
			addBatchLoggableCheckBox.setText("LoggerConsoleDialog.addBatchLoggable");
		}
		return addBatchLoggableCheckBox;
	}

	/**
	 * This method initializes connectionOpenLoggableCheckBox	
	 * 	
	 * @return javax.swing.JCheckBox	
	 */
	private JCheckBox getConnectionOpenLoggableCheckBox() {
		if (connectionOpenLoggableCheckBox == null) {
			connectionOpenLoggableCheckBox = new JCheckBox();
			connectionOpenLoggableCheckBox.setText("LoggerConsoleDialog.connectionOpenLoggable");
		}
		return connectionOpenLoggableCheckBox;
	}

	/**
	 * This method initializes connectionCloseLoggableCheckBox	
	 * 	
	 * @return javax.swing.JCheckBox	
	 */
	private JCheckBox getConnectionCloseLoggableCheckBox() {
		if (connectionCloseLoggableCheckBox == null) {
			connectionCloseLoggableCheckBox = new JCheckBox();
			connectionCloseLoggableCheckBox.setText("LoggerConsoleDialog.connectionCloseLoggable");
		}
		return connectionCloseLoggableCheckBox;
	}

	/**
	 * This method initializes commitLoggableCheckBox	
	 * 	
	 * @return javax.swing.JCheckBox	
	 */
	private JCheckBox getCommitLoggableCheckBox() {
		if (commitLoggableCheckBox == null) {
			commitLoggableCheckBox = new JCheckBox();
			commitLoggableCheckBox.setText("LoggerConsoleDialog.commitLoggable");
		}
		return commitLoggableCheckBox;
	}

	/**
	 * This method initializes rollbackLoggableCheckBox	
	 * 	
	 * @return javax.swing.JCheckBox	
	 */
	private JCheckBox getRollbackLoggableCheckBox() {
		if (rollbackLoggableCheckBox == null) {
			rollbackLoggableCheckBox = new JCheckBox();
			rollbackLoggableCheckBox.setText("LoggerConsoleDialog.rollbackLoggable");
		}
		return rollbackLoggableCheckBox;
	}

	/**
	 * This method initializes executeStatementLoggableCheckBox	
	 * 	
	 * @return javax.swing.JCheckBox	
	 */
	private JCheckBox getExecuteStatementLoggableCheckBox() {
		if (executeStatementLoggableCheckBox == null) {
			executeStatementLoggableCheckBox = new JCheckBox();
			executeStatementLoggableCheckBox.setText("LoggerConsoleDialog.executeStatementLoggable");
		}
		return executeStatementLoggableCheckBox;
	}

	/**
	 * This method initializes executePreparedStatementLoggableCheckBox	
	 * 	
	 * @return javax.swing.JCheckBox	
	 */
	private JCheckBox getExecutePreparedStatementLoggableCheckBox() {
		if (executePreparedStatementLoggableCheckBox == null) {
			executePreparedStatementLoggableCheckBox = new JCheckBox();
			executePreparedStatementLoggableCheckBox.setText("LoggerConsoleDialog.executePreparedStatementLoggable");
		}
		return executePreparedStatementLoggableCheckBox;
	}

	/**
	 * This method initializes prepareStatementLoggableCheckBox	
	 * 	
	 * @return javax.swing.JCheckBox	
	 */
	private JCheckBox getPrepareStatementLoggableCheckBox() {
		if (prepareStatementLoggableCheckBox == null) {
			prepareStatementLoggableCheckBox = new JCheckBox();
			prepareStatementLoggableCheckBox.setText("LoggerConsoleDialog.prepareStatementLoggable");
			prepareStatementLoggableCheckBox
					.addChangeListener(new javax.swing.event.ChangeListener() {
						public void stateChanged(javax.swing.event.ChangeEvent e) {
							getExecutePreparedStatementLoggableCheckBox().setEnabled(
									prepareStatementLoggableCheckBox.isSelected());
						}
					});
		}
		return prepareStatementLoggableCheckBox;
	}

	/**
	 * This method initializes closeStatementLoggableCheckBox	
	 * 	
	 * @return javax.swing.JCheckBox	
	 */
	private JCheckBox getCloseStatementLoggableCheckBox() {
		if (closeStatementLoggableCheckBox == null) {
			closeStatementLoggableCheckBox = new JCheckBox();
			closeStatementLoggableCheckBox.setText("LoggerConsoleDialog.closeStatementLoggable");
		}
		return closeStatementLoggableCheckBox;
	}
	
	/**
	 * This method initializes executeBatchLoggableCheckBox	
	 * 	
	 * @return javax.swing.JCheckBox	
	 */
	private JCheckBox getExecuteBatchLoggableCheckBox() {
		if (executeBatchLoggableCheckBox == null) {
			executeBatchLoggableCheckBox = new JCheckBox();
			executeBatchLoggableCheckBox.setText("LoggerConsoleDialog.executeBatchLoggable");
		}
		return executeBatchLoggableCheckBox;
	}
	
	private String pid = null;  //  @jve:decl-index=0:
	
	/**
	 * JMXで設定を読み込む
	 * @param pid
	 */
	boolean loadConfig(String pid) {
		
		this.pid = pid;
		
		try {
			MBeanServerConnection connection = getConnection();
			
			if (connection == null) {
				JOptionPane.showMessageDialog(
						null,
						M17N.get("LoggerConsoleDialog.error.get_mbean"),
						"",
						JOptionPane.WARNING_MESSAGE);
				return false;
			}
			
			Set<ObjectInstance> mbeans = connection.queryMBeans(
					new ObjectName(LogModeMBean.JMX_OBJECT_NAME), null);
	
			for (ObjectInstance objInstance : mbeans) {
				
				ObjectName objectName = objInstance.getObjectName();
				
				getCallStackLevelTextField().setText(
						String.valueOf(connection.getAttribute(objectName, "CallStackLevel")));
				getConnectionOpenLoggableCheckBox().setSelected(
						(Boolean) connection.getAttribute(objectName, "ConnectionOpenLoggable"));
				getConnectionCloseLoggableCheckBox().setSelected(
						(Boolean) connection.getAttribute(objectName, "ConnectionCloseLoggable"));
				getCommitLoggableCheckBox().setSelected(
						(Boolean) connection.getAttribute(objectName, "CommitLoggable"));
				getRollbackLoggableCheckBox().setSelected(
						(Boolean) connection.getAttribute(objectName, "RollbackLoggable"));
				getExecuteStatementLoggableCheckBox().setSelected(
						(Boolean) connection.getAttribute(objectName, "ExecuteStatementLoggable"));
				getPrepareStatementLoggableCheckBox().setSelected(
						(Boolean) connection.getAttribute(objectName, "PrepareStatementLoggable"));
				getExecutePreparedStatementLoggableCheckBox().setSelected(
						(Boolean) connection.getAttribute(objectName, "ExecutePreparedStatementLoggable"));
				getCloseStatementLoggableCheckBox().setSelected(
						(Boolean) connection.getAttribute(objectName, "CloseStatementLoggable"));
				getAddBatchLoggableCheckBox().setSelected(
						(Boolean) connection.getAttribute(objectName, "AddBatchLoggable"));
				getExecuteBatchLoggableCheckBox().setSelected(
						(Boolean) connection.getAttribute(objectName, "ExecuteBatchLoggable"));
			}
		} catch (AttachNotSupportedException e) {
			String message = M17N.get("LoggerConsoleDialog.error.attach_not_supported");
			logger.error(message, e);
			
			JOptionPane.showMessageDialog(
					null,
					message,
					"",
					JOptionPane.ERROR_MESSAGE);
			
			return false;
			
		} catch (Exception e) {
			String message = M17N.get("LoggerConsoleDialog.error.get_mbean");
			logger.error(message, e);
			
			JOptionPane.showMessageDialog(
					null,
					message,
					"",
					JOptionPane.ERROR_MESSAGE);
			
			return false;
		}
		
		getExecutePreparedStatementLoggableCheckBox().setEnabled(
				getPrepareStatementLoggableCheckBox().isSelected());
		
		return true;
	}
	
	private void storeConfig() {
		
		try {
			MBeanServerConnection connection = getConnection();
			
			if (connection == null) {
				JOptionPane.showMessageDialog(
						null,
						M17N.get("LoggerConsoleDialog.warn.mbean_not_found"),
						"",
						JOptionPane.WARNING_MESSAGE);
				return;
			}
			
			ObjectName objectName = new ObjectName(LogModeMBean.JMX_OBJECT_NAME);
			
			connection.setAttribute(objectName,	
					new Attribute("CallStackLevel", 
						Integer.valueOf(getCallStackLevelTextField().getText())));
			connection.setAttribute(objectName,	
					new Attribute("ConnectionOpenLoggable", 
						getConnectionOpenLoggableCheckBox().isSelected()));
			connection.setAttribute(objectName,	
					new Attribute("ConnectionCloseLoggable", 
						getConnectionCloseLoggableCheckBox().isSelected()));
			connection.setAttribute(objectName,	
					new Attribute("CommitLoggable", 
						getCommitLoggableCheckBox().isSelected()));
			connection.setAttribute(objectName,	
					new Attribute("RollbackLoggable", 
						getRollbackLoggableCheckBox().isSelected()));			
			connection.setAttribute(objectName,	
					new Attribute("ExecuteStatementLoggable", 
						getExecuteStatementLoggableCheckBox().isSelected()));
			connection.setAttribute(objectName,	
					new Attribute("PrepareStatementLoggable", 
						getPrepareStatementLoggableCheckBox().isSelected()));
			connection.setAttribute(objectName,	
					new Attribute("ExecutePreparedStatementLoggable", 
						getExecutePreparedStatementLoggableCheckBox().isSelected()));
			connection.setAttribute(objectName,	
					new Attribute("CloseStatementLoggable", 
						getCloseStatementLoggableCheckBox().isSelected()));
			connection.setAttribute(objectName,	
					new Attribute("AddBatchLoggable", 
						getAddBatchLoggableCheckBox().isSelected()));
			connection.setAttribute(objectName,	
					new Attribute("ExecuteBatchLoggable", 
						getExecuteBatchLoggableCheckBox().isSelected()));
		
		} catch (Exception e) {
			String message = M17N.get("LoggerConsoleDialog.error.set_mbean");
			logger.error(message, e);
			
			JOptionPane.showMessageDialog(
					null,
					message,
					"",
					JOptionPane.ERROR_MESSAGE);
		} 
	}
	
	private MBeanServerConnection getConnection() throws AttachNotSupportedException, IOException, AgentLoadException, AgentInitializationException, MalformedObjectNameException, NullPointerException {
		// attach to the target application
		VirtualMachine vm = VirtualMachine.attach(this.pid);

		// get the connector address
		String connectorAddress = vm.getAgentProperties().getProperty(
				CONNECTOR_ADDRESS);

		// no connector address, so we start the JMX agent
		if (connectorAddress == null) {
			String agent = vm.getSystemProperties()
					.getProperty("java.home")
					+ File.separator
					+ "lib"
					+ File.separator
					+ "management-agent.jar";
			vm.loadAgent(agent);

			// agent is started, get the connector address
			connectorAddress = vm.getAgentProperties().getProperty(
					CONNECTOR_ADDRESS);
		}

		// establish connection to connector server
		JMXServiceURL url = new JMXServiceURL(connectorAddress);
		JMXConnector connector = JMXConnectorFactory.connect(url);

		MBeanServerConnection connection = connector
				.getMBeanServerConnection();

		Set<ObjectInstance> mbeans = connection.queryMBeans(
				new ObjectName(LogModeMBean.JMX_OBJECT_NAME), null);

		if (mbeans.isEmpty()) {
			return null;
		}
		
		return connection;
	}

}  //  @jve:decl-index=0:visual-constraint="10,10"
