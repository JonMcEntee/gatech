package opt;

import dist.Distribution;

import shared.Instance;
import util.linalg.Vector;

/**
 * A simulated annealing hill climbing algorithm
 * @author Andrew Guillory gtg008g@mail.gatech.edu
 * @version 1.0
 */
public class SimulatedAnnealing extends OptimizationAlgorithm {
    
    /**
     * The current optimiation data
     */
    private Instance cur;
    
    /**
     * The current optimization value
     */
    private double curVal;

    private boolean verbose;

    private String path;

    private int testNumber;

    private int iterNumber = 0;

    /**
     * The temperature to start with
     */
    private double tValue;

    /**
     * The current temperature
     */
    private double t;
    
    /**
     * The cooling parameter
     */
    private double cooling;

    private int bitSize;
    
    /**
     * Make a new simulated annealing hill climbing
     * @param t the starting temperature
     * @param cooling the cooling exponent
     * @param hcp the problem to solve
     */
    public SimulatedAnnealing(double t, double cooling, HillClimbingProblem hcp) {
        super(hcp);
        this.verbose = false;
        this.t = t;
        this.tValue = t;
        this.cooling = cooling;
        this.cur = hcp.random();
        this.curVal = hcp.value(cur);
    }

    public SimulatedAnnealing(double t, double cooling, HillClimbingProblem hcp, boolean verbose, int testNumber, int bitSize,
                              String path) {
        this(t, cooling, hcp);
        this.verbose = verbose;
        this.path = path;
        this.testNumber = testNumber;
        this.bitSize = bitSize;
    }

    /**
     * @see shared.Trainer#train()
     */
    public double train() {
        HillClimbingProblem p = (HillClimbingProblem) getOptimizationProblem();
        Instance neigh = p.neighbor(cur);
        double neighVal = p.value(neigh);
        if (neighVal > curVal || Distribution.random.nextDouble() < 
                Math.exp((neighVal - curVal) / t)) {
            curVal = neighVal;
            cur = neigh;
        }
        t *= cooling;
        if (verbose) {
            //System.out.println("SA: " + curVal);
            append("SA," + this.testNumber + "," + bitSize + "," + iterNumber + "," + curVal + "," + instanceToString(cur), path);
            iterNumber++;
        }
        return curVal;
    }

    /**
     * @see opt.OptimizationAlgorithm#getOptimal()
     */
    public Instance getOptimal() {
        return cur;
    }

    public void reset() {
        HillClimbingProblem hcp = (HillClimbingProblem) getOptimizationProblem();
        this.t = this.tValue;
        this.cur = hcp.random();
        this.curVal = hcp.value(cur);
    }

}