package it.polimi.tiw.project.controllers;

import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import com.google.gson.Gson;
import it.polimi.tiw.project.beans.ChangesHandler;
import it.polimi.tiw.project.dao.CategoriesDAO;
import it.polimi.tiw.project.utils.ConnectionHandler;
import it.polimi.tiw.project.utils.SimpleResponse;

/**
 * Servlet implementation class HandleChanges
 */
@WebServlet("/MoveCategories")
public class MoveCategories extends HttpServlet {
	private static final long serialVersionUID = 1L;
	private Connection connection;
       
    /**
     * @see HttpServlet#HttpServlet()
     */
    public MoveCategories() {
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
		ArrayList<Integer> catIdToMove = new ArrayList<Integer>();
		ArrayList<Integer> newFatherId = new ArrayList<Integer>();
		SimpleResponse customResponse = new SimpleResponse();
		ChangesHandler[] newChanges = new Gson().fromJson(request.getReader(), ChangesHandler[].class);
		for (ChangesHandler el : newChanges) {
			int sourceId = el.getSourceId();
			int destinationId = el.getDestinationId();
			if (sourceId <= 0 || destinationId <= 0) {
				customResponse.setAndSendResponse(response, HttpServletResponse.SC_BAD_REQUEST, "Incorrect parameters.");
				return;
			}
		}
		
		// Executing operation
		for (ChangesHandler el : newChanges) {
			catIdToMove.add(el.getSourceId());
			newFatherId.add(el.getDestinationId());
		}
		
		CategoriesDAO cDAO = new CategoriesDAO(connection);
		try {
			cDAO.moveCategory(catIdToMove, newFatherId);
		} catch (SQLException e) {
			customResponse.setAndSendResponse(response, HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Failed to update categories.");
			return;
		} catch (Exception e) {
			customResponse.setAndSendResponse(response, HttpServletResponse.SC_BAD_REQUEST, "Something went wrong moving categories.");
			return;
		}
		
		customResponse.setAndSendResponse(response, HttpServletResponse.SC_OK, "Ok");
		
	}

}
