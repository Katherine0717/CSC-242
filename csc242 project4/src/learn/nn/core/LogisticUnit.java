package learn.nn.core;

import learn.math.util.VectorOps;

import java.util.List;

/**
 * A LogisticUnit is a Unit that uses a sigmoid
 * activation function.
 */
public class LogisticUnit extends NeuronUnit {

    /**
     * The activation function for a LogisticUnit is a 0-1 sigmoid
     * centered at z=0: 1/(1+e^(-z)). (AIMA Fig 18.7)
     */
    @Override
    public double activation(double z) {
        // This must be implemented by you
        return 1.0/(1.0+Math.exp(-z));
    }
//
    /**
     * Derivative of the activation function for a LogisticUnit.
     * For g(z)=1/(1+e^(-z)), g'(z)=g(z)*(1-g(z)) (AIMA p. 727).
     * @see https://calculus.subwiki.org/wiki/Logistic_function#First_derivative
     */
    public double activationPrime(double z) {
        double y = activation(z);
        return y * (1.0 - y);
    }

    /**
     * Update this unit's weights using the logistic regression
     * gradient descent learning rule (AIMA Eq 18.8).
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
            incomingConnections.get(i).weight = incomingConnections.get(i).weight + (alpha * (y - hw) * hw * (1 - hw)) * X[i];
        }
    }

}
