package learn.nn.examples;

import java.io.IOException;
import java.util.Date;
import java.util.List;

import learn.nn.core.Example;
import learn.nn.core.MultiLayerFeedForwardNeuralNetwork;
import learn.nn.core.NeuralNetwork;
import learn.nn.core.NeuralNetworkListener;

public class MNISTNeuralNetwork extends MultiLayerFeedForwardNeuralNetwork {
    protected static final int NUM_INPUTS = 28*28;
    protected static final int NUM_HIDDENS = 300;
    protected static final int NUM_OUTPUTS = 10;

    public MNISTNeuralNetwork() {
        super(NUM_INPUTS, NUM_HIDDENS, NUM_OUTPUTS);
    }

    public static void main(String[] argv) throws IOException {
        int epochs = 100;
        double alpha = 0.10;
        MNISTNeuralNetwork network = new MNISTNeuralNetwork();
        System.out.println("Reading the file...");
        List<Example> trainingSet = MNISTRead.read("learn/nn/examples/t10k-images-idx3-ubyte", "learn/nn/examples/t10k-labels-idx1-ubyte");
        List<Example> testingSet = MNISTRead.read("learn/nn/examples/t10k-images-idx3-ubyte", "learn/nn/examples/t10k-labels-idx1-ubyte");
        System.out.println("Printing the result (it might take a while)...");
        System.out.println("EPOCH\tACC\tHHMMSS");
        network.addListener(new NeuralNetworkListener() {
            protected long startTime;
            public void trainingEpochStarted(NeuralNetwork network, int epoch) {
                startTime = new Date().getTime();
            }
            public boolean trainingEpochCompleted(NeuralNetwork network, int epoch) {
                long now = new Date().getTime();
                double accuracy = network.test(testingSet);
                long elapsed = now - startTime;
                long s = elapsed / 1000;
                long h = s / (60*60);
                s -= h * 60*60;
                long m = s / 60;
                s -= m*60;
                System.out.format("%d\t%.3f\t%02d:%02d:%02d\n", epoch, accuracy, h, m, s);
                return true;
            }
        });
        network.train(trainingSet, epochs, alpha);
    }

}
