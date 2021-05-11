package bn.inference;

import java.util.List;
import java.util.Set;

import bn.base.BooleanDomain;
import bn.base.BooleanValue;
import bn.base.NamedVariable;
import bn.core.Assignment;
import bn.core.BayesianNetwork;
import bn.core.CPT;
import bn.core.Distribution;
import bn.core.Inferencer;
import bn.core.RandomVariable;
import bn.util.ArraySet;

public class EnumerationInferencer implements bn.core.Inferencer{

//    function ENUMERATION-ASK(X , e, bn) returns a distribution over X
//      inputs: X , the query variable
//              e, observed values for variables E
//              bn, a Bayes net with variables {X} ∪ E ∪ Y /* Y = hidden variables */
//      Q(X ) ← a distribution over X , initially empty
//      for each value xi of X do
//          Q(xi) ← ENUMERATE-ALL(bn.VARS, exi ) where exi is e extended with X = xi
//      return NORMALIZE(Q(X))

	public Distribution query(RandomVariable X, Assignment e, BayesianNetwork network) {
		bn.base.Distribution Q=new bn.base.Distribution(X);
		for(bn.core.Value value: X.getDomain()) {
			e.put(X, value);
			Q.set(value, EnumerateAll(network.getVariablesSortedTopologically(),e,network));
		}
		Q.normalize();
		return Q;
	}

//    function ENUMERATE-ALL(vars, e) returns a real number
//       if EMPTY?(vars) then return 1.0
//       Y ← FIRST(vars)
//      if Y has value y in e
//          then return P (y | parents(Y )) × ENUMERATE-ALL(REST(vars), e)
//          else return􏰁 P(y|parents(Y)) × ENUMERATE-ALL(REST(vars), ey)
//               where ey is e extended with Y = y

	public double EnumerateAll(List<RandomVariable> vars, Assignment e, BayesianNetwork network) {
		if(vars.isEmpty()) {
			return 1.0;
		}
		RandomVariable Y=vars.get(0);
		if(e.containsKey(Y)) {
			return network.getProbability(Y, e) * EnumerateAll(vars.subList(1, vars.size()), e, network);
		}else {
			double sum=0.0;
			for(bn.core.Value value: Y.getDomain()) {
				Assignment newAssignment=e.copy();
				newAssignment.put(Y, value);
				sum+=network.getProbability(Y, newAssignment)* EnumerateAll(vars.subList(1, vars.size()), newAssignment, network);
			}
			return sum;
		}
	}
}
