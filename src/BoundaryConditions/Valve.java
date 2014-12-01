/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package BoundaryConditions;

import static Aux.Constants.TRANSIENT;
import static Aux.Constants.g;
import static Aux.MOCAux.calcBM;
import static Aux.MOCAux.calcBP;
import static Aux.MOCAux.calcCM;
import static Aux.MOCAux.calcCP;
import Controllers.Controller;
import Curves.GenericCurve;
import static java.lang.Math.pow;
import static java.lang.Math.sqrt;

/**
 *
 * @author Bernardo Carvalho Trindade - bct52@cornell.edu
 */
public class Valve extends BoundaryCondition {

    final Controller controller;
    final GenericCurve curve;
    final double area;

    /**
     *
     * @param ID
     * @param elevation
     * @param B
     * @param R
     * @param diameter
     * @param controller
     * @param curve
     * @param timeRegime
     */
    public Valve(int ID, double elevation, double[] B, double[] R,
            double diameter, Controller controller, GenericCurve curve, int timeRegime) {
        super(ID, elevation, B, R, timeRegime);
        this.controller = controller;
        this.curve = curve;
        this.area = Math.PI * diameter * diameter / 4;
    }

    @Override
    public double[] calculate(double[] pipesHQ, double t) {
        
        double inH = pipesHQ[0],
                inQ = -pipesHQ[1],
                outH = pipesHQ[2],
                outQ = pipesHQ[3],
                inB = B[0],
                inR = R[0],
                outB = B[1],
                outR = R[1];

        double Cp = calcCP(inH, inQ, inB);
        double Bp = calcBP(inQ, inB, inR);
        double Cm = calcCM(outH, outQ, outB);
        double Bm = calcBM(outQ, outB, outR);

        double opening = (timeRegime == TRANSIENT ? 
                controller.getNewSetValue(t) : controller.getNewSetValue(0));        
        double tauCd = curve.getValue(opening);
        
        double Cv = pow(tauCd * area, 2) * g;

        // This decides the flow direction.
        double S = (Cp - Cm > 0) ? 1 : -1;

        // Calculates flow rate based on characteristic equations.
        double Qp = -S * (Bp + Bm) * Cv + S * sqrt(pow((Bp + Bm) * Cv, 2)
                + 2 * S * Cv * (Cp - Cm));

        double upstH = Cp - Bp * Qp;
        double dnstH = Cm + Bm * Qp;

        Q = new double[]{Qp, Qp};
        H = (upstH + dnstH) / 2;

        return new double[]{upstH, -Qp, dnstH, Qp};

    }

}
