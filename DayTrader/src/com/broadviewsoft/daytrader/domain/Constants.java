package com.broadviewsoft.daytrader.domain;

import java.text.DateFormat;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;

public class Constants {
  public final static boolean PROD_MODE = false;
  
  public final static String FILENAME_CONNECTOR = "_";
  public final static String STOCK_DATA_FILE_EXTENSION = ".csv";
  
	public final static long MINUTE_IN_MILLI_SECONDS = 60*1000;
	
	public final static int INIT_CASH_AMOUNT = 12000;
//	public final static String[] INIT_STOCK_SYMBOLS = {"UVXY"};
	public final static String[] INIT_STOCK_SYMBOLS = {};
//	public final static int[] INIT_STOCK_VOLUMES = {500};
	public final static int[] INIT_STOCK_VOLUMES = {0};
//	public final static double[] INIT_STOCK_PRICES = {10};
	public final static double[] INIT_STOCK_PRICES = {};
	
	public final static double LOCKWIN_PRE_CLOSE_FACTOR = 1.05;
	public final static double LOCKWIN_CUR_OPEN_FACTOR = 1.02;
	public final static double STOPLOSS_CUR_OPEN_FACTOR = 0.98;
	
	public final static String STOCK_PRICE_TIMESTAMP_PATTERN = "M/d/yyyy h:mm:ss a";
	
//	public final static String MARKET_START_TIME = "09:30 AM";
//	public final static String MARKET_END_TIME = "04:00 PM";
	
	public final static long MARKET_OPEN_TIME = (long)(9.5*60*60*1000);
	public final static long MARKET_CLOSE_TIME = 16*60*60*1000;
	
	public final static String MARKET_HOURS_PATTERN = "HH:mm";
	public final static DateFormat MARKET_HOURS_FORMATTER = new SimpleDateFormat(MARKET_HOURS_PATTERN);

	public final static String TRADE_DATE_PATTERN = "MM/dd/yyyy";
	public final static DateFormat TRADE_DATE_FORMATTER = new SimpleDateFormat(TRADE_DATE_PATTERN);
	
	public final static String TX_DATE_PATTERN = "M/d/yy HH:mm";
	public final static DateFormat TX_DATE_FORMATTER = new SimpleDateFormat(TX_DATE_PATTERN);

	public final static String STOCK_PRICE_PATTERN = "#,##0.00";
	public final static NumberFormat STOCK_PRICE_FORMATTER = new DecimalFormat(STOCK_PRICE_PATTERN);

//	public final static String STOCK_VOLUME_PATTERN = "#,##0";
	public final static String STOCK_VOLUME_PATTERN = "0";
	public final static NumberFormat STOCK_VOLUME_FORMATTER = new DecimalFormat(STOCK_VOLUME_PATTERN);
	
	public final static String HISTORY_DATA_PATH = "D:/projects/DayTrader/design/";
}
