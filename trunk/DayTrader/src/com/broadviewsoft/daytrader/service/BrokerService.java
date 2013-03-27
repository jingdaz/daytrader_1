package com.broadviewsoft.daytrader.service;

import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
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
	 * Retrieve current stock price; Use data feed for now. To obtain real-time
	 * price, data feed should be used
	 * 
	 * @param timestamp
	 * @return
	 */
	private double getCurPrice(Stock stock, Date timestamp, Period period, PriceType type) {
		return dataFeeder.getPrice(stock.getSymbol(), timestamp,	period, type);
	}

	public List<Account> getAccounts() {
		return accounts;
	}

	public void setAccounts(List<Account> accounts) {
		this.accounts = accounts;
	}

	public void registerAccount(Account account) {
		if (!accounts.contains(account)) {
			accounts.add(account);
		}
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

	public StockItem getYesdayItem(String symbol, int index) {
	  return dataFeeder.getItemByIndex(symbol, Period.DAY, index);
	}
	
	// FIXME java.util.ConcurrentModificationException
	public void checkOrder(Date clock) {
		Order protectionOrder = null;
		for (Account acct : accounts) {
			List<Order> orders = acct.getOrders();
			
			// Clone all orders for check
			List<Order> clonedOrders = new ArrayList<Order>();
			clonedOrders.addAll(orders);
			
			for (Order order : clonedOrders) {
				if (order.getStatus() == OrderStatus.OPEN) {
					protectionOrder = fulfillOrder(acct, order, clock);
					if (protectionOrder != null) {
						acct.placeOrder(clock, protectionOrder);
						logger.info("\tPlacing Stop Sell order at " + clock
								+ " to protect new purchase.");
					}
				}
			}
		}
	}

	private Order fulfillOrder(Account account, Order order, Date clock) {
		Order result = null;
		double typ = getCurPrice(order.getStock(), clock, Period.MIN, PriceType.Typical);
		double low = getCurPrice(order.getStock(), clock, Period.MIN, PriceType.Low);
		double high = getCurPrice(order.getStock(), clock, Period.MIN, PriceType.High);
		
		if (typ <= 0) {
			logger.info("No historitcal price data found for "
					+ order.getStock().getSymbol() + " on ["
					+ Util.format(clock) + "]");
			return result;
		}

		double deal = 0;
		boolean buyFulfilled = false;

		switch (order.getTxType()) {
		case BUY:
			switch (order.getOrderType()) {
			case LIMIT:
				if (low <= order.getLimitPrice()) {
	        deal = (order.getLimitPrice() >= high ? typ : order.getLimitPrice());
					logger.info("\tExecuted Limit Buy order on [" + Util.format(clock)
							+ "]");
					order.setStatus(OrderStatus.EXECUTED);
					Transaction tx = new Transaction(order);
					tx.setDealTime(clock);
					tx.setDealPrice(deal);
					tx.setCommission(Constants.COMMISSION_FEE);
					account.getTransactions().add(tx);
					buyFulfilled = true;
					// update holdings
					account.updateHoldings(tx);
				}
				break;

			case MARKET:
			  deal = typ;
				logger.info("\tExecuted Market Buy order on [" + Util.format(clock) + "]");
				order.setStatus(OrderStatus.EXECUTED);
				Transaction tx = new Transaction(order);
				tx.setDealTime(clock);
				tx.setDealPrice(typ);
				tx.setCommission(Constants.COMMISSION_FEE);
				account.getTransactions().add(tx);
				buyFulfilled = true;
				// update holdings
				account.updateHoldings(tx);
				break;

			case STOP:
        if (high >= order.getStopPrice()) {
          deal = (order.getStopPrice() <= low ? typ : order.getStopPrice());
					logger.info("\tExecuted Stop Buy order on " + Util.format(clock));
					order.setStatus(OrderStatus.EXECUTED);
					Transaction tx2 = new Transaction(order);
					tx2.setDealTime(clock);
					tx2.setDealPrice(deal);
					tx2.setCommission(Constants.COMMISSION_FEE);
					account.getTransactions().add(tx2);
					buyFulfilled = true;
					// update holdings
					account.updateHoldings(tx2);
				}
				break;

			case STOPLIMIT:
				logger.info("\tExecuted Stop-Limit Buy order on " + Util.format(clock));
				break;
			}
			// always set stop order for protection
			if (buyFulfilled == true) {
				double stopPrice = Constants.PROTECTION_STOP_PRICE * deal;
//				double limitPrice = Constants.PROTECTION_LIMIT_PRICE * curPrice;
				result = Order.createOrder(clock, TransactionType.SELL,
						OrderType.STOP, order.getQuantity(), 0, stopPrice);
			}
			break;

		case SELL:
			StockHolding sh = account.getHolding(order.getStock());
			double diff = (typ - sh.getAvgPrice()) / sh.getAvgPrice();
			if (diff > Constants.STOCK_PRICE_UP_ALERT
					|| diff < Constants.STOCK_PRICE_DOWN_ALERT) {
				alertPriceChange(sh, diff, typ, clock);
			}
			switch (order.getOrderType()) {
			case LIMIT:
				if (high >= order.getLimitPrice()) {
				  deal = (order.getLimitPrice() <= low ? typ : order.getLimitPrice());
					logger.info("\tExecuted Limit Sell order on " + Util.format(clock));
					order.setStatus(OrderStatus.EXECUTED);
					Transaction tx2 = new Transaction(order);
					tx2.setDealTime(clock);
					tx2.setDealPrice(deal);
					tx2.setCommission(Constants.COMMISSION_FEE);
					account.getTransactions().add(tx2);
					// update holdings
					account.updateHoldings(tx2);
				}
				break;

			case MARKET:
			  deal = typ;
				logger.info("\tExecuted Market Sell order on " + Util.format(clock));
				order.setStatus(OrderStatus.EXECUTED);
				Transaction tx = new Transaction(order);
				tx.setDealTime(clock);
				tx.setDealPrice(typ);
				tx.setCommission(Constants.COMMISSION_FEE);
				account.getTransactions().add(tx);
				// update holdings
				account.updateHoldings(tx);
				break;

			case STOP:
				if (low <= order.getStopPrice()) {
				  deal = (order.getStopPrice() >= high ? typ : order.getStopPrice());
					logger.info("\tExecuted Stop Sell order on " + Util.format(clock));
					order.setStatus(OrderStatus.EXECUTED);
					Transaction tx2 = new Transaction(order);
					tx2.setDealTime(clock);
					tx2.setDealPrice(deal);
					tx2.setCommission(Constants.COMMISSION_FEE);
					account.getTransactions().add(tx2);
					// update holdings
					account.updateHoldings(tx2);
				}
				break;

			case STOPLIMIT:
				logger.info("\tExecuted Stop-Limit Sell order on " + Util.format(clock));
				break;
			}

			break;
		}
		return result;
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
		sb.append(Util.format(clock));
		sb.append("]");

//		logger.info(sb.toString());
		logger.debug(sb.toString());

	}

}
