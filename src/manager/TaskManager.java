package manager;

import model.Epic;
import model.SubTask;
import model.Task;
import java.util.List;
import java.util.TreeSet;

public interface TaskManager {
    Task createTask(Task task);

    Epic createEpic(Epic epic);

    SubTask createSubTask(SubTask subTask);

    Task updateTask(Task task);

    Epic updateEpic(Epic epic);

    SubTask updateSubTask(SubTask subTask);

    Task returnTaskByID(int id);

    Epic returnEpicByID(int id);

    SubTask returnSubTaskByID(int id);

    void deleteAllTasks();

    void deleteAllEpic();

    void deleteAllSubTask();

    void deleteTaskByID(int id);

    void deleteEpicByID(int id);

    void deleteSubTaskByID(int id);

    List<Task> getAllTasks();

    List<Epic> getAllEpics();

    List<SubTask> getAllSubTask();

    List<SubTask> getAllSubTaskByEpic(Epic epic);

    List<Task> getHistory();

    TreeSet<Task> getPrioritizedTasks();

    boolean isTimeIntersections(Task task1);

}
