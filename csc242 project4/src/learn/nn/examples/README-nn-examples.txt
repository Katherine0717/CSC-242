README for nn.examples

For XOR (AIMA p. 730) and Majority (AIMA p. 731), it is easier to simply generate
the desired number of examples of the functions than to create files of them.

Iris
 - https://archive.ics.uci.edu/ml/datasets/iris
 - iris.names.txt, iris.data.txt
 - I found a reference somewhere online that this dataset can be learned by an
   NN with four inputs, seven hidden units, and 3 output units (1 output/species).
   YMMV.
 
 MNIST
 - http://yann.lecun.com/exdb/mnist/
 - https://archive.ics.uci.edu/ml/databases/mnist/
 - The LeCun webpage lists several configurations of 2- and 3-layer NNs that
   have been used on this problem, and their accuracy on the test set. For example,
   a 2-layer network with 300, 800, or 1000 hidden units yielding 1.6-4.7% test
   error rate. Again, YMMV (that's life with NNs).
 
You should write your own main methods to demonstrate your neural network implementation(s) and produce the data needed for your report.

Running a NeuralNetwork should generally do the following:
- Read the data from a file
- Create the appropriate kind of NeuralNetwork with the appropriate number of inputs and outputs for the data
- Train the network on the data
  - Printing the information needed for graphs in your report after each step

Examples of what you might want to print (and then include in your report, usually as graphs):
- final accuracy on the training data, the testing data, or both (probably separately)
- confusion matrix
- results of cross-fold validation
- per-epoch accuracy suitable for plotting in a learning curve

See AIMA for ideas and examples.
