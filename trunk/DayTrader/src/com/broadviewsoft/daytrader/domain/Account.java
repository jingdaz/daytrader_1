package com.broadviewsoft.daytrader.domain;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.broadviewsoft.daytrader.util.Util;

public class Account {
	private static Log logger = LogFactory.getLog(Account.class);

	private Long acctNbr = Constants.DEFAULT_ACCOUNT_NUMBER;
	private CurrencyType currencyType = CurrencyType.USD;
	private double cashAmount = 0;

	private List<StockHolding> holdings = new ArrayList<StockHolding>();
	private List<Order> orders = new ArrayList<Order>();
	private List<Transaction> transactions = new ArrayList<Transaction>();

	public Account() {

	}

	public void init() {
		cashAmount = Constants.INIT_CASH_AMOUNT;
		for (int i = 0; i < Constants.INIT_STOCK_SYMBOLS.length; i++) {
			Stock stock = new Stock();
			StockHolding sh = new StockHolding();
			sh.setStock(stock);
			sh.setQuantity(Constants.INIT_STOCK_VOLUMES[i]);
			sh.setAvgPrice(Constants.INIT_STOCK_PRICES[i]);
			holdings.add(sh);
		}
	}

	public void placeOrder(Order order) {
		Iterator<Order> it = orders.listIterator();
		while (it.hasNext()) {
			Order o = it.next();
			if (o.getTxType() == order.getTxType()
					&& o.getStatus() == OrderStatus.OPEN) {
				// it.remove();
				o.setStatus(OrderStatus.CANCELLED);
				logger.info("Cancelling order placed on " + order.getOrderTime());
			}
		}
		orders.add(order);
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

	public void updateHoldings(Transaction tx) {
		Stock stock = tx.getStock();
		TransactionType type = tx.getTxType();
		double dealPrice = tx.getDealPrice();
		int qty = tx.getQuantity();

		// in hand already
		if (isInHolding(tx.getStock())) {
			Iterator<StockHolding> it = holdings.listIterator();
			// empty first?
			while (it.hasNext()) {
				StockHolding sh = it.next();
				if (sh.getStock().getSymbol()
						.equalsIgnoreCase(stock.getSymbol())) {
					switch (type) {
					case BUY:
						cashAmount -= qty * dealPrice;
						double avgPrice = (sh.getAvgPrice() * sh.getQuantity() + dealPrice
								* qty)
								/ (sh.getQuantity() + qty);
						sh.setAvgPrice(avgPrice);
						sh.setQuantity(sh.getQuantity() + qty);
						break;

					case SELL:
						if (qty > sh.getQuantity()) {
							System.out
									.println("Selling quantity exceeds holding in account: "
											+ qty);
						} else {
							cashAmount += qty * dealPrice;
							int remaining = sh.getQuantity() - qty;
							if (remaining == 0) {
								it.remove();
							} else {
								sh.setQuantity(remaining);
							}

						}
						break;
					}
				}
			}
		}

		// not in hand yet
		else {
			switch (type) {
			case BUY:
				cashAmount -= qty * dealPrice;
				StockHolding sh = new StockHolding();
				sh.setStock(tx.getStock());
				sh.setAvgPrice(tx.getDealPrice());
				sh.setQuantity(tx.getQuantity());
				holdings.add(sh);
				break;

			case SELL:
				System.out
						.println("Error occurred when attempting to sell non-existing stock.");
				break;
			}
		}

	}

	private boolean isInHolding(Stock stock) {
		for (StockHolding sh : holdings) {
			if (sh.getStock().getSymbol().equalsIgnoreCase(stock.getSymbol())) {
				return true;
			}
		}

		return false;
	}

	public void showHoldings() {
		StringBuilder sb = new StringBuilder();
		sb.append("\r\nCash\t\t");
		for (StockHolding sh : holdings) {
			sb.append("Symbol\tQty\tPrice\t\t");
		}
		sb.append("Total\r\n");

		sb.append(Util.format(cashAmount) + "\t");
		double total = cashAmount;
		for (StockHolding sh : holdings) {
			total += sh.getQuantity() * sh.getAvgPrice();
			sb.append(sh.getStock().getSymbol() + "\t" + sh.getQuantity()
					+ "\t" + Util.format(sh.getAvgPrice()) + "\t\t");
		}
		sb.append(Util.format(total) + "\r\n");
		logger.info(sb.toString());
	}

	public void showOrders() {
		StringBuilder sb = new StringBuilder();
		sb.append(Order.printHeaders(CurrencyType.USD));

		// print out transaction details
		for (Order order : orders) {
			sb.append(order + "\r\n");
		}
		logger.info(sb.toString());
	}

	public void showTransactions() {
		StringBuilder sb = new StringBuilder();
		sb.append(Transaction.printHeaders(CurrencyType.USD));

		// print out transaction details
		for (Transaction tx : transactions) {
			sb.append(tx + "\r\n");
		}
		logger.info(sb.toString());
	}

}