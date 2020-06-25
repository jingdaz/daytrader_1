package com.broadviewsoft.daytrader.service;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.broadviewsoft.daytrader.domain.Constants;
import com.broadviewsoft.daytrader.domain.DataException;
import com.broadviewsoft.daytrader.domain.DataFileType;
import com.broadviewsoft.daytrader.domain.Period;
import com.broadviewsoft.daytrader.domain.Stock;
import com.broadviewsoft.daytrader.domain.StockData;
import com.broadviewsoft.daytrader.service.impl.CsvDataFileService;

public class MockDataFeeder extends AbstractDataFeeder {
	private static Log logger = LogFactory.getLog(MockDataFeeder.class);

	private IHistoryDataService service = new CsvDataFileService();

	public MockDataFeeder() {
		this(false);
	}

	/**
	 * init method load stock data based on DataFileType
	 * 
	 * @param prodMode
	 */
	public MockDataFeeder(boolean prodMode) {
		this.prodMode = prodMode;
		if (!initialized) {
			init(Constants.STOCKS_WITH_DATA, DataFileType.Generic);
			initialized = true;
		}
	}

	public void init(String[] symbols, DataFileType type) {
		try {
			for (String symbol : symbols) {
				StockData sd = new StockData();
				sd.setStock(new Stock(symbol));
				sd.setMins(service.loadData(symbol, Period.MIN01, type));
				sd.setMin5s(service.loadData(symbol, Period.MIN05, type));
				sd.setMin15s(service.loadData(symbol, Period.MIN15, type));
				sd.setHours(service.loadData(symbol, Period.HOUR, type));
				sd.setDays(service.loadData(symbol, Period.DAY, type));
				sd.setWeeks(service.loadData(symbol, Period.WEEK, type));
				allData.add(sd);
				logger.debug("Finished loading historical data for Stock: " + symbol);
			}
		} catch (DataException e) {
			logger.error("Error when loading historical data.", e);
		}

	}

}
