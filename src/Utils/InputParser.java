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
     * @return
     */
    public static ArrayList parse() {
        ArrayList<Pipe> pipes = new ArrayList<>();
        ArrayList<BoundaryCondition> boundaryConditions = new ArrayList<>();

        // Add something like new input fileParser(pipes, boundaryConditons) 
        // to read an input file.
        Pipe p1 = new Pipe(1, 0.4, 200, 1000, 0.008, 0.02, 0, 0, SIMPLE_MOC);
        Pipe p2 = new Pipe(2, 0.4, 200, 1000, 0.008, 0.02, 0, 0, SIMPLE_MOC);
        Pipe p3 = new Pipe(3, 0.4, 200, 1000, 0.008, 0.02, 0, 0, SIMPLE_MOC);
        // Adds pipes to pipe list.
        pipes.add(p1);
        pipes.add(p2);
        pipes.add(p3);
        
        Reservoir r1 = new Reservoir(1, 100, 10, 0, new double[] {p1.getB()}, 
                new double[] {p1.getR()});
        Reservoir r2 = new Reservoir(2, 100, 0, 0, new double[] {p3.getB()}, 
                new double[] {p2.getR()});
        Reservoir r3 = new Reservoir(3, 100, 0, 0, new double[] {p3.getB()}, 
                new double[] {p3.getR()});
        Junction j1 = new Junction(4, 0, 
                new double[] {p1.getB(), p2.getB(), p3.getB()}, 
                new double[] {p1.getR(), p2.getR(), p3.getR()});
        
        // Adds boundary conditions (BC) to BCs list.
        boundaryConditions.add(r3);
        boundaryConditions.add(r2);
        boundaryConditions.add(r1);
        boundaryConditions.add(j1);

        // Connectivity table.
        int[][] linkTable = new int[][]{{1, 0, 0, -1}, {0, -1, 0, 1}, {0, 0, -1, 1}};

        ArrayList listsAndTables = new ArrayList<>();

        // Groups all lists and tables and returns them in a single ArrayList.
        listsAndTables.add(pipes);
        listsAndTables.add(boundaryConditions);
        listsAndTables.add(linkTable);
        
        return listsAndTables;
    }
}
