package learn.nn.examples;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import learn.nn.core.Example;
import learn.nn.core.MultiLayerFeedForwardNeuralNetwork;

public class IrisNeuralNetwork {

    public static List<Example> readData(String Filename) throws IOException {
        List<Example> examples = new ArrayList<>(150);
        BufferedReader bufferedReader = new BufferedReader(new FileReader(Filename));
        String curline;
        while((curline = bufferedReader.readLine()) != null) {
            String[] num = curline.split(",");
            Example example = new Example(4, 3);
            if(num[0].equals("")){
                break;
            }
            double input1 = Double.parseDouble(num[0]);
            double input2 = Double.parseDouble(num[1]);
            double input3 = Double.parseDouble(num[2]);
            double input4 = Double.parseDouble(num[3]);
            String label = num[4];
            double[] inputs = {input1, input2, input3, input4};
            double[] outputs = {0.0, 0.0, 0.0};
            if (label.startsWith("Iris-setosa")) {
                outputs[0] = 1.0;
            } else if (label.startsWith("Iris-versicolor")) {
                outputs[1] = 1.0;
            } else if (label.startsWith("Iris-virginica")) {
                outputs[2] = 1.0;
            }
            example.inputs = inputs;
            example.outputs = outputs;
            examples.add(example);
        }
        bufferedReader.close();
        return examples;
    }

    public static void main(String[] argv) throws IOException {
        int epochs = 1000;
        double alpha = 0.10;
        if (argv.length > 0) {
            epochs = Integer.parseInt(argv[0]);
        }
        if (argv.length > 1) {
            alpha = Double.parseDouble(argv[1]);
        }
        System.out.println("Reading file...");
        List<Example> examples = readData("learn/nn/examples/iris.data.txt");
        MultiLayerFeedForwardNeuralNetwork MLFFNN = new MultiLayerFeedForwardNeuralNetwork(4, 7, 3);
        System.out.println("Epochs: " + epochs + "\talpha = " + alpha);
        MLFFNN.train(examples, epochs, alpha);
        MLFFNN.dump();
        double accuracy = MLFFNN.test(examples);
        System.out.println("Total accuracy=" + accuracy);
        System.out.println();

        int n = examples.size();
        int k = 10;
        System.out.println("k-Fold Cross-Validation with k = " + k);
        double acc = MLFFNN.kFoldCrossValidate(examples, k, epochs, alpha);
        System.out.format("average accuracy: %.3f\n", acc);
        System.out.println();
        System.out.println("Learning Curve testing on all training data");
        System.out.println("Epochs\tAccurary");
        for (epochs = 100; epochs <= 3000; epochs += 100) {
            MLFFNN.train(examples, epochs, alpha);
            accuracy = MLFFNN.test(examples);
            System.out.format("%d\t%.3f\n", epochs, accuracy);
        }
    }

}