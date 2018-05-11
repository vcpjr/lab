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

import pojo.KGNode;
import pojo.Tweet;
import pojo.dbpediaspotlight.Annotation;
import util.CSVReport;
import util.TweetFileReader;

public class NerdExecutor {

	private static final String RELATIONSHIP_INSTANCE = "instance";
	private static final String RELATIONSHIP_TYPE_OF = "type_of";
	private static final String RELATIONSHIP_SUBCLASS_OF = "subclass_of";
	
	
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
        
    	ArrayList<KGNode> resources = new ArrayList<>(); //instances or classes
        for(int i = 0; i < tweets.size(); ++i) {
            try {
            	Tweet t = tweets.get(i);
                final Annotation annotated = rest.getAnnotation(t.getText(), confidence, language);
                annotated.getResources().forEach(resource -> {
	            	String uri = resource.getURI();
	            	KGNode nodeInstance = getKGNode(resources, uri, RELATIONSHIP_INSTANCE, 1);
	            	addResource(resources, nodeInstance);
                    	
                    resource.getTypes().forEach(classType-> {
                    	String dbpediaURI = getDBpediaClassURI(classType);
                    		
                		if(dbpediaURI != null){
                			KGNode nodeClassType = getKGNode(resources, getDBpediaClassURI(classType), RELATIONSHIP_TYPE_OF, 1);
                			addResource(resources, nodeClassType);
                			
                			ArrayList<KGNode> subclasses = getSubclassesOf(nodeClassType);
                			subclasses.forEach(subclass ->{
                				KGNode nodeClassSubclassOf = getKGNode(resources, subclass.getUri(), RELATIONSHIP_SUBCLASS_OF, 1);
                				addResource(resources, nodeClassSubclassOf);
                			});
                		}
                	});
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

        generateReportCSV(resources, language, confidence);
    }

    private ArrayList<KGNode> getSubclassesOf(KGNode nodeClassType) {
    	ArrayList<KGNode> subclasses = new ArrayList<>();
    		
    	String querySPARQL =  getQueryPrefix() + 
    			" select ?filho " + 
    			" where {?filho rdfs:subClassOf <" + nodeClassType.getUri() + ">}";

    	Query query = QueryFactory.create(querySPARQL);
    	QueryExecution qexec = QueryExecutionFactory.sparqlService("http://dbpedia.org/sparql", query);
    	try {
    		ResultSet results = qexec.execSelect();

    		while(results.hasNext()) {
    			String adjacentURI = results.next().toString();
    			String[] res = adjacentURI.split("filho = <");
    			res = res[1].split(">");
    			adjacentURI = res[0];

    			KGNode subclass = new KGNode(adjacentURI);
    			subclasses.add(subclass);
    		}
    	}finally {
    		qexec.close();
    	}
    	return subclasses;
    }

	private String getDBpediaClassURI(String label) {
		String uri = null;
		String[] parts = label.split("DBpedia:");
		
		if(parts != null && parts.length == 2){
			uri = "http://dbpedia.org/ontology/" + parts[1];
		}
		return uri;
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

	private void addResource(ArrayList<KGNode> resources, KGNode resource) {
    	if(!resources.contains(resource) || !containsLabel(resources, resource.getLabel())){
			resources.add(resource);
		}
	}

	private void generateReportCSV(ArrayList<KGNode> resources, String language, float confidence) {
    	CSVReport nerdReport = new CSVReport("Resource; #Direct Hits; #Indirect Hits (Type); #Indirect Hits (Subclass)");

    	LOG.info("******************NerdExecutor CSV generation*****************");
    	String nodeText;

    	for(KGNode n: resources){
    		nodeText = String.format(Locale.US, "%s;%d;%d;%d", n.getLabel(), n.getDirectHits(), n.getIndirectHitsType(), n.getIndirectHitsSubclassOf());
    		nerdReport.append(nodeText);
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

	private KGNode getKGNode(ArrayList<KGNode> resources, String uri, String relationship, int hits) {
		
		KGNode resource;
		if(containsLabel(resources, uri)){
			resource = findByURI(resources, uri);
		}else{
			resource = new KGNode(uri);
		}
		
		switch (relationship) {
		case RELATIONSHIP_INSTANCE:
			resource.setDirectHits(resource.getDirectHits() + hits);
			break;
		case RELATIONSHIP_TYPE_OF:
			resource.setIndirectHitsType(resource.getIndirectHitsType() + hits);
			break;
		case RELATIONSHIP_SUBCLASS_OF:
			resource.setIndirectHitsSubclassOf(resource.getIndirectHitsSubclassOf() + hits);
			break;	
		}
		
		System.out.println("addResource: " + resource.toString());
		return resource;
	}

	public static boolean containsLabel(final List<KGNode> list, final String label){
	    return list.stream().filter(o -> o.getLabel().equals(label)).findFirst().isPresent();
	}
	
	public static KGNode findByURI(List<KGNode> nodes, String uri) {
	    return nodes.stream().filter(node -> uri.equals(node.getUri())).findFirst().orElse(null);
	}
}