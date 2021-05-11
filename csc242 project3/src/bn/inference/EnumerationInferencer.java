package bn.inference;

import bn.base.Distribution;
import bn.core.Assignment;
import bn.core.BayesianNetwork;
import bn.core.RandomVariable;

import java.util.ArrayList;
import java.util.List;

public class EnumerationInferencer{

//    function ENUMERATION-ASK(X , e, bn) returns a distribution over X
//      inputs: X , the query variable
//              e, observed values for variables E
//              bn, a Bayes net with variables {X} ∪ E ∪ Y /* Y = hidden variables */
//      Q(X ) ← a distribution over X , initially empty
//      for each value xi of X do
//          Q(xi) ← ENUMERATE-ALL(bn.VARS, exi ) where exi is e extended with X = xi
//      return NORMALIZE(Q(X))

    public Distribution EnumerationInferencer(RandomVariable X, Assignment e, BayesianNetwork network){
       Distribution Q = new Distribution(X);
       for(Object x : X.getDomain()){ // X = true, X = false
           Assignment ex = e.copy();
           ex.put(X, (bn.core.Value) x);
           List<RandomVariable> var = network.getVariablesSortedTopologically();
           Q.set((bn.core.Value) x, EnumerateAll(var, ex, network));
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

    public double EnumerateAll(List<RandomVariable> var, Assignment e, BayesianNetwork bn) {
        List<RandomVariable> vars = new ArrayList<>();
        for (RandomVariable rv : var){
            vars.add(rv);
        }

        if(vars.size()==0){
            return 1.0;
        }

        RandomVariable Y = vars.get(0);
        vars.remove(0); //REST(vars)

        if(e.containsKey(Y)){
            return bn.getProbability(Y, e) * EnumerateAll(vars, e, bn);
        }else{
            double sum = 0.0;
            for(Object y : Y.getDomain()){
                Assignment ey = e.copy();
                ey.put(Y, (bn.base.Value) y);
                sum = sum + bn.getProbability(Y, ey) * EnumerateAll(vars, ey, bn);
            }
            return sum;
        }
    }

}
