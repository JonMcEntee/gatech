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
 * 
 * @author Andrew Guillory gtg008g@mail.gatech.edu
 * @version 1.0
 */

public class ContinuousPeaksTest {
    /** The n value */
    private static final int N = 60;
    /** The t value */
    private static final int T = N / 10;

    private static String path = "src/output/continuous_peaks_results.csv";

    private static File result_file = new File(path);

    private static String finals_path = "src/output/continuous_peaks_finals.csv";

    private static File finals_file = new File(finals_path);
    
    public static void main(String[] args) {
        int[] ranges = new int[N];
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

        if(result_file.exists()) {
            result_file.delete();
        }
        append("algorithm,score,bitstring", path);

        RandomizedHillClimbing rhc = new RandomizedHillClimbing(hcp, true, path);
        FixedIterationTrainer fit = new FixedIterationTrainer(rhc, 1000);
        fit.train();

        SimulatedAnnealing sa = new SimulatedAnnealing(1E11, .95, hcp, true, path);
        fit = new FixedIterationTrainer(sa, 1000);
        fit.train();

        StandardGeneticAlgorithm ga = new StandardGeneticAlgorithm(200, 100, 10, gap, true, path);
        fit = new FixedIterationTrainer(ga, 1000);
        fit.train();

        MIMIC mimic = new MIMIC(200, 20, pop, true, path);
        fit = new FixedIterationTrainer(mimic, 1000);
        fit.train();

        if(finals_file.exists()) {
            finals_file.delete();
        }
        append("algorithm,score,iterations,training_time", finals_path);

        double start = System.nanoTime(), end, trainingTime;
        rhc = new RandomizedHillClimbing(hcp);
        fit = new FixedIterationTrainer(rhc, 20000);
        fit.train();
        end = System.nanoTime();
        trainingTime = end - start;
        double optimal_score = ef.value(rhc.getOptimal());
        append("RHC," + optimal_score + ",20000," + trainingTime/Math.pow(10, 9), finals_path);

        start = System.nanoTime();
        sa = new SimulatedAnnealing(1E11, .95, hcp);
        fit = new FixedIterationTrainer(sa, 20000);
        fit.train();
        end = System.nanoTime();
        trainingTime = end - start;
        optimal_score = ef.value(sa.getOptimal());
        append("SA," + optimal_score + ",20000," + trainingTime/Math.pow(10, 9), finals_path);

        start = System.nanoTime();
        ga = new StandardGeneticAlgorithm(200, 100, 10, gap);
        fit = new FixedIterationTrainer(ga, 1000);
        fit.train();
        end = System.nanoTime();
        trainingTime = end - start;
        optimal_score = ef.value(ga.getOptimal());
        append("GA," + optimal_score + ",1000," + trainingTime/Math.pow(10, 9), finals_path);

        start = System.nanoTime();
        mimic = new MIMIC(200, 20, pop);
        fit = new FixedIterationTrainer(mimic, 1000);
        fit.train();
        end = System.nanoTime();
        trainingTime = end - start;
        optimal_score = ef.value(mimic.getOptimal());
        append("MIMIC," + optimal_score + ",1000," + trainingTime/Math.pow(10, 9), finals_path);
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
