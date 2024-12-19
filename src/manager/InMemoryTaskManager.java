package manager;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import model.*;

public class InMemoryTaskManager implements TaskManager {

    private int idTask = 0;

    private Map<Integer, Task> taskList = new HashMap<>();
    private Map<Integer, Epic> epicList = new HashMap<>();
    private Map<Integer, SubTask> subTaskList = new HashMap<>();

    private HistoryManager historyManager = Managers.getDefaultHistory();
    @Override
    public Task createTask(Task task) {
        idTask++;
        task.setId(idTask);
        taskList.put(idTask, task);
        return task;
    }

    @Override
    public Epic createEpic(Epic epic) {
        idTask++;
        epic.setId(idTask);
        epicList.put(idTask, epic);
        return epic;
    }

    @Override
    public SubTask createSubTask(SubTask subTask) {

        if (!epicList.containsKey(subTask.getIdEpic())) {
            return null;
        }
        if (subTask.getId() == subTask.getIdEpic()) {
            return null;
        }

        idTask++;
        subTask.setId(idTask);
        Epic epic = epicList.get(subTask.getIdEpic());

        epic.getSubTaskList().add(subTask.getId());

        subTaskList.put(idTask, subTask);

        return subTask;
    }

    @Override
    public void updateTask(Task task) {

        if (!taskList.containsKey(task.getId())) {
            return;
        }
        taskList.put(task.getId(), task);
    }

    @Override
    public void updateEpic(Epic epic) {

        if (!epicList.containsKey(epic.getId())) {
            return;
        }
        epicList.put(epic.getId(), epic);
    }

    @Override
    public void updateSubTask(SubTask subTask) {

        if (!subTaskList.containsKey(subTask.getId())) {
            return;
        }

        subTaskList.put(subTask.getId(), subTask);
        Epic epic = epicList.get(subTask.getIdEpic());
        updateStatusEpic(epic);
    }

    @Override
    public Task returnTaskByID(int id) {

        historyManager.add(taskList.get(id));
        return taskList.get(id);
    }

    @Override
    public Epic returnEpicByID(int id) {
        historyManager.add(epicList.get(id));
        return epicList.get(id);
    }

    @Override
    public SubTask returnSubTaskByID(int id) {
        historyManager.add(subTaskList.get(id));
        return subTaskList.get(id);
    }

    @Override
    public void deleteAllTasks() {
        taskList.clear();
    }

    @Override
    public void deleteAllEpic() {
        epicList.clear();
        subTaskList.clear();
    }

    @Override
    public void deleteAllSubTask() {
        subTaskList.clear();

        for (Epic epic : epicList.values()) {
            epic.getSubTaskList().clear();
            updateStatusEpic(epic);
        }
    }

    @Override
    public void deleteTaskByID(int id) {
        taskList.remove(id);
    }

    @Override
    public void deleteEpicByID(int id) {
        ArrayList<SubTask> allSubTasksByEpic = new ArrayList<>(getAllSubTaskByEpic(returnEpicByID(id)));

        epicList.remove(id);
        for (SubTask subTask : allSubTasksByEpic) {
            subTaskList.remove(subTask.getId());
        }
    }

    @Override
    public void deleteSubTaskByID(int id) {
        Epic epic = epicList.get(subTaskList.get(id).getIdEpic());
        epic.getSubTaskList().remove((Integer) id);
        subTaskList.remove(id);
        updateStatusEpic(epic);
    }

    @Override
    public List<Task> getAllTasks() {

        for (Task task : taskList.values()) {
            historyManager.add(task);
        }

        return new ArrayList<>(taskList.values());
    }

    @Override
    public List<Epic> getAllEpics() {

        for (Epic epic : epicList.values()) {
            historyManager.add(epic);
        }

        return new ArrayList<>(epicList.values());
    }

    @Override
    public List<SubTask> getAllSubTask() {

        for (SubTask subTask : subTaskList.values()) {
            historyManager.add(subTask);
        }

        return new ArrayList<>(subTaskList.values());
    }

    @Override
    public List<SubTask> getAllSubTaskByEpic(Epic epic) {
        List<SubTask> allSubTaskByEpic = new ArrayList<>();

        for (SubTask subTask : subTaskList.values()) {
            if (subTask.getIdEpic() == epic.getId()) {
                allSubTaskByEpic.add(subTask);
                historyManager.add(subTask);
            }
        }
        return allSubTaskByEpic;
    }

    public List<Task> getHistory() {
        return historyManager.getHistory();
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
