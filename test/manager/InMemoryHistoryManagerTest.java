package manager;

import model.Epic;
import model.StatusTask;
import model.SubTask;
import model.Task;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;
import static org.junit.jupiter.api.Assertions.*;

class InMemoryHistoryManagerTest {

    private InMemoryHistoryManager historyManager;
    private InMemoryTaskManager manager;
    LocalDateTime newMillennium;

    @BeforeEach
    public void setUp() {
        historyManager = new InMemoryHistoryManager();
        manager = new InMemoryTaskManager();
        newMillennium = LocalDateTime.of(2000, 1, 1, 0, 0, 0, 0);
    }

    @Test
    void removeTask() {
        List<Task> history = historyManager.getHistory();

        Task task = new Task("task", "task", StatusTask.NEW, newMillennium);
        manager.createTask(task);

        assertTrue(history.isEmpty());

        task = manager.returnTaskByID(1);
        historyManager.add(task);

        history = historyManager.getHistory();
        assertFalse(history.isEmpty());

        manager.deleteTaskByID(1);
        historyManager.remove(1);

        history = historyManager.getHistory();
        assertTrue(history.isEmpty());
    }

    @Test
    void removeTaskFromHistoryAtBeginning() {
        Task task1 = new Task("task1", "task1", StatusTask.NEW,
                LocalDateTime.of(1997, 7, 1, 0, 0, 0, 0), Duration.ofHours(6));
        Task task2 = new Task("task2", "task2", StatusTask.NEW,
                LocalDateTime.of(1999, 7, 1, 0, 0, 0, 0), Duration.ofHours(6));
        manager.createTask(task1);
        manager.createTask(task2);

        historyManager.add(task1);
        historyManager.add(task2);

        historyManager.remove(task1.getId());
        List<Task> history = historyManager.getHistory();

        assertEquals(1, history.size());
        assertEquals(task2, history.get(0));
    }

    @Test
    void removeTaskFromHistoryAtEnd() {
        Task task1 = new Task("task1", "task1", StatusTask.NEW,
                LocalDateTime.of(1997, 7, 1, 0, 0, 0, 0), Duration.ofHours(6));
        Task task2 = new Task("task2", "task2", StatusTask.NEW,
                LocalDateTime.of(1997, 7, 1, 0, 0, 0, 0), Duration.ofHours(6));
        manager.createTask(task1);
        manager.createTask(task2);

        historyManager.add(task1);
        historyManager.add(task2);

        historyManager.remove(task2.getId());
        List<Task> history = historyManager.getHistory();

        assertEquals(1, history.size());
        assertEquals(task1, history.get(0));
    }

    @Test
    void removeTaskFromHistoryInMiddle() {
        Task task1 = new Task("task1", "task1", StatusTask.NEW,
                LocalDateTime.of(1997, 7, 1, 0, 0, 0, 0), Duration.ofHours(6));
        Task task2 = new Task("task2", "task2", StatusTask.NEW,
                LocalDateTime.of(1998, 7, 1, 0, 0, 0, 0), Duration.ofHours(6));
        Task task3 = new Task("task3", "task3", StatusTask.NEW,
                LocalDateTime.of(1999, 7, 1, 0, 0, 0, 0), Duration.ofHours(6));
        manager.createTask(task1);
        manager.createTask(task2);
        manager.createTask(task3);

        historyManager.add(task1);
        historyManager.add(task2);
        historyManager.add(task3);

        historyManager.remove(task2.getId());
        List<Task> history = historyManager.getHistory();

        assertEquals(2, history.size());
        assertEquals(task1, history.get(0));
        assertEquals(task3, history.get(1));
    }

    @Test
    void addTask() {
        Task task = new Task("task", "task", StatusTask.NEW, newMillennium);
        manager.createTask(task);

        List<Task> history = historyManager.getHistory();
        assertTrue(history.isEmpty());

        task = manager.returnTaskByID(1);
        historyManager.add(task);

        history = historyManager.getHistory();
        assertFalse(history.isEmpty());

        task.setNameTask("Task 2");
        assertEquals(task, history.get(0));
    }

    @Test
    void addEpic() {
        Epic epic = new Epic("epic", "epic", StatusTask.NEW, newMillennium);
        manager.createEpic(epic);

        List<Task> history = historyManager.getHistory();
        assertTrue(history.isEmpty());

        epic = manager.returnEpicByID(1);
        historyManager.add(epic);

        history = historyManager.getHistory();
        assertFalse(history.isEmpty());

        epic.setNameTask("Task 2");
        assertEquals(epic, history.get(0));
    }

    @Test
    void getHistoryWhenEmpty() {
        List<Task> history = historyManager.getHistory();
        assertTrue(history.isEmpty());

        Task task = new Task("task", "task", StatusTask.NEW, newMillennium);
        manager.createTask(task);

        historyManager.add(task);
        history = historyManager.getHistory();

        assertFalse(history.isEmpty());
        assertEquals(task, history.get(0));
    }

    @Test
    void getHistoryWhenNotEmpty() {
        Task task = new Task("task", "task", StatusTask.NEW, newMillennium);
        manager.createTask(task);

        historyManager.add(task);
        List<Task> history = historyManager.getHistory();

        assertFalse(history.isEmpty());
        assertEquals(task, history.get(0));
    }
    @Test
    void addSubTask() {
        Epic epic = new Epic("epic", "epic", StatusTask.NEW, newMillennium);
        manager.createEpic(epic);

        SubTask subTask = new SubTask("Task", "Description", StatusTask.NEW, epic.getId(), newMillennium);
        manager.createSubTask(subTask);

        List<Task> history = historyManager.getHistory();
        assertTrue(history.isEmpty());

        epic = manager.returnEpicByID(1);
        historyManager.add(epic);

        subTask = manager.returnSubTaskByID(2);
        historyManager.add(subTask);

        history = historyManager.getHistory();
        assertFalse(history.isEmpty());

        subTask.setNameTask("Task 2");
        assertEquals(subTask, history.get(1));

    }

    @Test
    void getHistory() {
        List<Task> history = null;

        assertNull(history);

        history = historyManager.getHistory();

        assertNotNull(history);
    }
}