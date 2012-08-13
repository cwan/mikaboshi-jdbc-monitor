package net.mikaboshi.jdbc.monitor.viewer;

import java.awt.Desktop;
import java.awt.Frame;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.net.URI;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.WindowConstants;

import net.mikaboshi.gui.GuiUtils;
import net.mikaboshi.jdbc.monitor.M17N;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * バージョン情報ダイアログ
 * @author Takuma Umezawa
 * @since 1.2.3
 */
public class VersionInfoDialog extends JDialog {

	private static final long serialVersionUID = 1L;
	private static Log logger = LogFactory.getLog(VersionInfoDialog.class);
	private JPanel jContentPane = null;
	private JLabel versionLabel = null;
	private JLabel copyrightLabel = null;
	private JLabel linkLabel = null;
	private JButton okButton = null;

	/**
	 * @param owner
	 */
	public VersionInfoDialog(Frame owner) {
		super(owner);
		initialize();
	}

	/**
	 * This method initializes this
	 * 
	 * @return void
	 */
	private void initialize() {
		this.setSize(370, 194);
		this.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
		this.setTitle("VersionInfoDialog.title");
		this.setContentPane(getJContentPane());
	}

	/**
	 * This method initializes jContentPane
	 * 
	 * @return javax.swing.JPanel
	 */
	private JPanel getJContentPane() {
		if (jContentPane == null) {
			GridBagConstraints gridBagConstraints4 = new GridBagConstraints();
			gridBagConstraints4.gridx = 0;
			gridBagConstraints4.ipady = 0;
			gridBagConstraints4.gridheight = 1;
			gridBagConstraints4.weighty = 90.0;
			gridBagConstraints4.ipadx = 24;
			gridBagConstraints4.gridy = 3;
			GridBagConstraints gridBagConstraints3 = new GridBagConstraints();
			gridBagConstraints3.gridx = 0;
			gridBagConstraints3.ipady = 0;
			gridBagConstraints3.anchor = GridBagConstraints.WEST;
			gridBagConstraints3.fill = GridBagConstraints.HORIZONTAL;
			gridBagConstraints3.weighty = 48.0;
			gridBagConstraints3.gridy = 2;
			linkLabel = new JLabel();
			final String href = M17N.get("VersionInfoDialog.site.url");
			linkLabel.setText("<html><a href=\"#\">" + href + "</a></html>");
			linkLabel.addMouseListener(new java.awt.event.MouseAdapter() {
				public void mouseClicked(java.awt.event.MouseEvent e) {
					try {
						Desktop.getDesktop().browse(new URI(href));
					} catch (Exception ex) {
						logger.error(ex.getMessage(), ex);
					}
				}
			});
			GridBagConstraints gridBagConstraints2 = new GridBagConstraints();
			gridBagConstraints2.gridx = 0;
			gridBagConstraints2.ipady = 0;
			gridBagConstraints2.fill = GridBagConstraints.HORIZONTAL;
			gridBagConstraints2.anchor = GridBagConstraints.WEST;
			gridBagConstraints2.weighty = 48.0;
			gridBagConstraints2.gridy = 1;
			copyrightLabel = new JLabel();
			copyrightLabel.setText("VersionInfoDialog.copyright");
			GridBagConstraints gridBagConstraints = new GridBagConstraints();
			gridBagConstraints.gridx = 0;
			gridBagConstraints.anchor = GridBagConstraints.WEST;
			gridBagConstraints.gridwidth = 1;
			gridBagConstraints.insets = new Insets(10, 0, 0, 0);
			gridBagConstraints.fill = GridBagConstraints.HORIZONTAL;
			gridBagConstraints.ipady = 0;
			gridBagConstraints.weighty = 48.0;
			gridBagConstraints.gridy = 0;
			versionLabel = new JLabel();
			versionLabel.setText("VersionInfoDialog.version");
			jContentPane = new JPanel();
			jContentPane.setLayout(new GridBagLayout());
			jContentPane.add(versionLabel, gridBagConstraints);
			jContentPane.add(copyrightLabel, gridBagConstraints2);
			jContentPane.add(linkLabel, gridBagConstraints3);
			jContentPane.add(getOkButton(), gridBagConstraints4);
			
			// ESCで閉じる
			GuiUtils.closeByESC(this, jContentPane);
		}
		return jContentPane;
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
					dispose();
				}
			});
		}
		return okButton;
	}

}  //  @jve:decl-index=0:visual-constraint="10,10"
