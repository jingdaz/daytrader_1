package com.broadviewsoft.daytrader.service;

import java.util.Date;
import java.util.List;

import com.broadviewsoft.daytrader.domain.Period;
import com.broadviewsoft.daytrader.domain.PriceType;
import com.broadviewsoft.daytrader.domain.StockItem;

public interface IDataFeeder {
	public StockItem getYesdayItem(String symbol, int index);

	public List<StockItem> getHistoryData(String symbol, Period period, Date cutTime);

	public StockItem getItemByIndex(String symbol, Period period, int index);

	public double getPriceByIndex(String symbol, Period period, int index, PriceType type);

	// FIXME StockItem timestamp cannot be null and mins with size > 1
	public double getPrice(String symbol, Date timestamp, Period period, PriceType type);

	public int getCurItemIndex(String symbol, Date curTime, Period period);
}
