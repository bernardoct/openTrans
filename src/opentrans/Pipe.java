/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package opentrans;

import java.io.IOException;
import java.util.Arrays;
import static opentrans.MOCAux.*;
import static opentrans.Constants.*;

/**
 *
 * @author bernardoct
 */
public class Pipe {

    final double diameter, length, aws, f, dX, dt;
    private final double B;
    private final double R;
    final double  area, elevationUpstream, elevationDownstream;
    final int nNodes, model;
    int i0 = 0;
    double[] H, Qin, Qout, voidSpace;

    /**
     * 
     * @param diameter
     * @param length
     * @param aws
     * @param f
     * @param dt
     * @param elevationUpstream
     * @param elevationDownstream
     * @param model 
     */
    public Pipe(double diameter, double length, double aws, double f, double dt,   
            double elevationUpstream, double elevationDownstream, int model) {
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
     * @param Hu
     * @param Hd
     * @param Qu
     * @param Qd
     * @throws IOException 
     */
    public void calculate(int i0, double Hu, double Hd, double Qu, double Qd) throws IOException {
        double[][] HQ;

        // Set up boundary conditions.
        H[0] = Hu;
        H[nNodes - 1] = Hd;
        Qin[0] = Qu;

        if (model == SIMPLE_MOC) {
            Qin[nNodes - 1] = Qd;
        } else {
            Qout[nNodes - 1] = Qd;
        }

        // Calculate heads and flow rates.
        HQ = calcMOC(H, Qin, i0);

        // Update results.
        H = Arrays.copyOf(HQ[0], nNodes);
        Qin = Arrays.copyOf(HQ[1], nNodes);
    }

    /**
     * 
     * @param H
     * @param Q
     * @param i0
     * @return 
     */
    public double[][] calcMOC(double[] H, double[] Q, int i0) {
        double CP, CM, BP, BM;
        int nNodess = H.length;
        //HQ[0] is head and HQ[1] is flow rate.
        double HQ[][] = new double[2][nNodess];

        HQ[0] = H;
        HQ[1] = Q;

        // MOC loop over the length
        for (int i = i0 + 1; i < nNodes - 1; i += 2) {
            CP = calcCP(H[i - 1], Q[i - 1], getB());
            CM = calcCM(H[i + 1], Q[i + 1], getB());
            BP = calcBP(Q[i - 1], getB(), getR());
            BM = calcBM(Q[i + 1], getB(), getR());
            HQ[0][i] = calcH(BM, BP, CM, CP);
            HQ[1][i] = calcQ(BM, BP, CM, CP);
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
