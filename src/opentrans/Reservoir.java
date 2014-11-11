/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package opentrans;

import static opentrans.MOCAux.*;

/**
 *
 * @author bernardoct
 */
public class Reservoir extends BoundaryCondition{

    double plantArea;

    /**
     * 
     * @param plantArea
     * @param H
     * @param elevation
     * @param Bus
     * @param Rus 
     */
    public Reservoir(double plantArea, double H, double elevation, double Bus, 
            double Rus) {
        this.plantArea = plantArea;
        
        B = new double[1];
        R = new double[1];
        
        B[0] = Bus;
        R[0] = Rus;
    }

    @Override
    public void calculate(double[] pipeH, double[] pipeQ) {
        double Cm = calcCM(pipeH[0], pipeQ[0], B[0]);
        double Bm = calcBM(pipeQ[0], B[0], R[0]);

        Q[0] = (getH() - Cm) / Bm;
    }

}
