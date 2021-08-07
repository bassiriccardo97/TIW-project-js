package it.polimi.tiw.project.beans;

/**
 * The <CODE>ChangesHandler</CODE> contains information about a move or a delete of a category, in particular: the <CODE>sourceId</CODE> (id of the category to move or delete) and the <CODE>destinationId</CODE> (id of the new father in case of a "move")
 */
public class ChangesHandler {
	private int sourceId;
	private int destinationId;
	  
	/* GETTERS */
	
	/**
	 * Getter for <CODE>sourceId</CODE>, the id of the category to move or delete
	 * @return category id
	 */
	public int getSourceId() {
		return sourceId;
	}
	
	/**
	 * Getter for <CODE>destinationId</CODE>, the id of the new father, in case of a "move"
	 * @return category id
	 */
	public int getDestinationId() {
		return destinationId;
	}
	
	/* SETTERS */

	/**
	 * Setter for <CODE>sourceId</CODE>
	 * @param sourceId	the id of the category to move or delete
	 */
	public void setSourceId(int sourceId) {
		this.sourceId = sourceId;
	}

	/**
	 * Setter for <CODE>destinationId</CODE>
	 * @param destinationId	the id of the new father, in case of a "move"
	 */
	public void setDestinationId(int destinationId) {
		this.destinationId = destinationId;
	}
}
