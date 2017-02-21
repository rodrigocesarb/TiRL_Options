/**
 * 
 */
package ExperimentosAAMAS;

import java.io.BufferedReader;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;
import java.util.Random;

import burlap.behavior.policy.Policy;
import burlap.mdp.auxiliary.stateconditiontest.StateConditionTest;
import burlap.mdp.core.action.Action;
import burlap.mdp.core.state.State;

/**
 * @author Felipe Leno 
 * Combines multiple option files into a single non-deterministic option. Builds on the original code created by Rodrigo Bonini.
 */
public class MONonDetOption extends MOOption {
	Hashtable<List<Integer>, NodeOpt> option;
	/**
	 * Creates a MOOption from files describing individual options
	 * @param name Name of the option
	 * @param files files to read from
	 * @param hasher HashableStateFactory to be used
	 */
	public MONonDetOption(String name, String[] files,List<Action> allActions){
		super(name,null,null);
		this.option = readFromFiles(files,allActions);
		super.setPolicy(new NonDetExecutaOption(option));
		StateConditionTest initiationConditions = new StateConditionClass(true,option);
		super.setInitiationTest(initiationConditions);
		StateConditionTest terminationConditions = new StateConditionClass(false,option);
		super.setTerminationStates(terminationConditions);
	}


	/**
	 * Read the option files and combines them
	 * @param files
	 * @return
	 */
	private Hashtable<List<Integer>, NodeOpt> readFromFiles(String[] files,List<Action> allActions) {
		Hashtable<List<Integer>,Hashtable<String,Integer>> counts = new Hashtable<List<Integer>,Hashtable<String,Integer>>();
		Hashtable<List<Integer>, NodeOpt> retorno = new Hashtable<List<Integer>, NodeOpt>();
		try{
			for(String file: files){
				BufferedReader br = new BufferedReader(new FileReader(file));
				String line;
				br.readLine(); //header

				while ((line = br.readLine()) != null) {

					// use comma as separator
					String[] elements = line.split(",");
					String action = elements[0];
					List<Integer> state = new ArrayList<Integer>();
					state.add(Integer.parseInt(elements[1]));
					state.add(Integer.parseInt(elements[2]));

					if (!counts.containsKey(state)){
						//including one entry for each action
						Hashtable<String,Integer> hash = new Hashtable<String,Integer>();
						for(Action a: allActions){
							hash.put(a.actionName(),0);
						}

						counts.put(state, hash);
					}

					//summs up counters
					Hashtable<String,Integer> c = counts.get(state);
					c.put(action, c.get(action) + 1);

				}
			}
		}catch(Exception e){e.printStackTrace();}
		
		//Agregate all states in the option
		for (List<Integer> state : counts.keySet()){
			//Initiating options
			NodeOpt node = new NodeOpt();
			node.actions.addAll(allActions);
			for(int i=0;i<allActions.size();i++){
				node.probs.add(0f);
			}
			
			Hashtable<String,Integer> count = counts.get(state);
			int sum = 0;
			for (String ele : count.keySet())
				sum+=count.get(ele);
			for(String action : count.keySet()){
				int i=0;
				//Find action
				while(!allActions.get(i).equals(action))
					i++;
				//MERGE OPTIONS
				float prob = (float) (count.get(action) + 1) / sum + allActions.size();
				node.probs.set(i, prob);
			}
			retorno.put(state, node);
			
		}
		
		return retorno;
	}

	/**
	 * Actions and probabilities
	 * @author Felipe Leno da Silva
	 *
	 */
	protected class NodeOpt{
		public List<Action> actions;
		public List<Float> probs;
		public NodeOpt(){
			actions = new ArrayList<Action>();
			probs = new ArrayList<Float>();
		}
	}

	/**
	 *  Code for executing the option
	 * @author Felipe Leno da Silva
	 *
	 */
	public class NonDetExecutaOption implements Policy{

		Random r;

		Hashtable<List<Integer>, NodeOpt> option;


		public NonDetExecutaOption(Hashtable<List<Integer>, NodeOpt> option){
			this.option = option;
			r = new Random();

		}


		@Override
		public Action action(State s) {
			List<Integer> state= new ArrayList<Integer>();
			state.add((Integer) s.get("agent:x"));
			state.add((Integer) s.get("agent:y"));

			if(option.containsKey(state)){
				//Select actions with the specified prob
				float rGen = r.nextFloat();
				int i = -1; float sum = 0;

				List<Float> probs = this.option.get(state).probs;
				while(rGen>sum){
					i++;
					sum+=probs.get(i);
				}
				return this.option.get(state).actions.get(i);

			}
			//State not found in options, return null
			return null;
			//return option.get(state).actions.get(r.nextInt(option.get(state).actions.size()));
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
		Hashtable<List<Integer>, NodeOpt> option;

		boolean inicio;
		public StateConditionClass(boolean inicio,Hashtable<List<Integer>, NodeOpt> option){
			this.option = option;
			this.inicio = inicio;
		}
		@Override
		public boolean satisfies(State s) {
			List<Integer> state= new ArrayList<Integer>();
			state.add((Integer) s.get("agent:x"));
			state.add((Integer) s.get("agent:y"));
			boolean estaNaOption = option.containsKey(state);
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
