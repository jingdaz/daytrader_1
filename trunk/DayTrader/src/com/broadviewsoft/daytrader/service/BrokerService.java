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
import com.broadviewsoft.daytrader.domain.OrderType;
import com.broadviewsoft.daytrader.domain.Period;
import com.broadviewsoft.daytrader.domain.PriceType;
import com.broadviewsoft.daytrader.domain.Stock;
import com.broadviewsoft.daytrader.domain.StockHolding;
import com.broadviewsoft.daytrader.domain.StockItem;
import com.broadviewsoft.daytrader.domain.StockPrice;
import com.broadviewsoft.daytrader.domain.Transaction;
import com.broadviewsoft.daytrader.domain.TransactionType;
import com.broadviewsoft.daytrader.util.Util;

public class BrokerService {
	private static Log logger = LogFactory.getLog(BrokerService.class);

	private List<Account> accounts = new ArrayList<Account>();

	private DataFeeder dataFeeder = null;

	public BrokerService() {
		dataFeeder = new DataFeeder(Constants.PROD_MODE);
		dataFeeder.init(Constants.STOCKS_WITH_DATA);
	}

	// TODO Real-time data feed
	/**
	 * Retrieve cuurent stock price Use data feed for real-time price in future
	 * 
	 * @param timestamp
	 * @return
	 */
	public StockPrice getCurrentPrice(Stock stock, Date timestamp) {
		double curPrice = dataFeeder.getPrice(stock.getSymbol(), timestamp,
				PriceType.Typical);
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

	public void registerAccount(Account account) {
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
			logger.info("No historitcal price data found for "
					+ order.getStock().getSymbol() + " on ["
					+ Util.format(clock) + "]");
			return;
		}
		double curPrice = sp.getPrice();
		boolean buyFulfilled = false;
		switch (order.getTxType()) {
		case BUY:
			switch (order.getOrderType()) {
			case LIMIT:
				if (curPrice <= order.getLimitPrice()) {
					logger.debug("\tExecuted Limit Buy order on [" + clock
							+ "]");
					order.setStatus(OrderStatus.EXECUTED);
					Transaction tx = new Transaction(order);
					tx.setDealTime(clock);
					tx.setDealPrice(curPrice);
					tx.setCommission(Constants.COMMISSION_FEE);
					account.getTransactions().add(tx);
					buyFulfilled = true;
					// update holdings
					account.updateHoldings(tx);
				}
				break;

			case MARKET:
				logger.debug("\tExecuted Market Buy order on [" + clock + "]");
				order.setStatus(OrderStatus.EXECUTED);
				Transaction tx = new Transaction(order);
				tx.setDealTime(clock);
				tx.setDealPrice(curPrice);
				tx.setCommission(Constants.COMMISSION_FEE);
				account.getTransactions().add(tx);
				buyFulfilled = true;
				// update holdings
				account.updateHoldings(tx);
				break;

			case STOP:
				if (sp.getPrice() <= order.getLimitPrice()) {
					logger.debug("\tExecuted Stop Buy order on " + clock);
					order.setStatus(OrderStatus.EXECUTED);
					Transaction tx2 = new Transaction(order);
					tx2.setDealTime(clock);
					tx2.setDealPrice(curPrice);
					tx2.setCommission(Constants.COMMISSION_FEE);
					account.getTransactions().add(tx2);
					buyFulfilled = true;
					// update holdings
					account.updateHoldings(tx2);
				}
				break;

			case STOPLIMIT:
				logger.debug("\tExecuted Stop-Limit Buy order on " + clock);
				break;
			}
			// always set stop order for protection
			if (buyFulfilled == true) {
				Order sell = Order.createOrder(clock, TransactionType.SELL,
						OrderType.STOP, order.getQuantity());
				account.placeOrder(clock, sell);
				logger.debug("\tPlacing Stop Sell order at " + clock
						+ " to protect new purchase.");
			}
			break;

		case SELL:
			StockHolding sh = account.getHolding(order.getStock());
			double diff = (curPrice - sh.getAvgPrice()) / sh.getAvgPrice();
			if (diff > Constants.STOCK_PRICE_UP_ALERT
					|| diff < Constants.STOCK_PRICE_DOWN_ALERT) {
				alertPriceChange(sh, diff, curPrice, clock);
			}
			switch (order.getOrderType()) {
			case LIMIT:
				if (sp.getPrice() >= order.getLimitPrice()) {
					logger.debug("\tExecuted Limit Sell order on " + clock);
					order.setStatus(OrderStatus.EXECUTED);
					Transaction tx2 = new Transaction(order);
					tx2.setDealTime(clock);
					tx2.setDealPrice(curPrice);
					tx2.setCommission(Constants.COMMISSION_FEE);
					account.getTransactions().add(tx2);
					// update holdings
					account.updateHoldings(tx2);
				}
				break;

			case MARKET:
				logger.debug("\tExecuted Market Sell order on " + clock);
				order.setStatus(OrderStatus.EXECUTED);
				Transaction tx = new Transaction(order);
				tx.setDealTime(clock);
				tx.setDealPrice(sp.getPrice());
				tx.setCommission(Constants.COMMISSION_FEE);
				account.getTransactions().add(tx);
				// update holdings
				account.updateHoldings(tx);
				break;

			case STOP:
				if (sp.getPrice() <= order.getLimitPrice()) {
					logger.debug("\tExecuted Stop Sell order on " + clock);
					order.setStatus(OrderStatus.EXECUTED);
					Transaction tx2 = new Transaction(order);
					tx2.setDealTime(clock);
					tx2.setDealPrice(curPrice);
					tx2.setCommission(Constants.COMMISSION_FEE);
					account.getTransactions().add(tx2);
					// update holdings
					account.updateHoldings(tx2);
				}
				break;

			case STOPLIMIT:
				logger.debug("\tExecuted Stop-Limit Sell order on " + clock);
				break;
			}

			break;
		}

	}

	private void alertPriceChange(StockHolding sh, double diff,
			double curPrice, Date clock) {
		StringBuilder sb = new StringBuilder();
		sb.append("Price changed for ");
		sb.append(sh.getStock().getSymbol());
		sb.append(" ");
		sb.append(Util.format(100 * diff));
		sb.append("% from ");
		sb.append(Util.format(sh.getAvgPrice()));
		sb.append(" to ");
		sb.append(Util.format(curPrice));
		sb.append(" on [");
		// sb.append(Util.format(clock));
		sb.append(clock);
		sb.append("]");

		logger.info(sb.toString());

	}

}
