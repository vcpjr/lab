package dao;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

/**
 * Classe respons�vel por criar e destruir conexões com bancos de dados
 * 
 * O banco escolhido foi o MySQL, assim � necess�rio utilizar o respectivo
 * driver JDBC
 * 
 * @author Vilmar C. Pereira Júnior
 * 
 *         Disciplina de Desenvolvimento Web Senac 2017.1
 *
 */
public class ConnectionFactory {

	public Connection obterConexao() {
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

	public static void fecharConexao(Connection con) {
		try {
			con.close();
		} catch (SQLException e) {
			throw new RuntimeException(e);
		}
	}
}