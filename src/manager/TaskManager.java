package manager;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import model.*;

public class TaskManager {

    public static int idTask = 0;

    private HashMap<Integer, Task> taskList = new HashMap<>();
    private HashMap<Integer, Epic> epicList = new HashMap<>();
    private HashMap<Integer, SubTask> subTaskList = new HashMap<>();

    public Task createTask(Task task) {
        idTask++;
        task.setId(idTask);
        return task;
    }

    public Epic createEpic(Epic epic) {
        idTask++;
        epic.setId(idTask);
        return epic;
    }

    public SubTask createSubTask(SubTask subTask) {

        if (!epicList.containsKey(subTask.getIdEpic())) {
            return null;
        }

        idTask++;
        subTask.setId(idTask);
        Epic epic = epicList.get(subTask.getIdEpic());

        epic.getSubTaskList().add(subTask.getId());

        return subTask;
    }

    public void updateTask(Task task) {

        if (!taskList.containsKey(task.getId())) {
            return;
        }
        taskList.put(task.getId(), task);
    }

    public void updateEpic(Epic epic) {

        if (!epicList.containsKey(epic.getId())) {
            return;
        }
        epicList.put(epic.getId(), epic);
    }

    public void updateSubTask(SubTask subTask) {

        if (!subTaskList.containsKey(subTask.getId())) {
            return;
        }

        subTaskList.put(subTask.getId(), subTask);
        Epic epic = epicList.get(subTask.getIdEpic());
        updateStatusEpic(epic);
    }

    public Task returnTaskByID(int id) {
        return taskList.get(id);
    }

    public Epic returnEpicByID(int id) {
        return epicList.get(id);
    }

    public SubTask returnSubTaskByID(int id) {
        return subTaskList.get(id);
    }

    public void deleteAllTasks() {
        taskList.clear();
    }

    public void deleteAllEpic() {
        epicList.clear();
        subTaskList.clear();
    }

    public void deleteAllSubTask() {
        subTaskList.clear();

        for (Epic epic : epicList.values()) {
            epic.getSubTaskList().clear();
            updateStatusEpic(epic);
        }
    }

    public void deleteTaskByID(int id) {
        taskList.remove(id);
    }

    public void deleteEpicByID(int id) {
        ArrayList<SubTask> allSubTasksByEpic = new ArrayList<>(getAllSubTaskByEpic(returnEpicByID(id)));

        epicList.remove(id);
        for (SubTask subTask : allSubTasksByEpic) {
            subTaskList.remove(subTask.getId());
        }
    }

    public void deleteSubTaskByID(int id) {
        Epic epic = epicList.get(subTaskList.get(id).getIdEpic());
        epic.getSubTaskList().remove(id);
        subTaskList.remove(id);
        updateStatusEpic(epic);
    }

    public List<Task> getAllTasks() {
        return new ArrayList<>(taskList.values());
    }

    public List<Task> getAllEpics() {
        return new ArrayList<>(epicList.values());
    }

    public List<Task> getAllSubTask() {
        return new ArrayList<>(subTaskList.values());
    }

    public List<SubTask> getAllSubTaskByEpic(Epic epic) {
        List<SubTask> allSubTaskByEpic = new ArrayList<>();

        for (SubTask subTask : subTaskList.values()) {
            if (subTask.getIdEpic() == epic.getId()) {
                allSubTaskByEpic.add(subTask);
            }
        }
        return allSubTaskByEpic;
    }

    private void updateStatusEpic(Epic epic) {

        if (epic.getSubTaskList().isEmpty()) {
            epic.setStatusTask(StatusTask.NEW);
            return;
        }

        ArrayList<SubTask> subTasksByEpic = new ArrayList<>(getAllSubTaskByEpic(epic));
        boolean AllSubTaskNew = true;
        boolean AllSubTaskDone = true;

        for (SubTask subTaskByList : subTasksByEpic) {
            if (subTaskByList.getStatusTask() != StatusTask.NEW) {
                AllSubTaskNew = false;
            }
            if (subTaskByList.getStatusTask() != StatusTask.DONE) {
                AllSubTaskDone = false;
            }
        }

        if (AllSubTaskNew) {
            epic.setStatusTask(StatusTask.NEW);
            updateEpic(epic);
        }
        else if (AllSubTaskDone) {
            epic.setStatusTask(StatusTask.DONE);
            updateEpic(epic);
        }
        else {
            epic.setStatusTask(StatusTask.IN_PROGRESS);
            updateEpic(epic);
        }
    }
}
