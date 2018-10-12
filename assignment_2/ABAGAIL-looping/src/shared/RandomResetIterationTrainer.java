package shared;

import opt.OptimizationAlgorithm;
import opt.RandomizedHillClimbing;

/**
 * A fixed iteration trainer
 * @author Andrew Guillory gtg008g@mail.gatech.edu
 * @version 1.0
 */
public class RandomResetIterationTrainer implements Trainer {

    /**
     * The inner trainer
     */
    private OptimizationAlgorithm trainer;

    /**
     * The score to train until
     */
    private int score;

    /**
     * How many iterations with no improvement
     */
    private int threshold;

    private int sameValueIterations = 0;

    private int tolerance;

    public int totalIterations = 0;

    /**
     * Make a new fixed iterations trainer
     * @param t the trainer
     * @param iter the number of iterations
     */
    public RandomResetIterationTrainer(OptimizationAlgorithm t, int score, int threshold, int tolerance) {
        trainer = t;
        this.score = score;
        this.threshold = threshold;
        this.tolerance = tolerance;
    }

    public RandomResetIterationTrainer(OptimizationAlgorithm t, int score) {
        trainer = t;
        this.score = score;
        this.threshold = 2000;
        this.tolerance = 100000;
    }

    /**
     * @see Trainer#train()
     */
    public double train() {
        double curVal = 0;
        double newVal;
        while (true) {
            newVal = trainer.train();
            //System.out.println(newVal);
            if (newVal > curVal) {
                curVal = newVal;
                sameValueIterations = 0;
            } else {
                sameValueIterations++;
            }

            if (sameValueIterations >= threshold) {
                trainer.reset();
                curVal = 0;
            }

            if (curVal >= score) {
                break;
            }

            totalIterations++;

            if (totalIterations >= tolerance) {
                break;
            }
        }

        return curVal;
    }
    

}
