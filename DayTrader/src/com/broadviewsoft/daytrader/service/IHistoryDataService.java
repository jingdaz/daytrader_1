package com.broadviewsoft.daytrader.service;

import java.util.List;

import com.broadviewsoft.daytrader.domain.DataException;
import com.broadviewsoft.daytrader.domain.DataFileType;
import com.broadviewsoft.daytrader.domain.Period;
import com.broadviewsoft.daytrader.domain.StockItem;

/**
 * Interface to load stock data 
 * 
 * @author Jason
 *
 */
public interface IHistoryDataService {
	public List<StockItem> loadData(String symbol, Period period, DataFileType type)
			throws DataException;
	
//	public List<StockItem> loadData(String csvFilename,
//			DataFileType type) throws DataException;
}
