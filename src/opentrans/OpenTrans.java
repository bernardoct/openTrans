/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package opentrans;

import BoundaryConditions.Problem;
import java.io.File;
import static Aux.Constants.SIMPLE_MOC;

/**
 *
 * @author bernardoct
 */
public class OpenTrans {

    
    /**
     * @param args the command line arguments
     * @throws java.lang.InterruptedException
     */
    public static void main(String[] args) throws InterruptedException, Exception {
        
        Problem p = new Problem(0.002, SIMPLE_MOC, 1000, new File("/yay"));
        p.calculate();
    }
    
}
