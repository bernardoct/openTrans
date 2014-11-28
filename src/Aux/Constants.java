/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package Aux;

import java.text.DecimalFormat;

/**
 *
 * @author bernardoct
 */
public class Constants {
    /**
     *
     */
    public static final int DOWNSTREAM = 1;
    /**
     *
     */
    public static final int UPSTREAM = -1;
    /**
     *
     */
    public static final double g = 9.806;
      /**
       *
       */
      public static final DecimalFormat dFmt = new DecimalFormat("0.000");
      /**
       * 
       */
      public static final int SIMPLE_MOC = 0;
      /**
       * 
       */
      public static final int DGCM = 1;
      /**
       * 
       */
      public static final int STEADY_STATE = 0;
      /**
       * 
       */
      public static final int TRANSIENT = 1;
      /**
      *
      */
      public static final double DT_STEADY_STATE = 1;
      /**
       * 
       */
      public static final double CONVERGENCY_HEAD_THRESHOLD = 1-1e-6;
      /**
       * 
       */
      public static final int CONVERGED = 1;
      /**
       * 
       */
      public static final int STILL_HAVE_NOT_CONVERGED = 0;

}
