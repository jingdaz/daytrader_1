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
import com.broadviewsoft.daytrader.domain.StockHolding;
import com.broadviewsoft.daytrader.domain.Transaction;
import com.broadviewsoft.daytrader.domain.TransactionType;
import com.broadviewsoft.daytrader.util.Util;

/**
 * Abstraction of Broker
 * 
 * @author Jason Zhang
 *
 */
public class BrokerService {
	private static Log logger = LogFactory.getLog(BrokerService.class);

//	private List<Account> accounts = new ArrayList<Account>();
	private IDataFeeder dataFeeder = null;

	public BrokerService() {
		// use MockDataFeeder by default
		dataFeeder = DataFeederFactory.newInstance();
	}

	// FIXME java.util.ConcurrentModificationException
	public void checkOrder(Account account, Date clock) {
		Order protectionOrder = null;
		List<Order> orders = account.getOrders();

		// Clone all orders for check
		List<Order> clonedOrders = new ArrayList<Order>();
		clonedOrders.addAll(orders);

		for (Order order : clonedOrders) {
			if (order.getStatus() == OrderStatus.OPEN) {
				protectionOrder = fulfillOrder(account, order, clock);
				if (protectionOrder != null) {
					account.placeOrder(clock, protectionOrder);
					logger.info("\tPlacing Stop Sell order at " + Util.format(clock) + " to protect new purchase.");
				}
			}
		}
	}

	public double getPrice(String symbol, Period period, int index, PriceType priceType) {
		return dataFeeder.getPriceByIndex(symbol, period, index, priceType);
	}
	
	public int getItemIndex(String symbol, Date tradeDate, Period period) {
		return dataFeeder.getCurItemIndex(symbol, tradeDate, period);
	}
	
	private Order fulfillOrder(Account account, Order order, Date clock) {
		Order result = null;
		 double typ = dataFeeder.getPrice(order.getStock().getSymbol(), clock,
		 Period.MIN01, PriceType.Typical);
		 double low = dataFeeder.getPrice(order.getStock().getSymbol(), clock,
		 Period.MIN01, PriceType.Low);
		 double high = dataFeeder.getPrice(order.getStock().getSymbol(), clock,
		 Period.MIN01, PriceType.High);

		if (typ <= 0) {
			logger.info("No historitcal price data found for " + order.getStock().getSymbol() + " on ["
					+ Util.format(clock) + "]");
			return result;
		}

		double deal = 0;
		boolean conditionMet = false;
		boolean fundSufficient = false;
		Transaction tx = null;

		switch (order.getTxType()) {
		case BUY:
			switch (order.getOrderType()) {
			case LIMIT:
				if (low <= order.getLimitPrice()) {
					conditionMet = true;
					deal = (order.getLimitPrice() >= high ? typ : order.getLimitPrice());
					fundSufficient = account.getCashAmount() > order.getQuantity() * deal;
				}
				break;

			case MARKET:
				conditionMet = true;
				deal = typ;
				fundSufficient = account.getCashAmount() > order.getQuantity() * deal;
				break;

			case STOP:
				if (high >= order.getStopPrice()) {
					conditionMet = true;
					deal = (order.getStopPrice() <= low ? typ : order.getStopPrice());
					fundSufficient = account.getCashAmount() > order.getQuantity() * deal;
				}
				break;

			case STOPLIMIT:
				// XXX assume time sequence does not matter for scenario price rises abruptly
				if (high >= order.getStopPrice() && low <= order.getLimitPrice()) {
					conditionMet = true;
					deal = (order.getStopPrice() <= typ ? typ : order.getLimitPrice());
					fundSufficient = account.getCashAmount() > order.getQuantity() * deal;
				}
				break;
			}
			if (conditionMet) {
				if (fundSufficient) {
					logger.info(
							"Executed " + order.getOrderType() + " BUY order @" + deal + " on " + Util.format(clock));
					order.setStatus(OrderStatus.EXECUTED);
					order.setCostPrice(deal);
					tx = new Transaction(order);
					tx.setDealTime(clock);
					tx.setDealPrice(deal);
					tx.setCommission(Constants.COMMISSION_FEE);
					account.getTransactions().add(tx);
					// update holdings
					account.updateHoldings(tx);
				} else {
					order.setStatus(OrderStatus.REJECTED);
					logger.info("Order triggered but no sufficient fund available; Rejected!");
				}
			}

			// always set stop order for protection
			if (!Constants.HUMAN_STRATEGY_ENABLED && conditionMet && fundSufficient) {
				double stopPrice = Constants.PROTECTION_STOP_PRICE * deal;
				// double limitPrice = Constants.PROTECTION_LIMIT_PRICE * curPrice;
				result = Order.createOrder(order.getStock(), clock, TransactionType.SELL, OrderType.STOP, order.getQuantity(), 0,
						stopPrice);
			}
			break;

		case SELL:
			StockHolding sh = account.getHolding(order.getStock());
			double diff = (typ - sh.getAvgPrice()) / sh.getAvgPrice();
			if (diff > Constants.STOCK_PRICE_UP_ALERT || diff < Constants.STOCK_PRICE_DOWN_ALERT) {
				alertPriceChange(sh, diff, typ, clock);
			}
			switch (order.getOrderType()) {
			case LIMIT:
				if (high >= order.getLimitPrice()) {
					conditionMet = true;
					deal = (order.getLimitPrice() <= low ? typ : order.getLimitPrice());

				}
				break;

			case MARKET:
				conditionMet = true;
				deal = typ;
				break;

			case STOP:
				if (low <= order.getStopPrice()) {
					conditionMet = true;
					deal = (order.getStopPrice() >= high ? typ : order.getStopPrice());
				}
				break;

			// XXX assume time sequence does not matter for scenario price drops abruptly
			case STOPLIMIT:
				if (low <= order.getStopPrice() && high >= order.getLimitPrice()) {
					conditionMet = true;
					deal = (order.getStopPrice() >= typ ? typ : order.getLimitPrice());
				}
				break;
			}

			if (conditionMet) {
				logger.info("Executed " + order.getOrderType() + " SELL order @" + deal + " on " + Util.format(clock));
				order.setStatus(OrderStatus.EXECUTED);
				order.setCostPrice(deal);
				tx = new Transaction(order);
				tx.setDealTime(clock);
				tx.setCommission(Constants.COMMISSION_FEE);
				account.getTransactions().add(tx);
				// update holdings
				account.updateHoldings(tx);
			}
			break;
		}
		return result;
	}

	private void alertPriceChange(StockHolding sh, double diff, double curPrice, Date clock) {
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

		// logger.info(sb.toString());
		logger.debug(sb.toString());

	}

}
