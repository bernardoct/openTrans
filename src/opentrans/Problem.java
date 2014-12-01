/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package opentrans;

import static Aux.Constants.CONVERGENCY_CHECK;
import BoundaryConditions.BoundaryCondition;
import static Aux.Constants.CONVERGENCY_HEAD_THRESHOLD;
import static Aux.Constants.DOWNSTREAM;
import static Aux.Constants.FLOW_RATE;
import static Aux.Constants.HEAD;
import static Aux.Constants.NOT_CONNECTED;
import static Aux.Constants.STEADY_STATE;
import static Aux.Constants.TRANSIENT;
import static Aux.Constants.UPSTREAM;
import static Aux.Constants.dFmt;
import java.util.ArrayList;
import java.util.Collections;
import Pipe.Pipe;
import Utils.Comparators.BCComparator;
import Utils.Input.InputParser;
import Utils.Comparators.PipeComparator;
import java.io.File;

/**
 *
 * @author Bernardo Carvalho Trindade - bct52@cornell.edu
 */
public class Problem {

    private static double t = 0;
    public final double dt, simulationTime, printInterval, timeStartWriting;

    /**
     *
     */
    public final int model;
    int[][] linkTable;
    String pathFolder = "";

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
     * @param inputFilePath
     * @throws java.lang.Exception
     */
    public Problem(double dt, int model, double simulationTime, String inputFilePath) throws Exception {
        InputParser p = new InputParser();

        ArrayList parsedInput = p.parse(inputFilePath);
        pathFolder = (new File(inputFilePath)).getParent();

        pipesTransient = (ArrayList<Pipe>) parsedInput.get(0);
        pipesSteadyState = (ArrayList<Pipe>) parsedInput.get(1);
        boundaryConditionsTransient = (ArrayList<BoundaryCondition>) parsedInput.get(2);
        boundaryConditionsSteadyState = (ArrayList<BoundaryCondition>) parsedInput.get(3);
        linkTable = (int[][]) parsedInput.get(4);

        this.dt = ((double[]) parsedInput.get(5))[0];
        this.model = (int) Math.round(((double[]) parsedInput.get(5))[1]);
        this.simulationTime = ((double[]) parsedInput.get(5))[2];
        this.timeStartWriting = ((double[]) parsedInput.get(5))[3];
        this.printInterval = ((double[]) parsedInput.get(5))[4];

        tableQpipes = new double[linkTable.length][linkTable[0].length];
        tableHpipes = new double[linkTable.length][linkTable[0].length];
        tableQBcs = new double[linkTable.length][linkTable[0].length];
        tableHBcs = new double[linkTable.length][linkTable[0].length];

        Collections.sort(pipesTransient, new PipeComparator());
        Collections.sort(pipesSteadyState, new PipeComparator());
        Collections.sort(boundaryConditionsTransient, new BCComparator());
        Collections.sort(boundaryConditionsSteadyState, new BCComparator());

        String str = "";
        for (int[] l : linkTable) {
            for (int i : l) {
                str += i + "\t";
            }
            System.out.println(str);
            str = "";
        }

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
        double[] HQ, HQPipeInput = new double[4], HQBcInput,
                lastRecordedFlowRates = new double[pipesSteadyState.size()];
        double convergenceQ, tWriteNext = 0;
        t = 0;

        if (timeRegime == TRANSIENT) {
            pipes = pipesTransient;
            boundaryConditions = boundaryConditionsTransient;
        } else {
            pipes = pipesSteadyState;
            boundaryConditions = boundaryConditionsSteadyState;
        }

        Collections.sort(pipes, new PipeComparator());
        Collections.sort(boundaryConditions, new BCComparator());

        int timeStepLimit = (timeRegime == TRANSIENT ? nTimeSteps : 500000);

        for (int tn = 0; tn < timeStepLimit; tn++) {
            t += dt;

            // Finds the boundary conditions assigned to a pipe, gets their Q's
            // and H's, and calculates the flows in the pipes.
            for (int i = 0; i < linkTable.length; i++) {

                for (int j = 0; j < linkTable[0].length; j++) {
                    if (linkTable[i][j] == UPSTREAM) {
                        HQPipeInput[0] = tableHBcs[i][j];
                        HQPipeInput[1] = tableQBcs[i][j];
                    } else if (linkTable[i][j] == DOWNSTREAM) {
                        HQPipeInput[2] = tableHBcs[i][j];
                        HQPipeInput[3] = -tableQBcs[i][j];
                    }
                }

                // Calculate flow in the pipe.
                HQ = pipes.get(i).calculate(i0, HQPipeInput);

                // Update tables with pipe upstream and downstream 
                // heads and flow rates.
                for (int j = 0; j < linkTable[0].length; j++) {
                    if (linkTable[i][j] == UPSTREAM) {
                        tableHpipes[i][j] = HQ[0];
                        tableQpipes[i][j] = HQ[1];
                    } else if (linkTable[i][j] == DOWNSTREAM) {
                        tableHpipes[i][j] = HQ[2];
                        tableQpipes[i][j] = HQ[3];
                    }
                }
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
                        if (linkTable[i][j] == UPSTREAM) {
                            HQBcInput[2 * k] = tableHpipes[i][j];
                            HQBcInput[2 * k + 1] = tableQpipes[i][j];
                            pipesBc[k] = i;
                            k++;
                        }
                        if (linkTable[i][j] == DOWNSTREAM) {
                            HQBcInput[2 * k] = tableHpipes[i][j];
                            HQBcInput[2 * k + 1] = -tableQpipes[i][j];
                            pipesBc[k] = i;
                            k++;
                        }
                    }
                    
                    // Calculates the BC.
                    HQ = boundaryConditions.get(j).calculate(HQBcInput, t);

                    // Fixes back the directions of the flow rates following the 
                    // same logic as the loop above.                                        
                    k = 0;
                    for (int i = 0; i < pipesBc.length; i++) {
                        if (linkTable[pipesBc[i]][j] != NOT_CONNECTED) {
                            tableHBcs[pipesBc[i]][j] = HQ[2 * k];
                            tableQBcs[pipesBc[i]][j] = HQ[2 * k + 1];
                        }
                        k++;
                    }
                }
            }

            i0 = Math.abs(i0 - 1);

            // Check if steady state converged by comparring the current heads
            // in the pipes to the head 20 time steps before. Copies heads and
            // flow rates from steady state to transient objects.
            if (timeRegime == STEADY_STATE) {
                if (tn == CONVERGENCY_CHECK) {
                    // Counts how many pipes converged.
                    for (int i = 0; i < pipes.size(); i++) {
                        convergenceQ = Math.abs(pipes.get(i).getQ1());
                        if (convergenceQ
                                <= lastRecordedFlowRates[i] / CONVERGENCY_HEAD_THRESHOLD
                                && convergenceQ
                                >= lastRecordedFlowRates[i] * CONVERGENCY_HEAD_THRESHOLD) {
                            countConverge++;
                        }

                        lastRecordedFlowRates[i] = convergenceQ;
                    }

                    // If all pipes converged, copy value.
                    if (countConverge == pipes.size()) {
                        System.out.println(countConvergence * CONVERGENCY_CHECK);
                        tn = timeStepLimit;
                        for (int i = 0; i < pipes.size(); i++) {
                            pipesTransient.get(i).setSteadyState(
                                    pipesSteadyState.get(i).getSteadyState());
                        }

                        for (int i = 0; i < boundaryConditions.size(); i++) {
                            boundaryConditionsTransient.get(i).setH(
                                    boundaryConditionsSteadyState.get(i).getH());
                            boundaryConditionsTransient.get(i).setQ(
                                    boundaryConditionsSteadyState.get(i).getQ());
                        }
                    } else {
                        // Reset counter and try again in a few time steps.
                        countConverge = 0;
                        tn = 0;
                        countConvergence++;
                    }
                }
            } else {
                if (t > tWriteNext && t > timeStartWriting) {
                    tWriteNext += printInterval;
                    String outputFile = pathFolder + "/Results/H" + dFmt.format(t) + ".tsv";
                    Utils.Output.AsyncFileWriter.write(new File(outputFile), pipesTransient, HEAD);
                    outputFile = pathFolder + "/Results/Q" + dFmt.format(t) + ".tsv";
                    Utils.Output.AsyncFileWriter.write(new File(outputFile), pipesTransient, FLOW_RATE);
                }
            }

        }

        if (timeRegime == STEADY_STATE) {
            System.out.println();

            System.out.println("Pipes:");
            for (Pipe pi : pipesSteadyState) {
                System.out.println(pi.toString());
            }

            System.out.println("Boundary Conditions:");
            for (BoundaryCondition bc : boundaryConditionsSteadyState) {
                System.out.println(bc.toString());
            }
        } else {
            System.out.println();

            System.out.println("Pipes:");
            for (Pipe pi : pipesTransient) {
                System.out.println(pi.toString());
            }

            System.out.println("Boundary Conditions:");
            for (BoundaryCondition bc : boundaryConditionsTransient) {
                System.out.println(bc.toString());
            }
        }

        return 0;
    }

}
