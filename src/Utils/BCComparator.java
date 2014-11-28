/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Utils;

import Exceptions.ElementsWithSameIDException;
import java.util.Comparator;
import java.util.logging.Level;
import java.util.logging.Logger;
import BoundaryConditions.BoundaryCondition;

/**
 *
 * @author bernardoct
 */
public class BCComparator implements Comparator<BoundaryCondition> {

    /**
     *
     * @param t
     * @param t1
     * @return
     */
    @Override
    public int compare(BoundaryCondition t, BoundaryCondition t1) {
        if (t.ID < t1.ID) {
            return -1;
        } else if (t.ID > t1.ID) {
            return 1;
        } else {
            try {
                throw new ElementsWithSameIDException("There are two boundary "
                        + "conditions with ID = " + t.ID);
            } catch (ElementsWithSameIDException ex) {
                Logger.getLogger(BCComparator.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        return 0;
    }
    
}
