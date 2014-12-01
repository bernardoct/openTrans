/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Curves;

/**
 *
 * @author Bernardo Carvalho Trindade - bct52@cornell.edu
 */
public abstract class Curve {

    /**
     *
     */
    public final int ID;
    double[] values;
    double[] keys;

    /**
     *
     * @param ID
     * @param values
     * @param keys
     */
    public Curve(int ID, double[] values, double[] keys) {
        this.ID = ID;
        this.values = values;
        this.keys = keys;
    }

    /**
     *
     * @param v
     * @return
     */
    public double mapValue(double v) {
        int i = 1;
        while (v < keys[i - 1] && i < keys.length - 1) {
            i++;
        }

        if (v > keys[keys.length - 1]) {
            return values[values.length - 1];
        } else if (v < keys[0]) {
            return values[0];
        } else {
            return (v - keys[i - 1]) * (values[i] - values[i - 1])
                    / (keys[i] - keys[i - 1]) + values[i - 1];
        }
    }

    /**
     *
     * @param v
     * @return
     */
    public abstract double getValue(double v);

}
