package manager;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class ManagersTest {

    @Test
    void getDefaultHistory() {
        HistoryManager InMemoryHistoryManager = null;

        assertNull(InMemoryHistoryManager);

        InMemoryHistoryManager = Managers.getDefaultHistory();

        assertNotNull(InMemoryHistoryManager);
    }

    @Test
    void getDefault() {
        TaskManager taskManager = null;

        assertNull(taskManager);

        taskManager = Managers.getDefault();

        assertNotNull(taskManager);
    }
}