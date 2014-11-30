/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Utils;

import static Aux.Constants.DGCM;
import static Aux.Constants.SIMPLE_MOC;
import static Aux.Constants.STEADY_STATE;
import static Aux.Constants.TRANSIENT;
import Pipe.Pipe;
import java.util.ArrayList;
import BoundaryConditions.BoundaryCondition;
import BoundaryConditions.Junction;
import BoundaryConditions.ReservoirConstHead;
import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

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
    ArrayList<Pipe> pipesTransient = new ArrayList<>(),
            pipesSteadyState = new ArrayList<>();
    ArrayList<BoundaryCondition> boundaryConditionsTransient = new ArrayList<>(),
            boundaryConditionsSteadyState = new ArrayList<>();
    ArrayList<int[]> preLinkTable = new ArrayList<>();
    Map<Integer, ArrayList<Double>> bcB = new HashMap<>();
    Map<Integer, ArrayList<Double>> bcR = new HashMap<>();

    /**
     *
     * @param filePath Path to the input file.
     * @return Bernardo Carvalho Trindade - bct52@cornell.edu
     * @throws java.io.FileNotFoundException
     */
    public ArrayList parse(String filePath) throws FileNotFoundException, IOException, Exception {

        double[] parameters = null;

        BufferedReader br = new BufferedReader(new FileReader(filePath));

        String line;
        line = br.readLine();

        // Starts looping through the lines of the input file.
        while (!line.equals("[END]")) {
            if (!line.matches("[ \t]*") && line.charAt(0) != '%') {
                switch (line) {
                    case "[PARAMETERS]":
                        line = br.readLine();
                        // Gets vector with problem parameters.
                        parameters = readParameters(line);
                        line = br.readLine();
                        break;
                    case "[PIPES]":
                        line = br.readLine();
                        while (!line.matches("[ \t]*") && line.charAt(0) != '[') {
                            while (line.length() > 0 && line.charAt(0) != '%' && !line.
                                    matches("[ \t]*") && line.charAt(0) != '[') {
                                // Reads pipe info and creates both transient and steady state pipes.
                                readPipe(line, parameters[0], (int) parameters[1]);
                                line = br.readLine();
                            }
                        }
                        break;
                    case "[RESERVOIRS]":
                        line = br.readLine();
                        while (!line.matches("[ \t]*") && line.charAt(0) != '[') {
                            while (line.length() > 0 && line.charAt(0) != '%' && !line.
                                    matches("[ \t]*") && line.charAt(0) != '[') {
                                // Reads reservoir info and creates both transient and steady state reservoir.
                                readReservoir(line);
                                line = br.readLine();
                            }
                        }
                        break;
                    case "[JUNCTIONS]":
                        line = br.readLine();
                        while (!line.matches("[ \t]*") && line.charAt(0) != '[') {
                            while (line.length() > 0 && line.charAt(0) != '%' && !line.
                                    matches("[ \t]*") && line.charAt(0) != '[') {
                                // Reads junction info and creates both transient and steady state junction.
                                readJunction(line);
                                line = br.readLine();
                            }
                        }
                        break;
                }

            }
        }

        ArrayList listsAndTables = new ArrayList<>();

        // Groups all lists and tables and returns them in a single ArrayList.
        listsAndTables.add(pipesTransient);
        listsAndTables.add(pipesSteadyState);
        listsAndTables.add(boundaryConditionsTransient);
        listsAndTables.add(boundaryConditionsSteadyState);
        listsAndTables.add(buildLinkTable());
        listsAndTables.add(parameters);

        return listsAndTables;
    }

    /**
     *
     * @param line Line of the input file containing the parameters.
     * @return Array with dt, method for handling cavitation, and total
     * simulation time.
     * @throws IOException
     */
    private static double[] readParameters(String line) throws IOException, Exception {
        String[] l;
        int methodTransient;

        l = line.split("\\s+");

        switch (l[1]) {
            case "SIMPLE_MOC":
                methodTransient = SIMPLE_MOC;
                break;
            case "DGCM":
                methodTransient = DGCM;
                break;
            default:
                throw new Exception(l[1] + " is not a supported method.");
        }

        return new double[]{Double.parseDouble(l[0]), methodTransient,
            Double.parseDouble(l[2])};
    }

    /**
     *
     * @param line Line with pipe parameters.
     * @param dt Problem dt.
     * @param method Cavitation handling method.
     * @throws IOException
     */
    private void readPipe(String line, double dt, int method) throws IOException {
        String[] l = line.split("\\s+");
        Pipe p1, p2;
        ArrayList<Double> B, R;
        int ID = Integer.parseInt(l[0]),
                bcUs = Integer.parseInt(l[1]),
                bcDw = Integer.parseInt(l[2]);

        p1 = new Pipe(ID, Double.parseDouble(l[3]),
                Double.parseDouble(l[4]), Double.parseDouble(l[5]),
                Double.parseDouble(l[6]), dt, Double.parseDouble(l[7]),
                Double.parseDouble(l[8]), method, TRANSIENT);
        p2 = new Pipe(ID, Double.parseDouble(l[3]),
                Double.parseDouble(l[4]), Double.parseDouble(l[5]),
                Double.parseDouble(l[6]), dt, Double.parseDouble(l[7]),
                Double.parseDouble(l[8]), method, STEADY_STATE);
        pipesTransient.add(p1);
        pipesSteadyState.add(p2);

        // Registers pipe connectivity information to later build the link table.
        preLinkTable.add(new int[]{ID, bcUs, bcDw});
        Collections.sort(preLinkTable, new PipeConnectivityComparator());
    }

    /**
     *
     * @param line Line with reservoir parameters.
     */
    private void readReservoir(String line) {
        String[] l = line.split("\\s+");
        int ID = Integer.parseInt(l[0]);
        double[] B, R;

        boundaryConditionsTransient.add(new ReservoirConstHead(
                Integer.parseInt(l[0]),
                Double.parseDouble(l[1]),
                Double.parseDouble(l[2]),
                Double.parseDouble(l[3]),
                getArrayOfB(pipesTransient, ID),
                getArrayOfR(pipesTransient, ID)));

        boundaryConditionsSteadyState.add(new ReservoirConstHead(
                Integer.parseInt(l[0]),
                Double.parseDouble(l[1]),
                Double.parseDouble(l[2]),
                Double.parseDouble(l[3]),
                getArrayOfB(pipesSteadyState, ID),
                getArrayOfR(pipesSteadyState, ID)));
    }

    /**
     *
     * @param line Line with Junction Parameters.
     */
    private void readJunction(String line) {
        String[] l = line.split("\\s+");
        int ID = Integer.parseInt(l[0]);
        double[] B, R;

        boundaryConditionsTransient.add(new Junction(
                Integer.parseInt(l[0]),
                Double.parseDouble(l[1]),
                getArrayOfB(pipesTransient, ID),
                getArrayOfR(pipesTransient, ID)));

        boundaryConditionsSteadyState.add(new Junction(
                Integer.parseInt(l[0]),
                Double.parseDouble(l[1]),
                getArrayOfB(pipesSteadyState, ID),
                getArrayOfR(pipesSteadyState, ID)));

    }

    /**
     *
     * @param pipeList Either transient or steady state pipe list
     * @param ID Boundary condition ID.
     * @return Array of doubles with the B values of all pipes connected to that
     * boundary condition.
     */
    private double[] getArrayOfB(ArrayList<Pipe> pipeList, int ID) {
        ArrayList<Double> B = new ArrayList<>();

        for (int[] s : preLinkTable) {
            if (s[1] == ID || s[2] == ID) {
                B.add(getPipeByID(pipeList, s[0]).getB());
            }
        }

        return listDoubleToArrayPrimitive(B);
    }

    /**
     *
     * @param pipeList Either transient or steady state pipe list
     * @param ID Boundary condition ID.
     * @return Array of doubles with the R values of all pipes connected to that
     * boundary condition.
     */
    private double[] getArrayOfR(ArrayList<Pipe> pipeList, int ID) {
        ArrayList<Double> R = new ArrayList<>();

        for (int[] s : preLinkTable) {
            if (s[1] == ID || s[2] == ID) {
                R.add(getPipeByID(pipeList, s[0]).getR());
            }
        }

        return listDoubleToArrayPrimitive(R);
    }

    /**
     *
     * @param pipeList
     * @param ID
     * @return
     */
    private Pipe getPipeByID(ArrayList<Pipe> pipeList, int ID) {
        for (Pipe p : pipeList) {
            if (p.ID == ID) {
                return p;
            }
        }

        return null;
    }

    /**
     *
     * @param list
     * @return
     */
    private double[] listDoubleToArrayPrimitive(ArrayList<Double> list) {
        double[] a = new double[list.size()];

        for (int i = 0; i < a.length; i++) {
            a[i] = list.get(i);
        }

        return a;
    }

    private int[][] buildLinkTable() {
        int[][] linkTable
                = new int[pipesTransient.size()][boundaryConditionsTransient.size()];

        for (int[] l : preLinkTable) {
            linkTable[l[0] - 1][l[1] - 1] = 1;
            linkTable[l[0] - 1][l[2] - 1] = -1;
        }

        return linkTable;
    }
}
