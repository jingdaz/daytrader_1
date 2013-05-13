package com.broadviewsoft.daytrader.service;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.RenderingHints;
import java.awt.Stroke;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import javax.swing.*;

import com.broadviewsoft.daytrader.domain.Constants;
import com.broadviewsoft.daytrader.domain.Period;
import com.broadviewsoft.daytrader.domain.StockItem;

public class StockChart extends JPanel {
  private static final int MAX_ITEMS = 181;
  private static final int PREF_W = 940;
  private static final int PREF_H = 1060;
  
  private static final int BORDER_GAP = 20;
  
  private static final int RSI_Y_TOP = 20;
  private static final int RSI_Y_BOT = 220;
  
  private static final int PRICE_Y_TOP = 230;
  private static final int PRICE_Y_BOT = 430;
  
  private static final int CCI_Y_TOP = 440;
  private static final int CCI_Y_BOT = 1040;
  
  private static final Color GRAPH_COLOR = Color.green;
  private static final Color GRAPH_POINT_COLOR = new Color(150, 50, 50, 180);
  private static final Stroke GRAPH_STROKE = new BasicStroke(2f);
  private static final int GRAPH_POINT_WIDTH = 12;
  private static final int Y_HATCH_CNT = 10;
  private List<StockItem> items;

  public StockChart(List<StockItem> items) {
     this.items = items;
  }

  @Override
  protected void paintComponent(Graphics g) {
     super.paintComponent(g);
     Graphics2D g2 = (Graphics2D)g;
     g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

     paintRsiPart(g2);
     paintPricePart(g2);
     paintCciPart(g2);
     

     // and for x axis
     for (int i = 0; i < items.size() - 1; i++) {
        int x0 = (i + 1) * (getWidth() - BORDER_GAP * 2) / (items.size() - 1) + BORDER_GAP;
        int x1 = x0;
        int y0 = getHeight() - BORDER_GAP;
        int y1 = y0 - GRAPH_POINT_WIDTH;
        g2.drawLine(x0, y0, x1, y1);
     }

//     g2.setStroke(oldStroke);      
     g2.setColor(GRAPH_POINT_COLOR);
//     for (int i = 0; i < graphPoints.size(); i++) {
//        int x = graphPoints.get(i).x - GRAPH_POINT_WIDTH / 2;
//        int y = graphPoints.get(i).y - GRAPH_POINT_WIDTH / 2;;
//        int ovalW = GRAPH_POINT_WIDTH;
//        int ovalH = GRAPH_POINT_WIDTH;
////        g2.fillOval(x, y, ovalW, ovalH);
//     }
  }

  private void paintRsiPart(Graphics2D g2) {
    int[] rsis = transitRSI(items, BORDER_GAP, (RSI_Y_TOP+RSI_Y_BOT)/2);
  }
  
  private void paintPricePart(Graphics2D g2) {
    int[] ccis = transitPrice(items, BORDER_GAP, (PRICE_Y_TOP+PRICE_Y_BOT)/2);
    
    // create hatch marks for y axis. 
    for (int i = 0; i < Y_HATCH_CNT; i++) {
       int x0 = BORDER_GAP;
       int x1 = GRAPH_POINT_WIDTH + BORDER_GAP;
       int y0 = getHeight() - (((i + 1) * (getHeight() - BORDER_GAP * 2)) / Y_HATCH_CNT + BORDER_GAP);
       int y1 = y0;
       g2.drawLine(x0, y0, x1, y1);
    }
    
  }

  private void paintCciPart(Graphics2D g2) {
    int xScale =  (getWidth() - 2 * BORDER_GAP) / (MAX_ITEMS - 1);

    int[] ccis = transitCCI(items, BORDER_GAP, (CCI_Y_TOP+CCI_Y_BOT)/2);
    
    List<Point> graphPoints = new ArrayList<Point>();
    for (int i = 0; i < items.size(); i++) {
       int x1 = (i * xScale + BORDER_GAP);
       int y1 = ccis[i];
       graphPoints.add(new Point(x1, y1));
    }

    Stroke oldStroke = g2.getStroke();
    g2.setColor(GRAPH_COLOR);
    g2.setStroke(GRAPH_STROKE);
    for (int i = 0; i < graphPoints.size() - 1; i++) {
       int x1 = graphPoints.get(i).x;
       int y1 = graphPoints.get(i).y;
       int x2 = graphPoints.get(i + 1).x;
       int y2 = graphPoints.get(i + 1).y;
       g2.drawLine(x1, y1, x2, y2);         
    }

    // create x axes 
    g2.drawLine(BORDER_GAP, (CCI_Y_TOP+CCI_Y_BOT)/2, getWidth() - BORDER_GAP, (CCI_Y_TOP+CCI_Y_BOT)/2);
    // create y axes
    g2.drawLine(BORDER_GAP, getHeight() - BORDER_GAP, BORDER_GAP, BORDER_GAP);

    // create hatch marks for y axis. 
    for (int i = 0; i < Y_HATCH_CNT; i++) {
       int x0 = BORDER_GAP;
       int x1 = GRAPH_POINT_WIDTH + BORDER_GAP;
       int y0 = getHeight() - (((i + 1) * (getHeight() - BORDER_GAP * 2)) / Y_HATCH_CNT + BORDER_GAP);
       int y1 = y0;
       g2.drawLine(x0, y0, x1, y1);
    }
  }
  
  @Override
  public Dimension getPreferredSize() {
     return new Dimension(PREF_W, PREF_H);
  }

  public static void createAndShowGui(List<StockItem> items) {
     StockChart mainPanel = new StockChart(items);

     JFrame frame = new JFrame("DrawGraph");
     frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
     frame.getContentPane().add(mainPanel);
     frame.pack();
     frame.setLocationByPlatform(true);
     frame.setVisible(true);
  }

  private int[] transitRSI(List<StockItem> items, int orgx, int orgy) {
    double rsiRange = (Constants.RSI_MAX_LIMIT - Constants.RSI_MIN_LIMIT)/2.0;
    double rsiScale = (RSI_Y_BOT - RSI_Y_TOP)/2.0;
    
	int[] result = new int[items.size()];
	for (int i=0; i<items.size(); i++) {
		result[i] = (int) (orgy - items.get(i).getRsi()/rsiRange*rsiScale);
	}
	return result;
  }
  
  private int[] transitPrice(List<StockItem> items, int orgx, int orgy) {
    double cciRange = (Constants.CCI_MAX_LIMIT - Constants.CCI_MIN_LIMIT)/2.0;
    double cciScale = (CCI_Y_BOT - CCI_Y_TOP)/2.0;
    
  int[] result = new int[items.size()];
  for (int i=0; i<items.size(); i++) {
    result[i] = (int) (orgy - items.get(i).getCci()/cciRange*cciScale);
  }
  return result;
  }

  private int[] transitCCI(List<StockItem> items, int orgx, int orgy) {
    double cciRange = (Constants.CCI_MAX_LIMIT - Constants.CCI_MIN_LIMIT)/2.0;
    double cciScale = (CCI_Y_BOT - CCI_Y_TOP)/2.0;
    
  int[] result = new int[items.size()];
  for (int i=0; i<items.size(); i++) {
    result[i] = (int) (orgy - items.get(i).getCci()/cciRange*cciScale);
  }
  return result;
  }

  public static void main(String[] args) throws ParseException {
	  Date curTime = Constants.TRADE_DATE_FORMATTER.parse("04/19/2013");
	  IDataFeeder dataFeeder = DataFeederFactory.newInstance();
	  final List<StockItem> items = dataFeeder.getHistoryData("UVXY", Period.MIN5, curTime);
     SwingUtilities.invokeLater(new Runnable() {
        public void run() {
           createAndShowGui(items);
        }
     });
  }

}
 
