/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Pipe;

import java.util.Arrays;
import static Aux.MOCAux.*;
import static Aux.Constants.*;

/**
 *
 * @author Bernardo Carvalho Trindade - bct52@cornell.edu
 */
public class Pipe {

    public final double diameter,

    /**
     *
     */
    length,

    /**
     *
     */
    f,

    /**
     *
     */
    area,

    /**
     *
     */
    elevationUpstream,

    /**
     *
     */
    elevationDownstream;

    /**
     *
     */
    public final int model;

    /**
     *
     */
    public final int nNodes;

    /**
     *
     */
    public final int ID;
    public final double aws,

    /**
     *
     */
    dX;
    final double B;
    final double R;

    double averageHead = 0;
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
     * @param type
     */
    public Pipe(int ID, double diameter, double length, double aws, double f, double dt,
            double elevationUpstream, double elevationDownstream, int model, int type) {
        this.ID = ID;
        this.diameter = diameter;
        this.elevationUpstream = elevationUpstream;
        this.elevationDownstream = elevationDownstream;
        this.model = model;
        this.f = f;
        this.area = Math.PI * Math.pow(diameter, 2) / 4;

        if (type == TRANSIENT) {
            this.dX = dt * aws;
            this.aws = aws;
            this.nNodes = (int) (Math.ceil((length / dX + 1) / 2) * 2 - 1);
            this.length = (nNodes - 1) * dX;
            this.B = calcB(this.aws, area);
            this.R = calcR(f, dX, diameter, area);
        } else {
            this.dX = length / 2;
            this.aws = dX / DT_STEADY_STATE;
            this.nNodes = 3;
            this.length = (nNodes - 1) * dX;
            this.B = calcB(this.aws, area);
            this.R = calcR(f, dX, diameter, area);
        }

        this.H = new double[nNodes];
        this.Qin = new double[nNodes];
        this.Qout = new double[nNodes];
        this.voidSpace = new double[nNodes];
    }

    /**
     *
     * @param i0
     * @param timeRegime Either transient or steady state.
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

        // Calculate heads and flow rates for either steady state or transient
        // simulations.
        HQQu = calcMOC(H, Qin, B, R, nNodes, i0);

        // Update results.
        H = Arrays.copyOf(HQQu[0], nNodes);
        Qin = Arrays.copyOf(HQQu[1], nNodes);
        Qout = Arrays.copyOf(HQQu[2], nNodes);

        return new double[]{H[1], Qin[1], H[nNodes - 2], Qout[nNodes - 2]};
    }

    /**
     *
     * @param H
     * @param Q
     * @param i0
     * @return
     */
    private double[][] calcMOC(double[] H, double[] Q, double stateB,
            double stateR, int stateNNodes, int i0) {
        double CP, CM, BP, BM;
        //HQ[0] is head and HQ[1] is flow rate.
        double HQ[][] = new double[3][stateNNodes];

        HQ[0] = H;
        HQ[1] = Q;
        HQ[2] = Q;

        // MOC loop over the length
        for (int i = i0 + 1; i < stateNNodes - 1; i += 2) {
            CP = calcCP(H[i - 1], Q[i - 1], stateB);
            CM = calcCM(H[i + 1], Q[i + 1], stateB);
            BP = calcBP(Q[i - 1], stateB, stateR);
            BM = calcBM(Q[i + 1], stateB, stateR);
            HQ[0][i] = calcH(BM, BP, CM, CP);
            HQ[1][i] = calcQ(BM, BP, CM, CP);
            HQ[2][i] = HQ[1][i];
        }

        return HQ;
    }

    /**
     * Sets steady state for the pipe.
     *
     * @param steadyStateVariables Array with upstream head, downstream head,
     * and flow rate for steady state flow. Heads count from boundary condition
     * nodes.
     */
    public void setSteadyState(double[] steadyStateVariables) {
        double upstH = steadyStateVariables[0];
        double dwstH = steadyStateVariables[1];
        double pipeQ = steadyStateVariables[2];

        for (int i = 0; i < nNodes; i++) {
            H[i] = upstH - (upstH - dwstH) / (nNodes - 1) * i;
            Qin[i] = pipeQ;
            Qout[i] = pipeQ;
        }
    }
    
    /**
     *
     * @return
     */
    @Override
    public String toString() {
        return "ID\tHup\tHdw\tQ\tB\tR\tL\ta\tdx\tnNodes\n" + ID + "\t" + dFmt.format(H[0]) + "\t" + 
                dFmt.format(H[nNodes-1]) + "\t" + dFmt.format(Qin[1]) + 
                "\t" + dFmt.format(B) + "\t" + dFmt.format(R) + 
                 "\t" + dFmt.format(length)  + "\t" + dFmt.format(aws) + 
                "\t" + dFmt.format(dX) + "\t" + nNodes + "\n";
    }
    
    /**
     * 
     * @return Steady state values for heads in the extremities and flow rate.
     */
    public double[] getSteadyState() {
        return new double[] {H[0], H[2], Qin[1]};
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

    /**
     *
     * @return The flow rate in the first node for steady state convergence
     * verification.
     */
    public double getQ1() {
        return Qin[1];
    }
    
    /**
     * 
     * @return 
     */
    public double[] getH() {
        return H;
    }
    
    /**
     * 
     * @return 
     */
    public double[] getQ() {
        return Qin;
    }

}
