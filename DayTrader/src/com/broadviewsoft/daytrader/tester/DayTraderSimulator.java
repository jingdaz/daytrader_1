package com.broadviewsoft.daytrader.tester;

import com.broadviewsoft.daytrader.domain.Account;

public class DayTraderSimulator {
	private Account account;
	
	
	public DayTraderSimulator() {
		account = new Account();
	}
	
	public void tradeDaily(double preClose, double curOpen) {
		account.init();
		account.sellOverNight(preClose, curOpen);
		
		
		account.showTransactions();
	}
	
	
	
	
	public static void main(String[] args) {
		DayTraderSimulator simulator = new DayTraderSimulator();
		double preClose = 9.91;
		double curOpen = 9.98;
		
		simulator.tradeDaily(preClose, curOpen);
	
	}
}
