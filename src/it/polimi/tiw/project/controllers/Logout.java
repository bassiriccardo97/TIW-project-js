package it.polimi.tiw.project.controllers;

import java.io.IOException;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import it.polimi.tiw.project.utils.SimpleResponse;

/**
 * Servlet implementation class Logout
 */
@WebServlet("/Logout")
public class Logout extends HttpServlet {
	private static final long serialVersionUID = 1L;
       
    /**
     * @see HttpServlet#HttpServlet()
     */
    public Logout() {
        super();
        // TODO Auto-generated constructor stub
    }

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
	    SimpleResponse customResponse = new SimpleResponse();
		HttpSession session = request.getSession(false);
	    
		// set attribute user = null in order to maintain the session and the language set, so the login page is in the last language selected
	    session.setAttribute("user", null);
		customResponse.setAndSendResponse(response, HttpServletResponse.SC_OK, "User logged out.");
	}

}
