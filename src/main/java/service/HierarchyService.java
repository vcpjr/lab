package service;

import java.io.File;

import org.apache.jena.rdf.model.Model;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class HierarchyService {

	private static final Logger LOG = LoggerFactory.getLogger(HierarchyService.class);
	private final Model hierarchyRDF = null;
	
	public HierarchyService(File annotationsFile, String rootClass) {
		

	}

	public Model getHierarchyRDF() {
		return hierarchyRDF;
	}
}
