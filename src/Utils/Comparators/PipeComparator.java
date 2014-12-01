/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Utils.Comparators;

import Exceptions.ElementsWithSameIDException;
import java.util.Comparator;
import java.util.logging.Level;
import java.util.logging.Logger;
import Pipe.Pipe;

/**
 *
 * @author Bernardo Carvalho Trindade - bct52@cornell.edu
 */
public class PipeComparator implements Comparator<Pipe> {

    /**
     *
     * @param t
     * @param t1
     * @return
     */
    @Override
    public int compare(Pipe t, Pipe t1) {
        if (t.ID < t1.ID) {
            return -1;
        } else if (t.ID > t1.ID) {
            return 1;
        } else {
            try {
                throw new ElementsWithSameIDException("There are two pipes "
                        + "with ID = " + t.ID);
            } catch (ElementsWithSameIDException ex) {
                Logger.getLogger(PipeComparator.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        return 0;
    }
    
}
