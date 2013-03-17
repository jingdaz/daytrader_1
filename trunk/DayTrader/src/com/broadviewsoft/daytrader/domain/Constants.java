package com.broadviewsoft.daytrader.domain;

import java.text.DateFormat;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class Constants {
	private static Log logger = LogFactory.getLog(Constants.class);

	public final static long DEFAULT_ACCOUNT_NUMBER = 93345;
	public final static double COMMISSION_FEE = 9.95;

	public final static boolean PROD_MODE = false;

	public final static String FILENAME_CONNECTOR = "_";
	public final static String CSV_SEPARATOR = ",";
	public final static String STOCK_DATA_FILE_EXTENSION = ".csv";
	public final static String STOCK_DATA_FILE_GF_PREFIX = "GF_";
	public final static String STOCK_DATA_FILE_BVS_PREFIX = "";

	public final static int MINUTE_IN_SECONDS = 60;
	public final static long MINUTE_IN_MILLI_SECONDS = 60 * 1000;
	public final static long HOUR_IN_MILLI_SECONDS = 60 * 60 * 1000;
	public final static long DAY_IN_MILLI_SECONDS = 24 * 60 * 60 * 1000;

	// 9:30 AM
	public final static long MARKET_OPEN_TIME = 34200000;
	// 4:00 PM
	public final static long MARKET_CLOSE_TIME = 57600000;

	public final static int STATUS_QUEUE_DEPTH = 4;
	public final static int STATUS_INTERVAL = 36;
	public final static int DEFAULT_QUANTITY = 1000;

	public final static double CCI_TOP_DIVERGENCE = 100;
	public final static double CCI_BOTTOM_DIVERGENCE = -100;

	public final static double CCI_WEAKEST_LIMIT = 25;
	public final static double CCI_STRONGEST_LIMIT = -25;

	/* Alert */
	public static final double STOCK_PRICE_UP_ALERT = 0.02;
	public static final double STOCK_PRICE_DOWN_ALERT = -0.02;

	/* CCI Constant */
	public static final double CCI_FACTOR = 0.015;
	public static final int CCI_INTERVAL = 20;

	public final static String[] MARKET_HOLIDAYS = { "01/01/2013", "02/18/2013" };

	public final static String[] STOCKS_WITH_DATA = {"UVXY"};
	public final static int INIT_CASH_AMOUNT = 10000;
	public final static String[] INIT_STOCK_SYMBOLS = {"UVXY"};
	public final static int[] INIT_STOCK_VOLUMES = { 1000 };

	public final static double PRICE_HIGHER_FACTOR = 1.01;
	public final static double PRICE_LOWER_FACTOR = 0.99;

	public final static double LOCKWIN_PRE_CLOSE_FACTOR = 1.05;
	public final static double LOCKWIN_CUR_OPEN_FACTOR = 1.02;
	public final static double STOPLOSS_CUR_OPEN_FACTOR = 0.98;

	public final static String STOCK_PRICE_TIMESTAMP_PATTERN = "M/d/yyyy H:mm";
	public final static DateFormat STOCK_PRICE_TIMESTAMP_FORMATTER = new SimpleDateFormat(
			STOCK_PRICE_TIMESTAMP_PATTERN);
	
	// public final static String MARKET_START_TIME = "09:30 AM";
	// public final static String MARKET_END_TIME = "04:00 PM";

	public static final String UNIX_TIMESTAMP_PREFIX = "a";

	public final static String MARKET_HOURS_PATTERN = "HH:mm";
	public final static DateFormat MARKET_HOURS_FORMATTER = new SimpleDateFormat(
			MARKET_HOURS_PATTERN);

	public final static String TRADE_DATE_PATTERN = "MM/dd/yyyy";
	public final static DateFormat TRADE_DATE_FORMATTER = new SimpleDateFormat(
			TRADE_DATE_PATTERN);

	public final static String TX_DATE_PATTERN = "M/d/yy HH:mm";
	public final static DateFormat TX_DATE_FORMATTER = new SimpleDateFormat(
			TX_DATE_PATTERN);

	public final static String STOCK_PRICE_PATTERN = "###0.00";
	public final static NumberFormat STOCK_PRICE_FORMATTER = new DecimalFormat(
			STOCK_PRICE_PATTERN);

	// public final static String STOCK_VOLUME_PATTERN = "#,##0";
	public final static String STOCK_VOLUME_PATTERN = "0";
	public final static NumberFormat STOCK_VOLUME_FORMATTER = new DecimalFormat(
			STOCK_VOLUME_PATTERN);

	public final static String HISTORY_DATA_FILE_PATH = "D:/projects/DayTrader/resources/data/";
	// sample link -
	// http://www.google.com/finance/getprices?p=1d&f=d,o,h,l,c,v&df=cpct&i=60&q=UVXY
	public final static String HISTORY_DATA_GOOGLE_SITE = "http://www.google.com/finance/getprices?p=2d&f=d,o,h,l,c,v&df=cpct";

	public static List<Date> MARKET_CLOSE_DAYS = new ArrayList<Date>();

	static {
		for (int i = 0; i < MARKET_HOLIDAYS.length; i++) {
			try {
				MARKET_CLOSE_DAYS.add(Constants.TRADE_DATE_FORMATTER
						.parse(MARKET_HOLIDAYS[i]));
			} catch (ParseException e) {
				logger.error("Error when parsing market holidays.");
			}
		}
	}
}
