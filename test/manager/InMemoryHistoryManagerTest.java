package manager;

import model.Epic;
import model.StatusTask;
import model.SubTask;
import model.Task;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class InMemoryHistoryManagerTest {

    private InMemoryHistoryManager historyManager;
    private InMemoryTaskManager manager;
    @BeforeEach
    public void setUp() {
        historyManager = new InMemoryHistoryManager();
        manager = new InMemoryTaskManager();
    }

    @Test
    public void testMaxSizeHistory() {
        for (int i = 0; i < 12; i++) {
            Task task = new Task("Task " + i, "Description " + i, StatusTask.NEW);
            task.setId(i);
            historyManager.add(task);
        }

        List<Task> history = historyManager.getHistory();
        assertEquals(10, history.size());
        assertEquals(2, history.get(0).getId());
    }

    @Test
    void removeTask() {
        List<Task> history = historyManager.getHistory();

        Task task = new Task("task", "task", StatusTask.NEW);
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
    void addTask() {
        Task task = new Task("task", "task", StatusTask.NEW);
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
        Epic epic = new Epic("epic", "epic", StatusTask.NEW);
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
    void addSubTask() {
        Epic epic = new Epic("epic", "epic", StatusTask.NEW);
        manager.createEpic(epic);

        SubTask subTask = new SubTask("Task", "Description", StatusTask.NEW, epic.getId());
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