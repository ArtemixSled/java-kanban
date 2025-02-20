package manager;
import model.Epic;
import model.StatusTask;
import model.SubTask;
import model.Task;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.io.IOException;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;
import java.util.TreeSet;

import static org.junit.jupiter.api.Assertions.*;

public abstract class AbstractTaskManagerTest<T extends TaskManager> {

    protected T manager;

    LocalDateTime newMillennium;

    @BeforeEach
    public void setUp() {
        manager = createTaskManager();
        newMillennium = LocalDateTime.of(2000, 1, 1, 0, 0, 0, 0);
    }

    @AfterEach
    public void tearDown() {
        if (manager instanceof FileBackedTaskManager) {
            FileBackedTaskManager fileBackedTaskManager = (FileBackedTaskManager) manager;
            File file = fileBackedTaskManager.getFile();
            if (file.exists()) {
                file.delete();
            }
        }
    }

    protected abstract T createTaskManager();

    @Test
    void createTask() {
        Task task = manager.createTask(new Task("Test1", "Test1", StatusTask.NEW, newMillennium));

        assertNotNull(task, "Задачу не создал (Объект task содержит null)");
    }

    @Test
    void createEpic() {
        Epic epic = manager.createEpic(new Epic("Test1", "Test1", StatusTask.NEW, newMillennium));

        assertNotNull(epic, "Эпик не создал (Объект epic содержит null)");
    }

    @Test
    void createSubTask() {
        Epic epic = manager.createEpic(new Epic("Test1", "Test1", StatusTask.NEW, newMillennium));
        SubTask subTask = manager.createSubTask(new SubTask("Test1", "Test1", StatusTask.NEW, epic.getId(), newMillennium));

        assertNotNull(subTask, "Подзадачу не создал (Объект subTask содержит null)");
    }

    @Test
    void returnTaskByID() {
        int testId1 = 1;
        int testId22 = 22;

        manager.createTask(new Task("Test1", "Test1", StatusTask.NEW, newMillennium));

        Task task1 = manager.returnTaskByID(testId1);
        Task task2 = manager.returnTaskByID(testId22);

        assertNotNull(task1);
        assertNull(task2);
    }

    @Test
    void returnEpicByID() {
        int testId1 = 1;
        int testId22 = 22;

        manager.createEpic(new Epic("Test1", "Test1", StatusTask.NEW, newMillennium));

        Epic task1 = manager.returnEpicByID(testId1);
        Epic task2 = manager.returnEpicByID(testId22);

        assertNotNull(task1);
        assertNull(task2);
    }

    @Test
    void returnSubTaskByID() {
        int testId2 = 2;
        int testId22 = 22;

        Epic epic = manager.createEpic(new Epic("Test1", "Test1", StatusTask.NEW, newMillennium));
        manager.createSubTask(new SubTask("Test2", "Test2", StatusTask.NEW, epic.getId(), newMillennium));

        SubTask task2 = manager.returnSubTaskByID(testId2);
        SubTask task22 = manager.returnSubTaskByID(testId22);

        assertNotNull(task2);
        assertNull(task22);
    }

    @Test
    void deleteTaskByID() {
        int testId1 = 1;

        manager.createTask(new Task("Test1", "Test1", StatusTask.NEW, newMillennium));

        assertNotNull(manager.returnTaskByID(testId1));

        manager.deleteTaskByID(testId1);

        assertNull(manager.returnTaskByID(testId1));
    }

    @Test
    void deleteEpicByID() {
        int testId1 = 1;

        manager.createEpic(new Epic("Test1", "Test1", StatusTask.NEW, newMillennium));

        assertNotNull(manager.returnEpicByID(testId1));

        manager.deleteEpicByID(testId1);

        assertNull(manager.returnEpicByID(testId1));
    }

    @Test
    void deleteSubTaskByID() {
        int testId2 = 2;

        Epic epic = manager.createEpic(new Epic("Test1", "Test1", StatusTask.NEW, newMillennium));
        manager.createSubTask(new SubTask("Test2", "Test2", StatusTask.NEW, epic.getId(), newMillennium));

        assertNotNull(manager.returnSubTaskByID(testId2));

        manager.deleteSubTaskByID(testId2);

        assertNull(manager.returnSubTaskByID(testId2));
    }

    @Test
    void getAllTasks() {
        Task task1 = new Task("Task 1", "Description 1", StatusTask.NEW,
                LocalDateTime.of(2001, 1, 1, 0, 0, 0, 0), Duration.ofHours(6));
        Task task2 = new Task("Task 2", "Description 2", StatusTask.NEW,
                LocalDateTime.of(2002, 1, 1, 0, 0, 0, 0), Duration.ofHours(6));

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
        Epic epic1 = new Epic("Task 1", "Description 1", StatusTask.NEW, newMillennium);
        Epic epic2 = new Epic("Task 2", "Description 2", StatusTask.NEW, newMillennium);

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
        Epic epic = new Epic("Task", "Description", StatusTask.NEW, newMillennium);
        manager.createEpic(epic);

        SubTask subTask1 = new SubTask("Task 1", "Description 1", StatusTask.NEW, epic.getId(),
                LocalDateTime.of(2005, 1, 1, 0, 0, 0, 0), Duration.ofHours(6));
        SubTask subTask2 = new SubTask("Task 2", "Description 2", StatusTask.NEW, epic.getId(),
                LocalDateTime.of(2001, 1, 1, 0, 0, 0, 0), Duration.ofHours(6));

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
        Epic epic = new Epic("Task", "Description", StatusTask.NEW, newMillennium);
        manager.createEpic(epic);

        SubTask subTask1 = new SubTask("Task 1", "Description 1", StatusTask.NEW, epic.getId(),
                LocalDateTime.of(2001, 1, 1, 0, 0, 0, 0), Duration.ofHours(6));
        SubTask subTask2 = new SubTask("Task 2", "Description 2", StatusTask.NEW, epic.getId(),
                LocalDateTime.of(2003, 1, 1, 0, 0, 0, 0), Duration.ofHours(6));

        manager.createSubTask(subTask1);
        manager.createSubTask(subTask2);

        List<SubTask> allTasks = manager.getAllSubTaskByEpic(epic);

        assertNotNull(allTasks, "Список задач пустой");
        assertEquals(2, allTasks.size(), "Количество задач должно быть равно 2");
        assertTrue(allTasks.contains(subTask1), "Список задач должен содержать epic1");
        assertTrue(allTasks.contains(subTask2), "Список задач должен содержать epic2");

    }

    @Test
    void getHistory() {
        List<Task> history = null;

        assertNull(history);

        history = manager.getHistory();

        assertNotNull(history);
    }

    @Test
    void equalsTask() {
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
        Epic taskOne = manager.createEpic(new Epic("Test1", "Test1", StatusTask.NEW, newMillennium));
        SubTask taskTwo = manager.createSubTask(new SubTask("Test1", "Test1", StatusTask.NEW, taskOne.getId(), newMillennium));

        taskOne.setId(1);
        taskTwo.setId(2);

        assertNotEquals(taskOne, taskTwo);

        taskTwo.setId(1);

        assertEquals(taskOne, taskTwo);
    }

    @Test
    public void testTaskIdConflict() {
        Task task1 = new Task("Test1", "Test1", StatusTask.NEW, newMillennium, Duration.ofHours(6));
        manager.createTask(task1);

        int originalTaskId = task1.getId();

        Task task2 = new Task("Test2", "Test2", StatusTask.NEW,
                LocalDateTime.of(2001, 1, 1, 0, 0, 0, 0), Duration.ofHours(6));
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
        Task task1 = new Task("Test1", "Test1", StatusTask.NEW, newMillennium);
        String nameTask = task1.getNameTask();
        String description = task1.getDescription();
        StatusTask statusTask = task1.getStatusTask();

        manager.createTask(task1);

        task1 = manager.returnTaskByID(1);

        assertEquals(nameTask, task1.getNameTask());
        assertEquals(description, task1.getDescription());
        assertEquals(statusTask, task1.getStatusTask());
    }

    @Test
    void subTaskCannotIsEpic() {
        Epic epic = manager.createEpic(new Epic("Test1", "Test1", StatusTask.NEW, newMillennium));
        SubTask subTask = manager.createSubTask(new SubTask("Test1", "Test1", StatusTask.NEW,
                epic.getId(), newMillennium));

        subTask.setId(1);
        assertNull(manager.createSubTask(subTask));
    }

    @Test
    void testTaskDuration() {
        Epic epic = manager.createEpic(new Epic("Test1", "Test1", StatusTask.NEW,
                LocalDateTime.of(2000, 1, 1, 0, 0, 0, 0)));

        SubTask subTask1 = manager.createSubTask(new SubTask("Test1", "Test1", StatusTask.NEW, epic.getId(),
                LocalDateTime.of(2000, 5, 1, 0, 0, 0, 0), Duration.ofHours(6)));
        subTask1.setDuration(Duration.ofHours(20));

        SubTask subTask2 = manager.createSubTask(new SubTask("Test2", "Test2", StatusTask.NEW, epic.getId(),
                LocalDateTime.of(2005, 7, 1, 0, 0, 0, 0), Duration.ofHours(6)));
        subTask2.setDuration(Duration.ofHours(10));

        manager.refreshEpicInfo(epic);

        assertEquals(LocalDateTime.of(2000, 5, 1, 0, 0, 0, 0), epic.getStartTime());

        assertEquals(LocalDateTime.of(2005, 7, 1, 10, 0, 0, 0), epic.getEndTime());

        Duration expectedDuration = Duration.between(epic.getStartTime(), epic.getEndTime());
        assertEquals(expectedDuration, epic.getDuration());
    }

    @Test
    void testGetPrioritizedTasks() {
        Epic epic = manager.createEpic(new Epic("Epic1", "Test1", StatusTask.NEW));

        SubTask subTask1 = manager.createSubTask(new SubTask("SubTask1", "Test1", StatusTask.NEW, epic.getId(),
                LocalDateTime.of(2001, 7, 1, 0, 0, 0, 0), Duration.ofHours(6)));
        subTask1.setDuration(Duration.ofHours(20));

        SubTask subTask2 = manager.createSubTask(new SubTask("SubTask2", "Test1", StatusTask.NEW, epic.getId(),
                LocalDateTime.of(1999, 7, 1, 0, 0, 0, 0), Duration.ofHours(6)));
        subTask2.setDuration(Duration.ofHours(10));

        manager.createTask(new Task("Task1", "Test1", StatusTask.NEW,
                LocalDateTime.of(2006, 7, 1, 0, 0, 0, 0), Duration.ofHours(6)));

        manager.createTask(new Task("Task2", "Test1", StatusTask.NEW,
                LocalDateTime.of(1998, 7, 1, 0, 0, 0, 0), Duration.ofHours(6)));

        Task taskCH = manager.createTask(new Task("Чучело", "Test1", StatusTask.NEW, null));

        TreeSet<Task> listPrioritizedTasks = manager.getPrioritizedTasks();

        assertEquals(4, listPrioritizedTasks.size(), "Количество задач должно быть 4");

        Task[] tasksArray = listPrioritizedTasks.toArray(new Task[0]);

        assertEquals(LocalDateTime.of(1998, 7, 1, 0, 0, 0, 0), tasksArray[0].getStartTime());
        assertEquals(LocalDateTime.of(1999, 7, 1, 0, 0, 0, 0), tasksArray[1].getStartTime());
        assertEquals(LocalDateTime.of(2001, 7, 1, 0, 0, 0, 0), tasksArray[2].getStartTime());
        assertEquals(LocalDateTime.of(2006, 7, 1, 0, 0, 0, 0), tasksArray[3].getStartTime());

        assertNull(taskCH.getStartTime());
    }

    @Test
    void testIsTimeIntersections() {
        Task task1 = manager.createTask(new Task("Task1", "Test1", StatusTask.NEW,
                LocalDateTime.of(2000, 1, 1, 0, 0, 0, 0), Duration.ofHours(6)));

        Task task2 = manager.createTask(new Task("Task2", "Test2", StatusTask.NEW,
                LocalDateTime.of(2005, 7, 1, 0, 0, 0, 0), Duration.ofHours(6)));

        Task task3 = manager.createTask(new Task("Task3", "Test3", StatusTask.NEW,
                LocalDateTime.of(2000, 1, 1, 3, 0, 0, 0), Duration.ofHours(4)));

        Task task4 = manager.createTask(new Task("Task4", "Test4", StatusTask.NEW,
                LocalDateTime.of(2010, 1, 1, 0, 0, 0, 0), Duration.ofHours(6)));

        Task task5 = manager.createTask(new Task("Task5", "Test5", StatusTask.NEW, null));

        assertFalse(manager.isTimeIntersections(task1), "Задача task1 не должна пересекаться с другими задачами.");

        assertFalse(manager.isTimeIntersections(task2), "Задача task2 не должна пересекаться с другими задачами.");

        assertNull(task3, "Задачу не создал, т.к. при создании сработала проверка на пересечение");

        assertFalse(manager.isTimeIntersections(task4), "Задача task4 не должна пересекаться с другими задачами.");

        assertFalse(manager.isTimeIntersections(task5), "Задача с null временем начала не должна пересекаться.");
    }

    @Test
    void testUpdateStatusEpicNew() {
        Epic epic = manager.createEpic(new Epic("Epic1", "Test1", StatusTask.NEW));

        SubTask subTask1 = new SubTask("Subtask 1", "Description", StatusTask.NEW, epic.getId(),
                LocalDateTime.of(2001, 7, 1, 0, 0, 0, 0), Duration.ofHours(6));
        SubTask subTask2 = new SubTask("Subtask 2", "Description", StatusTask.NEW, epic.getId(),
                LocalDateTime.of(2005, 7, 1, 0, 0, 0, 0), Duration.ofHours(6));

        manager.createSubTask(subTask1);
        manager.createSubTask(subTask2);

        assertEquals(StatusTask.NEW, epic.getStatusTask());
    }

    @Test
    void testUpdateStatusEpicDone() {
        Epic epic = manager.createEpic(new Epic("Epic1", "Test1", StatusTask.NEW));

        SubTask subTask1 = new SubTask("Subtask 1", "Description", StatusTask.DONE, epic.getId(),
                LocalDateTime.of(2001, 7, 1, 0, 0, 0, 0), Duration.ofHours(6));
        SubTask subTask2 = new SubTask("Subtask 2", "Description", StatusTask.DONE, epic.getId(),
                LocalDateTime.of(2005, 7, 1, 0, 0, 0, 0), Duration.ofHours(6));

        manager.createSubTask(subTask1);
        manager.createSubTask(subTask2);

        assertEquals(StatusTask.DONE, epic.getStatusTask());
    }

    @Test
    void testUpdateStatusEpicNewAndDone() {
        Epic epic = manager.createEpic(new Epic("Epic1", "Test1", StatusTask.NEW));

        SubTask subTask1 = new SubTask("Subtask 1", "Description", StatusTask.NEW, epic.getId(),
                LocalDateTime.of(2001, 7, 1, 0, 0, 0, 0), Duration.ofHours(6));
        SubTask subTask2 = new SubTask("Subtask 2", "Description", StatusTask.DONE, epic.getId(),
                LocalDateTime.of(2005, 7, 1, 0, 0, 0, 0), Duration.ofHours(6));

        manager.createSubTask(subTask1);
        manager.createSubTask(subTask2);

        assertEquals(StatusTask.IN_PROGRESS, epic.getStatusTask());
    }

    @Test
    void testUpdateStatusEpicInProgress() {
        Epic epic = manager.createEpic(new Epic("Epic1", "Test1", StatusTask.NEW));

        SubTask subTask1 = new SubTask("Subtask 1", "Description", StatusTask.IN_PROGRESS, epic.getId(),
                LocalDateTime.of(2001, 7, 1, 0, 0, 0, 0), Duration.ofHours(6));
        SubTask subTask2 = new SubTask("Subtask 2", "Description", StatusTask.IN_PROGRESS, epic.getId(),
                LocalDateTime.of(2005, 7, 1, 0, 0, 0, 0), Duration.ofHours(6));

        manager.createSubTask(subTask1);
        manager.createSubTask(subTask2);

        assertEquals(StatusTask.IN_PROGRESS, epic.getStatusTask());
    }
}
