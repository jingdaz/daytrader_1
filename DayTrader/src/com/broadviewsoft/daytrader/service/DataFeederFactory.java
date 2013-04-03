package com.broadviewsoft.daytrader.service;

public class DataFeederFactory
{
  private static MockDataFeeder feeder = null;

  public static synchronized MockDataFeeder newInstance() {
    if (feeder == null) {
      feeder = new MockDataFeeder();
    }
    return feeder;
  }

}
 
