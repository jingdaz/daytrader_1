package com.broadviewsoft.daytrader.util;

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

import com.broadviewsoft.daytrader.domain.DataException;
import com.broadviewsoft.daytrader.domain.DataFileType;
import com.broadviewsoft.daytrader.domain.StockItem;

public class Merger123 {
	private static Log logger = LogFactory.getLog(Merger123.class);

	public static List<StockItem> mergeMin23(String fn2, String fn3) throws DataException {
		List<StockItem> itemList2 = readFile(fn2);
		List<StockItem> itemList3 = readFile(fn3);
		
		List<StockItem> result = Util.mergeStockItemListMin23(itemList2, itemList3);
		return result;
	}
	
	/**
	 * prefer to using only min02 file to split into min01
	 * @param fn2
	 * @return
	 * @throws DataException
	 */
	public static List<StockItem> splitMin2(String fn2) throws DataException {
		List<StockItem> itemList2 = readFile(fn2);
		List<StockItem> result = Util.splitStockItemListMin2(itemList2);
		return result;
	}
	
	public static List<StockItem> readFile(String fn) throws DataException {
		List<StockItem> result = new ArrayList<StockItem>();

		ICsvBeanReader beanReader = null;
		StockItem item = null;
		try {
			beanReader = new CsvBeanReader(new FileReader(fn),
					CsvPreference.STANDARD_PREFERENCE);

			// the header elements are used to map the values to the bean (names
			// must match)
			final String[] header = beanReader.getHeader(true);
			final CellProcessor[] processors = Util.getProcessors(DataFileType.Generic);

			while ((item = beanReader.read(StockItem.class, header, processors)) != null) {
				result.add(item);
			}

		} catch (FileNotFoundException e) {
			logger.error(fn, e);
			throw new DataException();
		} catch (IOException e) {
			logger.error(item, e);
			throw new DataException();
		} finally {
			if (beanReader != null) {
				try {
					beanReader.close();
				} catch (IOException e) {
					logger.error("Error when closing file.");
				}
			}
		}
		
		return result;
	}
	
	public static void main(String[] args) throws DataException {
//		String fn2 = "C:/workspaces/DayTrader/resources/rawdata/SHOP/2020/archive/SH_20200501_min02.csv";
		String fn2 = "C:/workspaces/daytrader/DayTrader/resources/rawdata/SPY/2020/SPY_0617_min02.csv";

		
//		String fn2 = "C:/workspaces/DayTrader/resources/rawdata/UNG/2020/UN0501_min02.csv";
//		String fn2 = "C:/workspaces/DayTrader/resources/rawdata/UNG/2020/UN0417_min02.csv";

		
//		String fn3 = "C:/workspaces/DayTrader/resources/rawdata/QQQ/2020/QQ0131_min03.csv";

//		List<StockItem> result = mergeMin23(fn2, fn3);
		List<StockItem> result = splitMin2(fn2);	
		for (StockItem si : result) {
			System.out.println(si.toString(DataFileType.BVS));
			
		}
		
	}

}
