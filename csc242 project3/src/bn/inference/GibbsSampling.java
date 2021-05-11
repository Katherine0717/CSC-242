package bn.inference;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
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
import bn.core.Value;
import bn.util.ArraySet;

public class GibbsSampling {
    public static Distribution GibbsAsk (RandomVariable X, Assignment e, BayesianNetwork bn, int N) {
        bn.base.Distribution answer =new bn.base.Distribution(X);
        Assignment xAssignment=e.copy();
        List<RandomVariable> var=bn.getVariablesSortedTopologically();
        List<RandomVariable> z_not_evidence=var;
        //Generate the non evidence random variable list z
        for(RandomVariable variable: e.keySet()) {
            if(var.contains(variable)) {
                z_not_evidence.remove(variable);
            }
        }
        //System.out.println(z_not_evidence);
        xAssignment=Initialization(xAssignment,z_not_evidence);
        //System.out.println(xAssignment);
        for(int j=0;j<N;j++) {
            for(RandomVariable RV: z_not_evidence) {
                Value val=MBSample(RV, xAssignment, bn, var);
                //System.out.println("The before assignment is "+xAssignment);
                xAssignment.put(RV, val);
                //System.out.println("The after assignment is "+xAssignment);
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

    public static Assignment Initialization (Assignment xAssignment, List<RandomVariable> notEvidence) {
        List<RandomVariable> z_copy=notEvidence;
        Random r=new Random();
        BooleanValue TRUE = BooleanValue.TRUE;
        BooleanValue FALSE = BooleanValue.FALSE;
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

    public static Value MBSample(RandomVariable RV, Assignment xAssignment, BayesianNetwork bn, List<RandomVariable> var) {
        //System.out.println("The random variable is "+RV);
        //System.out.println("The assignment is "+xAssignment);
        Assignment new_Assignment=xAssignment.copy();
        Set<RandomVariable> parentSet=bn.getParents(RV);
        Set<RandomVariable> childrenSet=bn.getChildren(RV);
        for(RandomVariable children: childrenSet) {
            parentSet.addAll(bn.getParents(children));
        }
        parentSet.addAll(childrenSet);
        List<RandomVariable> MBList=new ArrayList<RandomVariable>();
        //System.out.println("All its MB is "+parentSet);
        for(RandomVariable vars: parentSet) {
            if(!vars.equals(RV)) {
                MBList.add(vars);
            }
        }
        //System.out.println("Final list is "+MBList);
        bn.base.Distribution gibbDistribution =new bn.base.Distribution(RV);
        for (Value domain:RV.getDomain()) {
            new_Assignment.put(RV,domain);
            //System.out.println("The new_Ass is"+new_Assignment);
            double prob=1.0;
            prob=prob*bn.getProbability(RV, new_Assignment);
            //System.out.println("The probability is"+prob);
            for(RandomVariable MB:MBList) {
                prob=prob*bn.getProbability(MB, new_Assignment);
                //System.out.println("The children " +MB+" probability is"+prob);
            }
            gibbDistribution.put(domain, prob);
            //System.out.println("The gibbs distribution is "+gibbDistribution);
        }
        gibbDistribution.normalize();
        //System.out.println("The gibbs distribution after normalization is "+gibbDistribution);

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
        //System.out.println("The random number is"+random);
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

    public static void main(String[] args) {
        // TODO Auto-generated method stub
        RandomVariable C = new NamedVariable("C", new BooleanDomain());
        RandomVariable S = new NamedVariable("S", new BooleanDomain());
        RandomVariable R = new NamedVariable("R", new BooleanDomain());
        RandomVariable W = new NamedVariable("W", new BooleanDomain());
        BayesianNetwork bn = new bn.base.BayesianNetwork();
        bn.add(C);
        bn.add(S);
        bn.add(R);
        bn.add(W);
        // Shorthands
        BooleanValue TRUE = BooleanValue.TRUE;
        BooleanValue FALSE = BooleanValue.FALSE;
        Assignment a;

        // C (no parents)
        CPT Bprior = new bn.base.CPT(C);
        a = new bn.base.Assignment();
        Bprior.set(TRUE, a, 0.5);
        Bprior.set(FALSE, a, 1-0.5);
        bn.connect(C, new ArraySet<RandomVariable>() , Bprior);

        // C -> S
        Set<RandomVariable> justC = new ArraySet<RandomVariable>();
        justC.add(C);
        CPT SgivenC = new bn.base.CPT(S);
        a = new bn.base.Assignment();
        a.put(C, TRUE);
        SgivenC.set(TRUE, a, 0.1);
        SgivenC.set(FALSE, a, 1-0.1);
        a = new bn.base.Assignment();
        a.put(C, FALSE);
        SgivenC.set(TRUE, a, 0.5);
        SgivenC.set(FALSE, a, 1-0.5);
        bn.connect(S, justC, SgivenC);

        // C -> R
        justC.add(C);
        CPT RgivenC = new bn.base.CPT(R);
        a = new bn.base.Assignment();
        a.put(C, TRUE);
        RgivenC.set(TRUE, a, 0.8);
        RgivenC.set(FALSE, a, 1-0.8);
        a = new bn.base.Assignment();
        a.put(C, FALSE);
        RgivenC.set(TRUE, a, 0.2);
        RgivenC.set(FALSE, a, 1-0.2);
        bn.connect(R, justC, RgivenC);

        // S,R -> W
        Set<RandomVariable> SR = new ArraySet<RandomVariable>();
        SR.add(S);
        SR.add(R);
        CPT WgivenSR = new bn.base.CPT(W);
        a = new bn.base.Assignment();
        a.put(S, TRUE);
        a.put(R, TRUE);
        WgivenSR.set(TRUE, a, 0.99);
        WgivenSR.set(FALSE, a, 1-0.99);
        a = new bn.base.Assignment();
        a.put(S, TRUE);
        a.put(R, FALSE);
        WgivenSR.set(TRUE, a, 0.90);
        WgivenSR.set(FALSE, a, 1-0.90);
        a = new bn.base.Assignment();
        a.put(S, FALSE);
        a.put(R, TRUE);
        WgivenSR.set(TRUE, a, 0.90);
        WgivenSR.set(FALSE, a, 1-0.90);
        a = new bn.base.Assignment();
        a.put(S, FALSE);
        a.put(R, FALSE);
        WgivenSR.set(TRUE, a, 0.0);
        WgivenSR.set(FALSE, a, 1-0.0);
        bn.connect(W, SR, WgivenSR);

        System.out.println(bn);

        System.out.println("P(Rain|Sprinkler=true) = <0.3,0.7>");
//        Inferencer exact = new EnumerationInferencer();
        a = new bn.base.Assignment();
        a.put(S, TRUE);
        System.out.println(GibbsAsk(R,a,bn,100000));
        //System.out.println(dist);

    }

}
