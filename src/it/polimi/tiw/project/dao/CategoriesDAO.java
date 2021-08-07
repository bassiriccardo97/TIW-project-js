package it.polimi.tiw.project.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import com.google.gson.Gson;

import it.polimi.tiw.project.beans.Category;

/**
 * DAO for categories
 */
public class CategoriesDAO {
	private Connection connection;

	/**
	 * Constructor for <CODE>CategoriesDAO</CODE>
	 * @param connection	the <CODE>connection</CODE> to the database
	 */
	public CategoriesDAO(Connection connection) {
		this.connection = connection;
	}

	/**
	 * Retrieves the whole list of categories in the database
	 * @return the list of categories
	 * @throws SQLException
	 */
	private List<Category> getCategories() throws SQLException {
		List<Category> categories = new ArrayList<Category>();
		String query = "SELECT * FROM category";
		ResultSet result = null;
		PreparedStatement pStatement = null;
		try {
			pStatement = connection.prepareStatement(query);
			ResultSet rs = pStatement.executeQuery();
			while (rs.next()) {
				Category c = new Category();
				c.setId(rs.getInt("category_id"));
				c.setName(rs.getString("name"));
				c.setIndex(rs.getString("index"));
				c.setFatherId(Integer.parseInt(rs.getString("father_id")));
				categories.add(c);
			}
		} catch (SQLException e) {
			throw new SQLException("Failed to retrieve the categories.");
		} finally {
			try {
				if (result != null) {
					result.close();
				}
			} catch (Exception e1) {
				throw new SQLException("Cannot close result.");
			}
			try {
				if (pStatement != null) {
					pStatement.close();
				}
			} catch (Exception e1) {
				throw new SQLException("Cannot close statement.");
			}
		}

		return categories;
	}
	
	/**
	 * Retrieve the list of categories in <CODE>JSON</CODE> format
	 * @return	 list of category as <CODE>JSON</CODE>
	 * @throws SQLException
	 */
	public String getCategoriesJson() throws SQLException {
		List<Category> categories = this.getCategories();
		Gson gson = new Gson();
		String categoriesJson = gson.toJson(categories);
		
		return categoriesJson;
	}

	/**
	 * Adds a new category in the database
	 * @param name	the new category's name
	 * @param fatherId	the father category of the new category
	 * @return the id of the new category if successfully added, -1 otherwise
	 * @throws SQLException
	 */
	public int createCategory(String name, int fatherId) throws SQLException {
		String query = "INSERT INTO category (name,`index`, father_id) VALUES (?, ?, ?)";
		String query1 = "SELECT category_id FROM category WHERE name = ?";
		int res = -1;
		
		connection.setAutoCommit(false);
		PreparedStatement pStatement = null;
		PreparedStatement pStatement1 = null;
		try {
			int index = getNextIndex(fatherId);
			pStatement = connection.prepareStatement(query);
			pStatement.setString(1, name);
			pStatement.setInt(2, index);
			pStatement.setInt(3, fatherId);
			pStatement.executeUpdate();
			
			pStatement1 = connection.prepareStatement(query1);
			pStatement1.setString(1, name);
			ResultSet rs = pStatement1.executeQuery();
			if (rs.isBeforeFirst()) {
				rs.next();
	  			res = rs.getInt("category_id");
	  			connection.commit();
			} else {
				throw new SQLException("Failed to add new category.");
			}
  		} catch (SQLException e) {
  			connection.rollback();
  			throw new SQLException("Failed to add new category.");
  		} finally {
  			try {
  				pStatement.close();
  				pStatement1.close();
  			} catch (Exception e1) { }
  		}
		return res;
	}
  
	/**
	 * Gets the next index for a new child of a father
	 * @param fatherId	the father id for which calculate the next index for a new child 
	 * @return the next index to assign
	 * @throws SQLException
	 */
	private int getNextIndex(int fatherId) throws SQLException {
		int count = 0;
		String query = "SELECT * FROM category WHERE father_id = ?";
		try {
			PreparedStatement pStatement = connection.prepareStatement(query);
			pStatement.setInt(1, fatherId);
			ResultSet result = pStatement.executeQuery();
			while (result.next()) {
				count++;
			}
		} catch (SQLException e) {
			throw new SQLException(e);
		}
		return count;
	}
	
	/**
	 * Applies all the moves required as a transaction
	 * @param catIds		list of all the categories' id to move
	 * @param newFatherIds	list of all the new fathers' category ids
	 * @throws SQLException
	 * @throws Exception
	 */
	public void moveCategory(List<Integer> catIds, List<Integer> newFatherIds) throws SQLException, Exception {
		
		// TODO useful to test error messages sent in JSON
		/*for (Integer i : catIds) {
			if (Collections.frequency(catIds, i) > 1) {
				//throw new Exception("Failed to move the categories.");	NOT WORKING --> json problems ???
				return;
			}
		}*/
  		
  		List<Category> categories = this.getCategories();
  		ArrayList<Integer> fathers;
	
  		List<Category> categoriesToMove = new ArrayList<Category>();
  		List<Category> oldFathers = new ArrayList<Category>();

  		List<Integer> oldFatherIds = new ArrayList<Integer>();
  		boolean next = false;
  		
  		// creates the list of the old father ids
  		for (Integer id : catIds) {
  			if (!next) {
	  			for (Category c : categories) {
	  				if (c.getId() == id) {
	  					oldFatherIds.add(c.getFatherId());
	  					next = true;
	  				}
	  			}
	  			next = false;
  			}
  		}
  		
  		// if the sizes of the list of ids do not coincide throws an exception
  		if (catIds.size() != oldFatherIds.size() || catIds.size() != newFatherIds.size() || oldFatherIds.size() != newFatherIds.size()) {
  			throw new Exception("Failed to move categories.");
  		}
  		
  		// for each category to move id creates a list of the categories to move and a list of the related old father categories
  		// it also counts if for each category to move is present the category itself, the old father and the new father in the categories list (count = 3)
  		for (int i = 0; i < catIds.size(); i++) {
  			int count = 0;
  			for (int j = 0; j < categories.size(); j++) {
  				if (catIds.get(i) != 0) {
	  				if (categories.get(j).getId() == catIds.get(i)) {
	  					categoriesToMove.add(categories.get(j));
	  					count++;
	  				}
  				} else {
  					Category root = new Category();
  					root.setId(0);
  					categoriesToMove.add(root);
  					count++;
  				}
  				
  				if (oldFatherIds.get(i) != 0) {
	  				if (categories.get(j).getId() == oldFatherIds.get(i)) {
	  					oldFathers.add(categories.get(j));
	  					count++;
	  				}
  				} else {
  					Category root = new Category();
  					root.setId(0);
  					oldFathers.add(root);
  					count++;
  				}
  				
  				if (categories.get(j).getId() == newFatherIds.get(i)) {
  					count++;
  				}
  				// if the whole categories list has been visited and the count is less then 3, some category is missing and throws an exception
  				if (j == categories.size() - 1 && count < 3) {
  					throw new Exception("Failed to move the categories.");
  				}
  			}
  		}
  		
  		// retrieves the recursive fathers of the potential new father and checks that the category to move is not a father of the new father
  		for (int i = 0; i < catIds.size(); i++) {
  			fathers = this.getFathersListOfChild(categories, newFatherIds.get(i));
  			if (fathers.contains(catIds.get(i))) {
  				throw new Exception("Failed to move the categories.");
  			}
  		}
  		
  		try {
  			movePerform(categories, categoriesToMove, oldFathers, newFatherIds);
  		} catch (SQLException e) {
  			throw new SQLException("Failed to move the category.");
  		} catch (Exception e) {
  			throw new Exception("Invalid action.");
  		}
		
  	}
	
	/**
	 * Performs all the moves requested
	 * @param categories		list of the categories on the database 
	 * @param categoriesToModify		list of the categories to move
	 * @param oldFathers		list of the old fathers
	 * @param newFatherIds	list of the new fathers' id
	 * @throws SQLException
	 * @throws Exception
	 */
	private void movePerform(List<Category> categories, List<Category> categoriesToModify, List<Category> oldFathers, List<Integer> newFatherIds) throws SQLException, Exception {
		
		// updates the list of all the categories with the locally performed move
		for (int i = 0; i < categoriesToModify.size(); i++) {
			categories = updateCategories(categories, categoriesToModify.get(i), newFatherIds.get(i));
		}

  		String query = "UPDATE category SET father_id = ?, `index` = ? WHERE category_id = ?;";
  		PreparedStatement pstatement = null;
  		connection.setAutoCommit(false);
		
  		try {
  			// for each category to move, search it in the list of all categories and pushes the changed values on the database
  			// then for each of the children of the old father pushes the changed values
  			for (int i = 0; i < categoriesToModify.size(); i++) {
  				for (Category c : categories) {
  					if (c.getId() == categoriesToModify.get(i).getId()) {
  						pstatement = connection.prepareStatement(query);
  						pstatement.setInt(1, c.getFatherId());
  						pstatement.setInt(2, Integer.parseInt(c.getIndex()));
  						pstatement.setInt(3, c.getId());
  						pstatement.executeUpdate();
  					}
  					if (c.getId() == oldFathers.get(i).getId()) {
  						List<Category> children = categoryChildren(categories, c.getId());
  						for (Category child : children) {
  							pstatement = connection.prepareStatement(query);
  	  						pstatement.setInt(1, child.getFatherId());
  	  						pstatement.setInt(2, Integer.parseInt(child.getIndex()));
  	  						pstatement.setInt(3, child.getId());
  	  						pstatement.executeUpdate();
  						}
  					}
  				}
  			}
  			connection.commit();
  		} catch (SQLException e) {
  			connection.rollback();
  			throw new SQLException();
  		} finally {
  			try {
  				pstatement.close();
  			} catch (Exception e1) { }
  		}
	}
	
	/**
	 * Updates the list of all categories locally with the requested move one at time
	 * @param categories		the list of all the categories
	 * @param cat	the category to move
	 * @param newFatherId	the new father of the category
	 * @return the updated list of the categories
	 * @throws Exception
	 */
	private List<Category> updateCategories(List<Category> categories, Category cat, Integer newFatherId) throws Exception{
		
		if (newFatherId == 0) {
			throw new Exception("Cannot move a category to root");
		}
		
		boolean reorder = true;
		// if a category is moved to its current father, the index will be incremented, but then decremented thanks to this flag
		if (cat.getFatherId() != newFatherId) {
			reorder = false;
		}

		int catIndex = Integer.parseInt(cat.getIndex());
		int j = -1;
		int newIndex = -1;
		Category newFather = null;
		// visits the whole list of categories and applies the changes according to the requested move:
		// 		- when it finds the category to move, stores in j its index in the list
		// 		- when it finds the new father, stores the category
		// 		- when it finds a category that is child of the new father, if its category index is bigger or equal to the newIndex (initialized to -1) for the category to move, then updates the newIndex with the value of the category index + 1
		//		- when it finds a category that is child of the old father and its category index is bigger than the category index of the category to move, then decrements the category index of 1
		for (int i = 0; i < categories.size(); i++) {
			int ind = Integer.parseInt(categories.get(i).getIndex());
			if (categories.get(i).getId() == cat.getId()) {
				j = i;
			}
			if (categories.get(i).getId() == newFatherId) {
				newFather = categories.get(i);
			}
			if (categories.get(i).getFatherId() == newFatherId && Integer.valueOf(categories.get(i).getIndex()) >= newIndex) {
				newIndex = Integer.valueOf(categories.get(i).getIndex()) + 1;
			}
			if (categories.get(i).getFatherId() == cat.getFatherId() && ind > catIndex) {
				ind--;
				categories.get(i).setIndex(String.valueOf(ind));
			}
		}
		
		// j = -1 means that the category to move is not present in the list of all categories
		// same for newFather = null
		if (j == -1 || newFather == null) {
			throw new Exception("Failed to move category.");
		}
		
		// newIndex = -1 means that the new father had no children, so the newIndex must be 0
		if (newIndex == -1) {
			newIndex = 0;
		} else if (reorder) {
			newIndex--;
		}
		
		// applies the changes for the category to move, in the list
		categories.get(j).setFatherId(newFather.getId());
		categories.get(j).setIndex(String.valueOf(newIndex));
		
		return categories;
	}
	
	private ArrayList<Category> categoryChildren(List<Category> categories, int id) {
		ArrayList<Category> rootChildren = new ArrayList<Category>();
		for (Category c : categories) {
			if (c.getFatherId() == id) {
				rootChildren.add(c);
			}
		}
		
		return rootChildren;
	}
	
	/**
	 * Gets the list of all recursive fathers of the category with id = catId 
	 * @param categories		the list of all the categories
	 * @param catId		the id of the category for which find all the recursive fathers
	 * @return the list of fathers
	 */
	private ArrayList<Integer> getFathersListOfChild(List<Category> categories, int catId) {
		ArrayList<Integer> fathers = new ArrayList<Integer>();
		boolean stop = false;
		Integer tempId = catId;
		
		if (catId == 0) {
			return fathers;
		}
		
		while(!stop) {
			for (Category c : categories) {
				if (c.getId() == tempId) {
					if (c.getFatherId() == 0) {
						fathers.add(0);
						stop = true;
					} else {
						fathers.add(c.getFatherId());
						tempId = c.getFatherId();
					}
				}
			}
		}
		
		return fathers;
	}
  	
	/**
	 * Perform the delete of a category
	 * @param categories		the list of all the categories
	 * @param categoriesToModify		the list of the categories' id to delete
	 * @param oldFathers		the list of the old fathers
	 * @param childrenToDelete	the list of children categories to delete
	 * @throws SQLException
	 * @throws Exception
	 */
  	private void deletePerform(List<Category> categories, Category categoryToModify, Category oldFather, List<Category> childrenToDelete) throws SQLException, Exception {

  		String query = "DELETE FROM category WHERE category_id = ?;";
  		String queryChildren = "UPDATE category SET `index` = ? WHERE category_id = ?";
  		connection.setAutoCommit(false);

  		Integer oldIndex = null;
  		
  		if (categoryToModify.getIndex().contains(".")) {
  			// the category to move is not direct child of root
  			oldIndex = Integer.parseInt(categoryToModify.getIndex().substring(categoryToModify.getIndex().lastIndexOf(".") + 1));
  		} else {
  			// the category to move is direct child of root
  			oldIndex = Integer.parseInt(categoryToModify.getIndex());
  		}
  		
  		PreparedStatement pstatement = null;
  		try {
			pstatement = connection.prepareStatement(query);
  			pstatement.setInt(1, categoryToModify.getId());
  			pstatement.executeUpdate();
      
  			for (Category c : childrenToDelete) {
  				// delete all category's children
  				pstatement = connection.prepareStatement(query);
  				pstatement.setInt(1, c.getId());
  				pstatement.executeUpdate();
  			}
  			
  			List<Category> fatherChildren = this.categoryChildren(categories, oldFather.getId());

  			for(Category c : fatherChildren) {
  				// update the indexes of the brothers that come after the category moved or deleted
  				if(categoryToModify.getId() != 0) {
	  				if(Integer.parseInt(c.getIndex().substring(c.getIndex().lastIndexOf(".") + 1)) > oldIndex) {
	  					pstatement = connection.prepareStatement(queryChildren);
	  					pstatement.setInt(1, Integer.parseInt(c.getIndex().substring(c.getIndex().lastIndexOf(".") + 1)) - 1);
	  					pstatement.setInt(2, c.getId());
	  					pstatement.executeUpdate();    	
	  				}
  				} else {
  					if(Integer.parseInt(c.getIndex()) > oldIndex) {
	  					pstatement = connection.prepareStatement(queryChildren);
	  					pstatement.setInt(1, Integer.parseInt(c.getIndex()) - 1);
	  					pstatement.setInt(2, c.getId());
	  					pstatement.executeUpdate();    	
	  				}
  				}
  			}
  			connection.commit();
  		} catch (SQLException e) {
  			connection.rollback();
  			throw new SQLException();
  		} finally {
  			try {
  				pstatement.close();
  			} catch (Exception e1) { }
  		}   
  	}
  	
  	/**
  	 * Delete a category
  	 * @param catId		id of the category to delete
  	 * @param fatherId	id of the father of the category to delete
  	 * @return the list of the deleted category's children
  	 * @throws SQLException
  	 * @throws Exception
  	 */
  	public List<Category> deleteCategory(int catId, int fatherId) throws SQLException, Exception {
  		if (catId == 0) {
  			throw new Exception("Failed to delete the category.");
  		}
  		
  		List<Category> categories = this.getCategories();
	
  		Category catToDelete = null;
		Category father = null;
  		int count = 0;
  		if (fatherId == 0) {
  			// the father of the category to delete is the root
  			Category root = new Category();
  			root.setId(0);
  			father = root;
  			count++;
  		}
  		// search the category to delete and its father (if not root)
  		for (Category c : categories) {
  			if (count == 2) {
  				break;
  			}
  			if (c.getId() == catId) {
  				catToDelete = c;
  				count++;
  			} else if (fatherId != 0 && c.getId() == fatherId) {
  				father = c;
  				count++;
  			}
  		}
  		
  		if (catToDelete == null || father == null) {
  			throw new Exception("Failed to delete the category.");
  		}
  		
  		ArrayList<Category> childrenToDelete = new ArrayList<Category>();
  		this.categoryChildrenAll(childrenToDelete, categories, catId);
  		
  		try {
  			deletePerform(categories, catToDelete, father, childrenToDelete);
  		} catch (SQLException e) {
  			throw new SQLException("Failed to delete the category.");
  		} catch (Exception e) {
  			throw new Exception("Invalid action.");
  		}
  		
  	  	return childrenToDelete;   
  	}
  	
  	/**
  	 * Inserts all the children of a category recursively
  	 * @param childrenToDelete	the list of all children
  	 * @param categories		the list of all the categories
  	 * @param catId		the id of the category for which search the children
  	 */
  	private void categoryChildrenAll(List<Category> childrenToDelete, List<Category> categories, int catId) {
  		for (Category c : categories) {
			if (c.getFatherId() == catId) {
				childrenToDelete.addAll(this.categoryChildren(categories, catId));
				this.categoryChildrenAll(childrenToDelete, categories, c.getId());
			}
  		}
  	}
  	
  	/**
  	 * Renames a category on the database
  	 * @param catId
  	 * @param newName
  	 * @throws SQLException
  	 */
  	public void renameCategory(int catId, String newName) throws SQLException{
  		String query = "UPDATE category SET name = ? WHERE category_id = ?;";

  		try {
  			PreparedStatement pstatement = connection.prepareStatement(query);
  			pstatement.setString(1, newName);
  			pstatement.setInt(2, catId);
  			pstatement.executeUpdate();
  		} catch (SQLException e) {
  			throw new SQLException("Failed to rename the category.");
  		}
  	}
  	
  	/**
  	 * Checks if a category is present on the database
  	 * @param id	category id to check
  	 * @return 1 if present, -1 otherwise
  	 * @throws SQLException
  	 */
  	public int checkCategoryExists(int id) throws SQLException {
  		String query = "SELECT * FROM category WHERE category_id = ?";
  		
  		ResultSet rs = null;
		PreparedStatement pStatement = null;
		try {
			pStatement = connection.prepareStatement(query);
			pStatement.setInt(1, id);
			rs = pStatement.executeQuery();
			if (!rs.isBeforeFirst()) {
				return -1;
			} else {
				return 1;
			}
		} catch (SQLException e) {
			// TODO: handle this exception well
			//e.printStackTrace();
			throw new SQLException("Failed to check if category exists.");
		}
  	}
}
