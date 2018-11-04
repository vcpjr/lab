package dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import pojo.KGNode;
import pojo.Tweet;

/**
 *
 * @author Vilmar César Pereira Júnior
 */
public class TweetDAO {

	private Connection connection;

	public TweetDAO() {
	}

	public void insert(Tweet tweet) {
		this.getConnection();
		
		String text = tweet.getText();
		if(text != null && text.length() > 500){
			text = text.substring(0, 500);
		}
		
		String sql = "INSERT INTO Tweet (ID, USERID, TEXT, CREATIONDATE, ISRETWEET) VALUES (?, ?, ?, ?, ?)";
		try {
			PreparedStatement stmt = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
			stmt.setLong(1, tweet.getId());
			stmt.setLong(2, tweet.getUserId());
			stmt.setString(3, text);
			stmt.setDate(4, new java.sql.Date(tweet.getCreationDate().getTime()));
			stmt.setBoolean(5, tweet.isRetweet());
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
		String sql = "DELETE FROM Tweet WHERE id = ?";

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
	
	private ArrayList<KGNode> getAnnotatedNodesFromTweet(Long idTweet, Connection conn){
		if(conn == null){
			this.getConnection();
		}

		ArrayList<KGNode> annotatedNodes = new ArrayList<>();
		String sql = "SELECT T.IDNODE FROM KGNODE_TWEET T WHERE T.IDTWEET = ?";
		try {
			PreparedStatement stmt = connection.prepareStatement(sql);
			stmt.setLong(1, idTweet);

			int nodeId = -1;
			ResultSet rs = stmt.executeQuery();
			KGNodeDAO dao = new KGNodeDAO();
			while (rs.next()) {
				nodeId = rs.getInt(1);
				KGNode node = dao.getById(nodeId, conn);
				annotatedNodes.add(node);
			}

			stmt.close();
			return annotatedNodes;
		} catch (SQLException e) {
			return null;
		} finally {
			ConnectionFactory.closeConnection(this.connection);
		}
	}

	public Tweet getById(int id, Connection conn) {
		if(conn == null){
			this.getConnection();
		}

		Tweet tweet = null;
		String sql = "SELECT * FROM Tweet t "
				+ " WHERE t.id = ? ";
		try {
			PreparedStatement stmt = connection.prepareStatement(sql);
			stmt.setInt(1, id);
			stmt.setMaxRows(1);

			ResultSet rs = stmt.executeQuery();
			while (rs.next()) {
				tweet = this.getResultSet(rs);
			}
			stmt.close();
			
			ArrayList<KGNode> annotatedNodes = getAnnotatedNodesFromTweet(tweet.getId(), conn);
			if(annotatedNodes != null){
				tweet.setAnnotatedResources(annotatedNodes);
			}
			
			return tweet;
		} catch (SQLException e) {
			return null;
		} finally {
			if(conn == null){
				ConnectionFactory.closeConnection(this.connection);
			}
		}
	}

	public List<Tweet> list() {
		this.getConnection();
		List<Tweet> tweets = new ArrayList<Tweet>();
		String sql = "SELECT * FROM Tweet";
		try {
			PreparedStatement stmt = connection.prepareStatement(sql);
			ResultSet rs = stmt.executeQuery();
			while (rs.next()) {
				Tweet t = this.getResultSet(rs);
				tweets.add(t);
			}
			stmt.close();

			return tweets;
		} catch (SQLException e) {

		} finally {
			ConnectionFactory.closeConnection(this.connection);
		}
		return tweets;
	}

	private Tweet getResultSet(ResultSet rs) {
		Tweet node = null;
		try {
			node = new Tweet();
			node.setId(rs.getLong("id"));
			node.setUserId(rs.getLong("userid"));
			node.setText(rs.getString("text"));
			node.setCreationDate(rs.getDate("creationDate"));
			node.setRetweet(rs.getBoolean("isRetweet"));
		} catch (SQLException e) {

		}
		return node;
	}

	public void deleteAll() {
		this.getConnection();
		String sql = "DELETE FROM Tweet WHERE 1 = 1; ";
		try {
			PreparedStatement stmt = connection.prepareStatement(sql);
			stmt.executeUpdate();
			stmt.close();
		} catch (SQLException e) {
		}
		this.closeConnection();
	}

	public Connection getConnection() {
		this.connection = new ConnectionFactory().createConnection();
		return this.connection;
	}

	public void closeConnection(){
		ConnectionFactory.closeConnection(this.connection);
	}
}