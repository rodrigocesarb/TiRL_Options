package ExperimentosAAMAS;

import java.util.ArrayList;
import java.util.List;


import burlap.mdp.core.action.Action;
import burlap.mdp.core.state.State;
import burlap.mdp.singleagent.model.RewardFunction;


/**
 * Defines a reward function that always returns -1.
 * @author James MacGlashan
 *
 */
public class UniformCostRF2 implements RewardFunction {


//w1 = peso do objetivo 1
//w2 = peso do objetivo 2

	RewardFunction rf1,rf2;

	double w1=0.8, w2=0.2;

	public UniformCostRF2(RewardFunction rf1, RewardFunction rf2) {
	
		this.rf1 = rf1;
		this.rf2 = rf2;

	
	}
	
	public UniformCostRF2(){
	
	}

	public UniformCostRF2(RewardFunction rf1, RewardFunction rf2, double w1, double w2) {
		this.w1 = w1;
		this.w2 = w2;

		this.rf1 = rf1;
		this.rf2 = rf2;

	}


	//colocar um if se estiver em cima de algum ouro para dar recompensa assim que pegar o ouro e mudar essa funçao
	//	@Override
	//	public double reward(State s, Action a, State sprime) {
	//
	//		return -1;
	//	}

	public double reward(State s, Action a, State sprime){
		double reward1 = rf1.reward(s, a, sprime);
		double reward2 = rf2.reward(s, a, sprime);
		
		double finalReward = reward1 * w1 + reward2 * w2;

		return finalReward;
	}
	

	



}
