package net.mikaboshi.jdbc.monitor.viewer;

import java.awt.BorderLayout;
import java.awt.Frame;
import java.awt.event.MouseEvent;
import java.util.List;

import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.ListSelectionModel;
import javax.swing.SwingUtilities;
import javax.swing.WindowConstants;
import javax.swing.event.MouseInputAdapter;

import net.mikaboshi.gui.GuiUtils;
import net.mikaboshi.jdbc.monitor.M17N;

import com.sun.tools.attach.VirtualMachine;
import com.sun.tools.attach.VirtualMachineDescriptor;

/**
 * ロガーの設定をするため、対象のJavaプロセスを選択する
 * @author Takuma Umezawa
 * @since 1.3.0
 */
public class JavaProcessDialog extends JDialog {

	private static final long serialVersionUID = 1L;
	private JPanel jpsContentPane = null;
	private JScrollPane jpsScrollPane = null;
	private JTable jpsTable = null;
	private JLabel selectJpsLabel = null;
	
	/**
	 * This method initializes jpsScrollPane	
	 * 	
	 * @return javax.swing.JScrollPane	
	 */
	private JScrollPane getJpsScrollPane() {
		if (jpsScrollPane == null) {
			jpsScrollPane = new JScrollPane();
			jpsScrollPane.setViewportView(getJpsTable());
		}
		return jpsScrollPane;
	}

	/**
	 * This method initializes jpsTable	
	 * 	
	 * @return javax.swing.JTable	
	 */
	private JTable getJpsTable() {
		if (jpsTable == null) {
			
			String[] header = {
				M17N.get("JavaProcessDialog.jpsTable.name"),
				M17N.get("JavaProcessDialog.jpsTable.pid")
			};
			
			List<VirtualMachineDescriptor> vmList = VirtualMachine.list();
			
			if (vmList == null || vmList.isEmpty()) {
				JOptionPane.showMessageDialog(
						null,
						M17N.get("JavaProcessDialog.warn.jps_not_found"),
						"",
						JOptionPane.ERROR_MESSAGE);
			}
			
			String[][] data = new String[vmList.size()][2];
			
			int iVm = 0;
			for (VirtualMachineDescriptor vm : vmList) {
				data[iVm++] = new String[] {
						vm.displayName(),
						vm.id()
				};
			}
			
			jpsTable = new JTable(data, header) {
				private static final long serialVersionUID = 1L;

				@Override
				public String getToolTipText(MouseEvent event) {
					// 選択行の名前をツールチップテキストで表示する
					
				    int row = rowAtPoint(event.getPoint());
				    
				    if (row < 0) {
				    	return null;
				    }
				    
				    return getModel().getValueAt(row, 0).toString();
				}
				
				@Override
				public boolean isCellEditable(int row, int column) {
					return false;
				}
				
			};
			jpsTable.setCellSelectionEnabled(false);
			jpsTable.setRowSelectionAllowed(true);
			jpsTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
			jpsTable.setColumnSelectionAllowed(false);
			
			jpsTable.getColumnModel().getColumn(0).setPreferredWidth(200);
			
			jpsTable.addMouseListener(new MouseInputAdapter() {
				@Override
				public void mouseClicked(MouseEvent event) {
					// セルをダブルクリックしたとき、設定ペインに切り替える
					if (SwingUtilities.isLeftMouseButton(event) &&
							event.getClickCount() == 2) {
						
						String pid = jpsTable.getValueAt(
								jpsTable.rowAtPoint(event.getPoint()), 1).toString();
						
						LoggerConsoleDialog dialog = new LoggerConsoleDialog((Frame) getOwner());
						
						if (dialog.loadConfig(pid)) {
							dialog.setLocationRelativeTo(JavaProcessDialog.this);
							dialog.setVisible(true);
							dispose();
						} else {
							dialog.dispose();
						}
						
						return;
					}
				}
			});
		}
		return jpsTable;
	}

	/**
	 * @param owner
	 */
	public JavaProcessDialog(Frame owner) {
		super(owner);
		initialize();
	}
	
	public boolean isLoadSuccess() {
		return getJpsTable().getRowCount() != 0;
	}

	/**
	 * This method initializes this
	 * 
	 * @return void
	 */
	private void initialize() {
		this.setSize(411, 211);
		this.setTitle("JavaProcessDialog.title");
		this.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
		this.setContentPane(getJpsContentPane());
	}

	/**
	 * This method initializes jpsContentPane
	 * 
	 * @return javax.swing.JPanel
	 */
	private JPanel getJpsContentPane() {
		if (jpsContentPane == null) {
			BorderLayout borderLayout = new BorderLayout();
			borderLayout.setVgap(10);
			borderLayout.setHgap(0);
			selectJpsLabel = new JLabel();
			selectJpsLabel.setText("JavaProcessDialog.selectJpsLabel");
			jpsContentPane = new JPanel();
			jpsContentPane.setLayout(borderLayout);
			jpsContentPane.add(selectJpsLabel, java.awt.BorderLayout.NORTH);
			jpsContentPane.add(getJpsScrollPane(), BorderLayout.CENTER);
			
			// ESCで閉じる
			GuiUtils.closeByESC(this, jpsContentPane);
		}
		return jpsContentPane;
	}

}  //  @jve:decl-index=0:visual-constraint="10,10"
