package com.broadviewsoft.daytrader.util;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.RandomAccessFile;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Stream;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.supercsv.cellprocessor.ift.CellProcessor;
import org.supercsv.io.CsvBeanReader;
import org.supercsv.io.ICsvBeanReader;
import org.supercsv.prefs.CsvPreference;

import com.broadviewsoft.daytrader.domain.Constants;
import com.broadviewsoft.daytrader.domain.DataException;
import com.broadviewsoft.daytrader.domain.DataFileType;
import com.broadviewsoft.daytrader.domain.Period;
import com.broadviewsoft.daytrader.domain.StockItem;

public class FileMerger {
	private static Log logger = LogFactory.getLog(FileMerger.class);
	private static final String FILE_NAME_PREFIX_PATTERN = "_\\d{4}_?";

	public static void mergeExt(String path, String symbol, Period period) throws DataException {
		List<StockItem> result = new ArrayList<StockItem>();
		File dir = new File(path);
		String filePattern = symbol + FILE_NAME_PREFIX_PATTERN + period.name() + ".CSV";
		logger.info("File pattern is " + filePattern);
		File[] files = dir.listFiles(f -> f.getName().toUpperCase().matches(filePattern));
		logger.info("File size is " + files.length);
        Arrays.asList(files).stream().sorted().forEach(System.out::println);
		String[] filenames = Stream.of(files).map( d -> { return d.getPath();}).toArray( String[]::new );
//	    Arrays.stream(filenames).forEach(System.out::println);
        result = merge(filenames);
//        logger.info("Processed merging of " + files.length + " " + period + " files\n");
        String outpath = path + Constants.PATH_SEPARATOR + symbol + "_0020" + period.name() + ".dat";
		writeToFile(outpath, result);
    }
 
	public static void testStream(File[] files) throws DataException {
        String[] filenames = Stream.of(files).map( d -> { return d.getPath();}).toArray( String[]::new );
        Arrays.stream(filenames).forEach(System.out::println);
//        List<StockItem> result = merge(filenames);
//        result.stream().forEach(System.out::println);
    }

	
	public static List<StockItem> merge(String... fns) throws DataException {
		int len = fns.length;
		List<List<StockItem>> itemLists = new ArrayList<List<StockItem>>(len);
		for (String fn : fns) {
			logger.info("Processing file " + fn);
			itemLists.add(readFile(fn));
		}
		List<StockItem> result = Util.mergeStockItemLists(itemLists);
		return result;
	}
	
	public static List<StockItem> merge2(String fn1, String fn2) throws DataException {
		List<StockItem> items1 = readFile(fn1);
		List<StockItem> items2 = readFile(fn2);
		List<StockItem> result = Util.mergeStockItemList(items1, items2);
		return result;
	}
	/**
	 * Read stock details line by line from data file
	 * 
	 * The number of headers determines data file type
	 * 
	 * i.e., with Volume header or without
	 * 
	 * @param filename input  filename
	 * @return list of stockItems containing price details
	 * 
	 * @throws DataException in case of errors on file process
	 * 
	 */
	public static List<StockItem> readFile(String filename) throws DataException {
		List<StockItem> result = new ArrayList<StockItem>();

		ICsvBeanReader beanReader = null;
		StockItem item = null;
		try {
			beanReader = new CsvBeanReader(new FileReader(filename),
					CsvPreference.STANDARD_PREFERENCE);

			// the header elements are used to map the values to the bean (names
			// must match)
			final String[] header = beanReader.getHeader(true);
//			final CellProcessor[] processors = Util.getProcessors(DataFileType.BVS);
			final CellProcessor[] processors = Util.getProcessors(DataFileType.Generic);

			while ((item = beanReader.read(StockItem.class, header, processors)) != null) {
				result.add(item);
			}

		} catch (FileNotFoundException e) {
			logger.error(filename, e);
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
	
	public static void writeToFile(String path, List<StockItem> items) {
		BufferedWriter writer = null;
		try {
			writer = new BufferedWriter(
                    new FileWriter(path, true));
			writer.append(Constants.NEW_FILE_HEADER);
			writer.newLine();
			for (StockItem item : items) {
				writer.append(item.toCsvString());
				writer.newLine();
			}
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			try {
				if (writer != null) {
					writer.close();
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		
	}

	public static void writeToNewFile(String path, List<StockItem> items) {
		File fout = new File(path);
		FileOutputStream fos = null;
		BufferedWriter bw = null;
		
		try {
			fos = new FileOutputStream(fout);
			bw = new BufferedWriter(new OutputStreamWriter(fos));
			bw.write(Constants.NEW_FILE_HEADER + "\n");
			for (StockItem item : items) {
				bw.write(item.toString());
				bw.newLine();
			}
			

		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			try {
				if (bw != null) {
					bw.close();
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		
	}
	
	public static void main(String[] args) throws DataException {
//		String fn1 = "C:/workspaces/DayTrader/resources/rawdata/QQQ/QQ1019_min.csv";
//		String fn2 = "C:/workspaces/DayTrader/resources/rawdata/QQQ/QQ1024_min.csv";
//		String fn3 = "C:/workspaces/DayTrader/resources/rawdata/QQQ/QQ1025_min.csv";
		
//		String fn3 = "C:/workspaces/DayTrader/resources/data/QQ1228_day.csv";
//		List<StockItem> result = merge(fn1, fn2, fn3);
//		
//		result.stream().forEach(System.out::println);

		String[] symbols = {"TSLA"};

//		String[] symbols = {"QQQ", "SPY","UVXY", "SHOP", "ROKU", "TSLA", "UNG"};
		
		for (String symbol : symbols) {
			for (Period p : Period.values()) {
				mergeExt("C:/workspaces/DayTrader/resources/rawdata/" + symbol + "/2020", symbol, p);
				System.out.println("finishing " + symbol + " " + p.name() + "\n");
			}
		}
		
//		String fn = "QQQ_0602_min01.csv";
//		String pt = "QQQ" + FILE_NAME_PREFIX_PATTERN+Period.MIN01.name()+ ".CSV";
//		boolean matched = fn.toUpperCase().matches(pt);
//		System.out.println(fn.toUpperCase() + " matches " + pt + " = " + matched);
	}

}
