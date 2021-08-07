package it.polimi.tiw.project.beans;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.regex.Pattern;

/**
 * A <CODE>Category</CODE> is identified with a unique <CODE>id</CODE> and a unique <CODE>name</CODE>. It has an <CODE>index</CODE> related to its position in the tree structure and has a <CODE>fatherId</CODE> which is the <CODE>id</CODE> of its father
 */
public class Category {
	private int id;
	private String name;
	private String index;
	private int fatherId;


	/* GETTERS */
	
	/**
	 * Getter for category <CODE>id</CODE>
	 * @return category id
	 */
	public int getId() {
		return id;
	}
	
	/**
	 * Getter for category <CODE>name</CODE>
	 * @return category name
	 */
	public String getName() {
		return name;
	}
	
	/**
	 * Gets the category <CODE>name</CODE> encoded as URL
	 * @return URL encoded category <CODE>name</CODE>
	 * @throws UnsupportedEncodingException
	 */
	public String getEncodedName() throws UnsupportedEncodingException {
		return URLEncoder.encode(name, "UTF-8");
	}

	/**
	 * Getter for category <CODE>index</CODE>
	 * @return category index
	 */
	public String getIndex() {
		return this.index;
	}

	/**
	 * Getter for category <CODE>fatherId</CODE>
	 * @return category <CODE>fatherId</CODE>
	 */
	public int getFatherId() {
		return fatherId;
	}
	
	/* SETTERS */

	/**
	 * Setter for category <CODE>id</CODE>
	 * @param id	category id
	 */
	public void setId(int id) {
		this.id = id;
	}

	/**
	 * Setter for category <CODE>name</CODE>
	 * @param name	category <CODE>name</CODE>
	 */
	public void setName(String name) {
		this.name = name;
	}
	
	/**
	 * Setter for category <CODE>index</CODE>
	 * @param index		category <CODE>index</CODE>
	 */
	public void setIndex(String index) {
		this.index = index;
	}

	/**
	 * Setter for category <CODE>fatherId</CODE>
	 * @param fatherId	category <CODE>fatherId</CODE>
	 */
	public void setFatherId(int fatherId) {
		this.fatherId = fatherId;
	}

	/**
	 * Gets the category <CODE>index</CODE> to be printed in the HTML
	 * @return category <CODE>index</CODE> to be printed
	 */
	public String getIndexToPrint() {
		/* 
		 * indexes in db start from 0 and are single integers.
		 * in category object, each category has the index in form of 1.1.1 as string, created considering the index of the father, adding '.' and the index of the current category: father has index 1.1 so its first child has index 1.1.0.
		 * indexes to print must start from 1 (not 0), so the single chars must be incremented of 1: 0.0 becomes 1.1 
		 */
		String indexToPrint = "";
		String indexTmp = this.index;
		if (!indexTmp.contains(".")) {
			Integer t = Integer.parseInt(this.index);
			t++;
			return t.toString();
		}
		String[] indexes = indexTmp.split(Pattern.quote("."));
		for (int i = 0; i < indexes.length; i++) {
			Integer t = Integer.parseInt(indexes[i]);
			t++;
			indexToPrint += t.toString();
			if (i < indexes.length - 1) {
				indexToPrint += ".";
			}
		}
		return indexToPrint;
	}

}
