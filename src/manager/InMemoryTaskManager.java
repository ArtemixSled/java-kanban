package manager;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

import model.*;

public class InMemoryTaskManager implements TaskManager {

    private int idTask = 0;

    protected Map<Integer, Task> taskList = new HashMap<>();

    protected Map<Integer, Epic> epicList = new HashMap<>();

    protected Map<Integer, SubTask> subTaskList = new HashMap<>();

    private HistoryManager historyManager = Managers.getDefaultHistory();

    @Override
    public Task createTask(Task task) {

        if (isTimeIntersections(task) == true) {
            return null;
        }

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
        if (isTimeIntersections(subTask) == true) {
            return null;
        }

        idTask++;
        subTask.setId(idTask);
        Epic epic = epicList.get(subTask.getIdEpic());

        epic.getSubTaskList().add(subTask.getId());

        subTaskList.put(idTask, subTask);

        updateStatusEpic(epic);

        return subTask;
    }

    @Override
    public void updateTask(Task task) {

        if (!taskList.containsKey(task.getId())) {
            return;
        }
        if (isTimeIntersections(task) == true) {
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
        if (isTimeIntersections(subTask) == true) {
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
        taskList.values().stream()
                .forEach(task -> historyManager.remove(task.getId()));

        taskList.clear();
    }

    @Override
    public void deleteAllEpic() {
        epicList.values().stream()
                .forEach(epic -> historyManager.remove(epic.getId()));

        subTaskList.values().stream()
                .forEach(subTask -> historyManager.remove(subTask.getId()));

        epicList.clear();
        subTaskList.clear();
    }

    @Override
    public void deleteAllSubTask() {
        subTaskList.clear();

        subTaskList.values().stream()
                .forEach(subTask -> historyManager.remove(subTask.getId()));

        epicList.values().stream()
                .peek(epic -> epic.getSubTaskList().clear())
                .forEach(this::updateStatusEpic);
    }

    @Override
    public void deleteTaskByID(int id) {
        historyManager.remove(id);
        taskList.remove(id);
    }

    @Override
    public void deleteEpicByID(int id) {
        ArrayList<SubTask> allSubTasksByEpic = new ArrayList<>(getAllSubTaskByEpic(returnEpicByID(id)));

        historyManager.remove(id);
        epicList.remove(id);

        allSubTasksByEpic.stream()
                .map(SubTask::getId)
                .forEach(subTaskId -> subTaskList.remove(subTaskId));

    }

    @Override
    public void deleteSubTaskByID(int id) {
        Epic epic = epicList.get(subTaskList.get(id).getIdEpic());

        epic.getSubTaskList().remove((Integer) id);
        historyManager.remove(id);
        subTaskList.remove(id);

        updateStatusEpic(epic);
    }

    @Override
    public List<Task> getAllTasks() {
        taskList.values().stream()
                .forEach(task -> historyManager.add(task));

        return new ArrayList<>(taskList.values());
    }

    @Override
    public List<Epic> getAllEpics() {
        epicList.values().stream()
                .forEach(epic -> historyManager.add(epic));

        return new ArrayList<>(epicList.values());
    }

    @Override
    public List<SubTask> getAllSubTask() {
        subTaskList.values().stream()
                .forEach(subTask -> historyManager.add(subTask));

        return new ArrayList<>(subTaskList.values());
    }

    @Override
    public List<SubTask> getAllSubTaskByEpic(Epic epic) {

        List<SubTask> allSubTaskByEpic = subTaskList.values()
                .stream()
                .filter(subTask -> subTask.getIdEpic() == epic.getId())
                .peek(historyManager::add)  // Добавляем в historyManager
                .collect(Collectors.toList());

        return allSubTaskByEpic;
    }

    @Override
    public List<Task> getHistory() {
        return historyManager.getHistory();
    }

    private void updateStatusEpic(Epic epic) {

        if (epic.getSubTaskList().isEmpty()) {
            epic.setStatusTask(StatusTask.NEW);
            return;
        }

        ArrayList<SubTask> subTasksByEpic = new ArrayList<>(getAllSubTaskByEpic(epic));

        boolean isAllSubTaskNew = subTasksByEpic.stream()
                .allMatch(subTask -> subTask.getStatusTask() == StatusTask.NEW);

        boolean isAllSubTaskDone = subTasksByEpic.stream()
                .allMatch(subTask -> subTask.getStatusTask() == StatusTask.DONE);

        if (isAllSubTaskNew) {
            epic.setStatusTask(StatusTask.NEW);
            updateEpic(epic);
        } else if (isAllSubTaskDone) {
            epic.setStatusTask(StatusTask.DONE);
            updateEpic(epic);
        } else {
            epic.setStatusTask(StatusTask.IN_PROGRESS);
            updateEpic(epic);
        }
    }

    public LocalDateTime epicEndTime(Epic epic) {
        if (epic == null || epic.getSubTaskList() == null || epic.getSubTaskList().isEmpty()) {
            return null;
        }

        List<Integer> subTaskListId = epic.getSubTaskList();

        return subTaskListId.stream()
                .map(subTaskList::get)
                .map(SubTask::getEndTime)
                .filter(endTime -> endTime != null)
                .max(LocalDateTime::compareTo)
                .orElse(null);

    }

    public LocalDateTime epicStartTime(Epic epic) {
        if (epic == null || epic.getSubTaskList() == null || epic.getSubTaskList().isEmpty()) {
            return null;
        }

        List<Integer> subTaskListId = epic.getSubTaskList();

        return subTaskListId.stream()
                .map(subTaskList::get)
                .map(SubTask::getStartTime)
                .filter(entTime -> entTime != null)
                .min(LocalDateTime::compareTo)
                .orElse(null);

    }

    public Duration epicDuration(LocalDateTime epicStartTime, LocalDateTime epicEndTime) {
        return Duration.between(epicStartTime, epicEndTime);
    }

    public void refreshEpicInfo(Epic epic) {
        if (epic == null) {
            return;
        }

        updateStatusEpic(epic);

        epic.setStartTime(epicStartTime(epic));
        epic.setEndTime(epicEndTime(epic));
        epic.setDuration(epicDuration(epic.getStartTime(), epic.getEndTime()));
    }

    public TreeSet<Task> getPrioritizedTasks() {
        TreeSet<Task> listPrioritizedTasks = new TreeSet<>();

        listPrioritizedTasks.addAll(taskList.values()
                .stream()
                .filter(task -> task.getStartTime() != null)
                .collect(Collectors.toList())
        );

        listPrioritizedTasks.addAll(subTaskList.values()
                        .stream()
                        .filter(subTask -> subTask.getStartTime() != null)
                        .collect(Collectors.toList())
        );

        return listPrioritizedTasks;
    }

    public boolean isTimeIntersections(Task task) {
        if (task.getStartTime() == null) {
            return false;
        }

        TreeSet<Task> listPrioritizedTasks = getPrioritizedTasks();

        return listPrioritizedTasks.stream()
                .filter(taskFromList -> task.getId() != taskFromList.getId())
                .anyMatch(taskFromList ->
                        (task.getStartTime().isAfter(taskFromList.getStartTime()) &&
                                task.getStartTime().isBefore(taskFromList.getEndTime())) ||
                                (task.getEndTime().isAfter(taskFromList.getStartTime()) &&
                                        task.getEndTime().isBefore(taskFromList.getEndTime()))
                );
    }

    public int getIdTask() {
        return idTask;
    }

    public void setIdTask(int idTask) {
        this.idTask = idTask;
    }
}
