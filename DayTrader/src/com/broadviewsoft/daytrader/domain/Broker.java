package com.broadviewsoft.daytrader.domain;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class Broker {
	// private List<StockPrice> priceTrace = new ArrayList<StockPrice>();
	private List<Account> accounts = new ArrayList<Account>();

	private DataFeeder dataFeeder;

	public Broker() {
		dataFeeder = new DataFeeder(Constants.PROD_MODE);
	}

	// TODO Real-time data feed
	/**
	 * Retrieve cuurent stock price Use data feed for real-time price in future
	 * 
	 * @param timestamp
	 * @return
	 */
	public StockPrice getCurrentPrice(Stock stock, Date timestamp) {
		StockPrice sp = new StockPrice();
		sp.setDealTime(timestamp);
		sp.setPrice(dataFeeder.getPrice(stock.getSymbol(), timestamp));
		return sp;
	}

	public List<Account> getAccounts() {
		return accounts;
	}

	public void setAccounts(List<Account> accounts) {
		this.accounts = accounts;
	}

	public void addAccount(Account account) {
		accounts.add(account);
	}

	public DataFeeder getDataFeeder() {
		return dataFeeder;
	}

	public void setDataFeeder(DataFeeder dataFeeder) {
		this.dataFeeder = dataFeeder;
	}

	public List<StockItem> collectData(String symbol, Period period,
			Date cutTime) {
		return dataFeeder.getHistoryData(symbol, period, cutTime);
	}

	public void checkOrder(Date clock) {
		for (Account acct : accounts) {
			List<Order> orders = acct.getOrders();
			for (Order order : orders) {
				if (order.getStatus() == OrderStatus.OPEN) {
					fulfillOrder(acct, order, clock);
				}
			}
		}
	}

	private void fulfillOrder(Account account, Order order, Date clock) {
		StockPrice sp = getCurrentPrice(order.getStock(), clock);
		switch (order.getTxType()) {
		case BUY:
			switch (order.getOrderType()) {
			case LIMIT:
				if (sp.getPrice() <= order.getLimitPrice()) {
					order.setStatus(OrderStatus.EXECUTED);
					Transaction tx = new Transaction(order);
					tx.setDealTime(clock);
					tx.setDealPrice(sp.getPrice());
					tx.setCommission(9.95);
					account.getTransactions().add(tx);
				}
				break;

			case MARKET:
				order.setStatus(OrderStatus.EXECUTED);
				Transaction tx = new Transaction(order);
				tx.setDealTime(clock);
				tx.setDealPrice(sp.getPrice());
				tx.setCommission(9.95);
				account.getTransactions().add(tx);
				// update holdings
				account.updateHoldings(tx);
				break;

			case STOP:
				if (sp.getPrice() <= order.getLimitPrice()) {
					order.setStatus(OrderStatus.EXECUTED);
					Transaction tx2 = new Transaction(order);
					tx2.setDealTime(clock);
					tx2.setDealPrice(sp.getPrice());
					tx2.setCommission(9.95);
					account.getTransactions().add(tx2);
				}
				break;

			case STOPLIMIT:
				break;
			}
			break;

		case SELL:
			switch (order.getOrderType()) {
			case LIMIT:
				if (sp.getPrice() >= order.getLimitPrice()) {
					order.setStatus(OrderStatus.EXECUTED);
					Transaction tx2 = new Transaction(order);
					tx2.setDealTime(clock);
					tx2.setDealPrice(sp.getPrice());
					tx2.setCommission(9.95);
					account.getTransactions().add(tx2);
					// update holdings
					account.updateHoldings(tx2);
				}
				break;

			case MARKET:
				order.setStatus(OrderStatus.EXECUTED);
				Transaction tx = new Transaction(order);
				tx.setDealTime(clock);
				tx.setDealPrice(sp.getPrice());
				tx.setCommission(9.95);
				account.getTransactions().add(tx);
				break;

			case STOP:
				if (sp.getPrice() <= order.getLimitPrice()) {
					order.setStatus(OrderStatus.EXECUTED);
					Transaction tx2 = new Transaction(order);
					tx2.setDealTime(clock);
					tx2.setDealPrice(sp.getPrice());
					tx2.setCommission(9.95);
					account.getTransactions().add(tx2);
				}
				break;

			case STOPLIMIT:
				break;
			}

			break;
		}

	}

}
