package com.broadviewsoft.daytrader.util;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.broadviewsoft.daytrader.domain.Constants;
import com.broadviewsoft.daytrader.domain.DataException;

public class FileHeaderUpdater {
	private static Log logger = LogFactory.getLog(FileHeaderUpdater.class);
	private static final String FILE_NAME_PREFIX_PATTERN = "_\\d{4}_\\w{3,5}.TXT$";
	private static final String RAW_FILE_HEADER = "Date,Open,High,Low,Close,Wilder's RSI 14,Commodity Channel Index (CCI) 14";
	private static final String EOL = "\n";
	private static final int FILE_CAPACITY = 100000000;

	public static void updateHeader(String path, String symbol) {
		StringBuilder sb = null;
		File dir = new File(path);
		System.out.println(symbol+FILE_NAME_PREFIX_PATTERN);
		File[] files = dir.listFiles(f -> f.getName().toUpperCase().matches(symbol+FILE_NAME_PREFIX_PATTERN));
		for (File f : files) {
			try {
				List<String> lines = Files.readAllLines(f.toPath());
				System.out.println("current pathr: " + f.getPath());
				String s = lines.get(0);
				if (RAW_FILE_HEADER.equalsIgnoreCase(s)) {
					lines.set(0, Constants.NEW_FILE_HEADER);
				}
				sb = new StringBuilder(FILE_CAPACITY);
				for (String line : lines) {
					sb.append(line);
					sb.append(EOL);
				}
				Files.write(Paths.get(f.getPath().replace("txt", "csv")), sb.toString().getBytes());
				sb = null;
			} catch (IOException e) {
				e.printStackTrace();
			} 
		}
    }
 
	public static void main(String[] args) throws DataException {
//		updateHeader("C:/workspaces/DayTrader/resources/rawdata/QQQ/2020", "QQQ");
//		updateHeader("C:/workspaces/DayTrader/resources/rawdata/SPY/2020", "SPY");
		updateHeader("C:/workspaces/DayTrader/resources/rawdata/UVXY/2020", "UVXY");
		updateHeader("C:/workspaces/DayTrader/resources/rawdata/SHOP/2020", "SHOP");
		updateHeader("C:/workspaces/DayTrader/resources/rawdata/ROKU/2020", "ROKU");
		updateHeader("C:/workspaces/DayTrader/resources/rawdata/TSLA/2020", "TSLA");
		updateHeader("C:/workspaces/DayTrader/resources/rawdata/UNG/2020", "UNG");
		System.out.println("finishing header updates.");
		
	}

}
