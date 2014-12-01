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
                outQ = pipesHQ[3];

        double Cp = calcCP(inH, inQ, B[1]);
        double Bp = calcBP(inQ, B[1], R[1]);
        double Cm = calcCM(outH, outQ, B[0]);
        double Bm = calcBM(outQ, B[0], R[0]);

        double opening = (timeRegime == TRANSIENT ? 
                controller.getNewSetValue(t) : controller.getNewSetValue(1e-10));        
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
