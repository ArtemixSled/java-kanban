import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class TaskManager {

    public static int idTask = 0;

    private HashMap<Integer, Task> TaskList = new HashMap<>();
    private HashMap<Integer, Epic> EpicList = new HashMap<>();
    private HashMap<Integer, SubTask> SubTaskList = new HashMap<>();

    public Task createTask(Task task) {
        idTask++;
        task.setId(idTask);
        updateTask(task);
        return task;
    }

    public Epic createEpic(Epic epic) {
        idTask++;
        epic.setId(idTask);
        updateEpic(epic);
        return epic;
    }

    public SubTask createSubTask(SubTask subTask) {
        idTask++;
        subTask.setId(idTask);

        Epic epic = EpicList.get(subTask.getIdEpic());
        epic.getSubTaskList().put(idTask, subTask);

        updateSubTask(subTask);

        return subTask;
    }

    public void updateTask(Task task) {
        TaskList.put(task.getId(), task);
    }

    public void updateEpic(Epic epic) {
        EpicList.put(epic.getId(), epic);
    }

    public void updateSubTask(SubTask subTask) {
        SubTaskList.put(subTask.getId(), subTask);
        Epic epic = EpicList.get(subTask.getIdEpic());
        updateStatusEpic(epic);
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

    public Task returnTaskByID(int id) {
        return TaskList.get(id);
    }

    public Epic returnEpicByID(int id) {
        return EpicList.get(id);
    }

    public SubTask returnSubTaskByID(int id) {
        return SubTaskList.get(id);
    }

    public void deleteAllTasks() {
        TaskList.clear();
    }

    public void deleteAllEpic() {
        EpicList.clear();
        SubTaskList.clear();
    }

    public void deleteAllSubTask() {
        SubTaskList.clear();

        for (Epic epic : EpicList.values()) {
            epic.getSubTaskList().clear();
            updateStatusEpic(epic);
        }
    }

    public void deleteTaskByID(int id) {
        TaskList.remove(id);
    }

    public void deleteEpicByID(int id) {
        ArrayList<SubTask> allSubTasksByEpic = new ArrayList<>(getAllSubTaskByEpic(returnEpicByID(id)));

        EpicList.remove(id);
        for (SubTask subTask : allSubTasksByEpic) {
            SubTaskList.remove(subTask.getId());
        }
    }

    public void deleteSubTaskByID(int id) {
        Epic epic = EpicList.get(SubTaskList.get(id).getIdEpic());
        epic.getSubTaskList().remove(id);
        SubTaskList.remove(id);
        updateStatusEpic(epic);
    }

    public HashMap<Integer, Epic> getEpicList() {
        return EpicList;
    }

    public List<Task> getAllTasks() {
        return new ArrayList<>(TaskList.values());
    }

    public List<Task> getAllEpics() {
        return new ArrayList<>(EpicList.values());
    }

    public List<Task> getAllSubTask() {
        return new ArrayList<>(SubTaskList.values());
    }

    public List<SubTask> getAllSubTaskByEpic(Epic epic) {
        List<SubTask> allSubTaskByEpic = new ArrayList<>();

        for (SubTask subTask : SubTaskList.values()) {
            if (subTask.getIdEpic() == epic.getId()) {
                allSubTaskByEpic.add(subTask);
            }
        }
        return allSubTaskByEpic;
    }
}
