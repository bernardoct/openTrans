/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package BoundaryConditions;

import static Aux.Constants.dFmt;

/**
 *
 * @author bernardoct
 */
public abstract class BoundaryCondition {

    protected double H;
    protected final double elevation;

    protected double[] Q;
    protected double[] B;
    protected double[] R;

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
        Q = new double[B.length];
    }

    /**
     *
     * @param pipesHQ
     * @return
     */
    public abstract double[] calculate(double[] pipesHQ);

    @Override
    public String toString() {
        String str = "H\tQ\tB\tR\n" + dFmt.format(H) + "\t";

        for (int i = 0; i < Q.length; i++) {
            str += dFmt.format(Q[i]) + "\t";
        }

        for (int i = 0; i < B.length; i++) {
            str += dFmt.format(B[i]) + "\t";
        }

        for (int i = 0; i < R.length; i++) {
            str += dFmt.format(R[i]) + "\t";
        }

        str += "\n";

        return str;
    }

    /**
     * @return the B
     */
    public double[] getB() {
        return B;
    }

    /**
     * @return the R
     */
    public double[] getR() {
        return R;
    }

    /**
     * @return the H
     */
    public double getH() {
        return H;
    }

    /**
     * @param H the H to set
     */
    public void setH(double H) {
        this.H = H;
    }

    /**
     * @return the Q
     */
    public double[] getQ() {
        return Q;
    }

    /**
     * @param Q the Q to set
     */
    public void setQ(double[] Q) {
        this.Q = Q;
    }

}
