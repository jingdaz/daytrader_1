package com.broadviewsoft.daytrader.service;

import java.util.List;

import com.broadviewsoft.daytrader.domain.DataException;
import com.broadviewsoft.daytrader.domain.Period;
import com.broadviewsoft.daytrader.domain.StockItem;

public interface IHistoryDataService {
	public List<StockItem> loadData(String symbol, Period period)
			throws DataException;
}
