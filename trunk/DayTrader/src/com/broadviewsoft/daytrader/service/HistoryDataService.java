package com.broadviewsoft.daytrader.service;

import java.util.List;

import com.broadviewsoft.daytrader.domain.StockItem;

public interface HistoryDataService {
	public List<StockItem> loadData();
}
