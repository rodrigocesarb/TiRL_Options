package ExperimentosAAMAS;

import java.util.Hashtable;
import java.util.Map;

import burlap.mdp.core.action.Action;
import burlap.statehashing.HashableState;

public class PoliticaStar {
	
	HashableState estado;
	Action acao;
	Integer idx;
	
	public PoliticaStar(Hashtable<HashableState, Action> hashtable, int i) {

		
		
		
		 for (Object o: hashtable.entrySet()) {
			 
		        Map.Entry entry = (Map.Entry) o;

		        // THIS IS THE IMPORTANT LINE
		        if(entry.getKey().equals(this.acao ))
		        {
		        	this.estado = (HashableState) entry.getValue();
		        }
		    }
		 
		 this.acao = hashtable.get(i);
		 this.idx = i;
		
	}

	public HashableState getEstado() {
		return estado;
	}
	public void setEstado(HashableState estado) {
		this.estado = estado;
	}
	public Action getAcao() {
		return acao;
	}
	public void setAcao(Action acao) {
		this.acao = acao;
	}
	public Integer getIdx() {
		return idx;
	}
	public void setIdx(Integer idx) {
		this.idx = idx;
	}
	
	

}
