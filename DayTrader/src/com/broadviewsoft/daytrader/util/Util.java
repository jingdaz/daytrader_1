package com.broadviewsoft.daytrader.util;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.TimeZone;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.broadviewsoft.daytrader.domain.Constants;
import com.broadviewsoft.daytrader.domain.DataFileType;
import com.broadviewsoft.daytrader.domain.Period;

public class Util {
	private static Log logger = LogFactory.getLog(Util.class);

	public static String format(double price) {
		return Constants.STOCK_PRICE_FORMATTER.format(price);
	}

	public static String format(long price) {
		return Constants.STOCK_VOLUME_FORMATTER.format(price);
	}

	public static String format(Date date) {
		return Constants.STOCK_PRICE_TIMESTAMP_FORMATTER.format(date);
	}

	public static double trim(double price) {
		return ((int) (100 * price)) / 100.0;
	}

	public static int compare(double price1, double price2) {
		if (price1 > Constants.PRICE_HIGHER_FACTOR * price2) {
			return 1;
		}
		if (price1 < Constants.PRICE_LOWER_FACTOR * price2) {
			return -1;
		}
		return 0;
	}

	public static Date forwardOneHour(Date date) {
		date = new Date(date.getTime() - Constants.HOUR_IN_MILLI_SECONDS);
		return date;
	}

	public static Date backwardOneHour(Date date) {
		date = new Date(date.getTime() + Constants.HOUR_IN_MILLI_SECONDS);
		return date;
	}

	public static Date convertUnixTimestamp(String date) {
		// convert to milli-seconds
		// since Unix timestamp based on seonds
		// from "the epoch" (Jan 1, 1970, 00:00:00 GMT)
		return new Date(1000 * Long.valueOf(date));
	}

	public static String getLastLine(String filename) {
		BufferedReader in = null;
		String lastLine = null;
		String tmp = null;
		try {
			in = new BufferedReader(new FileReader(filename));
			while ((tmp = in.readLine()) != null) {
				lastLine = tmp;
			}
			System.out.println(lastLine);
			in.close();
		} catch (FileNotFoundException e) {
			logger.error("File not found: " + filename);
		} catch (IOException e) {
			logger.error("Error occurred when reading from File: " + filename);
		} finally {
			if (in != null) {
				try {
					in.close();
				} catch (IOException e) {
				}
			}
		}
		return lastLine;
	}

	public static String getDataPath(String symbol, Period period,
			DataFileType type) {
		switch (type) {
		case GF:
			return getStockDataPath(Constants.STOCK_DATA_FILE_GF_PREFIX,
					symbol, period);
		default:
			return getStockDataPath(Constants.STOCK_DATA_FILE_BVS_PREFIX,
					symbol, period);
		}
	}

	public static String getStockDataPath(String prefix, String symbol,
			Period period) {
		StringBuilder sb = new StringBuilder();
		sb.append(Constants.HISTORY_DATA_FILE_PATH);
		sb.append(prefix);
		sb.append(symbol);
		sb.append(Constants.FILENAME_CONNECTOR);
		sb.append(period.name());
		sb.append(Constants.STOCK_DATA_FILE_EXTENSION);

		logger.debug("Generated file name: " + sb);
		return sb.toString();
	}

	public static int marketOpenMins(Date date) {
    Calendar cal = new GregorianCalendar();
    cal.setTime(date);
    int hour = cal.get(Calendar.HOUR_OF_DAY);
    int min = cal.get(Calendar.MINUTE);
    return (60 * hour + min);
	}
	
	public static void main(String[] args) {
		String filename = "D:/projects/DayTrader/resources/UVXY_MIN.csv";
		String line = getLastLine(filename);
		System.out.println("last line=" + line);
	}
}
