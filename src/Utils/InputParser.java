/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Utils;

import static Aux.Constants.SIMPLE_MOC;
import Pipe.Pipe;
import java.util.ArrayList;
import BoundaryConditions.BoundaryCondition;
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
     * @return
     */
    public static ArrayList parse() {
        ArrayList<Pipe> pipes = new ArrayList<>();
        ArrayList<BoundaryCondition> boundaryConditions = new ArrayList<>();

        // Add something like new input fileParser(pipes, boundaryConditons) 
        // to read an input file.
        Pipe p1 = new Pipe(1, 0.4, 200, 1000, 0.008, 0.02, 0, 0, SIMPLE_MOC);
        // Adds pipes to pipe list.
        pipes.add(p1);
        
        Reservoir r1 = new Reservoir(1, 100, 10, 0, new double[] {p1.getB()}, 
                new double[] {p1.getR()});
        Reservoir r2 = new Reservoir(2, 100, 0, 0, new double[] {p1.getB()}, 
                new double[] {p1.getR()});
        
        // Adds boundary conditions (BC) to BCs list.
        boundaryConditions.add(r2);
        boundaryConditions.add(r1);

        // Connectivity table.
        int[][] linkTable = new int[][]{{1, -1}};

        ArrayList listsAndTables = new ArrayList<>();

        // Groups all lists and tables and returns them in a single ArrayList.
        listsAndTables.add(pipes);
        listsAndTables.add(boundaryConditions);
        listsAndTables.add(linkTable);
        
        return listsAndTables;
    }
}
