package com.example.taskmanager;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;

public class Task {
    private int id;
    private String title;
    private String description;
    private boolean completed;
    private int priority;
    private String category; 
    private LocalDateTime deadline;

    public Task(int id, String title, String description, boolean completed, int priority, String category, LocalDateTime deadline) { 
        this.id = id;
        this.title = title;
        this.description = description;
        this.completed = completed;
        this.priority = priority;
        this.category = category;
        this.deadline = deadline;
    }

    // ゲッターとセッターを追加
    public int getId() {
        return id;
    }

    public String getTitle() {
        return title;
    }

    public String getDescription() {
        return description;
    }

    public boolean isCompleted() {
        return completed;
    }

    public void setCompleted(boolean completed) {
        this.completed = completed;
    }

    public int getPriority() {
        return priority;
    }

    public void setPriority(int priority) {
        this.priority = priority;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }
    
    public LocalDateTime getDeadline() { 
        return deadline; 
    } 
    
    public void setDeadline(LocalDateTime deadline) { 
        this.deadline = deadline; 
    }
    
    public Date getDeadlineAsDate() { 
    	return deadline == null ? null : Date.from(deadline.atZone(ZoneId.systemDefault()).toInstant()); 
    }
}
