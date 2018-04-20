package service;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;

import org.apache.jena.query.Query;
import org.apache.jena.query.QueryExecution;
import org.apache.jena.query.QueryExecutionFactory;
import org.apache.jena.query.QueryFactory;
import org.apache.jena.query.ResultSet;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.Property;
import org.apache.jena.rdf.model.Resource;
import org.apache.jena.rdf.model.SimpleSelector;
import org.apache.jena.rdf.model.StmtIterator;

import pojo.Facet;
import pojo.KGNode;
import util.BridgesFileReader;

public class BridgeExecutor {

	// K: Recurso ou classe da DBpedia
	// V: classe da ontologia de alto nível
	private final HashMap<KGNode, String> keyBridges;
	private final HashMap<KGNode, String> newBridges;
	private final HashMap<KGNode, String> inconsistentBridges;
	
	private final HashMap<String, String> prefixes; //K: prefix (rdf); V: uri (http://www.w3.org/2000/01/rdf-schema#)
	private final ArrayList<String> properties; //rdf:type or rdfs:subClassOf
	
	private KGNode root;
	private List<KGNode> nodesFromFile;
	ArrayList<KGNode> childrenNonRecursive = new ArrayList<>();
	
	public BridgeExecutor(HashMap<String, String> prefixes, ArrayList<String> properties, String rootURI, File datasetFile){
        nodesFromFile = BridgesFileReader.readKGNodesFromFile(datasetFile);

		//TODO read keyBridges from a CSV file
		keyBridges = new HashMap<>();
		newBridges = new HashMap<>();
		inconsistentBridges = new HashMap<>();
		this.properties = properties;
		this.prefixes = prefixes;
		
		//TODO usar a consulta para pegar o label
		String rootLabel = rootURI;
		root = new KGNode(rootLabel, rootURI);
	}
	
	public void execute(){
		//recursive procedure, executes until the final of the nodeFromKG children hierarchy
		//TODO teste com a raiz variável, de acordo com os termos encontrados em cada tweet
		// Validar
		for(KGNode node: nodesFromFile){
			this.root = node;
			//this.checkComplete(this.root, null);
			this.checkCompleteNonRecursive(root, null);
		}
		
		
	}
	
	/**
	 * 
	 * @param node node from hierarchy on Knowledge Graph
	 * @param dc domain class bridged to b
	 * 
	 * @returns void, but the algorithm populates newBridges and inconsistentBridges maps
	 */
	private void checkComplete(KGNode node, String domainClass){
		
		ArrayList<KGNode> children = getChildrenFromNodeOnKG(node);
		
		for(int i = 0; i< children.size(); i++){
			KGNode childNode = children.get(i);
			domainClass = keyBridges.get(childNode);
			
			if(domainClass == null){
				checkComplete(childNode, domainClass);
			}else{
				if(keyBridges.containsKey(childNode)){
					if(keyBridges.get(childNode) != null 
							&& keyBridges.get(childNode).equals(domainClass)){
						newBridges.put(childNode, domainClass);
					}else{
						inconsistentBridges.put(childNode, domainClass);
					}
				}else {//Ainda não tem ponte
					newBridges.put(childNode, domainClass);
				}
			}
		}
	}
	
	/**
	 * 
	 * @param node node from hierarchy on Knowledge Graph
	 * @param dc domain class bridged to b
	 * 
	 * @returns void, but the algorithm populates newBridges and inconsistentBridges maps
	 */
	private void checkCompleteNonRecursive(KGNode node, String domainClass){
		
		ArrayList<KGNode> children = getChildrenFromNodeOnKG(node);
		
		//TODO melhorar caso funcione
		for (KGNode n: children) {
			if(!this.childrenNonRecursive.contains(n)){
				this.childrenNonRecursive.add(n);
			}
		}
		
		for(int i = 0; i< children.size(); i++){
			KGNode childNode = children.get(i);
			domainClass = keyBridges.get(childNode);
			
			if(keyBridges.containsKey(childNode)){
				if(keyBridges.get(childNode) != null 
						&& keyBridges.get(childNode).equals(domainClass)){
					newBridges.put(childNode, domainClass);
				}else{
					inconsistentBridges.put(childNode, domainClass);
				}
			}else {//Ainda não tem ponte
				newBridges.put(childNode, domainClass);
			}
		}
	}

	/**
	 * 
	 * @param nodeFromKG the parent node on the knowledge graph (e.g., DBpedia)
	 *  
	 * @return a list from the direct children from nodeFromKG 
	 */
	private ArrayList<KGNode> getChildrenFromNodeOnKG(KGNode nodeFromKG) {
		
		ArrayList<KGNode> children = new ArrayList<>();
		//Testar com todas as propriedades?
		
		//TODO como identificar uma folha na hierarquia?
		// 1 - Verificar de contém a propriedade rdf-schema#domain
		// Entidades não tem domínio!!
		String queryPrefix = "";
		
		for(String prefix: prefixes.keySet()){
			//PREFIX rdfs: <http://www.w3.org/2000/01/rdf-schema#>
			queryPrefix += "PREFIX " + prefix + ": <" + prefixes.get(prefix) +  "> \n";
		}
		
		for(String property: properties){
			//consulta que retorna o label dos filhos de um determinado nodo pai, dada uma relação
			String querySPARQL =  queryPrefix + 
					
					" select ?pai ?filho " + 
					" where {?filho " + property + " ?pai . ?pai rdfs:label \"agent\"@en } LIMIT 100";
			
			
			Query query = QueryFactory.create(querySPARQL);
			QueryExecution qexec = QueryExecutionFactory.sparqlService("http://dbpedia.org/sparql", query);

			try {
			    ResultSet results = qexec.execSelect();
			    
			    //TODO incluir as relações para KGNode atual
		    	System.out.println(results.toString());
			    
			    while(results.hasNext()) {
			    	System.out.println(results.next().toString());
			    }
			} 
			finally {
			   qexec.close();
			}
			if(!children.contains(nodeFromKG)){
				children.add(nodeFromKG);
			}
		}
	
		return children;
	}
	
	// TODO rever as entradas
	@Deprecated
	private Model bridgeBoost(Model hierarchyRDF, ArrayList<String> domainClasses, Model manualBridgesRDF) {
		// TODO como instanciar?
		Model boostedBridges = null;
		Model inconsistencies;

		LinkedList<Resource> queue = new LinkedList<>();

		String domain = "dbpedia.org"; // TODO parametrizar
		String propertyString = "type";// TODO parametrizar (lista de
										// propriedades)

		Resource root = getRootFromHierarchy(hierarchyRDF);
		queue.add(root);

		Model datasetModel = Facet.getSingleton().openGraph(domain);
		Property property = datasetModel.getProperty(propertyString);
		StmtIterator iterator = datasetModel.listStatements(new SimpleSelector(null, property, (KGNode) null));

		Resource r;
		while (!queue.isEmpty()) {
			r = queue.removeFirst();

			StmtIterator adjacents = datasetModel.listStatements(new SimpleSelector(r, property, (KGNode) null));
			Resource r_i;
			while (adjacents.hasNext()) {
				r_i = (Resource) adjacents.next();

				// TODO continuar
				// RDFVisitorMapping rv = RDFVisitorMapping.getSingleton();

			}
		}

		return boostedBridges;
	}

	@Deprecated
	private Resource getRootFromHierarchy(Model hierarchyRDF) {
		// TODO Auto-generated method stub
		return null;
	}
}