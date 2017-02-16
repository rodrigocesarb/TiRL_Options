package ExperimentosAAMAS;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.List;
import java.util.Set;


import burlap.behavior.singleagent.learning.LearningAgent;
import burlap.behavior.singleagent.learning.tdmethods.QLearning;
import burlap.domain.singleagent.gridworld.GridWorldDomain;
import burlap.domain.singleagent.gridworld.GridWorldVisualizer;


//powerset de numeros

//aki! olhar esse
public class TestePowerSetOld {

	public static void main(String[] args) {
		int contador = 1;  //mostra a qtd de elementos gerados pelo powerset
//		int quantidadePoliticasOtimas = 5;  //qtd de politicas a serem geradas pelo powerset
//		boolean mostraQLearning = false; //controle p/ mostrar a execuçao do q-learning
//		boolean rodaQLearning = false; //controle p/ rodar a execuçao do q-learning


		//		BasicBehavior3 qLearningGreedy = new BasicBehavior3();

		TestePowerSetOld ps = new TestePowerSetOld();

		//aki! mudar de integer pra nao sei o q
		Set<Integer> set = new HashSet<Integer>();


		//aki! mudar de integer pra nao sei o q
		//imprime todos os elementos(l) do powerset de L individualmente
		for (Set<Integer> s : ps.powerSet(set)) {
			System.out.println("Elemento "+contador+" do powerset: "+s);
			contador++;
		}
		contador-= 1;
		System.out.println("Quantidade de elementos contidos no powerset: "+contador);

	}


	//aki! mudar de integer pra nao sei o q
	public Set<Set<Integer>> powerSet(Set<Integer> originalSet) {
		// Original set size e.g. 3

		int size = originalSet.size();

		//primeiro parametro do Math.pow indica de quantos em quantos elementos haverá merge(2 = de 2 em 2 sempre)
		//segundo parametro do Math.pow indica a quantidade de politicas otimas
		// Number of subsets 2^n, e.g 2^3 = 8

		int numberOfSubSets = (int) Math.pow(2, size);

		//aki! mudar de integer pra nao sei o q
		Set<Set<Integer>> sets = new HashSet<Set<Integer>>();

		//aki! mudar de integer pra nao sei o q
		ArrayList<Integer> originalList = new ArrayList<Integer>(originalSet);

		for (int i = 0; i < numberOfSubSets; i++) {
			// Get binary representation of this index e.g. 010 = 2 for n = 3
			String bin = getPaddedBinString(i, size);
			//Get each subset l
			Set<Integer> set = getSet(bin, originalList);
			//adiciona a lista sets na lista set
			sets.add(set);
		}
		return sets;
	}


	//aki! mudar de integer pra nao sei o q
	//Gets a sub-set based on the binary representation. E.g. for 010 where n = 3 it will bring a new Set with value 2
	private Set<Integer> getSet(String bin, List<Integer> origValues){
		Set<Integer> result = new HashSet<Integer>();

		for(int i = bin.length()-1; i >= 0; i--){
			//Only get sub-sets where bool flag is on
			if(bin.charAt(i) == '1'){
				int val = origValues.get(i);
				result.add(val);
			}
		}
		return result;
	}

	
	//aki! mudar de integer pra nao sei o q
	//Converts an int to Bin and adds left padding to zero's based on size
	private String getPaddedBinString(int i, int size) {
		String bin = Integer.toBinaryString(i);
		bin = String.format("%0" + size + "d", Integer.parseInt(bin));
		//		System.out.println("Numeros binario: "+ bin);
		return bin;
	}

	

}