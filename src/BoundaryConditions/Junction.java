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
 * @author Bernardo Carvalho Trindade - bct52@cornell.edu
 */
public class Junction extends BoundaryConditions.BoundaryCondition {

    /**
     *
     * @param ID
     * @param elevation
     * @param B
     * @param R
     * @param timeRegime
     */
    public Junction(int ID, double elevation, double[] B, double[] R, int timeRegime) {
        super(ID, elevation, B, R, timeRegime);
    }

    @Override
    public double[] calculate(double[] pipesHQ, double t) {

        double SB = 0, SC = 0, pipeQ, pipeH;
        double[] CP = new double[pipesHQ.length / 2], 
                BP = new double[pipesHQ.length / 2];
        double[] bcHQ = new double[pipesHQ.length];

        for (int i = 0; i < pipesHQ.length / 2; i++) {
            pipeH = pipesHQ[2 * i];            
            pipeQ = -pipesHQ[2 * i + 1];

            CP[i] = calcCP(pipeH, pipeQ, B[i]);
            BP[i] = calcBP(pipeQ, B[i], R[i]);

            SB += 1 / BP[i];
            SC += CP[i] / BP[i];
        }

        setH(SC / SB);
        
        for (int i = 0; i < B.length; i++) {

            Q[i] = -H / BP[i] + CP[i] / BP[i];

            // Return the orientation of the first flow rate to its original.
            bcHQ[2 * i] = H; 
            bcHQ[2 * i + 1] = -Q[i];
        }
        
        return bcHQ;
    }

}
