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
 * Servlet implementation class RenameCategoryPerform
 */
@WebServlet("/RenameCategory")
public class RenameCategory extends HttpServlet {
	private static final long serialVersionUID = 1L;
	private Connection connection;
       
    /**
     * @see HttpServlet#HttpServlet()
     */
    public RenameCategory() {
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
		Integer catId = newCategory.getId();
		String name = newCategory.getName();
		
		if (catId == null || catId <= 0 || name == null || name.isEmpty()) {
			customResponse.setAndSendResponse(response, HttpServletResponse.SC_BAD_REQUEST, "Missing or incorrect parameters.");
			return;
		}
		
		CategoriesDAO service = new CategoriesDAO(connection); 
		try {
			service.renameCategory(catId, name);
		} catch (Exception e) {
	        customResponse.setAndSendResponse(response, HttpServletResponse.SC_INTERNAL_SERVER_ERROR, e.getMessage());
			return;
		}
		
        customResponse.setAndSendResponse(response, HttpServletResponse.SC_OK, "Rename ok.");
	}
	
	
	public void destroy() {
		try {
			ConnectionHandler.closeConnection(connection);
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

}
