package ExperimentosAAAI;

import java.util.Hashtable;

import burlap.behavior.policy.Policy;
import burlap.behavior.singleagent.options.SubgoalOption;
import burlap.mdp.auxiliary.stateconditiontest.StateConditionTest;
import burlap.mdp.core.action.Action;
import burlap.mdp.core.state.State;
import burlap.statehashing.HashableState;
import burlap.statehashing.HashableStateFactory;

public class MOOption extends SubgoalOption{
    

	protected Hashtable<HashableState, Action> option;

	public MOOption(String name, Hashtable<HashableState, Action> option, HashableStateFactory hasher) {
		super();
		this.option = option;
		super.setName(name);
		StateConditionTest initiationConditions = new StateConditionClass(true,option,hasher);
		super.setInitiationTest(initiationConditions);
		StateConditionTest terminationConditions = new StateConditionClass(false,option,hasher);
		super.setTerminationStates(terminationConditions);
		super.setPolicy(new ExecutaOption(option,hasher));
		
		
	}
	
		
		
	public class ExecutaOption implements Policy{
		Hashtable<HashableState, Action> option;
		HashableStateFactory hasher;
		public ExecutaOption(Hashtable<HashableState, Action> option, HashableStateFactory hasher){
			this.option = option;
			this.hasher = hasher;
		}
		@Override
		public Action action(State s) {
			HashableState hs= this.hasher.hashState(s);
			boolean estaNaOption = this.option.containsKey(hs);
			
			if(estaNaOption){
				return this.option.get(hs);
			}
			throw new RuntimeException("Tentando executar uma option em um estado em que ela nao existe");
		}

		@Override
		public double actionProb(State s, Action a) {
			throw new RuntimeException("Nao Implementado");
		}

		@Override
		public boolean definedFor(State s) {
			return true;
		}
		
	}
	
	
	public class StateConditionClass implements StateConditionTest {
		Hashtable<HashableState, Action> option;
		HashableStateFactory hasher;
		boolean inicio;
		public StateConditionClass(boolean inicio,Hashtable<HashableState, Action> option, HashableStateFactory hasher){
			this.option = option;
			this.hasher = hasher;
			this.inicio = inicio;
		}
		@Override
		public boolean satisfies(State s) {
			HashableState hs= this.hasher.hashState(s);
			boolean estaNaOption = option.containsKey(hs);
			boolean retorno;
			if(this.inicio){
				retorno = estaNaOption;
			}else{
				retorno = !estaNaOption;
			}
			return retorno;
		}
	
	}
}