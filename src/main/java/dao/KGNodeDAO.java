package dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.apache.jena.query.Query;
import org.apache.jena.query.QueryExecution;
import org.apache.jena.query.QueryExecutionFactory;
import org.apache.jena.query.QueryFactory;

import pojo.KGNode;

public class KGNodeDAO {

	private Connection connection;

	public KGNodeDAO() {
	}
	
	public String createRDFLink(KGNode source, KGNode destiny, String relationshipURI){
		String ret = "";
		ret = "<" + source.getUri() + "> <" + relationshipURI   + "> <" + destiny.getUri() + ">.";
		return ret;
	}
	
	public String createLabel(KGNode source){
		//<http://dbpedia.org/ontology/MusicalArtist>	<http://www.w3.org/2000/01/rdf-schema#label>	"musicien"@fr .
		String ret = "";
		ret = "<" + source.getUri() + "> <" + KGNode.LABEL_URI   + "> " + '"' + source.toString() + '"' + " .";
		return ret;
	}

	public int insertSubclass(int nodeClassId, KGNode nodeSuperclass) {
		String sql = "INSERT INTO KGNODE_SUBCLASS (IDNODE, IDSUBCLASS) VALUES (?, ?)";
		Integer superclassId = null;

		if(nodeSuperclass.getId() == null){
			superclassId = insert(nodeSuperclass);
		}else{
			superclassId = nodeSuperclass.getId();
		}

		Integer kgNodeSubclass_id = getKGNode_SubclassId(nodeClassId, superclassId);
		if(kgNodeSubclass_id == null){//New relationship
			this.getConnection();
			try {
				PreparedStatement stmt = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
				stmt.setInt(1, nodeClassId);
				stmt.setInt(2, superclassId);
				stmt.executeUpdate();
				stmt.close();
			} catch (SQLException e) {
				throw new RuntimeException(e);
			} finally {
				ConnectionFactory.closeConnection(this.connection);
			}
		}
		return superclassId;
	}

	public int insertType(int nodeInstanceId, KGNode nodeClassType) {
		String sql = "INSERT INTO KGNODE_TYPE (IDNODE, IDTYPE) VALUES (?, ?)";
		Integer classTypeId = null;

		if(nodeClassType.getId() == null){
			classTypeId = insert(nodeClassType);
		}else{
			classTypeId = nodeClassType.getId();
		}

		Integer kgNodeType_id = getKGNode_TypeId(nodeInstanceId, classTypeId);
		if(kgNodeType_id == null){//New relationship
			this.getConnection();
			try {
				PreparedStatement stmt = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
				stmt.setInt(1, nodeInstanceId);
				stmt.setInt(2, classTypeId);
				stmt.executeUpdate();
				stmt.close();
			} catch (SQLException e) {
				throw new RuntimeException(e);
			} finally {
				ConnectionFactory.closeConnection(this.connection);
			}
		}
		return classTypeId;
	}

	public int insertBridge(String uri, String highLevelClass, String type) {
		KGNode node = this.getByURI(uri);

		if(node == null){
			//TODO tratar
			System.out.println("KGNode not found: " + uri);
			return -1;
		}else{
			String sql = "INSERT INTO BRIDGE (IDNODE, HIGH_LEVEL_CLASS, TYPE) VALUES (?, ?, ?)";
			this.getConnection();
			int newId = -1;
			try {
				PreparedStatement stmt = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
				stmt.setInt(1, node.getId());
				stmt.setString(2, highLevelClass);
				stmt.setString(3, type);
				stmt.executeUpdate();

				ResultSet rs = stmt.getGeneratedKeys();
				if (rs.next()) {
					newId = rs.getInt(1);
				}
				stmt.close();
				return newId;
			} catch (SQLException e) {
				throw new RuntimeException(e);
			} finally {
				ConnectionFactory.closeConnection(this.connection);
			}
		}
	}

	private Integer getKGNode_TypeId(int nodeInstanceId, Integer classTypeId) {
		Integer kgNodeType_Id = null;
		String sql = "SELECT ID FROM KGNODE_TYPE WHERE IDNODE = ? AND IDTYPE = ?";
		this.getConnection();
		try {
			PreparedStatement stmt = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
			stmt.setInt(1, nodeInstanceId);
			stmt.setInt(2, classTypeId);
			stmt.setMaxRows(1);

			ResultSet rs = stmt.executeQuery();
			while(rs.next()){
				kgNodeType_Id = rs.getInt("ID");
			}

			stmt.close();
		} catch (SQLException e) {
			throw new RuntimeException(e);
		} finally {
			ConnectionFactory.closeConnection(this.connection);
		}

		return kgNodeType_Id;
	}

	private Integer getKGNode_SubclassId(int nodeClassId, Integer superclassId) {
		Integer kgNodeSubclass_Id = null;
		String sql = "SELECT ID FROM KGNODE_SUBCLASS WHERE IDNODE = ? AND IDSUBCLASS = ?";
		this.getConnection();
		try {
			PreparedStatement stmt = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
			stmt.setInt(1, nodeClassId);
			stmt.setInt(2, superclassId);
			stmt.setMaxRows(1);

			ResultSet rs = stmt.executeQuery();
			while(rs.next()){
				kgNodeSubclass_Id = rs.getInt("ID");
			}

			stmt.close();
		} catch (SQLException e) {
			throw new RuntimeException(e);
		} finally {
			ConnectionFactory.closeConnection(this.connection);
		}

		return kgNodeSubclass_Id;
	}

	public int insert(KGNode node) {
		KGNode n = this.getByURI(node.getUri());
		int newId = -1;

		if(n != null && n.getId() != null){
			n.setDirectHits(node.getDirectHits());
			n.setIndirectHitsSubclassOf(node.getIndirectHitsSubclassOf());
			n.setIndirectHitsType(node.getIndirectHitsType());

			this.update(n);
			newId = n.getId();
		}else{
			this.getConnection();
			String sql = "INSERT INTO KGNode (LABEL, URI, DIRECTHITS, INDIRECTHITSTYPE, INDIRECTHITSSUBCLASSOF, TYPE) VALUES (?, ?, ?, ?, ?, ?)";
			try {
				PreparedStatement stmt = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
				stmt.setString(1, node.getLabel());
				stmt.setString(2, node.getUri());
				stmt.setInt(3, node.getDirectHits());
				stmt.setInt(4, node.getIndirectHitsType());
				stmt.setInt(5, node.getIndirectHitsSubclassOf());
				stmt.setString(6, node.getNodeType());
				stmt.executeUpdate();

				ResultSet rs = stmt.getGeneratedKeys();
				if (rs.next()) {
					newId = rs.getInt(1);
				}
				stmt.close();
			} catch (SQLException e) {
				throw new RuntimeException(e);
			} finally {
				ConnectionFactory.closeConnection(this.connection);
			}
		}
		return newId;
	}

	private void update(KGNode node) {
		this.getConnection();
		String sql = " UPDATE KGNode SET DIRECTHITS = ?, INDIRECTHITSTYPE = ?, INDIRECTHITSSUBCLASSOF = ? ";
		sql += " WHERE ID = ? ";
		try {
			PreparedStatement stmt = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
			stmt.setInt(1, node.getDirectHits());
			stmt.setInt(2, node.getIndirectHitsType());
			stmt.setInt(3, node.getIndirectHitsSubclassOf());
			stmt.setInt(4, node.getId());
			stmt.executeUpdate();
			stmt.close();
		} catch (SQLException e) {
			throw new RuntimeException(e);
		} finally {
			ConnectionFactory.closeConnection(this.connection);
		}
	}

	public boolean delete(int id) {
		this.getConnection();
		boolean success = false;
		String sql = "DELETE FROM KGNode WHERE id = ?";

		try {
			PreparedStatement stmt = connection.prepareStatement(sql);
			stmt.setInt(1, id);

			int returnCode = stmt.executeUpdate();
			if (returnCode == 1) {
				success = true;
			} 
			stmt.close();
		} catch (SQLException e) {
			throw new RuntimeException(e);
		}
		return success;
	}

	public KGNode getById(int id, Connection conn) {
		if(conn == null){
			this.getConnection();
		}

		KGNode node = null;
		String sql = "SELECT n.* FROM KGNode n WHERE n.id = ?";
		try {
			PreparedStatement stmt = connection.prepareStatement(sql);
			stmt.setInt(1, id);
			stmt.setMaxRows(1);

			ResultSet rs = stmt.executeQuery();
			while (rs.next()) {
				node = this.getKGNodeResultSet(rs);
			}
			stmt.close();
			return node;
		} catch (SQLException e) {
			return null;
		} finally {
			if(conn == null){
				ConnectionFactory.closeConnection(this.connection);
			}
		}
	}

	public KGNode getByURI(String uri) {
		this.getConnection();
		KGNode node = null;
		String sql = "SELECT n.* FROM KGNode n WHERE n.URI = ?";
		try {
			PreparedStatement stmt = connection.prepareStatement(sql);
			stmt.setString(1, uri);
			stmt.setMaxRows(1);

			ResultSet rs = stmt.executeQuery();
			while (rs.next()) {
				node = this.getKGNodeResultSet(rs);
			}
			stmt.close();
			return node;
		} catch (SQLException e) {
			return null;
		} finally {
			ConnectionFactory.closeConnection(this.connection);
		}
	}


	private KGNode getKGNodeResultSet(ResultSet rs) {
		KGNode node = null;
		try {
			node = new KGNode();
			node.setId(rs.getInt("id"));
			node.setLabel(rs.getString("label"));
			node.setUri(rs.getString("uri"));
			node.setDirectHits(rs.getInt("directHits"));
			node.setIndirectHitsType(rs.getInt("indirectHitsType"));
			node.setIndirectHitsSubclassOf(rs.getInt("indirectHitsSubclassOf"));
		} catch (SQLException e) {

		}
		return node;
	}

	public List<KGNode> list() {
		this.getConnection();
		List<KGNode> nodes = new ArrayList<KGNode>();
		String sql = "SELECT * FROM KGNode";
		try {
			PreparedStatement stmt = connection.prepareStatement(sql);
			ResultSet rs = stmt.executeQuery();
			while (rs.next()) {
				KGNode node = this.getKGNodeResultSet(rs);
				nodes.add(node);
			}
			stmt.close();

			return nodes;
		} catch (SQLException e) {

		} finally {
			ConnectionFactory.closeConnection(this.connection);
		}
		return nodes;
	}

	/**
	 * Given a class node, return the path to Thing through subclassOf relationship
	 * 
	 * @param idActualCLass
	 * @return classes a list of classes node -> THING
	 */
	public ArrayList<KGNode> getSuperclassesPath(Integer idClassWithTypeRelationship, Integer idActualCLass, ArrayList<KGNode> classes, Connection conn) {
		//Não abre nem fecha conexão pois é recursivo
		ArrayList<KGNode> classesFullPath = this.getSuperclassesPath(idClassWithTypeRelationship, conn);

		if(classesFullPath == null || classesFullPath.isEmpty() || classes.isEmpty()){
			String sql = "SELECT S.IDSUBCLASS FROM KGNODE_SUBCLASS S WHERE S.IDNODE = ?";
			try {
				PreparedStatement stmt = connection.prepareStatement(sql);
				stmt.setInt(1, idActualCLass);
				int id = -1;
				ResultSet rs = stmt.executeQuery();
				while (rs.next()) {
					id = rs.getInt(1);
					KGNode superclassNode = this.getById(id, conn);
					if(!classes.contains(superclassNode)){
						classes.add(superclassNode);
						if(superclassNode.getLabel().equals(KGNode.URL_ROOT)){
							//Salva o caminho todo no banco
							this.insertPathToThing(idClassWithTypeRelationship, classes, conn);
						}
					}
					classes = getSuperclassesPath(idClassWithTypeRelationship,superclassNode.getId(), classes, conn);
				}
				stmt.close();
				return classes;
			} catch (SQLException e) {
				return null;
			} 
		}else{//Has path
			return classes;
		}
	}

	public ArrayList<KGNode> getSuperclassesPath(Integer idClassWithTypeRelationship, Connection conn) {
		//Não abre nem fecha conexão, pois é recursivo
		ArrayList<KGNode> path = new ArrayList<>();
		String sql;
		sql = "SELECT P.CSV_IDNODES_PATH_TO_THING FROM SUPERCLASSES_PATH P WHERE P.IDNODE_CLASS_TYPE_RELATIONSHIP = ?";
		try {
			PreparedStatement stmt = connection.prepareStatement(sql);
			stmt.setInt(1, idClassWithTypeRelationship);

			ResultSet rs = stmt.executeQuery();
			while (rs.next()) {
				String csvPath = rs.getString(1);
				String[] ids = csvPath.split(",");

				for(int i = 0; i< ids.length; i++){
					Integer id = Integer.valueOf(ids[i]);
					KGNode node = this.getById(id, conn);
					path.add(node);
				}
			}
			stmt.close();
			
			printPath(idClassWithTypeRelationship, path, conn);
			
			return path;
		} catch (SQLException e) {
			return null;
		}
	}


	private void printPath(Integer originNodeId, ArrayList<KGNode> path, Connection conn) {
		KGNode origin = this.getById(originNodeId, conn);
		String msg = "";
		for(KGNode node: path){
			msg += " -> " + node.toString();
		}
		System.out.println("Path from " + origin.toString() + ": " + msg);
	}

	private void insertPathToThing(Integer classWithTypeRelationshipId, ArrayList<KGNode> classes, Connection conn) {
		ArrayList<KGNode> oldPath = this.getSuperclassesPath(classWithTypeRelationshipId, conn);
		String sql = "";
		String ids = "";
		for(KGNode node: classes){
			ids += node.getId() + ",";
		}

		boolean insertOrUpdate = false;
		if(oldPath != null && !oldPath.isEmpty()){
			if(oldPath.size() < classes.size()){ //só atualiza por um caminho MAIOR
				//UPDATE
				sql = " UPDATE SUPERCLASSES_PATH SET CSV_IDNODES_PATH_TO_THING = ? ";
				sql += " WHERE IDNODE_CLASS_TYPE_RELATIONSHIP = ? ";
				insertOrUpdate = true;
				System.err.println("Updating PATH. Id_Type: " + classWithTypeRelationshipId);
			} 
		}else{//Novo caminho para o ID_TYPE
			sql = "INSERT INTO SUPERCLASSES_PATH (IDNODE_CLASS_TYPE_RELATIONSHIP, CSV_IDNODES_PATH_TO_THING) VALUES (?, ?)";
			insertOrUpdate = true;
		}

		if(insertOrUpdate){
			try {
				PreparedStatement stmt = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
				stmt.setInt(1, classWithTypeRelationshipId);
				stmt.setString(2, ids);
				stmt.executeUpdate();
				stmt.close();
			} catch (SQLException e) {
				throw new RuntimeException(e);
			}
		}
	}

	public ArrayList<KGNode> getTypesByInstanceId(Integer instanceNodeId, Connection conn) {
		this.getConnection();

		ArrayList<KGNode> types = new ArrayList<>();
		String sql = "SELECT T.IDTYPE FROM KGNODE_TYPE T WHERE T.IDNODE = ?";
		try {
			PreparedStatement stmt = connection.prepareStatement(sql);
			stmt.setInt(1, instanceNodeId);

			int idNodeType = -1;
			ResultSet rs = stmt.executeQuery();
			while (rs.next()) {
				idNodeType = rs.getInt(1);
				KGNode typeNode = this.getById(idNodeType, conn);
				types.add(typeNode);
			}

			stmt.close();
			return types;
		} catch (SQLException e) {
			return null;
		} finally {
			ConnectionFactory.closeConnection(this.connection);
		}
	}

	public Connection getConnection() {
		this.connection = new ConnectionFactory().createConnection();
		return this.connection;
	}

	public void closeConnection(){
		ConnectionFactory.closeConnection(this.connection);
	}

	public HashMap<KGNode, String> getBridges(String type) {
		HashMap<KGNode, String> map = new HashMap<>();
		this.getConnection();

		String sql;
		if(type == null){
			sql = "SELECT B.IDNODE, B.HIGH_LEVEL_CLASS, B.TYPE FROM BRIDGE B";
		}else{
			sql = "SELECT B.IDNODE, B.HIGH_LEVEL_CLASS, B.TYPE FROM BRIDGE B WHERE B.TYPE = '" + type + "'";
		}
		try {
			PreparedStatement stmt = connection.prepareStatement(sql);
			ResultSet rs = stmt.executeQuery();
			while (rs.next()) {
				int idNode = rs.getInt(1);
				String highLevelClass = rs.getString(2);
				KGNode node = this.getById(idNode, null);
				node.setBridgeType(rs.getString(3));
				map.put(node, highLevelClass);
			}

			stmt.close();
			return map;
		} catch (SQLException e) {
			return null;
		} finally {
			ConnectionFactory.closeConnection(this.connection);
		}
	}

	public ArrayList<KGNode> getSubclassesOf(KGNode nodeClassType) {
		ArrayList<KGNode> subclasses = new ArrayList<>();

		String querySPARQL =  getQueryPrefix() + 
				" select ?subclass " + 
				" where {<" + nodeClassType.getUri() + "> rdfs:subClassOf ?subclass}";

		Query query = QueryFactory.create(querySPARQL);
		QueryExecution qexec = QueryExecutionFactory.sparqlService("http://dbpedia.org/sparql", query);
		String msg = "------ Subclasses of " + nodeClassType.toString() + "\n";
		try {
			org.apache.jena.query.ResultSet results = qexec.execSelect();

			ArrayList<String> uris = new ArrayList<>();
			while(results.hasNext()) {
				boolean addSubclass = false;
				String adjacentURI = results.next().toString();

				if(!uris.contains(adjacentURI)){
					uris.add(adjacentURI);

					if(!adjacentURI.contains(KGNode.URL_ROOT)){
						if(adjacentURI.contains("dbpedia")){
							String[] res = adjacentURI.split("subclass = <");
							res = res[1].split(">");
							adjacentURI = res[0];
							addSubclass = true;
						}
					}else{
						adjacentURI = KGNode.URL_ROOT;
						addSubclass = true;
					}
					//Pode ser que a classe já exista
					KGNode subclass = getKGNode(adjacentURI, KGNode.RELATIONSHIP_SUBCLASS_OF, nodeClassType.getIndirectHitsType());
					if(subclass != null &&!containsLabel(subclasses, subclass.getLabel()) && addSubclass){
						subclasses.add(subclass);
						msg += "- " + subclass.getLabel() + "\n"; 
					}
				}
			}
		}finally {
			qexec.close();
		}
		msg += "-------------------------------------";
		System.out.println(msg);
		return subclasses;
	}

	public static boolean containsLabel(List<KGNode> list, String label){
		return list.stream().filter(o -> o.getLabel().equals(label)).findFirst().isPresent();
	}

	private String getQueryPrefix() {
		HashMap<String, String> prefixes = new HashMap<>();
		prefixes.put("rdfs", "http://www.w3.org/2000/01/rdf-schema#");
		prefixes.put("rdf", "http://www.w3.org/1999/02/22-rdf-syntax-ns#");

		String queryPrefix = "";
		for(String prefix: prefixes.keySet()){
			queryPrefix += "PREFIX " + prefix + ": <" + prefixes.get(prefix) +  "> \n";

		}
		return queryPrefix;
	}

	public KGNode getKGNode(String uri, String relationship, int hitsToSum) {
		KGNodeDAO dao = new KGNodeDAO();
		KGNode resource = null;

		if(uri.contains("dbpedia") || uri.contains(KGNode.URL_ROOT)){
			resource = dao.getByURI(uri);

			String type = "";
			if(uri.contains("dbpedia.org/ontology") || uri.contains(KGNode.URL_ROOT)){
				type = KGNode.NODE_TYPE_CLASS;
			}else if(uri.contains("dbpedia.org/resource")){
				type = KGNode.NODE_TYPE_INSTANCE;
			}

			if(resource == null){
				resource = new KGNode(uri, type);
			}

			switch (relationship) {
			case KGNode.RELATIONSHIP_INSTANCE:
				resource.setDirectHits(resource.getDirectHits() + hitsToSum);
				break;
			case KGNode.RELATIONSHIP_TYPE_OF:
				resource.setIndirectHitsType(resource.getIndirectHitsType() + hitsToSum);
				break;
			case KGNode.RELATIONSHIP_SUBCLASS_OF:
				resource.setIndirectHitsSubclassOf(resource.getIndirectHitsSubclassOf() + hitsToSum);
				break;	
			}

			dao.insert(resource);
			System.out.println("obtain/update node: " + resource.toString() + "---" + relationship);
		}
		return resource;
	}

	public void deleteAll() {
		deleteAllSubclasses();
		deleteAllTypes();
		deleteAllBridges();
		deleteAllSuperclassesPaths();
		deleteAllKGNodes();
	}

	private void deleteAllSubclasses() {
		this.getConnection();
		String sql = "DELETE FROM KGNode_Subclass WHERE 1 = 1; ";
		try {
			PreparedStatement stmt = connection.prepareStatement(sql);
			stmt.executeUpdate();
			stmt.close();
		} catch (SQLException e) {
		}
		this.closeConnection();
	}

	private void deleteAllTypes() {
		this.getConnection();
		String sql = "DELETE FROM KGNode_Type WHERE 1 = 1; ";
		try {
			PreparedStatement stmt = connection.prepareStatement(sql);
			stmt.executeUpdate();
			stmt.close();
		} catch (SQLException e) {
		}
		this.closeConnection();
	}

	public void deleteAllBridges() {
		this.getConnection();
		String sql = "DELETE FROM BRIDGE WHERE 1 = 1; ";
		try {
			PreparedStatement stmt = connection.prepareStatement(sql);
			stmt.executeUpdate();
			stmt.close();
		} catch (SQLException e) {
		}
		this.closeConnection();
	}

	private void deleteAllSuperclassesPaths() {
		this.getConnection();
		String sql = "DELETE FROM SUPERCLASSES_PATH WHERE 1 = 1; ";
		try {
			PreparedStatement stmt = connection.prepareStatement(sql);
			stmt.executeUpdate();
			stmt.close();
		} catch (SQLException e) {
		}
		this.closeConnection();
	}

	private void deleteAllKGNodes() {
		this.getConnection();
		String sql = "DELETE FROM KGNode WHERE 1 = 1; ";

		try {
			PreparedStatement stmt = connection.prepareStatement(sql);
			stmt.executeUpdate();
			stmt.close();
		} catch (SQLException e) {

		}
		this.closeConnection();
	}

	public ArrayList<KGNode> getByNodeType(String nodeType) {
		this.getConnection();
		ArrayList<KGNode> nodes = new ArrayList<KGNode>();
		String sql = "SELECT * FROM KGNode N WHERE N.TYPE = '" + nodeType + "'";
		try {
			PreparedStatement stmt = connection.prepareStatement(sql);
			ResultSet rs = stmt.executeQuery();
			while (rs.next()) {
				KGNode node = this.getKGNodeResultSet(rs);
				nodes.add(node);
			}
			stmt.close();

			return nodes;
		} catch (SQLException e) {

		} finally {
			ConnectionFactory.closeConnection(this.connection);
		}
		return nodes;
	}
}