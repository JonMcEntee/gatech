package opt.test;

import java.io.File;
import java.io.FileWriter;
import java.util.Arrays;

import dist.DiscreteDependencyTree;
import dist.DiscreteUniformDistribution;
import dist.Distribution;

import opt.DiscreteChangeOneNeighbor;
import opt.EvaluationFunction;
import opt.GenericHillClimbingProblem;
import opt.HillClimbingProblem;
import opt.NeighborFunction;
import opt.RandomizedHillClimbing;
import opt.SimulatedAnnealing;
import opt.example.*;
import opt.ga.CrossoverFunction;
import opt.ga.DiscreteChangeOneMutation;
import opt.ga.SingleCrossOver;
import opt.ga.GenericGeneticAlgorithmProblem;
import opt.ga.GeneticAlgorithmProblem;
import opt.ga.MutationFunction;
import opt.ga.StandardGeneticAlgorithm;
import opt.prob.GenericProbabilisticOptimizationProblem;
import opt.prob.MIMIC;
import opt.prob.ProbabilisticOptimizationProblem;
import shared.FixedIterationTrainer;
import shared.RandomResetIterationTrainer;

/**
 * 
 * @author Andrew Guillory gtg008g@mail.gatech.edu
 * @version 1.0
 */

public class ContinuousPeaksTest {
    private static int T;

    private static final int ITER = 10;

    private static String path = "src/output/continuous_peaks_results.csv";

    private static File result_file = new File(path);

    private static String finals_path = "src/output/continuous_peaks_finals.csv";

    private static File finals_file = new File(finals_path);

    private static int BITSTARTSIZE = 20;

    private static int BITENDSIZE = 60;

    private static int BITINTERVAL = 10;

    private static int SCORE_THRESHOLD;

    public static void main(String[] args) {
        if(result_file.exists()) {
            result_file.delete();
        }
        append("algorithm,run_num,bitstring_size,iteration,score,bitstring", path);

        if(finals_file.exists()) {
            finals_file.delete();
        }
        append("algorithm,run_num,bitstring_size,score,iterations,training_time", finals_path);

        for (int i = 0; i < ITER; i++) {
            for (int j = BITSTARTSIZE; j <= BITENDSIZE; j += BITINTERVAL) {
                T = j / 10;
                SCORE_THRESHOLD = 2*j - T - 1;
                oneRun(i, j);
            }
        }
    }

    public static void oneRun(int testNumber, int bitSize) {
        int[] ranges = new int[bitSize];
        Arrays.fill(ranges, 2);
        EvaluationFunction ef = new ContinuousPeaksEvaluationFunction(T);
        Distribution odd = new DiscreteUniformDistribution(ranges);
        NeighborFunction nf = new DiscreteChangeOneNeighbor(ranges);
        MutationFunction mf = new DiscreteChangeOneMutation(ranges);
        CrossoverFunction cf = new SingleCrossOver();
        Distribution df = new DiscreteDependencyTree(.1, ranges); 
        HillClimbingProblem hcp = new GenericHillClimbingProblem(ef, odd, nf);
        GeneticAlgorithmProblem gap = new GenericGeneticAlgorithmProblem(ef, odd, mf, cf);
        ProbabilisticOptimizationProblem pop = new GenericProbabilisticOptimizationProblem(ef, odd, df);

        double start = System.nanoTime(), end, trainingTime;
        RandomizedHillClimbing rhc = new RandomizedHillClimbing(hcp, true, testNumber, bitSize, path);
        RandomResetIterationTrainer fit = new RandomResetIterationTrainer(rhc, SCORE_THRESHOLD);
        fit.train();
        end = System.nanoTime();
        trainingTime = end - start;
        double optimal_score = ef.value(rhc.getOptimal());
        append("RHC," + testNumber + "," + bitSize + "," + optimal_score + "," + fit.totalIterations + "," + trainingTime/Math.pow(10, 9), finals_path);

        start = System.nanoTime();
        SimulatedAnnealing sa = new SimulatedAnnealing(1E11, .95, hcp, true, testNumber, bitSize, path);
        fit = new RandomResetIterationTrainer(sa, SCORE_THRESHOLD);
        fit.train();
        end = System.nanoTime();
        trainingTime = end - start;
        optimal_score = ef.value(sa.getOptimal());
        append("SA," + testNumber + "," + bitSize + "," + optimal_score + "," + fit.totalIterations + "," + trainingTime/Math.pow(10, 9), finals_path);

        start = System.nanoTime();
        StandardGeneticAlgorithm ga = new StandardGeneticAlgorithm(200, 100, 10, gap, true, testNumber, bitSize, path);
        fit = new RandomResetIterationTrainer(ga, SCORE_THRESHOLD);
        fit.train();
        end = System.nanoTime();
        trainingTime = end - start;
        optimal_score = ef.value(ga.getOptimal());
        append("GA," + testNumber + "," + bitSize + ","  + optimal_score + "," + fit.totalIterations + "," + trainingTime/Math.pow(10, 9), finals_path);

        start = System.nanoTime();
        MIMIC mimic = new MIMIC(200, 20, pop, true, testNumber, bitSize, path);
        fit = new RandomResetIterationTrainer(mimic, SCORE_THRESHOLD);
        fit.train();
        end = System.nanoTime();
        trainingTime = end - start;
        optimal_score = ef.value(mimic.getOptimal());
        append("MIMIC," + testNumber + "," + bitSize + ","  + optimal_score + "," + fit.totalIterations + "," + trainingTime/Math.pow(10, 9), finals_path);
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
