package com.broadviewsoft.daytrader.domain;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import com.broadviewsoft.daytrader.service.Util;

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
		for (int i = 0; i < Constants.INIT_STOCK_SYMBOLS.length; i++) {
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

	public void placeOrder(Order order) {
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
							System.out.println("Selling quantity exceeds holding in account: " + qty);
						}
						else {
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
				System.out.println("Error occurred when attempting to sell non-existing stock.");
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
		System.out.print("\r\nCash\t\t");
		for (StockHolding sh : holdings) {
			System.out.print("Symbol\tQty\tPrice\t\t");
		}
		System.out.println("Total");

		System.out.print(Util.format(cashAmount) + "\t");
		double total = cashAmount;
		for (StockHolding sh : holdings) {
			total += sh.getQuantity()*sh.getAvgPrice();
			System.out.print(sh.getStock().getSymbol()  + "\t" + sh.getQuantity()  + "\t" + Util.format(sh.getAvgPrice()) + "\t\t");
		}
		System.out.println(Util.format(total) + "\r\n");
	}

	public void showOrders() {
		System.out.println(Order.printHeaders(CurrencyType.USD));

		// print out transaction details
		for (Order order : orders) {
			System.out.println(order);
		}
	}

	public void showTransactions() {
		System.out.println(Transaction.printHeaders(CurrencyType.USD));

		// print out transaction details
		for (Transaction tx : transactions) {
			System.out.println(tx);
		}
	}

}
