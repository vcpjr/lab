package app;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.sql.Connection;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import dao.KGNodeDAO;
import dao.TweetDAO;
import pojo.KGNode;
import pojo.Tweet;
import service.BridgeExecutor;
import service.NerdExecutor;
import util.CSVReport;

/**
 * The main application, containing 2 of 3 steps from the proposal
 * 
 * 1 - Semantic enrichment and accounting
 * 	- Annotation of textual clips (tweets) with DBpedia Spotlight
 *  - Construction of a hierarchy of hit resources (ranking with de most mentioned DBpedia resources)
 * 
 * 2 - Semantic Data Cube Construction
 *  - Manual Key Brigdes construction (from domain experts)
 *  - Algorithm for checking and completion of the bridges
 * 
 * 
 * @author Vilmar César Pereira Júnior
 *
 */
public class App {
	private static final Logger LOG = LoggerFactory.getLogger(App.class);
	private static final String APP_ROOT = System.getProperty("user.dir");
	private static final File outputPath = new File(APP_ROOT,"output");
	private static ArrayList<Integer> rootIds = new ArrayList<>();



	public static void main(String[] args) {

		//TODO Future experiment to verify the impact of larger datasets and databases
		//runPerformanceTests();

		KGNodeDAO dao = new KGNodeDAO();
		TweetDAO tDAO = new TweetDAO();

		//Be careful! Each new execution deletes all registers!
		dao.deleteAll();
		tDAO.deleteAll();

		if (args.length < 3) {
			System.err.println("Parameters not found. For run this application use follow command.");
			System.out.println("./run.sh <dataset> <confidence_level> <language>\n");
			System.exit(1);
		}

		LOG.info("Starting Application.");
		String filePath = args[0];
		float confidence = Float.parseFloat(args[1]);
		String language = args[2];
		File inputFile = new File(filePath);

		if (!inputFile.exists()) {
			LOG.error("File not found!");
			System.err.println("File not found!");
			System.exit(1);
		}

		LOG.info("***** STEP 1: SEMANTIC ENRICHMENT AND ACCOUNTING");
		NerdExecutor nerdExecutor = new NerdExecutor(inputFile);
		nerdExecutor.execute(confidence, language);
		 
		
		LOG.info("***** STEP 2: SEMANTIC DATA CUBE CONSTRUCTION");
		dao.deleteAllBridges();
		createKeyBridges();

		BridgeExecutor bridgeService = new BridgeExecutor();
		bridgeService.execute();

		testGenerateRDF();
		
		/*
		rootIds.add(15280); //
		rootIds.add(14524); //
		rootIds.add(14383); //
		rootIds.add(14449); //
		rootIds.add(14385);

		KGNodeDAO dao = new KGNodeDAO();
		dao.deleteAllHierarchies();
		testGenerateHierarchies(rootIds);
		/*


		/*
		//TODO Future work -> create the final step 3: OLAP ANALYSIS
		//SemanticDataCubeExecutor semanticDataCubeExecutor = new SemanticDataCubeExecutor(hierarchyFile);
		//semanticDataCubeExecutor.execute(); //build the DW
		 * 
		 */
	}


	
	private static void runPerformanceTests() {
		generateSyntheticData(10);
		runQueryListAllNodes(10);

		/*
		rootIds.add(14412); //Organization
		rootIds.add(14395); //
		rootIds.add(14392); //
		rootIds.add(14374); //
		rootIds.add(15363); //
		rootIds.add(14408); //
		rootIds.add(15280); //
		rootIds.add(14524); //
		rootIds.add(14383); //
		rootIds.add(14449); //
		rootIds.add(14385);

		for(int i = 1000; i<(1000*100); i*=10){
			generateSyntheticData(i);
			runQueryGenerateHierarchies(i);
			runQueryListAllNodes(i);
		}
		*/
		
	}


	private static void generateSyntheticData(int growthFactor) {
		KGNodeDAO dao = new KGNodeDAO();
		TweetDAO tDAO = new TweetDAO();

		List<KGNode> nodes = dao.list();
		int amountOfNewRegisters = nodes.size() * growthFactor;
		
		// TODO gerar a quantidade de registros * growthFactor
		for(int i = 0; i< amountOfNewRegisters; i++){
			KGNode node = nodes.get((int) (Math.random() * nodes.size()));
			KGNode newNode = new KGNode((node.getUri() + "#GROWTH_FACTOR=" + growthFactor), node.getNodeType());
			newNode.setDirectHits(node.getDirectHits());
			newNode.setIndirectHitsSubclassOf(node.getIndirectHitsSubclassOf());
			newNode.setIndirectHitsType(node.getIndirectHitsType());
			newNode.setId(null);
			
			dao.insert(newNode);
		}

		List<Tweet> tweets = tDAO.list();
		int amountOfTweets = tweets.size() * growthFactor;
		for(int i = 0; i< amountOfTweets; i++){
			Tweet tweet = tweets.get((int) (Math.random() * tweets.size()));
			Tweet newTweet = new Tweet(tweet.getId(), tweet.getUserId(), tweet.getText(), tweet.getCreationDate(), tweet.isRetweet());
			tDAO.insert(newTweet);
		}
	}
	
	
	private static double runQueryListAllNodes(int growthFactor) {
		double executionTime = 0;
		KGNodeDAO dao = new KGNodeDAO();
		// TODO Executar as consultas nas tabelas KGNODE_GROWTHFACTOR

		long start = System.nanoTime();
		dao.list();
		long end = System.nanoTime();
		long elapsed = (end - start)/1000000;
		System.out.println((System.currentTimeMillis() + ": " + "sendOne.executeQuery(): " + elapsed + " milis\n"));

		dao.insertQueryExecutionTime("KGNodeDAO.list()", elapsed, growthFactor);

		return executionTime;
	}

	private static double runQueryGenerateHierarchies(int growthFactor) {
		double executionTime = 0;
		KGNodeDAO dao = new KGNodeDAO();
		// TODO Executar as consultas nas tabelas KGNODE_GROWTHFACTOR

		long start = System.nanoTime();
		testGenerateHierarchies(rootIds);
		long end = System.nanoTime();
		long elapsed = (end - start)/1000000;
		System.out.println((System.currentTimeMillis() + ": " + "sendOne.executeQuery(): " + elapsed + " milis\n"));

		dao.insertQueryExecutionTime("generateHierarchies()", elapsed, growthFactor);

		return executionTime;
	}
	
	private static double runQuerySuperclassesPath(int growthFactor) {
		double executionTime = 0;
		KGNodeDAO dao = new KGNodeDAO();
		// TODO Executar as consultas nas tabelas KGNODE_GROWTHFACTOR

		long start = System.nanoTime();
		dao.list();
		long end = System.nanoTime();
		long elapsed = (end - start)/1000000;
		System.out.println((System.currentTimeMillis() + ": " + "sendOne.executeQuery(): " + elapsed + " milis\n"));

		dao.insertQueryExecutionTime("superclassesPath()", elapsed, growthFactor);

		return executionTime;
	}

	private static void testGenerateHierarchies(ArrayList<Integer> rootIds) {
		LOG.info("***************************Hierarchies CSV generation**************************");
		CSVReport hierarchyReport = new CSVReport("Hierarchy Root; #Max. Depth; #Total members; #Max Node Fan-Out");
		KGNodeDAO dao = new KGNodeDAO();

		for(int idRootHierarchy: rootIds){
			KGNode root = dao.getById(idRootHierarchy, dao.getConnection());
			HashMap<Integer, ArrayList<KGNode>> hierarchy = dao.getHierarchy(root, root, 1, null, dao.getConnection());

			System.out.println("**********************************");
			System.out.println("Hierarchy from: " + root.toString());
			int totalMembers = 1;
			int fanOut = 0;
			for(int level: hierarchy.keySet()){

				System.out.println("** Level: " + level);
				ArrayList<KGNode> classes = hierarchy.get(level);

				for(KGNode node: classes){
					System.out.println("**** " + node.toString());
					totalMembers++;
				}
				if(level == 1){
					fanOut = totalMembers;
				}
			}

			int maxDepth = hierarchy.keySet().size();

			String nodeText = String.format(Locale.US, "%s;%d;%d;%d", root.getLabel(), maxDepth, totalMembers, fanOut);
			hierarchyReport.append(nodeText);
			System.out.println("**********************************");
		}
		hierarchyReport.generate(new File(outputPath, "hierarchies.csv"));
		LOG.info("***************************Hierarchies CSV end**************************");


	}

	private static void testGenerateRDF() {
		KGNodeDAO dao = new KGNodeDAO();

		ArrayList<KGNode> instances = dao.getByNodeType(KGNode.NODE_TYPE_INSTANCE);
		String rdf = "";

		ArrayList<Integer> visitedTypeIds = new ArrayList<>();
		ArrayList<Integer> generatedIdsLabels = new ArrayList<>();

		Connection conn = dao.getConnection();
		for(KGNode instance: instances){
			ArrayList<KGNode> types = dao.getTypesByInstanceId(instance.getId(), conn);

			for(KGNode type: types){
				//rdf += dao.createRDFLink(instance, type, KGNode.RELATIONSHIP_TYPE_OF_URI) + "\n";
				if(!visitedTypeIds.contains(type.getId())){
					System.out.println("Visiting Type: " + type.toString());

					//TODO FIXME owl:Thing está repetido
					visitedTypeIds.add(type.getId());
					rdf += dao.createLabel(type) + "\n";
					generatedIdsLabels.add(type.getId());
					ArrayList<KGNode> path = dao.getSuperclassesPath(type.getId(), dao.getConnection());
					KGNode previous = type;

					if(path!= null){
						for(KGNode nextNodeOnPath: path){
							if(!generatedIdsLabels.contains(nextNodeOnPath.getId())){
								rdf += dao.createLabel(nextNodeOnPath) + "\n";
								generatedIdsLabels.add(nextNodeOnPath.getId());
							}
							rdf += dao.createRDFLink(previous, nextNodeOnPath, KGNode.RELATIONSHIP_SUBCLASS_OF_URI) + "\n";
							previous = nextNodeOnPath;
						}
					}
				}
			}
		}

		generateRDFFile(rdf);
		System.out.println("*********** RDF File Generated ***********");
	}

	private static void generateRDFFile(String rdf) {
		File file = new File(outputPath, "nerdExecutionRDF.rdf"); 
		FileWriter writer;
		try {
			writer = new FileWriter(file);
			writer.write(rdf);
			writer.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	private static void createKeyBridges() {
		//TODO ler de arquivo futuramente
		KGNodeDAO dao = new KGNodeDAO();

		dao.insertBridge(KGNode.getDBpediaClassURI("DBpedia:SportsTeam"),"gr:BusinessEntity", KGNode.BRIDGE_TYPE_KEY);
		dao.insertBridge(KGNode.getDBpediaClassURI("DBpedia:Organisation"),"gr:BusinessEntity", KGNode.BRIDGE_TYPE_KEY);
		dao.insertBridge(KGNode.getDBpediaClassURI("DBpedia:AdministrativeRegion"),"gr:Location", KGNode.BRIDGE_TYPE_KEY);
		dao.insertBridge(KGNode.getDBpediaClassURI("DBpedia:Album"),"gr:ProductOrService", KGNode.BRIDGE_TYPE_KEY);
		dao.insertBridge(KGNode.getDBpediaClassURI("DBpedia:Animal"),"gr:ProductOrService", KGNode.BRIDGE_TYPE_KEY);
		dao.insertBridge(KGNode.getDBpediaClassURI("DBpedia:Anime"),"gr:ProductOrService", KGNode.BRIDGE_TYPE_KEY);
		dao.insertBridge(KGNode.getDBpediaClassURI("DBpedia:Artist"),"gr:BusinessEntity", KGNode.BRIDGE_TYPE_KEY);
		dao.insertBridge(KGNode.getDBpediaClassURI("DBpedia:Athlete"),"gr:BusinessEntity", KGNode.BRIDGE_TYPE_KEY);
		dao.insertBridge(KGNode.getDBpediaClassURI("DBpedia:Award"),"gr:Brand", KGNode.BRIDGE_TYPE_KEY);
		dao.insertBridge(KGNode.getDBpediaClassURI("DBpedia:Band"),"gr:BusinessEntity", KGNode.BRIDGE_TYPE_KEY);
		dao.insertBridge(KGNode.getDBpediaClassURI("DBpedia:Beverage"),"gr:ProductOrService", KGNode.BRIDGE_TYPE_KEY);
		dao.insertBridge(KGNode.getDBpediaClassURI("DBpedia:Biomolecule"),"gr:ProductOrService", KGNode.BRIDGE_TYPE_KEY);
		dao.insertBridge(KGNode.getDBpediaClassURI("DBpedia:BodyOfWater"),"gr:Location", KGNode.BRIDGE_TYPE_KEY);
		dao.insertBridge(KGNode.getDBpediaClassURI("DBpedia:Book"),"gr:ProductOrService", KGNode.BRIDGE_TYPE_KEY);
		dao.insertBridge(KGNode.getDBpediaClassURI("DBpedia:BritishRoyalty"),"gr:BusinessEntity", KGNode.BRIDGE_TYPE_KEY);
		dao.insertBridge(KGNode.getDBpediaClassURI("DBpedia:Broadcaster"),"gr:BusinessEntity", KGNode.BRIDGE_TYPE_KEY);
		dao.insertBridge(KGNode.getDBpediaClassURI("DBpedia:BroadcastNetwork"),"gr:BusinessEntity", KGNode.BRIDGE_TYPE_KEY);
		dao.insertBridge(KGNode.getDBpediaClassURI("DBpedia:Building"),"gr:Location", KGNode.BRIDGE_TYPE_KEY);
		dao.insertBridge(KGNode.getDBpediaClassURI("DBpedia:Cartoon"),"gr:ProductOrService", KGNode.BRIDGE_TYPE_KEY);
		dao.insertBridge(KGNode.getDBpediaClassURI("DBpedia:ChemicalCompound"),"gr:ProductOrService", KGNode.BRIDGE_TYPE_KEY);
		dao.insertBridge(KGNode.getDBpediaClassURI("DBpedia:ChemicalSubstance"),"gr:ProductOrService", KGNode.BRIDGE_TYPE_KEY);
		dao.insertBridge(KGNode.getDBpediaClassURI("DBpedia:City"),"gr:Location", KGNode.BRIDGE_TYPE_KEY);
		dao.insertBridge(KGNode.getDBpediaClassURI("DBpedia:Company"),"gr:BusinessEntity", KGNode.BRIDGE_TYPE_KEY);
		dao.insertBridge(KGNode.getDBpediaClassURI("DBpedia:Country"),"gr:Location", KGNode.BRIDGE_TYPE_KEY);
		dao.insertBridge(KGNode.getDBpediaClassURI("DBpedia:Currency"),"gr:PriceSpecification.hasCurrency", KGNode.BRIDGE_TYPE_KEY);
		dao.insertBridge(KGNode.getDBpediaClassURI("DBpedia:Device"),"gr:ProductOrService", KGNode.BRIDGE_TYPE_KEY);
		dao.insertBridge(KGNode.getDBpediaClassURI("DBpedia:EducationalInstitution"),"gr:BusinessEntity", KGNode.BRIDGE_TYPE_KEY);
		dao.insertBridge(KGNode.getDBpediaClassURI("DBpedia:Film"),"gr:ProductOrService", KGNode.BRIDGE_TYPE_KEY);
		dao.insertBridge(KGNode.getDBpediaClassURI("DBpedia:Food"),"gr:ProductOrService", KGNode.BRIDGE_TYPE_KEY);
		dao.insertBridge(KGNode.getDBpediaClassURI("DBpedia:Genre"),"gr:ProductOrService", KGNode.BRIDGE_TYPE_KEY);
		dao.insertBridge(KGNode.getDBpediaClassURI("DBpedia:GovernmentAgency"),"gr:BusinessEntity", KGNode.BRIDGE_TYPE_KEY);
		dao.insertBridge(KGNode.getDBpediaClassURI("DBpedia:Infrastructure"),"gr:BusinessEntity", KGNode.BRIDGE_TYPE_KEY);
		dao.insertBridge(KGNode.getDBpediaClassURI("DBpedia:Island"),"gr:Location", KGNode.BRIDGE_TYPE_KEY);
		dao.insertBridge(KGNode.getDBpediaClassURI("DBpedia:Lake"),"gr:Location", KGNode.BRIDGE_TYPE_KEY);
		dao.insertBridge(KGNode.getDBpediaClassURI("DBpedia:Locomotive"),"gr:ProductOrService", KGNode.BRIDGE_TYPE_KEY);
		dao.insertBridge(KGNode.getDBpediaClassURI("DBpedia:LunarCrater"),"gr:Location", KGNode.BRIDGE_TYPE_KEY);
		dao.insertBridge(KGNode.getDBpediaClassURI("DBpedia:Magazine"),"gr:ProductOrService", KGNode.BRIDGE_TYPE_KEY);
		dao.insertBridge(KGNode.getDBpediaClassURI("DBpedia:MeanOfTransportation"),"gr:ProductOrService", KGNode.BRIDGE_TYPE_KEY);
		dao.insertBridge(KGNode.getDBpediaClassURI("DBpedia:Museum"),"gr:BusinessEntity", KGNode.BRIDGE_TYPE_KEY);
		dao.insertBridge(KGNode.getDBpediaClassURI("DBpedia:MusicalArtist"),"gr:BusinessEntity", KGNode.BRIDGE_TYPE_KEY);
		dao.insertBridge(KGNode.getDBpediaClassURI("DBpedia:MusicalWork"),"gr:ProductOrService", KGNode.BRIDGE_TYPE_KEY);
		dao.insertBridge(KGNode.getDBpediaClassURI("DBpedia:MusicGenre"),"gr:ProductOrService", KGNode.BRIDGE_TYPE_KEY);
		dao.insertBridge(KGNode.getDBpediaClassURI("DBpedia:NaturalPlace"),"gr:Location", KGNode.BRIDGE_TYPE_KEY);
		dao.insertBridge(KGNode.getDBpediaClassURI("DBpedia:Newspaper"),"gr:ProductOrService", KGNode.BRIDGE_TYPE_KEY);
		dao.insertBridge(KGNode.getDBpediaClassURI("DBpedia:Non-ProfitOrganisation"),"gr:BusinessEntity", KGNode.BRIDGE_TYPE_KEY);
		dao.insertBridge(KGNode.getDBpediaClassURI("DBpedia:OfficeHolder"),"gr:BusinessEntity", KGNode.BRIDGE_TYPE_KEY);
		dao.insertBridge(KGNode.getDBpediaClassURI("DBpedia:PeriodicalLiterature"),"gr:ProductOrService", KGNode.BRIDGE_TYPE_KEY);
		dao.insertBridge(KGNode.getDBpediaClassURI("DBpedia:Place"),"gr:Location", KGNode.BRIDGE_TYPE_KEY);
		dao.insertBridge(KGNode.getDBpediaClassURI("DBpedia:PopulatedPlace"),"gr:Location", KGNode.BRIDGE_TYPE_KEY);
		dao.insertBridge(KGNode.getDBpediaClassURI("DBpedia:RaceHorse"),"gr:ProductOrService", KGNode.BRIDGE_TYPE_KEY);
		dao.insertBridge(KGNode.getDBpediaClassURI("DBpedia:RadioStation"),"gr:BusinessEntity", KGNode.BRIDGE_TYPE_KEY);
		dao.insertBridge(KGNode.getDBpediaClassURI("DBpedia:RecordLabel"),"gr:BusinessEntity", KGNode.BRIDGE_TYPE_KEY);
		dao.insertBridge(KGNode.getDBpediaClassURI("DBpedia:Region"),"gr:Location", KGNode.BRIDGE_TYPE_KEY);
		dao.insertBridge(KGNode.getDBpediaClassURI("DBpedia:River"),"gr:Location", KGNode.BRIDGE_TYPE_KEY);
		dao.insertBridge(KGNode.getDBpediaClassURI("DBpedia:Road"),"gr:Location", KGNode.BRIDGE_TYPE_KEY);
		dao.insertBridge(KGNode.getDBpediaClassURI("DBpedia:Rocket"),"gr:ProductOrService", KGNode.BRIDGE_TYPE_KEY);
		dao.insertBridge(KGNode.getDBpediaClassURI("DBpedia:Settlement"),"gr:Location", KGNode.BRIDGE_TYPE_KEY);
		dao.insertBridge(KGNode.getDBpediaClassURI("DBpedia:Ship"),"gr:ProductOrService", KGNode.BRIDGE_TYPE_KEY);
		dao.insertBridge(KGNode.getDBpediaClassURI("DBpedia:Single"),"gr:ProductOrService", KGNode.BRIDGE_TYPE_KEY);
		dao.insertBridge(KGNode.getDBpediaClassURI("DBpedia:SoccerClub"),"gr:BusinessEntity", KGNode.BRIDGE_TYPE_KEY);
		dao.insertBridge(KGNode.getDBpediaClassURI("DBpedia:Software"),"gr:ProductOrService", KGNode.BRIDGE_TYPE_KEY);
		dao.insertBridge(KGNode.getDBpediaClassURI("DBpedia:SportsLeague"),"gr:Brand", KGNode.BRIDGE_TYPE_KEY);
		dao.insertBridge(KGNode.getDBpediaClassURI("DBpedia:Stream"),"gr:Location", KGNode.BRIDGE_TYPE_KEY);
		dao.insertBridge(KGNode.getDBpediaClassURI("DBpedia:TelevisionShow"),"gr:ProductOrService", KGNode.BRIDGE_TYPE_KEY);
		dao.insertBridge(KGNode.getDBpediaClassURI("DBpedia:TelevisionStation"),"gr:BusinessEntity", KGNode.BRIDGE_TYPE_KEY);
		dao.insertBridge(KGNode.getDBpediaClassURI("DBpedia:Town"),"gr:Location", KGNode.BRIDGE_TYPE_KEY);
		dao.insertBridge(KGNode.getDBpediaClassURI("DBpedia:University"),"gr:BusinessEntity", KGNode.BRIDGE_TYPE_KEY);
		dao.insertBridge(KGNode.getDBpediaClassURI("DBpedia:VideoGame"),"gr:ProductOrService", KGNode.BRIDGE_TYPE_KEY);
		dao.insertBridge(KGNode.getDBpediaClassURI("DBpedia:Village"),"gr:Location", KGNode.BRIDGE_TYPE_KEY);
		dao.insertBridge(KGNode.getDBpediaClassURI("DBpedia:Weapon"),"gr:ProductOrService", KGNode.BRIDGE_TYPE_KEY);
		dao.insertBridge(KGNode.getDBpediaClassURI("DBpedia:Work"),"gr:Product", KGNode.BRIDGE_TYPE_KEY);
		dao.insertBridge(KGNode.getDBpediaClassURI("DBpedia:Wrestler"),"gr:BusinessEntity", KGNode.BRIDGE_TYPE_KEY);
		dao.insertBridge(KGNode.getDBpediaClassURI("DBpedia:Writer"),"gr:BusinessEntity", KGNode.BRIDGE_TYPE_KEY);
		dao.insertBridge(KGNode.getDBpediaClassURI("DBpedia:WrittenWork"),"gr:ProductOrService", KGNode.BRIDGE_TYPE_KEY);

		//dao.insertBridge(KGNode.getDBpediaClassURI("DBpedia:Agent"),"more general than gr classes", KGNode.BRIDGE_TYPE_KEY);
		//dao.insertBridge(KGNode.getDBpediaClassURI("DBpedia:ArchitecturalStructure"),"no gr class", KGNode.BRIDGE_TYPE_KEY);
		//dao.insertBridge(KGNode.getDBpediaClassURI("DBpedia:Bacteria"),"no gr class", KGNode.BRIDGE_TYPE_KEY);
		//dao.insertBridge(KGNode.getDBpediaClassURI("DBpedia:ComicsCharacter"),"no gr class", KGNode.BRIDGE_TYPE_KEY);
		//dao.insertBridge(KGNode.getDBpediaClassURI("DBpedia:Disease"),"no gr class", KGNode.BRIDGE_TYPE_KEY);
		//dao.insertBridge(KGNode.getDBpediaClassURI("DBpedia:EthnicGroup"),"No gr class, but may be relevant for e-business", KGNode.BRIDGE_TYPE_KEY);
		//dao.insertBridge(KGNode.getDBpediaClassURI("DBpedia:Eukaryote"),"no gr class", KGNode.BRIDGE_TYPE_KEY);
		//dao.insertBridge(KGNode.getDBpediaClassURI("DBpedia:Event"),"No gr class, but may be relevant for e-business", KGNode.BRIDGE_TYPE_KEY);
		//dao.insertBridge(KGNode.getDBpediaClassURI("DBpedia:FictionalCharacter"),"no gr class", KGNode.BRIDGE_TYPE_KEY);
		//dao.insertBridge(KGNode.getDBpediaClassURI("DBpedia:Fish"),"no gr class", KGNode.BRIDGE_TYPE_KEY);
		//dao.insertBridge(KGNode.getDBpediaClassURI("DBpedia:GivenName"),"No gr class, but may be relevant for e-business", KGNode.BRIDGE_TYPE_KEY);
		//dao.insertBridge(KGNode.getDBpediaClassURI("DBpedia:InformationAppliance"),"No gr class, but may be relevant for e-business", KGNode.BRIDGE_TYPE_KEY);
		//dao.insertBridge(KGNode.getDBpediaClassURI("DBpedia:Insect"),"no gr class", KGNode.BRIDGE_TYPE_KEY);
		//dao.insertBridge(KGNode.getDBpediaClassURI("DBpedia:Language"),"No gr class, but may be relevant for e-business", KGNode.BRIDGE_TYPE_KEY);
		//dao.insertBridge(KGNode.getDBpediaClassURI("DBpedia:Mammal"),"no gr class", KGNode.BRIDGE_TYPE_KEY);
		//dao.insertBridge(KGNode.getDBpediaClassURI("DBpedia:MilitaryUnit"),"no gr class", KGNode.BRIDGE_TYPE_KEY);
		//dao.insertBridge(KGNode.getDBpediaClassURI("DBpedia:MusicFestival"),"No gr class, but may be relevant for e-business", KGNode.BRIDGE_TYPE_KEY);
		//dao.insertBridge(KGNode.getDBpediaClassURI("DBpedia:Name"),"No gr class, but may be relevant for e-business", KGNode.BRIDGE_TYPE_KEY);
		//dao.insertBridge(KGNode.getDBpediaClassURI("DBpedia:Person"),"No gr class, but may be relevant for e-business", KGNode.BRIDGE_TYPE_KEY);
		//dao.insertBridge(KGNode.getDBpediaClassURI("DBpedia:Plant"),"no gr class", KGNode.BRIDGE_TYPE_KEY);
		//dao.insertBridge(KGNode.getDBpediaClassURI("DBpedia:PoliticalParty"),"no gr class", KGNode.BRIDGE_TYPE_KEY);
		//dao.insertBridge(KGNode.getDBpediaClassURI("DBpedia:Politician"),"no gr class", KGNode.BRIDGE_TYPE_KEY);
		//dao.insertBridge(KGNode.getDBpediaClassURI("DBpedia:Reptile"),"no gr class", KGNode.BRIDGE_TYPE_KEY);
		//dao.insertBridge(KGNode.getDBpediaClassURI("DBpedia:RouteOfTransportation"),"no gr class", KGNode.BRIDGE_TYPE_KEY);
		//dao.insertBridge(KGNode.getDBpediaClassURI("DBpedia:Royalty"),"no gr class", KGNode.BRIDGE_TYPE_KEY);
		//dao.insertBridge(KGNode.getDBpediaClassURI("DBpedia:SoapCharacter"),"no gr class", KGNode.BRIDGE_TYPE_KEY);
		//dao.insertBridge(KGNode.getDBpediaClassURI("DBpedia:SoccerPlayer"),"no gr class", KGNode.BRIDGE_TYPE_KEY);
		//dao.insertBridge(KGNodtee.getDBpediaClassURI("DBpedia:Species"),"no gr class", KGNode.BRIDGE_TYPE_KEY);
		//dao.insertBridge(KGNode.getDBpediaClassURI("DBpedia:TimePeriod"),"No gr class, but may be relevant for e-business", KGNode.BRIDGE_TYPE_KEY);
		//dao.insertBridge(KGNode.getDBpediaClassURI("DBpedia:TopicalConcept"),"no gr class", KGNode.BRIDGE_TYPE_KEY);
		//dao.insertBridge(KGNode.getDBpediaClassURI("DBpedia:Website"),"No gr class, but may be relevant for e-business", KGNode.BRIDGE_TYPE_KEY);
		//dao.insertBridge(KGNode.getDBpediaClassURI("DBpedia:Year"),"No gr class, but may be relevant for e-business", KGNode.BRIDGE_TYPE_KEY);
	}

	public static boolean containsLabel(final List<KGNode> list, final String label){
		return list.stream().filter(o -> o.getLabel().equals(label)).findFirst().isPresent();
	}

	private static void printHierarchies() {

		//		System.out.println("%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%");
		//		KGNodeDAO dao = new KGNodeDAO();
		//		List<KGNode> nodos = dao.list();
		//
		//		for(KGNode n: nodos){
		//			System.out.println("% NODE: " + n.toString());
		//			System.out.println("%%% SUBCLASSES");
		//			
		//			List<KGNode> subclasses = dao.getSuperclassesPath(n.getId(), null);
		//
		//			if(subclasses != null){
		//				for(KGNode s: subclasses){
		//					System.out.println("%% " + s.toString());
		//				}
		//			}
		//		}

		/*
        HashMap<KGNode, String> bridges = new HashMap<KGNode, String>();
        KGNode n1 = new KGNode("http://dbpedia.org/ontology/Agent");
        bridges.put(n1, "gr:BusinessEntity");

        KGNode n2 = new KGNode("http://dbpedia.org/ontology/Agent");

        List<KGNode> nodes = new ArrayList<>(bridges.keySet());
        String res = bridges.get(n2);
        System.out.println("Class GRO: " + res);
		 */
	}
}