package com.broadviewsoft.daytrader.tester;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.broadviewsoft.daytrader.domain.Account;
import com.broadviewsoft.daytrader.domain.Constants;
import com.broadviewsoft.daytrader.service.TradePlatform;
import com.broadviewsoft.daytrader.service.ITradeStrategy;
import com.broadviewsoft.daytrader.service.impl.CciStrategy;
import com.broadviewsoft.daytrader.service.impl.HumanStrategy;
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
public class DayTraderReplayer {
	private static Log logger = LogFactory.getLog(DayTraderReplayer.class);

	private TradePlatform tradePlatform = null;
	private List<ITradeStrategy> strategies = null;
	
	public DayTraderReplayer() {
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
	
	public void simulate(Account account, String symbol, Date startDate, Date endDate) {
		for (ITradeStrategy strategy : strategies) {
			logger.info("Applying strategy (" + strategy.getDescription() + ") for " + symbol);
			tradePlatform.trade(account, strategy, symbol, startDate, endDate);
		}
	}

	public static void main(String[] args) throws ParseException {
		DayTraderReplayer rewinder = new DayTraderReplayer();
		rewinder.addStrategies(new HumanStrategy());
		Account account = new Account();
//		simulator.addStrategies(new RsiStrategy());
		String[] symbols = Constants.INIT_STOCK_SYMBOLS;

		Date startDate = Constants.TRADE_DATE_FORMATTER.parse("04/04/2013");
		Date endDate = Constants.TRADE_DATE_FORMATTER.parse("04/08/2013");

		for (String symbol : symbols) {
		  rewinder.simulate(account, symbol, startDate, endDate);
		}
	}
}
