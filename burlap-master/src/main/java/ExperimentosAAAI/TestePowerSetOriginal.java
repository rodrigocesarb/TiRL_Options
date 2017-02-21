package ExperimentosAAAI;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class TestePowerSetOriginal {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		TestePowerSetOriginal ps = new TestePowerSetOriginal();
		Set<Integer> set = new HashSet<Integer>();
		set.add(1);
		set.add(2);
		set.add(3);
		set.add(4);
		set.add(5);
		for (Set<Integer> s : ps.powerSet(set)) {
			System.out.println(s);
		}
	}

	public Set<Set<Integer>> powerSet(Set<Integer> originalSet) {
		// Original set size e.g. 3
		int size = originalSet.size();
		// Number of subsets 2^n, e.g 2^3 = 8, 2^5 = 32
		int numberOfSubSets = (int) Math.pow(2, size);
		Set<Set<Integer>> sets = new HashSet<Set<Integer>>();
		ArrayList<Integer> originalList = new ArrayList<Integer>(originalSet);
		for (int i = 0; i < numberOfSubSets; i++) {
			// Get binary representation of this index e.g. 010 = 2 for n = 3
			String bin = getPaddedBinString(i, size);
			//Get sub-set
			Set<Integer> set = getSet(bin, originalList);
			if(set.size()>0)
				sets.add(set);
		}
		return sets;
	}

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

	//Converts an int to Bin and adds left padding to zero's based on size
	private String getPaddedBinString(int i, int size) {
		String bin = Integer.toBinaryString(i);
		bin = String.format("%0" + size + "d", Integer.parseInt(bin));
		return bin;
	}

}