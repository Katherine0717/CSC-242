package bn.inference;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Set;

import bn.base.BayesianNetwork;
import bn.base.BooleanDomain;
import bn.base.BooleanValue;
import bn.base.NamedVariable;
import bn.core.Assignment;
import bn.core.CPT;
import bn.core.Distribution;
import bn.core.Inferencer;
import bn.core.RandomVariable;
import bn.core.Value;
import bn.util.ArrayMap;
import bn.util.ArraySet;

public class RejectionSampling{

//    function PRIOR-SAMPLE(bn) returns an event sampled from the prior specified by bn
//      inputs: bn, a Bayesian network specifying joint distribution P(X1, . . . , Xn)
//      x ← an event with n elements
//      for each variable Xi in X1,...,Xn do
//          x[i] ← a random sample from P(Xi | parents(Xi))
//      return x

	public Map<RandomVariable, Value> PriorSample(BayesianNetwork bn){
		Map<RandomVariable, Value> xMap=new ArrayMap<RandomVariable, Value>();
		List<RandomVariable> list=bn.getVariablesSortedTopologically();
		Assignment a=new bn.base.Assignment();
		for(RandomVariable var: list) {
			bn.core.Value value=RandomSample(var, a, bn);
			xMap.put(var, value);
			a.put(var, value);
		}
		return xMap;
	}

	public static bn.core.Value RandomSample(RandomVariable X, Assignment a, BayesianNetwork network) {
		Map<Value, Double[]> chartMap=new HashMap<Value, Double[]>();
		double sum=0.0;
		//Create a Double[] to store each interval
		Double[] interval=new Double[X.getDomain().size()];
		for (Value value: X.getDomain()) {
			interval=new Double[X.getDomain().size()];
			a.put(X, value);
			for(int i=0;i<interval.length;i++) {
				interval[i]=sum;
				if(i!=interval.length-1) {
					sum=sum+network.getProbability(X, a);
				}
			}
			chartMap.put(value, interval);
		}
		for(int i=0;i<interval.length;i++) {
		}
		Random r=new Random();
		double random=r.nextDouble();
		//Find what value this variable belongs to
		for (Value value: chartMap.keySet()) {
			for(int i=0;i<interval.length-1;i++) {
				if(random>=chartMap.get(value)[i]&&random<=chartMap.get(value)[i+1]) {
					return value;
				}
			}
		}
		return null;
	}

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

	public Distribution RejectionSample(RandomVariable X, Assignment e, BayesianNetwork bn, int N) {
		bn.base.Distribution answer =new bn.base.Distribution(X);
		for(int j=0;j<N;j++) {
			Map<RandomVariable, Value> sampleMap=PriorSample(bn);
			if(isConsistent(sampleMap,e)) {
				if(!answer.containsKey(sampleMap.get(X))) {
					answer.put(sampleMap.get(X), (double)1);
				}else {
					answer.put(sampleMap.get(X), answer.get(sampleMap.get(X))+1);
				}
			}
		}
		answer.normalize();
		return answer;
	}

	public boolean isConsistent(Map<RandomVariable, Value> sample,Assignment e) {
		for(RandomVariable varE: e.keySet()) {
			if(!sample.get(varE).equals(e.get(varE))) {
				return false;
			}
		}
		return true;
	}
}
