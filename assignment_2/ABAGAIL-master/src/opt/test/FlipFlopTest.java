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

/**
 * A test using the flip flop evaluation function
 * @author Andrew Guillory gtg008g@mail.gatech.edu
 * @version 1.0
 */
public class FlipFlopTest {
    /** The n value */
    private static final int N = 100;

    private static final int ITER = 100;

    private static String path = "src/output/flip_flop_results.csv";

    private static File result_file = new File(path);

    private static String finals_path = "src/output/flip_flop_finals.csv";

    private static File finals_file = new File(finals_path);

    public static void main(String[] args) {
        if(result_file.exists()) {
            result_file.delete();
        }
        append("algorithm,run_num,iteration,score,bitstring", path);

        if(finals_file.exists()) {
            finals_file.delete();
        }
        append("algorithm,run_num,score,iterations,training_time", finals_path);

        for (int i = 0; i < ITER; i++) {
            oneRun(i);
        }
    }

    public static void oneRun(int testNumber) {
        int[] ranges = new int[N];
        Arrays.fill(ranges, 2);
        EvaluationFunction ef = new FlipFlopEvaluationFunction();
        Distribution odd = new DiscreteUniformDistribution(ranges);
        NeighborFunction nf = new DiscreteChangeOneNeighbor(ranges);
        MutationFunction mf = new DiscreteChangeOneMutation(ranges);
        CrossoverFunction cf = new SingleCrossOver();
        Distribution df = new DiscreteDependencyTree(.1, ranges); 
        HillClimbingProblem hcp = new GenericHillClimbingProblem(ef, odd, nf);
        GeneticAlgorithmProblem gap = new GenericGeneticAlgorithmProblem(ef, odd, mf, cf);
        ProbabilisticOptimizationProblem pop = new GenericProbabilisticOptimizationProblem(ef, odd, df);

        double start = System.nanoTime(), end, trainingTime;
        RandomizedHillClimbing rhc = new RandomizedHillClimbing(hcp, true, testNumber, path);
        FixedIterationTrainer fit = new FixedIterationTrainer(rhc, 20000);
        fit.train();
        end = System.nanoTime();
        trainingTime = end - start;
        double optimal_score = ef.value(rhc.getOptimal());
        append("RHC," + testNumber + "," + optimal_score + ",20000," + trainingTime/Math.pow(10, 9), finals_path);

        start = System.nanoTime();
        SimulatedAnnealing sa = new SimulatedAnnealing(100, .95, hcp, true, testNumber, path);
        fit = new FixedIterationTrainer(sa, 20000);
        fit.train();
        end = System.nanoTime();
        trainingTime = end - start;
        optimal_score = ef.value(sa.getOptimal());
        append("SA," + testNumber + "," + optimal_score + ",20000," + trainingTime/Math.pow(10, 9), finals_path);

        start = System.nanoTime();
        StandardGeneticAlgorithm ga = new StandardGeneticAlgorithm(200, 100, 20, gap, true, testNumber, path);
        fit = new FixedIterationTrainer(ga, 1000);
        fit.train();
        end = System.nanoTime();
        trainingTime = end - start;
        optimal_score = ef.value(ga.getOptimal());
        append("GA," + testNumber + "," + optimal_score + ",1000," + trainingTime/Math.pow(10, 9), finals_path);

        start = System.nanoTime();
        MIMIC mimic = new MIMIC(200, 5, pop, true, testNumber, path);
        fit = new FixedIterationTrainer(mimic, 1000);
        fit.train();
        end = System.nanoTime();
        trainingTime = end - start;
        optimal_score = ef.value(mimic.getOptimal());
        append("MIMIC," + testNumber + "," + optimal_score + ",1000," + trainingTime/Math.pow(10, 9), finals_path);
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
