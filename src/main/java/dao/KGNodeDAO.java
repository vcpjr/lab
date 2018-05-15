package dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import pojo.KGNode;

public class KGNodeDAO {

	private Connection connection;

	public KGNodeDAO() {
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
				ConnectionFactory.fecharConexao(this.connection);
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
				ConnectionFactory.fecharConexao(this.connection);
			}
		}
		return classTypeId;
	}
	
	public int insertBridge(String uri, String highLevelClass, String type) {
		
		KGNode node = this.getByURI(uri);
		
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
			ConnectionFactory.fecharConexao(this.connection);
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
			ConnectionFactory.fecharConexao(this.connection);
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
			ConnectionFactory.fecharConexao(this.connection);
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
			String sql = "INSERT INTO KGNode (LABEL, URI, DIRECTHITS, INDIRECTHITSTYPE, INDIRECTHITSSUBCLASSOF) VALUES (?, ?, ?, ?, ?)";

			try {
				PreparedStatement stmt = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
				stmt.setString(1, node.getLabel());
				stmt.setString(2, node.getUri());
				stmt.setInt(3, node.getDirectHits());
				stmt.setInt(4, node.getIndirectHitsType());
				stmt.setInt(5, node.getIndirectHitsSubclassOf());
				stmt.executeUpdate();

				ResultSet rs = stmt.getGeneratedKeys();
				if (rs.next()) {
					newId = rs.getInt(1);
				}
				stmt.close();
			} catch (SQLException e) {
				throw new RuntimeException(e);
			} finally {
				ConnectionFactory.fecharConexao(this.connection);
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

			ResultSet rs = stmt.getGeneratedKeys();
			stmt.close();
		} catch (SQLException e) {
			throw new RuntimeException(e);
		} finally {
			ConnectionFactory.fecharConexao(this.connection);
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

		}
		return success;
	}

	public void deleteAll() {
		this.getConnection();
		String sql = "DELETE FROM KGNode WHERE 1 = 1; ";

		try {
			PreparedStatement stmt = connection.prepareStatement(sql);
			stmt.executeUpdate();
			stmt.close();
		} catch (SQLException e) {
			
		}

		sql = "DELETE FROM KGNode_Subclass WHERE 1 = 1; ";
		try {
			PreparedStatement stmt = connection.prepareStatement(sql);
			stmt.executeUpdate();
			stmt.close();
		} catch (SQLException e) {
			
		}
		
		sql = "DELETE FROM KGNode_Type WHERE 1 = 1; ";
		try {
			PreparedStatement stmt = connection.prepareStatement(sql);
			stmt.executeUpdate();
			stmt.close();
		} catch (SQLException e) {
			
		}
		
		sql = "DELETE FROM BRIDGE WHERE 1 = 1; ";
		try {
			PreparedStatement stmt = connection.prepareStatement(sql);
			stmt.executeUpdate();
			stmt.close();
		} catch (SQLException e) {
			
		}
	}

	public KGNode getById(int id) {
		this.getConnection();
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
			ConnectionFactory.fecharConexao(this.connection);
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
			ConnectionFactory.fecharConexao(this.connection);
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
			//TODO
		} finally {
			ConnectionFactory.fecharConexao(this.connection);
		}

		return nodes;
	}

	/**
	 * Given a class node, return the path to Thing through subclassOf relationship
	 * 
	 * @param idNode
	 * @return classes a list of classes node -> THING
	 */
	public ArrayList<KGNode> getSuperclassesPath(Integer idNode, ArrayList<KGNode> classes) {
		this.getConnection();

		if(classes == null){
			classes = new ArrayList<>();
		}

		String sql = "SELECT S.IDSUBCLASS FROM KGNODE_SUBCLASS S WHERE S.IDNODE = ?";
		try {
			PreparedStatement stmt = connection.prepareStatement(sql);
			stmt.setInt(1, idNode);

			int id = -1;
			ResultSet rs = stmt.executeQuery();
			while (rs.next()) {
				id = rs.getInt(1);
				KGNode superclassNode = this.getById(id);

				if(!classes.contains(superclassNode)){
					classes.add(superclassNode);
				}

				classes = getSuperclassesPath(superclassNode.getId(), classes);
			}

			stmt.close();
			return classes;
		} catch (SQLException e) {
			return null;
		} finally {
			ConnectionFactory.fecharConexao(this.connection);
		}
	}

	public ArrayList<KGNode> getTypesByInstanceId(Integer instanceNodeId) {
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
				KGNode typeNode = this.getById(idNodeType);
				types.add(typeNode);
			}

			stmt.close();
			return types;
		} catch (SQLException e) {
			return null;
		} finally {
			ConnectionFactory.fecharConexao(this.connection);
		}
	}

	private void getConnection() {
		this.connection = new ConnectionFactory().obterConexao();
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
				KGNode node = this.getById(idNode);
				node.setBridgeType(rs.getString(3));
				map.put(node, highLevelClass);
			}

			stmt.close();
			return map;
		} catch (SQLException e) {
			return null;
		} finally {
			ConnectionFactory.fecharConexao(this.connection);
		}
	}
}