package it.polimi.tiw.project.beans;

import java.util.ArrayList;
import java.util.List;

/**
 * <CODE>UploadedFiles</CODE> contains all the <CODE>files</CODE> and related <CODE>fileNames</CODE> and also the <CODE>fatherId</CODE>, which is the id of the category in which files are stored 
 */
public class UploadedFiles {
	
	private int fatherId;
	private List<String> fileNames = new ArrayList<String>();
	private List<String> files = new ArrayList<String>();
	
	/* GETTERS */
	
	/**
	 * Getter for <CODE>fatherId</CODE>
	 * @return the id of the category in which files are stored
	 */
	public int getFatherId() {
		return fatherId;
	}
	
	/**
	 * Getter for <CODE>files</CODE>
	 * @return the list of files (base64 encoded)
	 */
	public List<String> getFiles() {
		return files;
	}

	/**
	 * Getter for <CODE>fileNames</CODE>
	 * @return the list of file names
	 */
	public List<String> getFileNames() {
		return fileNames;
	}
	
	/* SETTERS */
	
	/**
	 * 
	 * @param fatherId
	 */
	public void setFatherId(int fatherId) {
		this.fatherId = fatherId;
	}
	
	/**
	 * Setter for <CODE>files</CODE>
	 * @param files	a list of file (base64 encoded)
	 */
	public void setFiles(List<String> files) {
		this.files = files;
	}

	/**
	 * Setter for <CODE>fileNames</CODE>
	 * @param fileNames	a list of file names
	 */
	public void setFileNames(List<String> fileNames) {
		this.fileNames = fileNames;
	}
}
