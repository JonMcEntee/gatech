package opt;

import shared.Instance;
import shared.Trainer;
import util.linalg.Vector;

import java.io.FileWriter;
import java.io.IOException;

/**
 * An abstract class for optimzation algorithms
 * @author Andrew Guillory gtg008g@mail.gatech.edu
 * @version 1.0
 */
public abstract class OptimizationAlgorithm implements Trainer {
    /**
     * The problem to optimize
     */
    private OptimizationProblem op;
    
    /**
     * Make a new optimization algorithm
     * @param op the problem to optimize
     */
    public OptimizationAlgorithm(OptimizationProblem op) {
        this.op = op;
    }
    
    /**
     * Get an optimization problem
     * @return the problem
     */
    public OptimizationProblem getOptimizationProblem() {
        return op;
    }
    
    /**
     * Get the optimal data
     * @return the data
     */
    public abstract Instance getOptimal();

    public String instanceToString(Instance inst){
        Vector data = inst.getData();
        String bitstring = "";
        for (int i = 0; i < data.size(); i++){
            bitstring += Math.round(data.get(i));
        }
        return bitstring;
    }

    public static void append(String data, String path) {
        try {
            FileWriter pw = new FileWriter(path, true);
            pw.append(data);
            pw.append("\n");
            pw.flush();
            pw.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
