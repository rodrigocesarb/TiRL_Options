package ExperimentosAAAI;

import burlap.behavior.policy.EnumerablePolicy;
import burlap.behavior.policy.Policy;
import burlap.behavior.policy.support.ActionProb;
import burlap.behavior.singleagent.Episode;
import burlap.behavior.singleagent.options.EnvironmentOptionOutcome;
import burlap.behavior.singleagent.options.Option;
import burlap.mdp.auxiliary.stateconditiontest.StateConditionTest;
import burlap.mdp.auxiliary.stateconditiontest.StateConditionTestIterable;
import burlap.mdp.core.action.Action;
import burlap.mdp.core.state.State;
import burlap.mdp.singleagent.environment.Environment;

import java.util.List;


/**
 * A class for a classic subgoal Markov option. The option policy is specified with a {@link Policy} object,
 * the deterministic termination conditions with a {@link StateConditionTest} and the initiation set with a
 * {@link StateConditionTest}.
 * @author James MacGlashan
 *
 */
public class SubgoalOption2 implements Option {


	/**
	 * The name of the option
	 */
	protected String name;

	/**
	 * The policy of the options
	 */
	protected Policy						policy;
	
	/**
	 * The states in which the options can be initiated
	 */
	protected StateConditionTest2			initiationTest;
	
	/**
	 * The states in which the option terminates deterministically
	 */
	protected StateConditionTest2			terminationStates;


	/**
	 * A default constructor for serialization purposes. In general, you should use the {@link #SubgoalOption(String, Policy, StateConditionTest, StateConditionTest)}
	 * constructor.
	 */
	public SubgoalOption2() {
	}

	/**
	 * Initializes.
	 * @param name the name of the option
	 * @param p the option's policy
	 * @param initiationConditions the initiation states of the option
	 * @param terminationStates the deterministic termination states of the option.
	 */
	public SubgoalOption2(String name, Policy p, StateConditionTest2 initiationConditions, StateConditionTest2 terminationStates){
		this.name = name;
		this.policy = p;
		this.initiationTest = initiationConditions;
		this.terminationStates = terminationStates;
		
	}


	/**
	 * Returns the object defining the initiation states.
	 * @return the object defining the initiation states.
	 */
	public StateConditionTest2 getInitiationTest(){
		return this.initiationTest;
	}



	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public Policy getPolicy() {
		return policy;
	}

	public void setPolicy(Policy policy) {
		this.policy = policy;
	}

	public void setInitiationTest(StateConditionTest2 initiationTest) {
		this.initiationTest = initiationTest;
	}

	public StateConditionTest2 getTerminationStates() {
		return terminationStates;
	}

	public void setTerminationStates(StateConditionTest2 terminationStates) {
		this.terminationStates = terminationStates;
	}

	/**
	 * Returns true if the initiation states and termination states of this option are iterable; false if either of them are not.
	 * @return true if the initiation states and termination states of this option are iterable; false if either of them are not.
	 */
	public boolean enumerable(){
		return (initiationTest instanceof StateConditionTestIterable) && (terminationStates instanceof StateConditionTestIterable);
	}


	
	
	//add argumentos (hashtable)
	@Override
	public double probabilityOfTermination(State s, Episode history) {
		if(terminationStates.satisfies(s) || !policy.definedFor(s)){
			return 1.;
		}
		return 0.;
	}

	
	//add argumentos (hashtable)
	@Override
	public boolean inInitiationSet(State s) {
		return initiationTest.satisfies(s);
	}



	@Override
	public Action policy(State s, Episode history) {
		return policy.action(s);
	}


	@Override
	public List<ActionProb> policyDistribution(State s, Episode history) {
		if(!(policy instanceof EnumerablePolicy)){
			throw new RuntimeException("SubgoalOption cannot return policy distribution because underlying policy is not an EnumberablePolicy");
		}
		return ((EnumerablePolicy)policy).policyDistribution(s);
	}

	@Override
	public EnvironmentOptionOutcome control(Environment env, double discount) {
		return Option.Helper.control(this, env, discount);
	}

	@Override
	public boolean markov() {
		return true;
	}

	@Override
	public String actionName() {
		return this.name;
	}

	@Override
	public Action copy() {
		return new SubgoalOption2(name, this.policy, this.initiationTest, this.terminationStates);
	}

	@Override
	public boolean equals(Object o) {
		if(this == o) return true;
		if(o == null || getClass() != o.getClass()) return false;

		SubgoalOption2 that = (SubgoalOption2) o;

		return name.equals(that.name);

	}

	@Override
	public int hashCode() {
		return name.hashCode();
	}

	@Override
	public String toString() {
		return name;
	}
}
