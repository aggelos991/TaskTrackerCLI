package com.example.tasktrackercli.service;

import com.example.tasktrackercli.model.Task;
import com.example.tasktrackercli.model.TaskStatus;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.List;

@Component
@RequiredArgsConstructor
public class CliRunner implements CommandLineRunner {

    private final TaskService taskService;

    @Override
    public void run(String... args) throws Exception {
        BufferedReader in = new BufferedReader(new InputStreamReader(System.in));
        printHelp();
        while(true) {
            System.out.print("> ");
            String line = in.readLine();
            if (line == null || line.trim().isEmpty()) {
                continue;
            }

            String trimmedLine = line.trim();
            String fullCommand = trimmedLine.toLowerCase();

            try {
                if (fullCommand.equals("help")) {
                    printHelp();
                } else if (fullCommand.equals("exit") || fullCommand.equals("quit")) {
                    System.out.println("Exiting...");
                    return;
                } else if (fullCommand.equals("list done")) {
                    List<Task> doneTasks = taskService.getTasksFromStatus(TaskStatus.DONE);
                    if(doneTasks.isEmpty()){
                        System.out.println("No done tasks found.");
                    } else {
                        for (Task t : doneTasks) {
                            System.out.println("Task ID: " + t.getId() + ", Description:  "+ t.getDescription() + ", Status: " + t.getStatus());
                        }
                    }
                } else if (fullCommand.equals("list todo")) {
                    List<Task> toDoTasks = taskService.getTasksFromStatus(TaskStatus.TO_DO);
                    if(toDoTasks.isEmpty()){
                        System.out.println("No to-do tasks found.");
                    } else {
                        for (Task t : toDoTasks) {
                            System.out.println("Task ID: " + t.getId() + ", Description:  "+ t.getDescription() + ", Status: " + t.getStatus());
                        }
                    }
                } else if (fullCommand.equals("list in-progress")) {
                    List<Task> inProgressTasks = taskService.getTasksFromStatus(TaskStatus.IN_PROGRESS);
                    if(inProgressTasks.isEmpty()){
                        System.out.println("No in progress tasks found.");
                    } else {
                        for (Task t : inProgressTasks) {
                            System.out.println("Task ID: " + t.getId() + ", Description:  "+ t.getDescription() + ", Status: " + t.getStatus());
                        }
                    }
                } else if (fullCommand.equals("list")) {
                    List<Task> tasks = taskService.getAllTasks();
                    if(tasks.isEmpty()){
                        System.out.println("No tasks found.");
                    } else {
                        for (Task t : tasks) {
                            System.out.println("Task ID: " + t.getId() + ", Description:  "+ t.getDescription() + ", Status: " + t.getStatus());
                        }
                    }
                } else if (fullCommand.startsWith("add ")) {
                    String description = trimmedLine.substring(4).trim();
                    if(description.isEmpty()) {
                        System.out.println("Usage: add <title>");
                    } else {
                        Task task = taskService.addTask(description);
                        System.out.println("Task added successfully (ID: " + task.getId() + ")");
                    }
                } else if (fullCommand.equals("add")) {
                    System.out.println("Usage: add <title>");
                } else if (fullCommand.startsWith("update ")) {
                    String remainingInput = trimmedLine.substring(7).trim();
                    String[] updateParts = remainingInput.split("\\s+", 2);
                    if (updateParts.length < 2 || updateParts[0].isEmpty() || updateParts[1].isEmpty()) {
                        System.out.println("Usage: update <id> <new description>");
                    } else {
                        try {
                            Task updatedTask = taskService.updateTaskDescription(Long.parseLong(updateParts[0]), updateParts[1]);
                            System.out.println("Task updated successfully (ID: " + updatedTask.getId() + ")");
                        } catch (NumberFormatException e) {
                            System.out.println("Invalid id.");
                        }
                    }
                } else if (fullCommand.equals("update")) {
                    System.out.println("Usage: update <id> <new description>");
                } else if (fullCommand.startsWith("delete ")) {
                    String idStr = fullCommand.substring(7).trim();
                    if (idStr.isEmpty()) {
                        System.out.println("Usage: delete <id>");
                    } else {
                        try {
                            long id = Long.parseLong(idStr);
                            if(taskService.deleteTask(id)) {
                                System.out.println("Task deleted successfully.");
                            } else {
                                System.out.println("Task not found.");
                            }
                        } catch (NumberFormatException e) {
                            System.out.println("Invalid id.");
                        }
                    }
                } else if (fullCommand.equals("delete")) {
                    System.out.println("Usage: delete <id>");
                } else if (fullCommand.startsWith("mark-in-progress ")) {
                    String idStr = fullCommand.substring(17).trim();
                    if (idStr.isEmpty()) {
                        System.out.println("Usage: mark-in-progress <id>");
                    } else {
                        try {
                            Long inProgressId = Long.parseLong(idStr);
                            Task inProgressTask = taskService.updateTaskStatus(inProgressId, TaskStatus.IN_PROGRESS);
                            System.out.println("Task marked as IN_PROGRESS (ID: " + inProgressTask.getId() + ")");
                        } catch (NumberFormatException e) {
                            System.out.println("Invalid id.");
                        }
                    }
                } else if (fullCommand.equals("mark-in-progress")) {
                    System.out.println("Usage: mark-in-progress <id>");
                } else if (fullCommand.startsWith("mark-done ")) {
                    String idStr = fullCommand.substring(10).trim();
                    if (idStr.isEmpty()) {
                        System.out.println("Usage: mark-done <id>");
                    } else {
                        try {
                            Long doneId = Long.parseLong(idStr);
                            Task doneTask = taskService.updateTaskStatus(doneId, TaskStatus.DONE);
                            System.out.println("Task marked as DONE (ID: " + doneTask.getId() + ")");
                        } catch (NumberFormatException e) {
                            System.out.println("Invalid id.");
                        }
                    }
                } else if (fullCommand.equals("mark-done")) {
                    System.out.println("Usage: mark-done <id>");
                } else {
                    System.out.println("Unknown command. Type 'help' for available commands.");
                }
            } catch (Exception e) {
                System.out.println("Error: " + e.getMessage());
            }
        }
    }

    private void printHelp() {
        System.out.println("  add <title>             - add a new task");
        System.out.println("  list                    - list all tasks");
        System.out.println("  list todo               - list tasks with status TODO");
        System.out.println("  list in-progress        - list tasks with status IN_PROGRESS");
        System.out.println("  list done               - list tasks with status DONE");
        System.out.println("  update <id> <description> - update task description");
        System.out.println("  mark-in-progress <id>   - mark task in-progress");
        System.out.println("  mark-done <id>          - mark task done");
        System.out.println("  delete <id>             - delete task");
        System.out.println("  help                    - show this help");
        System.out.println("  exit|quit               - exit");
    }
}