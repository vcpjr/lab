package app;

import static java.lang.System.exit;

import java.io.File;
import java.sql.SQLException;
import java.util.ArrayList;

import org.apache.jena.query.Query;
import org.apache.jena.query.QueryExecution;
import org.apache.jena.query.QueryExecutionFactory;
import org.apache.jena.query.QueryFactory;
import org.apache.jena.query.ResultSet;
import org.apache.jena.rdf.model.RDFNode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import service.BridgeExecutor;
import service.NerdExecutor;

public class App {
    private static final Logger LOG = LoggerFactory.getLogger(App.class);

    public static void main(String[] args) {

        /*if (args.length < 3) {
            System.err.println("Parameters not found. For run this application use follow command.");
            System.out.println("./run.sh <dataset> <confidence_level> <language>\n");
            exit(1);
        }

        LOG.info("Starting Application.");
        String filePath = args[0];
        float confidence = Float.parseFloat(args[1]);
        String language = args[2];
        File inputFile = new File(filePath);

        if (!inputFile.exists()) {
            LOG.error("File not found!");
            System.err.println("File not found!");
            exit(1);
        }

        //STEP 1: SEMANTIC ENRICHMENT AND ACCOUNTING
        NerdExecutor nerdExecutor = new NerdExecutor(inputFile);
        nerdExecutor.execute(confidence, language);
        
        //STEP 2: SEMANTIC DATA CUBE CONSTRUCTION
        //TODO
        RDFNode nodeFromKG = null;
        ArrayList<String> relationships = null;
		BridgeExecutor bridgeService = new BridgeExecutor(relationships );
		bridgeService.execute(nodeFromKG);
		
		//TODO create the final step
		//SemanticDataCubeExecutor semanticDataCubeExecutor = new SemanticDataCubeExecutor(hierarchyFile);
		//semanticDataCubeExecutor.execute(); //build the DW
		 * 
		 */
    	
    	String queryString=
    			"PREFIX p: <http://dbpedia.org/property/>"+
    			"PREFIX dbpedia: <http://dbpedia.org/resource/>"+
    			"PREFIX category: <http://dbpedia.org/resource/Category:>"+
    			"PREFIX rdfs: <http://www.w3.org/2000/01/rdf-schema#>"+
    			"PREFIX skos: <http://www.w3.org/2004/02/skos/core#>"+
    			"PREFIX geo: <http://www.georss.org/georss/>"+

    			"SELECT DISTINCT ?m ?n ?p ?d"+
    			"WHERE {"+
    			" ?m rdfs:label ?n."+
    			" ?m skos:subject ?c."+
    			" ?c skos:broader category:Churches_in_Paris."+
    			" ?m p:abstract ?d."+
    			" ?m geo:point ?p"+
    			" FILTER ( lang(?n) = 'fr' )"+
    			" FILTER ( lang(?d) = 'fr' )"+
    			" }";
    	
    			queryString = "select distinct ?Concept where {[] a ?Concept} LIMIT 20";

    			// now creating query object
    			Query query = QueryFactory.create(queryString);
    			// initializing queryExecution factory with remote service.
    			// **this actually was the main problem I couldn't figure out.**
    			QueryExecution qexec = QueryExecutionFactory.sparqlService("http://dbpedia.org/sparql", query);

    			//after it goes standard query execution and result processing which can
    			// be found in almost any Jena/SPARQL tutorial.
    			try {
    			    ResultSet results = qexec.execSelect();
    			    
			    	System.out.println(results.toString());
    			    
    			    while(results.hasNext()) {
    			    	System.out.println(results.next().toString());
    			    }
    			} 
    			finally {
    			   qexec.close();
    			}
        
    }
}
