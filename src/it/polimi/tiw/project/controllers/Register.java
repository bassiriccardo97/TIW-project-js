package it.polimi.tiw.project.controllers;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.sql.Connection;
import java.sql.SQLException;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.google.gson.Gson;

import it.polimi.tiw.project.beans.User;
import it.polimi.tiw.project.dao.UserDAO;
import it.polimi.tiw.project.utils.ConnectionHandler;
import it.polimi.tiw.project.utils.SimpleResponse;

/**
 * Servlet implementation class Register
 */
@WebServlet("/Register")
public class Register extends HttpServlet {
	private static final long serialVersionUID = 1L;
	private Connection connection;

       
    /**
     * @see HttpServlet#HttpServlet()
     */
    public Register() {
        super();
        // TODO Auto-generated constructor stub
    }

    public void init() throws ServletException {
		connection = ConnectionHandler.getConnection(getServletContext());
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
	    SimpleResponse customResponse = new SimpleResponse();
	    User user = new Gson().fromJson(request.getReader(), User.class);
	    String username = user.getUsername();
	    String password = user.getPassword();
	    
		if (username == null || username.isEmpty() || password == null || password.isEmpty()) {
			customResponse.setAndSendResponse(response, HttpServletResponse.SC_BAD_REQUEST, "Credentials must not be null.");
			return;
		}

		UserDAO uDAO = new UserDAO(connection);
		try {
			// hash the password
			MessageDigest digest = MessageDigest.getInstance("SHA-256");
			byte[] hash = digest.digest(password.getBytes(StandardCharsets.UTF_8));
			uDAO.registerUser(username, hash);
		} catch (Exception e) {
		    customResponse.setAndSendResponse(response, HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Internal error, change creds or retry later.");
			return;
		}
		
    	request.getSession().setAttribute("user", user);
		customResponse.setAndSendResponse(response, HttpServletResponse.SC_OK, username);
	}

	public void destroy() {
		try {
			ConnectionHandler.closeConnection(connection);
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

}
