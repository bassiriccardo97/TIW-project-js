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
 * Servlet implementation class DownloadImage
 */
@WebServlet("/DownloadImage")
public class DownloadImage extends HttpServlet {
	private static final long serialVersionUID = 1L;
	String folderPath = "";
       
    /**
     * @see HttpServlet#HttpServlet()
     */
    public DownloadImage() {
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
		String name = null;
		
		name = request.getParameter("filename");
		if (name == null || name.isEmpty()) {
			customResponse.setAndSendResponse(response, HttpServletResponse.SC_BAD_REQUEST, "Missing or incorrect parameters.");
			return;
		}
		
		File file = new File(folderPath, name);

		if (!file.exists() || file.isDirectory()) {
			customResponse.setAndSendResponse(response, HttpServletResponse.SC_BAD_REQUEST, "Missing or incorrect parameters.");
			return;
		}
		
		response.setHeader("Content-Type", getServletContext().getMimeType(name));
		response.setHeader("Content-Length", String.valueOf(file.length()));
		
		response.setHeader("Content-Disposition", "attachment; filename=\"" + file.getName() + "\"");
																									
		Files.copy(file.toPath(), response.getOutputStream());
		
		customResponse.setAndSendResponse(response, HttpServletResponse.SC_OK, "Download started.");

	}

}
