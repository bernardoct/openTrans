/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Utils.Comparators;

import java.util.Comparator;

/**
 *
 * @author bernardoct
 */
public class PipeConnectivityComparator implements Comparator<int[]> {

    /**
     *
     * @param t
     * @param t1
     * @return
     */
    @Override
    public int compare(int[] t, int[] t1) {
        if (t[0] < t1[0]) {
            return -1;
        } else {
            return 1;
        } 

    }
}
