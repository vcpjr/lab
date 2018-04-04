package service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;

import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.Property;
import org.apache.jena.rdf.model.RDFNode;
import org.apache.jena.rdf.model.Resource;
import org.apache.jena.rdf.model.SimpleSelector;
import org.apache.jena.rdf.model.StmtIterator;

import pojo.Facet;

public class BridgeService {

	
	/**
	 * 
	 * @param n node from hierarchy on Knowledge Graph
	 * @param dc domain class bridged to b
	 * @param KB key bridges 
	 * 
	 * TODO representar as pontes como pares (nodo KG, classe de alto nível)?
	 * @param NB new bridges
	 * @param IB inconsistent bridges
	 */
	public static void checkComplete(RDFNode n, RDFNode dc, HashMap<RDFNode, RDFNode> KB, ArrayList<RDFNode> NB, ArrayList<RDFNode> IB){
		
		ArrayList<RDFNode> children = getChildren(n);
		
		for(int i = 0; i< children.size(); i++){
			RDFNode nc = children.get(i);
			RDFNode domainClass = getClassBridged(nc);
			
			if(domainClass == null){
				checkComplete(nc, domainClass, KB, NB, IB);
			}else{
				if(KB.containsKey(nc)){
					if(KB.get(nc) == domainClass){
						NB.add(nc);
					}else{
						IB.add(nc);
					}
				}
			}
		}
	}	
	
	private static RDFNode getClassBridged(RDFNode nc) {
		// TODO Auto-generated method stub
		return null;
	}

	private static ArrayList<RDFNode> getBridges(RDFNode nc) {
		// TODO Auto-generated method stub
		return null;
	}

	private static ArrayList<RDFNode> getChildren(RDFNode c) {
		// TODO Auto-generated method stub
		return null;
	}

	// TODO rever as entradas
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

	private Resource getRootFromHierarchy(Model hierarchyRDF) {
		// TODO Auto-generated method stub
		return null;
	}
}