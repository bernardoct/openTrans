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
public class GenericCurve extends Curve{

    public GenericCurve(int ID, double[] values, double[] range) {
        super(ID, values, range);
    }

    @Override
    public double getValue(double v) {
        return mapValue(v);
    }
    
    
    
}
