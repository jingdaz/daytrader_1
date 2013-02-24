package com.broadviewsoft.daytrader.domain;

import java.util.ArrayList;
import java.util.List;

public class Account {
	private Long acctNbr;
	private CurrencyType currencyType;
	private double cashAmount;
	private List<StockHolding> holdings = new ArrayList<StockHolding>();
	
	private List<Order> orders = new ArrayList<Order>();
	private List<Transaction> transactions = new ArrayList<Transaction>();
	
	public Account() {
		
	}

	public void init() {
		cashAmount = Constants.INIT_CASH_AMOUNT;
		for (int i=0; i<Constants.INIT_STOCK_SYMBOLS.length; i++) {
			Stock stock = new Stock();
			StockHolding sh = new StockHolding();
			sh.setStock(stock);
			sh.setQuantity(Constants.INIT_STOCK_VOLUMES[i]);
			sh.setAvgPrice(Constants.INIT_STOCK_PRICES[i]);
			holdings.add(sh);
		}
	
	}
	
	/**
	 * 
	 */
	public void handleOverNight(double preClose, double curOpen) {
		double lockWinLimit = Math.min(Constants.LOCKWIN_PRE_CLOSE_FACTOR
				* preClose, Constants.LOCKWIN_CUR_OPEN_FACTOR * curOpen);
		double stopLoss = Constants.STOPLOSS_CUR_OPEN_FACTOR * curOpen;
		
	}
	
	
	
	public Long getAcctNbr() {
		return acctNbr;
	}

	public void setAcctNbr(Long acctNbr) {
		this.acctNbr = acctNbr;
	}

	public CurrencyType getCurrencyType() {
		return currencyType;
	}

	public void setCurrencyType(CurrencyType currencyType) {
		this.currencyType = currencyType;
	}

	public double getCashAmount() {
		return cashAmount;
	}

	public void setCashAmount(double cashAmount) {
		this.cashAmount = cashAmount;
	}

	public List<StockHolding> getHoldings() {
		return holdings;
	}

	public void setHoldings(List<StockHolding> holdings) {
		this.holdings = holdings;
	}

	public List<Order> getOrders() {
		return orders;
	}

	public void setOrders(List<Order> orders) {
		this.orders = orders;
	}

	public List<Transaction> getTransactions() {
		return transactions;
	}

	public void setTransactions(List<Transaction> transactions) {
		this.transactions = transactions;
	}
	
	public void showTransactions() {
		
	}
}