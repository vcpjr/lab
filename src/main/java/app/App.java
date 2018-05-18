package app;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import dao.KGNodeDAO;
import pojo.KGNode;
import service.BridgeExecutor;

public class App {
	private static final Logger LOG = LoggerFactory.getLogger(App.class);

	public static void main(String[] args) {
		/*
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
        
		
		//STEP 1: SEMANTIC ENRICHMENT AND ACCOUNTING
		
		////////dao.deleteAll();
		NerdExecutor nerdExecutor = new NerdExecutor(inputFile);
		nerdExecutor.execute(confidence, language);
		*/
		KGNodeDAO dao = new KGNodeDAO();

		//TODO executar o STEP 2 daqui em diante
		//STEP 2: SEMANTIC DATA CUBE CONSTRUCTION
		dao.deleteAllBridges();
		createKeyBridges();
		
		BridgeExecutor bridgeService = new BridgeExecutor();
		bridgeService.execute();

		//TODO desenhar os caminhos -> Ver APIs para isso
		//http://graphstream-project.org/
		//http://jgrapht.org/
		//http://bfo.com/download/
		
		/*
		//TODO create the final step
		//SemanticDataCubeExecutor semanticDataCubeExecutor = new SemanticDataCubeExecutor(hierarchyFile);
		//semanticDataCubeExecutor.execute(); //build the DW
		 * 
		 */
	}

	private static void createKeyBridges() {
		//TODO ler de arquivo??
		KGNodeDAO dao = new KGNodeDAO();
		dao.insertBridge(KGNode.getDBpediaClassURI("DBpedia:Agent"),"more general than gr classes", KGNode.BRIDGE_TYPE_KEY);
		dao.insertBridge(KGNode.getDBpediaClassURI("DBpedia:AdministrativeRegion"),"gr:Location", KGNode.BRIDGE_TYPE_KEY);
		dao.insertBridge(KGNode.getDBpediaClassURI("DBpedia:Album"),"gr:ProductOrService", KGNode.BRIDGE_TYPE_KEY);
		dao.insertBridge(KGNode.getDBpediaClassURI("DBpedia:Animal"),"gr:ProductOrService", KGNode.BRIDGE_TYPE_KEY);
		dao.insertBridge(KGNode.getDBpediaClassURI("DBpedia:Anime"),"gr:ProductOrService", KGNode.BRIDGE_TYPE_KEY);
		dao.insertBridge(KGNode.getDBpediaClassURI("DBpedia:ArchitecturalStructure"),"no gr class", KGNode.BRIDGE_TYPE_KEY);
		dao.insertBridge(KGNode.getDBpediaClassURI("DBpedia:Artist"),"gr:BusinessEntity", KGNode.BRIDGE_TYPE_KEY);
		dao.insertBridge(KGNode.getDBpediaClassURI("DBpedia:Athlete"),"gr:BusinessEntity", KGNode.BRIDGE_TYPE_KEY);
		dao.insertBridge(KGNode.getDBpediaClassURI("DBpedia:Award"),"gr:Brand", KGNode.BRIDGE_TYPE_KEY);
		dao.insertBridge(KGNode.getDBpediaClassURI("DBpedia:Bacteria"),"no gr class", KGNode.BRIDGE_TYPE_KEY);
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
		dao.insertBridge(KGNode.getDBpediaClassURI("DBpedia:ComicsCharacter"),"no gr class", KGNode.BRIDGE_TYPE_KEY);
		dao.insertBridge(KGNode.getDBpediaClassURI("DBpedia:Company"),"gr:BusinessEntity", KGNode.BRIDGE_TYPE_KEY);
		dao.insertBridge(KGNode.getDBpediaClassURI("DBpedia:Country"),"gr:Location", KGNode.BRIDGE_TYPE_KEY);
		dao.insertBridge(KGNode.getDBpediaClassURI("DBpedia:Currency"),"gr:PriceSpecification.hasCurrency", KGNode.BRIDGE_TYPE_KEY);
		dao.insertBridge(KGNode.getDBpediaClassURI("DBpedia:Device"),"gr:ProductOrService", KGNode.BRIDGE_TYPE_KEY);
		dao.insertBridge(KGNode.getDBpediaClassURI("DBpedia:Disease"),"no gr class", KGNode.BRIDGE_TYPE_KEY);
		dao.insertBridge(KGNode.getDBpediaClassURI("DBpedia:EducationalInstitution"),"gr:BusinessEntity", KGNode.BRIDGE_TYPE_KEY);
		dao.insertBridge(KGNode.getDBpediaClassURI("DBpedia:EthnicGroup"),"No gr class, but may be relevant for e-business", KGNode.BRIDGE_TYPE_KEY);
		dao.insertBridge(KGNode.getDBpediaClassURI("DBpedia:Eukaryote"),"no gr class", KGNode.BRIDGE_TYPE_KEY);
		dao.insertBridge(KGNode.getDBpediaClassURI("DBpedia:Event"),"No gr class, but may be relevant for e-business", KGNode.BRIDGE_TYPE_KEY);
		dao.insertBridge(KGNode.getDBpediaClassURI("DBpedia:FictionalCharacter"),"no gr class", KGNode.BRIDGE_TYPE_KEY);
		dao.insertBridge(KGNode.getDBpediaClassURI("DBpedia:Film"),"gr:ProductOrService", KGNode.BRIDGE_TYPE_KEY);
		dao.insertBridge(KGNode.getDBpediaClassURI("DBpedia:Fish"),"no gr class", KGNode.BRIDGE_TYPE_KEY);
		dao.insertBridge(KGNode.getDBpediaClassURI("DBpedia:Food"),"gr:ProductOrService", KGNode.BRIDGE_TYPE_KEY);
		dao.insertBridge(KGNode.getDBpediaClassURI("DBpedia:Genre"),"gr:ProductOrService", KGNode.BRIDGE_TYPE_KEY);
		dao.insertBridge(KGNode.getDBpediaClassURI("DBpedia:GivenName"),"No gr class, but may be relevant for e-business", KGNode.BRIDGE_TYPE_KEY);
		dao.insertBridge(KGNode.getDBpediaClassURI("DBpedia:GovernmentAgency"),"gr:BusinessEntity", KGNode.BRIDGE_TYPE_KEY);
		dao.insertBridge(KGNode.getDBpediaClassURI("DBpedia:InformationAppliance"),"No gr class, but may be relevant for e-business", KGNode.BRIDGE_TYPE_KEY);
		dao.insertBridge(KGNode.getDBpediaClassURI("DBpedia:Infrastructure"),"gr:BusinessEntity", KGNode.BRIDGE_TYPE_KEY);
		dao.insertBridge(KGNode.getDBpediaClassURI("DBpedia:Insect"),"no gr class", KGNode.BRIDGE_TYPE_KEY);
		dao.insertBridge(KGNode.getDBpediaClassURI("DBpedia:Island"),"gr:Location", KGNode.BRIDGE_TYPE_KEY);
		dao.insertBridge(KGNode.getDBpediaClassURI("DBpedia:Lake"),"gr:Location", KGNode.BRIDGE_TYPE_KEY);
		dao.insertBridge(KGNode.getDBpediaClassURI("DBpedia:Language"),"No gr class, but may be relevant for e-business", KGNode.BRIDGE_TYPE_KEY);
		dao.insertBridge(KGNode.getDBpediaClassURI("DBpedia:Locomotive"),"gr:ProductOrService", KGNode.BRIDGE_TYPE_KEY);
		dao.insertBridge(KGNode.getDBpediaClassURI("DBpedia:LunarCrater"),"gr:Location", KGNode.BRIDGE_TYPE_KEY);
		dao.insertBridge(KGNode.getDBpediaClassURI("DBpedia:Magazine"),"gr:ProductOrService", KGNode.BRIDGE_TYPE_KEY);
		dao.insertBridge(KGNode.getDBpediaClassURI("DBpedia:Mammal"),"no gr class", KGNode.BRIDGE_TYPE_KEY);
		dao.insertBridge(KGNode.getDBpediaClassURI("DBpedia:MeanOfTransportation"),"gr:ProductOrService", KGNode.BRIDGE_TYPE_KEY);
		dao.insertBridge(KGNode.getDBpediaClassURI("DBpedia:MilitaryUnit"),"no gr class", KGNode.BRIDGE_TYPE_KEY);
		dao.insertBridge(KGNode.getDBpediaClassURI("DBpedia:Museum"),"gr:BusinessEntity", KGNode.BRIDGE_TYPE_KEY);
		dao.insertBridge(KGNode.getDBpediaClassURI("DBpedia:MusicalArtist"),"gr:BusinessEntity", KGNode.BRIDGE_TYPE_KEY);
		dao.insertBridge(KGNode.getDBpediaClassURI("DBpedia:MusicalWork"),"gr:ProductOrService", KGNode.BRIDGE_TYPE_KEY);
		dao.insertBridge(KGNode.getDBpediaClassURI("DBpedia:MusicFestival"),"No gr class, but may be relevant for e-business", KGNode.BRIDGE_TYPE_KEY);
		dao.insertBridge(KGNode.getDBpediaClassURI("DBpedia:MusicGenre"),"gr:ProductOrService", KGNode.BRIDGE_TYPE_KEY);
		dao.insertBridge(KGNode.getDBpediaClassURI("DBpedia:Name"),"No gr class, but may be relevant for e-business", KGNode.BRIDGE_TYPE_KEY);
		dao.insertBridge(KGNode.getDBpediaClassURI("DBpedia:NaturalPlace"),"gr:Location", KGNode.BRIDGE_TYPE_KEY);
		dao.insertBridge(KGNode.getDBpediaClassURI("DBpedia:Newspaper"),"gr:ProductOrService", KGNode.BRIDGE_TYPE_KEY);
		dao.insertBridge(KGNode.getDBpediaClassURI("DBpedia:Non-ProfitOrganisation"),"gr:BusinessEntity", KGNode.BRIDGE_TYPE_KEY);
		dao.insertBridge(KGNode.getDBpediaClassURI("DBpedia:OfficeHolder"),"gr:BusinessEntity", KGNode.BRIDGE_TYPE_KEY);
		dao.insertBridge(KGNode.getDBpediaClassURI("DBpedia:Organisation"),"gr:BusinessEntity", KGNode.BRIDGE_TYPE_KEY);
		dao.insertBridge(KGNode.getDBpediaClassURI("DBpedia:PeriodicalLiterature"),"gr:ProductOrService", KGNode.BRIDGE_TYPE_KEY);
		dao.insertBridge(KGNode.getDBpediaClassURI("DBpedia:Person"),"No gr class, but may be relevant for e-business", KGNode.BRIDGE_TYPE_KEY);
		dao.insertBridge(KGNode.getDBpediaClassURI("DBpedia:Place"),"gr:Location", KGNode.BRIDGE_TYPE_KEY);
		dao.insertBridge(KGNode.getDBpediaClassURI("DBpedia:Plant"),"no gr class", KGNode.BRIDGE_TYPE_KEY);
		dao.insertBridge(KGNode.getDBpediaClassURI("DBpedia:PoliticalParty"),"no gr class", KGNode.BRIDGE_TYPE_KEY);
		dao.insertBridge(KGNode.getDBpediaClassURI("DBpedia:Politician"),"no gr class", KGNode.BRIDGE_TYPE_KEY);
		dao.insertBridge(KGNode.getDBpediaClassURI("DBpedia:PopulatedPlace"),"gr:Location", KGNode.BRIDGE_TYPE_KEY);
		dao.insertBridge(KGNode.getDBpediaClassURI("DBpedia:RaceHorse"),"gr:ProductOrService", KGNode.BRIDGE_TYPE_KEY);
		dao.insertBridge(KGNode.getDBpediaClassURI("DBpedia:RadioStation"),"gr:BusinessEntity", KGNode.BRIDGE_TYPE_KEY);
		dao.insertBridge(KGNode.getDBpediaClassURI("DBpedia:RecordLabel"),"gr:BusinessEntity", KGNode.BRIDGE_TYPE_KEY);
		dao.insertBridge(KGNode.getDBpediaClassURI("DBpedia:Region"),"gr:Location", KGNode.BRIDGE_TYPE_KEY);
		dao.insertBridge(KGNode.getDBpediaClassURI("DBpedia:Reptile"),"no gr class", KGNode.BRIDGE_TYPE_KEY);
		dao.insertBridge(KGNode.getDBpediaClassURI("DBpedia:River"),"gr:Location", KGNode.BRIDGE_TYPE_KEY);
		dao.insertBridge(KGNode.getDBpediaClassURI("DBpedia:Road"),"gr:Location", KGNode.BRIDGE_TYPE_KEY);
		dao.insertBridge(KGNode.getDBpediaClassURI("DBpedia:Rocket"),"gr:ProductOrService", KGNode.BRIDGE_TYPE_KEY);
		dao.insertBridge(KGNode.getDBpediaClassURI("DBpedia:RouteOfTransportation"),"no gr class", KGNode.BRIDGE_TYPE_KEY);
		dao.insertBridge(KGNode.getDBpediaClassURI("DBpedia:Royalty"),"no gr class", KGNode.BRIDGE_TYPE_KEY);
		dao.insertBridge(KGNode.getDBpediaClassURI("DBpedia:Settlement"),"gr:Location", KGNode.BRIDGE_TYPE_KEY);
		dao.insertBridge(KGNode.getDBpediaClassURI("DBpedia:Ship"),"gr:ProductOrService", KGNode.BRIDGE_TYPE_KEY);
		dao.insertBridge(KGNode.getDBpediaClassURI("DBpedia:Single"),"gr:ProductOrService", KGNode.BRIDGE_TYPE_KEY);
		dao.insertBridge(KGNode.getDBpediaClassURI("DBpedia:SoapCharacter"),"no gr class", KGNode.BRIDGE_TYPE_KEY);
		dao.insertBridge(KGNode.getDBpediaClassURI("DBpedia:SoccerClub"),"gr:BusinessEntity", KGNode.BRIDGE_TYPE_KEY);
		dao.insertBridge(KGNode.getDBpediaClassURI("DBpedia:SoccerPlayer"),"no gr class", KGNode.BRIDGE_TYPE_KEY);
		dao.insertBridge(KGNode.getDBpediaClassURI("DBpedia:Software"),"gr:ProductOrService", KGNode.BRIDGE_TYPE_KEY);
		dao.insertBridge(KGNode.getDBpediaClassURI("DBpedia:Species"),"no gr class", KGNode.BRIDGE_TYPE_KEY);
		dao.insertBridge(KGNode.getDBpediaClassURI("DBpedia:SportsLeague"),"gr:Brand", KGNode.BRIDGE_TYPE_KEY);
		dao.insertBridge(KGNode.getDBpediaClassURI("DBpedia:SportsTeam"),"gr:BusinessEntity", KGNode.BRIDGE_TYPE_KEY);
		dao.insertBridge(KGNode.getDBpediaClassURI("DBpedia:Stream"),"gr:Location", KGNode.BRIDGE_TYPE_KEY);
		dao.insertBridge(KGNode.getDBpediaClassURI("DBpedia:TelevisionShow"),"gr:ProductOrService", KGNode.BRIDGE_TYPE_KEY);
		dao.insertBridge(KGNode.getDBpediaClassURI("DBpedia:TelevisionStation"),"gr:BusinessEntity", KGNode.BRIDGE_TYPE_KEY);
		dao.insertBridge(KGNode.getDBpediaClassURI("DBpedia:TimePeriod"),"No gr class, but may be relevant for e-business", KGNode.BRIDGE_TYPE_KEY);
		dao.insertBridge(KGNode.getDBpediaClassURI("DBpedia:TopicalConcept"),"no gr class", KGNode.BRIDGE_TYPE_KEY);
		dao.insertBridge(KGNode.getDBpediaClassURI("DBpedia:Town"),"gr:Location", KGNode.BRIDGE_TYPE_KEY);
		dao.insertBridge(KGNode.getDBpediaClassURI("DBpedia:University"),"gr:BusinessEntity", KGNode.BRIDGE_TYPE_KEY);
		dao.insertBridge(KGNode.getDBpediaClassURI("DBpedia:VideoGame"),"gr:ProductOrService", KGNode.BRIDGE_TYPE_KEY);
		dao.insertBridge(KGNode.getDBpediaClassURI("DBpedia:Village"),"gr:Location", KGNode.BRIDGE_TYPE_KEY);
		dao.insertBridge(KGNode.getDBpediaClassURI("DBpedia:Weapon"),"gr:ProductOrService", KGNode.BRIDGE_TYPE_KEY);
		dao.insertBridge(KGNode.getDBpediaClassURI("DBpedia:Website"),"No gr class, but may be relevant for e-business", KGNode.BRIDGE_TYPE_KEY);
		dao.insertBridge(KGNode.getDBpediaClassURI("DBpedia:Work"),"gr:Product", KGNode.BRIDGE_TYPE_KEY);
		dao.insertBridge(KGNode.getDBpediaClassURI("DBpedia:Wrestler"),"gr:BusinessEntity", KGNode.BRIDGE_TYPE_KEY);
		dao.insertBridge(KGNode.getDBpediaClassURI("DBpedia:Writer"),"gr:BusinessEntity", KGNode.BRIDGE_TYPE_KEY);
		dao.insertBridge(KGNode.getDBpediaClassURI("DBpedia:WrittenWork"),"gr:ProductOrService", KGNode.BRIDGE_TYPE_KEY);
		dao.insertBridge(KGNode.getDBpediaClassURI("DBpedia:Year"),"No gr class, but may be relevant for e-business", KGNode.BRIDGE_TYPE_KEY);
	}

	public static boolean containsLabel(final List<KGNode> list, final String label){
		return list.stream().filter(o -> o.getLabel().equals(label)).findFirst().isPresent();
	}

	private static void test() {

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