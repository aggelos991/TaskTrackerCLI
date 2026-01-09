package com.example.tasktrackercli.service;

import com.example.tasktrackercli.model.Task;
import com.example.tasktrackercli.model.TaskStatus;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicLong;

@Service
public class TaskService {
    private final List<Task> tasks = Collections.synchronizedList(new ArrayList<>());
    private final AtomicLong counter = new AtomicLong(1);

    //add task
    public Task addTask(String title) {
        Task task = Task.builder()
                .id(counter.getAndIncrement())
                .description(title)
                .status(TaskStatus.TO_DO)
                .createdAt(LocalDateTime.now())
                .build();
        synchronized (tasks) {
            tasks.add(task);
        }
        return task;
    }

    // update task
    public Task updateTaskDescription(Long id,String title){
        synchronized (tasks){
            for(Task task: tasks){
                if(task.getId().equals(id)){
                    task.setDescription(title);
                    task.setUpdatedAt(LocalDateTime.now());
                    return task;
                }
            }
        }
        throw new IllegalArgumentException("Task not found with ID: " + id);
    }

    public Task updateTaskStatus(Long id, TaskStatus status){
        synchronized (tasks){
            for(Task task: tasks){
                if(task.getId().equals(id)){
                    task.setStatus(status);
                    task.setUpdatedAt(LocalDateTime.now());
                    return task;
                }
            }
        }
        throw new IllegalArgumentException("Task not found with ID: " + id);
    }

    // delete task
    public boolean deleteTask(Long id) {
        synchronized (tasks){
            return tasks.removeIf(t -> Objects.equals(t.getId(), id));
        }
    }

    // list all tasks
    public List<Task> getAllTasks(){
        synchronized (tasks) {
            return tasks;
        }
    }

    public List<Task> getTasksFromStatus(TaskStatus status) {
        synchronized (tasks) {
            List<Task> filteredTasks = new ArrayList<>();
            for (Task task : tasks) {
                if (task.getStatus() == status) {
                    filteredTasks.add(task);
                }
            }
            return filteredTasks;
        }
    }
}
