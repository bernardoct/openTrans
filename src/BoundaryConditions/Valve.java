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
public class Valve extends BoundaryCondition {

    public Valve(int ID, double elevation, double[] B, double[] R) {
        super(ID, elevation, B, R);
    }

    @Override
    public double[] calculate(double[] pipesHQ) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }
    
    
}
