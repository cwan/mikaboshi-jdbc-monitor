package net.mikaboshi.jdbc.monitor;

import java.io.IOException;
import java.nio.charset.Charset;

import net.mikaboshi.jdbc.monitor.IllegalLogException;
import net.mikaboshi.jdbc.monitor.LogEntry;
import net.mikaboshi.jdbc.monitor.LogFileAccessor;

public class LogFileAccessorTestx {

	/**
	 * @param args
	 * @throws IOException 
	 * @throws IllegalLogException 
	 */
	public static void main(String[] args) throws IOException, IllegalLogException {
		
		LogFileAccessor logAccessor =
			new LogFileAccessor("jdbc.log", Charset.defaultCharset().name());
		
		ViewerConfig.getInstance().setLoadConnectInfoFromLog(true);
		
		for (int i = 0; i < 600; i++) {
			LogEntry entry = logAccessor.readNextLog();
			if (entry != null) {
				System.out.println(entry.toLogString());
				logAccessor.unlock();
			}
			
			try {
				Thread.sleep(10);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
		
		System.out.println("driver = " + ViewerConfig.getInstance().getConnectInfo().getDriver());
		System.out.println("url = " + ViewerConfig.getInstance().getConnectInfo().getUrl());
		System.out.println("user = " + ViewerConfig.getInstance().getConnectInfo().getUser());
		System.out.println("password = " + ViewerConfig.getInstance().getConnectInfo().getPassword());

	}

}
