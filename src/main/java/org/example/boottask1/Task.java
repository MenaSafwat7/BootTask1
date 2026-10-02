package org.example.boottask1;

import org.springframework.boot.context.properties.bind.DefaultValue;

import java.time.LocalDate;
import java.time.LocalTime;

public class Task {
    private Long id;
    private String title;
    private String description;
    private boolean completed;
    private LocalDate dueDate;

    public void setMaxTask(int maxTask) {
        this.maxTask = maxTask;
    }

    private int maxTask;

    public int getMaxTask() {
        return maxTask;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public boolean isCompleted() { return completed; }
    public void setCompleted(boolean completed) { this.completed = completed; }

    public LocalDate getDueDate() { return dueDate; }
    public void setDueDate(LocalDate dueDate) { this.dueDate = dueDate; }
}
