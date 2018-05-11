package app;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import pojo.KGNode;
import service.NerdExecutor;

public class App {
    private static final Logger LOG = LoggerFactory.getLogger(App.class);

    public static void main(String[] args) {
    	
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
        
        
        // TODO rever a contagem de hits
        //STEP 1: SEMANTIC ENRICHMENT AND ACCOUNTING
        NerdExecutor nerdExecutor = new NerdExecutor(inputFile);
        nerdExecutor.execute(confidence, language);
        
        
    	/*
        //STEP 2: SEMANTIC DATA CUBE CONSTRUCTION
        ArrayList<String> properties = new ArrayList<>();
        
        //TODO teste apenas com subclassof
        //properties.add("rdf:type");
        properties.add("rdfs:subClassOf");
        
		HashMap<String, String> prefixes = new HashMap<>();
		prefixes.put("rdfs", "http://www.w3.org/2000/01/rdf-schema#");
		prefixes.put("rdf", "http://www.w3.org/1999/02/22-rdf-syntax-ns#");
		
		BridgeExecutor bridgeService = new BridgeExecutor(prefixes, properties, inputFile);
		bridgeService.execute();
        */
		/*
		//TODO create the final step
		//SemanticDataCubeExecutor semanticDataCubeExecutor = new SemanticDataCubeExecutor(hierarchyFile);
		//semanticDataCubeExecutor.execute(); //build the DW
		 * 
		 */
    }
    
	public static boolean containsLabel(final List<KGNode> list, final String label){
	    return list.stream().filter(o -> o.getLabel().equals(label)).findFirst().isPresent();
	}
	
	private static void test() {
		//TODO TESTE
        HashMap<KGNode, String> bridges = new HashMap<KGNode, String>();
        KGNode n1 = new KGNode("http://dbpedia.org/ontology/Agent");
        bridges.put(n1, "gr:BusinessEntity");
        
        KGNode n2 = new KGNode("http://dbpedia.org/ontology/Agent");
        
        List<KGNode> nodes = new ArrayList<>(bridges.keySet());
        String res = bridges.get(n2);
        System.out.println("Class GRO: " + res);

	}
}