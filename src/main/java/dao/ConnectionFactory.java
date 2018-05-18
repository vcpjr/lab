package dao;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

/**
 * @author Vilmar C. Pereira Júnior
 */
public class ConnectionFactory {

	public Connection createConnection() {
		String nomeEsquema = "mestrado";
		String enderecoBanco = "jdbc:mysql://localhost/" + nomeEsquema + "?autoReconnect=true&useSSL=false";
		String usuario = "root";
		String senha = "1234";
		String driverJDBC = "com.mysql.jdbc.Driver";

		try {
			Class.forName(driverJDBC);
			Connection conexao = DriverManager.getConnection(enderecoBanco, usuario, senha);
			return conexao;
		} catch (SQLException | ClassNotFoundException e) {
			throw new RuntimeException(e);
		}
	}

	public static void closeConnection(Connection con) {
		try {
			con.close();
		} catch (SQLException e) {
			throw new RuntimeException(e);
		}
	}
}