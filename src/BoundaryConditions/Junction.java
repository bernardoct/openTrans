/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package BoundaryConditions;

import static Aux.MOCAux.calcBP;
import static Aux.MOCAux.calcCP;

/**
 *
 * @author bernardoct
 */
public class Junction extends BoundaryConditions.BoundaryCondition {

    /**
     *
     * @param ID
     * @param elevation
     * @param B
     * @param R
     */
    public Junction(int ID, double elevation, double[] B, double[] R) {
        super(ID, elevation, B, R);
    }

    @Override
    public double[] calculate(double[] pipesHQ) {

        double SB = 0, SC = 0, pipeQ, pipeH;
        double[] CP = new double[10], BP = new double[10];
        double[] bcHQ = new double[pipesHQ.length];
        double Q = 0;

        for (int i = 0; i < pipesHQ.length / 2; i += 2) {
            pipeH = pipesHQ[2 * i];
            if (i == 0) {
                pipeQ = -pipesHQ[2 * i + 1];
            } else {
                pipeQ = pipesHQ[2 * i + 1];
            }

            CP[i] = calcCP(pipeH, pipeQ, B[i]);
            BP[i] = calcBP(pipeQ, B[i], R[i]);

            SB += 1 / BP[i];
            SC += CP[i] / BP[i];
            i++;
        }

        H = SC / SB;

        bcHQ[0] = H;
        for (int i = 0; i < pipesHQ.length / 2; i++) {

            Q = -H / BP[i] + CP[i] / BP[i];

            // Return the orientation of the first flow rate to its original.
            Q = (i == 1? -Q: Q);
            
            bcHQ[i + 1] = Q;
        }
        
        return bcHQ;
    }

}
