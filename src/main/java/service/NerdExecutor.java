package service;

import java.io.File;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

import org.apache.jena.query.Query;
import org.apache.jena.query.QueryExecution;
import org.apache.jena.query.QueryExecutionFactory;
import org.apache.jena.query.QueryFactory;
import org.apache.jena.query.ResultSet;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.gson.JsonSyntaxException;

import dao.KGNodeDAO;
import pojo.KGNode;
import pojo.Tweet;
import pojo.dbpediaspotlight.Annotation;
import util.CSVReport;
import util.TweetFileReader;

public class NerdExecutor {

    private static final String APP_ROOT = System.getProperty("user.dir");
    private static final Logger LOG = LoggerFactory.getLogger(NerdExecutor.class);
    private static final File outputPath = new File(APP_ROOT,"output");

    private final List<Tweet> tweets;
    private final SpotlightRest rest;
    private final String inputFilename;

    public NerdExecutor(File datasetFile) {
        rest = new SpotlightRest();
        tweets = TweetFileReader.readTweetsFromFile(datasetFile);
        String filePath = datasetFile.getName();
        inputFilename = filePath.split("\\.")[0];
    }
    
    /**
     * Importa as planilhas geradas pelo NerdExecutor nos primeiros experimentos (05/2017)
     */
    public void importBR2015Dataset(){
    	
    	
    }

    public void execute(float confidence, String language) {
    	//TODO alterar para incluir: 
    	//Alterar para gerar uma planilha só
    	//tweet id (id), user id, text, pre-processed text, confidence, mention, URI, Chosen class, Request time

        //TODO confirmar a contagem de HITS (criar KGNode? Salvar no banco?)
        //Diretos: menções a instâncias
        //Indiretos por Type: uma classe diretamente relacionada à uma instância
        //Indiretos por Subclass-Of: uma classe é relacionada através de uma relação de subsumption até uma classe type de uma instância
        
        /*
         * Exemplo: 
         * 
         * Resource(directHits, indirectHitsType, indirectHitsSubclassOf)
         * 			
         *    	 Thing(0,0,2)
         *	       |
         *	     Person(0,0,2)
         *	       |
         *	     Singer(0,2,0)
         *    	/             \
         * Selena(1,0,0)	 Anitta(1,0,0)			   
         * 
         * */
        
    	KGNodeDAO dao = new KGNodeDAO();
    	ArrayList<Integer> annotatedInstanceIds = new ArrayList<>();
        for(int i = 0; i < tweets.size(); ++i) {
            try {
            	Tweet t = tweets.get(i);
                final Annotation annotated = rest.getAnnotation(t.getText(), confidence, language);
                annotated.getResources().forEach(resource -> {
	            	String uri = resource.getURI();
	            	KGNode nodeInstance = getKGNode(uri, KGNode.RELATIONSHIP_INSTANCE, 1);
	            	
	            	if(nodeInstance != null){
	            		int directHitsOnInstance = nodeInstance.getDirectHits();
	            		
	            		int nodeInstanceId = dao.insert(nodeInstance);
	            		if(!annotatedInstanceIds.contains(nodeInstanceId)){
	            			annotatedInstanceIds.add(nodeInstanceId);
	            		}
	            		
	            		resource.getTypes().forEach(classType-> {
	            			String dbpediaTypeURI = KGNode.getDBpediaClassURI(classType);
	            			//System.out.println("Type: " + dbpediaTypeURI);
	            			
	            			if(dbpediaTypeURI != null){
	            				KGNode nodeClassType = getKGNode(dbpediaTypeURI, KGNode.RELATIONSHIP_TYPE_OF, directHitsOnInstance);
	            				int classTypeId = dao.insertType(nodeInstanceId, nodeClassType);
	            				//System.out.println("INSERT (Type, Instance): (" + nodeClassType.getUri() +"," + nodeInstance.getUri() + ")" );
	            				
	            				ArrayList<KGNode> subclasses = getSubclassesOf(nodeClassType);
	            				subclasses.forEach(subclass ->{
	            					//System.out.println("Subclass: " + subclass.getUri());
	            					KGNode nodeClassSubclassOf = getKGNode(subclass.getUri(), KGNode.RELATIONSHIP_SUBCLASS_OF, directHitsOnInstance);
	            					dao.insertSubclass(classTypeId, nodeClassSubclassOf);
	            				});
	            			}
	            		});
	            	}
                });
            } catch (UnexpectedStatusCodeException e) {
                LOG.error(String.format(Locale.US,
                    "Unexpected status code error: tweet=%s, confidence=%.2f, language=%s",
                    tweets.get(i).getText(), confidence, language),
                    e);
            } catch (JsonSyntaxException e) {
                LOG.error("Json Syntax error to annotate tweet message: '" + tweets.get(i).getText() + "'", e);
            } catch (Exception e) {
                LOG.error("Unknown exception", e);
            }
        }

        generateReportCSV(annotatedInstanceIds, language, confidence);
    }

	private ArrayList<KGNode> getSubclassesOf(KGNode nodeClassType) {
    	ArrayList<KGNode> subclasses = new ArrayList<>();
    		
    	String querySPARQL =  getQueryPrefix() + 
    			" select ?subclass " + 
    			" where {<" + nodeClassType.getUri() + "> rdfs:subClassOf ?subclass}";

    	Query query = QueryFactory.create(querySPARQL);
    	QueryExecution qexec = QueryExecutionFactory.sparqlService("http://dbpedia.org/sparql", query);
    	String msg = "------ Subclasses of " + nodeClassType.toString() + "\n";
    	try {
    		ResultSet results = qexec.execSelect();
    		
    		ArrayList<String> uris = new ArrayList<>();
    		while(results.hasNext()) {
    			boolean addSubclass = false;
    			String adjacentURI = results.next().toString();
    			
    			if(!uris.contains(adjacentURI)){
    				uris.add(adjacentURI);
    				
    				if(!adjacentURI.contains(KGNode.URL_ROOT)){
    					if(adjacentURI.contains("dbpedia")){
    						String[] res = adjacentURI.split("subclass = <");
    						res = res[1].split(">");
    						adjacentURI = res[0];
    						addSubclass = true;
    					}
    				}else{
    					adjacentURI = KGNode.URL_ROOT;
    					addSubclass = true;
    				}
    				//Pode ser que a classe já exista
    				KGNode subclass = getKGNode(adjacentURI, KGNode.RELATIONSHIP_SUBCLASS_OF, nodeClassType.getIndirectHitsType());
    				if(subclass != null &&!containsLabel(subclasses, subclass.getLabel()) && addSubclass){
    					subclasses.add(subclass);
    					msg += "- " + subclass.getLabel() + "\n"; 
    				}
    			}
    		}
    	}finally {
    		qexec.close();
    	}
    	msg += "-------------------------------------";
    	System.out.println(msg);
    	return subclasses;
    }

	private String getQueryPrefix() {
    	HashMap<String, String> prefixes = new HashMap<>();
    	prefixes.put("rdfs", "http://www.w3.org/2000/01/rdf-schema#");
    	prefixes.put("rdf", "http://www.w3.org/1999/02/22-rdf-syntax-ns#");

    	String queryPrefix = "";
    	for(String prefix: prefixes.keySet()){
    		queryPrefix += "PREFIX " + prefix + ": <" + prefixes.get(prefix) +  "> \n";
    	
    	}
    	
		return queryPrefix;
	}

	private void generateReportCSV(ArrayList<Integer> instanceIds, String language, float confidence) {
    	CSVReport nerdReport = new CSVReport("Resource; #Direct Hits; #Indirect Hits (Type); #Indirect Hits (Subclass)");
    	KGNodeDAO dao = new KGNodeDAO();
    	
    	LOG.info("******************NerdExecutor CSV generation*****************");

    	ArrayList<KGNode> resources = new ArrayList<>();
    	for(Integer id: instanceIds){
    		KGNode n = dao.getById(id);
    		System.out.println("* Instance");
    		appendNodeOnReport(n, resources, nerdReport);
    		ArrayList<KGNode> types = dao.getTypesByInstanceId(n.getId());
    		for(KGNode type: types){
    			System.out.println("** Type");
    			appendNodeOnReport(type, resources, nerdReport);
        		ArrayList<KGNode> subclasses = dao.getSuperclassesPath(type.getId(), null);
        		for(KGNode subclass: subclasses){
        			System.out.println("*** Subclass");
        			appendNodeOnReport(subclass, resources, nerdReport);
        		}
    		}
    	}
    	String postfixFilename = String.format(
    			Locale.US,
    			"%s-%s-%s-%.2f.csv",
    			inputFilename.replaceAll("\\s", ""),
    			LocalDate.now().toString(),
    			language,
    			confidence);
    	
    	nerdReport.generate(new File(outputPath, "nerdExecution-" + postfixFilename));

    	LOG.info("***************************NerdExecutor CSV end**************************");
    }

	private void appendNodeOnReport(KGNode n, ArrayList<KGNode> resources, CSVReport nerdReport) {
		if(!containsLabel(resources, n.getLabel())){
			resources.add(n);
			String nodeText = String.format(Locale.US, "%s;%d;%d;%d", n.getLabel(), n.getDirectHits(), n.getIndirectHitsType(), n.getIndirectHitsSubclassOf());
			nerdReport.append(nodeText);
			System.out.println(nodeText);
		}
	}

	private KGNode getKGNode(String uri, String relationship, int hitsToSum) {
		KGNodeDAO dao = new KGNodeDAO();
		KGNode resource = null;
		
		if(uri.contains("dbpedia") || uri.contains(KGNode.URL_ROOT)){
			resource = dao.getByURI(uri);
			
			if(resource == null){
				resource = new KGNode(uri);
			}
			
			switch (relationship) {
			case KGNode.RELATIONSHIP_INSTANCE:
				resource.setDirectHits(resource.getDirectHits() + hitsToSum);
				break;
			case KGNode.RELATIONSHIP_TYPE_OF:
				resource.setIndirectHitsType(resource.getIndirectHitsType() + hitsToSum);
				break;
			case KGNode.RELATIONSHIP_SUBCLASS_OF:
				resource.setIndirectHitsSubclassOf(resource.getIndirectHitsSubclassOf() + hitsToSum);
				break;	
			}
			
			dao.insert(resource);
			System.out.println("obtain/update node: " + resource.toString() + "---" + relationship);
		}
		return resource;
	}

	public static boolean containsLabel(final List<KGNode> list, final String label){
	    return list.stream().filter(o -> o.getLabel().equals(label)).findFirst().isPresent();
	}
	
	public static KGNode findByURI(List<KGNode> nodes, String uri) {
	    return nodes.stream().filter(node -> uri.equals(node.getUri())).findFirst().orElse(null);
	}
}