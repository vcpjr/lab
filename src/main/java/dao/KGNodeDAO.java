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

		System.out.println("** Creating RDF link: " + ret);
		return ret;
	}

	public String createLabel(KGNode source){
		//<http://dbpedia.org/ontology/MusicalArtist>	<http://www.w3.org/2000/01/rdf-schema#label>	"musicien"@fr .

		String ret = "";
		ret = "<" + source.getUri() + "> <" + KGNode.LABEL_URI   + "> " + '"' + source.toString() + '"' + " .";

		System.out.println("** Creating RDF label: " + ret);
		return ret;
	}

	public int insertSuperclass(int nodeClassId, KGNode nodeSuperclass) {
		String sql = "INSERT INTO KGNODE_SUPERCLASS (IDNODE, IDSUPERCLASS) VALUES (?, ?)";
		Integer superclassId = null;

		if(nodeSuperclass.getId() == null){
			superclassId = insert(nodeSuperclass);
		}else{
			superclassId = nodeSuperclass.getId();
		}

		Integer kgNodeSuperclass_id = getKGNode_SuperclassId(nodeClassId, superclassId);
		if(kgNodeSuperclass_id == null){//New relationship
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
			System.out.println("KGNode not found: " + uri);
			return -1;
		}else{
			int idBridge = this.getIdBridgeByIdNode(node.getId());
			
			String sql;
			if(idBridge > 0){
				sql = " UPDATE BRIDGE SET IDNODE = ?, HIGH_LEVEL_CLASS = ?, TYPE = ? ";
				sql += " WHERE ID = ? ";
			}else{
				sql = "INSERT INTO BRIDGE (IDNODE, HIGH_LEVEL_CLASS, TYPE) VALUES (?, ?, ?)";
			}
			this.getConnection();
			int newId = -1;
			try {
				PreparedStatement stmt = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
				stmt.setInt(1, node.getId());
				stmt.setString(2, highLevelClass);
				stmt.setString(3, type);
				
				if(idBridge > 0){
					stmt.setInt(4, idBridge);
				}
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

	private int getIdBridgeByIdNode(int nodeId) {
		
		int bridgeId = -1;
		String sql = " SELECT B.ID FROM BRIDGE B WHERE B.IDNODE = ? ";
		this.getConnection();
		try {
			PreparedStatement stmt = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
			stmt.setInt(1, nodeId);
			stmt.setMaxRows(1);

			ResultSet rs = stmt.executeQuery();
			while(rs.next()){
				bridgeId = rs.getInt("ID");
			}

			stmt.close();
		} catch (SQLException e) {
			throw new RuntimeException(e);
		} finally {
			ConnectionFactory.closeConnection(this.connection);
		}

		return bridgeId;
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

	private Integer getKGNode_SuperclassId(int nodeClassId, Integer superclassId) {
		Integer kgNodeSuperclass_Id = null;
		String sql = "SELECT ID FROM KGNODE_SUPERCLASS WHERE IDNODE = ? AND IDSUPERCLASS = ?";
		this.getConnection();
		try {
			PreparedStatement stmt = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
			stmt.setInt(1, nodeClassId);
			stmt.setInt(2, superclassId);
			stmt.setMaxRows(1);

			ResultSet rs = stmt.executeQuery();
			while(rs.next()){
				kgNodeSuperclass_Id = rs.getInt("ID");
			}

			stmt.close();
		} catch (SQLException e) {
			throw new RuntimeException(e);
		} finally {
			ConnectionFactory.closeConnection(this.connection);
		}

		return kgNodeSuperclass_Id;
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
		String sql = "SELECT n.*, b.type as bridgeType FROM KGNode n LEFT JOIN BRIDGE b on n.id = b.IDNODE "
				+ " WHERE n.id = ? ";
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
			
			//Bridges
			node.setBridgeType(rs.getString("bridgeType"));
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
	 * Given a class node, return the ASCENDING path to Thing through subclassOf relationship
	 * 
	 * @param idActualCLass
	 * @return classes a list of classes node -> THING
	 */
	public ArrayList<KGNode> getSuperclassesPath(Integer idClassWithTypeRelationship, Integer idActualCLass, ArrayList<KGNode> classes, Connection conn) {
		//Não abre nem fecha conexão pois é recursivo
		ArrayList<KGNode> classesFullPath = this.getSuperclassesPath(idClassWithTypeRelationship, conn);

		if(classesFullPath == null || classesFullPath.isEmpty() || classes.isEmpty()){
			String sql = "SELECT S.IDSUPERCLASS FROM KGNODE_SUPERCLASS S WHERE S.IDNODE = ?";
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

	/**
	 * Método recursivo que retorna todos as classes (subclasses ou types) abaixo de um determinado nodo
	 * 
	 * @param node o nodo de partida da recursão
	 * @return Map<Integer, ArrayList<Node>>, onde:
	 * 	K: nível abaixo do (nível do nodo é 0)
	 *  V: lista de subclasses/types no nível encontrado 
	 */
	/**
	 * Returns a map of KGNodes, where the key is the number of the level descending from root to the leaves (types) 
	 * @param idRoot id from KGNode root from hierarchy (directly associated to Thing)
	 * @return
	 */
	public HashMap<Integer, ArrayList<KGNode>> getSubclasses(KGNode initialNode, KGNode actualNode, int actualLevel, HashMap<Integer, ArrayList<KGNode>> map, Connection conn){

		if(initialNode == actualNode){
			map = new HashMap<>();
		}
		
		this.addOnSubclassLevel(map, actualNode, actualLevel);
		
		System.out.println("Get subclasses from: " + actualNode.toString());
		ArrayList<KGNode> subclassesFromLevel = this.getDirectSubclassesFromNode(actualNode.getId(), conn);
		
		if(subclassesFromLevel != null && !subclassesFromLevel.isEmpty()){
			int nextLevel = actualLevel + 1;
			for(KGNode subclassNodeFromLevel: subclassesFromLevel){
				this.addOnSubclassLevel(map, subclassNodeFromLevel, nextLevel);
				map = getSubclasses(initialNode, subclassNodeFromLevel, nextLevel, map, conn);	
			}
		}

		return map;
	}
	
	private void addOnSubclassLevel(HashMap<Integer, ArrayList<KGNode>> map, KGNode node, int level) {
		
		//TODO TESTAR, tem ERRO!!
		if(map.containsKey(level)){
			ArrayList<KGNode> classes = map.get(level);
			if(!containsLabel(classes, node.getLabel())){
				System.out.println("Add node " + node.toString() + "on level " + level);
				classes.add(node);
				//this.insertNodeOnHierarchy(node, level, nodeRoot);
			}
		}else{//Cria um novo nível
			ArrayList<KGNode> classes = new ArrayList<>();
			classes.add(node);
			System.out.println("Add node " + node.toString() + "on NEW level " + level);
			map.put(level, classes);
			//TODO salvar caminho de subclasses no banco?
			//this.insertNodeOnHierarchy(node, level, nodeRoot);
		}

	}


	/**
	 * Returns a map of KGNodes, where the key is the number of the level descending from root to the leaves (types) 
	 * @param idRoot id from KGNode root from hierarchy (directly associated to Thing)
	 * @return hierarchy
	 */
	public HashMap<Integer, ArrayList<KGNode>> getHierarchy(KGNode nodeRoot, KGNode actualNode, int actualLevel, HashMap<Integer, ArrayList<KGNode>> hierarchy, Connection conn){

		if(actualNode == nodeRoot){
			hierarchy = this.getHierarchyFromTable(nodeRoot);
		}

		ArrayList<KGNode> superclassesFromActualLevel = this.getDirectSuperclassesFromNode(actualNode.getId(), conn);

		int nextLevel = actualLevel + 1;
		for(KGNode superclassNode: superclassesFromActualLevel){
			this.addOnHierarchy(hierarchy, superclassNode, nodeRoot, actualLevel);
			hierarchy = this.getHierarchy(nodeRoot, superclassNode, nextLevel, hierarchy, conn);
		}
		return hierarchy;
	}


	private void addOnHierarchy(HashMap<Integer, ArrayList<KGNode>> hierarchy, KGNode node, KGNode nodeRoot, int level) {
		if(hierarchy.containsKey(level)){
			ArrayList<KGNode> classes = hierarchy.get(level);
			if(!containsLabel(classes, node.getLabel())){
				classes.add(node);
				this.insertNodeOnHierarchy(node, level, nodeRoot);
			}
		}else{//Cria um novo nível
			ArrayList<KGNode> classes = new ArrayList<>();
			classes.add(node);
			hierarchy.put(level, classes);
			this.insertNodeOnHierarchy(node, level, nodeRoot);
		}

	}
	
	private HashMap<Integer, ArrayList<KGNode>> getHierarchyFromTable(KGNode nodeRoot) {
		HashMap<Integer, ArrayList<KGNode>> hierarchy = new HashMap<>();

		String sql = "SELECT H.IDNODE, H.LEVEL FROM HIERARCHY H WHERE H.IDROOT = ?";
		try {
			PreparedStatement stmt = connection.prepareStatement(sql);
			stmt.setInt(1, nodeRoot.getId());

			ResultSet rs = stmt.executeQuery();
			while (rs.next()) {
				int idNode = rs.getInt(1);
				int level = rs.getInt(2);
				KGNode node = this.getById(idNode, this.getConnection());

				if(hierarchy.containsKey(level)){
					ArrayList<KGNode> classes = hierarchy.get(level);
					if(!containsLabel(classes, node.getLabel())){
						classes.add(node);
					}
				}else{//Cria um novo nível no map
					ArrayList<KGNode> classes = new ArrayList<>();
					classes.add(node);
					hierarchy.put(level, classes);
				}

			}
			stmt.close();
		} catch (SQLException e) {
			System.err.println(e.toString());
		} 
		return hierarchy;
	}



	private void insertNodeOnHierarchy(KGNode node, int level, KGNode nodeRoot) {
		String sql = "INSERT INTO HIERARCHY (IDROOT, IDNODE, LEVEL) VALUES (?, ?, ?)";

		this.getConnection();
		try {
			PreparedStatement stmt = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
			stmt.setInt(1, nodeRoot.getId());
			stmt.setInt(2, node.getId());
			stmt.setInt(3, level);
			stmt.executeUpdate();
			stmt.close();
		} catch (SQLException e) {
			throw new RuntimeException(e);
		} finally {
			ConnectionFactory.closeConnection(this.connection);
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
	
	public ArrayList<KGNode> getInstancesByTypeId(Integer typeId, Connection conn) {
		this.getConnection();

		ArrayList<KGNode> instances = new ArrayList<>();
		String sql = "SELECT T.IDNODE FROM KGNODE_TYPE T WHERE T.IDTYPE = ?";
		try {
			PreparedStatement stmt = connection.prepareStatement(sql);
			stmt.setInt(1, typeId);

			int instanceNodeId = -1;
			ResultSet rs = stmt.executeQuery();
			while (rs.next()) {
				instanceNodeId = rs.getInt(1);
				KGNode instance = this.getById(instanceNodeId, conn);
				instances.add(instance);
			}

			stmt.close();
			return instances;
		} catch (SQLException e) {
			return null;
		} finally {
			ConnectionFactory.closeConnection(this.connection);
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

	public ArrayList<KGNode> getDirectSuperclassesFromNode(Integer nodeId, Connection conn) {
		if(conn == null){
			this.getConnection();
		}
		
		//TODO testar
		ArrayList<KGNode> superclasses = new ArrayList<>();
		String sql = "SELECT s.idsuperclass from kgnode_superclass s where s.idnode = ?";
		try {
			PreparedStatement stmt = connection.prepareStatement(sql);
			stmt.setInt(1, nodeId);

			int idNode = -1;
			ResultSet rs = stmt.executeQuery();
			while (rs.next()) {
				idNode = rs.getInt(1);
				KGNode superclassNode = this.getById(idNode, conn);
				superclasses.add(superclassNode);
			}

			stmt.close();
			return superclasses;
		} catch (SQLException e) {
			return null;
		} finally {
			ConnectionFactory.closeConnection(this.connection);
		}
	}
	
	public ArrayList<KGNode> getDirectSubclassesFromNode(Integer nodeId, Connection conn) {
		
		if(conn == null){
			this.getConnection();
		}
		//TODO testar
		String sql = "SELECT S.IDNODE FROM KGNODE_SUPERCLASS S WHERE S.IDSUPERCLASS = ?";
		ArrayList<KGNode> subclasses = new ArrayList<>();
		try {
			PreparedStatement stmt = connection.prepareStatement(sql);
			stmt.setInt(1, nodeId);

			int idNode = -1;
			ResultSet rs = stmt.executeQuery();
			while (rs.next()) {
				idNode = rs.getInt(1);
				KGNode subclassNode = this.getById(idNode, conn);
				subclasses.add(subclassNode);
			}

			stmt.close();
			
			String msg = "Subclasses from " + this.getById(nodeId, conn).toString() + ": ";
			for(KGNode sub: subclasses){
				msg += "," + sub.toString();
			}
			System.out.println(msg);
			
			return subclasses;
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

	public HashMap<Integer, String> getBridges(String type) {
		HashMap<Integer, String> map = new HashMap<>();
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
				map.put(idNode, highLevelClass);
			}

			stmt.close();
			return map;
		} catch (SQLException e) {
			return null;
		} finally {
			ConnectionFactory.closeConnection(this.connection);
		}
	}

	public ArrayList<KGNode> getSuperclassesOf_SpotlightQuery(KGNode nodeClassType) {
		ArrayList<KGNode> superclasses = new ArrayList<>();

		String querySPARQL =  getQueryPrefix() + 
				" select ?superclass " + 
				" where {<" + nodeClassType.getUri() + "> rdfs:subClassOf ?superclass}";

		Query query = QueryFactory.create(querySPARQL);
		QueryExecution qexec = QueryExecutionFactory.sparqlService("http://dbpedia.org/sparql", query);
		String msg = "------ Subclasses of " + nodeClassType.toString() + "\n";
		try {
			org.apache.jena.query.ResultSet results = qexec.execSelect();

			ArrayList<String> uris = new ArrayList<>();
			while(results.hasNext()) {
				boolean addSuperclass = false;
				String adjacentURI = results.next().toString();

				if(!uris.contains(adjacentURI)){
					uris.add(adjacentURI);

					if(!adjacentURI.contains(KGNode.URL_ROOT)){
						if(adjacentURI.contains("dbpedia")){
							String[] res = adjacentURI.split("superclass = <");
							res = res[1].split(">");
							adjacentURI = res[0];
							addSuperclass = true;
						}
					}else{
						adjacentURI = KGNode.URL_ROOT;
						addSuperclass = true;
					}
					//Pode ser que a classe já exista
					KGNode superclass = getKGNode(adjacentURI, KGNode.RELATIONSHIP_SUBCLASS_OF, nodeClassType.getIndirectHitsType());
					if(superclass != null &&!containsLabel(superclasses, superclass.getLabel()) && addSuperclass){
						superclasses.add(superclass);
						msg += "- " + superclass.getLabel() + "\n"; 
					}
				}
			}
		}finally {
			qexec.close();
		}
		msg += "-------------------------------------";
		System.out.println(msg);
		return superclasses;
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
		deleteAllSuperclasses();
		deleteAllTypes();
		deleteAllBridges();
		deleteAllSuperclassesPaths();
		deleteAllKGNodes();
	}

	private void deleteAllSuperclasses() {
		this.getConnection();
		String sql = "DELETE FROM KGNode_Superclass WHERE 1 = 1; ";
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

	public void deleteAllHierarchies() {
		this.getConnection();
		String sql = "DELETE FROM HIERARCHY WHERE 1 = 1; ";
		try {
			PreparedStatement stmt = connection.prepareStatement(sql);
			stmt.executeUpdate();
			stmt.close();
		} catch (SQLException e) {
		}
		this.closeConnection();

	}
}