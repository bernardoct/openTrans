/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Pipe;

import java.io.IOException;
import java.util.Arrays;
import static Aux.MOCAux.*;
import static Aux.Constants.*;

/**
 *
 * @author bernardoct
 */
public class Pipe {

    final double diameter, length, aws, f, dX, dt, B, R;
    final double area, elevationUpstream, elevationDownstream;
    final int nNodes, model;

    /**
     *
     */
    public final int ID;
    int i0 = 0;
    double[] H, Qin, Qout, voidSpace;

    /**
     *
     * @param ID
     * @param diameter
     * @param length
     * @param aws
     * @param f
     * @param dt
     * @param elevationUpstream
     * @param elevationDownstream
     * @param model
     */
    public Pipe(int ID, double diameter, double length, double aws, double f, double dt,
            double elevationUpstream, double elevationDownstream, int model) {
        this.ID = ID;
        this.diameter = diameter;
        this.length = length;
        this.aws = aws;
        this.f = f;
        this.dt = dt;
        this.elevationUpstream = elevationUpstream;
        this.elevationDownstream = elevationDownstream;
        this.model = model;

        this.dX = dt * aws;
        this.nNodes = (int) (Math.ceil((length / dX + 1) / 2) * 2 - 1);
        this.H = new double[nNodes];
        this.Qin = new double[nNodes];
        this.Qout = new double[nNodes];
        this.voidSpace = new double[nNodes];
        this.area = Math.pow(diameter, 2) * diameter / 4;
        this.B = calcB(aws, area);
        this.R = calcR(f, dX, diameter, area);
    }

    /**
     *
     * @param i0
     * @param HQBcs Vector with upstream head, upstream flow rate, downstream
     * head, and downstream flow rate, respectively.
     * @return Vector with calculated upstream head, upstream flow rate, 
     * downstream head, and downstream flow rate, respectively.
     * @throws Exception
     */
    public double[] calculate(int i0, double[] HQBcs) throws Exception {
        double[][] HQQu;

        // Set up boundary conditions.
        H[0] = HQBcs[0];
        Qin[0] = HQBcs[1];
        H[nNodes - 1] = HQBcs[2];
        Qout[nNodes - 1] = HQBcs[3];

        if (model == SIMPLE_MOC) {
            Qin[nNodes - 1] = HQBcs[3];
        } else {
            throw new UnsupportedOperationException("DGCM not supported yet.");
        }

        // Calculate heads and flow rates.
        HQQu = calcMOC(H, Qin, i0);

        // Update results.
        H = Arrays.copyOf(HQQu[0], nNodes);
        Qin = Arrays.copyOf(HQQu[1], nNodes);
        Qout = Arrays.copyOf(HQQu[2], nNodes);
        
        return new double[] {H[1], Qin[1], H[nNodes - 2], Qout[nNodes - 2]};
    }

    /**
     *
     * @param H
     * @param Q
     * @param i0
     * @return
     */
    private double[][] calcMOC(double[] H, double[] Q, int i0) {
        double CP, CM, BP, BM;
        //HQ[0] is head and HQ[1] is flow rate.
        double HQ[][] = new double[3][nNodes];

        HQ[0] = H;
        HQ[1] = Q;
        HQ[2] = Q;

        // MOC loop over the length
        for (int i = i0 + 1; i < nNodes - 1; i += 2) {
            CP = calcCP(H[i - 1], Q[i - 1], getB());
            CM = calcCM(H[i + 1], Q[i + 1], getB());
            BP = calcBP(Q[i - 1], getB(), getR());
            BM = calcBM(Q[i + 1], getB(), getR());
            HQ[0][i] = calcH(BM, BP, CM, CP);
            HQ[1][i] = calcQ(BM, BP, CM, CP);
            HQ[2][i] = HQ[1][i];
        }

        return HQ;
    }

    /**
     *
     * @return
     */
    public double getQus() {
        return Qin[1];
    }

    /**
     *
     * @return
     */
    public double getQds() {
        if (model == SIMPLE_MOC) {
            return Qin[nNodes - 2];
        } else {
            return Qout[nNodes - 2];
        }
    }

    /**
     *
     * @return
     */
    public double getHus() {
        return H[1];
    }

    /**
     *
     * @return
     */
    public double getHds() {
        return H[nNodes - 2];
    }

    /**
     * @return the B
     */
    public double getB() {
        return B;
    }

    /**
     * @return the R
     */
    public double getR() {
        return R;
    }

}
