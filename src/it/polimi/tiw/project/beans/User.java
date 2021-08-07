package it.polimi.tiw.project.beans;

/**
 * A <CODE>User</CODE> is identified with a unique <CODE>id</CODE> and a unique <CODE>username</CODE>. It also has an hashed <CODE>password</CODE>
 */
public class User {
    private int id;
    private String username;
    private String password;

	/* GETTERS */
	
	/**
	 * Getter for <CODE>id</CODE>
	 * @return the <CODE>id</CODE> of the users
	 */
    public int getId() {
        return id;
    }
	
	/**
	 * Getter for <CODE>username</CODE>
	 * @return the <CODE>username</CODE> of the users
	 */
    public String getUsername() {
        return username;
    }

	/**
	 * Getter for <CODE>password</CODE>
	 * @return the <CODE>password</CODE> of the users
	 */
    public String getPassword() {
        return password;
    }
	
	/* SETTERS */
	
	/**
	 * Setter for <CODE>id</CODE>
	 * @param id	<CODE>id</CODE> of the user
	 */
    public void setId(int id) {
        this.id = id;
    }

	/**
	 * Setter for <CODE>username</CODE>
	 * @param username	<CODE>username</CODE> of the user
	 */
    public void setUsername(String username) {
        this.username = username;
    }

	/**
	 * Setter for <CODE>password</CODE>
	 * @param password	<CODE>password</CODE> of the user
	 */
    public void setPassword(String password) {
        this.password = password;
    }
}
