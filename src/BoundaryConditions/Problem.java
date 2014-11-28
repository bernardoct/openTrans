/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package BoundaryConditions;

import static Aux.Constants.CONVERGENCY_HEAD_THRESHOLD;
import static Aux.Constants.STEADY_STATE;
import static Aux.Constants.TRANSIENT;
import java.io.File;
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
    public final double dt, simulationTime;
    public final int model;
    int[][] linkTable;

    ArrayList<Pipe> pipes = new ArrayList<>();
    ArrayList<Pipe> pipesTransient = new ArrayList<>();
    ArrayList<Pipe> pipesSteadyState = new ArrayList<>();
    ArrayList<BoundaryCondition> boundaryConditions = new ArrayList<>();
    ArrayList<BoundaryCondition> boundaryConditionsTransient = new ArrayList<>();
    ArrayList<BoundaryCondition> boundaryConditionsSteadyState = new ArrayList<>();
    ArrayList<double[]> boundaryQ = new ArrayList<>();
    double[] boundaryH;
    double[][] tableQpipes;
    double[][] tableHpipes;
    double[][] tableQBcs;
    double[][] tableHBcs;

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

        pipesTransient = (ArrayList<Pipe>) parsedInput.get(0);
        pipesSteadyState = (ArrayList<Pipe>) parsedInput.get(1);
        boundaryConditionsTransient = (ArrayList<BoundaryCondition>) parsedInput.get(2);
        boundaryConditionsSteadyState = (ArrayList<BoundaryCondition>) parsedInput.get(3);
        linkTable = (int[][]) parsedInput.get(4);

        tableQpipes = new double[linkTable.length][linkTable[0].length];
        tableHpipes = new double[linkTable.length][linkTable[0].length];
        tableQBcs = new double[linkTable.length][linkTable[0].length];
        tableHBcs = new double[linkTable.length][linkTable[0].length];

        Collections.sort(pipesTransient, new PipeComparator());
        Collections.sort(boundaryConditionsTransient, new BCComparator());
        Collections.sort(pipesSteadyState, new PipeComparator());
        Collections.sort(boundaryConditionsSteadyState, new BCComparator());
    }

    /**
     *
     * @param timeRegime If the problem is being calculated in steady state or
     * transient stage.
     * @return @throws Exception
     */
    public int calculate(int timeRegime) throws Exception {
        int nTimeSteps = (int) Math.ceil(simulationTime / dt), countConvergence = 0;
        int[] bcsPipe = new int[]{-1, -1};
        int[] pipesBc;
        int i0 = 0, nPipes, countConverge = 0;
        double[] HQ, HQPipeInput, HQBcInput,
                lastRecordedHeads = new double[pipesSteadyState.size()];

        if (timeRegime == TRANSIENT) {
            pipes = pipesTransient;
            boundaryConditions = boundaryConditionsTransient;
        } else {
            pipes = pipesSteadyState;
            boundaryConditions = boundaryConditionsSteadyState;
        }

        Collections.sort(pipes, new PipeComparator());
        Collections.sort(boundaryConditions, new BCComparator());

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
                    for (int[] linkTable1 : linkTable) {
                        if (linkTable1[j] != 0) {
                            nPipes += Math.abs(linkTable1[j]);
                        }
                    }
                    pipesBc = new int[nPipes];
                    HQBcInput = new double[2 * nPipes];

                    // Arranges pipe heads and flow rates to be input into the BC.
                    k = 0;
                    for (int i = 0; i < linkTable.length; i++) {
                        if (linkTable[i][j] != 0) {
                            pipesBc[k] = i;
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

            // Check if steady state converged by comparring the current heads
            // in the pipes to the head 20 time steps before. Copies heads and
            // flow rates from steady state to transient objects.
            if (timeRegime == STEADY_STATE) {
                if (tn == 20) {
                    // Counts how many pipes converged.
                    for (int i = 0; i < pipes.size(); i++) {
                        if (pipes.get(i).getQ1() <= lastRecordedHeads[i] / CONVERGENCY_HEAD_THRESHOLD
                                && pipes.get(i).getQ1() >= lastRecordedHeads[i] * CONVERGENCY_HEAD_THRESHOLD) {
                            countConverge++;
                        }

                        lastRecordedHeads[i] = pipes.get(i).getQ1();
                    }

                    // If all pipes converged, copy value.
                    if (countConverge == pipes.size()) {
                        tn = nTimeSteps;
                        for (int i = 0; i < pipes.size(); i++) {
                            pipesTransient.get(i).setSteadyState(pipesSteadyState.get(i).getSteadyState());
                        }
                        
                        for (int i = 0; i < boundaryConditions.size(); i++) {
                            boundaryConditionsTransient.get(i).setH(boundaryConditionsSteadyState.get(i).getH());
                            boundaryConditionsTransient.get(i).setQ(boundaryConditionsSteadyState.get(i).getQ());
                        }
                    } else {
                        // Reset counter and try again in a few time steps.
                        countConverge = 0;
                        tn = 0;
                        countConvergence++;
                    }
                }
            }

        }

        System.out.println("Pipes:");
        for (Pipe p : pipes) {
            System.out.println(p.toString());
        }

        System.out.println("Boundary Conditions:");
        for (BoundaryCondition bc : boundaryConditions) {
            System.out.println(bc.toString());
        }
        
        return 0;
    }

}
