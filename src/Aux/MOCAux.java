/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package Aux;

import static Aux.Constants.g;
import static java.lang.Math.pow;

/**
 * HAs a set of static functions to calculate the auxiliary variables of the
 * traditional MOC.
 *
 * @author Bernardo Trindade
 */
public class MOCAux {

    /**
     *
     * @param Hm Head upstream.
     * @param Qm Flow rate upstream.
     * @param B Pipe impedance.
     * @return Value of CP.
     */
    public static double calcCP(double Hm, double Qm, double B) {
        double CP = Hm + B * Qm;
        return CP;
    }

    /**
     *
     * @param Hp Head downstream.
     * @param Qp Flow rate downstream.
     * @param B Pipe impedance.
     * @return Value of CM.
     */
    public static double calcCM(double Hp, double Qp, double B) {
        double CM = Hp - B * Qp;
        return CM;
    }

    /**
     *
     * @param Qm Flow rate upstream.
     * @param B Pipe impedance.
     * @param R Pipe friction coefficient.
     * @return Value of BP.
     */
    public static double calcBP(double Qm, double B, double R) {
        double BP = B + R * Math.abs(Qm);
        return BP;
    }

    /**
     *
     * @param Qp Flow rate downstream.
     * @param B Pipe impedance.
     * @param R Pipe friction coefficient.
     * @return Value of BM.
     */
    public static double calcBM(double Qp, double B, double R) {
        double BM = B + R * Math.abs(Qp);
        return BM;
    }

    /**
     * 
     * @param BM
     * @param BP
     * @param CM
     * @param CP
     * @return 
     */
    public static double calcH(double BM, double BP, double CM, double CP) {
        double H = (CP * BM + CM * BP) / (BP + BM);
        return H;
    }

    /**
     * 
     * @param BM
     * @param BP
     * @param CM
     * @param CP
     * @return 
     */
    public static double calcQ(double BM, double BP, double CM, double CP) {
        double Q = (CP - CM) / (BP + BM);
        return Q;
    }
    
    /**
     * 
     * @param a
     * @param A
     * @return 
     */
    public static double calcB(double a, double A) {
        double B = a / (g * A);     
        return B;
    }
    
    /**
     * 
     * @param f
     * @param dX
     * @param D
     * @param A
     * @return 
     */
    public static double calcR(double f, double dX, double D, double A) {
        double R = f * dX / (2 * g * D * pow(A, 2));   
        return R;
    }
}
