package it.polimi.tiw.project.controllers;

import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import it.polimi.tiw.project.dao.CategoriesDAO;
import it.polimi.tiw.project.utils.ConnectionHandler;
import it.polimi.tiw.project.utils.SimpleResponse;
/**
 * Servlet implementation class GetCategories
 */
@WebServlet("/GetCategories")
public class GetCategories extends HttpServlet {
	private static final long serialVersionUID = 1L;
	private Connection connection;

	/**
	 * @see HttpServlet#HttpServlet()
	 */
	public GetCategories() {
		super();
		// TODO Auto-generated constructor stub
	}

	
	public void init() throws ServletException {
		connection = ConnectionHandler.getConnection(getServletContext());
	}
	

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
	    String categoriesJson = null;
	    SimpleResponse customResponse = new SimpleResponse();
	    CategoriesDAO cDAO = new CategoriesDAO(connection);
	    try {
	    	categoriesJson = cDAO.getCategoriesJson();
	    } catch (SQLException e) {
	    	customResponse.setAndSendResponse(response, HttpServletResponse.SC_INTERNAL_SERVER_ERROR, e.getMessage());
	    	return;
	    }
	    
	    if (categoriesJson == null) {
	        customResponse.setAndSendResponse(response, HttpServletResponse.SC_UNAUTHORIZED, "Incorrect request.");
	        return;
	      } else {
	        customResponse.setAndSendResponse(response, HttpServletResponse.SC_OK, categoriesJson);
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
