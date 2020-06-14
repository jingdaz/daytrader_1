package com.broadviewsoft.daytrader.service.impl;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.broadviewsoft.daytrader.domain.Constants;
import com.broadviewsoft.daytrader.domain.DataException;
import com.broadviewsoft.daytrader.domain.DataFileType;
import com.broadviewsoft.daytrader.domain.Period;
import com.broadviewsoft.daytrader.domain.StockItem;
import com.broadviewsoft.daytrader.service.AbstractHistoryDataService;
import com.broadviewsoft.daytrader.service.CCIService;
import com.broadviewsoft.daytrader.service.RSIService;

public class HistoryDataGoogleFileService extends AbstractHistoryDataService { 
private static Log logger = LogFactory.getLog(HistoryDataGoogleFileService.class);
private static final String path = "D:/projects/DayTrader/resources/data/";

  @Override
  public BufferedReader getReader(String symbol, Period period) throws DataException
  {

    try {
      return new BufferedReader(new FileReader(path + "GF_" + symbol + "_" + period.name() + ".txt"));
    } catch (IOException e)
    {
      logger.error("Error occurred when reading from Google Finance data file.");
      throw new DataException();
    }
  }

	public static void main(String[] args) throws DataException {
		HistoryDataGoogleFileService gfService = new HistoryDataGoogleFileService();
		CsvDataFileService fileService = new CsvDataFileService();
		List<StockItem> result = null;
		String symbol = "UVXY";
		Period[] ps = {Period.MIN05};
		
		for (Period p : ps) {
			logger.info("\r\nworking on " + p.name());
			gfService.loadData(symbol, p, DataFileType.GF);
			result = fileService.loadData(symbol, p, DataFileType.GF);
			RSIService.calculateRsi(Constants.RSI_INTERVAL, result);
      CCIService.calculateCci(Constants.CCI_INTERVAL, result);
      gfService.appendToFile(symbol, p, result, DataFileType.BVS);
		}
		logger.info("Done.");
	}

}
