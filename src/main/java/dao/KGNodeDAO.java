package dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import pojo.KGNode;

public class KGNodeDAO {

	private Connection connection;

	public KGNodeDAO() {
	}

	public void insertSubclass(int nodeClassId, KGNode nodeSubclass) {
		String sql = "INSERT INTO KGNODE_SUBCLASS (IDNODE, IDSUBCLASS) VALUES (?, ?)";

		Integer subclassId = null;
		if(nodeSubclass.getId() == null){
			subclassId = insert(nodeSubclass);
		}

		if(subclassId != null){
			this.getConnection();
			try {
				PreparedStatement stmt = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
				stmt.setInt(1, nodeClassId);
				stmt.setInt(2, subclassId);
				stmt.executeUpdate();
				stmt.close();
			} catch (SQLException e) {
				throw new RuntimeException(e);
			} finally {
				ConnectionFactory.fecharConexao(this.connection);
			}
		}
	}

	public int insertType(int nodeInstanceId, KGNode nodeClassType) {
		String sql = "INSERT INTO KGNODE_TYPE (IDNODE, IDTYPE) VALUES (?, ?)";

		Integer classTypeId = null;
		if(nodeClassType.getId() == null){
			classTypeId = insert(nodeClassType);
		}else{
			classTypeId = nodeClassType.getId();
		}

		if(classTypeId != null){
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
}