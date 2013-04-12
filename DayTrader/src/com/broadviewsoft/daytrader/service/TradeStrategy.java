package com.broadviewsoft.daytrader.service;

import java.util.Date;
import java.util.LinkedList;
import java.util.List;

import com.broadviewsoft.daytrader.domain.Constants;
import com.broadviewsoft.daytrader.domain.Period;
import com.broadviewsoft.daytrader.domain.StockItem;
import com.broadviewsoft.daytrader.domain.StockStatus;

/**
 * possible to mix strategies?
 * 
 * @author Jingda
 * 
 */
public abstract class TradeStrategy implements ITradeStrategy {
	protected Period period = null;
	protected IDataFeeder dataFeeder = null;

	public Period getPeriod() {
		return period;
	}

	public void setPeriod(Period period) {
		this.period = period;
	}

	public String getDescription() {
		StringBuilder sb = new StringBuilder();
		sb.append(getClass().getSimpleName());
		sb.append(" ");
		sb.append(period);
		return sb.toString();
	}

	public StockStatus analyze(BrokerService broker, String symbol,
			Period period, Date date, int ytaItemIndex) {
		StockStatus curStatus = new StockStatus(symbol, date);
		List<StockItem> data = dataFeeder.getHistoryData(symbol, period, date);
		List<StockItem> histData = data.subList(data.size()-Constants.CHART_CCI_SHOW_HISTORY, data.size());

		StockItem ytaItem = dataFeeder.getYesdayItem(symbol, ytaItemIndex);
		StockItem preHigh = findPreHigh(data);
		StockItem preLow = findPreLow(data);
		StockItem curItem = data.get(data.size() - 1);

		curStatus.setYtaItem(ytaItem);
		curStatus.setPreHigh(preHigh);
		curStatus.setPreLow(preLow);
		curStatus.setCurItem(curItem);
		curStatus.setChartItems(new LinkedList<StockItem>(data));
		curStatus.setHistItems(new LinkedList<StockItem>(histData));

		return curStatus;
	}

	/**
	 * Find previous Low of CCI within 48 intervals
	 * 
	 * @param data
	 * @return
	 */
	private StockItem findPreLow(List<StockItem> data) {
		List<StockItem> sample = data.subList(data.size()
				- Constants.STATUS_INTERVAL, data.size());
		StockItem preLow = sample.get(0);
		for (StockItem si : sample) {
			if (preLow.getCci() >= si.getCci()) {
				preLow = si;
			}
		}
		return preLow;
	}

	private StockItem findPreHigh(List<StockItem> data) {
		List<StockItem> sample = data.subList(data.size()
				- Constants.STATUS_INTERVAL, data.size());
		StockItem preHigh = sample.get(0);
		for (StockItem si : sample) {
			if (preHigh.getCci() <= si.getCci()) {
				preHigh = si;
			}
		}
		return preHigh;
	}

}
