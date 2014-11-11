/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package opentrans;

/**
 *
 * @author bernardoct
 */
public abstract class BoundaryCondition {
    private double H;
    
    double[] Q;
    double[] B;
    double[] R;
    
    /**
     * 
     * @param pipeH
     * @param pipeQ 
     */
    public abstract void calculate(double[] pipeH, double[] pipeQ);

    /**
     * @return the H
     */
    public double getH() {
        return H;
    }
    
    
}
