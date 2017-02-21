package ExperimentosAAAI;




import java.util.Hashtable;

public class HashTableDemo {
	   public static void main(String args[]) {
	   // create hash table 
	   Hashtable htable1 = new Hashtable();      
	      
	   // put values in table
	   htable1.put(1, "A");
	   htable1.put(2, "B");
	   htable1.put(3, "C");
	   htable1.put(4, "D");
	      
	   // get values at key 3
	   System.out.println("Values at key 3 is:"+htable1.get(3)); 
	   }    
	}