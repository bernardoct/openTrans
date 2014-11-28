/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package BoundaryConditions;

/**
 *
 * @author bernardoct
 */
public abstract class BoundaryCondition {
    protected double H;
    protected final double elevation;
    
    protected double[] Q, B, R;

    /**
     *
     */
    public final int ID;

    /**
     *
     * @param ID
     * @param elevation
     * @param R
     * @param B
     */
    public BoundaryCondition(int ID, double elevation, double[] B, double[] R) {
        this.ID = ID;
        this.elevation = elevation;
        this.B = B;
        this.R = R;
    } 
    
    
    /**
     * 
     * @param pipesHQ
     * @return 
     */
    public abstract double[] calculate(double[] pipesHQ);

    /**
     * @return the H
     */
    public double getH() {
        return H;
    }

    /**
     * @return the Q
     */
    public double[] getQ() {
        return Q;
    }
    
    
    
    
}
