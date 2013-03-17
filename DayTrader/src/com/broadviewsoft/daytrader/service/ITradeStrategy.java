package com.broadviewsoft.daytrader.service;

import java.util.Date;

import com.broadviewsoft.daytrader.domain.Account;
import com.broadviewsoft.daytrader.domain.DailyStatus;
import com.broadviewsoft.daytrader.domain.Period;
import com.broadviewsoft.daytrader.domain.StockStatus;

public interface ITradeStrategy {
	
	/* Show strategy description */
	public String getDescription();
	
	/* Retrieve strategy period */
	public Period getPeriod();
	
	public DailyStatus getDailyStatus();

	public void setDailyStatus(DailyStatus dailyStatus); 
	
	/* Analyze current status */
	public StockStatus analyze(BrokerService broker, String symbol, Period period, Date date);
	
	/* Handle over night holdings if any */
	public void handleOverNight(Account account, String symbol, Date timestamp, double preClose, double curOpen);
	
	/* Execute strategy based on status analysis */
	public void execute(StockStatus status, Account account);
	
	public void resetDailyStatus();
}
