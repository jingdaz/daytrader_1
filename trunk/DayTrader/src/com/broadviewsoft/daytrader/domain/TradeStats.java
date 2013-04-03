package com.broadviewsoft.daytrader.domain;

import java.util.ArrayList;
import java.util.List;

public class TradeStats
{
    private List<DailyAccount> dailyAccounts = new ArrayList<DailyAccount>();

    public TradeStats() {
      
    }

    public List<DailyAccount> getDailyAccounts()
    {
      return dailyAccounts;
    }

    public void setDailyAccounts(List<DailyAccount> dailyAccounts)
    {
      this.dailyAccounts = dailyAccounts;
    }
    
    public void addDailyAccount(DailyAccount da) {
      dailyAccounts.add(da);
    }
}
 
