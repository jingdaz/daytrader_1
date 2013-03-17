package com.broadviewsoft.daytrader.domain;

import java.util.ArrayList;
import java.util.Date;
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
	private double initialAmount = 0;

	private List<StockHolding> holdings = new ArrayList<StockHolding>();
	private List<Order> orders = new ArrayList<Order>();
	private List<Transaction> transactions = new ArrayList<Transaction>();

	public Account() {

	}

	public void init(double preClose, Date today) {
		cashAmount = Constants.INIT_CASH_AMOUNT;
		for (int i = 0; i < Constants.INIT_STOCK_SYMBOLS.length; i++) {
			Stock stock = new Stock(Constants.INIT_STOCK_SYMBOLS[i]);
			StockHolding sh = new StockHolding();
			sh.setStock(stock);
			sh.setQuantity(Constants.INIT_STOCK_VOLUMES[i]);
			sh.setAvgPrice(preClose);
			holdings.add(sh);
		}
		// record initial amount in total
		initialAmount = getTotal();
	}

	 public void reset() {
	    this.cashAmount = Constants.INIT_CASH_AMOUNT;
	    this.orders.clear();
	    this.transactions.clear();
	    this.holdings.clear();
	 }
	 
	public void placeOrder(Date now, Order order) {
		Iterator<Order> it = orders.listIterator();
		while (it.hasNext()) {
			Order o = it.next();
			if (o.getTxType() == order.getTxType()
					&& o.getStatus() == OrderStatus.OPEN) {
				// it.remove();
				o.setStatus(OrderStatus.CANCELLED);
				logger.info("[" + Util.format(now) + "] Cancelling order placed on [" + Util.format(order.getOrderTime()) + "]");
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

	public StockHolding getHolding(Stock stock) {
	  for (StockHolding sh : holdings) {
	    if (sh.getStock().getSymbol().equalsIgnoreCase(stock.getSymbol())) {
	      return sh;
	    }
	  }
	  return null;
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
						  logger.info("Selling quantity exceeds holding in account: "
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
			  logger.error("Error occurred when attempting to sell non-existing stock.");
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

	public double getTotal() {
	   double total = cashAmount;
	    for (StockHolding sh : holdings) {
	      total += sh.getQuantity() * sh.getAvgPrice();
	    }
	    return total;
	}
	
	public void showHoldings() {
		StringBuilder sb = new StringBuilder();
		sb.append("\r\nCash\t\t");
		for (StockHolding sh : holdings) {
			sb.append("Symbol\tQty\tPrice\t\t");
		}
		sb.append("Total\t\t");
		sb.append("Fee\t\t");
    sb.append("Profit");
    sb.append("\r\n");
    
		sb.append(Util.format(cashAmount) + "\t");
		if (cashAmount < 1000) {
		  sb.append("\t");
		}

		for (StockHolding sh : holdings) {
			sb.append(sh.getStock().getSymbol() + "\t" + sh.getQuantity()
					+ "\t" + Util.format(sh.getAvgPrice()) + "\t\t");
		}
		double totalAmount = getTotal();
    sb.append(Util.format(totalAmount) + "\t");
    double fees = getTransactions().size()*Constants.COMMISSION_FEE;
    if (fees > 0) {
      sb.append(Util.format(fees));
    }
    sb.append("\t\t");
    double profit = totalAmount-initialAmount-fees;
    if (Math.abs(profit) > 1) {
      sb.append(Util.format(profit));
    }
    sb.append("\r\n");
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
