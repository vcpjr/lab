package service;

import java.util.ArrayList;
import java.util.LinkedList;

import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.Property;
import org.apache.jena.rdf.model.RDFNode;
import org.apache.jena.rdf.model.Resource;
import org.apache.jena.rdf.model.SimpleSelector;
import org.apache.jena.rdf.model.StmtIterator;

import pojo.Facet;

public class BridgeService {

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
