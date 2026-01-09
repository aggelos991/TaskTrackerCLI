package com.example.tasktrackercli.service

import com.example.tasktrackercli.model.Task
import com.example.tasktrackercli.model.TaskStatus
import spock.lang.Specification
import spock.lang.Subject

import java.time.LocalDateTime

class CliRunnerSpec extends Specification {

    TaskService taskService = Mock()

    @Subject
    CliRunner cliRunner = new CliRunner(taskService)

    ByteArrayOutputStream outputStream
    PrintStream originalOut

    def setup() {
        outputStream = new ByteArrayOutputStream()
        originalOut = System.out
        System.setOut(new PrintStream(outputStream))
    }

    def cleanup() {
        System.setOut(originalOut)
    }

    def "should add task successfully"() {
        given:
        def task = new Task(1L, "Buy groceries", TaskStatus.TO_DO, LocalDateTime.now(), LocalDateTime.now())
        def input = "add Buy groceries\nexit\n"
        System.setIn(new ByteArrayInputStream(input.bytes))

        when:
        cliRunner.run()

        then:
        1 * taskService.addTask("Buy groceries") >> task
        outputStream.toString().contains("Task added successfully (ID: 1)")
    }

    def "should add task with special characters"() {
        given:
        def task = new Task(1L, "Buy @#\$% special", TaskStatus.TO_DO, LocalDateTime.now(), LocalDateTime.now())
        def input = "add Buy @#\$% special\nexit\n"
        System.setIn(new ByteArrayInputStream(input.bytes))

        when:
        cliRunner.run()

        then:
        1 * taskService.addTask("Buy @#\$% special") >> task
        outputStream.toString().contains("Task added successfully (ID: 1)")
    }

    def "should handle add command with missing description"() {
        given:
        def input = "add\nexit\n"
        System.setIn(new ByteArrayInputStream(input.bytes))

        when:
        cliRunner.run()

        then:
        0 * taskService.addTask(_)
        outputStream.toString().contains("Usage: add <title>")
    }

    def "should handle add command with only spaces"() {
        given:
        def input = "add    \nexit\n"
        System.setIn(new ByteArrayInputStream(input.bytes))

        when:
        cliRunner.run()

        then:
        0 * taskService.addTask(_)
        outputStream.toString().contains("Usage: add <title>")
    }

    def "should update task successfully"() {
        given:
        def task = new Task(1L, "Updated description", TaskStatus.TO_DO, LocalDateTime.now(), LocalDateTime.now())
        def input = "update 1 Updated description\nexit\n"
        System.setIn(new ByteArrayInputStream(input.bytes))

        when:
        cliRunner.run()

        then:
        1 * taskService.updateTaskDescription(1L, "Updated description") >> task
        outputStream.toString().contains("Task updated successfully (ID: 1)")
    }

    def "should update task with multi-word description"() {
        given:
        def task = new Task(1L, "This is a very long description", TaskStatus.TO_DO, LocalDateTime.now(), LocalDateTime.now())
        def input = "update 1 This is a very long description\nexit\n"
        System.setIn(new ByteArrayInputStream(input.bytes))

        when:
        cliRunner.run()

        then:
        1 * taskService.updateTaskDescription(1L, "This is a very long description") >> task
        outputStream.toString().contains("Task updated successfully (ID: 1)")
    }

    def "should handle update command with invalid format"() {
        given:
        def input = "update 1\nexit\n"
        System.setIn(new ByteArrayInputStream(input.bytes))

        when:
        cliRunner.run()

        then:
        0 * taskService.updateTaskDescription(_, _)
        outputStream.toString().contains("Usage: update <id> <new description>")
    }

    def "should handle update command with missing description"() {
        given:
        def input = "update 1  \nexit\n"
        System.setIn(new ByteArrayInputStream(input.bytes))

        when:
        cliRunner.run()

        then:
        0 * taskService.updateTaskDescription(_, _)
        outputStream.toString().contains("Usage: update <id> <new description>")
    }

    def "should handle update command without arguments"() {
        given:
        def input = "update\nexit\n"
        System.setIn(new ByteArrayInputStream(input.bytes))

        when:
        cliRunner.run()

        then:
        0 * taskService.updateTaskDescription(_, _)
        outputStream.toString().contains("Usage: update <id> <new description>")
    }

    def "should handle update with invalid id format"() {
        given:
        def input = "update abc New description\nexit\n"
        System.setIn(new ByteArrayInputStream(input.bytes))

        when:
        cliRunner.run()

        then:
        0 * taskService.updateTaskDescription(_, _)
        outputStream.toString().contains("Invalid id")
    }

    def "should delete task successfully"() {
        given:
        def input = "delete 1\nexit\n"
        System.setIn(new ByteArrayInputStream(input.bytes))

        when:
        cliRunner.run()

        then:
        1 * taskService.deleteTask(1L) >> true
        outputStream.toString().contains("Task deleted successfully")
    }

    def "should handle delete task not found"() {
        given:
        def input = "delete 999\nexit\n"
        System.setIn(new ByteArrayInputStream(input.bytes))

        when:
        cliRunner.run()

        then:
        1 * taskService.deleteTask(999L) >> false
        outputStream.toString().contains("Task not found")
    }

    def "should handle delete with invalid id"() {
        given:
        def input = "delete abc\nexit\n"
        System.setIn(new ByteArrayInputStream(input.bytes))

        when:
        cliRunner.run()

        then:
        0 * taskService.deleteTask(_)
        outputStream.toString().contains("Invalid id")
    }

    def "should handle delete without id"() {
        given:
        def input = "delete\nexit\n"
        System.setIn(new ByteArrayInputStream(input.bytes))

        when:
        cliRunner.run()

        then:
        0 * taskService.deleteTask(_)
        outputStream.toString().contains("Usage: delete <id>")
    }

    def "should handle delete with only spaces"() {
        given:
        def input = "delete   \nexit\n"
        System.setIn(new ByteArrayInputStream(input.bytes))

        when:
        cliRunner.run()

        then:
        0 * taskService.deleteTask(_)
        outputStream.toString().contains("Usage: delete <id>")
    }

    def "should list all tasks"() {
        given:
        def tasks = [
                new Task(1L, "Task 1", TaskStatus.TO_DO, LocalDateTime.now(), LocalDateTime.now()),
                new Task(2L, "Task 2", TaskStatus.DONE, LocalDateTime.now(), LocalDateTime.now())
        ]
        def input = "list\nexit\n"
        System.setIn(new ByteArrayInputStream(input.bytes))

        when:
        cliRunner.run()

        then:
        1 * taskService.getAllTasks() >> tasks
        outputStream.toString().contains("Task ID: 1")
        outputStream.toString().contains("Task ID: 2")
    }

    def "should handle empty task list"() {
        given:
        def input = "list\nexit\n"
        System.setIn(new ByteArrayInputStream(input.bytes))

        when:
        cliRunner.run()

        then:
        1 * taskService.getAllTasks() >> []
        outputStream.toString().contains("No tasks found")
    }

    def "should list done tasks"() {
        given:
        def doneTasks = [
                new Task(1L, "Task 1", TaskStatus.DONE, LocalDateTime.now(), LocalDateTime.now())
        ]
        def input = "list done\nexit\n"
        System.setIn(new ByteArrayInputStream(input.bytes))

        when:
        cliRunner.run()

        then:
        1 * taskService.getTasksFromStatus(TaskStatus.DONE) >> doneTasks
        outputStream.toString().contains("Task ID: 1")
        outputStream.toString().contains("Status: DONE")
    }

    def "should list todo tasks"() {
        given:
        def tasks = [new Task(1L, "Task 1", TaskStatus.TO_DO, LocalDateTime.now(), LocalDateTime.now())]
        def input = "list todo\nexit\n"
        System.setIn(new ByteArrayInputStream(input.bytes))

        when:
        cliRunner.run()

        then:
        1 * taskService.getTasksFromStatus(TaskStatus.TO_DO) >> tasks
        outputStream.toString().contains("Task ID: 1")
        outputStream.toString().contains("Status: TO_DO")
    }

    def "should list in-progress tasks"() {
        given:
        def tasks = [new Task(1L, "Task 1", TaskStatus.IN_PROGRESS, LocalDateTime.now(), LocalDateTime.now())]
        def input = "list in-progress\nexit\n"
        System.setIn(new ByteArrayInputStream(input.bytes))

        when:
        cliRunner.run()

        then:
        1 * taskService.getTasksFromStatus(TaskStatus.IN_PROGRESS) >> tasks
        outputStream.toString().contains("Task ID: 1")
        outputStream.toString().contains("Status: IN_PROGRESS")
    }

    def "should handle empty done tasks list"() {
        given:
        def input = "list done\nexit\n"
        System.setIn(new ByteArrayInputStream(input.bytes))

        when:
        cliRunner.run()

        then:
        1 * taskService.getTasksFromStatus(TaskStatus.DONE) >> []
        outputStream.toString().contains("No done tasks found")
    }

    def "should handle empty todo tasks list"() {
        given:
        def input = "list todo\nexit\n"
        System.setIn(new ByteArrayInputStream(input.bytes))

        when:
        cliRunner.run()

        then:
        1 * taskService.getTasksFromStatus(TaskStatus.TO_DO) >> []
        outputStream.toString().contains("No to-do tasks found")
    }

    def "should handle empty in-progress tasks list"() {
        given:
        def input = "list in-progress\nexit\n"
        System.setIn(new ByteArrayInputStream(input.bytes))

        when:
        cliRunner.run()

        then:
        1 * taskService.getTasksFromStatus(TaskStatus.IN_PROGRESS) >> []
        outputStream.toString().contains("No in progress tasks found")
    }

    def "should mark task as in-progress"() {
        given:
        def task = new Task(1L, "Task 1", TaskStatus.IN_PROGRESS, LocalDateTime.now(), LocalDateTime.now())
        def input = "mark-in-progress 1\nexit\n"
        System.setIn(new ByteArrayInputStream(input.bytes))

        when:
        cliRunner.run()

        then:
        1 * taskService.updateTaskStatus(1L, TaskStatus.IN_PROGRESS) >> task
        outputStream.toString().contains("Task marked as IN_PROGRESS (ID: 1)")
    }

    def "should handle mark-in-progress without id"() {
        given:
        def input = "mark-in-progress\nexit\n"
        System.setIn(new ByteArrayInputStream(input.bytes))

        when:
        cliRunner.run()

        then:
        0 * taskService.updateTaskStatus(_, _)
        outputStream.toString().contains("Usage: mark-in-progress <id>")
    }

    def "should handle mark-in-progress with invalid id"() {
        given:
        def input = "mark-in-progress abc\nexit\n"
        System.setIn(new ByteArrayInputStream(input.bytes))

        when:
        cliRunner.run()

        then:
        0 * taskService.updateTaskStatus(_, _)
        outputStream.toString().contains("Invalid id")
    }

    def "should handle mark-in-progress with only spaces"() {
        given:
        def input = "mark-in-progress   \nexit\n"
        System.setIn(new ByteArrayInputStream(input.bytes))

        when:
        cliRunner.run()

        then:
        0 * taskService.updateTaskStatus(_, _)
        outputStream.toString().contains("Usage: mark-in-progress <id>")
    }

    def "should mark task as done"() {
        given:
        def task = new Task(1L, "Task 1", TaskStatus.DONE, LocalDateTime.now(), LocalDateTime.now())
        def input = "mark-done 1\nexit\n"
        System.setIn(new ByteArrayInputStream(input.bytes))

        when:
        cliRunner.run()

        then:
        1 * taskService.updateTaskStatus(1L, TaskStatus.DONE) >> task
        outputStream.toString().contains("Task marked as DONE (ID: 1)")
    }

    def "should handle mark-done without id"() {
        given:
        def input = "mark-done\nexit\n"
        System.setIn(new ByteArrayInputStream(input.bytes))

        when:
        cliRunner.run()

        then:
        0 * taskService.updateTaskStatus(_, _)
        outputStream.toString().contains("Usage: mark-done <id>")
    }

    def "should handle mark-done with invalid id"() {
        given:
        def input = "mark-done xyz\nexit\n"
        System.setIn(new ByteArrayInputStream(input.bytes))

        when:
        cliRunner.run()

        then:
        0 * taskService.updateTaskStatus(_, _)
        outputStream.toString().contains("Invalid id")
    }

    def "should handle mark-done with only spaces"() {
        given:
        def input = "mark-done   \nexit\n"
        System.setIn(new ByteArrayInputStream(input.bytes))

        when:
        cliRunner.run()

        then:
        0 * taskService.updateTaskStatus(_, _)
        outputStream.toString().contains("Usage: mark-done <id>")
    }

    def "should display help"() {
        given:
        def input = "help\nexit\n"
        System.setIn(new ByteArrayInputStream(input.bytes))

        when:
        cliRunner.run()

        then:
        outputStream.toString().contains("add <title>")
        outputStream.toString().contains("list")
        outputStream.toString().contains("mark-done <id>")
    }

    def "should handle unknown command"() {
        given:
        def input = "unknown\nexit\n"
        System.setIn(new ByteArrayInputStream(input.bytes))

        when:
        cliRunner.run()

        then:
        outputStream.toString().contains("Unknown command. Type 'help' for available commands.")
    }

    def "should handle case-insensitive commands"() {
        given:
        def input = "HELP\nEXIT\n"
        System.setIn(new ByteArrayInputStream(input.bytes))

        when:
        cliRunner.run()

        then:
        outputStream.toString().contains("add <title>")
        outputStream.toString().contains("Exiting...")
    }

    def "should exit on quit command"() {
        given:
        def input = "quit\n"
        System.setIn(new ByteArrayInputStream(input.bytes))

        when:
        cliRunner.run()

        then:
        outputStream.toString().contains("Exiting...")
    }

    def "should exit on exit command"() {
        given:
        def input = "exit\n"
        System.setIn(new ByteArrayInputStream(input.bytes))

        when:
        cliRunner.run()

        then:
        outputStream.toString().contains("Exiting...")
    }

    def "should handle empty input"() {
        given:
        def input = "\n\nexit\n"
        System.setIn(new ByteArrayInputStream(input.bytes))

        when:
        cliRunner.run()

        then:
        noExceptionThrown()
    }

    def "should handle multiple empty lines"() {
        given:
        def input = "\n\n\n\n\nexit\n"
        System.setIn(new ByteArrayInputStream(input.bytes))

        when:
        cliRunner.run()

        then:
        noExceptionThrown()
    }

    def "should handle service exception"() {
        given:
        def input = "add Task\nexit\n"
        System.setIn(new ByteArrayInputStream(input.bytes))

        when:
        cliRunner.run()

        then:
        1 * taskService.addTask("Task") >> { throw new RuntimeException("Service error") }
        outputStream.toString().contains("Error: Service error")
    }

    def "should handle null task from service"() {
        given:
        def input = "add Task\nexit\n"
        System.setIn(new ByteArrayInputStream(input.bytes))

        when:
        cliRunner.run()

        then:
        noExceptionThrown()
        1 * taskService.addTask("Task") >> null

    }

    def "should handle service exception on update"() {
        given:
        def input = "update 1 New\nexit\n"
        System.setIn(new ByteArrayInputStream(input.bytes))

        when:
        cliRunner.run()

        then:
        1 * taskService.updateTaskDescription(1L, "New") >> { throw new RuntimeException("Update failed") }
        outputStream.toString().contains("Error: Update failed")
    }

    def "should handle service exception on delete"() {
        given:
        def input = "delete 1\nexit\n"
        System.setIn(new ByteArrayInputStream(input.bytes))

        when:
        cliRunner.run()

        then:
        1 * taskService.deleteTask(1L) >> { throw new RuntimeException("Delete failed") }
        outputStream.toString().contains("Error: Delete failed")
    }

    def "should handle commands with extra whitespace"() {
        given:
        def task = new Task(1L, "Task with spaces", TaskStatus.TO_DO, LocalDateTime.now(), LocalDateTime.now())
        def input = "  add   Task with spaces  \nexit\n"
        System.setIn(new ByteArrayInputStream(input.bytes))

        when:
        cliRunner.run()

        then:
        1 * taskService.addTask("Task with spaces") >> task
        outputStream.toString().contains("Task added successfully (ID: 1)")
    }
}