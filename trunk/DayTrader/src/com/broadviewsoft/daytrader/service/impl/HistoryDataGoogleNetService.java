package com.broadviewsoft.daytrader.service.impl;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
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

public class HistoryDataGoogleNetService extends AbstractHistoryDataService {
	private static Log logger = LogFactory
			.getLog(HistoryDataGoogleNetService.class);

	@Override
	public BufferedReader getReader(String symbol, Period period)
			throws DataException {
		StringBuilder loc = new StringBuilder();
		loc.append(Constants.HISTORY_DATA_GOOGLE_SITE);
		// https://www.google.com/finance/getprices?i=60&p=1d&f=d,o,h,l,c,v&df=cpct&q
		// =UVXY
		int interval = period.minutes() * Constants.MINUTE_IN_SECONDS;
		loc.append("&i=" + interval);
		loc.append("&q=" + symbol);
		logger.info("Requesting Google Finance site " + loc);

		URL googleFinance = null;
		BufferedReader in = null;
		URLConnection gfc = null;

		try {
			googleFinance = new URL(loc.toString());
			gfc = googleFinance.openConnection();
			in = new BufferedReader(new InputStreamReader(gfc.getInputStream()));
		} catch (MalformedURLException e) {
			logger.error("URL error: " + loc);
			throw new DataException();
		} catch (IOException e) {
			logger.error("Error occurred when reading from Google Finance.");
			throw new DataException();
		}

		return in;
	}

	public static void main(String[] args) throws DataException {
		// String line = "a1361543400,10.1,10.14, 10.1, 10.14,6483";
		// String line2 = "1,10.09,10.1,10.07,10.1,11039";
		// StockItem si = parseInput(line);
		// StockItem si2 = parseInput(line2);
		HistoryDataGoogleNetService gfService = new HistoryDataGoogleNetService();
		CsvDataFileService fileService = new CsvDataFileService();
		List<StockItem> result = null;
		String[] symbols = { "UVXY", "NUGT" };
		for (String symbol : symbols) {
			// Period[] ps = {Period.DAY, Period.WEEK};
			Period[] ps = Period.values();
			for (Period p : ps) {
				logger.info("\r\nworking on " + p.name());
				gfService.loadData(symbol, p, DataFileType.GF);
				result = fileService.loadData(symbol, p, DataFileType.GF);
				RSIService.calculateRsi(Constants.RSI_INTERVAL, result);
				CCIService.calculateCci(Constants.CCI_INTERVAL, result);
				gfService.appendToFile(symbol, p, result, DataFileType.BVS);
			}
		}
	}

}
