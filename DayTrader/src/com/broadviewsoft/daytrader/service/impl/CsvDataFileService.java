package com.broadviewsoft.daytrader.service.impl;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.supercsv.cellprocessor.ift.CellProcessor;
import org.supercsv.io.CsvBeanReader;
import org.supercsv.io.ICsvBeanReader;
import org.supercsv.prefs.CsvPreference;

import com.broadviewsoft.daytrader.domain.CurrencyType;
import com.broadviewsoft.daytrader.domain.DataException;
import com.broadviewsoft.daytrader.domain.DataFileType;
import com.broadviewsoft.daytrader.domain.Period;
import com.broadviewsoft.daytrader.domain.StockItem;
import com.broadviewsoft.daytrader.service.IHistoryDataService;
import com.broadviewsoft.daytrader.util.Util;

public class CsvDataFileService implements IHistoryDataService {
	private static Log logger = LogFactory.getLog(CsvDataFileService.class);

	public List<StockItem> loadData(String symbol, Period period,
			DataFileType type) throws DataException {
		List<StockItem> result = new ArrayList<StockItem>();
		String csvFilename = Util.getDataPath(symbol, period, type);

		ICsvBeanReader beanReader = null;
		StockItem item = null;
		try {
			beanReader = new CsvBeanReader(new FileReader(csvFilename),
					CsvPreference.STANDARD_PREFERENCE);

			// the header elements are used to map the values to the bean (names
			// must match)
			final String[] header = beanReader.getHeader(true);
			final CellProcessor[] processors = Util.getProcessors(type);

			while ((item = beanReader.read(StockItem.class, header, processors)) != null) {
				result.add(item);
			}

		} catch (FileNotFoundException e) {
			logger.error(csvFilename, e);
			throw new DataException();
		} catch (IOException e) {
			logger.error(item, e);
			throw new DataException();
		} finally {
			if (beanReader != null) {
				try {
					beanReader.close();
				} catch (IOException e) {
					logger.error("Error when closing csv file.");
				}
			}
		}
		return result;
	}
	
	public List<StockItem> loadData(String csvFilename,
			DataFileType type) throws DataException {
		List<StockItem> result = new ArrayList<StockItem>();

		ICsvBeanReader beanReader = null;
		StockItem item = null;
		try {
			beanReader = new CsvBeanReader(new FileReader(csvFilename),
					CsvPreference.STANDARD_PREFERENCE);

			// the header elements are used to map the values to the bean (names
			// must match)
			final String[] header = beanReader.getHeader(true);
			final CellProcessor[] processors = Util.getProcessors(type);

			while ((item = beanReader.read(StockItem.class, header, processors)) != null) {
				result.add(item);
			}

		} catch (FileNotFoundException e) {
			logger.error(csvFilename, e);
			throw new DataException();
		} catch (IOException e) {
			logger.error(item, e);
			throw new DataException();
		} finally {
			if (beanReader != null) {
				try {
					beanReader.close();
				} catch (IOException e) {
					logger.error("Error when closing csv file.");
				}
			}
		}
		return result;
	}

	public static void main(String[] args) throws DataException {
		CsvDataFileService service = new CsvDataFileService();
		// print out stock headers
		CurrencyType curType = CurrencyType.USD;
		String symbol = "UVXY";
		// Period period = Period.MIN5;
		// Period period = Period.DAY;
		Period period = Period.MIN01;
		System.out.println(StockItem.printHeaders(curType, symbol, period));

		// print out stock data
		List<StockItem> result = service.loadData(symbol, period,
				DataFileType.BVS);
		for (StockItem si : result) {
			System.out.println(si);
		}
	}
}
