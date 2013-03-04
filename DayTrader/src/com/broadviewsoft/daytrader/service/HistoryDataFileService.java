package com.broadviewsoft.daytrader.service;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.util.List;

import au.com.bytecode.opencsv.CSVReader;
import au.com.bytecode.opencsv.bean.ColumnPositionMappingStrategy;
import au.com.bytecode.opencsv.bean.CsvToBean;

import com.broadviewsoft.daytrader.domain.Constants;
import com.broadviewsoft.daytrader.domain.StockItem;

public class HistoryDataFileService implements HistoryDataService {

	@Override
	public List<StockItem> loadData() {
		String location = Constants.HISTORY_DATA_PATH;
		String filename = "uvxy_hour.csv";
		CSVReader reader = null;
		try {
			reader = new CSVReader(new FileReader(location + filename));
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
		
		ColumnPositionMappingStrategy<StockItem> strat = new ColumnPositionMappingStrategy<StockItem>();
		strat.setType(StockItem.class);
		String[] columns = new String[] {"timestamp", "open", "high", "low", "close", "rsi", "cci", "volume"};
		strat.setColumnMapping(columns);

		CsvToBean<StockItem> csv = new CsvToBean<StockItem>();
		List<StockItem> result = csv.parse(strat, reader);
		return result;
	}

	public static void main(String[] args) {
		HistoryDataFileService service = new HistoryDataFileService();
		List<StockItem> result = service.loadData();
	}
}
