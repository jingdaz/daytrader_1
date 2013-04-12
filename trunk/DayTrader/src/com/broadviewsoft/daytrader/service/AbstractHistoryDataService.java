package com.broadviewsoft.daytrader.service;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.broadviewsoft.daytrader.domain.Constants;
import com.broadviewsoft.daytrader.domain.DataException;
import com.broadviewsoft.daytrader.domain.DataFileType;
import com.broadviewsoft.daytrader.domain.Period;
import com.broadviewsoft.daytrader.domain.StockItem;
import com.broadviewsoft.daytrader.service.IHistoryDataService;
import com.broadviewsoft.daytrader.util.Util;

public abstract class AbstractHistoryDataService implements IHistoryDataService {
	private static Log logger = LogFactory.getLog(AbstractHistoryDataService.class);

	public abstract BufferedReader getReader(String symbol, Period period) throws DataException;
	
	public List<StockItem> loadData(String symbol, Period period, DataFileType type)
			throws DataException {
		List<StockItem> result = new ArrayList<StockItem>();
		
		BufferedReader in = null;
		String inputLine = null;
		Date timestamp = new Date();
		StockItem item = null;
		StockItem preItem = null;
		
		try {
			in = getReader(symbol, period);
			while ((inputLine = in.readLine()) != null) {
				logger.info(inputLine);
				if (inputLine
						.matches("^a{0,1}(\\d+)(,\\s*\\d*(\\.){0,1}\\d*){5}\\s*$")) {
					item = parseGFInput(period, timestamp, inputLine);
					// skip last item which on 4:01PM
					if (item != null && Util.getTimeInMins(item.getTimestamp())==Constants.MARKET_OPEN_TIME_IN_MINS) {
					  preItem = item;
					}
					else if (item != null && preItem != null) {
            result.add(Util.combine(preItem, item));
            preItem = null;
					}
					else if (item != null) {
					  result.add(item);
					}
				}
			}
			in.close();
		} catch (IOException e) {
			logger.error("Error occurred when reading from Google Finance.");
			throw new DataException();
		} finally {
			if (in != null) {
				try {
					in.close();
				} catch (IOException e) {
				}
			}
		}
		// append data to record file
		appendToFile(symbol, period, result, DataFileType.GF);
		return result;
	}

	public void appendToFile(String symbol, Period period, List<StockItem> items, DataFileType type) {
		BufferedWriter bufferWritter = null;
		try {
			String filename = Util.getDataPath(symbol, period, type);
			String lastLine = Util.getLastLine(filename);
			Date lastDate = null;
			boolean newData = false;
      
			if (lastLine != null && lastLine.startsWith(Constants.STOCK_DATA_FILE_HEADER)) {
			  logger.info("Empty stock data file found; ready to append.");
			  newData = true;
			}

			else {
       try {
          int idx = lastLine.indexOf(Constants.CSV_SEPARATOR);
  				lastDate = Constants.STOCK_PRICE_TIMESTAMP_FORMATTER
  						.parse(lastLine.substring(0, idx));
  			} catch (ParseException e) {
  				logger.error("Error found when parsing timestamp: "
  						+ lastLine);
  			}
			}
			// no data yet
			if (lastDate == null) {
				newData = true;
			}
			// true = append file
			bufferWritter = new BufferedWriter(new FileWriter(filename, true));
			for (StockItem item : items) {
				if (!newData && item.getTimestamp().after(lastDate)) {
					newData = true;
				}
				if (newData) {
					bufferWritter.write(item.toString(type));
				}
			}
			bufferWritter.flush();
			bufferWritter.close();
		} catch (IOException e) {
			logger.error("Error occurred when read/write stock data file.");
		} finally {
			if (bufferWritter != null) {
				try {
					bufferWritter.close();
				} catch (IOException e) {
				}
			}
		}
	}

	public static StockItem parseGFInput(Period period, Date timestamp,
			String line) {
		StockItem item = null;
		if (line != null) {
		  item = new StockItem();
			String[] tokens = line.split(Constants.CSV_SEPARATOR);
			// first record with UNIX timestamp
			if (tokens[0] != null
					&& tokens[0].startsWith(Constants.UNIX_TIMESTAMP_PREFIX)) {
			  // UNIX timestamp in seconds
				String startDay = tokens[0]
						.substring(Constants.UNIX_TIMESTAMP_PREFIX.length());
				long startTime = 1000 * Long.parseLong(startDay);
				switch (period) {
				case WEEK:
				case DAY:
					startTime -= Constants.MARKET_CLOSE_TIME;
					break;
				case MIN15:
				case MIN5:
				case MIN:
					break;
				}
				// set up start time for future parse
				timestamp.setTime(startTime);
				item.setTimestamp((Date) timestamp.clone());
			} 
			// consequent records with time offset from first record
			else {
				Calendar cal = new GregorianCalendar();
				cal.setTimeInMillis(timestamp.getTime() + 1000 * 60
						* period.minutes() * Long.parseLong(tokens[0]));
				long offset = cal.get(Calendar.HOUR_OF_DAY)
						* Constants.HOUR_IN_MILLI_SECONDS
						+ cal.get(Calendar.MINUTE)
						* Constants.MINUTE_IN_MILLI_SECONDS;
				logger.info("offset is " + cal.getTime());
				if ((period != Period.DAY && period != Period.WEEK && offset <= Constants.MARKET_OPEN_TIME)
						|| offset > Constants.MARKET_CLOSE_TIME) {
					logger.info("Offset skipped: " + cal.getTime());
					return null;
				}
				item.setTimestamp(cal.getTime());
			}
			item.setClose(Double.parseDouble(tokens[1]));
			item.setHigh(Double.parseDouble(tokens[2]));
			item.setLow(Double.parseDouble(tokens[3]));
			item.setOpen(Double.parseDouble(tokens[4]));
			item.setVolume(Long.parseLong(tokens[5]));

			// System.out.println("item timestamp=" + item.getTimestamp());
		}
		return item;
	}

	public static StockItem parseGFOutput(Period period, String line) {
		StockItem item = new StockItem();
		if (line != null) {
			String[] tokens = line.split(Constants.CSV_SEPARATOR);
			try {
				item.setTimestamp(Constants.STOCK_PRICE_TIMESTAMP_FORMATTER.parse(tokens[0]));
			} catch (ParseException e) {
				logger.error("Error found when parsing timestamp "
						+ e.getMessage());
			}
			item.setClose(Double.parseDouble(tokens[1]));
			item.setHigh(Double.parseDouble(tokens[2]));
			item.setLow(Double.parseDouble(tokens[3]));
			item.setOpen(Double.parseDouble(tokens[4]));
			item.setVolume(Long.parseLong(tokens[5]));
		}
		return item;
	}

}
