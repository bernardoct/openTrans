/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package BoundaryConditions;

import static Aux.MOCAux.*;

/**
 *
 * @author bernardoct
 */
public class Reservoir extends BoundaryCondition {

    double plantArea;

    /**
     *
     * @param ID Boundary condition ID.
     * @param plantArea Plant area of the reservoir
     * @param H Reservoir head
     * @param elevation Reservoir elevation
     * @param B
     * @param R
     */
    public Reservoir(int ID, double plantArea, double H, double elevation, double[] B,
            double[] R) {
        super(ID, elevation, B, R);
        this.plantArea = plantArea;

        this.H = H;
        Q = new double[1];
    }

    @Override
    public double[] calculate(double[] pipesHQ) {

        double Cm = calcCM(pipesHQ[0], pipesHQ[1], getB()[0]);
        double Bm = calcBM(pipesHQ[1], getB()[0], getR()[0]);

        getQ()[0] = (getH() - Cm) / Bm;

        return new double[]{getH(), getQ()[0]};
    }
}
