package opt.test;

import dist.*;
import opt.*;
import opt.example.*;
import opt.ga.*;
import shared.*;
import func.nn.backprop.*;

import java.util.*;
import java.io.*;
import java.text.*;

/**
 * Implementation of randomized hill climbing, simulated annealing, and genetic algorithm to
 * find optimal weights to a neural network that is classifying abalone as having either fewer 
 * or more than 15 rings. 
 *
 * @author Hannah Lau
 * @version 1.0
 */
public class AbaloneTest {
    private static Instance[] instances = initializeInstances();

    private static int inputLayer = 14, hiddenLayer = 5, outputLayer = 1, trainingIterations = 1000;
    private static BackPropagationNetworkFactory factory = new BackPropagationNetworkFactory();
    
    private static ErrorMeasure measure = new SumOfSquaresError();

    private static DataSet set = new DataSet(instances);

    private static BackPropagationNetwork networks[] = new BackPropagationNetwork[3];
    private static NeuralNetworkOptimizationProblem[] nnop = new NeuralNetworkOptimizationProblem[3];

    private static OptimizationAlgorithm[] oa = new OptimizationAlgorithm[3];
    private static String[] oaNames = {"RHC", "SA", "GA"};
    private static String results = "";

    private static DecimalFormat df = new DecimalFormat("0.000");
    private static String result_path = "src/output/abalone_results.csv";
    private static File result_file = new File(result_path);

    private static String finals_path = "src/output/abalone_finals.csv";
    private static File finals_file = new File(finals_path);

    public static void main(String[] args) {
        if(result_file.exists()) {
            result_file.delete();
        }
        try {
            append("algorithm,score", result_path);
        } catch (Exception e) {
            e.printStackTrace();
        }

        if(finals_file.exists()) {
            finals_file.delete();
        }
        try {
            append("algorithm,classified_correctly,classified_incorrectly,accuracy,training_time,test_time", finals_path);
        } catch (Exception e) {
            e.printStackTrace();
        }

        for(int i = 0; i < oa.length; i++) {
            networks[i] = factory.createClassificationNetwork(
                new int[] {inputLayer, hiddenLayer, outputLayer});
            nnop[i] = new NeuralNetworkOptimizationProblem(set, networks[i], measure);
        }

        System.out.println("hello");

        oa[0] = new RandomizedHillClimbing(nnop[0], true, result_path);
        oa[1] = new SimulatedAnnealing(1E11, .95, nnop[1], true, result_path);
        oa[2] = new StandardGeneticAlgorithm(200, 100, 10, nnop[2], true, result_path);

        System.out.println("hello2");

        for(int i = 0; i < oa.length; i++) {
            double start = System.nanoTime(), end, trainingTime, testingTime, correct = 0, incorrect = 0;
            train(oa[i], networks[i], oaNames[i]); //trainer.train();
            end = System.nanoTime();
            trainingTime = end - start;
            trainingTime /= Math.pow(10, 9);

            Instance optimalInstance = oa[i].getOptimal();
            networks[i].setWeights(optimalInstance.getData());

            double predicted, actual;
            start = System.nanoTime();
            for (int j = 0; j < instances.length; j++) {
                networks[i].setInputValues(instances[j].getData());
                networks[i].run();

                predicted = Double.parseDouble(instances[j].getLabel().toString());
                actual = Double.parseDouble(networks[i].getOutputValues().toString());

                double trash = Math.abs(predicted - actual) < 0.5 ? correct++ : incorrect++;

            }
            end = System.nanoTime();
            testingTime = end - start;
            testingTime /= Math.pow(10, 9);
            try{
            append(oaNames[i] + "," + correct + "," + incorrect + "," + correct / (correct + incorrect) * 100 + "," +
                    trainingTime + "," + testingTime, finals_path);
            }catch (Exception e) {
                e.printStackTrace();
            }
            results +=  "\nResults for " + oaNames[i] + ": \nCorrectly classified " + correct + " instances." +
                        "\nIncorrectly classified " + incorrect + " instances.\nPercent correctly classified: "
                        + df.format(correct/(correct+incorrect)*100) + "%\nTraining time: " + df.format(trainingTime)
                        + " seconds\nTesting time: " + df.format(testingTime) + " seconds\n";
        }

        System.out.println(results);
    }

    private static void train(OptimizationAlgorithm oa, BackPropagationNetwork network, String oaName) {
        System.out.println("\nError results for " + oaName + "\n---------------------------");

        for(int i = 0; i < trainingIterations; i++) {
            oa.train();

            double error = 0;
            for(int j = 0; j < instances.length; j++) {
                network.setInputValues(instances[j].getData());
                network.run();

                Instance output = instances[j].getLabel(), example = new Instance(network.getOutputValues());
                example.setLabel(new Instance(Double.parseDouble(network.getOutputValues().toString())));
                error += measure.value(output, example);
            }

            //System.out.println(df.format(error));
        }
    }

    private static Instance[] initializeInstances() {

        double[][][] attributes = new double[32560][][];

        try {
            BufferedReader br = new BufferedReader(new FileReader(new File("src/opt/test/formatted_adult_data.csv")));

            for(int i = 0; i < attributes.length; i++) {
                Scanner scan = new Scanner(br.readLine());
                scan.useDelimiter(",");

                attributes[i] = new double[2][];
                attributes[i][0] = new double[14]; // 7 attributes
                attributes[i][1] = new double[1];

                for(int j = 0; j < 14; j++)
                    attributes[i][0][j] = Double.parseDouble(scan.next());

                attributes[i][1][0] = Double.parseDouble(scan.next());
            }
        }
        catch(Exception e) {
            e.printStackTrace();
        }

        Instance[] instances = new Instance[attributes.length];

        for(int i = 0; i < instances.length; i++) {
            instances[i] = new Instance(attributes[i][0]);
            // classifications range from 0 to 30; split into 0 - 14 and 15 - 30
            instances[i].setLabel(new Instance(attributes[i][1][0]));
        }

        return instances;
    }

    public static void append(String data, String path) throws IOException{
        FileWriter pw = new FileWriter(path, true);
        pw.append(data);
        pw.append("\n");
        pw.flush();
        pw.close();
    }
}
