package com.broadviewsoft.daytrader.util;

import java.util.Date;

import com.broadviewsoft.daytrader.domain.Constants;

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
}
