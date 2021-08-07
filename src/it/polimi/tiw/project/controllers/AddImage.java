package it.polimi.tiw.project.controllers;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.sql.Connection;
import java.util.ArrayList;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.annotation.MultipartConfig;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.tomcat.util.codec.binary.Base64;

import com.google.gson.Gson;

import it.polimi.tiw.project.beans.UploadedFiles;
import it.polimi.tiw.project.dao.CategoriesDAO;
import it.polimi.tiw.project.utils.ConnectionHandler;
import it.polimi.tiw.project.utils.SimpleResponse;


/**
 * Servlet implementation class AddImage
 */
@WebServlet("/AddImage")
@MultipartConfig
public class AddImage extends HttpServlet {
	private static final long serialVersionUID = 1L;
	private Connection connection;
	String folderPath = "";
       
    /**
     * @see HttpServlet#HttpServlet()
     */
    public AddImage() {
        super();
        // TODO Auto-generated constructor stub
    }
    
    public void init() throws ServletException {
		connection = ConnectionHandler.getConnection(getServletContext());
        folderPath = System.getenv("outputpath");
	}    

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		SimpleResponse customResponse = new SimpleResponse();
		UploadedFiles params = new Gson().fromJson(request.getReader(), UploadedFiles.class);
		Integer fatherId = params.getFatherId();
		List<String> files = params.getFiles();
		List<String> fileNames = params.getFileNames();
		List<String> fileNamesBack = new ArrayList<String>();
		
		if (fatherId == null || files == null || files.isEmpty() || fileNames == null || fileNames.isEmpty() || files.size() != fileNames.size()) {
			customResponse.setAndSendResponse(response, HttpServletResponse.SC_BAD_REQUEST, "Missing or incorrect parameters.");
			//ErrorDispatcher.forward(getServletContext(), request, response, HttpServletResponse.SC_BAD_REQUEST, "Missing or incorrect parameters.");
			return;
		}
		
		CategoriesDAO service = new CategoriesDAO(connection);
		int res = -1;
		try {
			res = service.checkCategoryExists(fatherId);
		} catch (Exception e) {
			customResponse.setAndSendResponse(response, HttpServletResponse.SC_INTERNAL_SERVER_ERROR, e.getMessage());
			return;
		}
		
		if (res == -1) {
			customResponse.setAndSendResponse(response, HttpServletResponse.SC_BAD_REQUEST, "Missing or incorrect parameters.");
			return;
		}
		
		for (String f : files) {
			if (!f.contains("image/jpeg;base64,")) {
				customResponse.setAndSendResponse(response, HttpServletResponse.SC_BAD_REQUEST, "Missing or incorrect parameters.");
				//ErrorDispatcher.forward(getServletContext(), request, response, HttpServletResponse.SC_BAD_REQUEST, "Missing or incorrect parameters.");
				return;
			}
		}
		
		for (String f : fileNames) {
			File temp = new File(folderPath + fatherId + "--" + f);
			if (temp.exists()) {
				customResponse.setAndSendResponse(response, HttpServletResponse.SC_BAD_REQUEST, "File already exists.");
				return;
			}
		}
		
		// save each file with the name as: fatherId--fileName, ex. "7--Giove.jpg"
		for (int i = 0; i < files.size(); i++) {
			String outputPath = folderPath + fatherId + "--" + fileNames.get(i);
			byte[] data = Base64.decodeBase64(files.get(i).substring(files.get(i).indexOf(",")));
			try (OutputStream stream = new FileOutputStream(outputPath)) {
			    stream.write(data);
			} catch (Exception e) {
				e.printStackTrace();
				customResponse.setAndSendResponse(response, HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Error while saving a file.");
				return;
			}
			fileNamesBack.add(fatherId + "--" + fileNames.get(i));
		}

		customResponse.setAndSendResponse(response, HttpServletResponse.SC_OK, new Gson().toJson(fileNamesBack));
		
	}
}
