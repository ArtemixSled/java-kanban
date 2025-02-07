package manager;
import model.*;
import org.junit.jupiter.api.Test;
import java.io.File;
import java.io.IOException;

import static org.junit.jupiter.api.Assertions.*;

class FileBackedTaskManagerTest extends AbstractTaskManagerTest<FileBackedTaskManager> {

    private File tempFile;

    @Override
    protected FileBackedTaskManager createTaskManager() {
        try {
            tempFile = File.createTempFile("tasks", "txt");
            return new FileBackedTaskManager(tempFile);
        } catch (IOException e) {
            throw new RuntimeException("Не удалось создать временный файл", e);
        }
    }

    @Override
    public void tearDown() {
        super.tearDown();
        if (tempFile != null && tempFile.exists()) {
            assertTrue(tempFile.delete(), "Не удалось удалить временный файл");
        }
    }

    @Test
    void saveAndLoadEmptyFile() {
        manager.save();

        FileBackedTaskManager loadedManager = FileBackedTaskManager.loadFromFile(tempFile);

        assertTrue(loadedManager.getAllTasks().isEmpty());
        assertTrue(loadedManager.getAllEpics().isEmpty());
        assertTrue(loadedManager.getAllSubTask().isEmpty());
    }

    @Test
    void saveAndLoadTasks() {
        Task task1 = new Task("Task", "Description", StatusTask.NEW);
        Epic epic1 = new Epic("Epic", "Description", StatusTask.IN_PROGRESS);


        manager.createTask(task1);
        manager.createEpic(epic1);
        SubTask subTask1 = new SubTask("SubTask", "Description", StatusTask.DONE, epic1.getId());
        manager.createSubTask(subTask1);

        manager.save();

        FileBackedTaskManager loadedManager = FileBackedTaskManager.loadFromFile(tempFile);

        assertEquals(1, loadedManager.getAllTasks().size());
        assertEquals(1, loadedManager.getAllEpics().size());
        assertEquals(1, loadedManager.getAllSubTask().size());

        Task loadedTask = loadedManager.returnTaskByID(1);
        assertNotNull(loadedTask);
        assertEquals("Task", loadedTask.getNameTask());

        Epic loadedEpic = loadedManager.returnEpicByID(2);
        assertNotNull(loadedEpic);
        assertEquals("Epic", loadedEpic.getNameTask());

        SubTask loadedSubTask = loadedManager.returnSubTaskByID(3);
        assertNotNull(loadedSubTask);
        assertEquals("SubTask", loadedSubTask.getNameTask());
        assertEquals(epic1.getId(), loadedSubTask.getIdEpic());
    }

    @Test
    void saveAndLoadStatusUpdates() {
        Epic epic = new Epic("Epic", "Description", StatusTask.NEW);

        manager.createEpic(epic);
        SubTask subTask1 = new SubTask("SubTask 1", "Description", StatusTask.NEW, epic.getId());
        SubTask subTask2 = new SubTask("SubTask 2", "Description", StatusTask.IN_PROGRESS, epic.getId());
        manager.createSubTask(subTask1);
        manager.createSubTask(subTask2);

        manager.updateSubTask(subTask2);

        manager.save();

        FileBackedTaskManager loadedManager = FileBackedTaskManager.loadFromFile(tempFile);

        Epic loadedEpic = loadedManager.returnEpicByID(epic.getId());
        assertNotNull(loadedEpic);
        assertEquals(StatusTask.IN_PROGRESS, loadedEpic.getStatusTask());

        SubTask loadedSubTask = loadedManager.returnSubTaskByID(subTask2.getId());
        assertNotNull(loadedSubTask);
        assertEquals(StatusTask.IN_PROGRESS, loadedSubTask.getStatusTask());
    }
}