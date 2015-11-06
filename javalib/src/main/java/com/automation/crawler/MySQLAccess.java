package com.automation.crawler;


import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Map;

import org.apache.commons.lang3.tuple.Pair;

public class MySQLAccess {
	private Connection connect = null;
	private Statement statement = null;
	private PreparedStatement preparedStatement = null;
	private ResultSet resultSet = null;

	public enum COLUMNS {
		RECIPEINFO_ID, URL, DONE, DIRTY, SOURCE, JSON, TITLE, 
		DESCRIPTION, COOKING_TIME, SERVING, IMAGE_DOWNLOADED, CATEGORY, ADDED_AT
	}

	public void setUpDB() throws ClassNotFoundException, SQLException {
		// This will load the MySQL driver, each DB has its own driver
		Class.forName("com.mysql.jdbc.Driver");
		// Setup the connection with the DB
		connect = DriverManager.getConnection("jdbc:mysql://localhost/recipe?"
				+ "user=root&password=root");

		statement = connect.createStatement();
	}

	public void updateInDb(int hashCode, Map<String, String> list)
			throws Exception {
		try {
			setUpDB();
			String setSegment = "";
            list.put(COLUMNS.ADDED_AT.toString().toLowerCase(), System.currentTimeMillis()/1000 + "");
			for (String key : list.keySet()) {
				setSegment += (key + " = \"" + list.get(key)) + "\",";
			}

			setSegment = setSegment.substring(0, setSegment.length() - 1);
			String qury = String.format(
					"update recipe_item set %s where %s = %d", setSegment,
					COLUMNS.RECIPEINFO_ID.toString().toLowerCase(), hashCode);
			preparedStatement = connect.prepareStatement(qury);
			preparedStatement.executeUpdate();

		} catch (Exception e) {
			throw e;
		} finally {
			close();
		}
	}

	public void insertInDb(int hashCode, String url) throws Exception {
		try {
			setUpDB();
			preparedStatement = connect
					.prepareStatement("insert into  recipe.recipe_item values (default, ?, ?)");
			// Parameters start with 1
			preparedStatement.setInt(1, hashCode);
			preparedStatement.setString(2, url);
			preparedStatement.executeUpdate();

		} catch (Exception e) {
			throw e;
		} finally {
			close();
		}
	}

	public ArrayList<RecipeInfo> readDataBase(int limit, String whereClause)
			throws Exception {
		ArrayList<RecipeInfo> list = new ArrayList<>();
		try {
			setUpDB();
			String query = null;
			if (limit > 0) {
				query = String.format(
						"SELECT * from recipe.recipe_item %s limit %d",
						whereClause, limit);
			} else {
				query = String.format("SELECT * from recipe.recipe_item %s ",
						whereClause);
			}

			preparedStatement = connect.prepareStatement(query);
			resultSet = preparedStatement.executeQuery();

			while (resultSet.next()) {
				RecipeInfo info = new RecipeInfo();
				int hash = resultSet.getInt(COLUMNS.RECIPEINFO_ID.toString()
						.toLowerCase());
				String url = resultSet.getString(COLUMNS.URL.toString()
						.toLowerCase());
				String dirty = resultSet.getString(COLUMNS.DIRTY.toString()
						.toLowerCase());
				String done = resultSet.getString(COLUMNS.DONE.toString()
						.toLowerCase());
				String title = resultSet.getString(COLUMNS.TITLE.toString()
						.toLowerCase());
				String description = resultSet.getString(COLUMNS.DESCRIPTION.toString()
						.toLowerCase());
				String source = resultSet.getString(COLUMNS.SOURCE.toString()
						.toLowerCase());
				String category = resultSet.getString(COLUMNS.CATEGORY.toString()
						.toLowerCase());

				
				info.setHash(hash);
				info.setBaseUrl(url);
				info.setDescription(description);
				info.setTitle(title);
				info.setCategory(category);
				
				// System.out.println("hash: " + hash);
				// System.out.println("url: " + url);
				// System.out.println("dirty: " + dirty);
				// System.out.println("done: " + done);
				// System.out.println("source: " + source);
				
				list.add(info);
			}
		} catch (Exception e) {
			throw e;
		} finally {
			close();
		}

		return list;
	}

	// You need to close the resultSet
	private void close() {
		try {
			if (resultSet != null) {
				resultSet.close();
			}

			if (statement != null) {
				statement.close();
			}

			if (connect != null) {
				connect.close();
			}
		} catch (Exception e) {

		}
	}

	public static void main(String args[]) {
		MySQLAccess dao = new MySQLAccess();
//		try {
//			dao.readDataBase(10, true);
//		} catch (Exception e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
	}

}