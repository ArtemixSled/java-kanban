package manager;
import model.Epic;
import model.SubTask;
import model.Task;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

public class InMemoryHistoryManager implements HistoryManager {

    private LinkedList<Task> history = new LinkedList<>();

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
    public LinkedList<Task> getHistory() {
        return new LinkedList<>(history);
    }
}
