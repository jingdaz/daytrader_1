package com.broadviewsoft.daytrader.util;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Comparator;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.supercsv.cellprocessor.Optional;
import org.supercsv.cellprocessor.ParseDate;
import org.supercsv.cellprocessor.ParseDouble;
import org.supercsv.cellprocessor.constraint.LMinMax;
import org.supercsv.cellprocessor.constraint.NotNull;
import org.supercsv.cellprocessor.ift.CellProcessor;

import com.broadviewsoft.daytrader.domain.Constants;
import com.broadviewsoft.daytrader.domain.DataFileType;
import com.broadviewsoft.daytrader.domain.Period;
import com.broadviewsoft.daytrader.domain.StockItem;

public class Util {
	private static Log logger = LogFactory.getLog(Util.class);

	public static String format(double price) {
		return Constants.STOCK_PRICE_FORMATTER.format(price);
	}

	public static String format(long volume) {
		return Constants.STOCK_VOLUME_FORMATTER.format(volume);
	}

	public static String format(Date date) {
		return Constants.STOCK_PRICE_TIMESTAMP_FORMATTER.format(date);
	}

	public static double trim(double price) {
		return ((int) (100 * price)) / 100.0;
	}

	public static int compare(double price1, double price2) {
		if (price1 > Constants.PRICE_HIGHER_FACTOR * price2) {
			return 1;
		}
		if (price1 < Constants.PRICE_LOWER_FACTOR * price2) {
			return -1;
		}
		return 0;
	}

	public static Date forwardOneHour(Date date) {
		date = new Date(date.getTime() - Constants.HOUR_IN_MILLI_SECONDS);
		return date;
	}

	public static Date backwardOneHour(Date date) {
		date = new Date(date.getTime() + Constants.HOUR_IN_MILLI_SECONDS);
		return date;
	}

	public static Date convertUnixTimestamp(String date) {
		// convert to milli-seconds
		// since Unix timestamp based on seonds
		// from "the epoch" (Jan 1, 1970, 00:00:00 GMT)
		return new Date(1000 * Long.valueOf(date));
	}

	public static String getLastLine(String filename) {
		BufferedReader in = null;
		String lastLine = null;
		String tmp = null;
		try {
			in = new BufferedReader(new FileReader(filename));
			while ((tmp = in.readLine()) != null) {
				lastLine = tmp;
			}
			System.out.println(lastLine);
			in.close();
		} catch (FileNotFoundException e) {
			logger.error("File not found: " + filename);
		} catch (IOException e) {
			logger.error("Error occurred when reading from File: " + filename);
		} finally {
			if (in != null) {
				try {
					in.close();
				} catch (IOException e) {
				}
			}
		}
		return lastLine;
	}

	public static String getDataPath(String symbol, Period period, DataFileType type) {
		switch (type) {
		case GF:
			return getStockDataPath(Constants.STOCK_DATA_FILE_GF_PREFIX, symbol, period);
		default:
			return getStockDataPath(Constants.STOCK_DATA_FILE_BVS_PREFIX, symbol, period);
		}
	}

	public static String getStockDataPath(String prefix, String symbol, Period period) {
		StringBuilder sb = new StringBuilder();
		sb.append(Constants.HISTORY_DATA_FILE_PATH);
		sb.append(prefix);
		sb.append(symbol);
		sb.append(Constants.FILENAME_CONNECTOR);
		sb.append(period.name());
		sb.append(Constants.STOCK_DATA_FILE_EXTENSION);

		logger.debug("Generated file name: " + sb);
		return sb.toString();
	}

	public static int getTimeInMins(Date date) {
		Calendar cal = new GregorianCalendar();
		cal.setTime(date);
		int hour = cal.get(Calendar.HOUR_OF_DAY);
		int min = cal.get(Calendar.MINUTE);
		return (60 * hour + min);
	}

	public static Date roundup(Date date) {
		Calendar cal = new GregorianCalendar();
		cal.setTime(date);
		int year = cal.get(Calendar.YEAR);
		int month = cal.get(Calendar.MONTH);
		int day = cal.get(Calendar.DAY_OF_MONTH);
		Calendar result = new GregorianCalendar(year, month, day);
		return result.getTime();
	}

	/**
	 * Combine two items into one to handle 9:30am redundant item
	 * 
	 * @param preItem
	 *            9:30am item
	 * @param item
	 *            9:31am or 9:35am item
	 * @return
	 */
	public static StockItem combine(StockItem preItem, StockItem item) {
		if (preItem == null || item == null) {
			return null;
		}

		StockItem result = new StockItem();
		result.setTimestamp(item.getTimestamp());
		result.setOpen(preItem.getOpen());
		result.setClose(item.getClose());
		result.setHigh(Math.max(preItem.getHigh(), item.getHigh()));
		result.setLow(Math.min(preItem.getLow(), item.getLow()));
		result.setVolume(preItem.getVolume() + item.getVolume());

		return result;
	}

	/**
	 * Find previous Low of CCI within 48 intervals
	 * 
	 * @param data
	 * @return
	 */
	public static StockItem findPreLow(List<StockItem> data) {
		int startIdx = Math.max(0, data.size() - Constants.STATUS_INTERVAL);
		List<StockItem> sample = data.subList(startIdx, data.size());
		java.util.Optional<StockItem> result = sample.stream().min(Comparator.comparing(StockItem::getCci));
		if (result.isPresent()) {
			return result.get();
		}
		
		return null;
	}
	
	
	/**
	 * Find previous Low of CCI within 48 intervals
	 * 
	 * @param data
	 * @return
	 */
	public static StockItem findPreHigh(List<StockItem> data) {
		int startIdx = Math.max(0, data.size() - Constants.STATUS_INTERVAL);
		List<StockItem> sample = data.subList(startIdx, data.size());
		java.util.Optional<StockItem> result = sample.stream().max(Comparator.comparing(StockItem::getCci));
		if (result.isPresent()) {
			return result.get();
		}
		
		return null;
	}

	/**
	 * Sets up the processors used for the examples. There are 10 CSV columns,
	 * so 7 processors are defined. Empty columns are read as null (hence the
	 * NotNull() for mandatory columns).
	 * 
	 * @return the cell processors
	 */
	public static CellProcessor[] getProcessors(DataFileType type) {
		switch (type) {
		case GF:
			final CellProcessor[] processors = new CellProcessor[] {
					new ParseDate(Constants.STOCK_PRICE_TIMESTAMP_PATTERN), // timestamp
					new NotNull(new ParseDouble()), // open
					new NotNull(new ParseDouble()), // high
					new NotNull(new ParseDouble()), // low
					new NotNull(new ParseDouble()), // close
					new LMinMax(0L, LMinMax.MAX_LONG) // volume
			};
			return processors;
			
		case BVS:
			final CellProcessor[] processors1 = new CellProcessor[] {
					new ParseDate(Constants.STOCK_PRICE_TIMESTAMP_PATTERN), // timestamp
					new NotNull(new ParseDouble()), // open
					new NotNull(new ParseDouble()), // high
					new NotNull(new ParseDouble()), // low
					new NotNull(new ParseDouble()), // close
					new LMinMax(0L, LMinMax.MAX_LONG) // volume
			};
			return processors1;
			
		default:
			final CellProcessor[] processors2 = new CellProcessor[] {
					new ParseDate(Constants.STOCK_PRICE_TIMESTAMP_PATTERN), // timestamp
					new NotNull(new ParseDouble()), // open
					new NotNull(new ParseDouble()), // high
					new NotNull(new ParseDouble()), // low
					new NotNull(new ParseDouble()), // close
					new Optional(new ParseDouble()), // rsi
					new Optional(new ParseDouble()), // cci
			};
			return processors2;
		}
	}

	public static List<StockItem> splitStockItemListMin2(List<StockItem> list2) {
		List<StockItem> result = new ArrayList<StockItem>();
		if (list2 == null || list2.isEmpty()) {
			return result;
		}
		int i = 0;

		while (i<195) {
			// handle last 2 minutes
			if (i==194) {
				i--;
			}
			
			// load two continuous entries
			StockItem item22 = list2.get(i++);
			StockItem item24 = list2.get(i++);
			
			// split into four entries
			StockItem item1 = new StockItem(item22);
			StockItem item2 = new StockItem(item22);
			StockItem item3 = new StockItem(item24);
			StockItem item4 = new StockItem(item24);

			// update timestamps
			item1.setTimestamp(new Date(item22.getTimestamp().getTime() - 60*1000));
			item3.setTimestamp(new Date(item24.getTimestamp().getTime() - 60*1000));
			
			// use average for middle close
			item1.setOpen(item22.getOpen());
			item1.setClose(0.5*item22.getOpen() + 0.5*item22.getClose());
			
			// propagate to second minute
			item2.setOpen(item1.getClose());
			item2.setClose(item24.getOpen());

			// set up high/low
			if ((item22.getHigh()-item22.getOpen()) > (item22.getHigh()-item22.getClose())) {
				item2.setHigh(item22.getHigh());
				item2.setLow(0.5*item1.getClose()+0.5*item22.getLow());
				
				item1.setLow(item22.getLow());
				item1.setHigh(0.5*item1.getClose()+0.5*item22.getHigh());
			}
			else {
				item1.setHigh(item22.getHigh());
				item1.setLow(0.5*item1.getClose()+0.5*item22.getLow());
				
				item2.setLow(item22.getLow());
				item2.setHigh(0.5*item1.getClose()+0.5*item22.getHigh());
			}

			// use average for third minute
			item3.setOpen(item22.getClose());
			item3.setClose(0.5*item24.getOpen() + 0.5*item24.getClose());
			
			// propagate to fourth minute
			item4.setOpen(item3.getClose());
			item4.setClose(item24.getClose());
		
			// set up high/low
			if ((item24.getHigh()-item24.getOpen()) > (item24.getHigh()-item24.getClose())) {
				item4.setHigh(item24.getHigh());
				item4.setLow(0.5*item3.getClose()+0.5*item24.getLow());
				
				item3.setLow(item24.getLow());
				item3.setHigh(0.5*item3.getClose()+0.5*item24.getHigh());
			}
			else {
				item3.setHigh(item24.getHigh());
				item3.setLow(0.5*item3.getClose()+0.5*item24.getLow());
				
				item4.setLow(item24.getLow());
				item4.setHigh(0.5*item3.getClose()+0.5*item24.getHigh());
			}
			
			result.add(item1);
			result.add(item2);
			result.add(item3);
			result.add(item4);
		}

		return result;
	}
	
	public static List<StockItem> mergeStockItemLists(List<List<StockItem>> itemLists) {
		List<StockItem> result = new ArrayList<StockItem>();
		if (itemLists == null || itemLists.isEmpty()) {
			return result;
		}
		
		for (List<StockItem> itemList : itemLists) {
			result = mergeStockItemList(result, itemList);
		}
		
		return result;
	}

	public static List<StockItem> mergeStockItemListMin23(List<StockItem> list2, List<StockItem> list3) {
		List<StockItem> result = new ArrayList<StockItem>();
		
		int i = 0;
		int j = 0;
		while (i<195) {
			StockItem item22 = list2.get(i++);
			StockItem item24 = list2.get(i++);
			StockItem item26 = list2.get(i++);
			StockItem item33 = list3.get(j++);
			StockItem item36 = list3.get(j++);

			StockItem item1 = new StockItem(item22.getStock().getSymbol(), item22.getPeriod());
			StockItem item5 = new StockItem(item26.getStock().getSymbol(), item22.getPeriod());

			// update timestamp
			item1.setTimestamp(new Date(item22.getTimestamp().getTime() - 60*1000));
			item5.setTimestamp(new Date(item26.getTimestamp().getTime() - 60*1000));

			// update open/close prices
			item1.setOpen(item22.getOpen());
			item5.setOpen(item22.getOpen());
			
			// update high/low
			findHiLo(item1, item22);
			findHiLo(item33, item24);
			findHiLo(item5, item26);
			
			// update rsi
			findRsiExtrapolate(item1, item22, item33);
			findRsiInterpolate(item5, item24, item26);
			
			// update cci
			findCciExtrapolate(item1, item22, item33);
			findCciInterpolate(item5, item24, item26);
			
			result.add(item1);
			result.add(item22);
			result.add(item33);
			result.add(item24);
			result.add(item5);
			result.add(item26);
		}
		
		return result;
	}
	
	private static void findHiLo(StockItem item1, StockItem item2) {
		double cdp = 0.25 * (item2.getHigh() + item2.getLow() + item2.getOpen() + item2.getClose());
		double nh = 2 * cdp - item2.getLow();
		double nl = 2 * cdp - item2.getHigh();
		
		item1.setClose(cdp);
		item2.setOpen(cdp);
		if ((item2.getHigh()-item1.getOpen()) < (item2.getHigh()-item2.getClose())) {
			item1.setHigh(item2.getHigh());
			item2.setHigh(nh);
		} else {
			item1.setHigh(nh);
		}
		
		if ((item1.getOpen()-item2.getLow()) < (item2.getClose()-item2.getLow())) {
			item1.setLow(item2.getLow());
			item2.setLow(nl);
		} else {
			item1.setLow(nl);
		}
		
	}

	private static void findRsiInterpolate(StockItem item1, StockItem item2, StockItem item3) {
		item1.setRsi(0.5 * item2.getRsi() + 0.5 * item3.getRsi());
	}
	
	private static void findRsiExtrapolate(StockItem item1, StockItem item2, StockItem item3) {
		item1.setRsi(2 * item2.getRsi() - item3.getRsi());
	}
	
	private static void findCciInterpolate(StockItem item1, StockItem item2, StockItem item3) {
		item1.setCci(0.5 * item2.getCci() + 0.5 * item3.getCci());
	}

	private static void findCciExtrapolate(StockItem item1, StockItem item2, StockItem item3) {
		item1.setCci(2 * item2.getCci() - item3.getCci());
	}
	public static List<StockItem> mergeStockItemList(List<StockItem> list1, List<StockItem> list2) {
		if (list1 == null || list1.isEmpty()) {
			return list2;
		}

		if (list2 == null || list2.isEmpty()) {
			return list1;
		}

		int len1 = list1.size();
		int len2 = list2.size();

		List<StockItem> result = new ArrayList<StockItem>(Math.max(len1, len2));
		int idx1 = 0;
		int idx2 = 0;
		StockItem item1 = null;
		StockItem item2 = null;
		
		while (idx1<len1 && idx2<len2) {
			item1 = list1.get(idx1);
			item2 = list2.get(idx2);
			if (item1.getTimestamp().equals(item2.getTimestamp())) {
				idx2++;
			}
			else if (item1.getTimestamp().after(item2.getTimestamp())) {
				result.add(item2);
				idx2++;
			}
			else {
				result.add(item1);
				idx1++;
			}
		}
		
		if (idx1<len1) {
			result.addAll(list1.subList(idx1, len1));
		}
		
		if (idx2<len2) {
			result.addAll(list2.subList(idx2, len2));
		}
		
		return result;
	}
	
	public static void main(String[] args) throws ParseException {
		String filename = "C:/workspaces/DayTrader/resources/data/UVXY_MIN.csv";
		String line = getLastLine(filename);
		System.out.println("last line=" + line);

		DateFormat df = new SimpleDateFormat(Constants.STOCK_PRICE_TIMESTAMP_PATTERN);
		
		// test list merge
		StockItem item11 = new StockItem("QQQ", df.parse("3/19/2013 10:00"), Period.MIN05, 0, 0, 0, 0, 50, 0, 100);
		StockItem item12 = new StockItem("QQQ", df.parse("3/19/2013 10:05"), Period.MIN05, 0, 0, 0, 0, 50, 0, 200);
		StockItem item13 = new StockItem("QQQ", df.parse("3/19/2013 10:10"), Period.MIN05, 0, 0, 0, 0, 50, 0, 300);

		List<StockItem> list1 = new ArrayList<StockItem>();
		list1.add(item11);
		list1.add(item12);
		list1.add(item13);

		StockItem item20 = new StockItem("QQQ", df.parse("3/19/2013 09:50"), Period.MIN05, 0, 0, 0, 0, 50, 0, 400);
		StockItem item21 = new StockItem("QQQ", df.parse("3/19/2013 10:00"), Period.MIN05, 0, 0, 0, 0, 50, 0, 400);
		StockItem item22 = new StockItem("QQQ", df.parse("3/19/2013 10:10"), Period.MIN05, 0, 0, 0, 0, 50, 0, 500);
		StockItem item23 = new StockItem("QQQ", df.parse("3/19/2013 10:20"), Period.MIN05, 0, 0, 0, 0, 50, 0, 600);

		List<StockItem> list2 = new ArrayList<StockItem>();
		list2.add(item20);
		list2.add(item21);
		list2.add(item22);
		list2.add(item23);
		
		List<StockItem> list3 = mergeStockItemList(list1, list2);
		
		list3.stream().forEach(System.out::println);
	}
}
