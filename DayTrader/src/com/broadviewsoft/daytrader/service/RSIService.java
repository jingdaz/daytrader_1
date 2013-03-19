package com.broadviewsoft.daytrader.service;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.broadviewsoft.daytrader.domain.StockItem;
import com.broadviewsoft.daytrader.util.Util;

public class RSIService
{
  private static Log logger = LogFactory.getLog(RSIService.class);

  public static double[] calculateRsi(int interval, List<StockItem> items)
  {
    double[] rsi = new double[items.size()];
    double[] rs = new double[items.size()];

    double[] gains = new double[items.size()];
    double[] losses = new double[items.size()];
    double[] avgGains = new double[items.size()];
    double[] avgLosses = new double[items.size()];

    // Gain or Loss
    for (int i = 1; i < items.size(); i++)
    {
      double diff = items.get(i).getClose() - items.get(i - 1).getClose();
      if (diff > 0)
      {
        gains[i] = diff;
      }
      else
      {
        losses[i] = -diff;
      }
    }
    printIt(gains);
    printIt(losses);

    // Average Gain or Loss

    double sumG = 0;
    double sumL = 0;

    // The very first average Gain/Loss
    for (int i = 1; i <= interval; i++)
    {
      sumG += gains[i];
      sumL += losses[i];
    }
    avgGains[interval] = sumG / interval;
    avgLosses[interval] = sumL / interval;

    // The second, and subsequent average Gain/Loss
    for (int i = interval + 1; i < items.size(); i++)
    {
      avgGains[i] = (avgGains[i - 1] * (interval - 1) + gains[i]) / interval;
      avgLosses[i] = (avgLosses[i - 1] * (interval - 1) + losses[i]) / interval;
    }
    printIt(avgGains);
    printIt(avgLosses);

    // RSI
    for (int i = interval; i < items.size(); i++)
    {
      rs[i] = avgGains[i] / avgLosses[i];
      rsi[i] = 100 - 100 / (1 + rs[i]);
    }
    printIt(rs);
    printIt(rsi);
    return rsi;
  }

  private static void printIt(double[] prices)
  {
    for (double d : prices)
    {
      System.out.print(Util.format(d) + " ");
    }
    System.out.println("\r\n");
  }
  
  public static void main(String[] args) {
    double[] closes = new double[] {
        44.34,
        44.09,
        44.15,
        43.61,
        44.33,
        44.83,
        45.10,
        45.42,
        45.84,
        46.08,
        45.89,
        46.03,
        45.61,
        46.28,
        46.28,
        46.00,
        46.03,
        46.41,
        46.22
};
    List<StockItem> items = new ArrayList<StockItem>();
    for (double d : closes) {
      StockItem si = new StockItem();
      si.setClose(d);
      items.add(si);
    }
    double[] result = calculateRsi(14, items);
  }

}
