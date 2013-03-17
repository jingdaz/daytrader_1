package com.broadviewsoft.daytrader.service;

import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.broadviewsoft.daytrader.domain.Constants;
import com.broadviewsoft.daytrader.domain.StockItem;
import com.broadviewsoft.daytrader.util.Util;

public class CCIService {
	private static Log logger = LogFactory.getLog(CCIService.class);

	public static void calculateCci(int interval, List<StockItem> items) {
		double[] ccis = calcCci(interval, items);
		for (int i = 0; i < items.size(); i++) {
			items.get(i).setCci(ccis[i]);
		}
	}
	
	private static double[] calcCci(int interval, List<StockItem> items) {
		double[] cci = new double[items.size()];

		double[] tp = new double[items.size()];
		double[] sma = new double[items.size()];
		double[] meanDev = new double[items.size()];

		// Typical Price
		for (int i = 0; i < items.size(); i++) {
			tp[i] = items.get(i).getTypical();
		}
		printIt(tp);

		// SMA-20
		for (int i = 0; i < items.size(); i++) {
			if (i < interval - 1) {
				continue;
			}
			sma[i] = calcSma(items.subList(i - (interval - 1), i + 1));
		}
		printIt(sma);

		// Mean Deviation of TP
		for (int i = 0; i < items.size(); i++) {
			if (i < interval - 1) {
				continue;
			}
			meanDev[i] = calcMeanDev(interval, sma[i],
					items.subList(i - (interval - 1), i + 1));
		}
		printIt(meanDev);

		// CCI
		for (int i = 0; i < items.size(); i++) {
			if (i < interval - 1) {
				continue;
			}
			cci[i] = (tp[i] - sma[i]) / (Constants.CCI_FACTOR * meanDev[i]);
		}
		printIt(cci);
		return cci;
	}

	private static double calcSma(List<StockItem> items) {
		if (items == null || items.isEmpty()) {
			return 0;
		}

		double sum = 0;
		for (StockItem si : items) {
			sum += si.getTypical();
		}
		return sum / items.size();
	}

	private static double calcMeanDev(int interval, double sma,
			List<StockItem> items) {
		if (items == null || items.isEmpty() || interval <= 0) {
			return 0;
		}

		double sum = 0;
		for (int i = 0; i < interval; i++) {
			sum += Math.abs(items.get(i).getTypical() - sma);
		}
		return sum / interval;
	}

	private static void printIt(double[] prices) {
		for (double d : prices) {
			System.out.print(Util.format(d) + " ");
		}
		System.out.println("\r\n");
	}

}
