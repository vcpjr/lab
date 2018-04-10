package pojo;

import java.util.Map;

/**
 * 
 * Represents a Knowledge Graph Node (i.g., a class or resource on DBpedia)
 * @author vilmar
 *
 */
public class KGNode {

	private String label;
	private String uri;
	private Map<String, KGNode> relationships;
	
	public KGNode() {
		
	}

	public String getLabel() {
		return label;
	}

	public void setLabel(String label) {
		this.label = label;
	}

	public String getUri() {
		return uri;
	}

	public void setUri(String uri) {
		this.uri = uri;
	}

	public Map<String, KGNode> getRelationships() {
		return relationships;
	}

	public void setRelationships(Map<String, KGNode> relationships) {
		this.relationships = relationships;
	}

	public KGNode(String label, String uri, Map<String, KGNode> relationships) {
		super();
		this.label = label;
		this.uri = uri;
		this.relationships = relationships;
	}
}