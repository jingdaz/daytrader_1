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
	
	public List<ITradeStrategy> getStrategies() {
		return strategies;
	}

	public void setStrategies(List<ITradeStrategy> strategies) {
		this.strategies = strategies;
	}

	public void addStrategies(ITradeStrategy strategy) {
		strategies.add(strategy);
	}
	
	public void simulate(Date tradeDate, String symbol, double curOpen) {
		for (ITradeStrategy strategy : strategies) {
			logger.info("Applying strategy: " + strategy.getDescription() + "\r\n");
			dayTradeService.tradeDaily(strategy, symbol, tradeDate, curOpen);
		}
	}

	public static void main(String[] args) throws ParseException {
		DayTraderSimulator simulator = new DayTraderSimulator();
		simulator.addStrategies(new CciStrategy());
		String symbol = "UVXY";
		double curOpen = 9.98;
		Date tradeDate = Constants.TRADE_DATE_FORMATTER.parse("02/28/2013");

		simulator.simulate(tradeDate, symbol, curOpen);
	}
}
