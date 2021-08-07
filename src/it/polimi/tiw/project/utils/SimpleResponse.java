package it.polimi.tiw.project.utils;

import java.io.IOException;
import javax.servlet.http.HttpServletResponse;
import com.google.gson.Gson;

/**
 * The <CODE>SimpleResponse</CODE> contains information about the status of the server response and the message to return to the client 
 */
public class SimpleResponse {
	private int status;
	private String message;
  
	/* GETTERS */
	
	/**
	 * Getter for <CODE>message</CODE>
	 * @return the message string (often a <CODE>JSON</CODE>)
	 */
	public String getMessage() {
		return message;
	}

	/**
	 * Getter for the response <CODE>status</CODE>
	 * @return the status
	 */
	public int getStatus() {
		return status;
	}
	
	/* SETTERS */
	
	/**
	 * Setter for the <CODE>message</CODE>
	 * @param message	the message <CODE>string</CODE>
	 */
	public void setMessage(String message) {
		this.message = message;
	}
	
	/**
	 * Setter for the response <CODE>status</CODE>
	 * @param status	the status
	 */
	public void setStatus(int status) {
		this.status = status;
	}
  
	/**
	 * Sets the <CODE>status</CODE> and the <CODE>message</CODE>, then sends itself in <CODE>JSON</CODE> format to the client
	 * @param response	the response
	 * @param status	the response status
	 * @param message	the response message
	 * @throws IOException
	 */
	public void setAndSendResponse(HttpServletResponse response, int status, String message) throws IOException {
	    this.setMessage(message);
	    this.setStatus(status);
	    response.setStatus(status);
	    response.setContentType("application/json");
	    response.setCharacterEncoding("UTF-8");
	    String responseBody = new Gson().toJson(this);
	    response.getWriter().println(responseBody);
	}
}
