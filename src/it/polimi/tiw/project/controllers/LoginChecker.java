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
 * Servlet implementation class LoginChecker
 */
@WebServlet("/LoginChecker")
public class LoginChecker extends HttpServlet {
  private static final long serialVersionUID = 1L;
  private Connection connection = null;

  public LoginChecker() {
    super();
  }

  public void init() throws ServletException {
    connection = ConnectionHandler.getConnection(getServletContext());
  }

  protected void doPost(HttpServletRequest request, HttpServletResponse response)
      throws ServletException, IOException {
    SimpleResponse customResponse = new SimpleResponse();
    User user = new Gson().fromJson(request.getReader(), User.class);
    String username = user.getUsername();
    String password = user.getPassword();
    
    // Checks if there are all parameters
    if (username == null || password == null || username.isEmpty() || password.isEmpty()) {
    	customResponse.setAndSendResponse(response, HttpServletResponse.SC_BAD_REQUEST, "Credentials must not be null.");
      	return;
    }

    // Query db and checks password
    UserDAO userDao = new UserDAO(connection);
    int userID;
    try {  
		MessageDigest digest = MessageDigest.getInstance("SHA-256");
		byte[] passwordHash = digest.digest(password.getBytes(StandardCharsets.UTF_8));
		userID = userDao.checkCredentials(username, passwordHash);
    } catch (Exception e) {
      	customResponse.setAndSendResponse(response, HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Internal error, change creds or retry later.");
      	return;
    }

    // Check if user exists and the password is correct
    if (userID == -1) {
    	customResponse.setAndSendResponse(response, HttpServletResponse.SC_UNAUTHORIZED, "Incorrect credentials.");
    	return;
    } else {
    	request.getSession().setAttribute("user", user);
    	customResponse.setAndSendResponse(response, HttpServletResponse.SC_OK, username);
    	return;
    }
  }

  public void destroy() {
    try {
      ConnectionHandler.closeConnection(connection);
    } catch (SQLException e) {
      e.printStackTrace();
    }
  }

}
