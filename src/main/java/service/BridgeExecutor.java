package service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;

import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.ModelFactory;
import org.apache.jena.rdf.model.Property;
import org.apache.jena.rdf.model.RDFNode;
import org.apache.jena.rdf.model.Resource;
import org.apache.jena.rdf.model.SimpleSelector;
import org.apache.jena.rdf.model.StmtIterator;

import pojo.Facet;

public class BridgeExecutor {

	// K: Recurso ou classe da DBpedia
	// V: classe da ontologia de alto nível
	private final HashMap<RDFNode, String> keyBridges;
	private final HashMap<RDFNode, String> newBridges;
	private final HashMap<RDFNode, String> inconsistentBridges;
	
	private final ArrayList<String> relationships; //the selected RDF relations between the nodeFromKG and the children
	private final Model model; //from JENA
	
	public BridgeExecutor(ArrayList<String> relationships){
		//TODO read keyBridges from a file?
		keyBridges = new HashMap<>();
		newBridges = new HashMap<>();
		inconsistentBridges = new HashMap<>();
		this.relationships = relationships;
		
		model = ModelFactory.createDefaultModel();
	}
	
	//TODO parametrizar?
	public void execute(RDFNode nodeFromKG){
		//recursive procedure, executes until the final of the nodeFromKG children hierarchy
		this.checkComplete(nodeFromKG, null);
	}
	
	/**
	 * 
	 * @param nodeFromKG node from hierarchy on Knowledge Graph
	 * @param dc domain class bridged to b
	 * 
	 * @returns void, but the algorithm populates newBridges and inconsistentBridges maps
	 */
	private void checkComplete(RDFNode nodeFromKG, String domainClass){
		
		ArrayList<RDFNode> children = getChildrenFromNodeOnKG(nodeFromKG);
		
		for(int i = 0; i< children.size(); i++){
			RDFNode childNode = children.get(i);
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
	 * @param nodeFromKG the parent node on the knowledge graph (e.g., DBpedia)
	 *  
	 * @return a list from the direct children from nodeFromKG 
	 */
	private ArrayList<RDFNode> getChildrenFromNodeOnKG(RDFNode nodeFromKG) {
		
		ArrayList<RDFNode> children = new ArrayList<>();
		
		if(!model.containsResource(nodeFromKG)){
			//TODO adicionar o pai na hierarquia
			//model.add(resource, property, nodeFromKG);
		}
		
		//Testar com todas as propriedades?
		for(String relationshipName: relationships){
			Property property = model.createProperty(relationshipName);
			Resource resource = null; //TODO descobrir o que é (ver código do Juarez)
			//TODO Usar o JENA para ir populando a hierarquia
			//TODO consultas com SPARQL para os filhos?
			
			children.add(nodeFromKG);
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
		StmtIterator iterator = datasetModel.listStatements(new SimpleSelector(null, property, (RDFNode) null));

		Resource r;
		while (!queue.isEmpty()) {
			r = queue.removeFirst();

			StmtIterator adjacents = datasetModel.listStatements(new SimpleSelector(r, property, (RDFNode) null));
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