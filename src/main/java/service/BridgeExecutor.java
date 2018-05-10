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

import pojo.KGNode;
import util.BridgesFileReader;
import util.CSVReport;

public class BridgeExecutor {
	
	/*
	 * TODO 08/05/2018
	 * - Rever step 1 (contagem)
	 * - Definições de hits (Dawak)
	 * 		- Diretos
	 * 		- Indiretos por type
	 * 		- Indiretos por subclass_of 	 
	 * - Ordenar a planilha por hierarquias ou hits acumulados
	 * - Dúvidas nas pontes: usar brand
	 * - Filtragem por hits acumulados
	 * - Diminuir pseudocódigo
	 */

	private static final String APP_ROOT = System.getProperty("user.dir");
	private static final Logger LOG = LoggerFactory.getLogger(BridgeExecutor.class);
	private static final File outputPath = new File(APP_ROOT,"output");

	// K: Recurso ou classe da DBpedia
	// V: classe da ontologia de alto nível
	private final HashMap<KGNode, String> keyBridges;
	private final HashMap<KGNode, String> newBridges;
	private final HashMap<KGNode, String> inconsistentBridges;

	private final HashMap<String, String> prefixes; //K: prefix (rdf); V: uri (http://www.w3.org/2000/01/rdf-schema#)
	private final ArrayList<String> properties; //rdf:type or rdfs:subClassOf
	private final ArrayList<KGNode> visitedNodes;

	private KGNode root;

	public BridgeExecutor(HashMap<String, String> prefixes, ArrayList<String> properties, File datasetFile){
		keyBridges = BridgesFileReader.readKeyBridgesFromFile(datasetFile);
		newBridges = new HashMap<>();
		inconsistentBridges = new HashMap<>();
		this.properties = properties;
		this.prefixes = prefixes;
		this.visitedNodes = new ArrayList<>();
	}

	public void execute(){
		//recursive procedure, executes until the final of the nodeFromKG children hierarchy
		//TODO teste com a raiz variável, de acordo com os termos encontrados em cada tweet
		// Validar

		//this.checkCompleteNonRecursive(root, null);

		for(KGNode node: keyBridges.keySet()){
			this.root = node;
			this.checkComplete(this.root, keyBridges.get(node));
		}

		LOG.info("********************************************************************");
		LOG.info("******************BridgeExecutor execution complete*****************");
		LOG.info("************ Key Bridges: " + this.keyBridges.size());
		LOG.info("************ New Bridges: " + this.newBridges.size());
		LOG.info("************ Inconsistent Bridges: " + this.inconsistentBridges.size());


		generateReportCSV();
	}

	private void generateReportCSV() {
		CSVReport bridgeReport = new CSVReport("DBpediaClass; #Direct Hits; #Indirect Hits (Type); #Indirect Hits (Subclass); GoodRelations (gr:) class; Type");

		LOG.info("******************BridgeExecutor keyBridges CSV generation*****************");
		String nodeText;
		
		HashMap<KGNode, String> allBridges = new HashMap<>();
		
		//TODO testar
		for(KGNode n: keyBridges.keySet()){
			LOG.info("add key bridge: " + n.toString());
			n.setBridgeType("Key Bridge");
			allBridges.put(n, keyBridges.get(n));
		}
		
		for(KGNode n: newBridges.keySet()){
			//LOG.info("add new bridge: " + n.toString());
			n.setBridgeType("New Bridge");
			allBridges.put(n, keyBridges.get(n));
		}
		
		LOG.info("******************BridgeExecutor newBridges CSV generation*****************");

		for(KGNode n: allBridges.keySet()){
			nodeText = String.format(Locale.US, "%s;%d;%d;%d;%s;%s", n.getLabel(), n.getDirectHits(), n.getIndirectHitsType(), n.getIndirectHitsSubclassOf(), newBridges.get(n), n.getBridgeType());
			bridgeReport.append(nodeText);
			/*
			Set<KGNode> nodesRelatedToN = n.getRelationships().keySet();
			
			for(KGNode relatedNode: nodesRelatedToN){
				nodeText = String.format(Locale.US, "%s;%s;%s;%d", n.getLabel(), n.getUri(), n.getRelationships().get(relatedNode), n.getRelationships().size());
				bridgeReport.append(nodeText);
			}*/
		}
		String postfixFilename = String.format(Locale.US,"%s.csv",LocalDate.now().toString());
		bridgeReport.generate(new File(outputPath, "bridgesCheckedCompleted-" + postfixFilename));

		LOG.info("***************************BridgeExecutor CSV end**************************");

		
	}

	/**
	 * 
	 * @param node node from hierarchy on Knowledge Graph
	 * @param dc domain class bridged to b
	 * 
	 * @returns void, but the algorithm populates newBridges and inconsistentBridges maps
	 */
	private void checkComplete(KGNode node, String domainClass){

		LOG.info("** CheckComplete for node: " + node.getLabel());

		ArrayList<KGNode> children = getChildrenFromNodeOnKG(node);

		List<KGNode> nodesFromKeyBridges = new ArrayList<KGNode>(keyBridges.keySet());
		
		if(children.isEmpty()) {
			//no more classes related ("leaf")
			LOG.info("** Bridge : "+ node.getLabel() + " -> "+ domainClass + " ADDED");
			newBridges.put(node, domainClass);
		}else {
			for(int i = 0; i< children.size(); i++){
				KGNode childNode = children.get(i);
				String childDomainClass = keyBridges.get(childNode);
				
				LOG.info("** Domain class: " + domainClass);
				LOG.info("** Child domain class: " + childDomainClass);
				
				if(childDomainClass == null){
					LOG.info("** Domain class null for node: " + node.getLabel());
					checkComplete(childNode, domainClass);
				}else{
					LOG.info("** Domain class: "+ domainClass + ". Node: " + node.getLabel());
					if(containsLabel(nodesFromKeyBridges, childNode.getLabel())){
						LOG.info("** Domain class: "+ domainClass + "exists on keyBridges");
						if(keyBridges.get(childNode) != null 
								&& keyBridges.get(childNode).equals(domainClass)){
							LOG.info("** Bridge : "+ node.getLabel() + " -> "+ domainClass + " ADDED");
							newBridges.put(childNode, domainClass);
						}else{
							LOG.info("** Bridge : "+ node.getLabel() + " -> "+ domainClass + " INCONSISTENT");
							inconsistentBridges.put(childNode, domainClass);
						}
					}else {
						newBridges.put(childNode, domainClass);
					}
				}
			}
		}
	}

	private ArrayList<KGNode> getChildrenFromNodeOnKG(KGNode node) {

		ArrayList<KGNode> children = new ArrayList<>();

		//LOG.info("** Looking for child for node: " + node.getLabel());
		String queryPrefix = "";
		for(String prefix: prefixes.keySet()){
			//PREFIX rdfs: <http://www.w3.org/2000/01/rdf-schema#>
			queryPrefix += "PREFIX " + prefix + ": <" + prefixes.get(prefix) +  "> \n";
		}

		for(String property: properties){
			//consulta que retorna o label dos filhos de um determinado nodo pai, dada uma relação
			String querySPARQL =  queryPrefix + 
					" select ?filho " + 
					" where {?filho " + property + " <" + node.getUri() + ">}";

			//TODO fazer consulta local (no TDB)
			Query query = QueryFactory.create(querySPARQL);
			QueryExecution qexec = QueryExecutionFactory.sparqlService("http://dbpedia.org/sparql", query);

			try {
				ResultSet results = qexec.execSelect();
				//System.out.println(results.toString());

				while(results.hasNext()) {
					String adjacentURI = results.next().toString();
					String[] res = adjacentURI.split("filho = <");
					res = res[1].split(">");

					adjacentURI = res[0];

					String adjacentLabel = "";
					adjacentLabel  = adjacentURI;

					KGNode adjacent = new KGNode(adjacentURI);
					adjacent.addRelationship(property, node);

					if(!containsLabel(visitedNodes, adjacentLabel)) {
						if(containsLabel(children, adjacentLabel)){
							//TODO verificar a contagem de hits
							//							LOG.info("** CONTAINS LABEL: " + adjacent.getLabel());
							//adjacent.setIndirectHits(adjacent.getIndirectHits() + node.getDirectHits() + node.getIndirectHits() + 1);
						}else{
							//adjacent.setIndirectHits(adjacent.getIndirectHits() + 1);
							children.add(adjacent);
							//							LOG.info("** Add child: " + adjacent.getLabel());
						}
						visitedNodes.add(adjacent);
					}

				}
			} 
			finally {
				qexec.close();
			}

		}

		return children;
	}

	public boolean containsLabel(final List<KGNode> list, final String label){
		return list.stream().filter(o -> o.getLabel().equals(label)).findFirst().isPresent();
	}
	//
	//	/**
	//	 * 
	//	 * @param node node from hierarchy on Knowledge Graph
	//	 * @param dc domain class bridged to b
	//	 * 
	//	 * @returns void, but the algorithm populates newBridges and inconsistentBridges maps
	//	 */
	//	private void checkCompleteNonRecursive(KGNode node, String domainClass){
	//		
	//		LOG.info("CheckComplete non recursive");
	//		populateChildrenFromNodeOnKG(node);
	//		
	//		for(int i = 0; i< childrenNonRecursive.size(); i++){
	//			KGNode childNode = childrenNonRecursive.get(i);
	//			domainClass = keyBridges.get(childNode);
	//			
	//			if(keyBridges.containsKey(childNode)){
	//				if(keyBridges.get(childNode) != null 
	//						&& keyBridges.get(childNode).equals(domainClass)){
	//					newBridges.put(childNode, domainClass);
	//				}else{
	//					inconsistentBridges.put(childNode, domainClass);
	//				}
	//			}else {//Ainda não tem ponte
	//				newBridges.put(childNode, domainClass);
	//			}
	//		}
	//		LOG.info("End CheckComplete non recursive");
	//	}
	//
	//	/**
	//	 * 
	//	 * @param nodeFromKG the parent node on the knowledge graph (e.g., DBpedia)
	//	 *  
	//	 * @return a list from the direct children from nodeFromKG 
	//	 */
	//	private void populateChildrenFromNodeOnKG(KGNode nodeFromKG) {
	//		
	//		LOG.info("****************** Populate nodes from KG ********************");
	//		
	//    	if(!childrenNonRecursive.contains(nodeFromKG)){
	//    		LOG.info("** Add root: " + nodeFromKG.getLabel());
	//    		childrenNonRecursive.add(nodeFromKG);
	//    	}
	//		//Testar com todas as propriedades?
	//		
	//		//TODO como identificar uma folha na hierarquia?
	//		// 1 - Verificar se contém a propriedade rdf-schema#domain
	//		// Entidades não tem domínio!!
	//		String queryPrefix = "";
	//		
	//		for(String prefix: prefixes.keySet()){
	//			//PREFIX rdfs: <http://www.w3.org/2000/01/rdf-schema#>
	//			queryPrefix += "PREFIX " + prefix + ": <" + prefixes.get(prefix) +  "> \n";
	//		}
	//		
	//		for(String property: properties){
	//			//consulta que retorna o label dos filhos de um determinado nodo pai, dada uma relação
	//			String querySPARQL =  queryPrefix + 
	//					" select ?pai ?filho " + 
	//					" where {?filho " + property + " ?pai . ?pai rdfs:label \""+ nodeFromKG.getLabel() +"\"@en }";
	//			
	//			
	//			Query query = QueryFactory.create(querySPARQL);
	//			QueryExecution qexec = QueryExecutionFactory.sparqlService("http://dbpedia.org/sparql", query);
	//			
	//			try {
	//			    ResultSet results = qexec.execSelect();
	//		    	//System.out.println(results.toString());
	//			    
	//			    while(results.hasNext()) {
	//			    	String adjacentURI = results.next().toString();
	//			    	String[] res = adjacentURI.split("filho = <");
	//			    	res = res[1].split(">");
	//			    	
	//			    	adjacentURI = res[0];
	//			    	res = adjacentURI.split("http://dbpedia.org/");
	//			    	
	//			    	KGNode adjacent = new KGNode(adjacentURI);
	//			    	//adjacent.setIndirectHits(nodeFromKG.getDirectHits() + nodeFromKG.getIndirectHits());
	//			    	
	//			    	adjacent.addRelationship(property, nodeFromKG);
	//			    	
	//			    	//TODO como contar os hits?
	//			    	//TODO confirmar
	//			    	if(containsLabel(childrenNonRecursive, adjacent.getLabel())){
	//			    		adjacent.setIndirectHits(adjacent.getIndirectHits() + nodeFromKG.getDirectHits() + nodeFromKG.getIndirectHits() + 1);
	//			    	}else{
	//			    		adjacent.setIndirectHits(adjacent.getIndirectHits() + 1);
	//			    		childrenNonRecursive.add(adjacent);
	//			    		LOG.info("** Add child: " + adjacent.getLabel());
	//			    	}
	//			    }
	//			} 
	//			finally {
	//			   qexec.close();
	//			}
	//			
	//		}
	//	}
}