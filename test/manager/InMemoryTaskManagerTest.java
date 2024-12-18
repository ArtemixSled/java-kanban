package manager;

import model.Epic;
import model.StatusTask;
import model.SubTask;
import model.Task;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.util.HashMap;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class InMemoryTaskManagerTest {

    @AfterEach
    public void clearStaticId() {
        InMemoryTaskManager.idTask = 0;
    }

    @Test
    void createTask() {
        InMemoryTaskManager manager = new InMemoryTaskManager();

        Task task = manager.createTask(new Task("Test1", "Test1", StatusTask.NEW));

        assertNotNull(task, "Задачу не создал (Объект task содержит null)");
    }

    @Test
    void createEpic() {
        InMemoryTaskManager manager = new InMemoryTaskManager();

        Epic epic = manager.createEpic(new Epic("Test1", "Test1", StatusTask.NEW));

        assertNotNull(epic, "Эпик не создал (Объект epic содержит null)");
    }

    @Test
    void createSubTask() {
        InMemoryTaskManager manager = new InMemoryTaskManager();

        Epic epic = manager.createEpic(new Epic("Test1", "Test1", StatusTask.NEW));
        SubTask subTask = manager.createSubTask(new SubTask("Test1", "Test1", StatusTask.NEW, epic.getId()));

        assertNotNull(subTask, "Подзадачу не создал (Объект subTask содержит null)");
    }

    @Test
    void returnTaskByID() {
        InMemoryTaskManager manager = new InMemoryTaskManager();

        int testId1 = 1;
        int testId22 = 22;

        manager.createTask(new Task("Test1", "Test1", StatusTask.NEW));

        Task task1 = manager.returnTaskByID(testId1);
        Task task2 = manager.returnTaskByID(testId22);

        assertNotNull(task1);
        assertNull(task2);
    }


    @Test
    void returnEpicByID() {
        InMemoryTaskManager manager = new InMemoryTaskManager();

        int testId1 = 1;
        int testId22 = 22;

        manager.createEpic(new Epic("Test1", "Test1", StatusTask.NEW));

        Epic task1 = manager.returnEpicByID(testId1);
        Epic task2 = manager.returnEpicByID(testId22);

        assertNotNull(task1);
        assertNull(task2);
    }

    @Test
    void returnSubTaskByID() {
        InMemoryTaskManager manager = new InMemoryTaskManager();

        int testId2 = 2;
        int testId22 = 22;

        Epic epic = manager.createEpic(new Epic("Test1", "Test1", StatusTask.NEW));
        manager.createSubTask(new SubTask("Test2", "Test2", StatusTask.NEW, epic.getId()));

        SubTask task2 = manager.returnSubTaskByID(testId2);
        SubTask task22 = manager.returnSubTaskByID(testId22);

        assertNotNull(task2);
        assertNull(task22);
    }

    @Test
    void deleteTaskByID() {
        InMemoryTaskManager manager = new InMemoryTaskManager();
        int testId1 = 1;

        manager.createTask(new Task("Test1", "Test1", StatusTask.NEW));

        assertNotNull(manager.returnTaskByID(testId1));

        manager.deleteTaskByID(testId1);

        assertNull(manager.returnTaskByID(testId1));
    }

    @Test
    void deleteEpicByID() {
        InMemoryTaskManager manager = new InMemoryTaskManager();
        int testId1 = 1;

        manager.createEpic(new Epic("Test1", "Test1", StatusTask.NEW));

        assertNotNull(manager.returnEpicByID(testId1));

        manager.deleteEpicByID(testId1);

        assertNull(manager.returnEpicByID(testId1));
    }

    @Test
    void deleteSubTaskByID() {
        InMemoryTaskManager manager = new InMemoryTaskManager();

        int testId2 = 2;

        Epic epic = manager.createEpic(new Epic("Test1", "Test1", StatusTask.NEW));
        manager.createSubTask(new SubTask("Test2", "Test2", StatusTask.NEW, epic.getId()));

        assertNotNull(manager.returnSubTaskByID(testId2));

        manager.deleteSubTaskByID(testId2);

        assertNull(manager.returnSubTaskByID(testId2));
    }

    @Test
    void getAllTasks() {
        InMemoryTaskManager manager = new InMemoryTaskManager();

        Task task1 = new Task("Task 1", "Description 1", StatusTask.NEW);
        Task task2 = new Task("Task 2", "Description 2", StatusTask.NEW);

        manager.createTask(task1);
        manager.createTask(task2);

        List<Task> allTasks = manager.getAllTasks();

        assertNotNull(allTasks, "Список задач пустой");
        assertEquals(2, allTasks.size(), "Количество задач должно быть равно 2");
        assertTrue(allTasks.contains(task1), "Список задач должен содержать task1");
        assertTrue(allTasks.contains(task2), "Список задач должен содержать task2");
    }

    @Test
    void getAllEpics() {
        InMemoryTaskManager manager = new InMemoryTaskManager();

        Epic epic1 = new Epic("Task 1", "Description 1", StatusTask.NEW);
        Epic epic2 = new Epic("Task 2", "Description 2", StatusTask.NEW);

        manager.createEpic(epic1);
        manager.createEpic(epic2);

        List<Epic> allTasks = manager.getAllEpics();

        assertNotNull(allTasks, "Список задач пустой");
        assertEquals(2, allTasks.size(), "Количество задач должно быть равно 2");
        assertTrue(allTasks.contains(epic1), "Список задач должен содержать epic1");
        assertTrue(allTasks.contains(epic2), "Список задач должен содержать epic2");
    }

    @Test
    void getAllSubTask() {
        InMemoryTaskManager manager = new InMemoryTaskManager();

        Epic epic = new Epic("Task", "Description", StatusTask.NEW);
        manager.createEpic(epic);

        SubTask subTask1 = new SubTask("Task 1", "Description 1", StatusTask.NEW, epic.getId());
        SubTask subTask2 = new SubTask("Task 2", "Description 2", StatusTask.NEW, epic.getId());

        manager.createSubTask(subTask1);
        manager.createSubTask(subTask2);

        List<SubTask> allTasks = manager.getAllSubTask();

        assertNotNull(allTasks, "Список задач пустой");
        assertEquals(2, allTasks.size(), "Количество задач должно быть равно 2");
        assertTrue(allTasks.contains(subTask1), "Список задач должен содержать epic1");
        assertTrue(allTasks.contains(subTask2), "Список задач должен содержать epic2");
    }

    @Test
    void getAllSubTaskByEpic() {
        InMemoryTaskManager manager = new InMemoryTaskManager();

        Epic epic = new Epic("Task", "Description", StatusTask.NEW);
        manager.createEpic(epic);

        SubTask subTask1 = new SubTask("Task 1", "Description 1", StatusTask.NEW, epic.getId());
        SubTask subTask2 = new SubTask("Task 2", "Description 2", StatusTask.NEW, epic.getId());

        manager.createSubTask(subTask1);
        manager.createSubTask(subTask2);

        List<SubTask> allTasks = manager.getAllSubTaskByEpic(epic);

        assertNotNull(allTasks, "Список задач пустой");
        assertEquals(2, allTasks.size(), "Количество задач должно быть равно 2");
        assertTrue(allTasks.contains(subTask1), "Список задач должен содержать epic1");
        assertTrue(allTasks.contains(subTask2), "Список задач должен содержать epic2");

    }

    @Test
    void getInMemoryHistoryManager() {
        InMemoryTaskManager manager = new InMemoryTaskManager();
        InMemoryHistoryManager inMemoryHistoryManager = null;

        assertNull(inMemoryHistoryManager);

        inMemoryHistoryManager = manager.getInMemoryHistoryManager();

        assertNotNull(inMemoryHistoryManager);
    }

    @Test
    void equalsTask() {
        InMemoryTaskManager manager = new InMemoryTaskManager();

        Task taskOne = manager.createTask(new Task("Test1", "Test1", StatusTask.NEW));
        Task taskTwo = manager.createTask(new Task("Test1", "Test1", StatusTask.NEW));

        taskOne.setId(1);
        taskTwo.setId(2);

        assertNotEquals(taskOne, taskTwo);

        taskTwo.setId(1);

        assertEquals(taskOne, taskTwo);
    }

    @Test
    void equalsTaskHeirs() {
        InMemoryTaskManager manager = new InMemoryTaskManager();

        Epic taskOne = manager.createEpic(new Epic("Test1", "Test1", StatusTask.NEW));
        SubTask taskTwo = manager.createSubTask(new SubTask("Test1", "Test1", StatusTask.NEW, taskOne.getId()));

        taskOne.setId(1);
        taskTwo.setId(2);

        assertNotEquals(taskOne, taskTwo);

        taskTwo.setId(1);

        assertEquals(taskOne, taskTwo);
    }

    @Test
    public void testTaskIdConflict() {
        InMemoryTaskManager manager = new InMemoryTaskManager();

        Task task1 = new Task("Test1", "Test1", StatusTask.NEW);
        manager.createTask(task1);

        int originalTaskId = task1.getId();

        Task task2 = new Task("Test2", "Test2", StatusTask.NEW);
        manager.createTask(task2);

        List<Task> allTasks = manager.getAllTasks();

        assertEquals(2, allTasks.size());

        boolean containsOriginalTaskId = false;


        for (Task task : allTasks) {
            if (task.getId() == originalTaskId) {
                containsOriginalTaskId = true;
                break;
            }
        }
        assertTrue(containsOriginalTaskId);

        assertNotEquals(originalTaskId, task2.getId());
    }

    @Test
    void immutabilityTask() {
        InMemoryTaskManager manager = new InMemoryTaskManager();

        Task task1 = new Task("Test1", "Test1", StatusTask.NEW);
        String nameTask = task1.getNameTask();
        String description = task1.getDescription();
        StatusTask statusTask = task1.getStatusTask();

        manager.createTask(task1);

        task1 = null;
        task1 = manager.returnTaskByID(1);

        assertEquals(nameTask, task1.getNameTask());
        assertEquals(description, task1.getDescription());
        assertEquals(statusTask, task1.getStatusTask());
    }

    @Test
    void subTaskCannotIsEpic() {
        InMemoryTaskManager manager = new InMemoryTaskManager();

        Epic epic = manager.createEpic(new Epic("Test1", "Test1", StatusTask.NEW));
        SubTask subTask = manager.createSubTask(new SubTask("Test1", "Test1", StatusTask.NEW, epic.getId()));

        subTask.setId(1);
        assertNotNull(manager.createSubTask(subTask));
    }
}