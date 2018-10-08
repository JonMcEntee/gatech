package func.test;

import shared.ConvergenceTrainer;
import shared.DataSet;
import shared.Instance;
import shared.SumOfSquaresError;
import func.nn.backprop.*;

import java.io.*;
import java.text.DecimalFormat;
import java.util.Scanner;

/**
 * An XOR test
 * @author Andrew Guillory gtg008g@mail.gatech.edu
 * @version 1.0
 */
public class NNBinaryClassificationTest {

    private static Instance[] instances = initializeInstances();

    private static String results = "";

    private static int inputLayer = 14, hiddenLayer = 5, outputLayer = 1, trainingIterations = 1000;

    private static DecimalFormat df = new DecimalFormat("0.000");
    private static String finals_path = "src/output/abalone_finals.csv";
    /**
     * Tests out the perceptron with the classic xor test
     * @param args ignored
     */
    public static void main(String[] args) {
        BackPropagationNetworkFactory factory = new BackPropagationNetworkFactory();

        /*double[][][] data = {
            { { 0 }, { 0 } },
            { { 0 }, { 1 } },
            { { 0 }, { 1 } },
        };
        Instance[] patterns = new Instance[data.length];
        for (int i = 0; i < patterns.length; i++) {
            patterns[i] = new Instance(data[i][0]);
            patterns[i].setLabel(new Instance(data[i][1]));
        }*/

        BackPropagationNetwork network = factory.createClassificationNetwork(
           new int[] {inputLayer, hiddenLayer, outputLayer});

        DataSet set = new DataSet(instances);
        ConvergenceTrainer trainer = new ConvergenceTrainer( new BatchBackPropagationTrainer(set, network,
                new SumOfSquaresError(), new RPROPUpdateRule()),
                1E-100, trainingIterations);

        double start = System.nanoTime(), end, trainingTime, testingTime, correct = 0, incorrect = 0;
        trainer.train();
        end = System.nanoTime();
        trainingTime = end - start;
        trainingTime /= Math.pow(10,9);

        double predicted, actual;
        start = System.nanoTime();
        for(int j = 0; j < instances.length; j++) {
            network.setInputValues(instances[j].getData());
            network.run();

            predicted = Double.parseDouble(instances[j].getLabel().toString());
            actual = Double.parseDouble(network.getOutputValues().toString());

            double trash = Math.abs(predicted - actual) < 0.5 ? correct++ : incorrect++;

        }
        end = System.nanoTime();
        testingTime = end - start;
        testingTime /= Math.pow(10,9);

        append("GD," + correct + "," + incorrect + "," + correct/(correct+incorrect)*100 + "," +
                trainingTime + "," + testingTime, finals_path);

        results +=  "\nResults for Gradient Descent: \nCorrectly classified " + correct + " instances." +
                "\nIncorrectly classified " + incorrect + " instances.\nPercent correctly classified: "
                + df.format(correct/(correct+incorrect)*100) + "%\nTraining time: " + df.format(trainingTime)
                + " seconds\nTesting time: " + df.format(testingTime) + " seconds\n";

        System.out.println("Convergence in " 
            + trainer.getIterations() + " iterations");

        System.out.println(results);
        for (int i = 0; i < instances.length; i++) {
            network.setInputValues(instances[i].getData());
            network.run();
            //System.out.println("~~");
            //System.out.println(instances[i].getLabel());
            //System.out.println(network.getOutputValues());
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

                for(int j = 0; j < 14; j++) {
                    attributes[i][0][j] = Double.parseDouble(scan.next());
                }

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