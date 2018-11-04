package pojo;

/**
 * 
 * Represents a Knowledge Graph Node (i.g., a class or instance on DBpedia)
 * @author Vilmar César Pereira Júnior
 *
 */
public class KGNode {

	public static final String RELATIONSHIP_INSTANCE = "instance";
	public static final String RELATIONSHIP_TYPE_OF = "type_of";
	public static final String RELATIONSHIP_SUBCLASS_OF = "subclass_of";
	
	public static final String RELATIONSHIP_TYPE_OF_URI = "http://www.w3.org/1999/02/22-rdf-syntax-ns#type";
	public static final String RELATIONSHIP_SUBCLASS_OF_URI = "http://www.w3.org/2000/01/rdf-schema#subClassOf";
	public static final String LABEL_URI = "http://www.w3.org/2000/01/rdf-schema#label";
	
	public static final String URL_ROOT = "owl:Thing";

	public static final String NODE_TYPE_INSTANCE = "Instance";
	public static final String NODE_TYPE_CLASS = "Class";

	public static final String BRIDGE_TYPE_KEY = "Key";
	public static final String BRIDGE_TYPE_NEW = "New";
	public static final String BRIDGE_TYPE_INCONSISTENT = "Inconsistent";

	private Integer id;
	private String label;
	private String uri;
	private int directHits;
	private int indirectHitsType;
	private int indirectHitsSubclassOf;
	private String nodeType; //Class or instance
	private String bridgeType;

	public KGNode() {

	}

	public KGNode(String uri, String type) {
		super();
		//TODO teste
		//this.label = String.format("%s", label).toLowerCase();
		this.label = uri;
		this.uri = uri;
		this.nodeType = type;
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


	public int getDirectHits() {
		return directHits;
	}

	public void setDirectHits(int directHits) {
		this.directHits = directHits;
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;

		KGNode node = (KGNode) o;

		if (uri != node.uri) return false;

		return uri != null ? uri.equals(node.uri) : node.uri == null;
	}

	@Override
	public int hashCode() {
		int result = uri != null ? uri.hashCode() : 0;
		result = 31 * result;
		return result;
	}

	public int getIndirectHitsType() {
		return indirectHitsType;
	}

	public void setIndirectHitsType(int indirectHitsType) {
		this.indirectHitsType = indirectHitsType;
	}

	public int getIndirectHitsSubclassOf() {
		return indirectHitsSubclassOf;
	}

	public void setIndirectHitsSubclassOf(int indirectHitsSubclassOf) {
		this.indirectHitsSubclassOf = indirectHitsSubclassOf;
	}

	@Override
	public String toString() {
		String[] parts = this.label.split("http://dbpedia.org/ontology/");
		
		if(parts != null && parts.length == 2){
			this.setLabel(parts[1]);
		}
		
		return this.label + " (" + this.directHits + "," + this.indirectHitsType + "," + this.indirectHitsSubclassOf + ")";  
	}

	public String getBridgeType() {
		return bridgeType;
	}

	public void setBridgeType(String bridgeType) {
		this.bridgeType = bridgeType;
	}

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public static String getDBpediaClassURI(String label) {
		String uri = null;
		String[] parts = label.split("DBpedia:");

		if(parts != null && parts.length == 2){
			uri = "http://dbpedia.org/ontology/" + parts[1];
		}else if (label.contains("Thing")){
			uri = URL_ROOT;
		}
		return uri;
	}

	public String getNodeType() {
		return nodeType;
	}

	public void setNodeType(String nodeType) {
		this.nodeType = nodeType;
	}
}