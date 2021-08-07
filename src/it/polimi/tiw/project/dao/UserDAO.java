package it.polimi.tiw.project.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Arrays;

/**
 * DAO for users
 */
public class UserDAO {
	private Connection connection;

	/**
	 * Constructor for the DAO
	 * @param connection	the <CODE>Connection</CODE> to the database
	 */
	public UserDAO(Connection connection) {
		this.connection = connection;
	}
	
	/**
	 * Checks if the credentials are on the database and logs in the user
	 * @param username	the username
	 * @param password	the password
	 * @return the user id if credentials exists, -1 otherwise
	 * @throws SQLException
	 * @throws Exception
	 */
	public int checkCredentials(String username, byte[] password) throws SQLException, Exception {
		String query = "SELECT userid, password FROM user WHERE username = ?";
		try {
			PreparedStatement pStatement = connection.prepareStatement(query);
			pStatement.setString(1, username);
			ResultSet result = pStatement.executeQuery();
			if (!result.isBeforeFirst()) {
				return -1;
			} else {
				result.next();
				if (!Arrays.equals(password, result.getBytes("password"))) {
					return -1;
				} else {
					int userId = result.getInt("userid");
					return userId;
				}
			}
		} catch (SQLException e) {
			throw new Exception("Failed to login the user.");
		}
	}
	
	/**
	 * Registers the user on the database if the username doesn't already exists
	 * @param username	the username
	 * @param password	the password
	 * @throws SQLException
	 * @throws Exception
	 */
	public void registerUser(String username, byte[] password) throws SQLException, Exception {
		String query = "INSERT INTO user (username, password) VALUES (?, ?);";
		try {
			PreparedStatement pStatement = connection.prepareStatement(query);
			pStatement.setString(1, username);
			pStatement.setBytes(2, password);
			pStatement.executeUpdate();
		} catch (SQLException e) {
			throw new Exception("Failed to register the user.");
		}
	}
}
