/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Controllers;

import Curves.Curve;

/**
 *
 * @author Bernardo Carvalho Trindade - bct52@cornell.edu
 */
public class SetTimesController extends Controller {
     
    final Curve timeCurve; 

    public SetTimesController(int ID, Curve timeCurve) {
        super(ID);
        this.timeCurve = timeCurve;
    }
    
    

    @Override
    public double getNewSetValue(double t) {
        return timeCurve.mapValue(t);
    }
    
    
    
}
