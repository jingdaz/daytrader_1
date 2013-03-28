package com.broadviewsoft.daytrader.tester;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.broadviewsoft.daytrader.domain.Constants;
import com.broadviewsoft.daytrader.service.TradePlatform;
import com.broadviewsoft.daytrader.service.ITradeStrategy;
import com.broadviewsoft.daytrader.service.impl.CciStrategy;
import com.broadviewsoft.daytrader.service.impl.RsiStrategy;

/**
 * 
 * Test driver to simulate Day Trade using this system
 * <P>
 * Test driver to run backtest using this system to find an optimal strategy
 * </P>
 * <P>
 * <B>Creation date:</B> Mar 8, 2013 3:43:24 PM
 * </P>
 * 
 * @author Jason Zhang
 */
public class DayTraderSimulator {
	private static Log logger = LogFactory.getLog(DayTraderSimulator.class);

	private TradePlatform tradePlatform = null;
	private List<ITradeStrategy> strategies = null;
	
	public DayTraderSimulator() {
		tradePlatform = new TradePlatform();
		strategies = new ArrayList<ITradeStrategy>();
	}
	
	public List<ITradeStrategy> getStrategies() {
		return strategies;
	}

	public void setStrategies(List<ITradeStrategy> strategies) {
		this.strategies = strategies;
	}

	public void addStrategies(ITradeStrategy strategy) {
		strategies.add(strategy);
	}
	
	public void simulate(String symbol, Date startDate, Date endDate) {
		for (ITradeStrategy strategy : strategies) {
			logger.info("\r\nApplying strategy (" + strategy.getDescription() + ") for " + symbol);
			tradePlatform.trade(strategy, symbol, startDate, endDate);
		}
	}

	public static void main(String[] args) throws ParseException {
		DayTraderSimulator simulator = new DayTraderSimulator();
		simulator.addStrategies(new CciStrategy());
//		simulator.addStrategies(new RsiStrategy());
		String[] symbols = Constants.INIT_STOCK_SYMBOLS;
		Date startDate = Constants.TRADE_DATE_FORMATTER.parse("03/25/2013");
		Date endDate = Constants.TRADE_DATE_FORMATTER.parse("03/27/2013");
		for (String symbol : symbols) {
		  simulator.simulate(symbol, startDate, endDate);
		}
	}
}
