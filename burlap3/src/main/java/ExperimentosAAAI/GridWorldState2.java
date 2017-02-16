package ExperimentosAAAI;

import burlap.domain.singleagent.gridworld.state.GridAgent;
import burlap.mdp.core.oo.state.MutableOOState;
import burlap.mdp.core.oo.state.OOStateUtilities;
import burlap.mdp.core.oo.state.OOVariableKey;
import burlap.mdp.core.oo.state.ObjectInstance;
import burlap.mdp.core.state.MutableState;
import burlap.mdp.core.state.State;
import burlap.mdp.core.state.StateUtilities;
import burlap.mdp.core.state.annotations.ShallowCopyState;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static burlap.domain.singleagent.gridworld.GridWorldDomain.VAR_TYPE;
import static burlap.domain.singleagent.gridworld.GridWorldDomain.VAR_X;
import static burlap.domain.singleagent.gridworld.GridWorldDomain.VAR_Y;

/**
 * @author James MacGlashan.
 */
@ShallowCopyState
public class GridWorldState2 implements MutableOOState {

	public GridAgent2 agent;
	public List<GridLocation2> locations = new ArrayList<GridLocation2>();
	public List<GridGold> gold = new ArrayList<GridGold>();

	public GridWorldState2() {
	}

	public GridWorldState2(int x, int y, GridLocation2[] locations,GridGold[] gold){
		this(new GridAgent2(x, y), locations,gold);
	}


	public GridWorldState2(GridAgent2 agent, GridLocation2[] locations,GridGold[] gold){
		this.agent = agent;
		if(locations.length == 0){
			this.locations = new ArrayList<GridLocation2>();
		}
		else {
			this.locations = Arrays.asList(locations);
		}
		if(gold.length == 0){
			this.gold = new ArrayList<GridGold>();
		}
		else {
			this.gold = Arrays.asList(gold);
		}

	}




	public GridWorldState2(GridAgent2 agent, List<GridLocation2> locations,List<GridGold> golds){
		this.agent = agent;
		this.locations = locations;
		this.gold = golds;
	}


	//	public GridWorldState2(GridAgent agent, GridLocation2 locations) {
	//		this.agent = agent;
	//		this.locations = locations;
	//	}

	//verificar
	@Override
	public MutableOOState addObject(ObjectInstance o) {
		if(!(o instanceof GridLocation2) || (o instanceof GridGold)){
			throw new RuntimeException("Can only add GridLocation or GridGold objects to a GridWorldState.");
		}

		if(o instanceof GridLocation2){
			GridLocation2 loc = (GridLocation2)o;
			//copy on write
			touchLocations().add(loc);
		}
		if(o instanceof GridGold){
			GridGold gol = (GridGold)o;
			//copy on write
			touchGolds().add(gol);
		}
		return this;
	}


	//verificar
	@Override
	public MutableOOState removeObject(String oname) {
		if(oname.equals(agent.name())){
			throw new RuntimeException("Cannot remove agent object from state");
		}

		int ind = this.locationInd(oname);


		int ind2 = this.goldInd(oname);

		if(ind == -1 && ind2 == -1){
			throw new RuntimeException("Cannot find object " + oname);
		}

		if(ind != -1){
			//copy on write
			touchLocations().remove(ind);
		}
		
		if(ind2 != -1){
			//copy on write
			touchGolds().remove(ind2);
		}

		return this;
	}

	//verificar
	@Override
	public MutableOOState renameObject(String objectName, String newName) {

		if(objectName.equals(agent.name())){
			GridAgent2 nagent = agent.copyWithName(newName);
			this.agent = nagent;
		}
		else{
			int ind = this.locationInd(objectName);
			int ind2 = this.goldInd(objectName);

			if(ind == -1 && ind2 == -1){
				throw new RuntimeException("Cannot find object " + objectName);
			}

			//copy on write
			if(ind != -1){
				GridLocation2 nloc = this.locations.get(ind).copyWithName(newName);
				touchLocations().remove(ind);
				locations.add(ind, nloc);
			}
			if(ind2 != -1){
				//colocar gold tb
				GridGold ngold = this.gold.get(ind2).copyWithName(newName);
				touchGolds().remove(ind2);
				gold.add(ind2, ngold);
			}

		}

		return this;
	}

	@Override
	public int numObjects() {
		return 1 + this.locations.size() +this.gold.size();
	}



	//verificar
	@Override
	public ObjectInstance object(String oname) {
		if(oname.equals(agent.name())){
			return agent;
		}
		int ind = this.locationInd(oname);

		int ind2 = this.goldInd(oname);

		if(ind != -1){
			return locations.get(ind);
		}

		if(ind2 != -1){
			return gold.get(ind2);
		}

		return null;
	}

	//sei la qq isso faz(verificar)
	@Override
	public List<ObjectInstance> objects() {
		List<ObjectInstance> obs = new ArrayList<ObjectInstance>(1+locations.size()+gold.size());
		obs.add(agent);
		obs.addAll(locations);
		obs.addAll(gold);
		return obs;
	}

	@Override
	public List<ObjectInstance> objectsOfClass(String oclass) {
		if(oclass.equals("agent")){
			return Arrays.<ObjectInstance>asList(agent);
		}
		else if(oclass.equals("location")){
			return new ArrayList<ObjectInstance>(locations);
		}
//		else if(oclass.equals(GridWorldDomain2.CLASS_GOLD)){
		else if(oclass.equals("gold")){
			return new ArrayList<ObjectInstance>(gold);
		}
		throw new RuntimeException("Unknown class type " + oclass);
	}

	@Override
	public MutableState set(Object variableKey, Object value) {

		OOVariableKey key = OOStateUtilities.generateKey(variableKey);
		int iv = StateUtilities.stringOrNumber(value).intValue();
		if(key.obName.equals(agent.name())){
			if(key.obVarKey.equals(VAR_X)){
				touchAgent().x = iv;
			}
			else if(key.obVarKey.equals(VAR_Y)){
				touchAgent().y = iv;
			}
			else{
				throw new RuntimeException("Unknown variable key " + variableKey);
			}
			return this;
		}
		int ind = locationInd(key.obName);

		int ind2 = goldInd(key.obName);

		if(ind != -1){
			if(key.obVarKey.equals(VAR_X)){
				touchLocation(ind).x = iv;
			}
			else if(key.obVarKey.equals(VAR_Y)){
				touchLocation(ind).y = iv;
			}
			else if(key.obVarKey.equals(VAR_TYPE)){
				touchLocation(ind).type = iv;
			}
			else{
				throw new RuntimeException("Unknown variable key " + variableKey);
			}

			return this;
		}

		if(ind2 != -1){
			if(key.obVarKey.equals(VAR_X)){
				touchGold(ind2).x = iv;
			}
			else if(key.obVarKey.equals(VAR_Y)){
				touchGold(ind2).y = iv;
			}
			else if(key.obVarKey.equals(VAR_TYPE)){
				touchGold(ind2).type = iv;
			}
			else{
				throw new RuntimeException("Unknown variable key " + variableKey);
			}

			return this;
		}

		throw new RuntimeException("Unknown variable key " + variableKey);
	}
	

	@Override
	public List<Object> variableKeys() {
		return OOStateUtilities.flatStateKeys(this);
	}

	@Override
	public Object get(Object variableKey) {
		OOVariableKey key = OOStateUtilities.generateKey(variableKey);
		if(key.obName.equals(agent.name())){
			return agent.get(key.obVarKey);
		}
		int ind = this.locationInd(key.obName);
		int ind2 = this.goldInd(key.obName);


		if(ind == -1 && ind2 ==-1){
			throw new RuntimeException("Cannot find object " + key.obName);
		}

		//eu acho
		if(ind == locationInd(key.obName)){
			return locations.get(ind).get(key.obVarKey);
		}

		//eu acho
		if(ind2 == goldInd(key.obName)){
			return gold.get(ind2).get(key.obVarKey);
		}

		//teoricamente nunca é pra cair aki
		return 0;

	}


	@Override
	public State copy() {
		return new GridWorldState2(agent, locations, gold);
	}


	protected int locationInd(String oname){
		int ind = -1;
		for(int i = 0; i < locations.size(); i++){
			if(locations.get(i).name().equals(oname)){
				ind = i;
				break;
			}
		}
		return ind;
	}

	protected int goldInd(String oname){
		int ind = -1;
		for(int i = 0; i < gold.size(); i++){
			if(gold.get(i).name().equals(oname)){
				ind = i;
				break;
			}
		}
		return ind;
	}



	@Override
	public String toString() {
		return OOStateUtilities.ooStateToString(this);
	}

	public GridAgent2 touchAgent(){
		this.agent = agent.copy();
		return agent;
	}

	public List<GridLocation2> touchLocations(){
		this.locations = new ArrayList<GridLocation2>(locations);
		return locations;
	}

	public List<GridGold> touchGolds(){
		this.gold = new ArrayList<GridGold>(gold);
		return gold;
	}

	public List<GridLocation2> deepTouchLocations(){
		List<GridLocation2> nlocs = new ArrayList<GridLocation2>(locations.size());
		for(GridLocation2 loc : locations){
			nlocs.add(loc.copy());
		}
		locations = nlocs;
		return locations;
	}

	public List<GridGold> deepTouchGolds(){
		List<GridGold> ngold = new ArrayList<GridGold>(gold.size());
		for(GridGold gol : gold){
			ngold.add(gol.copy());
		}
		gold = ngold;
		return gold;
	}

	public GridLocation2 touchLocation(int ind){
		GridLocation2 n = locations.get(ind).copy();
		touchLocations().remove(ind);
		locations.add(ind, n);
		return n;
	}

	public GridGold touchGold(int ind){
		GridGold n = gold.get(ind).copy();
		touchGolds().remove(ind);
		gold.add(ind, n);
		return n;
	}
}
