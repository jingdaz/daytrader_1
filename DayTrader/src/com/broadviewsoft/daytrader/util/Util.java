package com.broadviewsoft.daytrader.util;

import java.util.Date;
import java.util.List;
import java.util.TimeZone;

import com.broadviewsoft.daytrader.domain.Constants;
import com.broadviewsoft.daytrader.domain.StockItem;

public class Util {
	public static String format(double price) {
		return Constants.STOCK_PRICE_FORMATTER.format(price);
	}

	public static String format(long price) {
		return Constants.STOCK_VOLUME_FORMATTER.format(price);
	}

	public static String format(Date date) {
		return Constants.TX_DATE_FORMATTER.format(date);
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
	
	public static Date convertDST(Date date) {
		// rollback one hour in case of Day-Saving Time
	    if (TimeZone.getDefault().inDaylightTime(date)) {
	    	date = new Date(date.getTime() - Constants.HOUR_IN_MILLI_SECONDS);
	    }
	    return date;
	}
	
}
