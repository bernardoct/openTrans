/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package Utils.Output;

import static Aux.Constants.HEAD;
import Pipe.Pipe;
import java.io.BufferedWriter;
import java.io.File;
import java.util.ArrayList;

/**
 *
 * @author Paulo Canais Martin - martins.tuga@gmail.com
 */
public class AsyncFileWriter extends Thread {

    private File file = null;
    private final StringBuffer content = new StringBuffer();
    private double x = 0;
    private final ArrayList<Pipe> pipesList;
    private final int whatToPrint;

    private AsyncFileWriter(File filePath, ArrayList<Pipe> pipesTransient, int whatToPrint) {
        this.file = filePath;
        this.pipesList = pipesTransient;
        this.whatToPrint = whatToPrint;
    }

    /**
     *
     * @param filePath
     * @param pipesTransient
     * @param whatToPrint
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
