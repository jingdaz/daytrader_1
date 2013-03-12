package com.broadviewsoft.daytrader.tester;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.broadviewsoft.daytrader.domain.Constants;
import com.broadviewsoft.daytrader.service.DayTradeService;
import com.broadviewsoft.daytrader.service.ITradeStrategy;
import com.broadviewsoft.daytrader.service.impl.CciStrategy;

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

	private DayTradeService dayTradeService = new DayTradeService();
	private List<ITradeStrategy> strategies = new ArrayList<ITradeStrategy>();
	
	public DayTraderSimulator() {
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
			logger.info("Applying strategy: " + strategy.getDescription() + "\r\n");
			dayTradeService.trade(strategy, symbol, startDate, endDate);
		}
	}

	public static void main(String[] args) throws ParseException {
		DayTraderSimulator simulator = new DayTraderSimulator();
		simulator.addStrategies(new CciStrategy());
		String symbol = "UVXY";
		Date startDate = Constants.TRADE_DATE_FORMATTER.parse("03/01/2013");
		Date endDate = Constants.TRADE_DATE_FORMATTER.parse("03/04/2013");
		simulator.simulate(symbol, startDate, endDate);
	}
}
