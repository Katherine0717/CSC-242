package bn.inference;

import java.util.List;
import java.util.Map;
import java.util.Set;

import bn.base.BayesianNetwork;
import bn.base.BooleanDomain;
import bn.base.BooleanValue;
import bn.base.Distribution;
import bn.base.NamedVariable;
import bn.core.Assignment;
import bn.core.CPT;
import bn.core.Inferencer;
import bn.core.RandomVariable;
import bn.util.ArrayMap;
import bn.util.ArraySet;

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
	
	public Distribution LikelihoodWeight(RandomVariable X, Assignment e, BayesianNetwork bn, int N) {
		bn.base.Distribution answer =new bn.base.Distribution(X);
		for(int j=0;j<N;j++) {
			Map<Assignment, Double> weightMap=new ArrayMap<Assignment, Double>();
			weightMap=WeightedSample(bn,e);
			for(Assignment ass:weightMap.keySet()) {
				if(!answer.containsKey(ass.get(X))) {
					answer.put(ass.get(X), weightMap.get(ass));
				}else {
					answer.put(ass.get(X), (answer.get(ass.get(X))+weightMap.get(ass)));
				}
			}
		}
		answer.normalize();
		return answer;
	}

//    function WEIGHTED-SAMPLE(bn,e) returns an event and a weight
//      w ← 1; x ← an event with n elements initialized from e
//      for each variable Xi in X1,...,Xn do
//          if Xi is an evidence variable with value xi in e
//               then w ← w × P(Xi = xi | parents(Xi))
//               else x[i] ← a random sample from P(Xi | parents(Xi))
//    return x, w
	
	public Map<Assignment, Double> WeightedSample(BayesianNetwork bn, Assignment e){
		Map<Assignment, Double> answerMap=new ArrayMap<Assignment, Double>();
		double w=1.0;
		Assignment xMap=e.copy();
		List<RandomVariable> list=bn.getVariablesSortedTopologically();
		for(RandomVariable var: list) {
			if(e.containsKey(var)) {
				w=w*bn.getProbability(var, xMap);
			}else {
				bn.core.Value value=RejectionSampling.RandomSample(var, xMap, bn);
				xMap.put(var, value);
			}
		}
		answerMap.put(xMap, w);
		return answerMap;
	}
}
