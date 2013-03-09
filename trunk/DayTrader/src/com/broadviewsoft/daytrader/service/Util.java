package com.broadviewsoft.daytrader.service;

import java.util.Date;

import com.broadviewsoft.daytrader.domain.Constants;

public class Util
{
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
		return ((int)(100*price))/100.0;
	}

}
 
