package it.polimi.tiw.project.controllers;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import it.polimi.tiw.project.utils.SimpleResponse;


/**
 * Servlet implementation class ShowImage
 */
@WebServlet("/ShowImage")
public class ShowImage extends HttpServlet {
	private static final long serialVersionUID = 1L;
	String folderPath = "";
       
    /**
     * @see HttpServlet#HttpServlet()
     */
    public ShowImage() {
        super();
        // TODO Auto-generated constructor stub
    }

    public void init() throws ServletException {
		folderPath = System.getenv("outputpath");
    }

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
	    SimpleResponse customResponse = new SimpleResponse();
	    String filename = request.getParameter("filename");
	    
		if (filename == null || filename.isEmpty()) {
	        customResponse.setAndSendResponse(response, HttpServletResponse.SC_BAD_REQUEST, "Missing or incorrect parameters.");
			return;
		}

		String outputPath = folderPath + filename;
		File file = new File(outputPath);

		if (!file.exists() || file.isDirectory()) {
			customResponse.setAndSendResponse(response, HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Failed to retrieve the file.");
			return;
		}

		response.setHeader("Content-Type", getServletContext().getMimeType(filename));
		response.setHeader("Content-Length", String.valueOf(file.length()));
		
		response.setHeader("Content-Disposition", "inline; filename=\"" + file.getName() + "\"");
		response.setCharacterEncoding("UTF-8");
		Files.copy(file.toPath(), response.getOutputStream());
	}

}
