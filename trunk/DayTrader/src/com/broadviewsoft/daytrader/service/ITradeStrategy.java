package com.broadviewsoft.daytrader.service;

import com.broadviewsoft.daytrader.domain.Account;
import com.broadviewsoft.daytrader.domain.StockStatus;

public interface ITradeStrategy
{
  public void execute(StockStatus status, Account account);
}
 
