package com.example.taskmanager;

import java.io.IOException;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

import jakarta.servlet.RequestDispatcher;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

public class TaskServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;
    private TaskDAO taskDAO;

    public void init() {
        taskDAO = new TaskDAO();
        List<String> categories = taskDAO.getAllCategories();
        getServletContext().setAttribute("categories", categories);
    }

    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String action = request.getParameter("action");
        if (action == null) {
        	action = "";
        }

        switch (action) {
            case "edit":
                showEditForm(request, response);
                break;
            case "update":
                updateTask(request, response);
                break;
            case "toggleComplete":
                toggleTaskCompletion(request, response);
                break;
            case "delete":
                deleteTask(request, response);
                break;
            case "deletedTasks":
                listDeletedTasks(request, response);
                break;
            case "restore":
                restoreTask(request, response);
                break;
            case "search":
                searchTasks(request, response);
                break;
            case "searchDeletedTasks":
                searchDeletedTasks(request, response);
                break;
            case "filter": 
                filterTasks(request, response); 
                break;
            case "permanentDelete":
            	permanentDeleteTask(request, response);
            	break;
            default:
                listTasks(request, response);
                break;
        }
    }

    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String action = request.getParameter("action");
        if ("addCategory".equals(action)) {
            String newCategory = request.getParameter("newCategory");
            try {
                taskDAO.insertCategory(newCategory);
                List<String> categories = taskDAO.getAllCategories();
                getServletContext().setAttribute("categories", categories);
                response.sendRedirect("task");
            } catch (SQLException e) {
                if (e.getMessage().contains("カテゴリが既に存在します。")) {
                    request.setAttribute("categoryError", "このカテゴリはすでに登録されています。");
                    listTasks(request, response);  // タスク一覧ページを再表示
                } else {
                    throw new ServletException(e);
                }
            }
        } else {
            String title = request.getParameter("title");
            String description = request.getParameter("description");
            String idStr = request.getParameter("id");
            int priority = Integer.parseInt(request.getParameter("priority"));
            String category = request.getParameter("category");
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm");
            LocalDateTime deadline = request.getParameter("deadline") == null || request.getParameter("deadline").isEmpty() ? null : LocalDateTime.parse(request.getParameter("deadline"), formatter);

            if (idStr == null || idStr.isEmpty()) {
                Task newTask = new Task(0, title, description, false, priority, category, deadline);
                taskDAO.insertTask(newTask);
            } else {
                int id = Integer.parseInt(idStr);
                Task existingTask = new Task(id, title, description, false, priority, category, deadline);
                taskDAO.updateTask(existingTask);
            }

            response.sendRedirect("task");
        }
    }
    
    protected void doPut(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException { 
    	updateTask(request, response); 
    }

    private void listTasks(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        List<Task> tasks = taskDAO.getAllTasks();
        
        // デバッグ用のメッセージ
        System.out.println("タスクリストのサイズ: " + tasks.size());
        for (Task task : tasks) {
            System.out.println("タスクタイトル: " + task.getTitle());
        }
        
        request.setAttribute("taskList", tasks);
        RequestDispatcher dispatcher = request.getRequestDispatcher("index.jsp");
        dispatcher.forward(request, response);
    }




    private void listDeletedTasks(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        List<Task> deletedTasks = taskDAO.getAllDeletedTasks();
        request.setAttribute("deletedTaskList", deletedTasks);
        RequestDispatcher dispatcher = request.getRequestDispatcher("deleted_tasks.jsp");
        dispatcher.forward(request, response);
    }

    private void showEditForm(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        int id = Integer.parseInt(request.getParameter("id"));
        Task existingTask = taskDAO.selectTask(id);
        RequestDispatcher dispatcher = request.getRequestDispatcher("edit.jsp");
        request.setAttribute("task", existingTask);
        dispatcher.forward(request, response);
    }

    private void updateTask(HttpServletRequest request, HttpServletResponse response) throws IOException {
        int id = Integer.parseInt(request.getParameter("id"));
        String title = request.getParameter("title");
        String description = request.getParameter("description");
        boolean completed = Boolean.parseBoolean(request.getParameter("completed"));
        int priority = Integer.parseInt(request.getParameter("priority"));
        String category = request.getParameter("category");
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm");
        LocalDateTime deadline = request.getParameter("deadline") == null || request.getParameter("deadline").isEmpty() ? null : LocalDateTime.parse(request.getParameter("deadline"), formatter);
        Task task = new Task(id, title, description, completed, priority, category, deadline);
        taskDAO.updateTask(task);
        response.sendRedirect("task");
    }

    private void toggleTaskCompletion(HttpServletRequest request, HttpServletResponse response) throws IOException {
        int id = Integer.parseInt(request.getParameter("id"));
        boolean completed = Boolean.parseBoolean(request.getParameter("completed"));
        taskDAO.updateTaskCompletion(id, !completed);
        response.sendRedirect("task");
    }

    private void deleteTask(HttpServletRequest request, HttpServletResponse response) throws IOException {
        int id = Integer.parseInt(request.getParameter("id"));
        taskDAO.deleteTask(id);
        response.sendRedirect("task");
    }

    private void restoreTask(HttpServletRequest request, HttpServletResponse response) throws IOException {
        int id = Integer.parseInt(request.getParameter("id"));
        taskDAO.restoreTask(id);
        response.sendRedirect("task?action=deletedTasks");
    }

    private void searchTasks(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String keyword = request.getParameter("keyword");
        List<Task> tasks = taskDAO.searchTasks(keyword);
        request.setAttribute("taskList", tasks);
        RequestDispatcher dispatcher = request.getRequestDispatcher("index.jsp");
        dispatcher.forward(request, response);
    }

    private void searchDeletedTasks(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String keyword = request.getParameter("keyword");
        List<Task> tasks = taskDAO.searchDeletedTasks(keyword);
        request.setAttribute("deletedTaskList", tasks);
        RequestDispatcher dispatcher = request.getRequestDispatcher("deleted_tasks.jsp");
        dispatcher.forward(request, response);
    }
    
    private void filterTasks(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException { 
        String priorityStr = request.getParameter("priority"); 
        String completedStr = request.getParameter("completed"); 
        String category = request.getParameter("category"); 
        Integer priority = priorityStr != null && !priorityStr.isEmpty() ? Integer.parseInt(priorityStr) : null; 
        Boolean completed = completedStr != null && !completedStr.isEmpty() ? Boolean.parseBoolean(completedStr) : null; 
        
        List<Task> tasks = taskDAO.filterTasks(priority, completed, category); 
        request.setAttribute("taskList", tasks); 
        RequestDispatcher dispatcher = request.getRequestDispatcher("index.jsp"); 
        dispatcher.forward(request, response); 
    }
    
    protected void permanentDeleteTask(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        try {
            int id = Integer.parseInt(request.getParameter("id"));
            System.out.println("削除対象のタスクID: " + id); // デバッグ用メッセージ
            taskDAO.permanentDeleteDeletedTask(id); // 修正
            System.out.println("削除済みタスクが完全に削除されました: " + id); // デバッグ用メッセージ
            response.sendRedirect("task?action=deletedTasks");
        } catch (SQLException e) {
            throw new ServletException("削除済みタスクの完全削除に失敗しました", e);
        }
    }


}
