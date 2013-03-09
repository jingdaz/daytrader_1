package com.broadviewsoft.daytrader.domain;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import com.broadviewsoft.daytrader.service.HistoryDataFileService;
import com.broadviewsoft.daytrader.service.HistoryDataService;
import com.broadviewsoft.daytrader.service.Util;

public class DataFeeder {
	private boolean prodMode = false;
	private HistoryDataService service = new HistoryDataFileService();
	List<StockItem> mins = new ArrayList<StockItem>();
	List<StockItem> min5s = new ArrayList<StockItem>();

	public DataFeeder() {
		this(false);
	}

	public DataFeeder(boolean prodMode) {
		mins = service.loadData("UVXY", Period.MIN);
		min5s = service.loadData("UVXY", Period.MIN5);

		this.prodMode = prodMode;
	}

	public boolean isProdMode() {
		return prodMode;
	}

	public void setProdMode(boolean prodMode) {
		this.prodMode = prodMode;
	}

	public List<StockItem> getHistoryData(String symbol, Period period,
			Date cutTime) {
		List<StockItem> result = new ArrayList<StockItem>();
		// TODO check NPE
		switch (period) {
		case MIN:
			return findSubList(mins, cutTime);
		case MIN5:
			return findSubList(min5s, cutTime);
		}
		return result;
	}

	// FIXME StockItem timestamp cannot be null and mins with size > 1
	public double getPrice(String symbol, Date timestamp) {
		double result = 0;

		if (timestamp == null || mins.isEmpty()
				|| timestamp.before(mins.get(0).getTimestamp())) {
			return result;
		}

		if (timestamp.after(mins.get(mins.size() - 1).getTimestamp())) {
			return result;
		}

		for (int i = 0; i < mins.size() - 1; i++) {
			if (timestamp.equals(mins.get(i).getTimestamp())) {
				return averagePrice(mins.get(i));
			}

			if (timestamp.after(mins.get(i).getTimestamp())
					&& timestamp.before(mins.get(i + 1).getTimestamp())) {
				return averagePrice(mins.get(i), mins.get(i + 1));
			}
		}
		return Util.trim(result);
	}

	// FIXME StockItem timestamp cannot be null and list with size > 1
	private List<StockItem> findSubList(List<StockItem> list, Date cutTime) {
		List<StockItem> result = new ArrayList<StockItem>();
		if (cutTime == null || list == null || list.isEmpty()
				|| list.get(0) == null
				|| cutTime.before(list.get(0).getTimestamp())) {
			return result;
		}

		if (list.get(list.size() - 1) == null
				|| cutTime.after(list.get(list.size() - 1).getTimestamp())) {
			return result;
		}

		for (int i = 0; i < list.size() - 1; i++) {
			if (!cutTime.before(list.get(i).getTimestamp())
					&& cutTime.before(list.get(i + 1).getTimestamp())) {
				return list.subList(0, i);
			}
		}

		return result;
	}

	private double averagePrice(StockItem item1) {
		return Util.trim((item1.getHigh() + item1.getLow() + item1.getClose()) / 3.0);
	}

	// FIXME ratio?
	private double averagePrice(StockItem item1, StockItem item2) {
		double avg1 = averagePrice(item1);
		double avg2 = averagePrice(item2);
		return Util.trim((avg1 + avg2) / 2.0);
	}
}
