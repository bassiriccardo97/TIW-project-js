package it.polimi.tiw.project.utils;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

import javax.servlet.ServletContext;
import javax.servlet.UnavailableException;

/**
 * The <CODE>ConnectionHandler</CODE> allows to open and close the <CODE>Connection</CODE> to the database
 */
public class ConnectionHandler {

	/**
	 * Opens the <CODE>Connection</CODE> to the database
	 * @param context	the <CODE>ServletContext</CODE> from which open the <CODE>Connection</CODE>
	 * @return the <CODE>Connection</CODE> to the database
	 * @throws UnavailableException
	 */
	public static Connection getConnection(ServletContext context) throws UnavailableException {
		Connection connection = null;
		try {

			String driver = context.getInitParameter("dbDriver");
			String url = context.getInitParameter("dbUrl");
			String user = context.getInitParameter("dbUser");
			String password = System.getenv("MYSQL_PSW");
			Class.forName(driver);
			connection = DriverManager.getConnection(url, user, password);
		} catch (ClassNotFoundException e) {
			throw new UnavailableException("Can't load database driver");
		} catch (SQLException e) {
		    System.out.println(e);
			throw new UnavailableException("Couldn't get db connection");
		}
		return connection;
	}


	/**
	 * Closes the <CODE>Connection</CODE> to the database
	 * @param connection	the <CODE>Connection</CODE> to be closed
	 * @throws SQLException
	 */
	public static void closeConnection(Connection connection) throws SQLException {
		if (connection != null) {
			connection.close();
		}
	}
	
}
