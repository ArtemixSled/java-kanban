package manager;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class ManagersTest {

    @Test
    void getDefaultHistory() {
        Managers<TaskManager> managers = new Managers<>();
        HistoryManager InMemoryHistoryManager = null;

        assertNull(InMemoryHistoryManager);

        InMemoryHistoryManager = managers.getDefaultHistory();

        assertNotNull(InMemoryHistoryManager);
    }

    @Test
    void getDefault() {
        Managers<TaskManager> managers = new Managers<>();
        TaskManager taskManager = null;

        assertNull(taskManager);

        taskManager = managers.getDefault();

        assertNotNull(taskManager);
    }
}