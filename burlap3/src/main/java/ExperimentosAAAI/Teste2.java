package ExperimentosAAAI;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.Scanner;

public class Teste2 {

	public static void main(String[] args) {

		Path Source = Paths.get("/Users/lti/Desktop/nameslist.txt");
		Path Source2 = Paths.get("/Users/lti/Desktop/nameslist2.txt");


		Charset charset = Charset.forName("Us-ASCII");

		try (
				BufferedWriter write1 = Files.newBufferedWriter(Source, charset, StandardOpenOption.APPEND);
				

				BufferedWriter write2 = Files.newBufferedWriter(Source2, charset, StandardOpenOption.APPEND);
				BufferedReader reader2 = Files.newBufferedReader(Source2, charset);



				) {
			Scanner input = new Scanner(System.in);
			String s;
			String line;
			int i = 0, isEndOfLine;

			do {
				
				
				System.out.println("Book Code");
				s = input.nextLine();
				write1.append(s, 0, s.length());
				write1.append(",");

				System.out.println("Enter more Books? y/n");
				s = input.nextLine();

			} while(s.equalsIgnoreCase("y"));
			
			write1.close();
			
			BufferedReader reader1 = Files.newBufferedReader(Source, charset);
			
			String sCurrentLine;

			while ((sCurrentLine = reader1.readLine()) != null){
				write2.append(sCurrentLine);
				write2.append("\n");
			}

		} catch (Exception e) {
			// TODO: handle exception
		}
	}

}

//				while ((sCurrentLine = reader2.readLine()) != null){
//					//System.out.println(sCurrentLine);
//					write4.append(sCurrentLine);
//					write4.append("\n");
//				}




//				reader.close();






