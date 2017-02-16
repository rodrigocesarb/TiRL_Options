package ExperimentosAAMAS;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;


public class CSV {
	public static void main(String[]args) throws FileNotFoundException{
		PrintWriter pw = new PrintWriter(new File("test.csv"));
		StringBuilder sb = new StringBuilder();

		int controleEpisodios = 1;
		int multiplo = 1;
		
		controleEpisodios = 10 * multiplo;

		if(controleEpisodios % 10 == 0){

			sb.append("id");
			sb.append(',');
			sb.append("Name");
			sb.append('\n');

			sb.append("1");
			sb.append(',');
			sb.append("Prashant Ghimire");
			sb.append('\n');

			pw.write(sb.toString());
			pw.close();
			System.out.println("done!");
			multiplo ++;
		}
	}
}