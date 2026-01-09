package com.example.tasktrackercli.service


import com.example.tasktrackercli.model.TaskStatus
import spock.lang.Specification
import spock.lang.Subject

class TaskServiceSpec extends Specification {

    @Subject
    TaskService taskService = new TaskService()

    def "should add a new task"() {
        when:
        def task = taskService.addTask("Buy groceries")

        then:
        noExceptionThrown()
        task != null
        task.description == "Buy groceries"
        task.id != null;
        task.status == TaskStatus.TO_DO
        task.createdAt != null
    }



    def "getAllTasks"(){
        given:
        taskService.addTask("task 1")
        taskService.addTask("task 2")

        when:
        def tasks = taskService.getAllTasks()

        then:
        noExceptionThrown()
        tasks.size() == 2
        tasks[0].description == "task 1"
        tasks[1].description == "task 2"
    }

    def "getTasksFromStatus TO-DO"(){
        given:
        taskService.addTask("task 1")
        taskService.updateTaskStatus(1, TaskStatus.TO_DO)

        when:
        def tasks = taskService.getTasksFromStatus(TaskStatus.TO_DO)

        then:
        noExceptionThrown()
        tasks.size() == 1
        tasks[0].status == TaskStatus.TO_DO
    }

    def "getTasksFromStatus IN-PROGRESS"(){
        given:
        taskService.addTask("task 1")
        taskService.updateTaskStatus(1, TaskStatus.IN_PROGRESS)

        when:
        def tasks = taskService.getTasksFromStatus(TaskStatus.IN_PROGRESS)

        then:
        noExceptionThrown()
        tasks.size() == 1
        tasks[0].status == TaskStatus.IN_PROGRESS
    }

    def "getTasksFromStatus DONE"(){
        given:
        taskService.addTask("task 1")
        taskService.updateTaskStatus(1, TaskStatus.DONE)

        when:
        def tasks = taskService.getTasksFromStatus(TaskStatus.DONE)

        then:
        noExceptionThrown()
        tasks.size() == 1
        tasks[0].status == TaskStatus.DONE
    }

    def "getTasksFromStatus thrown exception"(){
        given:
        taskService.addTask("task 1")
        taskService.updateTaskStatus(1, TaskStatus.DONE)

        when:
        def tasks = taskService.getTasksFromStatus(TaskStatus.IN_PROGRESS)

        then:
        noExceptionThrown()
    }

    def "updateTaskStatus is not compatible"(){
        given:
        taskService.addTask("task 1")

        when:
        taskService.updateTaskStatus(2, TaskStatus.DONE)

        then:
        thrown(IllegalArgumentException)

    }


    def "deleteTask"(){
        given:
        taskService.addTask("task 1")
        def tasks = taskService.getAllTasks()

        when:
        taskService.deleteTask(1)

        then:
        noExceptionThrown()
        tasks.size() == 0
    }

    def "updateTaskDescription"(){
        given:
        taskService.addTask("task 1")

        when:
        def task = taskService.updateTaskDescription(1, "task 1 updated")

        then:
        noExceptionThrown()
        task.updatedAt != null
        task.description == "task 1 updated"
    }

    def "updateTaskDescription exception"(){
        given:
        taskService.addTask("task 1")

        when:
        taskService.updateTaskDescription(2, "task 1 updated")

        then:
        thrown(IllegalArgumentException)
    }

}
