package manager;

import model.Epic;
import model.StatusTask;
import model.SubTask;
import model.Task;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class InMemoryHistoryManagerTest {

    @AfterEach
    public void clearStaticId() {
        InMemoryTaskManager.idTask = 0;
    }

    @Test
    void addTask() {
        InMemoryHistoryManager historyManager = new InMemoryHistoryManager();
        InMemoryTaskManager manager = new InMemoryTaskManager();

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
        InMemoryHistoryManager historyManager = new InMemoryHistoryManager();
        InMemoryTaskManager manager = new InMemoryTaskManager();

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
        InMemoryHistoryManager historyManager = new InMemoryHistoryManager();
        InMemoryTaskManager manager = new InMemoryTaskManager();

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
        InMemoryHistoryManager managerHistory = new InMemoryHistoryManager();
        List<Task> history = null;

        assertNull(history);

        history = managerHistory.getHistory();

        assertNotNull(history);
    }
}