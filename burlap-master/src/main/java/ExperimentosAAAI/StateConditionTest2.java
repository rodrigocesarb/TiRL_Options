package ExperimentosAAAI;

import java.util.Hashtable;

import burlap.mdp.core.action.Action;
import burlap.mdp.core.state.State;
import burlap.statehashing.HashableState;


/**
 * And interface for defining classes that check for certain conditions in states. These are useful
 * for specifying binary goal conditions for classic search-based planners like A*
 * @author James MacGlashan
 *
 */
public interface StateConditionTest2 {

	public boolean satisfies(State s, Hashtable<HashableState, Action> condicaoInicial);
	
	
}