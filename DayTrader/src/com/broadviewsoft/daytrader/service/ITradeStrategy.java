package com.broadviewsoft.daytrader.service;

import java.util.Date;

import com.broadviewsoft.daytrader.domain.Account;
import com.broadviewsoft.daytrader.domain.Period;
import com.broadviewsoft.daytrader.domain.StockStatus;

public interface ITradeStrategy {
	
	/* Show strategy description */
	public String getDescription();
	
	/* Retrieve strategy period */
	public Period getPeriod();
	
	/* Handle over night holdings if any */
	public void handleOverNight(Account account, String symbol, Date timestamp, double curOpen);
	
	/* Execute strategy based on status analysis */
	public void execute(StockStatus status, Account account);
}
