/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package opentrans;

import BoundaryConditions.Problem;
import static Aux.Constants.SIMPLE_MOC;
import static Aux.Constants.STEADY_STATE;
import static Aux.Constants.TRANSIENT;

/**
 *
 * @author Bernardo Carvalho Trindade - bct52@cornell.edu
 */
public class OpenTrans {

    
    /**
     * @param args the command line arguments
     * @throws java.lang.InterruptedException
     */
    public static void main(String[] args) throws InterruptedException, Exception {
        System.out.println(args[0] + "\n");
        Problem p = new Problem(0.002, SIMPLE_MOC, 1000, args[0]);
        p.calculate(STEADY_STATE);
        p.calculate(TRANSIENT);
    }
    
}
