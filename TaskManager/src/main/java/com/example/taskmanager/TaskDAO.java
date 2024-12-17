package com.example.taskmanager;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class TaskDAO {
    private String jdbcURL = "jdbc:postgresql://localhost:5432/taskdb";
    private String jdbcUsername = "postgres";
    private String jdbcPassword = "admin";

    public TaskDAO() {
        createTableIfNotExists();
    }

    protected Connection getConnection() {
        Connection connection = null;
        try {
            Class.forName("org.postgresql.Driver");
            connection = DriverManager.getConnection(jdbcURL, jdbcUsername, jdbcPassword);
        } catch (SQLException | ClassNotFoundException e) {
            e.printStackTrace();
        }
        return connection;
    }

    private void createTableIfNotExists() {
        String createTasksTableSQL = "CREATE TABLE IF NOT EXISTS tasks (" + 
        							 "id SERIAL PRIMARY KEY, " + 
        							 "title VARCHAR(255) NOT NULL, " + 
        							 "description TEXT, " + 
        							 "completed BOOLEAN DEFAULT FALSE, " + 
        							 "priority INT DEFAULT 0, " + 
        							 "category VARCHAR(255), " + 
        							 "deadline TIMESTAMP)";
        String createDeletedTasksTableSQL = "CREATE TABLE IF NOT EXISTS deleted_tasks (" + 
        							 		"id SERIAL PRIMARY KEY, " + 
        							 		"title VARCHAR(255) NOT NULL, " + 
        							 		"description TEXT, " + 
        							 		"completed BOOLEAN DEFAULT FALSE, " + 
        							 		"priority INT DEFAULT 0, " + 
        							 		"category VARCHAR(255), " + 
        							 		"deadline TIMESTAMP)";
        String createCategoriesTableSQL = "CREATE TABLE IF NOT EXISTS categories (" + 
                                			"id SERIAL PRIMARY KEY, " +
                                			"name VARCHAR(255) NOT NULL UNIQUE)";
        try (Connection connection = getConnection(); 
        	Statement statement = connection.createStatement()) { 
        	statement.execute(createTasksTableSQL); 
        	statement.execute(createDeletedTasksTableSQL);
        	statement.execute(createCategoriesTableSQL);
        } catch (SQLException e) { 
        	e.printStackTrace(); 
        } 
      }

    public List<Task> getAllTasks() {
        List<Task> tasks = new ArrayList<>();
        try (Connection connection = getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement("SELECT * FROM tasks ORDER BY priority DESC, id ASC")) {
            ResultSet rs = preparedStatement.executeQuery();
            while (rs.next()) {
                int id = rs.getInt("id");
                String title = rs.getString("title");
                String description = rs.getString("description");
                boolean completed = rs.getBoolean("completed");
                int priority = rs.getInt("priority");
                String category = rs.getString("category");
                LocalDateTime deadline = rs.getTimestamp("deadline") != null ? rs.getTimestamp("deadline").toLocalDateTime() : null;
                Task task = new Task(id, title, description, completed, priority, category, deadline);
                tasks.add(task);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return tasks;
    }


    
    public List<Task> getAllDeletedTasks() {
        List<Task> deletedTasks = new ArrayList<>();
        try (Connection connection = getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement("SELECT * FROM deleted_tasks ORDER BY priority DESC, id ASC")) {
            ResultSet rs = preparedStatement.executeQuery();
            while (rs.next()) {
                int id = rs.getInt("id");
                String title = rs.getString("title");
                String description = rs.getString("description");
                boolean completed = rs.getBoolean("completed");
                int priority = rs.getInt("priority");
                String category = rs.getString("category");
                LocalDateTime deadline = rs.getTimestamp("deadline") != null ? rs.getTimestamp("deadline").toLocalDateTime() : null;
                Task task = new Task(id, title, description, completed, priority, category, deadline);
                deletedTasks.add(task);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return deletedTasks;
    }



    
    public List<String> getAllCategories() { 
    	List<String> categories = new ArrayList<>(); 
    	String selectCategoriesSQL = "SELECT name FROM categories"; 
    	try (Connection connection = getConnection(); 
    		PreparedStatement preparedStatement = connection.prepareStatement(selectCategoriesSQL)) { 
    		ResultSet rs = preparedStatement.executeQuery(); 
    		while (rs.next()) { 
    			categories.add(rs.getString("name")); 
    		} 
    	} catch (SQLException e) { 
    		e.printStackTrace(); 
    	} 
    	return categories;
    }

    public List<Task> searchTasks(String keyword) {
        List<Task> tasks = new ArrayList<>();
        String searchSQL = "SELECT * FROM tasks WHERE title LIKE ? OR description LIKE ? OR category LIKE ? ORDER BY priority DESC, id ASC";
        try (Connection connection = getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(searchSQL)) {
            String searchKeyword = "%" + keyword + "%";
            preparedStatement.setString(1, searchKeyword);
            preparedStatement.setString(2, searchKeyword);
            preparedStatement.setString(3, searchKeyword);
            ResultSet rs = preparedStatement.executeQuery();
            while (rs.next()) {
                int id = rs.getInt("id");
                String title = rs.getString("title");
                String description = rs.getString("description");
                boolean completed = rs.getBoolean("completed");
                int priority = rs.getInt("priority");
                String category = rs.getString("category");
                LocalDateTime deadline = rs.getTimestamp("deadline") != null ? rs.getTimestamp("deadline").toLocalDateTime() : null;
                Task task = new Task(id, title, description, completed, priority, category, deadline);
                tasks.add(task);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return tasks;
    }



    public List<Task> searchDeletedTasks(String keyword) {
        List<Task> tasks = new ArrayList<>();
        String searchSQL = "SELECT * FROM deleted_tasks WHERE title LIKE ? OR description LIKE ? ORDER BY priority DESC, id ASC";
        try (Connection connection = getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(searchSQL)) {
            String searchKeyword = "%" + keyword + "%";
            preparedStatement.setString(1, searchKeyword);
            preparedStatement.setString(2, searchKeyword);
            ResultSet rs = preparedStatement.executeQuery();
            while (rs.next()) {
                int id = rs.getInt("id");
                String title = rs.getString("title");
                String description = rs.getString("description");
                boolean completed = rs.getBoolean("completed");
                int priority = rs.getInt("priority");
                String category = rs.getString("category");
                LocalDateTime deadline = rs.getTimestamp("deadline") != null ? rs.getTimestamp("deadline").toLocalDateTime() : null;
                Task task = new Task(id, title, description, completed, priority, category, deadline);
                tasks.add(task);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return tasks;
    }

        
    public List<Task> filterTasks(Integer priority, Boolean completed, String category) {
        List<Task> tasks = new ArrayList<>();
        StringBuilder query = new StringBuilder("SELECT * FROM tasks WHERE 1=1");
        if (priority != null) {
            query.append(" AND priority = ").append(priority);
        }
        if (completed != null) {
            query.append(" AND completed = ").append(completed);
        }
        if (category != null && !category.isEmpty()) {
            query.append(" AND category = '").append(category).append("'");
        }
        query.append(" ORDER BY priority DESC, id ASC");

        try (Connection connection = getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(query.toString())) {
            ResultSet rs = preparedStatement.executeQuery();
            while (rs.next()) {
                int id = rs.getInt("id");
                String title = rs.getString("title");
                String description = rs.getString("description");
                boolean taskCompleted = rs.getBoolean("completed");
                int taskPriority = rs.getInt("priority");
                String taskCategory = rs.getString("category");
                LocalDateTime deadline = rs.getTimestamp("deadline") != null ? rs.getTimestamp("deadline").toLocalDateTime() : null;
                Task task = new Task(id, title, description, taskCompleted, taskPriority, taskCategory, deadline);
                tasks.add(task);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return tasks;
    }





    public void insertTask(Task task) { 
        try (Connection connection = getConnection(); 
             PreparedStatement preparedStatement = connection.prepareStatement("INSERT INTO tasks (title, description, completed, priority, category, deadline) VALUES (?, ?, ?, ?, ?, ?)")) { 
            preparedStatement.setString(1, task.getTitle()); 
            preparedStatement.setString(2, task.getDescription()); 
            preparedStatement.setBoolean(3, task.isCompleted()); 
            preparedStatement.setInt(4, task.getPriority()); 
            preparedStatement.setString(5, task.getCategory()); 
            preparedStatement.setObject(6, task.getDeadline()); 
            preparedStatement.executeUpdate(); 
        } catch (SQLException e) { 
            e.printStackTrace(); 
        } 
    }


        public boolean isCategoryExists(String category) {
            boolean exists = false;
            String checkCategorySQL = "SELECT COUNT(*) FROM categories WHERE name = ?";
            try (Connection connection = getConnection();
                 PreparedStatement preparedStatement = connection.prepareStatement(checkCategorySQL)) {
                preparedStatement.setString(1, category);
                ResultSet rs = preparedStatement.executeQuery();
                if (rs.next()) {
                    exists = rs.getInt(1) > 0;
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
            return exists;
        }

        public void insertCategory(String category) throws SQLException {
            System.out.println("挿入するカテゴリ: " + category); // デバッグメッセージ
            if (category == null || category.isEmpty()) {
                throw new SQLException("カテゴリ名が無効です。");
            }
            String insertCategorySQL = "INSERT INTO categories (name) VALUES (?)";
            try (Connection connection = getConnection();
                 PreparedStatement preparedStatement = connection.prepareStatement(insertCategorySQL)) {
                preparedStatement.setString(1, category);
                preparedStatement.executeUpdate();
            } catch (SQLException e) {
                if (e.getSQLState().equals("23505")) {
                    throw new SQLException("カテゴリが既に存在します。");
                } else {
                    throw e;
                }
            }
        }



    public Task selectTask(int id) { 
        Task task = null; 
        try (Connection connection = getConnection(); 
             PreparedStatement preparedStatement = connection.prepareStatement("SELECT * FROM tasks WHERE id = ?")) { 
            preparedStatement.setInt(1, id); 
            ResultSet rs = preparedStatement.executeQuery(); 
            if (rs.next()) { 
                String title = rs.getString("title"); 
                String description = rs.getString("description"); 
                boolean completed = rs.getBoolean("completed"); 
                int priority = rs.getInt("priority"); 
                String category = rs.getString("category"); 
                LocalDateTime deadline = rs.getTimestamp("deadline") != null ? rs.getTimestamp("deadline").toLocalDateTime() : null; 
                task = new Task(id, title, description, completed, priority, category, deadline); 
            } 
        } catch (SQLException e) { 
            e.printStackTrace(); 
        } 
        return task; 
    }


    public void updateTask(Task task) { 
        try (Connection connection = getConnection(); 
             PreparedStatement preparedStatement = connection.prepareStatement("UPDATE tasks SET title = ?, description = ?, completed = ?, priority = ?, category = ?, deadline = ? WHERE id = ?")) { 
            preparedStatement.setString(1, task.getTitle()); 
            preparedStatement.setString(2, task.getDescription()); 
            preparedStatement.setBoolean(3, task.isCompleted()); 
            preparedStatement.setInt(4, task.getPriority()); 
            preparedStatement.setString(5, task.getCategory()); 
            preparedStatement.setObject(6, task.getDeadline()); 
            preparedStatement.setInt(7, task.getId()); 
            preparedStatement.executeUpdate(); 
        } catch (SQLException e) { 
            e.printStackTrace(); 
        } 
    }


    public void updateTaskCompletion(int id, boolean completed) {
        try (Connection connection = getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement("UPDATE tasks SET completed = ? WHERE id = ?")) {
            preparedStatement.setBoolean(1, completed);
            preparedStatement.setInt(2, id);
            preparedStatement.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
    
    public void deleteTask(int id) {
        String insertDeletedTaskSQL = "INSERT INTO deleted_tasks (title, description, completed, priority, category, deadline) SELECT title, description, completed, priority, category, deadline FROM tasks WHERE id = ?";
        String deleteTaskSQL = "DELETE FROM tasks WHERE id = ?";
        try (Connection connection = getConnection();
             PreparedStatement insertStatement = connection.prepareStatement(insertDeletedTaskSQL);
             PreparedStatement deleteStatement = connection.prepareStatement(deleteTaskSQL)) {
            insertStatement.setInt(1, id);
            insertStatement.executeUpdate();
            deleteStatement.setInt(1, id);
            deleteStatement.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void restoreTask(int id) {
        String insertTaskSQL = "INSERT INTO tasks (title, description, completed, priority, category, deadline) SELECT title, description, completed, priority, category, deadline FROM deleted_tasks WHERE id = ?";
        String deleteDeletedTaskSQL = "DELETE FROM deleted_tasks WHERE id = ?";
        try (Connection connection = getConnection();
             PreparedStatement insertStatement = connection.prepareStatement(insertTaskSQL);
             PreparedStatement deleteStatement = connection.prepareStatement(deleteDeletedTaskSQL)) {
            insertStatement.setInt(1, id);
            insertStatement.executeUpdate();
            deleteStatement.setInt(1, id);
            deleteStatement.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
    
    public void permanentDeleteDeletedTask(int id) throws SQLException {
        String sql = "DELETE FROM deleted_tasks WHERE id = ?";
        Connection conn = null;
        try {
            conn = getConnection();
            conn.setAutoCommit(false); // 自動コミットをオフにする
            try (PreparedStatement stmt = conn.prepareStatement(sql)) {
                stmt.setInt(1, id);
                System.out.println("SQLクエリを実行します: " + sql + " with id: " + id); // デバッグ用メッセージ
                int rowsAffected = stmt.executeUpdate();
                conn.commit(); // コミットする
                System.out.println("削除された行数: " + rowsAffected); // デバッグ用メッセージ
                if (rowsAffected == 0) {
                    System.out.println("削除対象のタスクが存在しませんでした。ID: " + id); // デバッグ用メッセージ
                } else {
                    System.out.println("タスクがデータベースから完全に削除されました: " + id); // デバッグ用メッセージ
                }
            }
        } catch (SQLException e) {
            if (conn != null) {
                try {
                    conn.rollback(); // 例外発生時にロールバックする
                } catch (SQLException rollbackEx) {
                    System.out.println("ロールバックに失敗しました: " + rollbackEx.getMessage()); // デバッグ用メッセージ
                }
            }
            System.out.println("タスクの削除に失敗しました: " + e.getMessage()); // デバッグ用メッセージ
            throw e;
        } finally {
            if (conn != null) {
                try {
                    conn.close(); // 接続をクローズする
                } catch (SQLException closeEx) {
                    System.out.println("接続のクローズに失敗しました: " + closeEx.getMessage()); // デバッグ用メッセージ
                }
            }
        }
    }
    
    

}


