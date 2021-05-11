package bn.inference;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Set;

import bn.base.*;
import bn.core.Assignment;
import bn.core.CPT;
import bn.core.Inferencer;
import bn.core.RandomVariable;
import bn.core.Value;
import bn.util.ArraySet;

public class GibbsSampling {
	public Distribution GibbsAsk (RandomVariable X, Assignment e, BayesianNetwork bn, int N) {
		Distribution answer =new Distribution(X);
		Assignment xAssignment=e.copy();
		List<RandomVariable> var=bn.getVariablesSortedTopologically();
		List<RandomVariable> z_not_evidence=var; // nonevidence variables
		
		//Generate the non evidence random variable list z
		for(RandomVariable variable: e.keySet()) {
			if(var.contains(variable)) {
				z_not_evidence.remove(variable);
			}
		}
		
		//Initialize randomly
		xAssignment=Initialization(xAssignment,z_not_evidence);
		
		for(int j=0;j<N;j++) {
			for(RandomVariable RV: z_not_evidence){
				Value val=MBSample(RV, xAssignment, bn, var);
				xAssignment.put(RV, val);
				if(!answer.containsKey(xAssignment.get(X))) {
					answer.put(xAssignment.get(X), (double)1);
				}else {
					answer.put(xAssignment.get(X), answer.get(xAssignment.get(X))+1);
				}
			}
		}
		answer.normalize();
		return answer;
	}

	public Assignment Initialization (Assignment xAssignment, List<RandomVariable> notEvidence) {
		List<RandomVariable> z_copy=notEvidence;
		Random r=new Random();
		StringValue TRUE = new StringValue("true");
		StringValue FALSE = new StringValue("false");
		double value=0.0;
		for(RandomVariable var: z_copy) {
			value=r.nextDouble();
			if(value<0.5) {
				xAssignment.put(var, TRUE);
			}
			else {
				xAssignment.put(var, FALSE);
			}
		}
		return xAssignment;
	}

	public Value MBSample(RandomVariable RV, Assignment xAssignment, BayesianNetwork bn, List<RandomVariable> var) {
		Assignment new_Assignment=xAssignment.copy();
		Set<RandomVariable> parentSet=bn.getParents(RV);
		Set<RandomVariable> childrenSet=bn.getChildren(RV);
		for(RandomVariable children: childrenSet) {
			parentSet.addAll(bn.getParents(children));
		}
		parentSet.addAll(childrenSet);
		//find the Markov blanket variables
		List<RandomVariable> MBList=new ArrayList<RandomVariable>();
		for(RandomVariable vars: parentSet) {
			if(!vars.equals(RV)) {
				MBList.add(vars);
			}
		}

		bn.base.Distribution gibbDistribution =new bn.base.Distribution(RV);
		for (Value domain:RV.getDomain()) {
			new_Assignment.put(RV,domain);
			double prob=1.0;
			prob=prob*bn.getProbability(RV, new_Assignment);
			for(RandomVariable MB:MBList) {
				prob=prob*bn.getProbability(MB, new_Assignment);
			}
			gibbDistribution.put(domain, prob);
		}
		gibbDistribution.normalize();

		//Random a value for this sampled variable
		Map<Value, Double[]> chartMap=new HashMap<Value, Double[]>();
		double sum=0.0;
		//Create a Double[] to store each interval
		Double[] interval=new Double[RV.getDomain().size()];
		for (Value value: RV.getDomain()) {
			interval=new Double[RV.getDomain().size()];
			for(int i=0;i<interval.length;i++) {
				interval[i]=sum;
				if(i!=interval.length-1) {
					sum=sum+gibbDistribution.get(value);
				}
			}
			chartMap.put(value, interval);
		}
		Random r=new Random();
		double random=r.nextDouble();

		//Find what value this variable belongs to 
		for (Value value: chartMap.keySet()) {
			for(int i=0;i<interval.length-1;i++) {
				if(random>chartMap.get(value)[i]&&random<chartMap.get(value)[i+1]) {
					return value;
				}
			}
		}
		return null;
	}

}
