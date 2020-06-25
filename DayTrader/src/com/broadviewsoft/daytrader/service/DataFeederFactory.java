package com.broadviewsoft.daytrader.service;

/**
 * Singleton class as data feeder factory
 * 
 * @author Jason Zhang
 *
 */
public class DataFeederFactory
{
  private static IDataFeeder feeder = null;

  public static synchronized IDataFeeder newInstance() {
    if (feeder == null) {
      feeder = new MockDataFeeder();
    }
    return feeder;
  }

}
 
