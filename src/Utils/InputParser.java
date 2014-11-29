/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Utils;

import static Aux.Constants.DOWNSTREAM;
import static Aux.Constants.SIMPLE_MOC;
import static Aux.Constants.STEADY_STATE;
import static Aux.Constants.TRANSIENT;
import static Aux.Constants.UPSTREAM;
import Pipe.Pipe;
import java.util.ArrayList;
import BoundaryConditions.BoundaryCondition;
import BoundaryConditions.Junction;
import BoundaryConditions.Reservoir;

/**
 *
 * @author bernardoct
 */
public class InputParser {

    /**
     *
     */
    public InputParser() {
    }

    /**
     *
     * @return Bernardo Carvalho Trindade - bct52@cornell.edu
     */
    public static ArrayList parse(String filePath) {
        ArrayList<Pipe> pipesTransient = new ArrayList<>(), 
                pipesSteadyState = new ArrayList<>();
        ArrayList<BoundaryCondition> boundaryConditionsTransient = new ArrayList<>(),
                boundaryConditionsSteadyState = new ArrayList<>();

        // Add something like new input fileParser(pipes, boundaryConditons) 
        // to read an input file.
        Pipe p1t = new Pipe(1, 0.4, 200, 1000, 0.008, 0.04, 0, 0, SIMPLE_MOC, TRANSIENT);
        Pipe p2t = new Pipe(2, 0.4, 200, 1000, 0.008, 0.04, 0, 0, SIMPLE_MOC, TRANSIENT);
        Pipe p3t = new Pipe(3, 0.4, 200, 1000, 0.008, 0.04, 0, 0, SIMPLE_MOC, TRANSIENT);
        // Adds pipes to pipe list.
        pipesTransient.add(p1t);
        pipesTransient.add(p2t);
        pipesTransient.add(p3t);
        
        Pipe p1s = new Pipe(1, 0.4, 200, 1000, 0.008, 0.04, 0, 0, SIMPLE_MOC, STEADY_STATE);
        Pipe p2s = new Pipe(2, 0.4, 200, 1000, 0.008, 0.04, 0, 0, SIMPLE_MOC, STEADY_STATE);
        Pipe p3s = new Pipe(3, 0.4, 200, 1000, 0.008, 0.04, 0, 0, SIMPLE_MOC, STEADY_STATE);
        // Adds pipes to pipe list.
        pipesSteadyState.add(p1s);
        pipesSteadyState.add(p2s);
        pipesSteadyState.add(p3s);
        
        Reservoir r1t = new Reservoir(1, 100, 10, 0, new double[] {p1t.getB()}, 
                new double[] {p1t.getR()});
        Reservoir r2t = new Reservoir(2, 100, 0, 0, new double[] {p3t.getB()}, 
                new double[] {p2t.getR()});
        Reservoir r3t = new Reservoir(3, 100, 0, 0, new double[] {p3t.getB()}, 
                new double[] {p3t.getR()});
        Junction j1t = new Junction(4, 0, 
                new double[] {p1t.getB(), p2t.getB(), p3t.getB()}, 
                new double[] {p1t.getR(), p2t.getR(), p3t.getR()});
        
        // Adds boundary conditions (BC) to BCs list.
        boundaryConditionsTransient.add(r3t);
        boundaryConditionsTransient.add(r2t);
        boundaryConditionsTransient.add(r1t);
        boundaryConditionsTransient.add(j1t);
        
        Reservoir r1s = new Reservoir(1, 100, 10, 0, new double[] {p1s.getB()}, 
                new double[] {p1s.getR()});
        Reservoir r2s = new Reservoir(2, 100, 0, 0, new double[] {p3s.getB()}, 
                new double[] {p2s.getR()});
        Reservoir r3s = new Reservoir(3, 100, 0, 0, new double[] {p3s.getB()}, 
                new double[] {p3s.getR()});
        Junction j1s = new Junction(4, 0, 
                new double[] {p1s.getB(), p2s.getB(), p3s.getB()}, 
                new double[] {p1s.getR(), p2s.getR(), p3s.getR()});
        
        // Adds boundary conditions (BC) to BCs list.
        boundaryConditionsSteadyState.add(r3s);
        boundaryConditionsSteadyState.add(r2s);
        boundaryConditionsSteadyState.add(r1s);
        boundaryConditionsSteadyState.add(j1s);

        // Connectivity table.
//        int[][] linkTable = new int[][]{{UPSTREAM, 0, 0, DOWNSTREAM}, {0, DOWNSTREAM, 0, UPSTREAM}, {0, 0, DOWNSTREAM, UPSTREAM}};
        int[][] linkTable = new int[][]{{UPSTREAM, 0, 0, DOWNSTREAM}, {0, DOWNSTREAM, 0, UPSTREAM}, {0, 0, UPSTREAM, DOWNSTREAM}};
//        int[][] linkTable = new int[][]{{UPSTREAM, 0, DOWNSTREAM}, {0, DOWNSTREAM, UPSTREAM}};
//        int[][] linkTable = new int[][]{{UPSTREAM, 0, DOWNSTREAM}, {0, UPSTREAM, DOWNSTREAM}};
        // Works.
//        int[][] linkTable = new int[][]{{UPSTREAM, DOWNSTREAM}};
        // Doesn't work.
//        int[][] linkTable = new int[][]{{DOWNSTREAM, UPSTREAM}};

        ArrayList listsAndTables = new ArrayList<>();

        // Groups all lists and tables and returns them in a single ArrayList.
        listsAndTables.add(pipesTransient);
        listsAndTables.add(pipesSteadyState);
        listsAndTables.add(boundaryConditionsTransient);
        listsAndTables.add(boundaryConditionsSteadyState);
        listsAndTables.add(linkTable);
        
        return listsAndTables;
    }
}
