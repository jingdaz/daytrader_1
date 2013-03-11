package com.broadviewsoft.daytrader.service;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.broadviewsoft.daytrader.domain.Account;
import com.broadviewsoft.daytrader.domain.Constants;
import com.broadviewsoft.daytrader.domain.Order;
import com.broadviewsoft.daytrader.domain.OrderStatus;
import com.broadviewsoft.daytrader.domain.Period;
import com.broadviewsoft.daytrader.domain.Stock;
import com.broadviewsoft.daytrader.domain.StockItem;
import com.broadviewsoft.daytrader.domain.StockPrice;
import com.broadviewsoft.daytrader.domain.Transaction;

public class BrokerService {
	private static Log logger = LogFactory.getLog(BrokerService.class);

	private List<Account> accounts = new ArrayList<Account>();

	private DataFeeder dataFeeder = null;

	public BrokerService() {
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
		double curPrice = dataFeeder.getPrice(stock.getSymbol(), timestamp);
		if (curPrice <= 0) {
			return null;
		}
		StockPrice sp = new StockPrice();
		sp.setDealTime(timestamp);
		sp.setPrice(curPrice);
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
		if (sp == null) {
			logger.error("No price for stock: "
					+ order.getStock().getSymbol());
			return;
		}
		switch (order.getTxType()) {
		case BUY:
			switch (order.getOrderType()) {
			case LIMIT:
				if (sp.getPrice() <= order.getLimitPrice()) {
					logger.info("\tExecuted Limit Buy order on " + clock);
					order.setStatus(OrderStatus.EXECUTED);
					Transaction tx = new Transaction(order);
					tx.setDealTime(clock);
					tx.setDealPrice(sp.getPrice());
					tx.setCommission(9.95);
					account.getTransactions().add(tx);
				}
				break;

			case MARKET:
				logger.info("\tExecuted Market Buy order on " + clock);
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
					logger.info("\tExecuted Stop Buy order on " + clock);
					order.setStatus(OrderStatus.EXECUTED);
					Transaction tx2 = new Transaction(order);
					tx2.setDealTime(clock);
					tx2.setDealPrice(sp.getPrice());
					tx2.setCommission(9.95);
					account.getTransactions().add(tx2);
				}
				break;

			case STOPLIMIT:
				logger.info("\tExecuted Stop-Limit Buy order on " + clock);
				break;
			}
			break;

		case SELL:
			switch (order.getOrderType()) {
			case LIMIT:
				if (sp.getPrice() >= order.getLimitPrice()) {
					logger.info("\tExecuted Limit Sell order on " + clock);
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
				logger.info("\tExecuted Market Sell order on " + clock);
				order.setStatus(OrderStatus.EXECUTED);
				Transaction tx = new Transaction(order);
				tx.setDealTime(clock);
				tx.setDealPrice(sp.getPrice());
				tx.setCommission(9.95);
				account.getTransactions().add(tx);
				break;

			case STOP:
				if (sp.getPrice() <= order.getLimitPrice()) {
					logger.info("\tExecuted Stop Sell order on " + clock);
					order.setStatus(OrderStatus.EXECUTED);
					Transaction tx2 = new Transaction(order);
					tx2.setDealTime(clock);
					tx2.setDealPrice(sp.getPrice());
					tx2.setCommission(9.95);
					account.getTransactions().add(tx2);
				}
				break;

			case STOPLIMIT:
				logger.info("\tExecuted Stop-Limit Sell order on " + clock);
				break;
			}

			break;
		}

	}

}
