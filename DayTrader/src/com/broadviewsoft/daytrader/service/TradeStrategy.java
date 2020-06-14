package com.broadviewsoft.daytrader.service;

import java.util.Date;
import java.util.LinkedList;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.broadviewsoft.daytrader.domain.Constants;
import com.broadviewsoft.daytrader.domain.Period;
import com.broadviewsoft.daytrader.domain.StockItem;
import com.broadviewsoft.daytrader.domain.StockStatus;
import com.broadviewsoft.daytrader.util.Util;

/**
 * possible to mix strategies?
 * 
 * @author Jingda
 * 
 */
public abstract class TradeStrategy implements ITradeStrategy {
	private static Log logger = LogFactory.getLog( TradeStrategy.class);
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

		StockItem ytaItem = dataFeeder.getYesterdayItem(symbol, ytaItemIndex);
		StockItem preHigh = Util.findPreHigh(data);
		StockItem preLow = Util.findPreLow(data);
		StockItem curItem = data.get(data.size() - 1);

		curStatus.setYtaItem(ytaItem);
		curStatus.setPreHigh(preHigh);
		curStatus.setPreLow(preLow);
		curStatus.setCurItem(curItem);
		curStatus.setChartItems(new LinkedList<StockItem>(data));
		curStatus.setHistItems(new LinkedList<StockItem>(histData));

		return curStatus;
	}

}
