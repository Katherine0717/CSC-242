package bn.inference;

import bn.base.BooleanValue;
import bn.base.Distribution;
import bn.base.*;
import bn.core.Assignment;
import bn.core.BayesianNetwork;
import bn.core.RandomVariable;
import bn.core.Value;
import javafx.util.Pair;

public class LikelihoodWeighting {

//    function LIKELIHOOD-WEIGHTING(X, e, bn, N) returns an estimate of P(X|e)
//      inputs: X , the query variable
//              e, observed values for variables E
//              bn, a Bayesian network specifying joint distribution P(X1, . . . , Xn)
//              N , the total number of samples to be generated
//      local variables: W, a vector of weighted counts for each value of X, initially zero
//
//      for j = 1 to N do
//          x, w ← WEIGHTED-SAMPLE(bn, e)
//          W[x] ← W[x] + w where x is the value of X inx
//      return NORMALIZE(W)

    public Distribution LikelihoodWeighting(RandomVariable X, Assignment e, BayesianNetwork bn, int N){
        Distribution w = new Distribution(X);
        for (Object o : X.getDomain()) {
            w.set((Value) o, 0);
        }

        Pair<Assignment, Double> wx;

        for(int i = 0; i < N; i++){
            wx = weightedSample(bn, e);
            double weight = wx.getValue();
            Assignment x = wx.getKey();

            w.set(x.get(X), w.get(x.get(X)) + weight);
        }

        w.normalize();
        return w;
    }

//    function WEIGHTED-SAMPLE(bn,e) returns an event and a weight
//      w ← 1; x ← an event with n elements initialized from e
//      for each variable Xi in X1,...,Xn do
//          if Xi is an evidence variable with value xi in e
//               then w ← w × P(Xi = xi | parents(Xi))
//               else x[i] ← a random sample from P(Xi | parents(Xi))
//    return x, w

    public Pair<Assignment, Double> weightedSample(BayesianNetwork bn, Assignment e){
        double weight = 1.0;
        Assignment x = e.copy();

        for(RandomVariable xi : bn.getVariablesSortedTopologically()){
            if(x.containsKey(xi)){
                weight *= bn.getProbability(xi, x);
            }else{
                x.put(xi, randomValue(bn, x, xi));
            }
        }
        return new Pair<Assignment, Double>(x, weight);
    }

    public Value randomValue(BayesianNetwork bn, Assignment e, RandomVariable var){
        double random = Math.random();
        Assignment temp = e.copy();
        Value T = new BooleanValue(true);
        Value F = new BooleanValue(false);
        temp.put(var, T);
        double prob = bn.getProbability(var, temp);
        if(random <= prob){
            return T;
        }else{
            return F;
        }
    }
}
