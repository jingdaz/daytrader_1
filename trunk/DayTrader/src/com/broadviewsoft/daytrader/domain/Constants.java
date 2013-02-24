package com.broadviewsoft.daytrader.domain;

import java.text.DateFormat;
import java.text.SimpleDateFormat;

public class Constants {
	public final static int INIT_CASH_AMOUNT = 12000;
	public final static String[] INIT_STOCK_SYMBOLS = {"UVXY"};
	public final static int[] INIT_STOCK_VOLUMES = {500};
	public final static double[] INIT_STOCK_PRICES = {10};
	
	public final static double LOCKWIN_PRE_CLOSE_FACTOR = 1.05;
	public final static double LOCKWIN_CUR_OPEN_FACTOR = 1.02;
	public final static double STOPLOSS_CUR_OPEN_FACTOR = 0.98;
	
//	public final static String MARKET_START_TIME = "09:30 AM";
//	public final static String MARKET_END_TIME = "04:00 PM";
	
	public final static long MARKET_OPEN_TIME = (long)9.5*60*60*1000;
	public final static long MARKET_CLOSE_TIME = (long)16*60*60*1000;
	
	public final static String MARKET_HOURS_PATTERN = "hh:mm a";
	public final static DateFormat MARKET_HOURS_FORMATTER = new SimpleDateFormat(MARKET_HOURS_PATTERN);

	public final static String TRADE_START_DATE_PATTERN = "MM/dd/YYYY";
	public final static DateFormat TRADE_DATE_FORMATTER = new SimpleDateFormat(TRADE_START_DATE_PATTERN);
	
}
