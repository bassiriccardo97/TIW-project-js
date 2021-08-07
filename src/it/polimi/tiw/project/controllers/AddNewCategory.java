package it.polimi.tiw.project.controllers;

import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import com.google.gson.Gson;
import it.polimi.tiw.project.beans.Category;
import it.polimi.tiw.project.dao.CategoriesDAO;
import it.polimi.tiw.project.utils.ConnectionHandler;
import it.polimi.tiw.project.utils.SimpleResponse;

/**
 * Servlet implementation class AddNewCategory
 */
@WebServlet("/AddNewCategory")
public class AddNewCategory extends HttpServlet {
	private static final long serialVersionUID = 1L;
	private Connection connection;
       
    /**
     * @see HttpServlet#HttpServlet()
     */
    public AddNewCategory() {
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
	    Category newCategory = new Gson().fromJson(request.getReader(), Category.class);
		String name = newCategory.getName();
		int fatherId = newCategory.getFatherId();
		Integer res = null;

	    if (name == null || name.isEmpty()) { // If the fatherId param it's not passed it will be default 0
	    	customResponse.setAndSendResponse(response, HttpServletResponse.SC_BAD_REQUEST, "Missing or incorrect parameters.");
	    	return;
	    }
	    
	    CategoriesDAO categoriesDAO = new CategoriesDAO(connection);
	    try {
	    	res = categoriesDAO.createCategory(name, fatherId);
	    } catch (Exception e) {
	    	customResponse.setAndSendResponse(response, HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Error in creating new category.");
	    	return;
	    };
	    
	    if (res == null || res == -1) {
	    	customResponse.setAndSendResponse(response, HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Error in creating new category.");
	    	return;
	    }
	    
	    customResponse.setAndSendResponse(response, HttpServletResponse.SC_OK, res.toString());
		
	}
	
	
	public void destroy() {
		try {
			ConnectionHandler.closeConnection(connection);
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

}
