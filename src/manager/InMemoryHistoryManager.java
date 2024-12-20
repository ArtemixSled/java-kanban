package manager;
import model.Task;
import java.util.LinkedList;
import java.util.List;

public class InMemoryHistoryManager implements HistoryManager {

    private List<Task> history = new LinkedList<>();

    private static final int MAX_HISTORY_SIZE = 10;

    @Override
    public void add(Task newTask) {

        if (newTask == null) {
            return;
        }

        Task task;
        task = new Task(newTask.getNameTask(), newTask.getDescription(), newTask.getStatusTask());
        task.setId(newTask.getId());

        history.add(task);

        if (history.size() > MAX_HISTORY_SIZE) {
            history.remove(0);
        }
    }

    @Override
    public List<Task> getHistory() {
        return new LinkedList<>(history);
    }
}
