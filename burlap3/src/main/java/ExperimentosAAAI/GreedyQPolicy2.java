package ExperimentosAAAI;

import burlap.behavior.policy.EnumerablePolicy;
import burlap.behavior.policy.PolicyUtils;
import burlap.behavior.policy.SolverDerivedPolicy;
import burlap.behavior.policy.support.ActionProb;
import burlap.behavior.singleagent.MDPSolverInterface;
import burlap.behavior.valuefunction.QProvider;
import burlap.behavior.valuefunction.QValue;
import burlap.debugtools.RandomFactory;
import burlap.mdp.core.action.Action;
import burlap.mdp.core.state.State;
import burlap.statehashing.HashableState;

import javax.management.RuntimeErrorException;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;



/**
 * A greedy policy that breaks ties by randomly choosing an action amongst the tied actions. This class requires a QComputablePlanner
 * @author James MacGlashan
 *
 */
public class GreedyQPolicy2 implements SolverDerivedPolicy, EnumerablePolicy {

	protected QProvider qplanner;
	protected Random rand;




	public GreedyQPolicy2(){
		qplanner = null;
		rand = RandomFactory.getMapped(0);
	}


	/**
	 * Initializes with a QComputablePlanner
	 * @param planner the QComputablePlanner to use
	 */
	public GreedyQPolicy2(QProvider planner){
		qplanner = planner;
		rand = RandomFactory.getMapped(0);
	}

	@Override
	public void setSolver(MDPSolverInterface solver){

		if(!(solver instanceof QProvider)){
			throw new RuntimeErrorException(new Error("Planner is not a QComputablePlanner"));
		}

		this.qplanner = (QProvider) solver;
	}


	//aki!
	@Override
	public Action action(State s) {
		List<QValue> qValues = this.qplanner.qValues(s);
		List <QValue> maxActions = new ArrayList<QValue>();
		maxActions.add(qValues.get(0));
		double maxQ = qValues.get(0).q;


		//2
		double numeroRandom = Math.random();
		double somaProb = 0.0;
		double somatoriaQ = 0.0;
		for(int i = 0; i < qValues.size(); i++){
			QValue q = qValues.get(i); //(1)
			somatoriaQ += q.q;	//(2)

		}
		for(int i = 0; i < qValues.size(); i++){
			double prob; 
			QValue q = qValues.get(i); 
			prob = q.q/somatoriaQ; //(3)

			somaProb += prob; //(4)

			//se o numero random menor q a soma da probabilidade i, retorna a propria açao
			if (numeroRandom <= somaProb){
				return maxActions.get(i-1).a;

			}
		}

		//teoricamente nunca é pra cair no null, só ta ai pq é obrigatorio um retorno
		return null;


	}






	@Override
	public double actionProb(State s, Action a) {
		return PolicyUtils.actionProbFromEnum(this, s, a);
	}

	// nao precisa
	@Override
	public List<ActionProb> policyDistribution(State s) {
		List<QValue> qValues = this.qplanner.qValues(s);
		int numMax = 1;
		double maxQ = qValues.get(0).q;
		for(int i = 1; i < qValues.size(); i++){
			QValue q = qValues.get(i);
			if(q.q == maxQ){
				numMax++;
			}
			else if(q.q > maxQ){
				numMax = 1;
				maxQ = q.q;
			}
		}

		List <ActionProb> res = new ArrayList<ActionProb>();
		double uniformMax = 1./(double)numMax;
		for(int i = 0; i < qValues.size(); i++){
			QValue q = qValues.get(i);
			double p = 0.;
			if(q.q == maxQ){
				p = uniformMax;
			}
			ActionProb ap = new ActionProb(q.a, p);
			res.add(ap);
		}


		return res;
	}


	// nao precisa
	@Override
	public boolean definedFor(State s) {
		return true; //can always find q-values with default value
	}

//
//	@Override
//	public Action action(HashableState curState) {
//		// TODO Auto-generated method stub
//		return null;
//	}







}
