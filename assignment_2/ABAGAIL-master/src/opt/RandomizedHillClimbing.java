package opt;

import shared.Instance;
import util.linalg.Vector;

/**
 * A randomized hill climbing algorithm
 * @author Andrew Guillory gtg008g@mail.gatech.edu
 * @version 1.0
 */
public class RandomizedHillClimbing extends OptimizationAlgorithm {
    
    /**
     * The current optimization data
     */
    private Instance cur;
    
    /**
     * The current value of the data
     */
    private double curVal;

    private boolean verbose;

    private String path;

    private int testNumber;

    private int iterNumber = 0;
    
    /**
     * Make a new randomized hill climbing
     */
    public RandomizedHillClimbing(HillClimbingProblem hcp) {
        super(hcp);
        cur = hcp.random();
        this.verbose = false;
        curVal = hcp.value(cur);
    }

    public RandomizedHillClimbing(HillClimbingProblem hcp, boolean verbose, int testNumber, String path) {
        this(hcp);
        this.verbose = verbose;
        this.path = path;
        this.testNumber = testNumber;
    }

    /**
     * @see shared.Trainer#train()
     */
    public double train() {
        HillClimbingProblem hcp = (HillClimbingProblem) getOptimizationProblem();
        Instance neigh = hcp.neighbor(cur);
        double neighVal = hcp.value(neigh);
        if (neighVal > curVal) {
            curVal = neighVal;
            cur = neigh;
        }

        if(verbose) {
            // System.out.println("RHC: " + curVal);
            append("RHC," + this.testNumber + "," + iterNumber + "," + curVal + "," + instanceToString(cur), path);
            iterNumber++;
        }
        return curVal;
    }

    /**
     * @see opt.OptimizationAlgorithm#getOptimalData()
     */
    public Instance getOptimal() {
        return cur;
    }

}
