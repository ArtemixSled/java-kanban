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

    private Set<Task> listPrioritizedTask = new TreeSet<>(Comparator.comparing(Task::getStartTime));

    @Override
    public Task createTask(Task task) {

        if (isTimeIntersections(task)) {
            return null;
        }

        idTask++;
        task.setId(idTask);
        taskList.put(idTask, task);

        if (task.getStartTime() != null) {
            listPrioritizedTask.add(task);
        }

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

        if (isTimeIntersections(subTask)) {
            return null;
        }

        idTask++;
        subTask.setId(idTask);
        Epic epic = epicList.get(subTask.getIdEpic());

        epic.getSubTaskList().add(subTask.getId());

        subTaskList.put(idTask, subTask);

        if (subTask.getStartTime() != null) {
            listPrioritizedTask.add(subTask);
        }

        refreshEpicInfo(epic);

        return subTask;
    }

    @Override
    public void updateTask(Task task) {

        if (!taskList.containsKey(task.getId())) {
            return;
        }
        if (isTimeIntersections(task)) {
            return;
        }

        if (task.getStartTime() != null) {
            listPrioritizedTask.remove(taskList.get(task.getId()));
            listPrioritizedTask.add(task);
        } else {
            listPrioritizedTask.remove(taskList.get(task.getId()));
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
        if (isTimeIntersections(subTask)) {
            return;
        }

        if (subTask.getStartTime() != null) {
            listPrioritizedTask.remove(subTaskList.get(subTask.getId()));
            listPrioritizedTask.add(subTask);
        } else {
            listPrioritizedTask.remove(subTaskList.get(subTask.getId()));
        }

        subTaskList.put(subTask.getId(), subTask);
        Epic epic = epicList.get(subTask.getIdEpic());
        refreshEpicInfo(epic);
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

        taskList.values().stream()
                .forEach(task -> listPrioritizedTask.remove(task));

        taskList.clear();
    }

    @Override
    public void deleteAllEpic() {
        epicList.values().stream()
                .forEach(epic -> historyManager.remove(epic.getId()));

        subTaskList.values().stream()
                .forEach(subTask -> historyManager.remove(subTask.getId()));

        taskList.values().stream()
                .forEach(subTask -> listPrioritizedTask.remove(subTask));

        epicList.clear();
        subTaskList.clear();
    }

    @Override
    public void deleteAllSubTask() {
        subTaskList.values().stream()
                .forEach(subTask -> listPrioritizedTask.remove(subTask));

        subTaskList.clear();

        subTaskList.values().stream()
                .forEach(subTask -> historyManager.remove(subTask.getId()));

        epicList.values().stream()
                .peek(epic -> epic.getSubTaskList().clear())
                .forEach(epic -> {
                    updateStatusEpic(epic);
                    refreshEpicInfo(epic);
                });
    }

    @Override
    public void deleteTaskByID(int id) {
        historyManager.remove(id);
        listPrioritizedTask.remove(taskList.get(id));
        taskList.remove(id);
    }

    @Override
    public void deleteEpicByID(int id) {
        ArrayList<SubTask> allSubTasksByEpic = new ArrayList<>(getAllSubTaskByEpic(returnEpicByID(id)));

        historyManager.remove(id);
        epicList.remove(id);

        allSubTasksByEpic.stream()
                .map(SubTask::getId)
                .forEach(subTaskId -> listPrioritizedTask.remove(subTaskList.get(subTaskId)));

        allSubTasksByEpic.stream()
                .map(SubTask::getId)
                .forEach(subTaskId -> subTaskList.remove(subTaskId));

    }

    @Override
    public void deleteSubTaskByID(int id) {
        Epic epic = epicList.get(subTaskList.get(id).getIdEpic());

        epic.getSubTaskList().remove((Integer) id);
        historyManager.remove(id);
        listPrioritizedTask.remove(subTaskList.get(id));
        subTaskList.remove(id);

        refreshEpicInfo(epic);
        updateStatusEpic(epic);
    }

    @Override
    public List<Task> getAllTasks() {
        return new ArrayList<>(taskList.values());
    }

    @Override
    public List<Epic> getAllEpics() {
        return new ArrayList<>(epicList.values());
    }

    @Override
    public List<SubTask> getAllSubTask() {
        return new ArrayList<>(subTaskList.values());
    }

    @Override
    public List<SubTask> getAllSubTaskByEpic(Epic epic) {

        List<SubTask> allSubTaskByEpic = subTaskList.values()
                .stream()
                .filter(subTask -> subTask.getIdEpic() == epic.getId())
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

    private LocalDateTime epicEndTime(Epic epic) {
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

    private LocalDateTime epicStartTime(Epic epic) {
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

    private Duration epicDuration(LocalDateTime epicStartTime, LocalDateTime epicEndTime) {
        if (epicStartTime == null || epicEndTime == null) {
            return Duration.ZERO;
        }

        return Duration.between(epicStartTime, epicEndTime);
    }


    private void refreshEpicInfo(Epic epic) {
        if (epic == null) {
            return;
        }

        updateStatusEpic(epic);

        epic.setStartTime(epicStartTime(epic));
        epic.setEndTime(epicEndTime(epic));
        epic.setDuration(epicDuration(epic.getStartTime(), epic.getEndTime()));
    }

    public TreeSet<Task> getPrioritizedTasks() {
        return new TreeSet<>(listPrioritizedTask);
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
