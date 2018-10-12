package func.nn.backprop;

import java.io.FileWriter;
import java.text.DecimalFormat;

import shared.DataSet;
import shared.GradientErrorMeasure;
import shared.Instance;
import func.nn.NetworkTrainer;

import java.io.IOException;

/**
 * A standard batch back propagation trainer
 * @author Andrew Guillory gtg008g@mail.gatech.edu
 * @version 1.0
 */
public class BatchBackPropagationTrainer extends NetworkTrainer {
    
    /**
     * The weight update rule to use
     */
    private WeightUpdateRule rule;
    
    /**
     * Make a new back propagation trainer
     * @param patterns the patterns to train on
     * @param network the network to train
     * @param errorMeasure the error measure to use
     */
    public BatchBackPropagationTrainer(DataSet patterns, 
            BackPropagationNetwork network, 
            GradientErrorMeasure errorMeasure,
            WeightUpdateRule rule) {
        super(patterns, network, errorMeasure);
        this.rule = rule;
    }

    private static DecimalFormat df = new DecimalFormat("0.000");

    /**
     * @see nn.Trainer#train()
     */
    public double train() {

        BackPropagationNetwork network =
            (BackPropagationNetwork) getNetwork();
        GradientErrorMeasure measure =
            (GradientErrorMeasure) getErrorMeasure();
        DataSet patterns = getDataSet();
        double error = 0;
        for (int i = 0; i < patterns.size(); i++) {
            Instance pattern = patterns.get(i);
            network.setInputValues(pattern.getData());
            network.run();
            Instance output = new Instance(network.getOutputValues());
            double[] errors = measure.gradient(output, pattern);
            error += measure.value(output, pattern);
            network.setOutputErrors(errors);
            network.backpropagate();
        }
        System.out.println(error);
        try {
            append(Double.toString(1 / error));
        } catch (Exception e) {
            e.getStackTrace();
        }
        network.updateWeights(rule);
        network.clearError();
        return error / patterns.size();
    }

    public void append(String data) throws IOException{
        FileWriter pw = new FileWriter("src/output/abalone_results.csv", true);
        pw.append("GD,");
        pw.append(data);
        pw.append("\n");
        pw.flush();
        pw.close();
    }

}
