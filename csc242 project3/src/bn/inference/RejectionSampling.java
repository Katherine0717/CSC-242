package bn.inference;

import bn.base.Distribution;
import bn.core.Assignment;
import bn.core.BayesianNetwork;
import bn.core.RandomVariable;
import bn.core.Value;

import java.util.ArrayList;
import java.util.List;

public class RejectionSampling{

//    function REJECTION-SAMPLING(X, e, bn, N) returns an estimate of P(X|e)
//      inputs: X , the query variable
//              e, observed values for variables E
//              bn, a Bayesian network
//              N , the total number of samples to be generated
//      local variables: N, a vector of counts for each value of X , initially zero
//
//      for j = 1 to N do
//          x ← PRIOR-SAMPLE(bn)
//          if x is consistent with e then
//              N[x] ← N[x] + 1 where x is the value of X in x
//      return NORMALIZE(N)

    public Distribution rejectionSampling(RandomVariable X, Assignment e, BayesianNetwork bn, int N){
        Distribution d = new Distribution(X);
        for (Object o : X.getDomain()) {
            d.set((Value) o, 0);
        }

        for (int i = 0; i < N; i++) {
            Assignment x = priorSample(bn);
            if(consistent(x, e)) {
                d.put(x.get(X), d.get(x.get(X)) + 1);
            }
        }
        d.normalize();
        return d;
    }

//    function PRIOR-SAMPLE(bn) returns an event sampled from the prior specified by bn
//      inputs: bn, a Bayesian network specifying joint distribution P(X1, . . . , Xn)

//      x ← an event with n elements
//      for each variable Xi in X1,...,Xn do
//          x[i] ← a random sample from P(Xi | parents(Xi))
//      return x

    public Assignment priorSample(BayesianNetwork bn){
        Assignment x = new bn.base.Assignment();
        List<RandomVariable> Node = bn.getVariablesSortedTopologically();

        for(RandomVariable xi : Node){
            ArrayList <Double> values = new ArrayList <Double> ();
            int count = 0;
            for(Object var : xi.getDomain()){
                Assignment temp = x.copy();
                temp.put(xi, (Value) var);
                double prob = bn.getProbability(xi, temp);
                values.add(count++, prob);
            }

            double temp = 0;
            double random = Math.random();
            int count2 = 0;
            for(Object var : xi.getDomain()){
                temp += values.get(count2++);
                if (random <= temp) {
                    x.put(xi, (Value) var);
                    break;
                }
            }
        }
        return x;
    }

    public boolean consistent(Assignment x, Assignment e) {
        for(RandomVariable V: e.variableSet()){
            if(x.containsKey(V)){
                if(!e.get(V).equals(x.get(V))){
                    return false;
                }
            }
        }
        return true;
    }
}
