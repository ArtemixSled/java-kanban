package manager;

public class Managers <T extends TaskManager> {

    private TaskManager manager = new InMemoryTaskManager();

    private static InMemoryHistoryManager InMemoryHistoryManager = new InMemoryHistoryManager();


    public static HistoryManager getDefaultHistory() {
        return InMemoryHistoryManager;
    }

    public TaskManager getDefault() {
        return manager;
    }
}
