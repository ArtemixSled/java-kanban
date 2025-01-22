package manager;
import model.Task;
import java.util.*;

public class InMemoryHistoryManager implements HistoryManager {

    private final Map<Integer, Node> taskMap = new HashMap<>();

    private int size = 0;

    private Node head;

    private Node tail;

    private static final int MAX_HISTORY_SIZE = 10;

    @Override
    public void add(Task newTask) {

        if (newTask == null) {
            return;
        }

        Task task  = new Task(newTask.getNameTask(), newTask.getDescription(), newTask.getStatusTask());
        task.setId(newTask.getId());

        Node newNode = new Node(task);

        linkLast(newNode);
        taskMap.put(newNode.task.getId(), newNode);

        if (size > MAX_HISTORY_SIZE) {
            removeNode(head);
        }
    }

    public void removeNode(Node node) {
        if (node == null) {
            return;
        }
        if (node.prev != null) {
            node.prev.next = node.next;
        } else {
            head = node.next;
        }
        if (node.next != null) {
            node.next.prev = node.prev;
        } else {
            tail = node.prev;
        }
        taskMap.remove(node.task.getId());
        size--;
    }

    public void linkLast(Node node) {
        if (tail == null) {
            head = node;
            tail = node;
        }
        else {
            tail.next = node;
            node.prev = tail;
            tail = node;
        }
        size++;
    }

    @Override
    public void remove(int id) {
        if (taskMap.containsKey(id)) {
            removeNode(taskMap.get(id));
        }
    }

    @Override
    public List<Task> getHistory() {
        List<Task> historyList = new ArrayList<>();
        Node current = head;
        while (current != null) {
            historyList.add(current.task);
            current = current.next;
        }
        return historyList;
    }
}
