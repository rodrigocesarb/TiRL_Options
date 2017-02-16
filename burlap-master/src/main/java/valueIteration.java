






				/*
				00	01	02
				10	11	12
				20	21	22
			 	*/


//fator de desconto gamma = 1.0(sempre anda na diereção do comando)


public class valueIteration {

	public static void main(String[] args) {

		valueIteration vi = new valueIteration();
		double[][] matriz= vi.inicializarMatriz();
		double[][] copia = vi.copiarMatriz(matriz);
		double[][] matrizAtual = vi.inicializarMatriz();
		double[][] matrizAnterior = vi.inicializarMatriz();
		int iteracao = 0;
		boolean mudouValor;
		do{
			mudouValor = vi.atualizaValores(matrizAtual,matrizAnterior,iteracao);
			iteracao++;                                
		}while(mudouValor);
	}             



	public boolean atualizaValores(double[][] matrizAtual, double[][] matrizAnterior,int iteracao) {
		double[][] copia=copiarMatriz(matrizAtual);
		int i=0,j=0;
		boolean podeCima = false, podeBaixo = false, podeEsquerda = false, podeDireita = false; //acoes
		double valor=Double.NEGATIVE_INFINITY;  //v(max) p/cada acao/movimento, variavel inicializada como -infinito
		double	v= 0.0;    //v final
		double gamma=1.0;
		double recompensa= -9999;
		boolean retorno = false;
		boolean mudou = false;


		for (i=0; i<matrizAtual.length; i++){
			for(j=0; j<matrizAtual.length; j++){
				valor=Double.NEGATIVE_INFINITY;

				if(i<2){
					podeCima = true;
				}
				else{
					podeCima = false;
				}

				if(i>0){ 
					podeBaixo = true;
				}
				else{
					podeBaixo = false;
				}


				if(j<2){
					podeDireita = true;
				}
				else{
					podeDireita = false;
				}

				if(j>0){ 
					podeEsquerda = true;
				}
				else{
					podeEsquerda = false;
				}



				if(podeBaixo){
					if(valor<=copia[i-1][j]){
						valor = copia[i-1][j];
						//System.out.println("Andou pra baixo");
						//imprimeMatriz(matrizAtual,iteracao);
					}
				}

				if(podeCima){
					if(valor<=copia[i+1][j]){
						valor = copia[i+1][j];
						//System.out.println("Andou pra cima");
						//imprimeMatriz(matrizAtual,iteracao);
					}
					
				}



				if(podeDireita){
					if(valor<=copia[i][j+1]){
						valor = copia[i][j+1];
						//System.out.println("Andou pra direita");
						//imprimeMatriz(matrizAtual,iteracao);
					}
				}

				if(podeEsquerda){
					if(valor<=copia[i][j-1]){
						valor = copia[i][j-1];
						//System.out.println("Andou pra esquerda");
						//imprimeMatriz(matrizAtual,iteracao);

					}
				}


				/*
				00	01	02
				10	11	12
				20	21	22
				 */


				//chegou na posi��o final
				if (i == 2 && j==2){
					v=0;
				}
				else{
					recompensa = -1;
					v= recompensa + (gamma * valor);
				}
				
				
				

				//
				if (v != matrizAtual[i][j]){
					mudou=true;	
					matrizAnterior[i][j] = matrizAtual[i][j];
					matrizAtual[i][j] = v;
				}


			}



		}



		imprimeMatriz(matrizAtual, iteracao);
		return mudou;
	}






	public double[][] inicializarMatriz(){
		//Inicializando o matrizInicial com todos os valores zerados
		double matrizInicial[][] = {{0,0,0},{0,0,0},{0,0,0}};
		int i=0,j=0;
		for (i=0; i<matrizInicial.length; i++){
			for(j=0; j<matrizInicial.length; j++){
			}
		}
		return matrizInicial;
	}


	//Copiar a matriz inteira para o proximo calculo
	public double[][] copiarMatriz(double matrizInicial[][]){
		//Inicializando a matriz com todos os valores zerados
		double copiaMatriz[][] = {{0,0,0},{0,0,0},{0,0,0}};
		int i=0,j=0;
		System.out.println("\n");
		for (i=0; i<matrizInicial.length; i++){
			for(j=0; j<matrizInicial.length; j++){
				copiaMatriz[i][j] = matrizInicial[i][j];	
			}
		}
		return copiaMatriz;

	}
	

	//imprime a matriz em v�rias linhas quebradas 3x3
	private void imprimeMatriz(double[][] matrizAtual, int iteracao) {
		String a = "";
		System.out.println("Iteracao "+iteracao);
		for(double[] linha : matrizAtual){
			for(int i=0;i<linha.length;i++){
				a+=+linha[i]+" ";
			}
			a+="\n";
		}
		System.out.println(a);

	}
	
	
	
	
}
