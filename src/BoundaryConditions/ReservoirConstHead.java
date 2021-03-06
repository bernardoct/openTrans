/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package BoundaryConditions;

import static Aux.MOCAux.*;

/**
 *
 * @author Bernardo Carvalho Trindade - bct52@cornell.edu
 */
public class ReservoirConstHead extends BoundaryCondition {

    double plantArea;

    /**
     *
     * @param ID Boundary condition ID.
     * @param plantArea Plant area of the reservoir
     * @param H Reservoir head
     * @param elevation Reservoir elevation
     * @param B
     * @param R
     * @param timeRegime
     */
    public ReservoirConstHead(int ID, double plantArea, double H, double elevation, double[] B,
            double[] R, int timeRegime) {
        super(ID, elevation, B, R, timeRegime);
        this.plantArea = plantArea;

        this.H = H;
        Q = new double[1];
    }

    @Override
    public double[] calculate(double[] pipesHQ, double t) {

        double Cm = calcCM(pipesHQ[0], pipesHQ[1], B[0]);
        double Bm = calcBM(pipesHQ[1], B[0], R[0]);

        Q[0] = (H - Cm) / Bm;

        return new double[]{H, Q[0]};
    }
}
