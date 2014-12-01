/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package Utils;

import static Aux.Constants.HEAD;
import Pipe.Pipe;
import java.io.BufferedWriter;
import java.io.File;
import java.util.ArrayList;

/**
 *
 * @author martins.tuga@gmail.com
 */
public class AsyncFileWriter extends Thread {

    private File file = null;
    private StringBuffer content = new StringBuffer();
    private double x = 0;
    private ArrayList<Pipe> pipesList;
    private int whatToPrint;

    private AsyncFileWriter(File filePath, ArrayList<Pipe> pipesTransient, int whatToPrint) {
        this.file = filePath;
        this.pipesList = pipesTransient;
        this.whatToPrint = whatToPrint;
    }

    /**
     *
     * @param filePath
     * @param content
     * @param H
     * @param dx
     */
    public static void write(File filePath, ArrayList<Pipe> pipesTransient, int whatToPrint) {
        AsyncFileWriter instance
                = new AsyncFileWriter(filePath, pipesTransient, whatToPrint);
        instance.start();
    }

    /**
     *
     */
    @Override
    public void run() {
        double[] arrayPrint;

        try {
            java.io.FileWriter fwFrame = new java.io.FileWriter(this.file);

//                  XYSeries seriesH = new XYSeries("Head");
//                  int i = 0;
//                  for (double h : H) {
//                        seriesH.add(h, i * dx);
//                  }
//
//                  XYSeriesCollection dataset = new XYSeriesCollection();
//                  dataset.addSeries(seriesH);
//
//                  JFreeChart chart = ChartFactory.createXYLineChart(
//                          "Line Chart Demo 2", // chart title
//                          "X", // x axis label
//                          "Y", // y axis label
//                          dataset, // data
//                          PlotOrientation.VERTICAL,
//                          true, // include legend
//                          true, // tooltips
//                          false // urls
//                          );
//
//                  ChartUtilities.saveChartAsPNG(new File(file.getPath() + ".png"), chart, 400, 300);
            for (Pipe p : pipesList) {
                
                if (whatToPrint == HEAD) {
                    arrayPrint = p.getH();
                } else {
                    arrayPrint = p.getQ();
                }
                
                for (double a : arrayPrint) {
                    content.append(Double.toString(x));
                    content.append("\t");
                    content.append(Double.toString(a));
                    content.append("\n");
                    x += p.dX;
                }
            }

            BufferedWriter bwFrame = new BufferedWriter(fwFrame);

            bwFrame.write(content.toString());
            bwFrame.flush();
            bwFrame.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
