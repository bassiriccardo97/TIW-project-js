package it.polimi.tiw.project.controllers;

import java.io.File;
import java.io.IOException;
import java.sql.Connection;
import java.text.Collator;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.google.gson.Gson;

import it.polimi.tiw.project.dao.CategoriesDAO;
import it.polimi.tiw.project.utils.ConnectionHandler;
import it.polimi.tiw.project.utils.SimpleResponse;

/**
 * Servlet implementation class ImagesList
 */
@WebServlet("/ImagesList")
public class ImagesList extends HttpServlet {
    private static final long serialVersionUID = 1L;
	private Connection connection;
    String folderPath = "";
       
    /**
     * @see HttpServlet#HttpServlet()
     */
    public ImagesList() {
        super();
        // TODO Auto-generated constructor stub
    }

    public void init() throws ServletException {
		connection = ConnectionHandler.getConnection(getServletContext());
		folderPath = System.getenv("outputpath");
    }

    /**
     * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
     */
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		String imagesListJson = null;
	    SimpleResponse customResponse = new SimpleResponse();
		List<String> images = new ArrayList<String>();
			
		String catId = request.getParameter("category-id");
		if (catId == null || catId.isEmpty()) {
			customResponse.setAndSendResponse(response, HttpServletResponse.SC_BAD_REQUEST, "Missing or incorrect parameters.");
			return;
		}
		
		try {
			if (Integer.parseInt(catId) <= 0) {
				customResponse.setAndSendResponse(response, HttpServletResponse.SC_BAD_REQUEST, "Missing or incorrect parameters.");
				return;
			}
		} catch (NumberFormatException nfe) {
			customResponse.setAndSendResponse(response, HttpServletResponse.SC_BAD_REQUEST, "Missing or incorrect parameters.");
			return;
		}
		
		CategoriesDAO service = new CategoriesDAO(connection);
		int res = -1;
		try {
			res = service.checkCategoryExists(Integer.valueOf(catId));
		} catch (Exception e) {
			customResponse.setAndSendResponse(response, HttpServletResponse.SC_INTERNAL_SERVER_ERROR, e.getMessage());
			return;
		}
		
		if (res == -1) {
			customResponse.setAndSendResponse(response, HttpServletResponse.SC_BAD_REQUEST, "The requested category does not exists.");
			return;
		}
		
		// collect in a list all the file names related to the category (the ones which name starts with the id of the category)
		File folder = new File(folderPath);
		File[] files = folder.listFiles();
		for (File file : files) {
		    String folderFileName = file.getName();
		    if ((folderFileName.endsWith(".jpg") || folderFileName.endsWith(".jpeg")) && folderFileName.substring(0, folderFileName.indexOf("--")).equals(catId.toString())) {
		    	images.add(folderFileName);
		    }
		}
		
		// sort the file names in alphabetical order (depends on the language set)
		images.sort(Collator.getInstance(Locale.ENGLISH));
		
		Gson gson = new Gson();
		imagesListJson = gson.toJson(images);
	
		if (imagesListJson == null) {
	        customResponse.setAndSendResponse(response, HttpServletResponse.SC_UNAUTHORIZED, "Incorrect request.");
	        return;
		} else {
	        customResponse.setAndSendResponse(response, HttpServletResponse.SC_OK, imagesListJson);
	        return;
		}
    }
}
