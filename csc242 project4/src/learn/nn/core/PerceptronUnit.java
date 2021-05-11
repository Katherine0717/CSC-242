package learn.nn.core;

import java.util.List;
import learn.math.util.VectorOps;

/**
 * A PerceptronUnit is a Unit that uses a hard threshold
 * activation function.
 */
public class PerceptronUnit extends NeuronUnit {

    /**
     * The activation function for a Perceptron is a hard 0/1 threshold
     * at z=0. (AIMA Fig 18.7)
     */
    @Override
    public double activation(double z) {
        // This must be implemented by you
        if(z>=0) {
            return 1.0;
        }
        else {
            return 0.0;
        }
    }

    @Override
    public double activationPrime(double in) {
        return 0;
    }

    /**
     * Update this unit's weights using the Perceptron learning
     * rule (AIMA Eq 18.7).
     * Remember: If there are n input attributes in vector x,
     * then there are n+1 weights including the bias weight w_0.
     */
    @Override
    public void update(double[] x, double y, double alpha) {
        // This must be implemented by you
        List<Connection> incomingConnections = this.incomingConnections;
        double W[] = new double[incomingConnections.size()];
        for(int i = 0; i < incomingConnections.size(); i++){
            W[i] = incomingConnections.get(i).weight; // get W the weight vector
        }

        double X[] = new double[x.length+1];
        X[0] = 1;
        for(int i = 0; i < x.length; i++){
            X[i+1] = x[i]; // get X vector
        }

        double hw = this.activation(VectorOps.dot(W, X)); // get hw(x)

        for(int i = 0; i < X.length; i++){
            incomingConnections.get(i).weight = incomingConnections.get(i).weight + (alpha * (y - hw)) * X[i];
        }
    }
}
