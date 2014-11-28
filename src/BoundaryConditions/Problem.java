/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package BoundaryConditions;

import static Aux.Constants.DOWNSTREAM;
import static Aux.Constants.UPSTREAM;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import Pipe.Pipe;
import Utils.BCComparator;
import Utils.PipeComparator;
import static Utils.InputParser.*;

/**
 *
 * @author bernardoct
 */
public class Problem {

    private static double t = 0;
    public final double dt,
            /**
             *
             */
            /**
             *
             */

    /**
     *
     */
    simulationTime;

    /**
     *
     */
    public final int model;
    int[][] linkTable;

    ArrayList<Pipe> pipes = new ArrayList<>();
    ArrayList<BoundaryCondition> boundaryConditions = new ArrayList<>();
    ArrayList<double[]> boundaryQ = new ArrayList<>();
    double[] boundaryH;

    /**
     *
     * @param dt
     * @param model
     * @param simulationTime
     * @param inputFile
     */
    public Problem(double dt, int model, double simulationTime, File inputFile) {
        ArrayList parsedInput = parse();

        this.dt = dt;
        this.model = model;
        this.simulationTime = simulationTime;

        pipes = (ArrayList<Pipe>) parsedInput.get(0);
        boundaryConditions = (ArrayList<BoundaryCondition>) parsedInput.get(1);
        linkTable = (int[][]) parsedInput.get(2);

        Collections.sort(pipes, new PipeComparator());
        Collections.sort(boundaryConditions, new BCComparator());

    }

    /**
     *
     * @return @throws IOException
     */
    public int calculate() throws Exception {
        int nTimeSteps = (int) Math.ceil(simulationTime / dt);
        int[] bcsPipe = new int[]{-1, -1};
        int[] pipesBc;
        int i0 = 0, nPipes = 0;
        double[] HQ, HQPipeInput, HQBcInput;

        double[][] tableQpipes = new double[linkTable.length][linkTable[0].length];
        double[][] tableHpipes = new double[linkTable.length][linkTable[0].length];
        double[][] tableQBcs = new double[linkTable.length][linkTable[0].length];
        double[][] tableHBcs = new double[linkTable.length][linkTable[0].length];

        for (int tn = 0; tn < nTimeSteps; tn++) {

            // Finds the boundary conditions assigned to a pipe, gets their Q's
            // and H's, and calculates the flows in the pipes.
            for (int i = 0; i < linkTable.length; i++) {

                for (int j = 0; j < linkTable[0].length; j++) {
                    if (linkTable[i][j] > 0) {
                        bcsPipe[0] = j;
                    } else if (linkTable[i][j] < 0) {
                        bcsPipe[1] = j;
                    }
                }

                // Throw exception in caso less than two boundary conditions
                // are found for a pipe.
                if (bcsPipe[0] == -1 || bcsPipe[1] == -1) {
                    throw new Exception("Pipe " + i + "does not have two "
                            + "boundary conditions attached to it.");
                }

                // Gather heads and flow rate into a vector to input in the 
                // pipe class.
                HQPipeInput = new double[]{
                    tableHBcs[i][bcsPipe[0]],
                    tableQBcs[i][bcsPipe[0]],
                    tableHBcs[i][bcsPipe[1]],
                    tableQBcs[i][bcsPipe[1]]};

                // Calculate flow in the pipe.
                HQ = pipes.get(i).calculate(i0, HQPipeInput);

                // Update tables with pipe upstream and downstream 
                // heads and flow rates.
                tableHpipes[i][bcsPipe[0]] = HQ[0];
                tableQpipes[i][bcsPipe[0]] = HQ[1];
                tableHpipes[i][bcsPipe[1]] = HQ[2];
                tableQpipes[i][bcsPipe[1]] = HQ[3];

                HQ = null;
            }

            if (i0 == 1) {
            // Finds the pipes assigned to boundary condition, gets their Q's
                // and H's, and calculates the flow through he boundary condition.
                int k;

            // Build boundary condition (BC) input array, in which signals are
                // adjusted so that the BC is connected downstream the first pipe,
                // upstream the second, and downstream the subsequent pipes.
                for (int j = 0; j < linkTable[0].length; j++) {

                // Adjusts the size of the input arrays for the BC based on the
                    // number of pipes connected to it.
                    nPipes = 0;
                    for (int i = 0; i < linkTable.length; i++) {
                        nPipes += Math.abs(linkTable[i][j]);
                    }
                    pipesBc = new int[nPipes];
                    HQBcInput = new double[2 * nPipes];

                    // Arranges pipe heads and flow rates to be input into the BC.
                    k = 0;
                    for (int i = 0; i < linkTable.length; i++) {
                        if (linkTable[i][j] != 0) {
                            pipesBc[k] = linkTable[i][j] * i;
                            HQBcInput[2 * k] = tableHpipes[i][j];
                            if (k > 0) {
                                if (linkTable[i][j] > 0) {
                                    HQBcInput[2 * k + 1] = -tableQpipes[i][j];
                                } else {
                                    HQBcInput[2 * k + 1] = tableQpipes[i][j];
                                }
                            } else {
                                if (linkTable[i][j] > 0) {
                                    HQBcInput[2 * k + 1] = tableQpipes[i][j];
                                } else {
                                    HQBcInput[2 * k + 1] = -tableQpipes[i][j];
                                }
                            }
                            k++;
                        }
                    }

                    // Calculates the BC.
                    HQ = boundaryConditions.get(j).calculate(HQBcInput);

                // Fixes back the directions of the flow rates following the 
                    // same logic as the loop above.
                    for (int i = 0; i < k; i++) {
                        tableHBcs[pipesBc[i]][j] = HQ[0];
                        if (i > 0) {
                            if (linkTable[i][j] > 0) {
                                tableQBcs[pipesBc[i]][j] = -HQ[i + 1];
                            } else {
                                tableQBcs[pipesBc[i]][j] = HQ[i + 1];
                            }
                        } else {
                            if (linkTable[i][j] > 0) {
                                tableQBcs[pipesBc[i]][j] = HQ[i + 1];
                            } else {
                                tableQBcs[pipesBc[i]][j] = -HQ[i + 1];
                            }
                        }
                    }

                    HQ = null;
                }
            }

            i0 = Math.abs(i0 - 1);
        }

        return 0;
    }

}
