/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package Aux;

import java.text.DecimalFormat;

/**
 *
 * @author Bernardo Carvalho Trindade - bct52@cornell.edu
 */
public class Constants {
    /**
     *
     */
    public static final int DOWNSTREAM = -1;
    /**
     *
     */
    public static final int UPSTREAM = 1;
    /**
     * 
     */
    public static final int NOT_CONNECTED = 0;
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
      public static final double DT_STEADY_STATE = 0.1;
      /**
       * 
       */
      public static final double CONVERGENCY_HEAD_THRESHOLD = 1-1e-5;
      /**
       * 
       */
      public static final int CONVERGED = 1;
      /**
       * 
       */
      public static final int STILL_HAVE_NOT_CONVERGED = 0;
      /**
       * 
       */
      public static final int HEAD = 0;
      /**
       * 
       */
      public static final int FLOW_RATE = 1;
      /**
       * 
       */
      public static final int CONVERGENCY_CHECK = 20;

}
