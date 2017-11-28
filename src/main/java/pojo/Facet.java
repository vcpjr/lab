package pojo;

import java.util.HashMap;

import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.ModelFactory;
import org.apache.jena.shared.AlreadyExistsException;
import org.apache.jena.shared.DoesNotExistException;
import org.apache.jena.util.iterator.ExtendedIterator;
import org.apache.jena.util.iterator.WrappedIterator;


public class Facet { // a Model Factory inspired in GraphMaker

	private static Facet singleton = new Facet();
	private Model defaultGraph;
	private int anonCounter;
	private HashMap<String,Model> listNamedGraphs;

	public Facet() {
		listNamedGraphs = new HashMap<String,Model>();
		anonCounter = 0;
	}
	
	public static Facet getSingleton() {
		return singleton;
	}
	
	public void setCounter(int i) {
	    anonCounter = i;
	}

	public Model getGraph() {
        if (defaultGraph == null) { 
        	defaultGraph = createGraph();
        }
        return defaultGraph;
	}

	public Model openGraph() {
		if (defaultGraph == null) {
			throw new DoesNotExistException("no default graph in this GraphMaker [" 
				+ this.getClass() + "]" );
		}
		return defaultGraph;
	}

	public Model createGraph() {
		return createGraph( "anon_" + anonCounter++ + "", false );
	}

	public Model createGraph(String name, boolean strict) {
		if (listNamedGraphs.containsKey(name)) {
			if(strict) {
				throw new AlreadyExistsException(" named graph already exists in this GraphMaker [" 
					+ this.getClass() + "]" );
			}
			return listNamedGraphs.get(name);
		}
		Model newGraph = ModelFactory.createDefaultModel();
		this.listNamedGraphs.put(name, newGraph);
		return newGraph;
	}

	public Model createGraph(String name) {
		return createGraph( name, false );
	}

	public Model openGraph(String name, boolean strict) {
		if (!listNamedGraphs.containsKey(name)) {
			if(strict) {
				throw new DoesNotExistException(" named graph already exists in this GraphMaker [" 
					+ this.getClass() + "]" );
			}
			Model newGraph = ModelFactory.createDefaultModel();
			this.listNamedGraphs.put(name, newGraph);
			return newGraph;
		}
		return listNamedGraphs.get(name);		
	}

	public Model openGraph(String name) {
		return openGraph( name, false );
	}

	public void removeGraph(String name) {
		this.listNamedGraphs.remove(name);

	}

	public boolean hasGraph(String name) {
		return listNamedGraphs.containsKey(name);
	}

	public void close() {
		listNamedGraphs.clear();
	}

	public ExtendedIterator<String> listGraphs() {
		ExtendedIterator<String> it = WrappedIterator.create(listNamedGraphs.keySet().iterator());
		return it;
	}
}
