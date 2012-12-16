package net.mikaboshi.jdbc.monitor.viewer;

import java.awt.FileDialog;
import java.awt.Frame;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;

import javax.swing.JOptionPane;

import net.mikaboshi.jdbc.monitor.LogEntry;
import net.mikaboshi.jdbc.monitor.M17N;

import org.apache.commons.io.IOUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * ログファイルの読み書き時のファイルダイアログに関する処理を行う。
 *
 * @author Takuma Umezawa
 *
 */
public class LogFileDialogHelper {

	private static Log systemLogger = LogFactory.getLog(LogFileDialogHelper.class);

	private LogFileDialogHelper() {}

	/**
	 * ファイルダイアログを表示し、現在のテーブルの内容をファイルに保存する。
	 * @param parent 親フレーム
	 * @param logTableModel 書き出す内容が格納されたLogTableModel
	 * @param encoding 書き出すファイルのエンコーディング
	 */
	public static void saveLogFile(
			Frame parent,
			LogTableModel logTableModel,
			String encoding) {

		FileDialog fileDialog = new FileDialog(
				parent,
				M17N.get("JdbcLogViewerFrame.file_dialog.export.title"),
				FileDialog.SAVE);

		fileDialog.setVisible(true);

		String fileName = fileDialog.getFile();
		String dir = fileDialog.getDirectory();
		fileDialog.dispose();

		if (fileName == null) {
			return;
		}

		File file = new File(dir + fileName);

		OutputStream out = null;
		PrintWriter writer = null;

		try {
			out = new FileOutputStream(file);
			writer = new PrintWriter(
							new OutputStreamWriter(out, encoding));

			for (LogEntry entry : logTableModel.getVisibleLogList()) {
				writer.println(entry.toLogString());
			}

			JOptionPane.showMessageDialog(
					null,
					M17N.get("JdbcLogViewerFrame.file_dialog.export.complete"),
					"",
					JOptionPane.INFORMATION_MESSAGE);

		} catch (IOException e) {
			systemLogger.error("Exporting log file failed.", e);

			JOptionPane.showMessageDialog(null,
					M17N.get("JdbcLogViewerFrame.file_dialog.export.failure", e.getMessage()),
					"",
					JOptionPane.ERROR_MESSAGE);
		} finally {
			IOUtils.closeQuietly(writer);
			IOUtils.closeQuietly(out);
		}
	}

	/**
	 * ファイルダイアログを開き、選択されたファイルを返す。
	 * 選択がキャンセルされた場合はnullを返す。
	 * @param parent
	 * @return
	 */
	public static File getSelectedLogFile(Frame parent) {
		FileDialog fileDialog = new FileDialog(
				parent,
				M17N.get("JdbcLogViewerFrame.file_dialog.open.title"),
				FileDialog.LOAD);

		fileDialog.setVisible(true);

		String file = fileDialog.getFile();
		String dir = fileDialog.getDirectory();
		fileDialog.dispose();

		if (file == null) {
			return null;
		}

		return new File(dir + file);
	}
}
