package com.broadviewsoft.daytrader.service;

import java.util.Date;
import java.util.Random;

import com.broadviewsoft.daytrader.domain.Account;
import com.broadviewsoft.daytrader.domain.Order;
import com.broadviewsoft.daytrader.domain.OrderStatus;
import com.broadviewsoft.daytrader.domain.OrderType;
import com.broadviewsoft.daytrader.domain.Stock;
import com.broadviewsoft.daytrader.domain.StockStatus;
import com.broadviewsoft.daytrader.domain.TransactionType;

public class CciStrategy extends TradeStrategy {
	private static Log log = LogFactory.getLog(CciStrategy.class);
	
	public void execute(StockStatus status, Account account) {
		// status one, account one
		Random random = new Random(System.currentTimeMillis());
		int n = random.nextInt(100);
		if (n < 70) {
			if (account.getHoldings() != null
					&& account.getHoldings().isEmpty()) {
				System.out.println("Placing market buy order @ "
						+ status.getTimestamp());
				Order newOrder = createOrder(status.getTimestamp(),
						TransactionType.BUY, OrderType.MARKET, 1000);
				account.placeOrder(newOrder);
			}
		}

		else if (n > 50) {
			if (account.getHoldings() != null
					&& !account.getHoldings().isEmpty()) {
				System.out.println("Placing limit sell order... @ "
						+ status.getTimestamp());
				double targetPrice = 10.0 + random.nextInt(10) / 50.0;
				Order newOrder = createOrder(status.getTimestamp(),
						TransactionType.SELL, OrderType.LIMIT, 1000, targetPrice);
				account.placeOrder(newOrder);
			}
		}

		// status two, account two

	}

	public Order createOrder(Date orderTime, TransactionType txType,
			OrderType orderType, int qty) {
		return createOrder(orderTime, txType, orderType, qty, 0, 0);
	}

	public Order createOrder(Date orderTime, TransactionType txType,
			OrderType orderType, int qty, double limitPrice) {
		return createOrder(orderTime, txType, orderType, qty, limitPrice, 0);
	}

	public Order createOrder(Date orderTime, TransactionType txType,
			OrderType orderType, int qty, double limitPrice, double stopPrice) {
		Order order = new Order();
		order.setOrderTime(orderTime);
		order.setStock(new Stock("UVXY"));
		order.setTxType(txType);
		order.setStatus(OrderStatus.OPEN);
		order.setOrderType(orderType);
		order.setQuantity(qty);
		order.setLimitPrice(limitPrice);
		order.setStopPrice(stopPrice);
		return order;
	}

}
