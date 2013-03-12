package com.broadviewsoft.daytrader.service;

import java.util.Date;
import java.util.LinkedList;
import java.util.List;

import com.broadviewsoft.daytrader.domain.Constants;
import com.broadviewsoft.daytrader.domain.DailyStatus;
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
	protected DailyStatus dailyStatus = null;

	public Period getPeriod() {
		return period;
	}

	public void setPeriod(Period period) {
		this.period = period;
	}

	public DailyStatus getDailyStatus() {
		return dailyStatus;
	}

	public void setDailyStatus(DailyStatus dailyStatus) {
		this.dailyStatus = dailyStatus;
	}

	public String getDescription() {
		StringBuilder sb = new StringBuilder();
		sb.append(getClass().getSimpleName());
		sb.append(" ");
		sb.append(period);
		return sb.toString();
	}

	public StockStatus analyze(BrokerService broker, String symbol,
			Period period, Date date) {
		StockStatus curStatus = new StockStatus(date);
		List<StockItem> data = broker.collectData(symbol, period, date);
		StockItem preHigh = findPreHigh(data);
		StockItem preLow = findPreLow(data);
		StockItem curItem = data.get(data.size() - 1);

		if (dailyStatus.isWeakest()
				&& curItem.getCci() > Constants.CCI_WEAKEST_LIMIT) {
			dailyStatus.setWeakest(false);
		}
		if (dailyStatus.isStrongest()
				&& curItem.getCci() < Constants.CCI_STRONGEST_LIMIT) {
			dailyStatus.setStrongest(false);
		}

		curStatus.setPreHigh(preHigh);
		curStatus.setPreLow(preLow);
		curStatus.setCurItem(curItem);
		curStatus.setChartItems(new LinkedList<StockItem>(data));
		dailyStatus.getStatuses().add(curStatus);

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

	public void resetDailyStatus() {
		dailyStatus.reset();
	}
}
