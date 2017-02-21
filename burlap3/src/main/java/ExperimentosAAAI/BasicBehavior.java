package ExperimentosAAAI;

import java.awt.Color;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.List;
import java.util.Random;
import java.util.Scanner;
import java.util.Set;

import burlap.behavior.policy.Policy;
import burlap.behavior.singleagent.Episode;
//import burlap.behavior.singleagent.EpisodeAnalysis;
import burlap.behavior.singleagent.auxiliary.EpisodeSequenceVisualizer;
import burlap.behavior.singleagent.auxiliary.StateReachability;
import burlap.behavior.singleagent.auxiliary.performance.LearningAlgorithmExperimenter;
import burlap.behavior.singleagent.auxiliary.performance.PerformanceMetric;
import burlap.behavior.singleagent.auxiliary.performance.TrialMode;
import burlap.behavior.singleagent.auxiliary.valuefunctionvis.ValueFunctionVisualizerGUI;
import burlap.behavior.singleagent.auxiliary.valuefunctionvis.common.ArrowActionGlyph;
import burlap.behavior.singleagent.auxiliary.valuefunctionvis.common.LandmarkColorBlendInterpolation;
import burlap.behavior.singleagent.auxiliary.valuefunctionvis.common.PolicyGlyphPainter2D;
import burlap.behavior.singleagent.auxiliary.valuefunctionvis.common.StateValuePainter2D;
import burlap.behavior.singleagent.learning.LearningAgent;
import burlap.behavior.singleagent.learning.LearningAgentFactory;
import burlap.behavior.singleagent.learning.tdmethods.QLearning;
import burlap.behavior.singleagent.learning.tdmethods.SarsaLam;
import burlap.behavior.singleagent.options.EnvironmentOptionOutcome;
import burlap.behavior.singleagent.options.Option;
import burlap.behavior.singleagent.options.OptionType;
import burlap.behavior.singleagent.options.SubgoalOption;
import burlap.behavior.singleagent.planning.deterministic.DDPlannerPolicy;
import burlap.behavior.singleagent.planning.deterministic.uninformed.bfs.BFS;
import burlap.behavior.valuefunction.ValueFunction;
import burlap.domain.singleagent.gridworld.state.GridAgent;
import burlap.domain.singleagent.gridworld.state.GridLocation;
import burlap.domain.singleagent.gridworld.state.GridWorldState;
import burlap.mdp.auxiliary.stateconditiontest.StateConditionTest;
import burlap.mdp.auxiliary.stateconditiontest.TFGoalCondition;
import burlap.mdp.core.Domain;
import burlap.mdp.core.TerminalFunction;
import burlap.mdp.core.action.Action;
import burlap.mdp.core.action.ActionType;
import burlap.mdp.core.oo.state.OOVariableKey;
import burlap.mdp.core.state.State;
import burlap.mdp.core.state.vardomain.VariableDomain;
import burlap.mdp.singleagent.SADomain;
import burlap.mdp.singleagent.common.GoalBasedRF;
import burlap.mdp.singleagent.common.VisualActionObserver;
import burlap.mdp.singleagent.environment.SimulatedEnvironment;
import burlap.mdp.singleagent.model.FactoredModel;
import burlap.mdp.singleagent.model.RewardFunction;
import burlap.mdp.singleagent.oo.OOSADomain;
import burlap.statehashing.HashableState;
import burlap.statehashing.HashableStateFactory;
import burlap.statehashing.simple.SimpleHashableStateFactory;
import burlap.visualizer.Visualizer;
//import examples.BasicBehavior2;

public class BasicBehavior<idx> {

	static GridWorldDomain2 gwdg;
	static GridWorldDomain2 gwdg2;
	static OOSADomain domain1; // Dominio com 1 recompensa - Objetivo 1 sem options
	static OOSADomain domain2; // Dominio com 1 recompensa - Objetivo 2 sem options
	static OOSADomain domain3; // Dominio com 2 recompensas
	static OOSADomain domain4; // Dominio com 2 recompensas e muros diferentes
	TerminalFunction tf;
	static StateConditionTest goalCondition;
	State initialState;


	HashableStateFactory hashingFactory;
	SimulatedEnvironment env;
	SimulatedEnvironment env2;
	int npoliticas = 5; //quantidade de politicas otimas geradas(L ou SP)



	//SEMPRE DEIXAR ESSAS 2 VARIAVEIS COM VALORES IGUAIS!!!
	int episodes = 100; //quantidade de episodios utilizados para geraçao das politicas otimas
	int controleEpisodes = 100;    //quantidade de rodadas do experimento(gerar excel)

	int nOptions = 1; //numero desejado de options (O) -- se colocar numero diferente de 3 ta dando pau
	int contadorPowerSet = 1;  //contador de politicas geradas no powerset (ps)
	int scoreExp1 = 0;
	int maiorScoreExp1 = 0;

	int scoreExp2 = 0;
	int maiorScoreExp2 = 0;

	//parametros objetivo 1
	public boolean objetivo1 = true;

	//parametros objetivo 2
	public boolean objetivo2 = false;

	//parametros para escolher qual experimento rodar
	static boolean experimento1 = true;
	static boolean experimento2 = false;
	static boolean experimento3 = false;
	static boolean experimento4 = false;
	
	static int qtdOuroSemOptions = 0;
	static int qtdOuroComOptions = 0;
	//variavel para controle de geraçao random dos agentes
	static boolean agenteRandom = false;
	static boolean dominio4Rodar = false;

	//variaveis para controlar a quantidade de experimentos que acontecerão

	static int qtdExperimentos = 10;

	Hashtable<HashableState, Action> condicaoInicial = new Hashtable<HashableState, Action>();


	//construtor para Objetivo visando a criaçao do dominio usado para gerar as politicas otimas iniciais
	public BasicBehavior(){
		GridGold[] golds = createGolds(); //parametro interno controla quantos ouros serao criados

		GridAgent2 agente;
		agente = createAgent();

		//		GridAgent2 agente = createVariousAgent();

		//		initialState = new GridWorldState2(new GridAgent2(0, 0), new GridLocation2[]{new GridLocation2(10, 10, "loc0")},golds);
		initialState = new GridWorldState2(agente, new GridLocation2[]{new GridLocation2(10, 10, "loc0")},golds);
		hashingFactory = new SimpleHashableStateFactory();
		tf = new GridWorldTerminalFunction2(10, 10);
		goalCondition = new TFGoalCondition(tf);



		//dominio single-objective para gerar as options só com o caminho
		if(objetivo1){
			//criaçao do dominio usado para gerar as politicas otimas iniciais
			//			if(o1semOptions){
			gwdg = new GridWorldDomain2(11, 11, 1);
			gwdg.setMapToFourRooms();
			gwdg.setTf(tf);

			domain1 = gwdg.generateDomain();

			env = new SimulatedEnvironment(domain1, initialState);
		}

		//dominio single-objective para gerar as options só com os ouros
		if(objetivo2){
			//criaçao do dominio usado para gerar as politicas otimas iniciais
			//			if(o2semOptions){
			gwdg = new GridWorldDomain2(11, 11,2);
			gwdg.setMapToFourRooms();
			gwdg.setTf(tf);
			domain2 = gwdg.generateDomain();

			env = new SimulatedEnvironment(domain2, initialState);
		}


		//dominio multiobjective
		if(objetivo1 && objetivo2){
			gwdg = new GridWorldDomain2(11, 11,3);
			gwdg.setMapToFourRooms();
			gwdg.setTf(tf);
			domain3 = gwdg.generateDomain();
			env = new SimulatedEnvironment(domain3, initialState);


			gwdg2 = new GridWorldDomain2(11, 11,3);
			gwdg2.changeWalls();
			gwdg2.setTf(tf);
			domain4 = gwdg2.generateDomain();
			env2 = new SimulatedEnvironment(domain4, initialState);
		}

		//codigo para visualizar o experimento1
		//		VisualActionObserver observer1 = new VisualActionObserver(domain1, 
		//				GridWorldVisualizer2.getVisualizer(gwdg.getMap()));
		//		observer1.initGUI();
		//		env.addObservers(observer1);

		//codigo para visualizar o experimento2
		//						VisualActionObserver observer2 = new VisualActionObserver(domain2, 
		//								GridWorldVisualizer2.getVisualizer(gwdg.getMap()));
		//						observer2.initGUI();
		//						env.addObservers(observer2);

		//codigo para visualizar o experimento3
		//		VisualActionObserver observer3 = new VisualActionObserver(domain3, 
		//				GridWorldVisualizer2.getVisualizer(gwdg.getMap()));
		//		observer3.initGUI();
		//		env.addObservers(observer3);


		//codigo para visualizar o experimento4
//		VisualActionObserver observer4 = new VisualActionObserver(domain4, 
//				GridWorldVisualizer2.getVisualizer(gwdg2.getMap()));
//		observer4.initGUI();
//		env2.addObservers(observer4);
	}


	public static void main(String[] args) throws FileNotFoundException {

		//BasicBehavior qLearningExample2 = new BasicBehavior();

		//		BasicBehavior qLearningGreedy = new BasicBehavior();
		//		String outputPath = "output/";

		String outputPath = "output/"; // directory to record results
		String outputPath2 = "output2/";
		String outputPath3 = "output3/";
		String outputPath4 = "output4/";

		boolean visualizarExperimento = false;


		//rodando com as options descobertas só para steps
		if(experimento1){
			BasicBehavior qLearningExample = new BasicBehavior();

			//roda q-learning para extrair as options com policyblocks
			List<Option> opt1 = qLearningExample.rodaExperimento(1);	

			//roda experimento sem option
			qLearningExample.rodarExperimentoSemAprenderOption(qLearningExample.domain1,false,1); //dominio, true = comOptions e false = semOptions, numero do experimento

			//adiciona as options no dominio
			qLearningExample.domain1.addActionTypes(getActionTypesFromOption(opt1));

			//roda experimento com option
			qLearningExample.rodarExperimentoSemAprenderOption(qLearningExample.domain1,true,1);
			//
//						if(visualizarExperimento){
//							qLearningExample.visualize(outputPath);
//						}
			//			contadorExperimentos++;
		}


		//rodando com as options descobertas no gold mine
		if(experimento2){
			BasicBehavior qLearningExample2 = new BasicBehavior();

			//roda q-learning para extrair as options com policyblocks
			List<Option> opt2 = qLearningExample2.rodaExperimento(2);

			//roda experimento sem option
			qLearningExample2.rodarExperimentoSemAprenderOption(qLearningExample2.domain2,false,2);

			//adiciona as options no dominio
			qLearningExample2.domain2.addActionTypes(getActionTypesFromOption(opt2));

			
			qLearningExample2.rodarExperimentoSemAprenderOption(qLearningExample2.domain2,true,2);

			//			if(visualizarExperimento){
			//				qLearningExample2.visualize(outputPath2);
			//			}
		}

		//rodando com as 2 options descobertas nos 2 dominios
		if(experimento3){
			BasicBehavior qLearningExample3 = new BasicBehavior();
			//roda q-learning para extrair as options com policyblocks
			List<Option> opt1 = qLearningExample3.rodaExperimento(1);
			//roda q-learning para extrair as options com policyblocks
			List<Option> opt2 = qLearningExample3.rodaExperimento(2);

			//roda experimento sem option
			qLearningExample3.rodarExperimentoSemAprenderOption(qLearningExample3.domain3,false,3);

			//adiciona as options no dominio
			qLearningExample3.domain3.addActionTypes(getActionTypesFromOption(opt1));
			//adiciona as options no dominio
			qLearningExample3.domain3.addActionTypes(getActionTypesFromOption(opt2));

			//roda experimento com option
			qLearningExample3.rodarExperimentoSemAprenderOption(qLearningExample3.domain3,true,3);

			//			if(visualizarExperimento){
			//				qLearningExample3.visualize(outputPath3);
			//			}
		}


		//rodando com options aprendidas nos dos 2 dominios e transfer learning para um dominio com muros diferentes
		if(experimento4){

			agenteRandom = false;

			BasicBehavior qLearningExample4 = new BasicBehavior();

			//cria o agente randomicamente ou no 0,0
			//qLearningExample4.createAgent();

			//descobre as options no 1o dominio single objective
			List<Option> opt1 = qLearningExample4.rodaExperimento(1);
			//descobre as options no 1o dominio single objective
			List<Option> opt2 = qLearningExample4.rodaExperimento(2);

			//aprendendo as options no dominio 3(com objetivos 1 e 2)
			qLearningExample4.rodarExperimentoSemAprenderOption(qLearningExample4.domain3,false,3);

			//adicionando as options no dominio 4
			qLearningExample4.domain4.addActionTypes(getActionTypesFromOption(opt1));
			//adicionando as options no dominio 4
			qLearningExample4.domain4.addActionTypes(getActionTypesFromOption(opt2));

			//parametro pra controle de execuçao do dominio 4
			dominio4Rodar = true;
			//mudar os muros 1x antes de rodar de novo com as options aprendidas
			qLearningExample4.rodarExperimentoSemAprenderOption(qLearningExample4.domain4,true,3);
//
//			if(visualizarExperimento){
//				qLearningExample4.visualize(outputPath4);
//			}
		}

	}


	private void rodarExperimentoSemAprenderOption(OOSADomain domain, boolean comOptions, int experiment) throws FileNotFoundException {
		//		SADomain domain = domain1;

		//		SADomain domain = domain2;

		//		SADomain domain = domain3;

		String outputPath = "output1";
		String outputPath2 = "output2";
		String outputPath3 = "output3";
		String outputPat4 = "output4";


		//		List<List<Hashtable<HashableState, Action>>> options = new ArrayList<List<Hashtable <HashableState, Action>>>();

		//		List<Hashtable<HashableState, Action>> options1 = new ArrayList<Hashtable <HashableState, Action>>();

		//		int n = 10; //numero de vezes que irá rodar q-learning e q-learning greedy

		double recompensa = 0;

		int controle = 0; //variavel para controlar a quantidade de episodios

		int execucoes = 1;

		boolean append = false;


		if(comOptions){
//			new File("/Users/lti/Desktop/testComOption"+experiment+".csv").delete();
			new File("/home/lti/experimento/testComOption"+experiment+".csv").delete();
		}
		else{	
//			new File("/Users/lti/Desktop/testSemOption"+experiment+".csv").delete();
			new File("/home/lti/experimento/testSemOption"+experiment+".csv").delete();
		}

		//nomeia os arquivos de acordo com o experimento
//		Path source1 = Paths.get("/Users/lti/Desktop/testComOption"+experiment+".csv");
		Path source1 = Paths.get("/home/lti/experimento/testComOption"+experiment+".csv");
		Charset charset = Charset.forName("Us-ASCII");

//		Path source2 = Paths.get("/Users/lti/Desktop/testSemOption"+experiment+".csv");
		Path source2 = Paths.get("/home/lti/experimento/testSemOption"+experiment+".csv");
		Charset charset2 = Charset.forName("Us-ASCII");

//		Path auxiliar = Paths.get("/Users/lti/Desktop/testSemOptionAuxiliar.csv");
		Path auxiliar = Paths.get("/home/lti/experimento/testSemOptionAuxiliar.csv");


		int contadorExperimentos = 1;
		while(contadorExperimentos <= qtdExperimentos){
			BasicBehavior qLearningExample4 = new BasicBehavior();


			//qLearningExample4.createAgent();
			QLearning2 agent = new QLearning2(domain, 0.9, hashingFactory, 0., 0.2);

			BufferedReader reader=null;
			BufferedWriter writer=null;

			try {

				if(comOptions){
					if(source1.toFile().exists()){
						writer = Files.newBufferedWriter(auxiliar, charset);
						append = true;  //se o arquivo existir, permite append no fim do arquivo
						reader = Files.newBufferedReader(source1,charset);
					}
					else{
						writer = Files.newBufferedWriter(source1, charset);
						append = false; //se o arquivo não existir, não permite append no fim do arquivo
					}
				}
				else{
					if(source2.toFile().exists()){
						writer = Files.newBufferedWriter(auxiliar, charset);
						append = true; //se o arquivo existir, permite append no fim do arquivo
						reader = Files.newBufferedReader(source2,charset);
					}
					else{
						writer = Files.newBufferedWriter(source2, charset);
						append = false; //se o arquivo não existir, não permite append no fim do arquivo
					}
				}

				if(append){
					String line = reader.readLine();
					line+=",Reward\n";
					writer.write(line);
				}else{
					String line = "Episodes" +     //se puser com ' é pego o caracter ascii(char)
							',' +
							"Reward" +
							'\n';
					writer.write(line);
				}

				controle=0;
				while(controle < episodes){
					//qLearningExample4.createAgent();
					qLearningExample(outputPath, agent, execucoes);
					//qLearningExample4.createAgent();
					recompensa = qLearningGreedy2(outputPath2, agent, 1);
					String line;
					if(append){
						line = reader.readLine();
						line+=","+ recompensa+"\n";   //se puser, cria uma nova coluna e se puser com ' é pego o caracter ascii(char)
					}
					else{
						line = controle + "," + recompensa +"\n";
					}
					writer.write(line);

					controle+=execucoes;
				}

				writer.close();


				if(append){
					if(comOptions){
						//mudar o nome do arquivo auxiliar para o arquivo desejado
						auxiliar.toFile().renameTo(source1.toFile());
					}
					else{
						//mudar o nome do arquivo auxiliar para o arquivo desejado
						auxiliar.toFile().renameTo(source2.toFile());
					}
				}
			}
			catch (Exception e) {
				e.printStackTrace();
			}
			System.out.println("\n Finish experiment "+contadorExperimentos);
			contadorExperimentos++;
		}

	}


	public void qLearningExample(String outputPath,QLearning2 agent, int episodes){

		//LearningAgent agent = new QLearning(domain, 0.99, hashingFactory, 0., 1.);

		//run learning for episodes

		for(int i = 0; i < episodes; i++){
			if(!dominio4Rodar){
				Episode e = agent.runLearningEpisode(env);
				env.resetEnvironment();
			}

			else{
				Episode e = agent.runLearningEpisode(env2);
				env2.resetEnvironment();
			}

		}

	}

	public double qLearningGreedy2(String outputPath2, QLearning2 agent, int episodes){

		GreedyQPolicy2 gqp = new GreedyQPolicy2(agent);

		List<State> listaDeEstados;
		List<Action> listaDeAcoes;

		double recompensa = 0.0;

		double recompensaTotal = 0.0;

		int nSteps = 0;

		//assim da out of bounds na classe greedyqpolicy2
		//agent.setLearningPolicy(gqp);

		//assim funciona, mas pode tar errado
		//seta a politica como greedy agent.setLearningGreedyPolicy 
		agent.setLearningGreedyPolicy(gqp);

		if (!outputPath2.endsWith("/")) {
			outputPath2 = outputPath2 + "/";
		}

		for(nSteps=1; nSteps<controleEpisodes;nSteps++){
			if(!dominio4Rodar){
				recompensa = agent.runLearningEpisodeWithHash2(env,-1); 
				recompensaTotal += recompensa;
			}

			else{
				recompensa = agent.runLearningEpisodeWithHash2(env2,-1); 
				recompensaTotal += recompensa;
			}


		}

		return recompensaTotal;

	}



	public Hashtable <HashableState, Action> qLearningGreedy(String outputPath2, QLearning2 agent, int episodes){

		GreedyQPolicy2 gqp = new GreedyQPolicy2(agent);

		List<State> listaDeEstados;
		List<Action> listaDeAcoes;

		Hashtable <HashableState, Action> resultadoPolitica = new Hashtable <HashableState, Action>();

		//assim da out of bounds na classe greedyqpolicy2
		//agent.setLearningPolicy(gqp);

		//assim funciona, mas pode tar errado
		//seta a politica como greedy agent.setLearningGreedyPolicy 
		agent.setLearningGreedyPolicy(gqp);

		if (!outputPath2.endsWith("/")) {
			outputPath2 = outputPath2 + "/";
		}

		if(!dominio4Rodar){
			resultadoPolitica = agent.runLearningEpisodeWithHash(env, episodes); 
		}

		else{
			resultadoPolitica = agent.runLearningEpisodeWithHash(env2, episodes); 
		}

		return resultadoPolitica;

	}


	private static ActionType[] getActionTypesFromOption(List<Option> opt1) {
		//Cria acao no ambiente para cada option
		ActionType[] acoes = new ActionType[opt1.size()];
		for(int i=0;i<opt1.size();i++){
			acoes[i] = new OptionType(opt1.get(i));
		}
		return acoes;
	}

	public List<Option> rodaExperimento(int objetivo) {

		//List<Hashtable<HashableState, Action>> options1 = new ArrayList<Hashtable <HashableState, Action>>();

		//List<Hashtable<HashableState, Action>> options2 = new ArrayList<Hashtable <HashableState, Action>>();

		List<Option> options;


		//experimento para criar as options manualmente
		options = criaOption(objetivo); //criacao do conjunto 1 de options(maximizando o objetivo 1)

		return options;
	}



	//visualizar experimento1
	//	public void visualize(String outputpath){
	//		Visualizer v = GridWorldVisualizer2.getVisualizer(gwdg.getMap());
	//		new EpisodeSequenceVisualizer(v, domain1, outputpath);
	//	}

	//visualizar experimento2
	//	public void visualize(String outputpath2){
	//		Visualizer v = GridWorldVisualizer2.getVisualizer(gwdg.getMap());
	//		new EpisodeSequenceVisualizer(v, domain2, outputpath2);
	//	}

	//	//visualizar experimento3
	//	public void visualize(String outputpath3){
	//		Visualizer v = GridWorldVisualizer2.getVisualizer(gwdg.getMap());
	//		new EpisodeSequenceVisualizer(v, domain3, outputpath3);
	//	}

	//visualizar experimento4
//	public void visualize(String outputpath4){
//		Visualizer v = GridWorldVisualizer2.getVisualizer(gwdg2.getMap());
//		new EpisodeSequenceVisualizer(v, domain4, outputpath4);
//	}


	//	//metodo para visualizar o 1o q-learning
	//		public void visualize(String outputpath){
	//			Visualizer v = GridWorldVisualizer2.getVisualizer(gwdg.getMap());
	//			new EpisodeSequenceVisualizer(v, domain, outputpath);
	//		}
	//
	//
	//	//metodo para visualizar o 2o q-learning(greedy)
	//	public void visualize2(String outputpath2){
	//		Visualizer v = GridWorldVisualizer2.getVisualizer(gwdg.getMap());
	//		new EpisodeSequenceVisualizer(v, domain, outputpath2);
	//	}



	public List<Option> criaOptionManual(int objetivo) {


		//cria o dominio a ser usado pelo qLearningExample e qLearningGreedy
		SADomain domain = null;
		if(objetivo==1){
			domain = this.domain1;
		}
		if(objetivo==2){
			domain = this.domain2;
		}
		if(objetivo==3){
			domain = this.domain3;
			//			if(dominio4Rodar){
			//				domain = this.domain4
			//			}
		}

		//cria o agente a ser usado pelo qLearningExample e qLearningGreedy
		QLearning2 agent = new QLearning2(domain, 0.9, hashingFactory, 0., 0.2);
		TestePowerSetOriginal ps = new TestePowerSetOriginal();

		List<Hashtable <HashableState, Action>> politicas = new ArrayList<Hashtable <HashableState, Action>>();  //politicas iniciais do conjunto L
		//<Hashtable<HashableState, Action>> => l
		//List<Hashtable <HashableState, Action>> => L
		//Set<Integer> => originalset
		//Set<Set<Integer>> => powerset
		//List<List<Hashtable<HashableState, Action>>> => O
		//List<Hashtable<HashableState, Action>> => pi(toEvaluate)
		//List<Hashtable<HashableState, Action>> => pi(toMerge)

		String outputPath = "output1";
		String outputPath2 = "output2";
		String outputPath3 = "output3";
		String outputPath4 = "output4";

		//		List<List<Hashtable<HashableState, Action>>> options = new ArrayList<List<Hashtable <HashableState, Action>>>();

		List<Hashtable<HashableState, Action>> options = new ArrayList<Hashtable <HashableState, Action>>();


		//gera as politicas otimas iniciais
		for(int x=0;x<npoliticas;x++){
			//roda q-learning

			qLearningExample(outputPath, agent, episodes);
			politicas.add(qLearningGreedy(outputPath2, agent, episodes));  //roda q-learning greedy com o que aprendeu na 1a execuçao qLearningExample 
		}

		//	System.out.println("Quantidade de Politicas Otimas Geradas "+politicas.size());

		Set<Integer> originalSet = new HashSet<Integer>();


		int n = 0; //controle para criaçao de options


		//enquanto nao forem criadas a quantidade de Options desejadas (nOptions ideal = 3)
		while(n<nOptions){ //lembrar de verificar se L esta vazio

			//cria o powerset numerico 2^n politicas(npoliticas maximo ideal = 5), entao teremos 32 elementos no powerset
			for(int x = 0; x<politicas.size();x++){
				originalSet.add(x);
				Set<Set<Integer>> powerSet = ps.powerSet(originalSet); //powerset gerado;
			}


			Set<Integer> maior = null;
			int maiorScoreExp1 = Integer.MIN_VALUE;
			for(Set<Integer> s: ps.powerSet(originalSet)){

				//faz score do merge das politicas

				//				if(!s.isEmpty()){
				int scoreExp1 = mergeScore(politicas,s);
				//				}

				if(scoreExp1>maiorScoreExp1){
					maior = s;
					maiorScoreExp1 = scoreExp1;
				}		

			}


			//faz o merge das politicas

			Hashtable<HashableState, Action> merged = mergePolicy(politicas,maior); //lista contendo a politica mergeada de maior score -- merge nao deveria ser lista, deveria ser uma Hashtable apenas

			options.add(merged);



			//adiciona a politica mergeada de maior score ao conjunto de options

			//	System.out.println("Options numero "+n+ " dentro do while, logo apos o merge "+options);


			//lista com as options candidatas a serem removidas das Source Policies
			Hashtable<Integer,List<HashableState>> toRemove = new Hashtable<Integer,List<HashableState>>(); 

			Enumeration<HashableState> m2 = merged.keys();

			//preparaçao do conjunto a ser removido pela operaçao de subtract
			//Apaga estados do conjunto de politicas
			//			for(Hashtable<HashableState, Action> m2 : merged){ //ok na 1a iteraçao q há merge, na 2a vem {}
			while (m2.hasMoreElements()) {
				//Itera em todos os estados

				HashableState s = m2.nextElement();				

				//Set<HashableState> states2 = m2.keySet(); //pega todos estados q tao dentro do hashset; ok 
				//				for(HashableState s : states2){ //ok
				//					for(HashableState s : m2){
				//remove de todas as politicas
				for(int index = 0;index<politicas.size();index++){ //ok na 1a iteraçao q há merge, na 2a vem {}   L
					Hashtable<HashableState, Action> p = politicas.get(index);  //l
					//copia todos os estados a serem removidos para uma unica lista
					if(p.containsKey(s)){ 

						if(!toRemove.containsKey(index)){
							toRemove.put(index, new ArrayList<HashableState>()); //cria um hashset com uma lista nova(pegar elemento do hashtable)
						}
						//							toRemove.get(index).add(s.nextElement()); 
						toRemove.get(index).add(s); 

					}
				}
			}


			//Apaga(subtrai) as partial policies mergeadas de maior score do conjunto de source policies
			//para todas as politicas que tenham algum estado para ser removido
			for(Integer index: toRemove.keySet()){
				Hashtable<HashableState, Action> p = politicas.get(index);
				List<HashableState> l = toRemove.get(index);
				//para cada estado de uma politica l
				for(HashableState s : l){
					if(p.size()>1){ //apenas remove da lista se tiver pelo menos um estado na politica
						p.remove(s); //zerando a lista p e consequentemente a de options tb
					}
					if(p.isEmpty() && politicas.size()>=1){ //se a politica tiver vazia e ainda houver politica, remover ela do conjunto inicial de politicas, mas sempre deve-se deixar ao menos uma politica no conjunto para nao deixá-lo vazio
						politicas.remove(p);
						break;
					}
				}
			}

			n++;

		}

		return criaObjOptionManual(options, this.hashingFactory);

	}


	//metodo "principal" para extrair options(policyblocks) da aplicaçao com o intuito de obter a menor quantidade de steps possivel
	public List<Option> criaOption(int objetivo){

		BasicBehavior qLearningExample4 = new BasicBehavior();


		//qLearningExample4.createAgent();

		//cria o dominio a ser usado pelo qLearningExample e qLearningGreedy
		SADomain domain = null;
		if(objetivo==1){
			domain = this.domain1;
		}
		if(objetivo==2){
			domain = this.domain2;
		}
		if(objetivo==3){
			domain = this.domain3;
		}

		//cria o agente a ser usado pelo qLearningExample e qLearningGreedy
		QLearning2 agent = new QLearning2(domain, 0.9, hashingFactory, 0., 0.2);
		TestePowerSetOriginal ps = new TestePowerSetOriginal();

		List<Hashtable <HashableState, Action>> politicas = new ArrayList<Hashtable <HashableState, Action>>();  //politicas iniciais do conjunto L
		//<Hashtable<HashableState, Action>> => l
		//List<Hashtable <HashableState, Action>> => L
		//Set<Integer> => originalset
		//Set<Set<Integer>> => powerset
		//List<List<Hashtable<HashableState, Action>>> => O
		//List<Hashtable<HashableState, Action>> => pi(toEvaluate)
		//List<Hashtable<HashableState, Action>> => pi(toMerge)

		String outputPath = "output1";
		String outputPath2 = "output2";
		String outputPath3 = "output3";

		//		List<List<Hashtable<HashableState, Action>>> options = new ArrayList<List<Hashtable <HashableState, Action>>>();

		List<Hashtable<HashableState, Action>> options = new ArrayList<Hashtable <HashableState, Action>>();


		//gera as politicas otimas iniciais
		for(int x=0;x<npoliticas;x++){
			//roda q-learning
			qLearningExample(outputPath, agent, episodes);
			politicas.add(qLearningGreedy(outputPath2, agent, episodes));  //roda q-learning greedy com o que aprendeu na 1a execuçao qLearningExample 
		}

		//	System.out.println("Quantidade de Politicas Otimas Geradas "+politicas.size());

		Set<Integer> originalSet = new HashSet<Integer>();


		int n = 0; //controle para criaçao de options


		//enquanto nao forem criadas a quantidade de Options desejadas (nOptions ideal = 3)
		while(n<nOptions){ //lembrar de verificar se L esta vazio

			//cria o powerset numerico 2^n politicas(npoliticas maximo ideal = 5), entao teremos 32 elementos no powerset
			for(int x = 0; x<politicas.size();x++){
				originalSet.add(x);
				Set<Set<Integer>> powerSet = ps.powerSet(originalSet); //powerset gerado;
			}


			Set<Integer> maior = null;
			int maiorScoreExp1 = Integer.MIN_VALUE;
			for(Set<Integer> s: ps.powerSet(originalSet)){

				//faz score do merge das politicas

				//				if(!s.isEmpty()){
				int scoreExp1 = mergeScore(politicas,s);
				//				}

				if(scoreExp1>maiorScoreExp1){
					maior = s;
					maiorScoreExp1 = scoreExp1;
				}		

			}


			//faz o merge das politicas

			Hashtable<HashableState, Action> merged = mergePolicy(politicas,maior); //lista contendo a politica mergeada de maior score -- merge nao deveria ser lista, deveria ser uma Hashtable apenas

			options.add(merged);



			//adiciona a politica mergeada de maior score ao conjunto de options

			//	System.out.println("Options numero "+n+ " dentro do while, logo apos o merge "+options);


			//lista com as options candidatas a serem removidas das Source Policies
			Hashtable<Integer,List<HashableState>> toRemove = new Hashtable<Integer,List<HashableState>>(); 

			Enumeration<HashableState> m2 = merged.keys();

			//preparaçao do conjunto a ser removido pela operaçao de subtract
			//Apaga estados do conjunto de politicas
			//			for(Hashtable<HashableState, Action> m2 : merged){ //ok na 1a iteraçao q há merge, na 2a vem {}
			while (m2.hasMoreElements()) {
				//Itera em todos os estados

				HashableState s = m2.nextElement();				

				//Set<HashableState> states2 = m2.keySet(); //pega todos estados q tao dentro do hashset; ok 
				//				for(HashableState s : states2){ //ok
				//					for(HashableState s : m2){
				//remove de todas as politicas
				for(int index = 0;index<politicas.size();index++){ //ok na 1a iteraçao q há merge, na 2a vem {}   L
					Hashtable<HashableState, Action> p = politicas.get(index);  //l
					//copia todos os estados a serem removidos para uma unica lista
					if(p.containsKey(s)){ 

						if(!toRemove.containsKey(index)){
							toRemove.put(index, new ArrayList<HashableState>()); //cria um hashset com uma lista nova(pegar elemento do hashtable)
						}
						//							toRemove.get(index).add(s.nextElement()); 
						toRemove.get(index).add(s); 

					}
				}
			}


			//Apaga(subtrai) as partial policies mergeadas de maior score do conjunto de source policies
			//para todas as politicas que tenham algum estado para ser removido
			for(Integer index: toRemove.keySet()){
				Hashtable<HashableState, Action> p = politicas.get(index);
				List<HashableState> l = toRemove.get(index);
				//para cada estado de uma politica l
				for(HashableState s : l){
					if(p.size()>1){ //apenas remove da lista se tiver pelo menos um estado na politica
						p.remove(s); //zerando a lista p e consequentemente a de options tb
					}
					if(p.isEmpty() && politicas.size()>=1){ //se a politica tiver vazia e ainda houver politica, remover ela do conjunto inicial de politicas, mas sempre deve-se deixar ao menos uma politica no conjunto para nao deixá-lo vazio
						politicas.remove(p);
						break;
					}
				}
			}

			n++;

		}

		return criaObjOption1(options, this.hashingFactory);

	}

	public static List<Option> criaObjOption1(List<Hashtable<HashableState, Action>> options, HashableStateFactory hasher) {

		List<Option> optList = new ArrayList<Option>();

		int index = 1;
		for(Hashtable<HashableState, Action> option : options){
			String nomeOption = "opt"+index;
			optList.add(new MOOption(nomeOption,option,hasher));
		}
		return optList;

	}

	public static List<Option> criaObjOptionManual(List<Hashtable<HashableState, Action>> options, HashableStateFactory hasher) {

		List<Option> optList = new ArrayList<Option>();

		int index = 1;
		for(Hashtable<HashableState, Action> option : options){
			String nomeOption = "opt"+index;
			optList.add(new MOOption(nomeOption,option,hasher));
		}
		return optList;

	}

	/**
	 * Realize the merge score
	 * @param politicas
	 * @param s
	 */
	public int mergeScore(List<Hashtable<HashableState, Action>> politicas, Set<Integer> set) {


		//Contador de frequencia de estados e açoes nas politicas otimas iniciais
		List<Integer> pToEvaluate = new ArrayList<>(set);

		List<Hashtable<HashableState, Action>> toEvaluate = new ArrayList<Hashtable<HashableState, Action>>();


		int indexToEvaluate = 1;

		//Prepara uma lista de politicas para serem comparadas
		for(int element : pToEvaluate){	 
			toEvaluate.add(politicas.get(element));
		}
		//	System.out.println("toEvaluate "+toEvaluate.toString());


		//Pegando todos os estados que existem na primeira politica do power set
		List<HashableState> states = new ArrayList<>(toEvaluate.get(0).keySet());

		//Verifica se cada estado existe na outra politica e se as açoes escolhidas em cada um deles sao iguais
		for(HashableState s : states){
			boolean existeEmTodas = true;

			int indexToEvaluate2 = 1;
			for(int currentPolicy=1;currentPolicy<toEvaluate.size();currentPolicy++){
				//Avalia se o estado existe nas outras politicas e se a acao eh igual
				if(!toEvaluate.get(currentPolicy).containsKey(s)){
					existeEmTodas = false;
					break;
				}

			}

			//Se existir em todas, compara as acoes escolhidas
			if(existeEmTodas){
				String acaoNaPrimeira = toEvaluate.get(0).get(s).actionName();
				boolean igualEmTodas = true;


				//Verifica se todas as açoes sao iguais
				while(igualEmTodas && indexToEvaluate2<pToEvaluate.size()){
					if(!acaoNaPrimeira.equals(toEvaluate.get(indexToEvaluate2).get(s).actionName())){
						igualEmTodas = false;
					}

					indexToEvaluate2++;
				}


				//Achou um estado em que ambas sao iguais, entao soma um no score
				if(igualEmTodas && politicas.size()>0){
					scoreExp1+=1;
				}

			}

		}

		//Multiplica o tamanho das politicas pela frequencia  (pToEvaluate.size())
		scoreExp1 *= pToEvaluate.size();


		//retorna a option candidata
		return scoreExp1;


	}

	public Hashtable<HashableState, Action> mergePolicy(List<Hashtable<HashableState, Action>> politicas, Set<Integer> set) {

		//Contador de frequencia de estados e açoes nas politicas otimas iniciais
		List<Integer> pToEvaluate = new ArrayList<>(set);

		List<Hashtable<HashableState, Action>> toEvaluate = new ArrayList<Hashtable<HashableState, Action>>();

		Hashtable<HashableState, Action> toMerge = new Hashtable<HashableState, Action>(); //criar uma lista para possuir o merge das politicas

		int indexToEvaluate = 1;

		//Prepara uma lista de politicas para serem comparadas
		for(int element : pToEvaluate){	 
			toEvaluate.add(politicas.get(element));
		}


		//Pegando todos os estados que existem na primeira politica do power set
		List<HashableState> states = new ArrayList<>(toEvaluate.get(0).keySet());

		//Verifica se cada estado existe na outra politica e se as açoes escolhidas em cada um deles sao iguais
		for(HashableState s : states){
			boolean existeEmTodas = true;

			int indexToEvaluate2 = 1;
			//aki! ver um jeito de fazer cair no if(existeEmTodas)
			for(int currentPolicy=1;currentPolicy<toEvaluate.size();currentPolicy++){
				//Avalia se o estado existe nas outras politicas e se a acao eh igual
				//nunca caindo aki!!
				if(!toEvaluate.get(currentPolicy).containsKey(s)){
					existeEmTodas = false;
					break;
				}

			}

			//Se existir em todas, compara as acoes escolhidas
			if(existeEmTodas){
				String acaoNaPrimeira = toEvaluate.get(0).get(s).actionName();
				boolean igualEmTodas = true;


				//Verifica se todas as açoes sao iguais
				while(igualEmTodas && indexToEvaluate2<pToEvaluate.size()){
					if(!acaoNaPrimeira.equals(toEvaluate.get(indexToEvaluate2).get(s).actionName())){
						igualEmTodas = false;
					}
					indexToEvaluate2++;
				}

				if(!(toMerge == null)){
					if(igualEmTodas && politicas.size()>0){
						if(!(politicas.get(0).get(s) == null)){
							toMerge.put(s,politicas.get(0).get(s));

						}
					}
				}
			}


		}
		return toMerge;
	}


	//criar o objeto do ouro e passar ele
	private GridGold[] createGolds() {

		boolean criaGoldRandom = false;


		int qtdGold = 0;
		GridGold[] goldState = new GridGold[qtdGold];  //parametro para informar quantos ouros vao ter no mapa


		//criar o ouro randomicamente
		if(criaGoldRandom){

			Random random = new Random();

			int x;
			int y;

			for(int i=0; i<qtdGold; i++){

				x = random.nextInt(11);
				y = random.nextInt(11);

				//se o ouro nascer em posiçoes contendo muros, gerar novamente os numeros
				while((x==0 && y ==4) || (x==0 && y ==5) || (x==2 && y ==4) || (x==2 && y ==5) ||  (x==3 && y ==4) || (x==3 && y ==5) || (x==4 && y ==4) || (x==4 && y ==5) || (x==5 && y ==4) || (x==5 && y ==5) || (x==6 && y ==4) || (x==6 && y ==5) || (x==7 && y ==4) || (x==7 && y ==5)  || (x==8 && y ==4) || (x==8 && y ==5) || (x==9 && y ==4) || (x==9 && y ==5) || (x==10 && y ==4) || (x==10 && y ==5)){
					y = random.nextInt(11);
					x = random.nextInt(11);
				}
				goldState[i] = new GridGold(x,y,"gold"+qtdGold);
			}
		}


		//se nao for pra criar os ouros randomicamente
		else{

//			goldState[0] = new GridGold(0,10,"gold1");
//			goldState[1] = new GridGold(4,10,"gold2");
//			goldState[2] = new GridGold(4,6,"gold3");
//			goldState[3] = new GridGold(7,0,"gold4");
//			goldState[4] = new GridGold(4,0,"gold5");
//			goldState[5] = new GridGold(10,10,"gold6");

		}
		return goldState;
	}


	private GridAgent2 createAgent() {

		//boolean criaAgentRandom = true;

		Random random = new Random();

		int x = 0;
		int y = 0;


		GridAgent2 agentState = new GridAgent2(); 


		if(dominio4Rodar && agenteRandom){
			//	for(int i=0; i<qtdAgents; i++){
			x = random.nextInt(11);
			y = random.nextInt(11);
			/*-------------------------------
			horizontalWall(0, 0, 5);
			horizontalWall(2, 4, 5);
			horizontalWall(6, 7, 4);
			horizontalWall(9, 10, 4);

			verticalWall(0, 0, 5);
			verticalWall(2, 7, 5);
			verticalWall(9, 10, 5);

			goldState[0] = new GridGold(10,10,"gold1");
			goldState[1] = new GridGold(4,10,"gold2");
			goldState[2] = new GridGold(9,3,"gold3");
			goldState[3] = new GridGold(10,5,"gold4");

			-------------------------------
			 */
			//se o agente nascer em posiçoes contendo muros ou ouro, gerar novamente os numeros
			while((x==10 && y==10) || (x==4 && y==10) || (x==9 && y ==3) || (x == 10 && y ==5) || (x==0 && y ==4) || (x==0 && y ==5) || (x==2 && y ==4) || (x==2 && y ==5) ||  (x==3 && y ==4) || (x==3 && y ==5) || (x==4 && y ==4) || (x==4 && y ==5) || (x==5 && y ==4) || (x==5 && y ==5) || (x==6 && y ==4) || (x==6 && y ==5) || (x==7 && y ==4) || (x==7 && y ==5)  || (x==8 && y ==4) || (x==8 && y ==5) || (x==9 && y ==4) || (x==9 && y ==5) || (x==10 && y ==4) || (x==10 && y ==5)){
				y = random.nextInt(11);
				x = random.nextInt(11);
			}
			agentState = new GridAgent2(x,y,"agent");
		}

		//se nao for pra criar os agentes randomicamente
		else{
//			x = random.nextInt(11);
//			y = random.nextInt(11);
			/*-------------------------------
			horizontalWall(0, 0, 7);
			horizontalWall(2, 3, 7);
			verticalWall(9, 10, 3);
			verticalWall(7, 8, 5);
			verticalWall(10, 10, 5);
			verticalWall(0, 0, 5);
			verticalWall(6, 6, 7);
			verticalWall(5, 7, 9);
			verticalWall(4, 4, 10);

			goldState[0] = new GridGold(10,10,"gold1");
			goldState[1] = new GridGold(4,10,"gold2");
			goldState[2] = new GridGold(9,3,"gold3");
			goldState[3] = new GridGold(10,5,"gold4");
			-------------------------------
			 */
			//se o agente nascer em posiçoes contendo muros, gerar novamente os numeros
//			while((x==10 && y==10) || (x==4 && y==10) || (x==9 && y ==3) || (x == 10 && y ==5) || (x==0 && y ==7) || (x==2 && y ==7) || (x==3 && y ==7) || (x==3 && y ==9) ||  (x==3 && y ==9) || (x==5 && y ==7) || (x==5 && y ==8) || (x==5 && y ==10) || (x==5 && y ==0) || (x==7 && y ==6) || (x==9 && y ==5) || (x==9 && y ==6) || (x==9 && y ==7) || (x==10 && y ==4)){
//				y = random.nextInt(11);
//				x = random.nextInt(11);
//			}
			agentState = new GridAgent2(x,y,"agent");
		}

		System.out.println("Estado inicial do agente:("+x+","+y+")");

		return agentState;
	}














	private GridAgent2[] createVariousAgent() {

		boolean criaAgentRandom = false;

		int qtdAgents = 1;

		GridAgent2[] agentState = new GridAgent2[qtdAgents]; 


		if(criaAgentRandom){

			Random random = new Random();

			int x;
			int y;

			for(int i=0; i<qtdAgents; i++){

				x = random.nextInt(10);
				y = random.nextInt(10);

				//se o ouro nascer em posiçoes contendo muros, gerar novamente os numeros
				while((x==0 && y ==4) || (x==0 && y ==5) || (x==2 && y ==4) || (x==2 && y ==5) ||  (x==3 && y ==4) || (x==3 && y ==5) || (x==4 && y ==4) || (x==4 && y ==5) || (x==5 && y ==4) || (x==5 && y ==5) || (x==6 && y ==4) || (x==6 && y ==5) || (x==7 && y ==4) || (x==7 && y ==5)  || (x==8 && y ==4) || (x==8 && y ==5) || (x==9 && y ==4) || (x==9 && y ==5) || (x==10 && y ==4) || (x==10 && y ==5)){
					y = random.nextInt(11);
					x = random.nextInt(11);
				}
				agentState[i] = new GridAgent2(x,y,"agent"+qtdAgents);
			}
		}

		//se nao for pra criar os agentes randomicamente
		else{
			agentState[qtdAgents] = new GridAgent2(0, 0);
		}


		return agentState;
	}






}
