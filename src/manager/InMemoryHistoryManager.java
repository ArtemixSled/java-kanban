package manager;
import model.Epic;
import model.SubTask;
import model.Task;
import java.util.ArrayList;
import java.util.List;

public class InMemoryHistoryManager implements HistoryManager {

    private List<Task> history = new ArrayList<>();

    private static final int MAX_HISTORY_SIZE = 10;

    @Override
    public void add(Task newTask) {
        Task task = null;
        if (newTask instanceof Task) {
            task = new Task(newTask.getNameTask(), newTask.getDescription(), newTask.getStatusTask());
            task.setId(newTask.getId());
        }
        if (newTask instanceof Epic) {
            task = new Epic(newTask.getNameTask(), newTask.getDescription(), newTask.getStatusTask());
            task.setId(newTask.getId());
        }
        if (newTask instanceof SubTask) {
            task = new SubTask(newTask.getNameTask(), newTask.getDescription(), newTask.getStatusTask(), ((SubTask) newTask).getIdEpic());
            task.setId(newTask.getId());
        }

        history.add(task);

        if (history.size() > MAX_HISTORY_SIZE) {
            history.remove(0);
        }
    }

    @Override
    public List<Task> getHistory() {
        return new ArrayList<>(history);
    }
}
