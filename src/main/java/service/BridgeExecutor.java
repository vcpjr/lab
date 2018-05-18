package service;

import java.io.File;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import dao.KGNodeDAO;
import pojo.KGNode;
import util.CSVReport;

public class BridgeExecutor {
	
	/*
	 * TODO 08/05/2018
	 * - Rever step 1 (contagem): OK
	 * - Ordenar a planilha por hierarquias ou hits acumulados: OK
	 * - Dúvidas nas pontes: usar brand: OK
	 * 
	 * - Definições de hits (Dawak)
	 * 		- Diretos
	 * 		- Indiretos por type
	 * 		- Indiretos por subclass_of 	 
	 * - Filtragem por hits acumulados
	 * - Diminuir pseudocódigo
	 */
	private static final String APP_ROOT = System.getProperty("user.dir");
    private static final Logger LOG = LoggerFactory.getLogger(NerdExecutor.class);
    private static final File outputPath = new File(APP_ROOT,"output");
	private KGNodeDAO dao = new KGNodeDAO();

	// K: Instância ou classe da DBpedia
	// V: Nome da classe da ontologia de alto nível
	private HashMap<KGNode, String> keyBridges;
	private HashMap<KGNode, String> newBridges;
	private HashMap<KGNode, String> inconsistentBridges;
	
	private KGNode root;

	public BridgeExecutor(){
		dao = new KGNodeDAO();
		keyBridges = dao.getBridges(KGNode.BRIDGE_TYPE_KEY);
	}

	public void execute(){
		//TODO teste com a raiz variável, de acordo com os termos encontrados em cada tweet
		for(KGNode node: keyBridges.keySet()){
			this.root = node;
			this.checkComplete(this.root, keyBridges.get(node));
		}
		
		newBridges = dao.getBridges(KGNode.BRIDGE_TYPE_NEW);
		inconsistentBridges = dao.getBridges(KGNode.BRIDGE_TYPE_INCONSISTENT);

		LOG.info("********************************************************************");
		LOG.info("******************BridgeExecutor execution complete*****************");
		LOG.info("************ Key Bridges: " + this.keyBridges.size());
		LOG.info("************ New Bridges: " + this.newBridges.size());
		LOG.info("************ Inconsistent Bridges: " + this.inconsistentBridges.size());

		generateReportCSV();
	}
	
	private void generateReportCSV() {
		CSVReport bridgeReport = new CSVReport("Resource; #Direct Hits; #Indirect Hits (Type); #Indirect Hits (Subclass); Accumulated; GoodRelations (gr:) class; Type");

		LOG.info("******************BridgeExecutor keyBridges CSV generation*****************");
		String nodeText;
		HashMap<KGNode, String> allBridges = dao.getBridges(null);
		LOG.info("******************BridgeExecutor newBridges CSV generation*****************");

		for(KGNode n: allBridges.keySet()){
			int accumulatedHits = n.getDirectHits() + n.getIndirectHitsSubclassOf() + n.getIndirectHitsType();
			nodeText = String.format(Locale.US, "%s;%d;%d;%d;%d;%s;%s", n.getLabel(), n.getDirectHits(), n.getIndirectHitsType(), n.getIndirectHitsSubclassOf(), accumulatedHits, allBridges.get(n), n.getBridgeType());
			bridgeReport.append(nodeText);
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

		KGNodeDAO dao = new KGNodeDAO();
		
		//TODO ajustar!!!
		ArrayList<KGNode> superclassesFromNode = dao.getSuperclassesPath(node.getId(), dao.getConnection());
		List<KGNode> nodesFromKeyBridges = new ArrayList<KGNode>(keyBridges.keySet());
		
		if(superclassesFromNode.isEmpty()) {
			dao.insertBridge(node.getUri(), domainClass, KGNode.BRIDGE_TYPE_NEW);
		}else {
			for(int i = 0; i< superclassesFromNode.size(); i++){
				KGNode childNode = superclassesFromNode.get(i);
				String childDomainClass = keyBridges.get(childNode);
				if(childDomainClass == null){
					checkComplete(childNode, domainClass);
				}else{
					if(containsLabel(nodesFromKeyBridges, childNode.getLabel())){
						LOG.info("** Domain class: "+ domainClass + "exists on keyBridges");
						if(keyBridges.get(childNode) != null 
								&& keyBridges.get(childNode).equals(domainClass)){
							dao.insertBridge(childNode.getUri(), domainClass, KGNode.BRIDGE_TYPE_NEW);
						}else{
							dao.insertBridge(childNode.getUri(), domainClass, KGNode.BRIDGE_TYPE_INCONSISTENT);
						}
					}else {
						dao.insertBridge(childNode.getUri(), domainClass, KGNode.BRIDGE_TYPE_NEW);
					}
				}
			}
		}
		dao.closeConnection();
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