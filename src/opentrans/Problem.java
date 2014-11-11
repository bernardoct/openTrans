/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package opentrans;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import static opentrans.Constants.SIMPLE_MOC;

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
    simulationTime;

    /**
     *
     */
    public final int model;
    int[][] linkTable, boundaryConnectionsUpstream, boundaryConnectionsDownstream;

    ArrayList<Pipe> pipes = new ArrayList<>();
    ArrayList<BoundaryCondition> boundaryConditions = new ArrayList<>();

    
    /**
     * 
     * @param dt
     * @param model
     * @param simulationTime
     * @param inputFile 
     */
    public Problem(double dt, int model, double simulationTime, File inputFile) {
        this.dt = dt;
        this.model = model;
        this.simulationTime = simulationTime;

        // Add something like new input fileParser(pipes, boundaryConditons) 
        // to read an input file.
        Pipe p1 = new Pipe(0.5, 2000, 1000, 0.008, 0.002, 0, 0, SIMPLE_MOC);
        Reservoir r1 = new Reservoir(100, 10, 0, p1.getB(), p1.getB());
        Reservoir r2 = new Reservoir(100, 0, 0, p1.getB(), p1.getB());

        pipes.add(p1);
        boundaryConditions.add(r1);
        boundaryConditions.add(r2);

        linkTable = new int[pipes.size()][3];
    }

    /**
     * 
     * @return
     * @throws IOException 
     */
    public int calculate() throws IOException {
        int nTimeSteps = (int) Math.ceil(simulationTime / dt);
        int i0 = 0, boundaryUpstream, boundaryDownstream;

        for (int i = 0; i < nTimeSteps; i++) {
            for (int n = 0; n < linkTable.length; n++) {
                boundaryUpstream = linkTable[n][0];
                boundaryDownstream = linkTable[n][2];  
                
                //Do the link with dictionaries maybe.
                
                pipes.get(n).calculate(i0, 
                        boundaryConditions.get(boundaryUpstream).getH(), 
                        boundaryConditions.get(boundaryDownstream).getH(), 
                        boundaryConditions.get(boundaryUpstream).getQds(), 
                        boundaryConditions.get(boundaryUpstream).getQus());
            }

            i0 = Math.abs(i0 - 1);
        }

        return 0;
    }

}
