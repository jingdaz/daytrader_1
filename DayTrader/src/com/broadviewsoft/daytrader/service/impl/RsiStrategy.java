package com.broadviewsoft.daytrader.service.impl;

import java.util.Date;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.broadviewsoft.daytrader.domain.Account;
import com.broadviewsoft.daytrader.domain.Period;
import com.broadviewsoft.daytrader.domain.StockStatus;
import com.broadviewsoft.daytrader.service.ITradeStrategy;
import com.broadviewsoft.daytrader.service.TradeStrategy;

public class RsiStrategy extends TradeStrategy implements ITradeStrategy
{
  private static Log logger = LogFactory.getLog(RsiStrategy.class);
  
  public RsiStrategy() {
		period = Period.MIN05;
	}

  public void execute(StockStatus status, Account account)
  {
    logger.info("Executing RSI strategy.");

  }

  public void handleOverNight(Account account, String symbol, Date timestamp, double preClose, double curOpen)
  {
    logger.info("Handling over-night holding using RSI strategy.");

  }

}
 
